package com.example.anrwatchdogdemo

import android.os.Handler
import android.os.Looper
import android.os.Process
import android.os.SystemClock
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean

class ANRWatchDog {
    private val TAG = "ANRWatchDog"
    private val mainHandler = Handler(Looper.getMainLooper())
    private val checkHandler = Handler(Looper.getMainLooper())
    private var lastResponseTime = SystemClock.uptimeMillis()
    private var noResponseCount = 0
    private val isRunning = AtomicBoolean(false)
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault())

    private val checkThread = Thread {
        while (isRunning.get()) {
            val currentTime = SystemClock.uptimeMillis()
            if (currentTime - lastResponseTime > 1000) { // 1秒没有响应
                noResponseCount++
                if (noResponseCount >= 5) { // 连续5次未收到响应
                    handleANR()
                    noResponseCount = 0
                }
            } else {
                noResponseCount = 0
            }
            try {
                Thread.sleep(1000) // 每秒检查一次
            } catch (e: InterruptedException) {
                if (!isRunning.get()) {
                    break
                }
            }
        }
    }

    private val heartbeatRunnable = object : Runnable {
        override fun run() {
            lastResponseTime = SystemClock.uptimeMillis()
            mainHandler.postDelayed(this, 1000) // 每秒发送一次心跳
        }
    }

    fun start() {
        if (isRunning.get()) {
            return
        }
        isRunning.set(true)
        checkThread.start()
        mainHandler.post(heartbeatRunnable)
        Log.d(TAG, "ANRWatchDog启动时间: ${dateFormat.format(Date())}")
    }

    fun stop() {
        isRunning.set(false)
        checkThread.interrupt()
        mainHandler.removeCallbacks(heartbeatRunnable)
        Log.d(TAG, "ANRWatchDog停止时间: ${dateFormat.format(Date())}")
    }

    private fun handleANR() {
        val currentTime = System.currentTimeMillis()
        Log.e(TAG, "检测到ANR，开始采集线程信息，时间: ${dateFormat.format(Date(currentTime))}")
        val threadDump = StringBuilder()
        threadDump.append("ANR发生时间: ${dateFormat.format(Date(currentTime))}\n\n")
        
        // 获取所有线程信息
        val threads = Thread.getAllStackTraces()
        for ((thread, stackTrace) in threads) {
            threadDump.append("线程: ${thread.name} (ID: ${thread.id})\n")
            threadDump.append("状态: ${thread.state}\n")
            threadDump.append("优先级: ${thread.priority}\n")
            threadDump.append("堆栈信息:\n")
            for (element in stackTrace) {
                threadDump.append("\tat $element\n")
            }
            threadDump.append("\n")
        }

        // 保存线程信息到文件
        val file = File(File("/data/data/com.example.anrwatchdogdemo/files"), "anr_dump_${currentTime}.txt")
        try {
            FileOutputStream(file).use { outputStream ->
                outputStream.write(threadDump.toString().toByteArray())
            }
            Log.d(TAG, "ANR信息已保存到: ${file.absolutePath}")
        } catch (e: Exception) {
            Log.e(TAG, "保存ANR信息失败", e)
        }
    }
} 