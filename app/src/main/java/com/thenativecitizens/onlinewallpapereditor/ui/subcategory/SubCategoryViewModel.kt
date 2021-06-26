package com.thenativecitizens.onlinewallpapereditor.ui.subcategory

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.thenativecitizens.onlinewallpapereditor.util.SubCategory
import com.thenativecitizens.onlinewallpapereditor.util.Wallpaper
import kotlinx.coroutines.*

class SubCategoryViewModel: ViewModel(){

    private var _listOfWallpapers = MutableLiveData<MutableList<Wallpaper>>()
    val listOfWallpapers: LiveData<MutableList<Wallpaper>> get() = _listOfWallpapers
    
    //Firebase Database references
    private var sbCategoryFirebaseDatabaseRef: DatabaseReference = Firebase.database.reference.child("subcategories")

    //CoroutineScope and Job
    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private var _isTwoSecsLoadingDone = MutableLiveData<Boolean>()
    val isTwoSecsLoadingDone: LiveData<Boolean> get() = _isTwoSecsLoadingDone


    init {
        _listOfWallpapers.value = mutableListOf()
        _isTwoSecsLoadingDone.value = false
    }

    fun fetchWallpapers(subCategoryName: String, categoryName: String){
        sbCategoryFirebaseDatabaseRef.child(categoryName).child(subCategoryName)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.getValue<SubCategory>()?.let { subCat ->
                        subCat.imageUrlList.mapIndexed { _, url ->
                            val list = _listOfWallpapers.value!!
                            list.add(Wallpaper(url, subCategoryName))
                            _listOfWallpapers.value = list.distinct().toMutableList()
                        }
                    }
                }
                override fun onCancelled(error: DatabaseError){}
            })
    }

    //called to begin a two seconds loading to allow
    //Glide load the Images, after the Urls has been fetched from the database
    fun beginTwoSecsLoading(){
        uiScope.launch {
            delay(2000) //delay for two seconds
            _isTwoSecsLoadingDone.value = true
        }
    }


    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}