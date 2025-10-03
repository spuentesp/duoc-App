package com.example.app.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {
    private val _screenTitle = MutableLiveData("Pantalla Principal")
    val screenTitle: LiveData<String> = _screenTitle

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    fun updateTitle(title: String) {
        _screenTitle.value = title
    }

    fun setLoading(loading: Boolean) {
        _isLoading.value = loading
    }
}