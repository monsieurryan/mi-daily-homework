package com.example.brvahgallerydemo.adapter

import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.example.brvahgallerydemo.DetailActivity
import com.example.brvahgallerydemo.R
import com.example.brvahgallerydemo.model.ContentItem

class ContentAdapter : BaseMultiItemQuickAdapter<ContentItem, BaseViewHolder>() {

    init {
        Log.d("ContentAdapter", "初始化适配器")
        addItemType(ContentItem.ImageItem::class.java.hashCode(), R.layout.item_image)
    }

    override fun setList(list: Collection<ContentItem>?) {
        Log.d("ContentAdapter", "设置数据列表，数量：${list?.size ?: 0}")
        super.setList(list)
        Log.d("ContentAdapter", "数据列表设置完成")
    }

    override fun convert(holder: BaseViewHolder, item: ContentItem) {
        Log.d("ContentAdapter", "转换数据项：${item.javaClass.simpleName}")
        when (item) {
            is ContentItem.ImageItem -> {
                Log.d("ContentAdapter", "处理图片项：${item.title}")
                holder.getView<ImageView>(R.id.imageView).let { imageView ->
                    Log.d("ContentAdapter", "加载图片：${item.imageUrl}")
                    Glide.with(imageView)
                        .load(item.imageUrl)
                        .into(imageView)
                }
                holder.setText(R.id.titleText, item.title)
                holder.getView<ImageButton>(R.id.favoriteButton).setOnClickListener {
                    item.isFavorite = !item.isFavorite
                    holder.setImageResource(
                        R.id.favoriteButton,
                        if (item.isFavorite) R.drawable.ic_favorite_filled
                        else R.drawable.ic_favorite_border
                    )
                }
                holder.itemView.setOnClickListener {
                    val intent = DetailActivity.newIntent(holder.itemView.context, item)
                    holder.itemView.context.startActivity(intent)
                }
            }
        }
    }
} 