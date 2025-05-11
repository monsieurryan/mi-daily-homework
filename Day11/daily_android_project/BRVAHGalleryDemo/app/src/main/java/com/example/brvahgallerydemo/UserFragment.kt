package com.example.brvahgallerydemo

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsetsController
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.brvahgallerydemo.adapter.ContentAdapter
import com.example.brvahgallerydemo.data.FavoriteManager
import com.example.brvahgallerydemo.databinding.FragmentUserBinding
import com.example.brvahgallerydemo.model.ContentItem
import com.example.brvahgallerydemo.viewmodel.HomeViewModel

class UserFragment : Fragment(), FavoriteManager.FavoriteChangeListener {
    private var _binding: FragmentUserBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeViewModel by viewModels()
    private lateinit var adapter: ContentAdapter
    private lateinit var favoriteManager: FavoriteManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d("UserFragment", "onCreateView")
        _binding = FragmentUserBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("UserFragment", "onViewCreated")
        setupToolbar()
        setupRecyclerView()
        observeData()
    }

    private fun setupToolbar() {
        val activity = requireActivity() as AppCompatActivity
        activity.setSupportActionBar(binding.toolbar)
        activity.supportActionBar?.apply {
            setDisplayShowTitleEnabled(true)
            setDisplayHomeAsUpEnabled(false)
        }

        // 设置状态栏
        WindowCompat.setDecorFitsSystemWindows(activity.window, false)
        activity.window.insetsController?.let { controller ->
            controller.hide(WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE)
            controller.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    private fun setupRecyclerView() {
        Log.d("UserFragment", "setupRecyclerView")
        adapter = ContentAdapter()
        favoriteManager = FavoriteManager.getInstance(requireContext())
        favoriteManager.addListener(this)
        adapter.setFavoriteManager(favoriteManager)
        
        binding.recyclerView.apply {
            layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            adapter = this@UserFragment.adapter
            Log.d("UserFragment", "RecyclerView adapter set")
        }
    }

    private fun observeData() {
        Log.d("UserFragment", "observeData")
        viewModel.contentItems.observe(viewLifecycleOwner) { items ->
            Log.d("UserFragment", "收到数据更新，数量：${items.size}")
            updateLikedItems(items)
        }
    }

    private fun updateLikedItems(items: List<ContentItem>) {
        Log.d("UserFragment", "开始更新点赞列表，总数据量：${items.size}")
        
        // 统计不同类型的内容数量
        val imageItems = items.filterIsInstance<ContentItem.ImageItem>()
        val textItems = items.filterIsInstance<ContentItem.TextItem>()
        Log.d("UserFragment", "图片数量：${imageItems.size}，文字数量：${textItems.size}")
        
        // 只显示已点赞的内容
        val likedItems = items.filter { item ->
            val isFavorite = when (item) {
                is ContentItem.ImageItem -> {
                    val result = favoriteManager.isFavorite(item.id)
                    Log.d("UserFragment", "检查图片点赞状态：${item.id} = $result")
                    result
                }
                is ContentItem.TextItem -> {
                    val result = favoriteManager.isFavorite(item.id)
                    Log.d("UserFragment", "检查文字点赞状态：${item.id} = $result")
                    result
                }
            }
            isFavorite
        }
        
        // 统计点赞内容的类型
        val likedImageItems = likedItems.filterIsInstance<ContentItem.ImageItem>()
        val likedTextItems = likedItems.filterIsInstance<ContentItem.TextItem>()
        Log.d("UserFragment", "点赞图片数量：${likedImageItems.size}，点赞文字数量：${likedTextItems.size}")
        
        adapter.setList(likedItems)
        Log.d("UserFragment", "数据已设置到适配器，点赞总数：${likedItems.size}")
        
        // 更新点赞数
        binding.likesCount.text = likedItems.size.toString()
    }

    override fun onFavoriteChanged(itemId: String, isFavorite: Boolean) {
        Log.d("UserFragment", "点赞状态改变：itemId=$itemId, isFavorite=$isFavorite")
        // 当点赞状态改变时，更新列表
        viewModel.contentItems.value?.let { items ->
            updateLikedItems(items)
        }
    }

    override fun onResume() {
        super.onResume()
        // 在页面恢复时更新列表
        viewModel.contentItems.value?.let { items ->
            updateLikedItems(items)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        favoriteManager.removeListener(this)
        _binding = null
    }
} 