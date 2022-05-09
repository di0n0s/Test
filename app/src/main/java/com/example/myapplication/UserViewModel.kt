package com.example.myapplication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class UserViewModel(private val iODispatcher: CoroutineDispatcher = Dispatchers.IO) : ViewModel() {

    private val apiService: ApiService = Retrofit.Builder()
        .baseUrl("https://reqres.in/api/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(ApiService::class.java)

    val userIntent = Channel<UserIntent>(Channel.UNLIMITED)

    private val _userState = MutableStateFlow<GetUserState>(GetUserState.Idle)
    val userState: StateFlow<GetUserState>
        get() = _userState

    init {
        handleUserIntent()
    }

    private fun handleUserIntent() {
        viewModelScope.launch {
            userIntent.consumeAsFlow().collect {
                when (it) {
                    is UserIntent.GetUser -> getUser(it.id)
                }
            }
        }
    }

    private fun getUser(id: String) {
        viewModelScope.launch(iODispatcher) {
            _userState.value = GetUserState.Loading

            _userState.value = try {
                val data = apiService.getUser(id).data
                GetUserState.Success(data)
            } catch (e: Exception) {
                GetUserState.Error(e.localizedMessage)
            }
        }

    }

}

sealed class UserIntent {
    class GetUser(val id: String) : UserIntent()
}

sealed class GetUserState {
    object Idle : GetUserState()
    object Loading : GetUserState()
    class Success(val user: User) : GetUserState()
    class Error(val error: String?) : GetUserState()
}