package com.example.brvahgallerydemo.adapter

import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.example.brvahgallerydemo.R
import com.example.brvahgallerydemo.model.MovieItem

class MovieAdapter : BaseMultiItemQuickAdapter<MovieItem, BaseViewHolder>() {
    init {
        addItemType(MovieItem::class.java.hashCode(), R.layout.item_movie)
    }

    override fun convert(holder: BaseViewHolder, item: MovieItem) {
        holder.setText(R.id.movieTitle, item.title)
        
        Glide.with(context)
            .load(item.fullPosterPath)
            .into(holder.getView(R.id.moviePoster))
    }
} 