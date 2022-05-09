package com.example.myapplication

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.myapplication.databinding.ActivityMainBinding
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val viewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        collectState()
        getUser()
    }

    private fun getUser() {
        lifecycleScope.launch {
            viewModel.userIntent.send(UserIntent.GetUser("2"))
        }
    }

    private fun collectState() {
        lifecycleScope.launch {
            viewModel.userState.collect {
                when (it) {
                    is GetUserState.Error -> Log.e("userState", "error")
                    GetUserState.Idle -> {}
                    GetUserState.Loading -> Log.e("userState", "Loading")
                    is GetUserState.Success -> renderUI(it.user)
                }
            }
        }

    }

    private fun renderUI(user: User) {
        with(binding) {
            Glide.with(this@MainActivity)
                .load(user.avatar)
                .into(imageView)

            fullNameTextView.text =
                getString(R.string.full_name, user.first_name, user.last_name)

            emailTextView.text = user.email
        }
    }
}