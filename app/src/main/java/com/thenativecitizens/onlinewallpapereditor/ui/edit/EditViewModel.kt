package com.thenativecitizens.onlinewallpapereditor.ui.edit

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class EditViewModel(application: Application) : AndroidViewModel(application) {

    fun checkWritePermission(): Boolean{
        return if (Build.VERSION.SDK_INT >= 29) //Due to scoped storage activated in API Level 29 above
            true
        else
            (ContextCompat.checkSelfPermission(getApplication(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED)
    }
}