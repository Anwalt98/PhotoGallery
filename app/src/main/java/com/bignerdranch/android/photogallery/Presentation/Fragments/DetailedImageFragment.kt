package com.bignerdranch.android.photogallery.Presentation.Fragments

import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bignerdranch.android.photogallery.Repository.FlickrFetchr
import com.bignerdranch.android.photogallery.R
import com.bignerdranch.android.photogallery.Repository.GalleryItem
import com.bignerdranch.android.photogallery.Repository.ThumbnailDownloader
import java.text.SimpleDateFormat


private const val ARG_PHOTO_URL = "photo_url"
class DetailedImage : Fragment() {

    private lateinit var thumbnailDownloader: ThumbnailDownloader<DetailedImage>
    private lateinit var photo : GalleryItem
    private lateinit var image : ImageView
    private lateinit var text : TextView
    private val flickrFetchr = FlickrFetchr()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        photo = arguments?.getSerializable(ARG_PHOTO_URL) as GalleryItem
        val responseHandler = Handler()
        thumbnailDownloader = ThumbnailDownloader(responseHandler) { _, bitmap ->
            val drawable = BitmapDrawable(resources, bitmap)
            bindDrawable(drawable)
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup? ,
        savedInstanceState: Bundle?
    ): View? {
        lifecycle.addObserver(thumbnailDownloader.fragmentLifecycleObserver)
        viewLifecycleOwner.lifecycle.addObserver(thumbnailDownloader.viewLifecycleObserver)
        val url2 = photo.url.replace("m.jpg","b.jpg")
        thumbnailDownloader.queueThumbnail(this, url2)
        val view = inflater.inflate(R.layout.item_detailed, container, false)
        image = view.findViewById(R.id.detailed_image)
        text = view.findViewById(R.id.detailed_description)
        image.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.default_img) ?: ColorDrawable())
        val pattern = "yyyy-MM-dd"
        val simpleDateFormat = SimpleDateFormat(pattern)
        val date: String = simpleDateFormat.format(photo.date_upload)
        text.text = date
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
//    fun getBitmapFromResources(resources: Resources?, drawable: Drawable): Bitmap? {
//        val options = BitmapFactory.Options()
//        options.inJustDecodeBounds = false
//        options.inDither = false
//        options.inSampleSize = 1
//        options.inScaled = false
//        options.inPreferredConfig = Bitmap.Config.ARGB_8888
//        return BitmapFactory.decodeResource(resources, drawable.toString().toInt(), options)
//    }
    private fun bindDrawable(drawable: Drawable){
        image.setImageDrawable(drawable)
    }
companion object{
    fun newInstance(itemImage: GalleryItem) : DetailedImage{
                val args = Bundle().apply {
                    putSerializable(ARG_PHOTO_URL, itemImage)
                }
                return DetailedImage().apply {
                    arguments = args
                }
            }
        }
    }
