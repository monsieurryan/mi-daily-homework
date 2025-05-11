package com.example.okhttpquerydemo

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.okhttpquerydemo.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val client = OkHttpClient()
    private val sharedPreferences by lazy { getSharedPreferences("HolidayData", Context.MODE_PRIVATE) }
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val displayFormat = SimpleDateFormat("yyyy年MM月dd日", Locale.getDefault())
    private val holidayAdapter = HolidayAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 设置RecyclerView
        binding.holidayRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = holidayAdapter
        }

        // 恢复保存的数据
        binding.countryCodeInput.setText(sharedPreferences.getString("countryCode", ""))
        binding.yearInput.setText(sharedPreferences.getString("year", ""))
        
        // 恢复保存的节日数据
        restoreHolidays()

        binding.queryButton.setOnClickListener {
            val countryCode = binding.countryCodeInput.text.toString()
            val year = binding.yearInput.text.toString()

            if (countryCode.isBlank() || year.isBlank()) {
                Toast.makeText(this, "请输入国家代码和年份", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 保存输入数据
            sharedPreferences.edit()
                .putString("countryCode", countryCode)
                .putString("year", year)
                .apply()

            queryHolidays(countryCode, year)
        }
    }

    private fun saveHolidays(holidays: List<Holiday>) {
        val holidayJsonArray = JSONArray()
        holidays.forEach { holiday ->
            val holidayJson = JSONObject().apply {
                put("date", holiday.date)
                put("localName", holiday.localName)
                put("name", holiday.name)
            }
            holidayJsonArray.put(holidayJson)
        }
        
        sharedPreferences.edit()
            .putString("holidays", holidayJsonArray.toString())
            .apply()
    }

    private fun restoreHolidays() {
        val holidaysJson = sharedPreferences.getString("holidays", null)
        if (holidaysJson != null) {
            try {
                val jsonArray = JSONArray(holidaysJson)
                val holidays = mutableListOf<Holiday>()
                
                for (i in 0 until jsonArray.length()) {
                    val holidayJson = jsonArray.getJSONObject(i)
                    holidays.add(Holiday(
                        date = holidayJson.getString("date"),
                        localName = holidayJson.getString("localName"),
                        name = holidayJson.getString("name")
                    ))
                }
                
                holidayAdapter.submitList(holidays)
            } catch (e: Exception) {
                Toast.makeText(this, "恢复历史数据失败", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun queryHolidays(countryCode: String, year: String) {
        lifecycleScope.launch {
            try {
                val startTime = System.currentTimeMillis()
                val response = withContext(Dispatchers.IO) {
                    val request = Request.Builder()
                        .url("https://date.nager.at/api/v3/publicholidays/$year/$countryCode")
                        .build()

                    client.newCall(request).execute()
                }

                if (!response.isSuccessful) {
                    throw Exception("网络请求失败: ${response.code}")
                }

                val endTime = System.currentTimeMillis()
                Toast.makeText(
                    this@MainActivity,
                    "网络请求耗时: ${endTime - startTime}ms",
                    Toast.LENGTH_SHORT
                ).show()

                val responseBody = response.body?.string() ?: throw Exception("响应体为空")
                val jsonArray = JSONArray(responseBody)
                val holidays = mutableListOf<Holiday>()
                
                for (i in 0 until jsonArray.length()) {
                    val holiday = jsonArray.getJSONObject(i)
                    val originalDate = holiday.getString("date")
                    val date = dateFormat.parse(originalDate)
                    val displayDate = date?.let { displayFormat.format(it) } ?: originalDate
                    
                    holidays.add(Holiday(
                        date = displayDate,
                        localName = holiday.getString("localName"),
                        name = holiday.getString("name")
                    ))
                }

                // 按原始日期字符串排序
                holidays.sortBy { dateFormat.parse(it.date.replace("年", "-").replace("月", "-").replace("日", "")) }

                // 保存节日数据
                saveHolidays(holidays)

                // 更新RecyclerView
                holidayAdapter.submitList(holidays)

            } catch (e: Exception) {
                Toast.makeText(this@MainActivity, "错误: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}