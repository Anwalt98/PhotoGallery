package com.bignerdranch.android.photogallery

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class GalleryItem(
    var title: String = "",
    var id: String = "",
    @SerializedName("url_s")
    var url: String = ""
) : Serializable