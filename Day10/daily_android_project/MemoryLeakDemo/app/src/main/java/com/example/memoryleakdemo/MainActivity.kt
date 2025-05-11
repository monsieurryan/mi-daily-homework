package com.example.memoryleakdemo

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    // 1.1 静态变量/单例引用导致的内存泄漏
    companion object {
        // 错误示例：静态变量持有Activity引用
        private var staticActivity: MainActivity? = null
        
        // 正确示例：使用WeakReference
        // private var staticActivity: WeakReference<MainActivity>? = null
    }

    // 1.2 注册和监听未正确释放导致的内存泄漏
    private var leakyListener: LeakyListener? = null

    // 1.3 内部线程持有外部类导致的内存泄漏
    private var leakyThread: Thread? = null

    // 1.4 Handler引起的内存泄漏
    private var leakyHandler: Handler? = null
    private val leakyRunnable = Runnable {
        // 模拟耗时操作
        Thread.sleep(10000)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 1.1 静态变量内存泄漏示例
        findViewById<Button>(R.id.btnStaticLeak).setOnClickListener {
            // 错误示例：直接赋值
            staticActivity = this
            
            // 正确示例：使用WeakReference
            // staticActivity = WeakReference(this)
        }

        // 1.2 监听器内存泄漏示例
        findViewById<Button>(R.id.btnListenerLeak).setOnClickListener {
            // 错误示例：未在onDestroy中移除监听器
            leakyListener = LeakyListener(this)
            leakyListener?.startListening()
        }

        // 1.3 线程内存泄漏示例
        findViewById<Button>(R.id.btnThreadLeak).setOnClickListener {
            // 错误示例：内部类持有外部类引用
            leakyThread = Thread {
                while (true) {
                    // 模拟耗时操作
                    Thread.sleep(1000)
                }
            }
            leakyThread?.start()
        }

        // 1.4 Handler内存泄漏示例
        findViewById<Button>(R.id.btnHandlerLeak).setOnClickListener {
            // 错误示例：Handler持有Activity引用
            leakyHandler = Handler(Looper.getMainLooper())
            leakyHandler?.postDelayed(leakyRunnable, 10000)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        
        // 1.1 静态变量内存泄漏修复
        staticActivity = null
        
        // 1.2 监听器内存泄漏修复
        leakyListener?.stopListening()
        leakyListener = null
        
        // 1.3 线程内存泄漏修复
        leakyThread?.interrupt()
        leakyThread = null
        
        // 1.4 Handler内存泄漏修复
        leakyHandler?.removeCallbacks(leakyRunnable)
        leakyHandler = null
    }
}

// 1.2 监听器内存泄漏示例类
class LeakyListener(private val activity: MainActivity) {
    fun startListening() {
        // 模拟注册监听器
    }

    fun stopListening() {
        // 模拟移除监听器
    }
}