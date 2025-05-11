package com.example.brvahgallerydemo

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.brvahgallerydemo.data.FavoriteManager
import com.example.brvahgallerydemo.model.ContentItem

class TextDetailActivity : AppCompatActivity() {
    private var titleText: TextView? = null
    private var contentText: TextView? = null
    private var likesText: TextView? = null
    private var favoriteButton: ImageButton? = null
    private var textItem: ContentItem.TextItem? = null
    private lateinit var favoriteManager: FavoriteManager

    companion object {
        private const val EXTRA_TEXT_ITEM = "extra_text_item"

        fun newIntent(context: Context, textItem: ContentItem.TextItem): Intent {
            return Intent(context, TextDetailActivity::class.java).apply {
                putExtra(EXTRA_TEXT_ITEM, textItem)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_text_detail)

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
        textItem = intent.getParcelableExtra(EXTRA_TEXT_ITEM)
    }

    private fun initViews() {
        titleText = findViewById(R.id.titleText)
        contentText = findViewById(R.id.contentText)
        likesText = findViewById(R.id.likesText)
        favoriteButton = findViewById(R.id.favoriteButton)
    }

    private fun setupData() {
        when {
            textItem != null -> {
                titleText?.text = textItem!!.title
                contentText?.text = textItem!!.content
                likesText?.text = "${textItem!!.likes} likes"
                
                // 设置初始点赞状态
                textItem!!.isFavorite = favoriteManager.isFavorite(textItem!!.id)
                updateFavoriteButton()
            }
        }
    }

    private fun setupListeners() {
        favoriteButton?.setOnClickListener {
            when {
                textItem != null -> {
                    textItem!!.isFavorite = !textItem!!.isFavorite
                    favoriteManager.toggleFavorite(textItem!!)
                    updateFavoriteButton()
                }
            }
        }
    }

    private fun updateFavoriteButton() {
        when {
            textItem != null -> {
                favoriteButton?.setImageResource(
                    if (textItem!!.isFavorite) R.drawable.ic_favorite_filled
                    else R.drawable.ic_favorite_border
                )
            }
        }
    }
} 