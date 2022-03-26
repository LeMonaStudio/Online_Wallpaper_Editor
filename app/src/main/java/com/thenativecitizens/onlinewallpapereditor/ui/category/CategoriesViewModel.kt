package com.thenativecitizens.onlinewallpapereditor.ui.category

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
import com.thenativecitizens.onlinewallpapereditor.model.Category
import com.thenativecitizens.onlinewallpapereditor.model.SubCategory
import kotlinx.coroutines.*

class CategoriesViewModel : ViewModel(){

    //Firebase Database references
    private var sbCategoryFirebaseDatabaseRef: DatabaseReference = Firebase.database.reference.child("subcategories")

    //Firebase Database references
    private var categoryFirebaseDatabaseRef: DatabaseReference = Firebase.database.reference.child("categories")

    //the SubCategories for the current Category
    private var _subCategoryList = MutableLiveData<MutableList<SubCategory>>()
    val subCategoryList: LiveData<MutableList<SubCategory>> get() = _subCategoryList

    //CoroutineScope and Job
    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private var _isTwoSecsLoadingDone = MutableLiveData<Boolean>()
    val isTwoSecsLoadingDone: LiveData<Boolean> get() = _isTwoSecsLoadingDone

    
    init {
        _subCategoryList.value = mutableListOf()
        _isTwoSecsLoadingDone.value = false
    }

    //fetch the sub categories for the selected Category
    fun fetchSubCategoriesFor(categoryName: String){
        val listOfSubCategories: MutableList<SubCategory> = mutableListOf()
        categoryFirebaseDatabaseRef.child(categoryName).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.getValue<Category>()?.let {
                    it.subCategoryList.forEach { subCategoryName ->
                        sbCategoryFirebaseDatabaseRef.child(categoryName).child(subCategoryName)
                            .addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    snapshot.getValue<SubCategory>()?.let { subCat ->
                                        listOfSubCategories.add(subCat)
                                        _subCategoryList.value = listOfSubCategories
                                    }
                                }
                                override fun onCancelled(error: DatabaseError){}
                        })
                    }
                }
            }
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
