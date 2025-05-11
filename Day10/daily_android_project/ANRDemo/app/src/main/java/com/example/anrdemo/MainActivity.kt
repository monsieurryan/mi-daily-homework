package com.example.anrdemo

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "ANRDemo"
        const val ANR_ACTION = "com.example.anrdemo.ANR_ACTION"
        private val dateFormat = SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault())
    }

    private val mainHandler = Handler(Looper.getMainLooper())
    private lateinit var anrReceiver: BroadcastReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 注册广播接收器
        anrReceiver = ANRReceiver()
        val filter = IntentFilter(ANR_ACTION)
        registerReceiver(anrReceiver, filter, Context.RECEIVER_NOT_EXPORTED)

        // 设置按钮点击事件
        findViewById<Button>(R.id.btnActivityANR).setOnClickListener {
            val startTime = System.currentTimeMillis()
            val startTimeStr = dateFormat.format(Date(startTime))
            Log.d(TAG, "Activity ANR 开始时间: $startTimeStr")
            
            // 在主线程中执行耗时操作
            mainHandler.post {
                // 更新 UI 状态
                findViewById<TextView>(R.id.statusText).text = "正在执行耗时操作..."
                
                // 执行大量计算和 IO 操作
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
                    
                    Log.d(TAG, "Activity ANR 计算结果: $result")
                } catch (e: Exception) {
                    Log.e(TAG, "Activity ANR 模拟过程中发生错误", e)
                }
                
                // 更新 UI 状态
                findViewById<TextView>(R.id.statusText).text = "耗时操作完成"
                val endTime = System.currentTimeMillis()
                val endTimeStr = dateFormat.format(Date(endTime))
                val duration = endTime - startTime
                Log.d(TAG, "Activity ANR 结束时间: $endTimeStr, 总耗时: ${duration}ms")
            }
        }

        findViewById<Button>(R.id.btnServiceANR).setOnClickListener {
            // 启动 Service
            val startTime = System.currentTimeMillis()
            val startTimeStr = dateFormat.format(Date(startTime))
            Log.d(TAG, "Service ANR 开始时间: $startTimeStr")
            startService(Intent(this, ANRService::class.java))
        }

        findViewById<Button>(R.id.btnReceiverANR).setOnClickListener {
            // 发送广播
            val startTime = System.currentTimeMillis()
            val startTimeStr = dateFormat.format(Date(startTime))
            Log.d(TAG, "Broadcast ANR 开始时间: $startTimeStr")
            val intent = Intent(this, ANRReceiver::class.java).apply {
                action = ANR_ACTION
            }
            sendBroadcast(intent)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // 注销广播接收器
        unregisterReceiver(anrReceiver)
    }
}