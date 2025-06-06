package com.example.fecalguard.view.history

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fecalguard.MainViewModel
import com.example.fecalguard.data.remote.response.DiagnosisResponse
import com.example.fecalguard.databinding.ActivityHistoryBinding
import dagger.hilt.android.AndroidEntryPoint
import com.example.fecalguard.utils.Result
import com.example.fecalguard.view.analyze.ResultActivity
import com.example.fecalguard.view.history.detail.DetailHistoryActivity

@AndroidEntryPoint
class HistoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHistoryBinding
    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        binding.backButton.setOnClickListener {
            finish()
        }

        setupHistoryData()
    }

    private fun setupHistoryData() {
        mainViewModel.getHistory()
        mainViewModel.historyResult.observe(this) { result ->
            when (result) {
                is Result.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.rvHistory.visibility = View.GONE
                }
                is Result.Success -> {
                    binding.progressBar.visibility = View.GONE
                    if (result.data.isNotEmpty()) {
                        binding.rvHistory.visibility = View.VISIBLE
                        setupHistory(result.data)
                    } else {
                        binding.rvHistory.visibility = View.GONE
                        binding.textAvailable.visibility = View.VISIBLE
                    }
                }
                is Result.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.textAvailable.visibility = View.VISIBLE
                    Log.w("Empty History", result.message)
                }
            }
        }
    }

    private fun setupHistory(data: List<DiagnosisResponse>) {
        binding.rvHistory.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        val adapter = HistoryItemAdapter(data)
        binding.rvHistory.adapter = adapter

        adapter.setOnItemClickCallback(object : HistoryItemAdapter.OnItemClickCallback {
            override fun onItemClicked(data: DiagnosisResponse) {
                handleItemClick(data)
            }
        })
    }

    private fun handleItemClick(data: DiagnosisResponse) {
        val intent = Intent(this, ResultActivity::class.java)
        intent.putExtra("data", data)
        startActivity(intent)
    }
}