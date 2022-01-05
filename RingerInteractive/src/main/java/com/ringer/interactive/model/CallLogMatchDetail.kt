package com.ringer.interactive.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class CallLogMatchDetail {

    @SerializedName("fromAddress")
    @Expose
    var fromAddress : String = ""
    @SerializedName("toAddress")
    @Expose
    var toAddress : String = ""
    @SerializedName("callType")
    @Expose
    var callType : String = ""
    @SerializedName("duration")
    @Expose
    var duration : String = ""
    @SerializedName("createdAt")
    @Expose
    var createdAt : String = ""
}