package com.example.breakkyuapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.view.isVisible
import com.example.breakkyuapp.databinding.ActivityLoginBinding
import com.example.breakkyuapp.repository.Resource
import com.example.breakkyuapp.viewModels.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    lateinit var binding: ActivityLoginBinding
    private val authViewModel by viewModels<AuthViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Log.e("HI", "Login")

        authViewModel.isLoggedIn()

        authViewModel.loginFlow.observe(this@LoginActivity) {
            binding.loader.isVisible = false
            when (it) {
                is Resource.Success -> {
                    binding.loader.isVisible = false
                    val intent = Intent(this@LoginActivity, HomeActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                }

                is Resource.Loading -> {
                    binding.loader.isVisible = true
                    binding.loginBtn.isVisible = false
                }

                is Resource.Failure -> {
                    binding.loader.isVisible = false
                    binding.loginBtn.isVisible = true

                    Toast.makeText(this, it.exception.message, Toast.LENGTH_SHORT).show()
                }

                else -> {
                    binding.loader.isVisible = false
                    binding.loginBtn.isVisible = true
                }
            }
        }


        binding.loginBtn.setOnClickListener {
            val email = binding.emailLogin.text.toString()
            val password = binding.passwordLogin.text.toString()

            authViewModel.login(email, password)
        }

        binding.goToRegisterBtn.setOnClickListener {
            finish()
        }
    }
}
