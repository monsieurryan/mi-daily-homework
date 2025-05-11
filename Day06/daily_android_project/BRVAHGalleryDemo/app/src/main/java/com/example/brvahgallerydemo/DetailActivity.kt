package com.example.brvahgallerydemo

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import com.example.brvahgallerydemo.model.ContentItem
import com.example.brvahgallerydemo.model.MovieItem

class DetailActivity : AppCompatActivity() {
    private var imageView: ImageView? = null
    private var titleText: TextView? = null
    private var descriptionText: TextView? = null
    private var likesText: TextView? = null
    private var favoriteButton: ImageButton? = null
    private var imageItem: ContentItem.ImageItem? = null
    private var movieItem: MovieItem? = null

    companion object {
        private const val EXTRA_IMAGE_ITEM = "extra_image_item"
        private const val EXTRA_MOVIE_ITEM = "extra_movie_item"

        fun newIntent(context: Context, imageItem: ContentItem.ImageItem): Intent {
            return Intent(context, DetailActivity::class.java).apply {
                putExtra(EXTRA_IMAGE_ITEM, imageItem)
            }
        }

        fun newIntent(context: Context, movieItem: MovieItem): Intent {
            return Intent(context, DetailActivity::class.java).apply {
                putExtra(EXTRA_MOVIE_ITEM, movieItem)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

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
        movieItem = intent.getParcelableExtra(EXTRA_MOVIE_ITEM)
    }

    private fun initViews() {
        imageView = findViewById(R.id.imageView)
        titleText = findViewById(R.id.titleText)
        descriptionText = findViewById(R.id.descriptionText)
        likesText = findViewById(R.id.likesText)
        favoriteButton = findViewById(R.id.favoriteButton)
    }

    private fun setupData() {
        when {
            imageItem != null -> {
                titleText?.text = imageItem!!.title
                descriptionText?.text = imageItem!!.description
                likesText?.text = "${imageItem!!.likes} likes"
                favoriteButton?.setImageResource(
                    if (imageItem!!.isFavorite) R.drawable.ic_favorite_filled
                    else R.drawable.ic_favorite_border
                )
                Glide.with(this)
                    .load(imageItem!!.imageUrl)
                    .into(imageView!!)
            }
            movieItem != null -> {
                titleText?.text = movieItem!!.title
                descriptionText?.text = movieItem!!.overview
                likesText?.text = "Rating: ${movieItem!!.voteAverage}"
                favoriteButton?.setImageResource(
                    if (movieItem!!.isFavorite) R.drawable.ic_favorite_filled
                    else R.drawable.ic_favorite_border
                )
                Glide.with(this)
                    .load(movieItem!!.fullPosterPath)
                    .into(imageView!!)
            }
        }
    }

    private fun setupListeners() {
        favoriteButton?.setOnClickListener {
            when {
                imageItem != null -> {
                    imageItem!!.isFavorite = !imageItem!!.isFavorite
                    updateFavoriteButton()
                }
                movieItem != null -> {
                    movieItem!!.isFavorite = !movieItem!!.isFavorite
                    updateFavoriteButton()
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
            movieItem != null -> {
                favoriteButton?.setImageResource(
                    if (movieItem!!.isFavorite) R.drawable.ic_favorite_filled
                    else R.drawable.ic_favorite_border
                )
            }
        }
    }
} 