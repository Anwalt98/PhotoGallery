package com.bignerdranch.android.photogallery.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.bignerdranch.android.photogallery.Repository.FlickrFetchr
import com.bignerdranch.android.photogallery.Repository.GalleryItem


class PhotoGalleryViewModel : ViewModel() {
    val itemImageLiveData: LiveData<List<GalleryItem>> = FlickrFetchr().fetchPhotos()

}