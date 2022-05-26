package com.ringer.interactive.call

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentProviderOperation
import android.content.Context
import android.content.Context.TELEPHONY_SERVICE
import android.content.pm.PackageManager
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
import androidx.core.app.ActivityCompat
import com.google.gson.JsonObject
import com.ringer.interactive.api.*
import com.ringer.interactive.model.*
import com.ringer.interactive.pref.Preferences
import contacts.core.*
import contacts.core.entities.*
import contacts.core.util.*
import okhttp3.HttpUrl
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.net.URL
import java.util.*


class AuthAPICall {

    var companyName = ""
    var callLogList: ArrayList<CallLogDetail> = ArrayList()
    var callLogMatchDetail: ArrayList<String> = ArrayList()
    var gallaryId = ""
    var contactList: ArrayList<StoreContact> = ArrayList()
    var isCalled = false
    var callLogMatchListDetail: ArrayList<CallLogMatchDetail> = ArrayList()
    var number: String = ""


    //APICall Get Token
    fun apiCallAuth(context: Context) {

        try {

            lateinit var call: Call<JsonObject>
            val api: Api = Connection().getCon(context, base_url)

            val currentTime = Date().time
            val storeTime = Date(Preferences().getTokenStoreTime(context)).time
            val difference = currentTime - storeTime
            val hours = (difference / (1000 * 60 * 60))
            val uuid1: String = getDeviceId(context).toString()
            Log.e("storeTimeDiff", currentTime.toString())
            Log.e("storeTimeDiff", storeTime.toString())
            Log.e("storeTimeDiff", difference.toString())
            Log.e("storeTimeDiff", hours.toString())
            Log.e("FToKEN", "" + Preferences().getFCMToken(context))
            Log.e("uuid", "" + uuid1)

            if (hours > 8) {
                Log.e("tokenCondition", "iff");
                call = api.getTokenWithAuth(
                    basic_auth,
                    Preferences().getEmailAddress(context),
                    Preferences().getUserPassword(context)
                )

                call.enqueue(object : javax.security.auth.callback.Callback, Callback<JsonObject> {
                    override fun onResponse(
                        call: Call<JsonObject>,
                        response: Response<JsonObject>
                    ) {

                        if (response.isSuccessful) {
                            if (response.body() != null) {

                                //Set Token BaseUrl
                                Preferences().setTokenBaseUrl(
                                    context,
                                    response.body()!!.get("location").asString
                                )

                                Preferences().setTokenStoreTime(context, System.currentTimeMillis())

                                //Set Auth Token
                                Preferences().setAuthToken(
                                    context,
                                    response.body()!!.get("token").asString
                                )


                                //api call to send fcm token
                                Log.e("apiFirebase", Preferences().getIsCalled(context).toString())
                                if (Preferences().getIsCalled(context)!!) {
                                    searchContact(context, Preferences().getTokenBaseUrl(context))
                                } else {
                                    apiCallSearchRegistration(
                                        context,
                                        Preferences().getTokenBaseUrl(context)
                                    )

                                    apiCallFirebaseToken(
                                        context,
                                        Preferences().getTokenBaseUrl(context)
                                    )
                                }
                            }
                        }
                    }

                    override fun onFailure(call: Call<JsonObject>, t: Throwable) {

                        Log.e("failure", "" + t.message)

                    }

                })
            } else {
                Log.e("tokenCondition", "else");
                if (Preferences().getIsCalled(context)!!) {
                    searchContact(context, Preferences().getTokenBaseUrl(context))
                } else {
                    apiCallSearchRegistration(
                        context,
                        Preferences().getTokenBaseUrl(context)
                    )

                   /* apiCallFirebaseToken(
                        context,
                        Preferences().getTokenBaseUrl(context)
                    )*/
                }
            }

        } catch (e: Exception) {

            Log.e("errorToken", "" + e.message)

        }
    }

