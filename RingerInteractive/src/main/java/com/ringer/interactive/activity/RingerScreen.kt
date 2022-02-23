package com.ringer.interactive.activity

import android.annotation.SuppressLint
import android.content.ContentProviderOperation
import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.StrictMode
import android.provider.ContactsContract
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.ringer.interactive.R
import com.ringer.interactive.call.AuthAPICall
import com.ringer.interactive.model.PhoneNumber
import com.ringer.interactive.pref.Preferences
import java.io.ByteArrayOutputStream
import java.net.URL

class RingerScreen : AppCompatActivity() {

    companion object {
        val PERMISSIONS_REQUEST_READ_CONTACTS = 100
        val PERMISSIONS_REQUEST_CALL_LOG = 101
    }


    var token: String = ""

    var name: String = ""
    var idContact: Long? = null
    var contactID: Long? = null

    var editContact = "0"
    var numberFromFirebase: String? = "0"
    var nameFromFirebase: String? = "0"
    var numberList :ArrayList<PhoneNumber> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ringer_screen)




       /* if (intent.extras != null) {
            nameFromFirebase = intent.extras!!.getString("name")
            numberFromFirebase = intent.extras!!.getString("number")

            //Load Contact
            loadContacts(this)
        }*/

    }
    //Load Contact Data
    fun loadContacts(context: Context) {

            //Get Contact Detail
//            getContacts(context)
        }


    //Get Contact Details
    @SuppressLint("Range")
    /*fun getContacts(context: Context): StringBuilder {
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
            backGroundCheckData(context)

        } else {
            backGroundCheckData(context)

        }
        cursor.close()
        return builder
    }*/

    //Create or Update Contact Detail in Background
 /*   private fun backGroundCheckData(context: Context) {
        if (numberList.size > 0) {

            for (i in 0 until numberList.size) {

                if (numberList[i].number.equals(numberFromFirebase, true)) {
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
    }*/


    //Create Contact In-BackGround
    private fun createContactBackGround(context: Context) {
        val DisplayName = nameFromFirebase
        val MobileNumber = numberFromFirebase
        val photo = ""+Preferences().getImageUrl(context)

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
                URL(Preferences().getImageUrl(context).toString())
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

        val ContactName = nameFromFirebase
        val ContactNumber = numberFromFirebase


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
                URL(Preferences().getImageUrl(context).toString())
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
   /* override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (intent!!.extras != null) {
            nameFromFirebase = intent.extras!!.getString("name")
            numberFromFirebase = intent.extras!!.getString("number")
            loadContacts(this)
        }
    }*/
    //Convert Bitmap Image to ByteArray to Save Contact Photo
    private fun bitmapToByteArray(bitmap: Bitmap?): ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap?.compress(Bitmap.CompressFormat.PNG, 90, stream)
        return stream.toByteArray()
    }
}

