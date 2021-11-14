package com.ringer.interactive.api

import com.google.gson.JsonObject
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
    @GET("data/api/types/contact/{contactId}/avatar")
    fun getAvatar(
        @Header(authorization) auth: String,
        @Path("contactId") contact_id : String
    ) : Call<ResponseBody>


    //send fcm token
    @FormUrlEncoded
    @POST(create_contact)
    fun sendFCMToken(
        @Field(firebaseToken) fcm_token : String
    ) : Call<JsonObject>



}