package com.example.breakkyuapp

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.breakkyuapp.adapters.BreakAdapter
import com.example.breakkyuapp.databinding.ActivityHomeBinding
import com.example.breakkyuapp.models.Break
import com.example.breakkyuapp.repository.Resource
import com.example.breakkyuapp.viewModels.AuthViewModel
import com.example.breakkyuapp.viewModels.BreakViewModel
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.type.DateTime
import dagger.hilt.android.AndroidEntryPoint
import java.sql.Time
import java.util.Date

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {

    lateinit var binding: ActivityHomeBinding

    private val authViewModel by viewModels<AuthViewModel>()

    private val breakViewModel by viewModels<BreakViewModel>()

    private lateinit var adapter: BreakAdapter
    val breakList = mutableListOf<Break>()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
            breakViewModel.checkIfAlreadyOnBreak(authViewModel.currentUser!!)
        }

        breakViewModel.checkIfAlreadyOnBreak(authViewModel.currentUser!!)

        breakViewModel.breakState.observe(this@HomeActivity) {
            if(it){
                binding.breaksIcons.isVisible = false
                binding.breakBtn.text = "Ready To Work?"
            } else {
                binding.breakBtn.text = "Going For a break?"
                binding.breaksIcons.isVisible = true
            }
        }

        binding.breakBtn.setOnClickListener {
            if(breakViewModel.breakState.value == true){
                val breakEndTime = Timestamp.now()
                var id = ""
                var fromTime: Timestamp? = null
                for(breakObj in breakList){
                    if(breakObj.userId == authViewModel.currentUser!!.uid){
                        id = breakObj.id
                        fromTime = breakObj.time
                    }
                }
                if(id != "" && fromTime != null) {
                    breakViewModel.deleteBreak(
                        authViewModel.currentUser!!,
                        breakEndTime,
                        fromTime,
                        id
                    )
                    Intent(this, BreakService::class.java).also {
                        it.action = BreakService.Actions.STOP.toString()
                        startService(it)
                    }
                }
            } else {
                addBreak("")
            }
            breakViewModel.checkIfAlreadyOnBreak(authViewModel.currentUser!!)
        }

        binding.teaBreakBtn.setOnClickListener {
            addBreak("tea")
        }

        binding.phoneBreakBtn.setOnClickListener {
            addBreak("phone")
        }

        binding.sickBreakBtn.setOnClickListener {
            addBreak("sick")

        }

        binding.lunchBreakBtn.setOnClickListener {
            addBreak("lunch")
        }

        breakViewModel.addBreakLiveData.observe(this@HomeActivity) {res ->
            when(res) {
                is Resource.Loading -> {
                    binding.loader.isVisible = true
                    binding.breakBtn.isVisible = false
                    binding.breaksRv.isVisible = false
                    binding.refreshHome.isVisible = false
                    binding.noBreaks.isVisible = false
                    binding.breaksIcons.isVisible = false
                    binding.breaksTv.isVisible = false
                }
                is Resource.Success -> {
                    binding.loader.isVisible = false
                    binding.breakBtn.isVisible = true
                    binding.breaksRv.isVisible = true
                    binding.refreshHome.isVisible = true
                    binding.noBreaks.isVisible = true
                    binding.breaksIcons.isVisible = true
                    binding.breaksTv.isVisible = true
                    res.result.addOnCompleteListener(this) {
                        val sharedPreferences = getSharedPreferences("BreakPrefs", Context.MODE_PRIVATE)
                        val editor = sharedPreferences.edit()
                        editor.putString("docID", it.result.id)
                        editor.apply()
                    }
                    breakViewModel.getBreaks()
                }
                is Resource.Failure -> {
                    binding.loader.isVisible = false
                    binding.breakBtn.isVisible = true
                    binding.breaksRv.isVisible = true
                    binding.refreshHome.isVisible = true
                    binding.noBreaks.isVisible = false
                    binding.breaksIcons.isVisible = true
                    binding.breaksTv.isVisible = true
                    Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()
                }
            }
        }

        breakViewModel.breakLiveData.observe(this@HomeActivity) {res ->
            when(res) {
                is Resource.Loading -> {
                    binding.loader.isVisible = true
                    binding.breakBtn.isVisible = false
                    binding.breaksRv.isVisible = false
                    binding.refreshHome.isVisible = false
                    binding.noBreaks.isVisible = false
                    binding.breaksIcons.isVisible = false
                    binding.breaksTv.isVisible = false
                }
                is Resource.Success -> {
                    binding.loader.isVisible = false
                    binding.breakBtn.isVisible = true
                    binding.breaksRv.isVisible = true
                    binding.refreshHome.isVisible = true
                    binding.noBreaks.isVisible = true
                    binding.breaksIcons.isVisible = true
                    binding.breaksTv.isVisible = true
                    val data = res.result
                    data.addOnCompleteListener(this@HomeActivity) { querySnapshot ->
                        breakList.clear()
                        val newBreakList = mutableListOf<Break>()
                        for (document in querySnapshot.result) {
                            val breakObj = Break(document.data.get("userName").toString(), document.data.get("time") as Timestamp
                            , document.data.get("userId").toString(), document.id, document.data.get("note").toString())
                            breakObj?.let {
                                breakList.add(it)
                                newBreakList.add(it)
                            }
                        }
                        if(breakList.isEmpty()){
                            binding.breaksRv.isVisible = false
                            binding.noBreaks.isVisible = true
                        } else {
                            binding.breaksRv.isVisible = true
                            binding.noBreaks.isVisible = false
                        }
                        adapter.submitList(newBreakList)
                    }.addOnFailureListener { exception ->
                        // Handle failure
                        Toast.makeText(this, "Error fetching data", Toast.LENGTH_SHORT).show()
                    }
                }
                is Resource.Failure -> {
                    binding.loader.isVisible = false
                    binding.breakBtn.isVisible = true
                    binding.breaksRv.isVisible = true
                    binding.refreshHome.isVisible = true
                    binding.noBreaks.isVisible = false
                    binding.breaksIcons.isVisible = true
                    binding.breaksTv.isVisible = true
                    Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.breaksRv.layoutManager = LinearLayoutManager(this)
        binding.breaksRv.adapter = adapter

//        binding.breakBtn.setOnClickListener{
//            breakViewModel.addBreak(authViewModel.currentUser!!)
//        }

        binding.searchBtn.setOnClickListener{
            val intent = Intent(this@HomeActivity, SearchActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.home_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if(id == R.id.logout){
            authViewModel.logout()
            val intent = Intent(this@HomeActivity, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }
        return true
    }

    fun addBreak(note:String) {
        val breakStartTime = Timestamp.now()

        breakViewModel.addBreak(authViewModel.currentUser!!, breakStartTime, note)
        Intent(this, BreakService::class.java).also {
            it.action = BreakService.Actions.START.toString()
            startService(it)
        }
    }
}