package com.example.fecalguard.data.local

data class UserSession(
    val token: String,
    val isLogin: Boolean = false
)