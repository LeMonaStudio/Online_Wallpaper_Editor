package com.thenativecitizens.onlinewallpapereditor.ui.preview

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel

class PreviewViewModel(application: Application) : AndroidViewModel(application) {

    //needed to check for permissions if it has been granted or not
    fun checkMediaPermissionsGranted(): List<String>{
        val listOfRequiredPermissions: MutableList<String> = mutableListOf()
        //read external storage permission
        if (ContextCompat.checkSelfPermission(getApplication(), Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) listOfRequiredPermissions.add(Manifest.permission.READ_EXTERNAL_STORAGE)

        //write external storage permission
        if (ContextCompat.checkSelfPermission(getApplication(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) listOfRequiredPermissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)

        //Android 10 and above do not require write permissions for MediaStore Api due to Scoped Storage
        if (Build.VERSION.SDK_INT >= 29) listOfRequiredPermissions.remove(Manifest.permission.WRITE_EXTERNAL_STORAGE)

        return  listOfRequiredPermissions
    }
}