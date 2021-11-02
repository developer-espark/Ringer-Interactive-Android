package com.ringer.interactive.call

import android.annotation.SuppressLint
import android.content.ContentProviderOperation
import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.StrictMode
import android.provider.ContactsContract
import android.util.Log
import com.google.gson.JsonObject
import com.ringer.interactive.api.Api
import com.ringer.interactive.api.Connection
import com.ringer.interactive.api.base_url
import com.ringer.interactive.model.PhoneNumber
import com.ringer.interactive.pref.Preferences
import okhttp3.HttpUrl
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.net.URL


class AuthAPICall {

    var numberList: ArrayList<PhoneNumber> = ArrayList()
    var editContact = "0"

    fun apiCallAuth(context: Context) {

        try {

            lateinit var call: Call<JsonObject>
            val api: Api = Connection().getCon(context,base_url)

            val auth = "Basic dGltQGJhc2Fsc21hcnRzb2x1dGlvbnMuY29tOlJpbmdjIyMxMjM0"
            call = api.getTokenWithAuth(
                auth,
                Preferences().getEmailAddress(context),
                Preferences().getUserPassword(context)
            )


            call.enqueue(object : javax.security.auth.callback.Callback, Callback<JsonObject> {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {

                    Log.e("repsonse", "" + response)
                    if (response.isSuccessful) {
                        if (response.body() != null) {

                            Log.e("resSuc", "resSuc")
                            Preferences().setTokenBaseUrl(context,response.body()!!.get("location").asString)
                            Preferences().setAuthToken(context,response.body()!!.get("token").asString)

                            //Search Contact
                            searchContact(context,Preferences().getTokenBaseUrl(context))


                        }
                    }

                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {

                    Log.e("failure", "" + t.message)

                }

            })
        } catch (e: Exception) {

            Log.e("error",""+e.message)
        }
    }

    private fun searchContact(context: Context, tokenBaseUrl: String) {

        lateinit var call: Call<JsonObject>
        val api: Api = Connection().getCon(context,tokenBaseUrl)

        call = api.searchContact(
            Preferences().getAuthToken(context)
        )


        call.enqueue(object : javax.security.auth.callback.Callback, Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {


                if (response.isSuccessful) {
                    if (response.body() != null) {


                        val contact_id_list : ArrayList<String> = ArrayList()
                        Log.e("responseSearch",""+response.body())

                        val objects = response.body()!!.get("objects").asJsonArray
                        if (objects.size() > 0){

                            for (i in 0 until objects.size()){

                                if (objects.get(i).asJsonObject.has("avatar")){

                                    contact_id_list.add(objects.get(i).asJsonObject.get("contactId").asString)

                                    //get image contact avatar


                                    val contact_id = objects[i].asJsonObject.get("contactId").asString
                                    val first_name = objects[i].asJsonObject.get("firstName").asString
                                    val phone = objects[i].asJsonObject.get("phone").asString
                                    Log.e("contact_id",contact_id)
                                    getContactImage(context,tokenBaseUrl,contact_id,first_name,phone)
                                }

                            }
                        }
                    }
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
        val api: Api = Connection().getCon(context,tokenBaseUrl)
        Log.e("contact_id_api",contactIdList)


        call = api.getAvatar(
            Preferences().getAuthToken(context),
            contactIdList
        )


        call.enqueue(object : javax.security.auth.callback.Callback, Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {



                if (response.isSuccessful) {
                    if (response.body() != null) {

                        Log.e("responseAuth",""+response.raw().request.url)
                        getContactList(context,response.raw().request.url,first_name,phone)

                    }
                }

            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {

                Log.e("failure", "" + t.message)

            }

        })

    }
    @SuppressLint("Range")
    fun getContactList(context: Context, url: HttpUrl, first_name: String, phone: String) : StringBuilder{
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
                            Log.e("Name ===>", phoneNumValue)
                        }
                    }

                    cursorPhone.close()
                }
            }
            backGroundCheckData(context,url,first_name,phone)


        } else {
            backGroundCheckData(context,url,first_name,phone)
            //   toast("No contacts available!")
        }
        cursor.close()
        return builder

    }
    private fun backGroundCheckData(context: Context,url: HttpUrl, first_name: String, phone: String) {
        if (numberList.size > 0) {

            for (i in 0 until numberList.size) {
//            1 (234) 567-890
                if (numberList[i].number.equals(phone, true)) {
                    editContact = "1"
//                    editContactBackGround(context,numberList[i].number,numberList[i].id)
//                    BlobEditContactBackGround(numberList[i].number)
                    break
                }
            }
            if (editContact != "1") {
                createContactBackGround(context,url,first_name,phone)
            }
        } else {
            createContactBackGround(context,url,first_name,phone)
        }
    }
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

        //------------------------------------------------------ Names
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

        //------------------------------------------------------ Mobile Number
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



        if (photo != null) {
            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)

            val url =
                URL(url.toString())
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
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
//            Toast.makeText(this, "Exception: " + e.message, Toast.LENGTH_SHORT).show()
        }
    }
    private fun editContactBackGround(context: Context, number: String, id: String) {
        val contentResolver = context.getContentResolver()

        val where =
            ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?"

        val emailParams =
            arrayOf(id, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
        val nameParams =
            arrayOf(id, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
        val numberParams =
            arrayOf(id, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
        val photoParams =
            arrayOf(id,ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE)
        val ops = ArrayList<ContentProviderOperation>()

        val email = "abc@gmail.com"
        val COntactname = "ABCD"
        val Contactnumber = "123456789"
        if(!email.equals(""))
        {
            ops.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                .withSelection(where,emailParams)
                .withValue(ContactsContract.CommonDataKinds.Email.DATA, email)
                .build())
        }

        if(!COntactname.equals(""))
        {
            ops.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                .withSelection(where,nameParams)
                .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, COntactname)
                .build())
        }

        if(!Contactnumber.equals(""))
        {

            ops.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                .withSelection(where,numberParams)
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, Contactnumber)
                .build())
        }
        // Picture
        // Picture
        try {
            val url =
                URL("https://i.picsum.photos/id/623/200/200.jpg?hmac=xquTjHIYmAPV3XEGlIUaV_KWyEofkbortxrK79jJhWA")
            val image = BitmapFactory.decodeStream(url.openConnection().getInputStream())
            /* val bitmap =
                 MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.parse("https://i.picsum.photos/id/623/200/200.jpg?hmac=xquTjHIYmAPV3XEGlIUaV_KWyEofkbortxrK79jJhWA"))
             val image = ByteArrayOutputStream()
             bitmap.compress(Bitmap.CompressFormat.JPEG, 100, image)*/
            ops.add(android.content.ContentProviderOperation.newUpdate(android.provider.ContactsContract.Data.CONTENT_URI)
                .withSelection(where,photoParams)
                .withValue(ContactsContract.CommonDataKinds.Photo.PHOTO, bitmapToByteArray(image))
                .build())

        } catch (e: java.lang.Exception) {
            Log.e("error",""+e.message)
            e.printStackTrace()
        }
        context.contentResolver.applyBatch(ContactsContract.AUTHORITY, ops)


    }
    private fun bitmapToByteArray(bitmap: Bitmap?): ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap?.compress(Bitmap.CompressFormat.PNG, 90, stream)
        return stream.toByteArray()
    }


}