package com.example.broadcastdemo

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.broadcastdemo.ui.theme.BroadcastDemoTheme

class MainActivity : ComponentActivity() {
    private lateinit var dynamicReceiver: DynamicReceiver
    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // 注册动态广播接收器，添加 RECEIVER_NOT_EXPORTED 标志
        dynamicReceiver = DynamicReceiver()
        val filter = IntentFilter(DynamicReceiver.ACTION_DYNAMIC)
        registerReceiver(dynamicReceiver, filter, Context.RECEIVER_NOT_EXPORTED)
        Log.d(TAG, "动态广播接收器已注册")

        setContent {
            BroadcastDemoTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "BroadcastReceiver演示",
                            style = MaterialTheme.typography.headlineMedium
                        )
                        
                        Button(
                            onClick = {
                                // 发送静态广播
                                val staticIntent = Intent(StaticReceiver.ACTION_STATIC).apply {
                                    putExtra("message", "这是来自静态广播的消息")
                                    setPackage(packageName)
                                }
                                sendBroadcast(staticIntent)
                                Log.d(TAG, "已发送静态广播")
                            }
                        ) {
                            Text("发送静态广播")
                        }

                        Button(
                            onClick = {
                                // 发送动态广播
                                val dynamicIntent = Intent(DynamicReceiver.ACTION_DYNAMIC).setPackage(/* TODO: provide the application ID. For example: */
                                    packageName
                                ).apply {
                                    putExtra("message", "这是来自动态广播的消息")
                                }
                                sendBroadcast(dynamicIntent)
                                Log.d(TAG, "已发送动态广播")
                            }
                        ) {
                            Text("发送动态广播")
                        }
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // 注销动态广播接收器
        unregisterReceiver(dynamicReceiver)
        Log.d(TAG, "动态广播接收器已注销")
    }
}