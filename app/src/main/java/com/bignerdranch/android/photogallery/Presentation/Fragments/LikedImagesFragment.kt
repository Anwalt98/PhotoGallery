package com.bignerdranch.android.photogallery.Presentation.Fragments

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bignerdranch.android.photogallery.Repository.ItemPhoto
import com.bignerdranch.android.photogallery.R
import com.bignerdranch.android.photogallery.Repository.ThumbnailDownloader
import com.bignerdranch.android.photogallery.viewModel.PhotoGalleryViewModel
import kotlin.properties.Delegates

private const val TAG = "PhotoGalleryFragment"
private const val ARG_PHOTO_URL = "photo_url"

class LikedImagesFragment : Fragment() {



    private lateinit var photoGalleryViewModel: PhotoGalleryViewModel
    private lateinit var photoRecyclerView: RecyclerView
    private lateinit var thumbnailDownloader: ThumbnailDownloader<PhotoHolder>

    override fun onCreate(
        savedInstanceState:
        Bundle?
    ) {
        super.onCreate(savedInstanceState)
        photoGalleryViewModel = ViewModelProvider(this)[PhotoGalleryViewModel::class.java]
        val responseHandler = Handler()

        thumbnailDownloader = ThumbnailDownloader(responseHandler) { photoHolder, bitmap ->
            val drawable = BitmapDrawable(resources, bitmap)
            photoHolder.bindDrawable(drawable)
        }
        lifecycle.addObserver(thumbnailDownloader.fragmentLifecycleObserver)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewLifecycleOwner.lifecycle.addObserver(thumbnailDownloader.viewLifecycleObserver)
        val view = inflater.inflate(R.layout.fragment_photo_gallery, container, false)
        photoRecyclerView = view.findViewById(R.id.photo_recycler_view)
        photoRecyclerView.layoutManager = GridLayoutManager(context, 3)
        return view
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(
            view,
            savedInstanceState
        )

       photoGalleryViewModel.itemImageLikedLiveData.observe(
            viewLifecycleOwner, Observer { galleryItems ->
                photoRecyclerView.adapter = PhotoAdapter(galleryItems)
            })
        }

    override fun onPause() {
        super.onPause()
        retainInstance = false
    }
    override fun onDestroy() {
        super.onDestroy()
        lifecycle.removeObserver(thumbnailDownloader.fragmentLifecycleObserver)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewLifecycleOwner.lifecycle.removeObserver(thumbnailDownloader.viewLifecycleObserver)
    }

    private inner class PhotoHolder(private val itemImageView: ImageView) : RecyclerView.ViewHolder(itemImageView),View.OnClickListener  {
        private lateinit var photo : ItemPhoto
        init {
            itemImageView.setOnClickListener(this)
        }
        fun bindDrawable(drawable: Drawable) {
            itemImageView.setImageDrawable(drawable)
        }
        fun setPhotoInHolder(itemPhoto: ItemPhoto){
            this.photo = itemPhoto
        }

        override fun onClick(v: View?) {
//            findNavController().clearBackStack(R.id.detailedImage)
            findNavController().navigate(R.id.detailedImage, bundleOf("KEY_DETAILED" to photo))
        }
    }

    private inner class PhotoAdapter(private val itemPhotos: List<ItemPhoto>) : RecyclerView.Adapter<PhotoHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoHolder {
            val view = layoutInflater.inflate(R.layout.list_item_gallery, parent, false) as ImageView
            return PhotoHolder(view)
        }
        override fun getItemCount(): Int = itemPhotos.size
        override fun onBindViewHolder(holder: PhotoHolder, position: Int) {
            val galleryItem = itemPhotos[position]
            val placeholder: Drawable = ContextCompat.getDrawable(requireContext(), R.drawable.default_img) ?: ColorDrawable()
            holder.bindDrawable(placeholder)
            holder.setPhotoInHolder(galleryItem)
            galleryItem.url?.let { thumbnailDownloader.queueThumbnail(holder, it) }
        }
    }
}
