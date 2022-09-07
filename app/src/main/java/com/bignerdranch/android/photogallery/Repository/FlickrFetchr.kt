package com.bignerdranch.android.photogallery.Repository

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.bignerdranch.android.photogallery.Repository.api.FlickrApi
import com.bignerdranch.android.photogallery.di.AppComponent
import com.bignerdranch.android.photogallery.di.AppModule
import com.bignerdranch.android.photogallery.di.DaggerAppComponent
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


private const val TAG = "FlickrFetchr"
class FlickrFetchr  {

    private var component: AppComponent = DaggerAppComponent.builder().appModule(AppModule()).build()
    var flickrApi : FlickrApi = component.provideFlickrApi()




    fun fetchPhotos(): LiveData<List<ItemPhoto>> {
        val responseLiveData: MutableLiveData<List<ItemPhoto>> = MutableLiveData()
        val flickrRequest: Call<FlickrResponse> = flickrApi.fetchPhotos()
        flickrRequest.enqueue(object : Callback<FlickrResponse> {
            override fun onFailure(call: Call<FlickrResponse>, t: Throwable) {
                Log.e(TAG, "Failed to fetch photos", t)
            }
            override fun onResponse(call: Call<FlickrResponse>, response: Response<FlickrResponse>) {
                Log.d(TAG, "Response received")
                val flickrResponse: FlickrResponse? = response.body()
                val photoResponse: PhotoResponse? = flickrResponse?.photos
                var itemImages: List<ItemPhoto> = photoResponse?.itemPhotos ?: mutableListOf()
                itemImages =
                    itemImages.filterNot {
                        it.url.isBlank()
                    }
                responseLiveData.value = itemImages
            }
        })
        return responseLiveData
    }
    @WorkerThread
    fun fetchPhoto(url: String): Bitmap? {
        val response: Response<ResponseBody> = flickrApi.fetchUrlBytes(url).execute()
        val bitmap = response.body()?.byteStream()?.use(BitmapFactory::decodeStream)
        Log.i(TAG, "Decoded bitmap=$bitmap from Response=$response")
        return bitmap
    }

}