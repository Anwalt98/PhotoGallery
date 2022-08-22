package com.bignerdranch.android.photogallery.api

import com.bignerdranch.android.photogallery.FlickrResponse
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Url

interface FlickrApi {
    @GET(
        "services/rest/?method=flickr.interestingness.getList" +
        "&api_key=a00fd9ffc70fe616e91424eb98512999" +
                "&format=json" +
                "&nojsoncallback=1" +
                "&extras=url_s"
    )
    fun fetchPhotos(): Call<FlickrResponse>
    @GET
    fun fetchUrlBytes(@Url url: String): Call<ResponseBody>
}