package com.bignerdranch.android.photogallery.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.bignerdranch.android.photogallery.Repository.ItemPhoto

import java.util.*

@Dao
interface ItemPhotoDao {
    @Query("SELECT * FROM ItemPhoto")
    fun getItemPhotos(): LiveData<List<ItemPhoto>>
    @Query("SELECT * FROM ItemPhoto WHERE id=(:id)")
    fun getItemPhoto(id: String): LiveData<ItemPhoto?>
    @Query("DELETE FROM ItemPhoto WHERE id= :id")
    fun deleteById(id: String)
    @Update
    fun updateItemPhoto(itemPhoto: ItemPhoto)
    @Insert
    fun addItemPhoto(itemPhoto: ItemPhoto)
}