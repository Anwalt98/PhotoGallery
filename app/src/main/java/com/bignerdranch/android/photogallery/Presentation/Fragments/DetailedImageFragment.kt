package com.bignerdranch.android.photogallery.Presentation.Fragments

import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.*
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider

import com.bignerdranch.android.photogallery.FirebaseNotifications.NotificationData
import com.bignerdranch.android.photogallery.FirebaseNotifications.PushNotification
import com.bignerdranch.android.photogallery.FirebaseNotifications.RetrofitInstance

import com.bignerdranch.android.photogallery.R
import com.bignerdranch.android.photogallery.Repository.ItemPhoto
import com.bignerdranch.android.photogallery.Repository.ThumbnailDownloader
import com.bignerdranch.android.photogallery.viewModel.PhotoGalleryViewModel

import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import kotlinx.android.synthetic.main.item_detailed.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*




const val TOPIC = "/topics/myTopic"
const val TITLE : String = "TITLE"
const val MESAGE = "message"

class DetailedImageFragment : Fragment(R.layout.item_detailed) {

    private lateinit var listLikedPhotos: List<ItemPhoto>
    private val listURL: MutableList<String> = mutableListOf()
    private lateinit var thumbnailDownloader: ThumbnailDownloader<DetailedImageFragment>
    private lateinit var likeButton: ImageButton
    private lateinit var photo: ItemPhoto
    private lateinit var image: ImageView
    private lateinit var text: TextView


    private val photoGalleryViewModel: PhotoGalleryViewModel by lazy {
        ViewModelProvider(this).get(PhotoGalleryViewModel::class.java)

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        findNavController().clearBackStack(R.id.detailed_image)
        photo = arguments?.getParcelable<ItemPhoto>("KEY_DETAILED") as ItemPhoto
        val responseHandler = Handler()
        thumbnailDownloader = ThumbnailDownloader(responseHandler) { _, bitmap ->
            val drawable = BitmapDrawable(resources, bitmap)
            bindDrawable(drawable)
        }
        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC)
        PushNotification(NotificationData(TITLE, MESAGE), TOPIC).also { sendNotrification(it)  }
    }
    private fun sendNotrification(notification: PushNotification) =
        CoroutineScope(Dispatchers.IO).launch {
            try {

                val response = RetrofitInstance.api.postNotification(notification)
                if (response.isSuccessful){
                    Log.d("AAA", "Gson : ${Gson().toJson(response)}")
                }
                else{
                    Log.e("AAA", response.errorBody().toString())
                }
            }
            catch (e : Exception){

            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        lifecycle.addObserver(thumbnailDownloader.fragmentLifecycleObserver)
        viewLifecycleOwner.lifecycle.addObserver(thumbnailDownloader.viewLifecycleObserver)

        thumbnailDownloader.queueThumbnail(this, imageQualified(photo.url))
        val view = inflater.inflate(R.layout.item_detailed, container, false)
        likeButton = view.findViewById(R.id.like_button) as ImageButton
        image = view.findViewById(R.id.detailed_image)
        text = view.findViewById(R.id.detailed_description)

        image.setImageDrawable(
            ContextCompat.getDrawable(requireContext(), R.drawable.default_img) ?: ColorDrawable()
        )
        text.text = setDate(photo.date_upload)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val observer = Observer<List<ItemPhoto>> { listLiked ->
            listLikedPhotos = listLiked
//            Log.d("TAG", listLikedPhotos.toString())
            for (i in listLikedPhotos) {
                listURL.add(i.url)
            }
            if (photo.url in listURL){
                like_button.setBackgroundResource(R.drawable.button_liked)
            }
            else {
                like_button.setBackgroundResource(R.drawable.button_not_liked)
            }
        }
        photoGalleryViewModel.itemImageLikedLiveData.observe(viewLifecycleOwner, observer)

        likeButton.setOnClickListener {
            if (photo.url !in listURL) {
                GlobalScope.launch(Dispatchers.IO) {
                    photoGalleryViewModel.addItemPhoto(photo)
                    Log.d("TAG", "added")
                }
                Toast.makeText(
                    context,
                    "Photo ${photo.title} was successfully added",
                    Toast.LENGTH_SHORT
                ).show()
                like_button.setBackgroundResource(R.drawable.button_liked)
                photoGalleryViewModel.itemImageLikedLiveData.observe(viewLifecycleOwner, observer)
            } else {
                Toast.makeText(
                    context,
                    "Photo ${photo.title} was deleted",
                    Toast.LENGTH_SHORT
                ).show()
                like_button.setBackgroundResource(R.drawable.button_not_liked)
                GlobalScope.launch(Dispatchers.IO) {
                    photoGalleryViewModel.deleteItemPhoto(photo)
                    Log.d("TAG", "added")
                }
                photoGalleryViewModel.itemImageLikedLiveData.observe(viewLifecycleOwner, observer)
            }
        }
    }

    private fun bindDrawable(drawable: Drawable) {
        image.setImageDrawable(drawable)
    }

    private fun setDate(date: Date): String {
        val pattern = "yyyy-MM-dd"
        val simpleDateFormat = SimpleDateFormat(pattern)
        return simpleDateFormat.format(date)
    }

    fun imageQualified(string: String): String {
        return string.replace("m.jpg", "b.jpg")
    }
}


