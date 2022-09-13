package com.bignerdranch.android.photogallery.Presentation.Fragments.Activity

import android.app.Application
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.bignerdranch.android.photogallery.FirebaseNotifications.NotificationData
import com.bignerdranch.android.photogallery.FirebaseNotifications.PushNotification
import com.bignerdranch.android.photogallery.FirebaseNotifications.RetrofitInstance


import com.bignerdranch.android.photogallery.R

import com.bignerdranch.android.photogallery.databinding.ActivityPhotoGalleryBinding


import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import kotlinx.coroutines.*

val TAG = "Corut"

class PhotoGalleryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPhotoGalleryBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPhotoGalleryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val navView: BottomNavigationView = binding.bottomNavView
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        val appBarConfiguration = AppBarConfiguration(setOf(R.id.navigation_gallery, R.id.navigation_liked))
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

    }

    private fun sendNotrification(notification: PushNotification) =
        CoroutineScope(Dispatchers.IO).launch {
            try {

                val response = RetrofitInstance.api.postNotification(notification)
                if (response.isSuccessful){
                Log.d("Response", "Gson : ${Gson().toJson(response)}")
                }
                else{
                    Log.e("Response", response.errorBody().toString())
                }
            }
            catch (e : Exception){

            }
        }


}