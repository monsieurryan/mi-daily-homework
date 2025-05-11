package com.example.crashdemo

import android.app.Application

class CrashDemoApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // 设置全局异常处理器
        Thread.setDefaultUncaughtExceptionHandler(CrashHandler.getInstance(this))
    }
} 