package com.example.broadcastdemo

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class StaticReceiver : BroadcastReceiver() {
    companion object {
        const val ACTION_STATIC = "com.example.broadcastdemo.STATIC_ACTION"
        private const val TAG = "StaticReceiver"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val message = intent.getStringExtra("message") ?: "No message"
        Log.d(TAG, "静态接收器收到广播: $message")
    }
} 