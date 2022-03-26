package com.thenativecitizens.onlinewallpapereditor.model

import android.net.Uri

data class DeviceImage(
    var contentUri: Uri,
    var imageName: String
)