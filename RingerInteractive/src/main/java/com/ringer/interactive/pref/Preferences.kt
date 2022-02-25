package com.ringer.interactive.pref

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import androidx.core.net.toUri
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.ringer.interactive.model.CallLogDetail
import com.ringer.interactive.model.CallLogMatchDetail
import com.ringer.interactive.model.StoreContact


class Preferences {
    private lateinit var preferences: SharedPreferences

    //set contact url
    fun setImageUrl(context: Context, image_url: Uri?) {
        preferences = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = preferences.edit()
        editor.putString("image_url", image_url.toString())
        editor.commit()
    }

    fun getImageUrl(context: Context): Uri {
        preferences = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
        val image_url: String? = preferences.getString("image_url", "")
        return image_url!!.toUri()

    }

    //set contact url
    fun setIsCalled(context: Context, isCalled: Boolean) {
        preferences = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = preferences.edit()
        editor.putBoolean("isCalled", isCalled)
        editor.commit()
    }

    fun getIsCalled(context: Context): Boolean? {
        preferences = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
        val isCalled: Boolean? = preferences.getBoolean("isCalled", false)
        return isCalled

    }


    //set email address
    fun setEmailAddress(context: Context, email: String) {
        preferences = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = preferences.edit()
        editor.putString("user_email", email)
        editor.commit()
    }

    fun getEmailAddress(context: Context): String {
        preferences = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
        val contact_email: String? = preferences.getString("user_email", "")
        return contact_email.toString()

    }

    //set password
    fun setPassword(context: Context, password: String) {
        preferences = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = preferences.edit()
        editor.putString("user_password", password)
        editor.commit()
    }

    fun getUserPassword(context: Context): String {
        preferences = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
        val user_password: String? = preferences.getString("user_password", "")
        return user_password.toString()

    }

    //set base url

    fun setTokenBaseUrl(context: Context, base_url: String) {
        preferences = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = preferences.edit()
        editor.putString("base_url", base_url)
        editor.commit()
    }

    fun getTokenBaseUrl(context: Context): String {
        preferences = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
        val base_url: String? = preferences.getString("base_url", "")
        return base_url.toString()
    }



    //set api auth token
    fun setAuthToken(context: Context, auth_token: String) {
        preferences = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = preferences.edit()
        editor.putString("auth_token", auth_token)
        editor.commit()
    }

    fun getAuthToken(context: Context): String {
        preferences = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
        val auth_token: String? = preferences.getString("auth_token", "")
        return auth_token.toString()
    }

    //set fcm token
    fun setFCMToken(context: Context,fcm_token : String){
        preferences = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = preferences.edit()
        editor.putString("fcm_token", fcm_token)
        editor.commit()
    }

    fun getFCMToken(context: Context): String {
        preferences = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
        val fcm_token: String? = preferences.getString("fcm_token", "")
        return fcm_token.toString()
    }

    //set app name
    fun setApplicationName(context: Context,app_name : String){
        preferences = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = preferences.edit()
        editor.putString("app_name", app_name)
        editor.commit()
    }

    fun getApplicationName(context: Context): String {
        preferences = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
        val app_name: String? = preferences.getString("app_name", "")
        return app_name.toString()
    }


    fun setCallLogArrayList(context: Context,callLogList : ArrayList<CallLogDetail>){
        preferences = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = preferences.edit()
        val gson = Gson()
        val json = gson.toJson(callLogList)
        editor.putString("call_log", json)
        editor.commit()

    }

    //store data

    fun setLocalData(context: Context,getOffersArrayList: ArrayList<StoreContact>) {
        preferences = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = preferences.edit()
        editor.putString("offers", Gson().toJson(getOffersArrayList))
        editor.apply()
    }

    fun getLocalData(context: Context): ArrayList<StoreContact>? {
        preferences = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
        return Gson().fromJson<ArrayList<StoreContact>>(
            preferences.getString("offers", ""),
            object : TypeToken<ArrayList<StoreContact?>?>() {}.type
        )
    }


    //Store Number

    fun setPhoneNumber(context: Context,mobile_number : String){
        preferences = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = preferences.edit()
        editor.putString("mobile_number", mobile_number)
        editor.commit()
    }

    fun getPhoneNumber(context: Context) : String{
        preferences = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
        val mobile_number: String? = preferences.getString("mobile_number", "")
        return mobile_number.toString()
    }

    // Store Image

    fun setImageUser(context: Context,imageUser : String){
        preferences = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = preferences.edit()
        editor.putString("image_user", imageUser)
        editor.commit()
    }
    fun getImageUser(context: Context) : String{
        preferences = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
        val image_user: String? = preferences.getString("image_user", "")
        return image_user.toString()
    }


    //Store Name

    fun setPhoneName(context: Context,mobile_name : String){
        preferences = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = preferences.edit()
        editor.putString("mobile_name", mobile_name)
        editor.commit()
    }

    fun getPhoneName(context: Context) : String{
        preferences = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
        val mobile_name: String? = preferences.getString("mobile_name", "")
        return mobile_name.toString()
    }

    // Is Call Merged

    fun setIsCallMerged(context: Context,isCallMerge : String){
        preferences = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = preferences.edit()
        editor.putString("isCallMerge", isCallMerge)
        editor.commit()
    }

    fun getIsCallMerge(context: Context) : String{
        preferences = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
        val isCallMerge: String? = preferences.getString("isCallMerge", "0")
        return isCallMerge.toString()
    }

    fun setStoreContact(context: Context,storeContact: StoreContact){
        preferences = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = preferences.edit()
        editor.putString("storeContact", Gson().toJson(storeContact))
        editor.commit()
    }

    fun getStoreContact(context: Context): StoreContact{
        preferences = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
        val store: String? = preferences.getString("storeContact", "0")
        return Gson().fromJson(store,StoreContact::class.java)

    }


    //store data

    fun setMatchCallLogDetail(context: Context,getOffersArrayList: ArrayList<CallLogMatchDetail>) {
        preferences = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = preferences.edit()
        editor.putString("CallLogMatchDetail", Gson().toJson(getOffersArrayList))
        editor.apply()
    }

    fun getMatchCallLogDetail(context: Context): ArrayList<CallLogMatchDetail>? {
        preferences = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
        return Gson().fromJson<ArrayList<CallLogMatchDetail>>(
            preferences.getString("CallLogMatchDetail", ""),
            object : TypeToken<ArrayList<CallLogMatchDetail?>?>() {}.type
        )
    }


}