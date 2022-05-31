package com.ringer.interactive.api

import android.content.Context
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class Connection {

    fun getCon(context: Context,baseURL : String): Api {


        val interceptor = HttpLoggingInterceptor()
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        val okHttpClient = OkHttpClient().newBuilder()
            .connectTimeout(30000, TimeUnit.SECONDS)
            .readTimeout(3000, TimeUnit.SECONDS)
            .addInterceptor(interceptor)
            .writeTimeout(3000, TimeUnit.SECONDS)
            .build()

        val gson = GsonBuilder().setLenient().create()
        val retrofit = Retrofit.Builder().baseUrl(baseURL)
            .addConverterFactory(GsonConverterFactory.create(gson)).client(okHttpClient).build()
        return retrofit.create(Api::class.java)
    }


}

