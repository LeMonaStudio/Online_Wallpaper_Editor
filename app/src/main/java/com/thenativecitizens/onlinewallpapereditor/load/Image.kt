package com.thenativecitizens.onlinewallpapereditor.load

import android.net.Uri
import android.os.Parcel
import android.os.Parcelable


data class Image(
    var uri: Uri?,
    var imageName: String? = ""
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readParcelable(Uri::class.java.classLoader),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(uri, flags)
        parcel.writeString(imageName)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Image> {
        override fun createFromParcel(parcel: Parcel): Image {
            return Image(parcel)
        }

        override fun newArray(size: Int): Array<Image?> {
            return arrayOfNulls(size)
        }
    }
}