package com.example.brvahgallerydemo

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.brvahgallerydemo.adapter.MainPagerAdapter
import com.example.brvahgallerydemo.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewPager: ViewPager2
    private lateinit var bottomNav: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewPager = binding.viewPager
        bottomNav = binding.bottomNav

        // 设置ViewPager适配器
        viewPager.adapter = MainPagerAdapter(this)
        
        // 启用滑动
        viewPager.isUserInputEnabled = true
        
        // 设置页面切换动画
        viewPager.setPageTransformer { page, position ->
            page.alpha = 1f - kotlin.math.abs(position)
            page.translationX = page.width * position
        }

        // 设置ViewPager页面切换监听
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                bottomNav.menu.getItem(position).isChecked = true
            }
        })

        // 设置底部导航栏点击事件
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    viewPager.currentItem = 0
                    true
                }
                R.id.navigation_profile -> {
                    viewPager.currentItem = 1
                    true
                }
                else -> false
            }
        }
    }
}