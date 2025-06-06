package com.example.fecalguard

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.fecalguard.databinding.ActivityMainBinding
import com.example.fecalguard.utils.showAlertDialog
import com.example.fecalguard.view.analyze.AnalyzeActivity
import com.example.fecalguard.view.auth.LoginActivity
import com.example.fecalguard.view.history.HistoryActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        binding.menuHistory.setOnClickListener { navigateToHistory() }
        binding.analyze.setOnClickListener { navigateToAnalyze() }
        checkLogin()
        setupAction()

    }

    private fun navigateToAnalyze() {
        val intent = Intent(this, AnalyzeActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToHistory() {
        val intent = Intent(this, HistoryActivity::class.java)
        startActivity(intent)
    }

    private fun checkLogin() {
        mainViewModel.getSession().observe(this) { user ->
            if (!user.isLogin) {
                val intent = Intent(this@MainActivity, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            }
        }
    }

    private fun setupAction() {
        binding.logoutButton.setOnClickListener {
            showAlertDialog(
                this,
                title = "Apakah Anda Yakin?",
                message = "Apakah Anda Ingin Keluar?",
                positiveButtonText = "Ya",
                negativeButtonText = "Batal",
                onPositive = { mainViewModel.logout() }
            )
        }
    }

}
