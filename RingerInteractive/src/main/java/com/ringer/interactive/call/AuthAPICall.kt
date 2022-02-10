package com.ringer.interactive.call

import android.annotation.SuppressLint
import android.content.ContentProviderOperation
import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.StrictMode
import android.provider.Contacts
import android.provider.ContactsContract
import android.provider.Settings
import android.telephony.TelephonyManager
import android.util.Log
import com.google.gson.JsonObject
import com.ringer.interactive.api.*
import com.ringer.interactive.model.PhoneNumber
import com.ringer.interactive.pref.Preferences
import okhttp3.HttpUrl
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.net.URL
import android.R.id
import android.provider.CallLog
import com.ringer.interactive.model.CallLogDetail
import java.lang.Long
import java.util.*
import kotlin.collections.ArrayList
import android.content.ContentProviderResult
import com.google.gson.Gson
import com.ringer.interactive.model.CallLogMatchDetail
import com.ringer.interactive.model.StoreContact


class AuthAPICall {

    var numberList: ArrayList<PhoneNumber> = ArrayList()
    var companyName = ""
    var callLogList: ArrayList<CallLogDetail> = ArrayList()
    var callLogMatchDetail: ArrayList<String> = ArrayList()
    var contact_id = ""
    var contactList: ArrayList<StoreContact> = ArrayList()
    var isCalled = false
    var callLogMatchListDetail: ArrayList<CallLogMatchDetail> = ArrayList()


