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


    init {

    }


    fun checkWritePermission(): Boolean{
        return if (Build.VERSION.SDK_INT >= 29) //Due to scoped storage activated in API Level 29 above
            true
        else
            (ContextCompat.checkSelfPermission(getApplication(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED)
    }
}




//ViewModelFactory
class EditViewModelFactory(private val application: Application): ViewModelProvider.Factory {

    @Suppress("unchecked cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EditViewModel::class.java)) {
            return EditViewModel(application) as T
        }

        throw IllegalArgumentException("Unknown viewModel Class")
    }
}