package com.example.brvahgallerydemo

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.brvahgallerydemo.adapter.MovieAdapter
import com.example.brvahgallerydemo.viewmodel.MovieViewModel

class MovieFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MovieAdapter
    private lateinit var viewModel: MovieViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("MovieFragment", "onCreateView")
        return inflater.inflate(R.layout.fragment_movie, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("MovieFragment", "onViewCreated")

        setupRecyclerView(view)
        setupViewModel()
        viewModel.loadMovies()
    }

    private fun setupRecyclerView(view: View) {
        Log.d("MovieFragment", "设置RecyclerView")
        recyclerView = view.findViewById(R.id.movieRecyclerView)
        adapter = MovieAdapter()
        
        recyclerView.apply {
            layoutManager = GridLayoutManager(context, 2)
            adapter = this@MovieFragment.adapter
        }

        adapter.setOnItemClickListener { _, _, position ->
            val movie = adapter.data[position]
            startActivity(DetailActivity.newIntent(requireContext(), movie))
        }
    }

    private fun setupViewModel() {
        Log.d("MovieFragment", "设置ViewModel")
        viewModel = ViewModelProvider(this)[MovieViewModel::class.java]
        
        viewModel.movies.observe(viewLifecycleOwner) { movies ->
            Log.d("MovieFragment", "收到 ${movies.size} 部电影数据")
            adapter.setList(movies)
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            // 这里可以添加加载指示器的显示/隐藏逻辑
            Log.d("MovieFragment", "加载状态: $isLoading")
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            }
        }
    }

    companion object {
        fun newInstance() = MovieFragment()
    }
} 