package com.example.brvahgallerydemo.adapter

import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.example.brvahgallerydemo.DetailActivity
import com.example.brvahgallerydemo.R
import com.example.brvahgallerydemo.TextDetailActivity
import com.example.brvahgallerydemo.data.FavoriteManager
import com.example.brvahgallerydemo.model.ContentItem

class ContentAdapter : BaseMultiItemQuickAdapter<ContentItem, BaseViewHolder>(), 
    FavoriteManager.FavoriteChangeListener {

    private lateinit var favoriteManager: FavoriteManager

    init {
        Log.d("ContentAdapter", "初始化适配器")
        addItemType(ContentItem.ImageItem::class.java.hashCode(), R.layout.item_image)
        addItemType(ContentItem.TextItem::class.java.hashCode(), R.layout.item_text)
    }

    fun setFavoriteManager(manager: FavoriteManager) {
        favoriteManager = manager
        favoriteManager.addListener(this)
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
                
                // 设置初始点赞状态
                item.isFavorite = favoriteManager.isFavorite(item.id)
                holder.setImageResource(
                    R.id.favoriteButton,
                    if (item.isFavorite) R.drawable.ic_favorite_filled
                    else R.drawable.ic_favorite_border
                )

                holder.getView<ImageButton>(R.id.favoriteButton).setOnClickListener {
                    item.isFavorite = !item.isFavorite
                    favoriteManager.toggleFavorite(item)
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
            is ContentItem.TextItem -> {
                Log.d("ContentAdapter", "处理文字项：${item.title}")
                holder.setText(R.id.titleText, item.title)
                holder.setText(R.id.previewText, item.preview)
                holder.setText(R.id.likesText, "${item.likes} likes")
                
                // 设置初始点赞状态
                item.isFavorite = favoriteManager.isFavorite(item.id)
                holder.setImageResource(
                    R.id.favoriteButton,
                    if (item.isFavorite) R.drawable.ic_favorite_filled
                    else R.drawable.ic_favorite_border
                )

                holder.getView<ImageButton>(R.id.favoriteButton).setOnClickListener {
                    item.isFavorite = !item.isFavorite
                    favoriteManager.toggleFavorite(item)
                    holder.setImageResource(
                        R.id.favoriteButton,
                        if (item.isFavorite) R.drawable.ic_favorite_filled
                        else R.drawable.ic_favorite_border
                    )
                }
                
                holder.itemView.setOnClickListener {
                    val intent = TextDetailActivity.newIntent(holder.itemView.context, item)
                    holder.itemView.context.startActivity(intent)
                }
            }
        }
    }

    override fun onFavoriteChanged(itemId: String, isFavorite: Boolean) {
        Log.d("ContentAdapter", "点赞状态改变：itemId=$itemId, isFavorite=$isFavorite")
        // 更新列表中对应项的点赞状态
        data.forEachIndexed { index, item ->
            when (item) {
                is ContentItem.ImageItem -> {
                    if (item.id == itemId) {
                        item.isFavorite = isFavorite
                        notifyItemChanged(index)
                    }
                }
                is ContentItem.TextItem -> {
                    if (item.id == itemId) {
                        item.isFavorite = isFavorite
                        notifyItemChanged(index)
                    }
                }
            }
        }
    }

    override fun onDetachedFromRecyclerView(recyclerView: androidx.recyclerview.widget.RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        // 在适配器从RecyclerView分离时移除监听器
        if (::favoriteManager.isInitialized) {
            favoriteManager.removeListener(this)
        }
    }
} 