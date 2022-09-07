package com.bignerdranch.android.photogallery.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.bignerdranch.android.photogallery.Repository.ItemPhoto

@Database(entities = [ItemPhoto::class],
    version=1)
@TypeConverters(ItemPhotoTypeConverters::class)
abstract class ItemPhotoDatabase : RoomDatabase() {
    abstract fun ItemPhotoDao(): ItemPhotoDao
}



