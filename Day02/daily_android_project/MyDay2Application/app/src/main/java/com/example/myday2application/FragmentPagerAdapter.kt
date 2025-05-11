// FragmentPagerAdapter.kt
package com.example.myday2application
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class FragmentPagerAdapter(activity: FragmentActivity, private val fragments: List<Fragment>) :
    FragmentStateAdapter(activity) {

    // 返回 Fragment 的数量
    override fun getItemCount(): Int = fragments.size

    // 根据位置返回对应的 Fragment
    override fun createFragment(position: Int): Fragment = fragments[position]
}