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
import com.example.brvahgallerydemo.databinding.FragmentHomeBinding
import com.example.brvahgallerydemo.viewmodel.HomeViewModel

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeViewModel by viewModels()
    private lateinit var adapter: ContentAdapter
    private lateinit var favoriteManager: FavoriteManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d("HomeFragment", "onCreateView")
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("HomeFragment", "onViewCreated")
        setupToolbar()
        setupRecyclerView()
        setupSwipeRefresh()
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
        Log.d("HomeFragment", "setupRecyclerView")
        adapter = ContentAdapter()
        favoriteManager = FavoriteManager.getInstance(requireContext())
        adapter.setFavoriteManager(favoriteManager)
        
        binding.recyclerView.apply {
            layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            adapter = this@HomeFragment.adapter
            Log.d("HomeFragment", "RecyclerView adapter set")
        }
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.refreshData()
        }
    }

    private fun observeData() {
        Log.d("HomeFragment", "observeData")
        viewModel.contentItems.observe(viewLifecycleOwner) { items ->
            Log.d("HomeFragment", "收到数据更新，数量：${items.size}")
            adapter.setList(items)
            Log.d("HomeFragment", "数据已设置到适配器")
            binding.swipeRefreshLayout.isRefreshing = false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 