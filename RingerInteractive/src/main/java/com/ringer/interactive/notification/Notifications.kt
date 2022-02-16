package com.ringer.interactive.notification

import android.annotation.SuppressLint
import android.content.ContentProviderOperation
import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.StrictMode
import android.provider.ContactsContract
import android.provider.ContactsContract.PhoneLookup
import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.ringer.interactive.api.Api
import com.ringer.interactive.api.Connection
import com.ringer.interactive.model.PhoneNumber
import com.ringer.interactive.model.StoreContact
import com.ringer.interactive.pref.Preferences
import okhttp3.HttpUrl
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.net.URL


class Notifications {
    var numberList: ArrayList<PhoneNumber> = ArrayList()
    var contact_id = ""
    var contactList: ArrayList<StoreContact> = ArrayList()
    var companyName = ""

     fun searchContact(context: Context, tokenBaseUrl: String) {


        lateinit var call: Call<JsonObject>
        val api: Api = Connection().getCon(context, tokenBaseUrl)

        call = api.searchContact(
            Preferences().getAuthToken(context)
        )

        call.enqueue(object : javax.security.auth.callback.Callback, Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                numberList.clear()
                try {

                    if (response.isSuccessful) {
                        if (response.body() != null) {

                            Log.e("times", "times")
                            val contact_id_list: ArrayList<String> = ArrayList()
                            var phoneMultiple: ArrayList<String> = ArrayList()
                            val objects = response.body()!!.get("objects").asJsonArray
                            if (objects.size() > 0) {

                                for (i in 0 until objects.size()) {


                                    contact_id_list.add(objects.get(i).asJsonObject.get("contactId").asString)

                                    val first_name =
                                        objects[i].asJsonObject.get("firstName").asString + " " + objects[i].asJsonObject.get(
                                            "lastName"
                                        ).asString

                                    val uFirstName = objects[i].asJsonObject.get("firstName").asString
                                    val lastName = objects[i].asJsonObject.get("lastName").asString

                                    if (objects[i].asJsonObject.has("galleryId")) {
                                        contact_id =
                                            objects[i].asJsonObject.get("galleryId").asString
                                    } else {
                                        contact_id = ""
                                    }

                                    val userContactID =objects[i].asJsonObject.get("contactId").asString

                                    phoneMultiple = ArrayList()
                                    var phone = objects[i].asJsonObject.get("phone").asJsonArray
                                    for (j in 0 until phone.size()) {
                                        phoneMultiple.add(phone[j].asString)
                                        Log.e("phoneMultiple", "" + phone[j])
                                    }


                                    var storeContact = StoreContact()
                                    storeContact.userName = first_name
                                    storeContact.galleryId = contact_id
                                    storeContact.phoneList = phoneMultiple
                                    storeContact.contactID = userContactID
                                    storeContact.firstName = uFirstName
                                    storeContact.lastName = lastName

                                    contactList.add(storeContact)


//                                    if (!contact_id.equals("")) {
//                                        getContactImage(
//                                            context,
//                                            tokenBaseUrl,
//
//                                        )
//                                    }


                                }

                            }
                            Log.e("listSize", "" + contactList.size)
                            for (i in 0 until contactList.size) {
                                if (!contactList.get(i).galleryId.equals("")) {
                                    getContactImage(context, tokenBaseUrl, contactList.get(i))
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
        storeContact: StoreContact
    ) {
        try {


            lateinit var call: Call<ResponseBody>
            val api: Api = Connection().getCon(context, tokenBaseUrl)

            call = api.getAvatar(
                Preferences().getAuthToken(context),
                storeContact.galleryId,
                storeContact.phoneList[0],
                storeContact.firstName,
                storeContact.lastName,
                storeContact.contactID
            )
            numberList.clear()
            call.enqueue(object : javax.security.auth.callback.Callback, Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {

                    try {

                        if (response.isSuccessful) {
                            if (response.body() != null) {

                                Log.e("ImageURLS",""+response.raw().request.url)
                                getContactList(context, response.raw().request.url, storeContact)

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
        } catch (e: Exception) {

        }
    }

    @SuppressLint("Range")
    fun getContactList(
        context: Context,
        url: HttpUrl,
        storeContact: StoreContact
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


                    val orgWhere =
                        ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?"
                    val orgWhereParams = arrayOf(
                        id,
                        ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE
                    )
                    val orgCur: Cursor = context.contentResolver.query(
                        ContactsContract.Data.CONTENT_URI,
                        null, orgWhere, orgWhereParams, null
                    )!!
                    if (orgCur.moveToFirst()) {
                        companyName =
                            orgCur.getString(orgCur.getColumnIndex(ContactsContract.CommonDataKinds.Organization.DATA))
                        val title =
                            orgCur.getString(orgCur.getColumnIndex(ContactsContract.CommonDataKinds.Organization.TITLE))
                    }
                    orgCur.close()


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
                            if (companyName == "") {
                                phoneNumber.company_name = ""
                            } else {
                                phoneNumber.company_name = companyName
                            }

                            numberList.add(phoneNumber)

                        }
                    }

                    cursorPhone.close()
                }
            }

            //checkBackground Data for update or create contact
            backGroundCheckData(context, url, storeContact, companyName)

        } else {

            //checkBackground Data for update or create contact
            backGroundCheckData(context, url, storeContact, companyName)

        }

        cursor.close()
        return builder
    }

    //checkBackground Data for update or create contact
    private fun backGroundCheckData(
        context: Context,
        url: HttpUrl,
        storeContact: StoreContact,
        company_name: String
    ) {
        try {


            Log.e("numberListSize", "" + numberList.size)
            Log.e("numberListSize123", "" + Gson().toJson(numberList))
            if (numberList.size > 0) {
                for (i in 0 until numberList.size) {
                    var isMatch = false
                    for (j in 0 until storeContact.phoneList.size) {


                        Log.e("numberList[i].number", "" + numberList[i].number[i])
                        Log.e("numberList[i].phone", "" + storeContact.phoneList[j])

                        if (numberList[i].number.equals(storeContact.phoneList[j], false)) {

//                        isMatch = true

                            editContactBackGround(
                                context,
                                storeContact.phoneList,
                                numberList[i].id,
                                storeContact.userName,
                                url,
                                company_name,
                                storeContact
                            )
//                            getContactList(context, url, storeContact)
                            break
                        }

                        /*if (numberList[i].number.contains(phone[j], false)) {
                            editContactBackGround(
                                context,
                                phone,
                                numberList[i].id,
                                first_name,
                                url,
                                company_name
                            )
                            break

                        } else {

                            //Create Contact BackGround
                            Log.e("numberListCreate", "Create")
                            createContactBackGround(context, url, first_name, phone, company_name)
                            break

                        }*/
                    }
//                if (isMatch == true) {
//
//                    editContactBackGround(
//                        context,
//                        phone,
//                        numberList[i].id,
//                        first_name,
//                        url,
//                        company_name
//                    )
//                } else {
//
//                    createContactBackGround(context, url, first_name, phone, company_name)
//                }

                }

            }
//            Preferences().setIsCalled(context, true)


        } catch (e: Exception) {

        }
    }
    //Edit Contact
    @SuppressLint("Range")
    private fun editContactBackGround(
        context: Context,
        number: ArrayList<String>,
        id: String,
        first_name: String,
        url: HttpUrl,
        company_name: String,
        storeContact: StoreContact
    ) {

        val contactUri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number[0]))
        val cur: Cursor = context.contentResolver.query(contactUri, null, null, null, null)!!
        try {
            if (cur.moveToFirst()) {
                do {
                    if (cur.getString(cur.getColumnIndex(PhoneLookup.NUMBER))
                            .equals(number[0], ignoreCase = true)
                    ) {
                        Log.e("asdads","adsasdad")
                        val lookupKey =
                            cur.getString(cur.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY))
                        val uri = Uri.withAppendedPath(
                            ContactsContract.Contacts.CONTENT_LOOKUP_URI,
                            lookupKey
                        )
                        context.contentResolver.delete(uri, null, null)

                    }
                } while (cur.moveToNext())
            }
        } catch (e: java.lang.Exception) {
            Log.e("errorDElete",""+e.message)
            println(e.stackTrace)
        } finally {
            cur.close()
        }
        createContactBackGround(context,
            url,
            storeContact,
            company_name)

    }

    private fun createContactBackGround(context: Context, url: HttpUrl, storeContact: StoreContact, companyName: String) {
        val DisplayName = storeContact.userName
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
            ops.add(
                ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(
                        ContactsContract.Data.MIMETYPE,
                        ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE
                    )
                    .withValue(
                        ContactsContract.CommonDataKinds.Organization.COMPANY,
                        Preferences().getApplicationName(context)
                    ).build()
            )
        }

        // Mobile Number


        //Mobile number will be inserted in ContactsContract.Data table
        for (i in 0 until storeContact.phoneList.size) {
            Log.e("phone123", "" + storeContact.phoneList[i])
            Log.e("phone1234", "" + storeContact.phoneList.size)
            ops.add(
                ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(
                        ContactsContract.Data.MIMETYPE,
                        ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE
                    )
                    .withValue(
                        ContactsContract.CommonDataKinds.Phone.NUMBER,
                        storeContact.phoneList[i]
                    )
                    .withValue(
                        ContactsContract.CommonDataKinds.Phone.TYPE,
                        ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE
                    )
                    .build()
            )
        }

        /* ops.add(
             ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                 .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                 .withValue(
                     ContactsContract.Data.MIMETYPE,
                     ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE
                 )
                 .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, phone)
                 .withValue(
                     ContactsContract.CommonDataKinds.Phone.TYPE,
                     ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE
                 )
                 .build()
         )
         ops.add(
             ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                 .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                 .withValue(
                     ContactsContract.Data.MIMETYPE,
                     ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE
                 )
                 .withValue(
                     ContactsContract.CommonDataKinds.Organization.COMPANY,
                     Preferences().getApplicationName(context)
                 )
                 .build()
         )*/

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
//            val s = context.contentResolver.applyBatch(
//                ContactsContract.AUTHORITY,
//                ops
//            ) //apply above data insertion into contacts list


        } catch (e: Exception) {
            Log.e("errorNewNotification", "" + e.message)
            e.printStackTrace()
        }

    }

    //Bitmap to ByteArray
    private fun bitmapToByteArray(bitmap: Bitmap?): ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap?.compress(Bitmap.CompressFormat.PNG, 90, stream)
        return stream.toByteArray()
    }




}