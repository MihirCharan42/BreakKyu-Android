package com.example.breakkyuapp

import android.Manifest
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import com.example.breakkyuapp.databinding.ActivityMainBinding
import com.example.breakkyuapp.repository.Resource
import com.example.breakkyuapp.viewModels.AuthViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    private val authViewModel by viewModels<AuthViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Log.e("HI", "Sign up")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                0
            )

        authViewModel.isLoggedIn()
//        Log.e("User", authViewModel.currentUser!!.uid.toString())

        authViewModel.signupFlow.observe(this@MainActivity) {
            binding.loader.isVisible = false
            when (it) {
                is Resource.Success -> {
                    binding.loader.isVisible = false
                    if (authViewModel.currentUser != null) {
                        val intent = Intent(this@MainActivity, HomeActivity::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                    } else {
                        val intent = Intent(this@MainActivity, LoginActivity::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                    }
                }

                is Resource.Loading -> {
                    binding.loader.isVisible = true
                    binding.registerBtn.isVisible = false
                }

                is Resource.Failure -> {
                    binding.loader.isVisible = false
                    binding.registerBtn.isVisible = true

                    Toast.makeText(this, "Account couldn't be created!", Toast.LENGTH_SHORT).show()
                }

                else -> {
                    binding.loader.isVisible = false
                    binding.registerBtn.isVisible = true
                }
            }
        }

        binding.registerBtn.setOnClickListener {
            val name = binding.nameRegister.text.toString()
            val email = binding.emailRegister.text.toString()
            val password = binding.passwordRegister.text.toString()

            authViewModel.signup(name, email, password)
        }

        binding.goToLoginBtn.setOnClickListener {
            val intent = Intent(this@MainActivity, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}