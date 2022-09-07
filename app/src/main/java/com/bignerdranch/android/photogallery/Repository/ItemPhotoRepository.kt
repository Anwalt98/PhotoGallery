package com.bignerdranch.android.photogallery.Repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Room
import com.bignerdranch.android.photogallery.database.ItemPhotoDao
import com.bignerdranch.android.photogallery.database.ItemPhotoDatabase
import kotlinx.coroutines.*


import java.util.*
import java.util.concurrent.Executors

private const val DATABASE_NAME = "ItemPhoto-database"
class ItemPhotoRepository private constructor(context: Context) {
    private val executor = Executors.newSingleThreadExecutor()

    private val database : ItemPhotoDatabase =
        Room.databaseBuilder(
            context.applicationContext,
            ItemPhotoDatabase::class.java,
            DATABASE_NAME
        ).build()
    private val itemPhotoDao = database.ItemPhotoDao()

    fun getItemPhotos(): LiveData<List<ItemPhoto>> =
        itemPhotoDao.getItemPhotos()

    fun getItemPhoto(id: String): LiveData<ItemPhoto?> =
        itemPhotoDao.getItemPhoto(id)

    fun deleteItemPhoto(id: String) {
        executor.execute {
            itemPhotoDao.deleteById(id)
        }
    }

    fun updateItemPhoto(crime: ItemPhoto) {
        executor.execute {
            itemPhotoDao.updateItemPhoto(crime)
        }
    }
    fun addItemPhoto(itemPhoto: ItemPhoto) {
           itemPhotoDao.addItemPhoto(itemPhoto)
    }

    companion object {
        private var INSTANCE: ItemPhotoRepository? = null
        fun initialize(context: Context) {
            if (INSTANCE == null) {
                INSTANCE = ItemPhotoRepository(context)
            }
        }
        fun get(): ItemPhotoRepository {
            return INSTANCE ?:
            throw IllegalStateException("ItemPhotoRepository must be initialized")
        }
    }
}