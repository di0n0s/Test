package com.example.myapplication

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.myapplication.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val viewModel: UserViewModel by viewModels { UserViewModel.UserViewModelFactory("2") }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        collectState()
    }

    private fun collectState() {
        lifecycleScope.launch {
            viewModel.userState.collect {
                when (it) {
                    GetUserState.Idle -> {}
                    is GetUserState.Error -> showError(it.error)

                    is GetUserState.Success -> renderUI(it.user)
                }
            }
        }
    }


    private fun showError(error: String?) {
        error?.let {
            Snackbar.make(
                binding.root,
                error, Snackbar.LENGTH_LONG
            ).show()
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