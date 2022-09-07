package com.bignerdranch.android.photogallery.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.bignerdranch.android.photogallery.Repository.FlickrFetchr
import com.bignerdranch.android.photogallery.Repository.ItemPhoto
import com.bignerdranch.android.photogallery.Repository.ItemPhotoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class PhotoGalleryViewModel : ViewModel() {
    val itemImageLiveData: LiveData<List<ItemPhoto>> = FlickrFetchr().fetchPhotos()

    private val itemPhotoRepository = ItemPhotoRepository.get()
    val itemImageLikedLiveData : LiveData<List<ItemPhoto>> = itemPhotoRepository.getItemPhotos()

   fun addItemPhoto(itemPhoto : ItemPhoto) {
          itemPhotoRepository.addItemPhoto(itemPhoto)
  }
    fun getItemPhoto(id : String) : LiveData<ItemPhoto?>{
      return  itemPhotoRepository.getItemPhoto(id)
    }

}