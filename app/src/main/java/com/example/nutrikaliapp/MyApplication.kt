package com.example.nutrikaliapp

import android.app.Application
import com.example.nutrikaliapp.utils.TokenManager

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        TokenManager.init(this)
    }
}