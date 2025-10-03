package com.example.app.data

import android.content.Context
import android.content.SharedPreferences
import com.example.app.model.User

class UserRepository(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_USER_PASSWORD = "user_password"
        private const val KEY_USER_CREATED_AT = "user_created_at"
        private const val KEY_HAS_USER = "has_user"
    }

    fun saveUser(user: User) {
        sharedPreferences.edit().apply {
            putString(KEY_USER_NAME, user.name)
            putString(KEY_USER_PASSWORD, user.password)
            putLong(KEY_USER_CREATED_AT, user.createdAt)
            putBoolean(KEY_HAS_USER, true)
            apply()
        }
    }

    fun getUser(): User? {
        if (!hasUser()) return null

        val name = sharedPreferences.getString(KEY_USER_NAME, "") ?: ""
        val password = sharedPreferences.getString(KEY_USER_PASSWORD, "") ?: ""
        val createdAt = sharedPreferences.getLong(KEY_USER_CREATED_AT, 0L)

        return if (name.isNotEmpty() && password.isNotEmpty()) {
            User(name, password, createdAt)
        } else null
    }

    fun hasUser(): Boolean {
        return sharedPreferences.getBoolean(KEY_HAS_USER, false)
    }

    fun verifyPassword(inputPassword: String): Boolean {
        val storedPassword = sharedPreferences.getString(KEY_USER_PASSWORD, "")
        return storedPassword == inputPassword
    }

    fun clearUser() {
        sharedPreferences.edit().clear().apply()
    }
}