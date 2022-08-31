package com.bignerdranch.android.photogallery

import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import java.util.*

private const val ARG_PHOTO_URL = "photo_url"
class DetailedImage : Fragment() {
    lateinit var drawable: BitmapDrawable
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
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        lifecycle.addObserver(thumbnailDownloader.fragmentLifecycleObserver)
        viewLifecycleOwner.lifecycle.addObserver(thumbnailDownloader.viewLifecycleObserver)
        thumbnailDownloader.queueThumbnail(this, photo.url)
       val view = inflater.inflate(R.layout.item_detailed, container, false)
        image = view.findViewById(R.id.detailed_image)
        image.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.default_img) ?: ColorDrawable())
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        image = view.findViewById(R.id.item_image)
//        text = view.findViewById(R.id.detailed_description)
//        image.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.default_img) ?: ColorDrawable())
    }
    fun bindDrawable(drawable: Drawable){
        image.setImageDrawable(drawable)
    }
companion object{
    fun newInstance(photo: GalleryItem) : DetailedImage{
                val args = Bundle().apply {
                    putSerializable(ARG_PHOTO_URL, photo)
                }
                return DetailedImage().apply {
                    arguments = args
                }
            }
        }
    }
