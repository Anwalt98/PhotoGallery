package com.bignerdranch.android.photogallery.Presentation.Fragments

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.transition.TransitionInflater
import android.util.Log
import android.view.*
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.resources.Compatibility.Api18Impl.setAutoCancel
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.graphics.drawable.toDrawable
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
import kotlin.properties.Delegates


const val TOPIC = "/topics/myTopic"
const val TITLE : String = "TITLE"
const val MESAGE = "message"

class DetailedImageFragment : Fragment(R.layout.item_detailed) {

    private lateinit var listLikedPhotos: List<ItemPhoto>
    private val listURL: MutableList<String> = mutableListOf()
    private lateinit var thumbnailDownloader: ThumbnailDownloader<DetailedImageFragment>
    private lateinit var likeButton: ImageButton
    private lateinit var photo: ItemPhoto
    private lateinit var bitmap: Bitmap
    private lateinit var image: ImageView
    private lateinit var text: TextView


    private val photoGalleryViewModel: PhotoGalleryViewModel by lazy {
        ViewModelProvider(this)[PhotoGalleryViewModel::class.java]

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val animation = TransitionInflater.from(requireContext()).inflateTransition(
            android.R.transition.move
        )
        photo = arguments?.getParcelable<ItemPhoto>("KEY_DETAILED") as ItemPhoto

       if (requireArguments().containsKey("BITMAP")) {
           bitmap = arguments?.getParcelable<Bitmap>("BITMAP") as Bitmap
       }
        val responseHandler = Handler()
        thumbnailDownloader = ThumbnailDownloader(responseHandler) { _, bitmap ->
            val drawable = BitmapDrawable(resources, bitmap)
            bindDrawable(drawable)
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
            if (this::bitmap.isInitialized) {
            bitmap.toDrawable(resources)
        }
            else {
               ContextCompat.getDrawable(requireContext(), R.drawable.default_img) ?: ColorDrawable()
           }
        )
        text.text = setDate(photo.date_upload)
        return view
    }
private var isInLiked by Delegates.notNull<Boolean>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val observer = Observer<List<ItemPhoto>> { listLiked ->
            isInLiked = photo in listLiked
            if (isInLiked) {
                like_button.setBackgroundResource(R.drawable.button_liked)
            } else {
                like_button.setBackgroundResource(R.drawable.button_not_liked)
            }
        }
        photoGalleryViewModel.itemImageLikedLiveData.observe(viewLifecycleOwner, observer)



        likeButton.setOnClickListener {
        like_button.animate().apply {
            duration = 250
            scaleX(1.5f)
            scaleY(1.5f)
        }.withEndAction {
            like_button.animate().apply {
                duration = 250
                scaleX(1f)
                scaleY(1f)
            }
        }
            if (!isInLiked) {
                GlobalScope.launch(Dispatchers.IO) { photoGalleryViewModel.addItemPhoto(photo)
                }
                Toast.makeText(context, "Photo ${photo.title} was successfully added", Toast.LENGTH_SHORT).show()
                Log.d("Img", "${photo.url} added")
                like_button.setBackgroundResource(R.drawable.button_liked)

            }
            else {
                GlobalScope.launch(Dispatchers.IO) {
                    photoGalleryViewModel.deleteItemPhoto(photo)
                }
                Toast.makeText(context, "Photo ${photo.title} was deleted", Toast.LENGTH_SHORT).show()
                like_button.setBackgroundResource(R.drawable.button_not_liked)
                Log.d("Img", "${photo.url} deleted")
//                 photoGalleryViewModel.itemImageLikedLiveData.observe(viewLifecycleOwner, observer)
            }
            makeNotification()
        }
    }


    private fun makeNotification(){
        val notification = NotificationCompat.Builder(activity as Context)
            .setSmallIcon(R.drawable.like_button)
            .setContentText("Text")
            .setContentTitle("Title").build()
        val notificationManager = requireActivity().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(1,notification)
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



