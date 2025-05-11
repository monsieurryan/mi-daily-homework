package com.example.crashdemo

import android.content.Context
import android.widget.Toast
import java.lang.Thread.UncaughtExceptionHandler

class CrashHandler private constructor(private val context: Context) : UncaughtExceptionHandler {
    private val defaultHandler: UncaughtExceptionHandler? = Thread.getDefaultUncaughtExceptionHandler()

    companion object {
        @Volatile
        private var instance: CrashHandler? = null

        fun getInstance(context: Context): CrashHandler {
            return instance ?: synchronized(this) {
                instance ?: CrashHandler(context.applicationContext).also { instance = it }
            }
        }
    }

    override fun uncaughtException(thread: Thread, throwable: Throwable) {
        // 显示Toast提示用户，包含具体的异常原因
        val message = "应用发生崩溃：${throwable.javaClass.simpleName} - ${throwable.message}"
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        
        // 调用系统默认的异常处理器
        defaultHandler?.uncaughtException(thread, throwable)
    }
} 