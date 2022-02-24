package com.ringer.interactive.call

import android.annotation.SuppressLint
import android.content.ContentProviderOperation
import android.content.Context
import android.content.Context.TELEPHONY_SERVICE
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.StrictMode
import android.provider.CallLog
import android.provider.ContactsContract
import android.provider.Settings
import android.telephony.TelephonyManager
import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.ringer.interactive.api.*
import com.ringer.interactive.model.*
import com.ringer.interactive.pref.Preferences
import okhttp3.HttpUrl
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.net.URL
import java.util.*
import kotlin.collections.ArrayList


class AuthAPICall {

    var numberList: ArrayList<PhoneNumber> = ArrayList()
    var companyName = ""
    var callLogList: ArrayList<CallLogDetail> = ArrayList()
    var callLogMatchDetail: ArrayList<String> = ArrayList()
    var contact_id = ""
    var contactList: ArrayList<StoreContact> = ArrayList()
    var isCalled = false
    var callLogMatchListDetail: ArrayList<CallLogMatchDetail> = ArrayList()
    var number: String = ""


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
//                            if (Preferences().getIsCalled(context)!!) {


                            apiCallSearchRegistration(context,Preferences().getTokenBaseUrl(context))

                            apiCallFirebaseToken(
                                context,
                                Preferences().getTokenBaseUrl(context)
                            )


//                            }

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

