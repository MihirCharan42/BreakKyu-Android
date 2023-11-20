package com.example.breakkyuapp

import android.os.Bundle
import android.os.PersistableBundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import com.example.breakkyuapp.adapters.BreakAdapter
import com.example.breakkyuapp.databinding.ActivityHomeBinding
import com.example.breakkyuapp.viewModels.AuthViewModel
import com.example.breakkyuapp.viewModels.BreakViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivityV2: AppCompatActivity() {
    lateinit var binding: ActivityHomeBinding

    private val authViewModel by viewModels<AuthViewModel>()

    private val breakViewModel by viewModels<BreakViewModel>()

    private lateinit var adapter: BreakAdapter

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar: Toolbar = findViewById(R.id.homeToolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Hi ${authViewModel.currentUser!!.email}"

        adapter = BreakAdapter()

        breakViewModel.getBreaks()

        binding.refreshHome.setOnRefreshListener {
            binding.refreshHome.isRefreshing = false
            breakViewModel.getBreaks()
        }

        breakViewModel.checkIfAlreadyOnBreak(authViewModel.currentUser!!)

        breakViewModel.breakState.observe(this@HomeActivityV2) {
            if(!it){
                binding.breaksIcons.isVisible = false
                binding.breakBtn.text = "Ready To Work?"
            } else {
                binding.breakBtn.text = "Going For a break?"
                binding.breaksIcons.isVisible = true
            }
        }
    }
}