package com.example.anrwatchdogdemo

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var anrWatchDog: ANRWatchDog
    private lateinit var btnSimulateANR: Button
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        
        // 初始化并启动ANRWatchDog
        anrWatchDog = ANRWatchDog()
        anrWatchDog.start()

        // 设置按钮点击事件
        btnSimulateANR = findViewById(R.id.btnSimulateANR)
        btnSimulateANR.setOnClickListener {
            simulateANR()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun simulateANR() {
        // 禁用按钮
        btnSimulateANR.isEnabled = false
        btnSimulateANR.text = "模拟中..."
        
        // 打印开始时间
        val startTime = System.currentTimeMillis()
        println("开始模拟ANR时间: ${dateFormat.format(Date(startTime))}")

        // 在主线程中模拟耗时操作
        Handler(Looper.getMainLooper()).post {
            try {
                // 阻塞主线程6秒
                Thread.sleep(6000)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            } finally {
                // 恢复按钮状态
                btnSimulateANR.isEnabled = true
                btnSimulateANR.text = "模拟ANR"
                
                // 打印结束时间
                val endTime = System.currentTimeMillis()
                println("模拟ANR结束时间: ${dateFormat.format(Date(endTime))}")
                println("总耗时: ${endTime - startTime}ms")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        anrWatchDog.stop()
    }
}