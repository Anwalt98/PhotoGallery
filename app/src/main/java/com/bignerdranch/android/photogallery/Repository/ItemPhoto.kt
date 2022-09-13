package com.bignerdranch.android.photogallery.Repository

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.io.Serializable
import java.util.*


@Entity
data class ItemPhoto(

    @PrimaryKey
    @SerializedName("id")
    var id: String = "",

    @SerializedName("title")
    var title: String = "",

    @SerializedName("url_s")
    var url: String = "",

    @SerializedName("description")
    var description: String = "",

    @SerializedName("date_upload")
    var date_upload : Date = Date()

) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        Date(parcel.readLong())
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(title)
        parcel.writeString(url)
        parcel.writeString(description)
        parcel.writeLong(date_upload.time)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ItemPhoto> {
        override fun createFromParcel(parcel: Parcel): ItemPhoto {
            return ItemPhoto(parcel)
        }

        override fun newArray(size: Int): Array<ItemPhoto?> {
            return arrayOfNulls(size)
        }
    }
}