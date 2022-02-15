package com.ringer.interactive.api

import com.google.gson.JsonObject
import com.ringer.interactive.model.CallLogMatchDetail
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.http.*

interface Api {

    //Token
    @GET(token_with_Categories)
    fun getTokenWithAuth(
        @Header(authorization) auth : String,
        @Query(username) user_name : String,
        @Query(password) pass_word : String
    ) : Call<JsonObject>

    //Search Contact
    @GET(search_contact)
    fun searchContact(
        @Header(authorization) auth: String
    ) : Call<JsonObject>

    //Get Avatar
    @GET("data/api/types/gallery/{galleryId}/avatar")
    fun getAvatar(
        @Header(authorization) auth: String,
        @Path("galleryId") contact_id : String,
        @Query("phone") phone_number : String,
        @Query("firstName") fname : String,
        @Query("lastName") lname : String,
        @Query("contactId") cid : String,
    ) : Call<ResponseBody>


    //send fcm token
    @POST(create_contact)
    fun sendFCMToken(
        @Header(authorization) auth : String,
        @Body jsonObject: JsonObject,
    ) : Call<JsonObject>

    //mobileCAlls
    @POST(mobile_calls)
    fun sendMobileCallLog(
        @Header(authorization) auth: String,
        @Body list: ArrayList<CallLogMatchDetail>
    ) : Call<JsonObject>

}