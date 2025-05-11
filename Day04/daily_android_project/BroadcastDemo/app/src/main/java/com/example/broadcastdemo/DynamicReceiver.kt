package com.example.broadcastdemo

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class DynamicReceiver : BroadcastReceiver() {
    companion object {
        const val ACTION_DYNAMIC = "com.example.broadcastdemo.DYNAMIC_ACTION"
        private const val TAG = "DynamicReceiver"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val message = intent.getStringExtra("message") ?: "No message"
        Log.d(TAG, "动态接收器收到广播: $message")
    }
} 