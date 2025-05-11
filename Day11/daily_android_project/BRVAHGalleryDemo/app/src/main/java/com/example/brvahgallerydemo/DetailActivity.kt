package com.example.brvahgallerydemo

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.brvahgallerydemo.data.FavoriteManager
import com.example.brvahgallerydemo.model.ContentItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.URL

class DetailActivity : AppCompatActivity() {
    private var imageView: ImageView? = null
    private var titleText: TextView? = null
    private var descriptionText: TextView? = null
    private var likesText: TextView? = null
    private var favoriteButton: ImageButton? = null
    private var downloadButton: ImageButton? = null
    private var imageItem: ContentItem.ImageItem? = null
    private lateinit var favoriteManager: FavoriteManager

    companion object {
        private const val EXTRA_IMAGE_ITEM = "extra_image_item"
        private const val PERMISSION_REQUEST_CODE = 100

        fun newIntent(context: android.content.Context, imageItem: ContentItem.ImageItem): Intent {
            return Intent(context, DetailActivity::class.java).apply {
                putExtra(EXTRA_IMAGE_ITEM, imageItem)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        favoriteManager = FavoriteManager.getInstance(this)
        initToolbar()
        initData()
        initViews()
        setupData()
        setupListeners()
    }

    private fun initToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { onBackPressed() }
    }

    private fun initData() {
        imageItem = intent.getParcelableExtra(EXTRA_IMAGE_ITEM)
    }

    private fun initViews() {
        imageView = findViewById(R.id.imageView)
        titleText = findViewById(R.id.titleText)
        descriptionText = findViewById(R.id.descriptionText)
        likesText = findViewById(R.id.likesText)
        favoriteButton = findViewById(R.id.favoriteButton)
        downloadButton = findViewById(R.id.downloadButton)
    }

    private fun setupData() {
        when {
            imageItem != null -> {
                titleText?.text = imageItem!!.title
                descriptionText?.text = imageItem!!.description
                likesText?.text = "${imageItem!!.likes} likes"
                
                // 设置初始点赞状态
                imageItem!!.isFavorite = favoriteManager.isFavorite(imageItem!!.id)
                updateFavoriteButton()
                
                Glide.with(this)
                    .load(imageItem!!.imageUrl)
                    .into(imageView!!)
            }
        }
    }

    private fun setupListeners() {
        favoriteButton?.setOnClickListener {
            when {
                imageItem != null -> {
                    imageItem!!.isFavorite = !imageItem!!.isFavorite
                    favoriteManager.toggleFavorite(imageItem!!)
                    updateFavoriteButton()
                }
            }
        }

        downloadButton?.setOnClickListener {
            checkPermissionAndDownload()
        }
    }

    private fun checkPermissionAndDownload() {
        val permissions = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            arrayOf(Manifest.permission.READ_MEDIA_IMAGES)
        } else {
            arrayOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        }
        
        val permissionsToRequest = permissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }.toTypedArray()

        if (permissionsToRequest.isNotEmpty()) {
            if (permissionsToRequest.any { permission ->
                    ActivityCompat.shouldShowRequestPermissionRationale(this, permission)
                }
            ) {
                // 显示权限说明对话框
                Toast.makeText(
                    this,
                    "需要存储权限来保存图片",
                    Toast.LENGTH_LONG
                ).show()
            }
            ActivityCompat.requestPermissions(
                this,
                permissionsToRequest,
                PERMISSION_REQUEST_CODE
            )
        } else {
            downloadImage()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && 
                    grantResults.all { it == PackageManager.PERMISSION_GRANTED }
                ) {
                    downloadImage()
                } else {
                    Toast.makeText(
                        this,
                        "需要存储权限来保存图片",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun downloadImage() {
        val imageItem = imageItem ?: return
        val imageUrl = imageItem.imageUrl
        val fileName = "image_${imageItem.id}.jpg"

        // 显示开始下载的提示
        Toast.makeText(this, "开始下载图片...", Toast.LENGTH_SHORT).show()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val url = URL(imageUrl)
                val connection = url.openConnection()
                connection.connect()
                
                // 获取文件大小
                val fileSize = connection.contentLength
                val inputStream = connection.getInputStream()
                
                // 创建下载目录
                val downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                if (!downloadDir.exists()) {
                    downloadDir.mkdirs()
                }
                
                val file = File(downloadDir, fileName)
                FileOutputStream(file).use { outputStream ->
                    val buffer = ByteArray(8192)
                    var bytesRead: Int
                    var totalBytesRead = 0L
                    
                    while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                        outputStream.write(buffer, 0, bytesRead)
                        totalBytesRead += bytesRead
                        
                        // 更新下载进度
                        if (fileSize > 0) {
                            val progress = (totalBytesRead * 100 / fileSize).toInt()
                            withContext(Dispatchers.Main) {
                                Toast.makeText(
                                    this@DetailActivity,
                                    "下载进度：$progress%",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }

                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@DetailActivity,
                        "图片已保存到相册",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@DetailActivity,
                        "保存图片失败：${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun updateFavoriteButton() {
        when {
            imageItem != null -> {
                favoriteButton?.setImageResource(
                    if (imageItem!!.isFavorite) R.drawable.ic_favorite_filled
                    else R.drawable.ic_favorite_border
                )
            }
        }
    }
} 