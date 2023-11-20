package com.example.breakkyuapp

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.breakkyuapp.adapters.BreakAdapter
import com.example.breakkyuapp.databinding.ActivitySearchBinding
import com.example.breakkyuapp.models.Break
import com.example.breakkyuapp.repository.Resource
import com.example.breakkyuapp.viewModels.BreakViewModel
import com.google.firebase.Timestamp
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchActivity : AppCompatActivity() {

    lateinit var binding: ActivitySearchBinding

    private val breakViewModel by viewModels<BreakViewModel>()

    private lateinit var adapter: BreakAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar: Toolbar = findViewById(R.id.searchToolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Search Users"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        adapter = BreakAdapter()

        breakViewModel.getBreaks()

        binding.searchTextfield.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                breakViewModel.getBreaks()
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })

        breakViewModel.breakLiveData.observe(this@SearchActivity) {res ->
            when(res) {
                is Resource.Loading -> {
                    binding.loader.isVisible = true
                    binding.usersRv.isVisible = false
                }
                is Resource.Success -> {
                    binding.loader.isVisible = false
                    binding.usersRv.isVisible = true
                    val data = res.result
                    data.addOnCompleteListener(this@SearchActivity) { querySnapshot ->
                        val breakList = mutableListOf<Break>()

                        for (document in querySnapshot.result) {

                            val breakObj = Break(document.data.get("userName").toString(), document.data.get("time") as Timestamp
                                , document.data.get("userId").toString(), document.id, document.data.get("note").toString())
                            val query = binding.searchTextfield.text.toString()
                            if(breakObj.name.contains(query)){
                                breakObj?.let {
                                    breakList.add(it)
                                }
                            }
                        }
                        if(breakList.isEmpty()){
                            binding.usersRv.isVisible = false
                            binding.noBreaks.isVisible = true
                            binding.noBreaks.text = "No ${binding.searchTextfield.text.toString()} was found!"
                        } else {
                            binding.usersRv.isVisible = true
                            binding.noBreaks.isVisible = false
                        }
                        adapter.submitList(breakList)
                    }.addOnFailureListener { exception ->
                        // Handle failure
                        Toast.makeText(this, "Error fetching data", Toast.LENGTH_SHORT).show()
                    }
                }
                is Resource.Failure -> {
                    binding.loader.isVisible = false
                    binding.usersRv.isVisible = true
                    binding.noBreaks.isVisible = true
                    binding.noBreaks.text = "Something went wrong"
                    Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()
                }
            }
        }
        binding.usersRv.layoutManager = LinearLayoutManager(this)
        binding.usersRv.adapter = adapter
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            finish()
        }

        return super.onOptionsItemSelected(item)
    }
}