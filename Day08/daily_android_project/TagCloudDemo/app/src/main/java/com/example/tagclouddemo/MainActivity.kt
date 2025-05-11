package com.example.tagclouddemo

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val tagCloudView = findViewById<TagCloudView>(R.id.tagCloudView)
        
        // 设置示例标签
        val tags = listOf(
            "Android", "Kotlin", "Java", "XML", "Gradle",
            "Android Studio", "Layout", "View", "Activity",
            "Fragment", "RecyclerView", "ConstraintLayout"
        )
        
        Log.d("MainActivity", "设置标签数据: $tags")
        tagCloudView.setTags(tags)
    }
}