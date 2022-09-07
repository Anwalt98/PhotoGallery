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
import com.bignerdranch.android.photogallery.R
import com.bignerdranch.android.photogallery.Repository.ItemPhoto
import com.bignerdranch.android.photogallery.Repository.ThumbnailDownloader
import com.bignerdranch.android.photogallery.viewModel.PhotoGalleryViewModel
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.item_detailed.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


private const val ARG_PHOTO_URL = "photo_url"
class DetailedImage : Fragment() {

    private lateinit var listLikedPhotos: List<ItemPhoto>
    private lateinit var thumbnailDownloader: ThumbnailDownloader<DetailedImage>
    private lateinit var likeButton: ImageButton
    var listURL: MutableList<String> = mutableListOf()
    private var callbacks: Callbacks? = null
    private lateinit var photo: ItemPhoto
    private lateinit var image: ImageView
    private lateinit var text: TextView
    private lateinit var bottomMenu : BottomNavigationView
    private val photoGalleryViewModel: PhotoGalleryViewModel by lazy {
        ViewModelProvider(this).get(PhotoGalleryViewModel::class.java)

    }

    interface Callbacks {
        fun onLikedSelected()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        photo = arguments?.getSerializable(ARG_PHOTO_URL) as ItemPhoto
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
        bottomMenu = view.findViewById(R.id.bottom_navigation) as BottomNavigationView
//        inflater.inflate(R.menu.bottom_navigation_menu, bottomMenu)
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
            Log.d("TAG", listLikedPhotos.toString())
            for (i in listLikedPhotos) {
                listURL.add(i.url)
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
            } else {
                Toast.makeText(
                    context,
                    "Photo ${photo.title} was already added",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        bottomMenu.setOnNavigationItemSelectedListener{ item ->
//            when (item.itemId) {
//                R.id.liked -> {
                    callbacks?.onLikedSelected()
                    Log.d("TAG", "pressed liked")
                    true
//                }
//                else -> false
//            }
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

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbacks = context as Callbacks?
    }

    override fun onDetach() {
        super.onDetach()
        callbacks = null
    }

    companion object {
        fun newInstance(itemImage: ItemPhoto): DetailedImage {
            val args = Bundle().apply {
                putSerializable(ARG_PHOTO_URL, itemImage)
            }
            return DetailedImage().apply {
                arguments = args
            }
        }
    }

//    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
//        super.onCreateOptionsMenu(menu, inflater)
//        inflater.inflate(R.menu.bottom_navigation_menu, bottomMenu)
//    }

//    override fun onOptionsItemSelected(item: MenuItem) : Boolean {
//        when (item.itemId) {
//            R.id.liked -> {
//                callbacks?.onLikedSelected()
//            Log.d("TAG", "pressed liked")
//            }
//        }
//      return  true}
}


