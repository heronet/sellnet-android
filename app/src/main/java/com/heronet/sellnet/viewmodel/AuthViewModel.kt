package com.heronet.sellnet.viewmodel

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.heronet.sellnet.data.UserRepository
import com.heronet.sellnet.dataStore
import com.heronet.sellnet.model.AuthData
import com.heronet.sellnet.util.AuthStatus
import com.heronet.sellnet.util.Constants.ID
import com.heronet.sellnet.util.Constants.NAME
import com.heronet.sellnet.util.Constants.ROLE
import com.heronet.sellnet.util.Constants.TOKEN
import com.heronet.sellnet.util.Resource
import com.heronet.sellnet.web.Location
import com.heronet.sellnet.web.LoginDto
import com.heronet.sellnet.web.RegisterDto
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: UserRepository,
    @ApplicationContext private val context: Context
): ViewModel() {
    var authStatus = mutableStateOf<AuthStatus>(AuthStatus.Unauthenticated())
    var isLoading = mutableStateOf(false)
    var isLocationsLoading = mutableStateOf(false)
    var isRefreshing = mutableStateOf(false)
    var errorText = mutableStateOf("")
    var registerErrorMessages = mutableStateOf(mapOf<String, String>())
    var locations = mutableStateOf<Location?>(null)

    init {
        refreshToken()
    }

    fun loginUser(email: String, password: String) {
        isLoading.value = true
        val loginDto = LoginDto(email, password)
        viewModelScope.launch {
            val resource = repository.loginUser(loginDto)
            setAuthData(resource)
        }
    }
    fun registerUser(name: String, email: String, password: String, phone: String, city: String, division: String) {
        isLoading.value = true
        val registerDto = RegisterDto(name, email, phone, password,
            city.lowercase(Locale.getDefault()), division.lowercase(Locale.getDefault()))
        viewModelScope.launch {
            val resource = repository.registerUser(registerDto)
            setAuthData(resource)
        }
    }
    fun getLocations() {
        isLocationsLoading.value = true
        viewModelScope.launch {
            when(val resource = repository.getLocations()) {
                is Resource.Error -> {
                    isLocationsLoading.value = false
                    errorText.value = resource.message!!
                }
                is Resource.Success -> {
                    locations.value = resource.data!!
                    isLocationsLoading.value = false
                    errorText.value = ""
                }
            }
        }
    }
    private fun refreshToken() {
        isRefreshing.value = true
        viewModelScope.launch {
            val dataStore = context.dataStore.data.first()
            val token = dataStore[TOKEN]
            if (token == null) {
                isRefreshing.value = false
            } else {
                val authData = AuthData(
                    id = dataStore[ID]!!,
                    name = dataStore[NAME]!!,
                    token = token,
                    roles = listOf(dataStore[ROLE]!!)
                )
                val resource = repository.refreshToken(token, authData)
                setAuthData(resource)
                isRefreshing.value = false
            }
        }
    }
    private suspend fun setAuthData(resource: Resource<AuthData>) {
        when(resource) {
            is Resource.Error -> {
                isLoading.value = false
                authStatus.value = AuthStatus.Unauthenticated()
                errorText.value = resource.message!!
                if (resource.messages != null) { // Used for register
                    registerErrorMessages.value = getErrorMap(resource.messages)
                }
                if (resource.message == "An error occurred") {
                    logout()
                }
            }
            is Resource.Success -> {
                isLoading.value = false
                errorText.value = ""
                authStatus.value = AuthStatus.Authenticated(authData = resource.data!!)

                context.dataStore.edit { authData ->
                    authData[TOKEN] = authStatus.value.authData!!.token
                    authData[ID] = authStatus.value.authData!!.id
                    authData[NAME] = authStatus.value.authData!!.name
                    authData[ROLE] = authStatus.value.authData!!.roles[0]
                }

                // Also, reset the locations list to save memory
                resetLocations()
            }
            is Resource.Loading -> {}
        }
    }
    fun logout() {
        viewModelScope.launch {
            context.dataStore.edit { authData ->
                authData.clear()
            }
        }
        authStatus.value = AuthStatus.Unauthenticated()
    }
    private fun resetLocations() {
        locations.value = null
    }
    private fun getErrorMap(messages: List<String>): MutableMap<String, String> {
        val errors = mutableMapOf<String, String>()
        messages.forEach {
            if (it.contains("Email")) {
                errors["Email"] = it
            }
            if (it.contains("Password")) {
                errors["Password"] = it
            }
            if (it.contains("Phone")) {
                errors["Phone"] = it
            }
        }
        return errors
    }
}