    //API Call for RegistrationSearch
    private fun apiCallSearchRegistration(context: Context, tokenBaseUrl: String) {

        lateinit var call: Call<JsonObject>
        val api: Api = Connection().getCon(context, tokenBaseUrl)

        val uuid1: String = getDeviceId(context).toString()
        call = api.searchMobileRegistration(
            Preferences().getAuthToken(context),
            uuid1
        )
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {

                if (response.isSuccessful) {
                    if (response.body() != null) {

                        Log.e("Response", "" + response)

                        val total = response.body()!!.get("count").asString
                        if (total == "0") {
                            apiCallFirebaseToken(context, tokenBaseUrl)
                        } else {
                            val objects = response.body()!!.get("objects").asJsonArray
                            val mobileRegistrationId =
                                objects.get(0).asJsonObject.get("mobileregistrationId").asString
                            apiCallDeleteToken(context, tokenBaseUrl, mobileRegistrationId)

                        }
                    }
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {

            }

        })


    }

    //Delete Token
    private fun apiCallDeleteToken(
        context: Context,
        tokenBaseUrl: String,
        mobileRegistrationId: String
    ) {
        lateinit var call: Call<JsonObject>
        val api: Api = Connection().getCon(context, tokenBaseUrl)

        call = api.searchDeleteRegistration(
            Preferences().getAuthToken(context),
            mobileRegistrationId
        )
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {

                if (response.code() == 204) {
                    apiCallFirebaseToken(context, tokenBaseUrl)
                }

            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {

            }

        })

    }

    //API Call For Firebase
    @SuppressLint("MissingPermission")
    private fun apiCallFirebaseToken(context: Context, tokenBaseUrl: String) {
        val deviceID = getDeviceId(context)
        lateinit var call: Call<JsonObject>
        val api: Api = Connection().getCon(context, tokenBaseUrl)



        Log.e("FToKEN", "" + Preferences().getPhone(context))
        try {
            val tpm = context.getSystemService(TELEPHONY_SERVICE) as TelephonyManager?
            number = tpm!!.line1Number
            Log.e("DeviceNumber", "" + number)
        } catch (e: Exception) {

            number = ""
        }

        Log.e("deviceID", "" + deviceID)
        Log.e("firebaseToken",""+firebaseToken)


        var jsonObject = JsonObject();
        jsonObject.addProperty(firebaseToken, Preferences().getFCMToken(context))
        jsonObject.addProperty(uuid, deviceID)
        jsonObject.addProperty(os, "Android")
        if (Preferences().getPhone(context) == "") {
            jsonObject.addProperty(phone, number)
        } else {
            jsonObject.addProperty(phone, Preferences().getPhone(context))
        }


        call = api.sendFCMToken(
            Preferences().getAuthToken(context),
            jsonObject,
        )

        call.enqueue(object : javax.security.auth.callback.Callback, Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful) {
                    Preferences().setIsCalled(context, true)
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

    //Searching Contact
    @SuppressLint("Range")
    private fun searchContact(context: Context, tokenBaseUrl: String) {

        var storeLocalDataArrayList: ArrayList<StoreContact> = ArrayList()
        contactList = ArrayList()
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

                            Log.e("responseSearchContact", response.body().toString())
                            val contacts: ArrayList<String> = ArrayList()
                            var phoneMultiple: ArrayList<String> = ArrayList()
                            val objects = response.body()!!.get("objects").asJsonArray
                            if (objects.size() > 0) {

                                for (i in 0 until objects.size()) {

                                    var contactId =
                                        objects.get(i).asJsonObject.get("contactId").asString

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
                                        gallaryId =
                                            objects[i].asJsonObject.get("galleryId").asString
                                    } else {
                                        gallaryId = ""
                                    }


                                    phoneMultiple = ArrayList()
                                    var phone = objects[i].asJsonObject.get("phone").asJsonArray
                                    for (j in 0 until phone.size()) {
                                        phoneMultiple.add(phone[j].asString)
                                        contacts.add(phone[j].asString)
                                        Log.e("phoneMultiple", "" + phone[j])
                                    }


                                    var storeContact = StoreContact()
                                    storeContact.contactId = contactId
                                    storeContact.userName = first_name
                                    storeContact.galleryId = gallaryId
                                    storeContact.phoneList = phoneMultiple
                                    storeContact.firstName = uFirstName
                                    storeContact.lastName = lastName
                                    storeContact.modifyAt = modifyAt

                                    contactList.add(storeContact)


                                    //Contact Query for same result
                                    val totalContacts = Contacts(context).query().find()
                                    val matchesContact = Contacts(context).broadQuery().whereAnyContactDataPartiallyMatches(phoneMultiple[0]).find()

                                    Log.e("matchesContact", "" + matchesContact)
                                    Log.e("totalContacts", "" + totalContacts)

                                    if (Preferences().getLocalData(context) != null) {
                                        storeLocalDataArrayList =
                                            Preferences().getLocalData(context)!!
                                        var isFound = false

                                        for (k in 0 until storeLocalDataArrayList.size) {
                                            if (contactId == storeLocalDataArrayList[k].contactId) {
                                                isFound = true
                                                if (modifyAt != storeLocalDataArrayList[k].modifyAt) {

                                                    if (matchesContact.size > 0) {
                                                        updateContact(
                                                            context,
                                                            matchesContact,
                                                            storeContact
                                                        )
                                                        break
                                                    }
//
                                                }
                                            }
                                        }
                                        if (!isFound || matchesContact.size == 0) {
                                            createContact(context, storeContact)
                                        }
                                    } else {
                                        if (matchesContact.size == 0) {
                                            createContact(context, storeContact)
                                        } else {
                                            updateContact(context, matchesContact, storeContact)
                                        }
                                    }
                                }

//                                Preferences().setLocalData(context, contactList)
                            }


                        }
                    }
                } catch (e: Exception) {

                    Log.e("errorSearch", "" + e.message)
                    Log.e("errorSearch", "" + e.stackTraceToString())
                } finally {
                    Log.e("hrereerer","reereereee")
                    getCallDetails(context, contactList)
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {

                Log.e("failure", "" + t.message)
                if (contactList.size > 0) {
                    getCallDetails(context, contactList);
                }
            }
        })
    }

    // Create Contact Background
    private fun createContact(context: Context, storeContact: StoreContact) {
        val insertResult = Contacts(context)
            .insert()
            .rawContact {
                setName {
                    givenName = storeContact.firstName
                    familyName = storeContact.lastName
                }
                for (k in 0 until storeContact.phoneList.size) {
                    addPhone {
                        number = storeContact.phoneList[k]
                        type = PhoneEntity.Type.MAIN
                    }
                }
                setOrganization {
                    company = companyName
                }
            }
            .allowBlanks(true)
            .commit()
        if (insertResult.isSuccessful) {
            val matchesContact = Contacts(context).query()
                .where { (Phone.Number `in` storeContact.phoneList) }
                .include {
                    Fields.all()
                }.find()

            if (matchesContact.size > 0) {
                getContactImageNew(
                    context,
                    Preferences().getTokenBaseUrl(context),
                    matchesContact[0].rawContacts[0],
                    storeContact
                )
            }
        }
    }

    //Update Contact Background
    private fun updateContact(
        context: Context,
        matchesContact: BroadQuery.Result,
        storeContact: StoreContact
    ) {
        val mutableContact1 =
            matchesContact[0].mutableCopy {
                setName {
                    givenName = storeContact.firstName
                    familyName = storeContact.lastName
                }
                /*for (jn in 0 until phoneList().size - 1) {
                    removePhone(phoneList().get(jn))
                }*/

                for (n in 0 until storeContact.phoneList.size) {
                    val contactMat = Contacts(context).broadQuery().whereAnyContactDataPartiallyMatches(storeContact.phoneList[n]).find()
                    if (contactMat.size == 0) {
                        addPhone {
                            number =
                                storeContact.phoneList[n]
                            type =
                                PhoneEntity.Type.MAIN
                        }
                    }
                }
                setOrganization {
                    company = companyName
                }
            }
        val updateResult1 =
            Contacts(context).update()
                .contacts(mutableContact1).commit()
        getContactImageNew(
            context,
            Preferences().getTokenBaseUrl(context),
            matchesContact[0].rawContacts[0],
            storeContact
        )
        Log.e(
            "updateResult",
            updateResult1.toString()
        )
    }

    // Get Contact Image API
    private fun getContactImageNew(
        context: Context, tokenBaseUrl: String, rawContact: RawContact,
        storeContact: StoreContact
    ) {
        val uuid1: String = getDeviceId(context).toString()
        try {

            lateinit var call: Call<ResponseBody>
            val api: Api = Connection().getCon(context, tokenBaseUrl)
            call = api.getAvatar(
                Preferences().getAuthToken(context),
                storeContact.galleryId,
                uuid1,
                storeContact.phoneList[0],
                storeContact.firstName,
                storeContact.lastName,
                storeContact.contactId,
                "Android"
            )
            call.enqueue(object : javax.security.auth.callback.Callback, Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    try {
                        if (response.isSuccessful) {
                            if (response.body() != null) {
                                val url = URL(response.raw().request.url.toString())
                                val image = BitmapFactory.decodeStream(
                                    url.openConnection().getInputStream()
                                )

                                rawContact.setPhoto(Contacts(context), image)
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
            Log.e("ExceptIon",""+e.message)
        }
    }

    private fun getContactImage(
        context: Context,
        tokenBaseUrl: String,
        contactIndex: Int,
        storeLocalDataList: ArrayList<StoreContact>,
        isEdit: Boolean
    ) {
        try {


            lateinit var call: Call<ResponseBody>
            val api: Api = Connection().getCon(context, tokenBaseUrl)

            call = api.getAvatar(
                Preferences().getAuthToken(context),
                storeLocalDataList[contactIndex].galleryId,
                "uuid1",
                storeLocalDataList[contactIndex].phoneList[0],
                storeLocalDataList[contactIndex].firstName,
                storeLocalDataList[contactIndex].lastName,
                storeLocalDataList[contactIndex].contactId,
                "Android"
            )
            call.enqueue(object : javax.security.auth.callback.Callback, Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    try {
                        if (response.isSuccessful) {
                            if (response.body() != null) {
//
                                if (isEdit) {
                                    editContact(
                                        context,
                                        response.raw().request.url,
                                        contactIndex,
                                        storeLocalDataList
                                    )
                                } else {
                                    createContactBackGround(
                                        context,
                                        response.raw().request.url,
                                        contactIndex,
                                        storeLocalDataList
                                    )
                                }
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
    private fun editContact(
        context: Context,
        url: HttpUrl,
        contactIndex: Int,
        storeLocalDataList: ArrayList<StoreContact>
    ) {
        val contactUri = Uri.withAppendedPath(
            ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
            Uri.encode(storeLocalDataList[contactIndex].phoneList[0])
        )
        val cur: Cursor = context.contentResolver.query(contactUri, null, null, null, null)!!
        try {
            if (cur.moveToFirst()) {
                do {
                    if (cur.getString(cur.getColumnIndex(ContactsContract.PhoneLookup.NUMBER))
                            .equals(
                                storeLocalDataList[contactIndex].phoneList[0],
                                ignoreCase = true
                            )
                    ) {
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
        createContactBackGround(
            context,
            url,
            contactIndex, storeLocalDataList
        )

    }


    //Call Detail
    @SuppressLint("MissingPermission")
    fun getCallDetails(context: Context, contactList: ArrayList<StoreContact>) {
        try {
            var numberArrayList: ArrayList<String> = ArrayList()
            val sb = StringBuffer()
            val managedCursor: Cursor? =
                context.contentResolver.query(CallLog.Calls.CONTENT_URI, null, null, null, null)
            val number: Int = managedCursor!!.getColumnIndex(CallLog.Calls.NUMBER)
            val name: Int = managedCursor!!.getColumnIndex(CallLog.Calls.CACHED_NAME)
            val type: Int = managedCursor.getColumnIndex(CallLog.Calls.TYPE)
            val date: Int = managedCursor.getColumnIndex(CallLog.Calls.DATE)
            val duration: Int = managedCursor.getColumnIndex(CallLog.Calls.DURATION)
            sb.append("Call Details :")
            Log.e("contactList",""+contactList.size)
            for (i in 0 until contactList.size) {
                if (contactList[i].phoneList.size > 0) {
                    numberArrayList.addAll(contactList[i].phoneList)
                }
            }
            Log.e("numberArrayList",""+numberArrayList.size)
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

                    if (numberArrayList.size > 0) {
                        if (numberArrayList.contains(phNumber) && callDayTime.time > Preferences().getContactLastSyncTime(
                                context
                            )
                        ) {
                            val appendString = "\n" +
                                    "Phone Number:--- ${phNumber} \n" +
                                    "Call Type:---" + type + " \n" +
                                    "Call Date:--- ${date} \n" +
                                    "Call duration in sec :--- ${duration} \n" +
                                    "Call Name:--- "

                            var callLogMatchDetail = CallLogMatchDetail()
                            callLogMatchDetail.toAddress = phNumber
                            callLogMatchDetail.fromAddress = number.toString()
                            if (dir == "OUTGOING") {
                                callLogMatchDetail.callType = "Outbound"
                            } else {

                                callLogMatchDetail.callType = "Inbound"
                            }
                            callLogMatchDetail.duration = callDuration
                            callLogMatchDetail.createdAt = callDayTime.time.toString()


                            Log.e("perticularNumberHistory", "" + appendString)

                            callLogMatchListDetail.add(callLogMatchDetail)
                            Log.e("callLogMatchDetail", "" + callLogMatchListDetail.size)
                        }
                    }
                }

                if (callLogMatchListDetail.size > 0) {
                    apiCallMobileCalls(context, callLogMatchListDetail)
                    Preferences().setContactLastSyncTime(context, System.currentTimeMillis())
                }
            } else {
                Log.e("NoCallLogFound", "NoCallLogFound")
            }
            managedCursor.close()
        } catch (e: Exception) {
            Log.e("error", "" + e.printStackTrace())
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
                            callLogMatchListDetail.add(callLogMatchDetail)
                            Preferences().setMatchCallLogDetail(context, callLogMatchListDetail)

                            Log.e("callLogMatchDetail", "" + callLogMatchListDetail.size)

                        }
                }
            } else {
                Log.e("call log", "no call log available")
            }



            apiCallMobileCalls(context, callLogMatchListDetail)
        } catch (e: Exception) {

        }
    }

    private fun apiCallMobileCalls(
        context: Context,
        callLogMatchListDetail: ArrayList<CallLogMatchDetail>?
    ) {
        try {

            val reverseList: List<CallLogMatchDetail> = callLogMatchListDetail!!.reversed()

//            Log.e("datareverse", "" + reverseList.get(0).toAddress)
//
//            var lastCallLog = callLogMatchListDetail!!.get(reverseList.size - 1)
//
//            var lastCallLogList: ArrayList<CallLogMatchDetail> = ArrayList()
//            lastCallLogList.add(lastCallLog)
//
//            Log.e("datareverse1", "" + lastCallLogList.get(0).toAddress)
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

    //Create Contact BackGround
    private fun createContactBackGround(
        context: Context,
        url: HttpUrl,
        contactIndex: Int,
        storeLocalDataList: ArrayList<StoreContact>
    ) {
        val DisplayName = storeLocalDataList[contactIndex].userName
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
        for (i in 0 until storeLocalDataList[contactIndex].phoneList.size) {
            ops.add(
                ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(
                        ContactsContract.Data.MIMETYPE,
                        ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE
                    )
                    .withValue(
                        ContactsContract.CommonDataKinds.Phone.NUMBER,
                        storeLocalDataList[contactIndex].phoneList[i]
                    )
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
            storeLocalDataList[contactIndex].isModified = true
            Preferences().setLocalData(context, storeLocalDataList)
        } catch (e: Exception) {
            Log.e("errorNewCreate", "" + e.message)
            e.printStackTrace()
        }
    }

    //Bitmap to ByteArray
    private fun bitmapToByteArray(bitmap: Bitmap?): ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap?.compress(Bitmap.CompressFormat.PNG, 90, stream)
        return stream.toByteArray()
    }

    // Get DeviceID
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