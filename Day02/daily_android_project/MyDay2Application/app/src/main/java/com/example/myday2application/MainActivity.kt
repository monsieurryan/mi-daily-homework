// MainActivity.kt
package com.example.myday2application  // 确保包名正确
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.myday2application.FragmentPagerAdapter
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.example.myday2application.R

class MainActivity : AppCompatActivity() {
    private lateinit var viewPager: ViewPager2
    private lateinit var bottomNavigation: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        viewPager = findViewById(R.id.viewPager)
        bottomNavigation = findViewById(R.id.bottomNavigation)

        val fragments = listOf(Fragment1(), Fragment2(), Fragment3())
        viewPager.adapter = FragmentPagerAdapter(this, fragments)

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                bottomNavigation.menu.getItem(position).isChecked = true
            }
        })

        bottomNavigation.setOnItemSelectedListener {
            viewPager.currentItem = when (it.itemId) {
                R.id.nav_home -> 0
                R.id.nav_dashboard -> 1
                R.id.nav_notifications -> 2
                else -> 0
            }
            true
        }
    }
}