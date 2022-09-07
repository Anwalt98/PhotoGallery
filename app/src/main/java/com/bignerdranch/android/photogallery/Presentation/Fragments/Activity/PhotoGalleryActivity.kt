package com.bignerdranch.android.photogallery.Presentation.Fragments.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bignerdranch.android.photogallery.Presentation.Fragments.DetailedImage
import com.bignerdranch.android.photogallery.Presentation.Fragments.PhotoGalleryFragment

import com.bignerdranch.android.photogallery.R
import com.bignerdranch.android.photogallery.Repository.ItemPhoto


class PhotoGalleryActivity : AppCompatActivity(), PhotoGalleryFragment.Callbacks,DetailedImage.Callbacks {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_gallery)
        val isFragmentContainerEmpty = savedInstanceState == null
        if (isFragmentContainerEmpty){
            supportFragmentManager
                .beginTransaction()
                .add(R.id.fragmentContainer,PhotoGalleryFragment.newInstance()).commit()
        }
    }

    override fun onCrimeSelected(itemImage: ItemPhoto) {
        val fragment = DetailedImage.newInstance(itemImage)
        supportFragmentManager.beginTransaction().replace(R.id.fragmentContainer, fragment).addToBackStack(null).commit()
    }

    override fun onLikedSelected() {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragmentContainer,PhotoGalleryFragment.newInstance(true)).addToBackStack(null).commit()
    }
}