package com.bignerdranch.android.photogallery.Repository

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.util.*


@Entity
data class ItemPhoto(

    @PrimaryKey
    @SerializedName("id")
    var id: String = "",

    @SerializedName("title")
    var title: String = "",

    @SerializedName("url_s")
    var url: String = "",

    @SerializedName("description")
    var description: String = "",

    @SerializedName("date_upload")
    var date_upload : Date = Date()

) : Serializable