    private fun apiCallSearchRegistration(context: Context, tokenBaseUrl: String) {
        lateinit var call: Call<JsonObject>
        val api: Api = Connection().getCon(context, tokenBaseUrl)

        val uuid1 : String = getDeviceId(context).toString()
        call = api.searchMobileRegistration(
            Preferences().getAuthToken(context),
            uuid1
        )
        call.enqueue(object : Callback<JsonObject>{
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {

                if (response.isSuccessful){
                    if (response.body() != null){

                        val total = response.body()!!.get("total").asString
                        if (total == "0"){

                            apiCallFirebaseToken(context, tokenBaseUrl)
                        }else{

                            apiCallDeleteToken(context,tokenBaseUrl)

                        }

                    }
                }

            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {

            }

        })




    }

    private fun apiCallDeleteToken(context: Context, tokenBaseUrl: String) {
        lateinit var call: Call<JsonObject>
        val api: Api = Connection().getCon(context, tokenBaseUrl)

        val uuid1 : String = getDeviceId(context).toString()
        call = api.searchDeleteRegistration(
            Preferences().getAuthToken(context),
            uuid1
        )
        call.enqueue(object : Callback<JsonObject>{
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {

                if (response.code() == 204){
                    apiCallFirebaseToken(context, tokenBaseUrl)
                }

            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {

            }

        })

    }

    @SuppressLint("MissingPermission")
    private fun apiCallFirebaseToken(context: Context, tokenBaseUrl: String) {
        Preferences().setIsCalled(context, false)
        val deviceID = getDeviceId(context)
        lateinit var call: Call<JsonObject>
        val api: Api = Connection().getCon(context, tokenBaseUrl)


        Log.e("FToKEN", "" + Preferences().getFCMToken(context))
        try {
            val tpm = context.getSystemService(TELEPHONY_SERVICE) as TelephonyManager?
            number = tpm!!.line1Number
            Log.e("DeviceNumber", "" + number)
        } catch (e: Exception) {

            number = ""
        }

        Log.e("deviceID",""+deviceID)


        var jsonObject = JsonObject();
        jsonObject.addProperty(firebaseToken, Preferences().getFCMToken(context))
        jsonObject.addProperty(uuid, deviceID)
        jsonObject.addProperty(os, "Android")
        jsonObject.addProperty(phone, number)

        call = api.sendFCMToken(
            Preferences().getAuthToken(context),
            jsonObject,
        )

        call.enqueue(object : javax.security.auth.callback.Callback, Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful) {
                    if (response.body() != null) {
                        Log.e("status", "40")
                        //Search Contact
                        searchContact(context, Preferences().getTokenBaseUrl(context))
                    } else {
                        Log.e("status", "409")
                        //Search Contact
                        searchContact(context, Preferences().getTokenBaseUrl(context))
                    }
                } else {
                    //Search Contact
                    Log.e("status", "4091")
//                    Notifications().searchContact(context, tokenBaseUrl)
                    searchContact(context, Preferences().getTokenBaseUrl(context))
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {

                Log.e("failure", "" + t.message)
                Log.e("status", "4092")
                //Search Contact
                searchContact(context, Preferences().getTokenBaseUrl(context))

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

                                    val modifyAt =
                                        objects[i].asJsonObject.get("modifiedAt").asString


                                    val uFirstName =
                                        objects[i].asJsonObject.get("firstName").asString


                                    val lastName = objects[i].asJsonObject.get("lastName").asString

                                    if (objects[i].asJsonObject.has("galleryId")) {
                                        contact_id =
                                            objects[i].asJsonObject.get("galleryId").asString
                                    } else {
                                        contact_id = ""
                                    }

                                    val userContactID =
                                        objects[i].asJsonObject.get("contactId").asString

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
                                    storeContact.modifyAt = modifyAt

                                    contactList.add(storeContact)

                                    Preferences().setStoreContact(context, storeContact)


                                }

                            }
                            Log.e("PreferenceData", "" + Preferences().getLocalData(context))
                            if (Preferences().getLocalData(context).isNullOrEmpty()) {

                                Log.e("dataclear", "dataclear")

                                Preferences().setLocalData(context, contactList)






                                Log.e("listSize", "" + contactList.size)
                                for (i in 0 until contactList.size) {
                                    if (!contactList.get(i).galleryId.equals("")) {
                                        getContactImage(
                                            context,
                                            tokenBaseUrl,
                                            contactList.get(i),
                                            contactList
                                        )
                                    }
                                }


                            } else {
                                var storeLocalDataArrayList: ArrayList<StoreContact> =
                                    Preferences().getLocalData(context)!!


                                Log.e("storeLocalDataArrayList", "" + storeLocalDataArrayList.size)
                                for (k in 0 until contactList.size) {
                                    var isModify = false
                                    for (j in 0 until storeLocalDataArrayList.size) {


                                        if (contactList[k].userName == storeLocalDataArrayList[j].userName) {


                                            if (contactList[k].modifyAt > storeLocalDataArrayList[j].modifyAt) {


                                                isModify = true
                                                break


                                            }
                                        }

                                    }
                                    if (isModify) {
                                        Log.e("modified", "modified")
                                        if (!contactList[k].galleryId.equals("")) {
                                            getContactImage(
                                                context,
                                                tokenBaseUrl,
                                                contactList.get(k),
                                                contactList
                                            )
                                        }
                                    } else {

                                        if (contactList.size > storeLocalDataArrayList.size) {

                                            if (!contactList[k].galleryId.equals("")) {
                                                getContactImage(
                                                    context,
                                                    tokenBaseUrl,
                                                    contactList.get(k),
                                                    contactList
                                                )
                                            }
                                        }
                                    }

                                }
                            }


                            /*Log.e("listSize", "" + contactList.size)
                            for (i in 0 until contactList.size) {
                                if (!contactList.get(i).galleryId.equals("")) {
                                    getContactImage(context, tokenBaseUrl, contactList.get(i))
                                }
                            }*/

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
        storeContact: StoreContact,
        storeLocalDataList: ArrayList<StoreContact>
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

                                getContactList(
                                    context,
                                    response.raw().request.url,
                                    storeContact,
                                    storeLocalDataList
                                )

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
        storeContact: StoreContact,
        storeLocalDataList: ArrayList<StoreContact>
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
                            var phoneNumValue = cursorPhone.getString(
                                cursorPhone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                            )
                            Log.e("phoneNumValueB", "" + phoneNumValue)
                            var phNo = phoneNumValue.replace("[()\\s-]+".toRegex(), "")

                            Log.e("phoneNumValueC", "" + phNo)
                            val names =
                                cursorPhone.getString(cursorPhone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))

                            val contactId =
                                cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.PhoneLookup._ID))
                            builder.append("Contact: ").append(name).append(", Phone Number: ")
                                .append(
                                    phoneNumValue
                                ).append("\n\n")
                            val phoneNumber = PhoneNumber()
                            phoneNumber.number = phNo
                            phoneNumber.id = contactId
                            phoneNumber.name = names
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
            backGroundCheckData(context, url, storeContact, companyName, storeLocalDataList)

        } else {

            //checkBackground Data for update or create contact
            backGroundCheckData(context, url, storeContact, companyName, storeLocalDataList)

        }

        cursor.close()
        return builder
    }

    //checkBackground Data for update or create contact
    private fun backGroundCheckData(
        context: Context,
        url: HttpUrl,
        storeContact: StoreContact,
        company_name: String,
        storeLocalDataList: ArrayList<StoreContact>
    ) {
        try {


            Log.e("numberListSize", "" + numberList.size)
            Log.e("numberListSize123", "" + Gson().toJson(numberList))
            if (numberList.size > 0) {
                for (j in 0 until storeContact.phoneList.size) {
                    var isMatch = false
                    var id: String = "0"
                    var phone_name: String = "0"

                    for (i in 0 until numberList.size) {

                        Log.e("NumberLIst", "" + numberList[i].number)
                        Log.e("NumberLIst1", "" + storeContact.phoneList[j])
                        if (numberList[i].number.equals(storeContact.phoneList[j], true)) {

                            Log.e("asdName", "" + numberList[i].name)

                            isMatch = true
                            id = numberList[i].id
                            phone_name = numberList[i].name


                        }
                    }
                    if (isMatch) {


                        editContact(
                            context,
                            storeContact.phoneList,
                            id,
                            storeContact.userName,
                            url,
                            company_name,
                            phone_name,
                            storeContact,
                            storeLocalDataList
                        )
                        /*editContactBackGround(
                            context,
                            storeContact.phoneList,
                            id,
                            storeContact.userName,
                            url,
                            company_name
                        )*/
                    } else {

                        createContactBackGround(
                            context,
                            url,
                            storeContact,
                            company_name,
                            storeLocalDataList
                        )
                    }

                }

            } else {

                //Create Contact BackGround
                createContactBackGround(
                    context,
                    url,
                    storeContact,
                    company_name,
                    storeLocalDataList
                )
//            createContactBackGround(context, url, first_name, phone, company_name)
            }
            Preferences().setIsCalled(context, true)

            //Get Call Detail

            getCallDetails(context, storeContact)
        } catch (e: Exception) {
            Preferences().setIsCalled(context, true)
        }
    }

    @SuppressLint("Range")
    private fun editContact(
        context: Context,
        phoneList: ArrayList<String>,
        id: String,
        userName: String,
        url: HttpUrl,
        companyName: String,
        phoneName: String,
        storeContact: StoreContact,
        storeLocalDataList: ArrayList<StoreContact>
    ) {
        val contactUri = Uri.withAppendedPath(
            ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
            Uri.encode(phoneList[0])
        )
        val cur: Cursor = context.contentResolver.query(contactUri, null, null, null, null)!!
        try {
            if (cur.moveToFirst()) {
                do {
                    if (cur.getString(cur.getColumnIndex(ContactsContract.PhoneLookup.NUMBER))
                            .equals(phoneList[0], ignoreCase = true)
                    ) {
                        Log.e("asdads", "adsasdad")
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
            Log.e("errorDElete", "" + e.message)
            println(e.stackTrace)
        } finally {
            cur.close()
        }
        createContactBackGroundDelete(
            context,
            url,
            storeContact,
            companyName, phoneName, storeLocalDataList
        )

    }

    private fun createContactBackGroundDelete(
        context: Context,
        url: HttpUrl,
        storeContact: StoreContact,
        companyName: String,
        phoneName: String,
        storeLocalDataList: ArrayList<StoreContact>
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
                        phoneName
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


        Preferences().setLocalData(context, storeLocalDataList)


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

    fun getCallDetails(context: Context, storeContact: StoreContact) {
//        callLogList.clear()

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
                    val callDayTime = Date(callDate.toLong())
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

                    Log.e("callLogList",""+callLogList.size)
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
                        if (callLogList[i].callLogNumber.equals(storeContact.phoneList[j], true)) {

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
                            Preferences().setMatchCallLogDetail(context,callLogMatchListDetail)



                            Log.e("callLogMatchDetail", "" + callLogMatchListDetail.size)

                        }
                }
            } else {
                Log.e("call log", "no call log available")
            }



            apiCallMobileCalls(context, Preferences().getMatchCallLogDetail(context))
        } catch (e: Exception) {

        }
    }

    private fun apiCallMobileCalls(
        context: Context,
        callLogMatchListDetail: ArrayList<CallLogMatchDetail>?
    ) {
        try {

            val reverseList: List<CallLogMatchDetail> = callLogMatchListDetail!!.reversed()

            Log.e("datareverse",""+reverseList.get(0).toAddress)

            var lastCallLog = callLogMatchListDetail!!.get(reverseList.size - 1)

            var lastCallLogList : ArrayList<CallLogMatchDetail> = ArrayList()
            lastCallLogList.add(lastCallLog)

            Log.e("datareverse1", "" + lastCallLogList.get(0).toAddress)
            lateinit var call: Call<JsonObject>
            val api: Api = Connection().getCon(context, Preferences().getTokenBaseUrl(context))

            call = api.sendMobileCallLog(
                Preferences().getAuthToken(context),
                lastCallLogList
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
        company_name: String,
        storeLocalDataList: ArrayList<StoreContact>
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
            Preferences().setLocalData(context, storeLocalDataList)
//            val s = context.contentResolver.applyBatch(
//                ContactsContract.AUTHORITY,
//                ops
//            ) //apply above data insertion into contacts list


        } catch (e: Exception) {
            Log.e("errorNewCreate", "" + e.message)
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
//        contentResolver.applyBatch(ContactsContract.Data.CONTENT_URI,)
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