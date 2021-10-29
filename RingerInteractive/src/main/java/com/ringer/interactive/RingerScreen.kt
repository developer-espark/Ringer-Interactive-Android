package com.ringer.interactive

import android.annotation.SuppressLint
import android.content.*
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.StrictMode
import android.provider.ContactsContract
import android.util.Log
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.ringer.PhoneNumber
import com.ringer.Preferences
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.lang.Exception
import java.net.URL

class RingerScreen : AppCompatActivity() {

    var token : String = ""

    var numberList: ArrayList<PhoneNumber> = ArrayList()
    var name: String = ""
    var idContact: Long? = null
    var contactID: Long? = null

    var editContact = "0"
    var numberFromFirebase: String? = "0"
    var nameFromFirebase: String? = "0"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ringer_screen)


    }
    fun initializeToken() {
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)


        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                return@OnCompleteListener
            }

            // Get new FCM registration token
            token = task.result!!

            // Log and toast

            Log.e("adsads", token)
        })
    }
    /*//Load Contact Data
    fun loadContacts(context: Context, param: String) {


        if (param != "0"){
            //Get Contact Detail
            getContacts(context, "1")
        }else{
            //Get Contact Detail
            getContacts(context, "0")
        }

    }
    //Get Contact Details
    @SuppressLint("Range")
    fun getContacts(context: Context, value: String): StringBuilder {
        val builder = StringBuilder()
        val resolver: ContentResolver = context.contentResolver
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
            if (value == "1") {

                //Check if Number is Exist or not
                // Open Contact Dialog
                checkData(context)
            }else{

                //BackGround
                //Create or Update Contact Detail in Background
                backGroundCheckData(context)
            }

        } else {
            if (value == "1") {

                //Create New Contact Dialog if Number is not exist
                createNewContact()
            }else{
                // Background Create New Contact if NUmber is not exist
                backGroundCheckData(context)
            }

        }
        cursor.close()
        return builder
    }

    //Create or Update Contact Detail in Background
    private fun backGroundCheckData(context: Context) {
        if (numberList.size > 0) {

            for (i in 0 until numberList.size) {

                if (numberList[i].number.equals("123456", true)) {
                    editContact = "1"

                    //Edit Contact In BackGround
                    editContactBackGround(context,numberList[i].number,numberList[i].id)

                    break
                }
            }
            if (editContact != "1") {

                //Create Contact In Background
                createContactBackGround(context)
            }
        } else {
            //Create Contact In Background
            createContactBackGround(context)
        }
    }

    //Check If Number is Already Exist or Not
    private fun checkData(context: Context) {

        if (numberList.size > 0) {

            for (i in 0 until numberList.size) {

                if (numberList[i].number.equals(numberFromFirebase, true)) {
                    editContact = "1"

                    //Edit Contact Dialog if Number Already Exist
//                    editContact(numberList[i].number)
                    editContactBackGround(context, numberList[i].number, numberList[i].id)
                    break
                }
            }
            if (editContact != "1") {

                //Create Contact Dialog If Number not available in contact
                createNewContact()
            }
        } else {
            //Create Contact Dialog If Number not available in contact
            createNewContact()
        }
    }

    @SuppressLint("Range")
    private fun editContact(number: String) {
        Toast.makeText(this, "Edit", Toast.LENGTH_SHORT).show()
        Log.e("editContact", "editContact")

        val uri: Uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI
        val cursor: Cursor? = applicationContext.contentResolver.query(
            uri,
            null,
            null,
            null,
            null
        )
        while (cursor!!.moveToNext()) {

            if (cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                    .equals(number, false)
            ) {
                idContact =
                    cursor.getLong(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID))
                contactID = cursor.getLong(cursor.getColumnIndex(ContactsContract.PhoneLookup._ID))
            }

        }


        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        val url = URL(Preferences().getImageUrl(this).toString())
        val image = BitmapFactory.decodeStream(url.openConnection().getInputStream())


        val i = Intent(Intent.ACTION_EDIT)
        val contactUri: Uri =
            ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, idContact!!)
        i.data = contactUri
        i.putExtra("finishActivityOnSaveCompleted", false)
        startActivity(i)


    }

    //Create Contact Dialog If Number not available in contact
    private fun createNewContact() {
        Log.e("createContact", "createContact")


        val intent =
            Intent(Intent.ACTION_INSERT, ContactsContract.Contacts.CONTENT_URI).apply {
                type = ContactsContract.RawContacts.CONTENT_TYPE
                putExtra(ContactsContract.Intents.Insert.NAME, nameFromFirebase)
                putExtra(ContactsContract.Intents.Insert.PHONE, numberFromFirebase)

                try {
                    val url = URL(Preferences().getImageUrl(this@RingerScreen).toString())
                    val image = BitmapFactory.decodeStream(url.openConnection().getInputStream())
                    val row = ContentValues().apply {
                        put(
                            ContactsContract.CommonDataKinds.Photo.PHOTO,
                            bitmapToByteArray(image)
                        )
                        put(
                            ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE
                        )
                    }
                    val data = arrayListOf(row)

                    putParcelableArrayListExtra(ContactsContract.Intents.Insert.DATA, data)
                } catch (e: IOException) {
                    println(e)
                }
            }
        startActivity(intent)
    }


    //Create Contact In-BackGround
    private fun createContactBackGround(context: Context) {
        val DisplayName = "XYZ"
        val MobileNumber = "123456"
        val photo =
            "https://www.fedex.com/content/dam/fedex/us-united-states/shipping/images/2020/Q3/icon_delivery_purple_lg_2143296207.png"

        val ops = java.util.ArrayList<ContentProviderOperation>()

        ops.add(
            ContentProviderOperation.newInsert(
                ContactsContract.RawContacts.CONTENT_URI
            )
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                .build()
        )


        //Contact Names
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


        // Contact Number
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



        //Contact Photo
        if (photo != null) {
            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)

            val url =
                URL("https://www.fedex.com/content/dam/fedex/us-united-states/shipping/images/2020/Q3/icon_delivery_purple_lg_2143296207.png")
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
            e.printStackTrace()
            Log.e("error",""+e.message)
//            Toast.makeText(this, "Exception: " + e.message, Toast.LENGTH_SHORT).show()
        }
    }

    //EditContact In-BackGround
    private fun editContactBackGround(context: Context, number: String, id: String) {
        val contentResolver: ContentResolver = context.contentResolver

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
        val ops = java.util.ArrayList<ContentProviderOperation>()

        val email = "abc@gmail.com"
        val ContactName = "ABCD"
        val ContactNumber = "1234567890"

        //Contact Email
        if(email != "")
        {
            ops.add(android.content.ContentProviderOperation.newUpdate(android.provider.ContactsContract.Data.CONTENT_URI)
                .withSelection(where,emailParams)
                .withValue(ContactsContract.CommonDataKinds.Email.DATA, email)
                .build())
        }

        //Contact Name
        if(ContactName != "")
        {
            ops.add(android.content.ContentProviderOperation.newUpdate(android.provider.ContactsContract.Data.CONTENT_URI)
                .withSelection(where,nameParams)
                .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, ContactName)
                .build())
        }

        //Contact Number
        if(ContactNumber != "")
        {

            ops.add(android.content.ContentProviderOperation.newUpdate(android.provider.ContactsContract.Data.CONTENT_URI)
                .withSelection(where,numberParams)
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, ContactNumber)
                .build())
        }
        // Picture
        try {
            val url =
                URL("https://i.picsum.photos/id/623/200/200.jpg?hmac=xquTjHIYmAPV3XEGlIUaV_KWyEofkbortxrK79jJhWA")
            val image = BitmapFactory.decodeStream(url.openConnection().getInputStream())
            ops.add(android.content.ContentProviderOperation.newUpdate(android.provider.ContactsContract.Data.CONTENT_URI)
                .withSelection(where,photoParams)
                .withValue(ContactsContract.CommonDataKinds.Photo.PHOTO, bitmapToByteArray(image))
                .build())

        } catch (e: Exception) {
            Log.e("error",""+e.message)
            e.printStackTrace()
        }
        context.contentResolver.applyBatch(ContactsContract.AUTHORITY, ops)


    }


    //If Application is Already Open with same Page then New Intent will be called
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (intent!!.extras != null) {
            nameFromFirebase = intent.extras!!.getString("name")
            numberFromFirebase = intent.extras!!.getString("number")
            loadContacts(this, "1")
        }
    }
    //Convert Bitmap Image to ByteArray to Save Contact Photo
    private fun bitmapToByteArray(bitmap: Bitmap?): ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap?.compress(Bitmap.CompressFormat.PNG, 90, stream)
        return stream.toByteArray()
    }*/
}

