package com.ringer.interactive.call

import android.annotation.SuppressLint
import android.content.ContentProviderOperation
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.StrictMode
import android.provider.ContactsContract
import android.provider.Settings
import android.util.Log
import com.google.gson.JsonObject
import com.ringer.interactive.api.Api
import com.ringer.interactive.api.Connection
import com.ringer.interactive.api.base_url
import com.ringer.interactive.api.basic_auth
import com.ringer.interactive.model.PhoneNumber
import com.ringer.interactive.pref.Preferences
import okhttp3.HttpUrl
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.net.URL
import androidx.core.content.ContextCompat.getSystemService

import android.telephony.TelephonyManager
import androidx.core.content.ContextCompat


class AuthAPICall {

    var numberList: ArrayList<PhoneNumber> = ArrayList()

    fun apiCallAuth(context: Context) {

        try {

            lateinit var call: Call<JsonObject>
            val api: Api = Connection().getCon(context, base_url)

            val auth = basic_auth
            call = api.getTokenWithAuth(
                auth,
                Preferences().getEmailAddress(context),
                Preferences().getUserPassword(context)
            )

            call.enqueue(object : javax.security.auth.callback.Callback, Callback<JsonObject> {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {

                    if (response.isSuccessful) {
                        if (response.body() != null) {

                            //Set Token BaseUrl
                            Preferences().setTokenBaseUrl(
                                context,
                                response.body()!!.get("location").asString
                            )

                            //Set Auth Token
                            Preferences().setAuthToken(
                                context,
                                response.body()!!.get("token").asString
                            )

                            //api call to send fcm token
                            apiCallFirebaseToken(context, Preferences().getTokenBaseUrl(context))

                            //Search Contact
                            searchContact(context, Preferences().getTokenBaseUrl(context))

                        }
                    }
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {

                    Log.e("failure", "" + t.message)

                }

            })
        } catch (e: Exception) {

            Log.e("errorToken", "" + e.message)

        }
    }

    private fun apiCallFirebaseToken(context: Context, tokenBaseUrl: String) {


        var imei: String = ""

        val telephonyManager =
            context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            imei = telephonyManager.imei
        } else {
            imei = telephonyManager.deviceId
        }

        lateinit var call: Call<JsonObject>
        val api: Api = Connection().getCon(context, tokenBaseUrl)

        call = api.sendFCMToken(
            Preferences().getFCMToken(context)
        )

        call.enqueue(object : javax.security.auth.callback.Callback, Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful) {
                    if (response.body() != null) {

                    }
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {

            }

        })

    }

    private fun searchContact(context: Context, tokenBaseUrl: String) {

        lateinit var call: Call<JsonObject>
        val api: Api = Connection().getCon(context, tokenBaseUrl)

        call = api.searchContact(
            Preferences().getAuthToken(context)
        )

        call.enqueue(object : javax.security.auth.callback.Callback, Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {

                try {

                    if (response.isSuccessful) {
                        if (response.body() != null) {

                            val contact_id_list: ArrayList<String> = ArrayList()

                            val objects = response.body()!!.get("objects").asJsonArray
                            if (objects.size() > 0) {

                                for (i in 0 until objects.size()) {

                                    if (objects.get(i).asJsonObject.has("avatar")) {

                                        contact_id_list.add(objects.get(i).asJsonObject.get("contactId").asString)

                                        //get image contact avatar

                                        val contact_id =
                                            objects[i].asJsonObject.get("contactId").asString
                                        val first_name =
                                            objects[i].asJsonObject.get("firstName").asString + " " + objects[i].asJsonObject.get(
                                                "lastName"
                                            ).asString
                                        val phone = objects[i].asJsonObject.get("phone").asString

                                        //Get Contact Image
                                        getContactImage(
                                            context,
                                            tokenBaseUrl,
                                            contact_id,
                                            first_name,
                                            phone
                                        )
                                    }
                                }
                            }
                        }
                    }
                } catch (e: Exception) {

                    Log.e("errorSearch", "" + e.message)

                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {

                Log.e("failure", "" + t.message)

            }

        })

    }

    private fun getContactImage(
        context: Context,
        tokenBaseUrl: String,
        contactIdList: String,
        first_name: String,
        phone: String
    ) {
        lateinit var call: Call<ResponseBody>
        val api: Api = Connection().getCon(context, tokenBaseUrl)

        call = api.getAvatar(
            Preferences().getAuthToken(context),
            contactIdList
        )

        call.enqueue(object : javax.security.auth.callback.Callback, Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {

                try {

                    if (response.isSuccessful) {
                        if (response.body() != null) {

                            getContactList(context, response.raw().request.url, first_name, phone)

                        }
                    }
                } catch (e: Exception) {

                    Log.e("errorImage", "" + e.message)

                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {

                Log.e("failure", "" + t.message)

            }
        })
    }

    @SuppressLint("Range")
    fun getContactList(
        context: Context,
        url: HttpUrl,
        first_name: String,
        phone: String
    ): StringBuilder {

        val builder = StringBuilder()
        val resolver = context.contentResolver
        val cursor = resolver.query(
            ContactsContract.Contacts.CONTENT_URI, null, null, null,
            null
        )

        if (cursor!!.count > 0) {
            while (cursor.moveToNext()) {
                val id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID))
                val name =
                    cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                val phoneNumber = (cursor.getString(
                    cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)
                )).toInt()

                if (phoneNumber > 0) {
                    val cursorPhone = context.contentResolver.query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?",
                        arrayOf(id),
                        null
                    )

                    if (cursorPhone!!.count > 0) {
                        while (cursorPhone.moveToNext()) {
                            val phoneNumValue = cursorPhone.getString(
                                cursorPhone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                            )
                            val contactId =
                                cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.PhoneLookup._ID))
                            builder.append("Contact: ").append(name).append(", Phone Number: ")
                                .append(
                                    phoneNumValue
                                ).append("\n\n")

                            val phoneNumber = PhoneNumber()
                            phoneNumber.number = phoneNumValue
                            phoneNumber.id = contactId
                            numberList.add(phoneNumber)

                        }
                    }

                    cursorPhone.close()
                }
            }

            //checkBackground Data for update or create contact
            backGroundCheckData(context, url, first_name, phone)

        } else {

            //checkBackground Data for update or create contact
            backGroundCheckData(context, url, first_name, phone)

        }

        cursor.close()
        return builder
    }

    //checkBackground Data for update or create contact
    private fun backGroundCheckData(
        context: Context,
        url: HttpUrl,
        first_name: String,
        phone: String
    ) {
        if (numberList.size > 0) {


            for (i in 0 until numberList.size) {


                if (numberList[i].number.equals(phone, false)) {

                    //editContactBackGround
                    editContactBackGround(context, phone, numberList[i].id, first_name, url)
                    break

                } else {

                    //Create Contact BackGround
                    createContactBackGround(context, url, first_name, phone)
                    break
                }

            }

        } else {

            //Create Contact BackGround
            createContactBackGround(context, url, first_name, phone)

        }


    }

    //Create Contact BackGround
    private fun createContactBackGround(
        context: Context,
        url: HttpUrl,
        first_name: String,
        phone: String
    ) {
        val DisplayName = first_name
        val MobileNumber = phone
        val photo = url

        val ops = ArrayList<ContentProviderOperation>()

        ops.add(
            ContentProviderOperation.newInsert(
                ContactsContract.RawContacts.CONTENT_URI
            )
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                .build()
        )

        // Names
        if (DisplayName != null) {
            ops.add(
                ContentProviderOperation.newInsert(
                    ContactsContract.Data.CONTENT_URI
                )
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(
                        ContactsContract.Data.MIMETYPE,
                        ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE
                    )
                    .withValue(
                        ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME,
                        DisplayName
                    ).build()
            )
        }

        // Mobile Number
        if (MobileNumber != null) {
            ops.add(
                ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(
                        ContactsContract.Data.MIMETYPE,
                        ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE
                    )
                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, MobileNumber)
                    .withValue(
                        ContactsContract.CommonDataKinds.Phone.TYPE,
                        ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE
                    )
                    .build()
            )
        }

        // Photo
        if (photo != null) {
            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)

            val url = URL(photo.toString())
            val image = BitmapFactory.decodeStream(url.openConnection().getInputStream())
            ops.add(
                ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(
                        ContactsContract.CommonDataKinds.Photo.PHOTO,
                        bitmapToByteArray(image)
                    )
                    .withValue(
                        ContactsContract.Data.MIMETYPE,
                        ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE
                    )
                    .build()
            )
        }

        try {
            context.contentResolver.applyBatch(ContactsContract.AUTHORITY, ops)
        } catch (e: Exception) {
            Log.e("errorNew", "" + e.message)
            e.printStackTrace()
        }
    }

    //Edit Contact
    private fun editContactBackGround(
        context: Context,
        number: String,
        id: String,
        first_name: String,
        url: HttpUrl
    ) {
        val contentResolver = context.contentResolver

        val where =
            ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?"

        val nameParams =
            arrayOf(id, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
        val numberParams = arrayOf(id, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
        val photoParams = arrayOf(id, ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE)
        val ops = ArrayList<ContentProviderOperation>()

        val contactName = "" + first_name
        val contactNumber = "" + number

        //Contact Name
        if (contactName != "") {
            ops.add(
                ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                    .withSelection(where, nameParams)
                    .withValue(
                        ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME,
                        contactName
                    )
                    .build()
            )
        }

        //Contact Number
        if (contactNumber != "") {
            ops.add(
                ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                    .withSelection(where, numberParams)
                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, contactNumber)
                    .build()
            )
        }

        // Contact Photo
        try {
            val url = URL(url.toString())
            val image = BitmapFactory.decodeStream(url.openConnection().getInputStream())
            ops.add(
                ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                    .withSelection(where, photoParams)
                    .withValue(
                        ContactsContract.CommonDataKinds.Photo.PHOTO,
                        bitmapToByteArray(image)
                    )
                    .build()
            )

        } catch (e: Exception) {
            Log.e("errorEdit", "" + e.message)
            e.printStackTrace()
        }
        contentResolver.applyBatch(ContactsContract.AUTHORITY, ops)
    }

    //Bitmap to ByteArray
    private fun bitmapToByteArray(bitmap: Bitmap?): ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap?.compress(Bitmap.CompressFormat.PNG, 90, stream)
        return stream.toByteArray()
    }
}