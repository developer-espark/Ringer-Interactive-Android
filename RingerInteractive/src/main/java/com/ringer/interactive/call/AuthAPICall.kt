package com.ringer.interactive.call

import android.annotation.SuppressLint
import android.content.ContentProviderOperation
import android.content.Context
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
import com.ringer.interactive.model.CallLogDetail
import com.ringer.interactive.model.CallLogMatchDetail
import com.ringer.interactive.model.PhoneNumber
import com.ringer.interactive.model.StoreContact
import com.ringer.interactive.pref.Preferences
import okhttp3.HttpUrl
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.lang.Long
import java.net.URL
import java.util.*
import java.util.regex.Pattern
import kotlin.ByteArray
import kotlin.Exception
import kotlin.Int
import kotlin.String
import kotlin.Throwable
import kotlin.arrayOf
import kotlin.toString


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
//                            if (Preferences().getIsCalled(context)!!) {
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

    private fun apiCallFirebaseToken(context: Context, tokenBaseUrl: String) {
        Preferences().setIsCalled(context, false)
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
                            var phoneNumValue = cursorPhone.getString(
                                cursorPhone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                            )
                            Log.e("phoneNumValueB",""+phoneNumValue)
                            var phNo = phoneNumValue.replace("[()\\s-]+".toRegex(), "")

                            Log.e("phoneNumValueC",""+phNo)
                            val names = cursorPhone.getString(cursorPhone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))

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
            backGroundCheckData(context, url, storeContact, companyName)

        } else {

            //checkBackground Data for update or create contact
            backGroundCheckData(context, url, storeContact, companyName)

        }

        cursor.close()
        return builder
    }
    fun PhoneNumberWithoutCountryCode(phoneNumberWithCountryCode: String): String? { //+91 7698989898
        val compile = Pattern.compile(
            "\\+(?:998|996|995|994|993|992|977|976|975|974|973|972|971|970|968|967|966|965|964|963|962|961|960|886|880|856|855|853|852|850|692|691|690|689|688|687|686|685|683|682|681|680|679|678|677|676|675|674|673|672|670|599|598|597|595|593|592|591|590|509|508|507|506|505|504|503|502|501|500|423|421|420|389|387|386|385|383|382|381|380|379|378|377|376|375|374|373|372|371|370|359|358|357|356|355|354|353|352|351|350|299|298|297|291|290|269|268|267|266|265|264|263|262|261|260|258|257|256|255|254|253|252|251|250|249|248|246|245|244|243|242|241|240|239|238|237|236|235|234|233|232|231|230|229|228|227|226|225|224|223|222|221|220|218|216|213|212|211|98|95|94|93|92|91|90|86|84|82|81|66|65|64|63|62|61|60|58|57|56|55|54|53|52|51|49|48|47|46|45|44\\D?1624|44\\D?1534|44\\D?1481|44|43|41|40|39|36|34|33|32|31|30|27|20|7|1\\D?939|1\\D?876|1\\D?869|1\\D?868|1\\D?849|1\\D?829|1\\D?809|1\\D?787|1\\D?784|1\\D?767|1\\D?758|1\\D?721|1\\D?684|1\\D?671|1\\D?670|1\\D?664|1\\D?649|1\\D?473|1\\D?441|1\\D?345|1\\D?340|1\\D?284|1\\D?268|1\\D?264|1\\D?246|1\\D?242|1)\\D?"
        )
        //Log.e(tag, "number::_>" +  number);//OutPut::7698989898
        return phoneNumberWithCountryCode.replace(compile.pattern().toRegex(), "")
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
                for (j in 0 until storeContact.phoneList.size) {
                    var isMatch = false
                    var id : String= "0"
                    var phone_name : String = "0"

                        for (i in 0 until numberList.size) {

                            Log.e("NumberLIst",""+numberList[i].number)
                            Log.e("NumberLIst1",""+storeContact.phoneList[j])
                        if (numberList[i].number.equals(storeContact.phoneList[j], true)) {

                            Log.e("asdName",""+numberList[i].name)

                            isMatch = true
                            id = numberList[i].id
                            phone_name = numberList[i].name



                        }
                    }
                    if (isMatch) {



                        editContact(context,storeContact.phoneList,id,storeContact.userName,url,company_name,phone_name,storeContact)
                        /*editContactBackGround(
                            context,
                            storeContact.phoneList,
                            id,
                            storeContact.userName,
                            url,
                            company_name
                        )*/
                    } else {

                        createContactBackGround(context, url, storeContact, company_name)
                    }

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
        storeContact: StoreContact
    ) {
        val contactUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneList[0]))
        val cur: Cursor = context.contentResolver.query(contactUri, null, null, null, null)!!
        try {
            if (cur.moveToFirst()) {
                do {
                    if (cur.getString(cur.getColumnIndex(ContactsContract.PhoneLookup.NUMBER))
                            .equals(phoneList[0], ignoreCase = true)
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
        createContactBackGroundDelete(context,
            url,
            storeContact,
            companyName,phoneName)

    }

    private fun createContactBackGroundDelete(
        context: Context,
        url: HttpUrl,
        storeContact: StoreContact,
        companyName: String,
        phoneName: String
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