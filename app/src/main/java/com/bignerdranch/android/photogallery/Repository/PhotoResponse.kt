package com.bignerdranch.android.photogallery.Repository

import com.google.gson.annotations.SerializedName

class PhotoResponse{
@SerializedName("photo")
lateinit var itemPhotos: List<ItemPhoto>
}