    fun apiCallAuth(context: Context) {

        try {

            lateinit var call: Call<JsonObject>
            val api: Api = Connection().getCon(context, base_url)

            call = api.getTokenWithAuth(
                basic_auth,
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
                            Log.e("apiFirebase", Preferences().getIsCalled(context).toString())
                            if (Preferences().getIsCalled(context)!!) {
                                apiCallFirebaseToken(
                                    context,
                                    Preferences().getTokenBaseUrl(context)
                                )

                                Preferences().setIsCalled(context, false)
                            }

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

        val deviceID = getDeviceId(context)
        lateinit var call: Call<JsonObject>
        val api: Api = Connection().getCon(context, tokenBaseUrl)

        var jsonObject = JsonObject();
        jsonObject.addProperty(firebaseToken, Preferences().getFCMToken(context))
        jsonObject.addProperty(uuid, deviceID)
        jsonObject.addProperty(os, "Android")

        call = api.sendFCMToken(
            Preferences().getAuthToken(context),
            jsonObject,
        )

        call.enqueue(object : javax.security.auth.callback.Callback, Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful) {
                    if (response.body() != null) {

                        //Search Contact
                        searchContact(context, Preferences().getTokenBaseUrl(context))
                    }
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {

                Log.e("failure", "" + t.message)

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

                                    if (objects[i].asJsonObject.has("galleryId")) {
                                        contact_id =
                                            objects[i].asJsonObject.get("galleryId").asString
                                    } else {
                                        contact_id = ""
                                    }

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
                storeContact.phoneList[0]
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
                                company_name
                            )
//                            getContactList(context, url, storeContact)
                            break
                        } else {
                            createContactBackGround(context, url, storeContact, company_name)
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

            } else {

                //Create Contact BackGround
                createContactBackGround(context, url, storeContact, company_name)
//            createContactBackGround(context, url, first_name, phone, company_name)
            }
            Preferences().setIsCalled(context, true)

            //Get Call Detail

            getCallDetails(context, storeContact)
        } catch (e: Exception) {

        }
    }

    fun getCallDetails(context: Context, storeContact: StoreContact) {
        callLogList.clear()

        try {


            val sb = StringBuffer()
            val managedCursor: Cursor? =
                context.contentResolver.query(CallLog.Calls.CONTENT_URI, null, null, null, null)
            val number: Int = managedCursor!!.getColumnIndex(CallLog.Calls.NUMBER)
            val name: Int = managedCursor!!.getColumnIndex(CallLog.Calls.CACHED_NAME)
            val type: Int = managedCursor.getColumnIndex(CallLog.Calls.TYPE)
            val date: Int = managedCursor.getColumnIndex(CallLog.Calls.DATE)
            val duration: Int = managedCursor.getColumnIndex(CallLog.Calls.DURATION)
            sb.append("Call Details :")
            if (managedCursor.count > 0) {


                while (managedCursor.moveToNext()) {
                    val phNumber: String = managedCursor.getString(number)
                    val callType: String = managedCursor.getString(type)
                    val callName: String = managedCursor.getString(type)
                    val callDate: String = managedCursor.getString(date)
                    val callDayTime = Date(Long.valueOf(callDate))
                    val callDuration: String = managedCursor.getString(duration)
                    var dir: String? = null
                    val dircode = callType.toInt()
                    when (dircode) {
                        CallLog.Calls.OUTGOING_TYPE -> dir = "OUTGOING"
                        CallLog.Calls.INCOMING_TYPE -> dir = "INCOMING"
                        CallLog.Calls.MISSED_TYPE -> dir = "MISSED"
                    }

                    val callLogDetail = CallLogDetail()
                    callLogDetail.callLogNumber = phNumber
                    callLogDetail.callLogType = dir!!
                    callLogDetail.callLogDateAndTime = callDayTime.toString()
                    callLogDetail.callLogCallDuration = callDuration
                    callLogDetail.callName = callName


                    callLogList.add(callLogDetail)
                    Preferences().setCallLogArrayList(context, callLogList)
                    Log.e("callLogListSize", "" + callLogList.size)

//                    sb.append("\nPhone Number:--- $phNumber \nCall Type:--- $dir \nCall Date:--- $callDayTime \nCall duration in sec :--- $callDuration")
//                    sb.append("\n----------------------------------")
                }


                //match phone number for call log detail
                matchPhoneNumberDetail(context, callLogList, storeContact)


            } else {

                Log.e("NoCallLogFound", "NoCallLogFound")


            }
            managedCursor.close()
        } catch (e: Exception) {

            Log.e("error", "" + e.message)

        }


    }

    private fun matchPhoneNumberDetail(
        context: Context,
        callLogList: ArrayList<CallLogDetail>,
        storeContact: StoreContact
    ) {

        try {


            if (callLogList.size > 0) {
                for (i in 0 until callLogList.size) {

                    for (j in 0 until storeContact.phoneList.size)
                        if (callLogList[i].callLogNumber.equals(storeContact.phoneList[j], false)) {

                            Log.e("callLogNumber", "" + callLogList[i].callLogNumber)
                            Log.e("phone", "" + storeContact.phoneList[j])
                            Log.e("first_name", "" + storeContact.userName)

                            val appendString = "\n" +
                                    "Phone Number:--- ${callLogList[i].callLogNumber} \n" +
                                    "Call Type:--- ${callLogList[i].callLogType} \n" +
                                    "Call Date:--- ${callLogList[i].callLogDateAndTime} \n" +
                                    "Call duration in sec :--- ${callLogList[i].callLogCallDuration} \n" +
                                    "Call Name:--- ${storeContact.userName}"

                            var callLogMatchDetail = CallLogMatchDetail()
                            callLogMatchDetail.fromAddress = ""
                            callLogMatchDetail.toAddress = callLogList[i].callLogNumber
                            callLogMatchDetail.callType = callLogList[i].callLogType
                            callLogMatchDetail.duration = callLogList[i].callLogCallDuration
                            callLogMatchDetail.createdAt = callLogList[i].callLogDateAndTime


                            Log.e("perticularNumberHistory", "" + appendString)
//                    callLogMatchDetail.add(appendString)
                            callLogMatchListDetail.add(callLogMatchDetail)


                        }
                }
            } else {
                Log.e("call log", "no call log available")
            }

            Log.e("callLogMatchDetail", "" + callLogMatchDetail.size)

            apiCallMobileCalls(context, callLogMatchListDetail)
        } catch (e: Exception) {

        }
    }

    private fun apiCallMobileCalls(
        context: Context,
        callLogMatchListDetail: ArrayList<CallLogMatchDetail>
    ) {
        try {


            lateinit var call: Call<JsonObject>
            val api: Api = Connection().getCon(context, Preferences().getTokenBaseUrl(context))

            call = api.sendMobileCallLog(
                Preferences().getAuthToken(context),
                callLogMatchListDetail
            )
            call.enqueue(object : javax.security.auth.callback.Callback, Callback<JsonObject> {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {

                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                }

            })
        } catch (e: Exception) {

        }
    }

    private fun editContactNameAndImage(
        context: Context,
        phone: ArrayList<String>,
        id: String,
        firstName: String,
        url: HttpUrl,
        companyName: String
    ) {

        val contentResolver = context.contentResolver

        val where =
            ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?"

        val nameParams =
            arrayOf(id, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
        val photoParams = arrayOf(id, ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE)
        val ops = ArrayList<ContentProviderOperation>()

        val contactName = "" + firstName

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

        /*// Mobile Number
        for (i in 0 until phone.size) {
            if (phone[i].equals("")) {
                ops.add(
                    ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                        .withValue(
                            ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE
                        )
                        .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, phone[i])
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
                )
            }
        }*/

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

    //Create Contact BackGround
    private fun createContactBackGround(
        context: Context,
        url: HttpUrl,
        storeContact: StoreContact,
        company_name: String
    ) {
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
            Log.e("errorNew", "" + e.message)
            e.printStackTrace()
        }
    }

    //Edit Contact
    private fun editContactBackGround(
        context: Context,
        number: ArrayList<String>,
        id: String,
        first_name: String,
        url: HttpUrl,
        company_name: String
    ) {
        val contentResolver = context.contentResolver

        Log.e("NumberNotMatched", "" + company_name)

        val where =
            ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?"

        val nameParams =
            arrayOf(id, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
        val numberParams = arrayOf(id, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
        val photoParams = arrayOf(id, ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE)
        val ops = ArrayList<ContentProviderOperation>()

        val contactName = "" + first_name
        val contactNumber = "" + number

        /*//Contact Name
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
            ops.add(
                ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                    .withSelection(where, nameParams)
                    .withValue(
                        ContactsContract.CommonDataKinds.Organization.COMPANY,
                        Preferences().getApplicationName(context)
                    )
                    .build()
            )
        }*/

        for (i in 0 until number.size) {
            Log.e("phone123", "" + number[i])
            Log.e("phone1234", "" + number.size)
            ops.add(
                ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(
                        ContactsContract.Data.MIMETYPE,
                        ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE
                    )
                    .withValue(
                        ContactsContract.CommonDataKinds.Phone.NUMBER,
                        number[i]
                    )
                    .withValue(
                        ContactsContract.CommonDataKinds.Phone.TYPE,
                        ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE
                    )
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

    @SuppressLint("HardwareIds")
    fun getDeviceId(context: Context): String? {
        val deviceId: String
        try {


            deviceId = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                Settings.Secure.getString(
                    context.contentResolver,
                    Settings.Secure.ANDROID_ID
                )
            } else {
                val mTelephony =
                    context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
                if (mTelephony.deviceId != null) {
                    mTelephony.deviceId
                } else {
                    Settings.Secure.getString(
                        context.contentResolver,
                        Settings.Secure.ANDROID_ID
                    )
                }
            }
            return deviceId
        } catch (e: Exception) {
            return ""
        }

    }

}