package com.example.app.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HomeViewModel : ViewModel() {
    private val _welcomeMessage = MutableLiveData("¡Bienvenido a nuestra aplicación!")
    val welcomeMessage: LiveData<String> = _welcomeMessage

    private val _buttonClickCount = MutableLiveData(0)
    val buttonClickCount: LiveData<Int> = _buttonClickCount

    fun updateWelcomeMessage(message: String) {
        _welcomeMessage.value = message
    }

    fun incrementButtonClick() {
        _buttonClickCount.value = (_buttonClickCount.value ?: 0) + 1
    }
}