package com.example.brvahgallerydemo.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.brvahgallerydemo.HomeFragment
import com.example.brvahgallerydemo.UserFragment
class MainPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> HomeFragment()
            1 -> UserFragment()
            else -> throw IllegalArgumentException("Invalid position $position")
        }
    }
} 