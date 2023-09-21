package com.timi.seulseul.data.model

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("userId")
    val userId : String,
    @SerializedName("id")
    val id : String,
    @SerializedName("title")
    val title : String,
    @SerializedName("body")
    val body : String,
)