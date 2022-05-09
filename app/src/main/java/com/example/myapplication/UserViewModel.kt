package com.example.myapplication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class UserViewModel(
    private val id: String,
    private val iODispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val apiService: ApiService = Retrofit.Builder()
        .baseUrl("https://reqres.in/api/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(
            ApiService::
            class.java
        )
) : ViewModel() {

    private val _userState: MutableStateFlow<GetUserState> by lazy {
        MutableStateFlow<GetUserState>(GetUserState.Idle).also {
            getUser(id)
        }
    }
    val userState: StateFlow<GetUserState>
        get() = _userState


    private fun getUser(id: String) {
        viewModelScope.launch(iODispatcher) {
            _userState.value = try {
                val data = apiService.getUser(id).data
                GetUserState.Success(data)
            } catch (e: Exception) {
                GetUserState.Error(e.localizedMessage)
            }
        }

    }

    @Suppress("UNCHECKED_CAST")
    class UserViewModelFactory(private val userId: String) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T = UserViewModel(userId) as T
    }

}

sealed class GetUserState {
    object Idle : GetUserState()
    class Success(val user: User) : GetUserState()
    class Error(val error: String?) : GetUserState()
}