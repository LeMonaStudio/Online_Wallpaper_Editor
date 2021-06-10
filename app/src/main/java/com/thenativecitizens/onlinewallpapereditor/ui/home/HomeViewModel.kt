package com.thenativecitizens.onlinewallpapereditor.ui.home

import android.app.Application
import androidx.lifecycle.*
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.thenativecitizens.onlinewallpapereditor.util.Category
import kotlinx.coroutines.*

class HomeViewModel(application: Application) : AndroidViewModel(application){

    //Firebase Database references
    private var categoryFirebaseDatabaseRef: DatabaseReference = Firebase.database.reference.child("categories")

    //Observable for Firebase Images
    private var _wallpaperCategoryList = MutableLiveData<MutableList<Category>>()
    val wallpaperCategoryList: LiveData<MutableList<Category>> get() = _wallpaperCategoryList

    //Holds the data snapshot keys to be able to update the list of categories on childChanged
    private var snapShotKeys: MutableList<String> = mutableListOf()

    //CoroutineScope and Job
    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private var _isTwoSecsLoadingDone = MutableLiveData<Boolean>()
    val isTwoSecsLoadingDone: LiveData<Boolean> get() = _isTwoSecsLoadingDone


    init {
        _wallpaperCategoryList.value = mutableListOf()
        _isTwoSecsLoadingDone.value = false
        fetchWallpaperCategories()
    }


    //fetches the categories from database
    private fun fetchWallpaperCategories() {
        categoryFirebaseDatabaseRef.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val list = _wallpaperCategoryList.value!!
                snapshot.getValue<Category>()?.let {
                    list.add(it)
                    _wallpaperCategoryList.value = list
                    snapShotKeys.add(snapshot.key!!)
                }
            }
            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val list = _wallpaperCategoryList.value!!
                val modifiedCategory = snapshot.getValue<Category>()
                val key = snapshot.key
                modifiedCategory?.let {newCat ->
                    val alteredIndex = snapShotKeys.indexOf(key)
                    list[alteredIndex] = newCat
                    _wallpaperCategoryList.value = list
                }
            }
            override fun onChildRemoved(snapshot: DataSnapshot) {
                val list = _wallpaperCategoryList.value!!
                val removedCategory = snapshot.getValue<Category>()
                val deletedKey = snapshot.key

                removedCategory?.let {
                    val index = snapShotKeys.indexOf(deletedKey)
                    list.removeAt(index)
                    _wallpaperCategoryList.value = list
                    snapShotKeys.removeAt(index)
                }
            }
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {}
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