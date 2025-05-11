package com.example.anrdemo

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ANRReceiver : BroadcastReceiver() {
    companion object {
        private const val TAG = "ANRDemo"
        private val dateFormat = SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault())
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        val startTime = System.currentTimeMillis()
        val startTimeStr = dateFormat.format(Date(startTime))
        Log.d(TAG, "Broadcast ANR 开始时间: $startTimeStr")
        
        // 在主线程中执行耗时操作
        try {
            // 执行大量计算
            var result = 0
            for (i in 0..100000000) {
                result += i
            }
            
            // 模拟 IO 操作
            Thread.sleep(10000)
            
            // 再次执行大量计算
            for (i in 0..100000000) {
                result += i
            }
            
            // 再次模拟 IO 操作
            Thread.sleep(10000)
            
            Log.d(TAG, "Broadcast ANR 计算结果: $result")
        } catch (e: Exception) {
            Log.e(TAG, "Broadcast ANR 模拟过程中发生错误", e)
        }
        
        val endTime = System.currentTimeMillis()
        val endTimeStr = dateFormat.format(Date(endTime))
        val duration = endTime - startTime
        Log.d(TAG, "Broadcast ANR 结束时间: $endTimeStr, 总耗时: ${duration}ms")
    }
} 