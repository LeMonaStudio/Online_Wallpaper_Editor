package com.thenativecitizens.onlinewallpapereditor.util

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.*

class DelayUtil : ViewModel(){

    //CoroutineScope and Job
    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    //observed to know when delay is finished
    private var _isDelayFinished = MutableLiveData<Boolean>()
    val isDelayFinished: LiveData<Boolean> get() = _isDelayFinished

    init {
        _isDelayFinished.value = false
    }


    //called to begin the delay
    fun beginDelay(time: Long){
        uiScope.launch {
            delay(time)
            _isDelayFinished.value = true
        }
    }


    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}