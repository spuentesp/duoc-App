package com.example.app.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

data class UserProfile(
    val name: String,
    val email: String,
    val isVerified: Boolean
)

class ProfileViewModel : ViewModel() {
    private val _userProfile = MutableLiveData(
        UserProfile(
            name = "Usuario Estudiante",
            email = "estudiante@duoc.cl",
            isVerified = true
        )
    )
    val userProfile: LiveData<UserProfile> = _userProfile

    private val _editMode = MutableLiveData(false)
    val editMode: LiveData<Boolean> = _editMode

    fun toggleEditMode() {
        _editMode.value = !(_editMode.value ?: false)
    }

    fun updateProfile(name: String, email: String) {
        _userProfile.value = _userProfile.value?.copy(
            name = name,
            email = email
        )
    }
}