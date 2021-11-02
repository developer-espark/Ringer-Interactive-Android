package com.ringer.interactive.api

import com.google.gson.JsonObject
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

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



}