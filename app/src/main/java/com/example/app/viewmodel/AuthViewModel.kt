package com.example.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.app.data.UserRepository
import com.example.app.model.User

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val userRepository = UserRepository(application)

    private val _isAuthenticated = MutableLiveData<Boolean>()
    val isAuthenticated: LiveData<Boolean> = _isAuthenticated

    private val _currentUser = MutableLiveData<User?>()
    val currentUser: LiveData<User?> = _currentUser

    private val _loginError = MutableLiveData<String?>()
    val loginError: LiveData<String?> = _loginError

    private val _registrationSuccess = MutableLiveData<Boolean>()
    val registrationSuccess: LiveData<Boolean> = _registrationSuccess

    init {
        checkUserStatus()
    }

    private fun checkUserStatus() {
        val user = userRepository.getUser()
        _currentUser.value = user
        _isAuthenticated.value = false
    }

    fun hasExistingUser(): Boolean {
        return userRepository.hasUser()
    }

    fun registerUser(name: String, password: String): Boolean {
        return if (name.isNotBlank() && password.isNotBlank()) {
            val user = User(name, password)
            userRepository.saveUser(user)
            _currentUser.value = user
            _isAuthenticated.value = true
            _registrationSuccess.value = true
            true
        } else {
            _loginError.value = "Nombre y contraseña no pueden estar vacíos"
            false
        }
    }

    fun login(password: String): Boolean {
        return if (userRepository.verifyPassword(password)) {
            _currentUser.value = userRepository.getUser()
            _isAuthenticated.value = true
            _loginError.value = null
            true
        } else {
            _loginError.value = "Contraseña incorrecta"
            false
        }
    }

    fun loginWithCredentials(name: String, password: String): Boolean {
        val user = userRepository.getUser()
        return if (user != null && user.name == name && user.password == password) {
            _currentUser.value = user
            _isAuthenticated.value = true
            _loginError.value = null
            true
        } else {
            _loginError.value = "Nombre o contraseña incorrectos"
            false
        }
    }

    fun logout() {
        _isAuthenticated.value = false
        _currentUser.value = userRepository.getUser()
    }

    fun clearError() {
        _loginError.value = null
    }

    fun refreshCurrentUser() {
        _currentUser.value = userRepository.getUser()
    }

    fun deleteUser() {
        userRepository.clearUser()
        _currentUser.value = null
        _isAuthenticated.value = false
    }
}
