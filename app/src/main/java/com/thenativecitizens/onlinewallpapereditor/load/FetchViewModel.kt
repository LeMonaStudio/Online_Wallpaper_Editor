package com.thenativecitizens.onlinewallpapereditor.load

import android.Manifest
import android.app.Application
import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.core.content.ContextCompat
import androidx.lifecycle.*
import kotlinx.coroutines.*

class FetchViewModel(application: Application) : AndroidViewModel(application){

    //Coroutine
    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    //Observable for device Images
    private var _deviceImageList = MutableLiveData<MutableList<Image>>()
    val deviceImageList: LiveData<MutableList<Image>> get() = _deviceImageList

    //Observable for Firebase Images
    private var _onlineImageList = MutableLiveData<MutableList<Image>>()
    val onlineImageList: LiveData<MutableList<Image>> get() = _onlineImageList

    //first image on the list
    private var firstImage: Image? = null


    init {
        _deviceImageList.value = mutableListOf()
        _onlineImageList.value = mutableListOf()
    }


    //Called to fetch images from the user's device and display
    fun fetchDeviceImages(){
        //get the collection of images on the device internal memory
        val imageCollections =
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
                MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
            } else{
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            }
        //set the projection i.e what we would be getting from each image file in the collection
        val projection = arrayOf(MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME)

        fetchImagesFromDevice(imageCollections, projection)
    }

    //Using Coroutine to fetch the Images from the device
    private fun fetchImagesFromDevice(
        imageCollections: Uri, projection: Array<String>) {
        uiScope.launch {
            _deviceImageList.value = backgroundFetch(imageCollections, projection)
        }
    }
    //the suspend function that fetches the images from the device in the background
    private suspend fun backgroundFetch(imageCollections: Uri, projection: Array<String>): MutableList<Image> {
        return withContext(Dispatchers.IO){
            //the mutable list of images to be returned by the Coroutine's suspend function
            val imageList: MutableList<Image> = mutableListOf()
            //context and the content resolver
            val context: Context = getApplication()
            val resolver: ContentResolver = context.contentResolver
            //sortOrder for the Images fetched
            val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"
            //the query to fetch the images
            val query = resolver.query(imageCollections, projection, null, null, sortOrder)
            query?.use { cursor ->
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)

                while (cursor.moveToNext()){
                    //Get the value of columns for a given image
                    val id = cursor.getLong(idColumn)
                    val name = cursor.getString(nameColumn)
                    //the Uri of the Image
                    val contentUri: Uri = ContentUris.withAppendedId(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        id
                    )
                    imageList.add(Image(contentUri, name))
                }
            }
            firstImage = imageList[0]

            imageList
        }
    }

    //Empty the loaded list of device Images
    fun emptyLoadedDeviceImageList(){
        //empty list of images
        val imageList: MutableList<Image> = mutableListOf()
        _deviceImageList.value = imageList
    }


    //Check for if permission is granted
    fun checkMediaAccessPermissionsGranted(): Boolean{
        //read external storage permission
        return  ContextCompat.checkSelfPermission(getApplication(),
            Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
    }




    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}






//ViewModelFactory
class FetchViewModelFactory(private val application: Application): ViewModelProvider.Factory {

    @Suppress("unchecked cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FetchViewModel::class.java)) {
            return FetchViewModel(application) as T
        }

        throw IllegalArgumentException("Unknown viewModel Class")
    }
}