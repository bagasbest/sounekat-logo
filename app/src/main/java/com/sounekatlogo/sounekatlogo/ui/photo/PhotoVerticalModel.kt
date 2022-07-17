package com.sounekatlogo.sounekatlogo.ui.photo

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PhotoVerticalModel(
    var uid : String ? = null,
    var name : String ? = null,
    var description : String ? = null,
    var imageSize : String ? = null,
    var image : String ? = null,
) : Parcelable