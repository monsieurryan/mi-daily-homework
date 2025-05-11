package com.example.brvahgallerydemo.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.brvahgallerydemo.WallpaperFragment
import com.example.brvahgallerydemo.MovieFragment
class MainPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> WallpaperFragment()
            1 -> MovieFragment()
            else -> throw IllegalArgumentException("Invalid position $position")
        }
    }
} 