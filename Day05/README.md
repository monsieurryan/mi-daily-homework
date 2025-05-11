# **第五次作业：Android权限与网络请求**

## **作业说明**

#### 完成一个国家假日查询工具

#### 核心功能
1. **输入界面**  
   - 支持输入国家代码（2位字母）和年份（4位数字）
   - 提供查询按钮触发数据获取

2. **数据查询**  
   - 通过指定API获取公共假日数据
   - 显示网络请求耗时及查询状态提示

3. **结果展示**  
   - 滚动列表显示假日名称、日期和类型
   - 保留最近一次查询结果（含输入内容）

#### 基本要求
- 支持离线查看历史记录
- 处理无效输入和网络异常
- 代码结构清晰且无内存泄漏

#### API示例
`https://date.nager.at/api/v3/publicholidays/2023/US`  
返回JSON数组格式的假日数据（含日期、名称、类型等字段）

---

#### **作业目录结构**

```markdown
Day04/  
├── README.md            # 本说明文档  
├── assets
│   ├── 作业-debug.apk      # 作业构建输出的apk
│   ├── 作业-演示视频.mov    # 作业功能演示视频（查询、暂存和打印耗时等功能展示） 
│   └── 作业-界面截图.png    # 作业界面截图
└── daily_android_project
    └── OkHttpQueryDemo     
        ├── app/
        │   ├── src/
        │   │   ├── main/
        │   │   │   ├── java/com/example/okhttpquerydemo/
        │   │   │   │   ├── MainActivity.kt       # 主界面和业务逻辑
        │   │   │   │   ├── Holiday.kt            # 节日数据模型类
        │   │   │   │   └── HolidayAdapter.kt     # RecyclerView适配器
        │   │   │   ├── res/
        │   │   │   │   ├── layout/
        │   │   │   │   │   ├── activity_main.xml # 主界面布局
        │   │   │   │   │   └── item_holiday.xml  # 假日列表项布局
        │   │   │   │   └── ... (其他资源文件)
        │   │   │   └── AndroidManifest.xml       # 应用清单文件
        │   ├── build.gradle.kts                  # 应用级构建配置
        └── build.gradle.kts                      # 项目级构建配置
```

---


## 实现细节

### 架构概览

应用基于单Activity架构，注重UI与数据操作的分离。核心组件包括：

- **MainActivity**：负责用户交互和UI更新
- **HolidayAdapter**：管理RecyclerView中假日数据的展示
- **SharedPreferences**：提供查询参数和结果的本地存储

### 关键技术实现

#### 1. 网络请求模块

使用OkHttp 4.12.0实现针对Nager.Date公共假日API的网络请求：

```kotlin
val request = Request.Builder()
    .url("https://date.nager.at/api/v3/publicholidays/$year/$countryCode")
    .build()

client.newCall(request).execute()
```

- 采用协程在IO调度器上执行网络操作，避免阻塞主线程
- 精确测量并显示网络请求耗时，提升用户体验
- 完整的异常处理和错误信息展示

#### 2. 数据持久化

通过SharedPreferences实现数据持久化：

```kotlin
// 保存输入数据
sharedPreferences.edit()
    .putString("countryCode", countryCode)
    .putString("year", year)
    .apply()

// 保存节日数据
sharedPreferences.edit()
    .putString("holidays", holidayJsonArray.toString())
    .apply()
```

- 存储用户最近查询的国家代码和年份
- 将假日数据转换为JSON格式存储，实现离线查看
- 应用启动时自动恢复上次查询结果

#### 3. UI实现

界面设计简洁直观：

- 采用水平LinearLayout排列查询条件输入区域
- 使用RecyclerView展示假日列表，支持滚动查看
- 通过ViewBinding简化视图操作：
  ```kotlin
  binding = ActivityMainBinding.inflate(layoutInflater)
  setContentView(binding.root)
  ```

#### 4. 日期处理

实现了从API日期格式到本地化显示的转换：

```kotlin
private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
private val displayFormat = SimpleDateFormat("yyyy年MM月dd日", Locale.getDefault())
```

- 接收ISO格式日期并转换为中文日期显示
- 按日期对假日数据进行排序，提供有序的查看体验

### 错误处理机制

全面的错误处理确保应用稳定性：

- 输入验证防止空查询：
  ```kotlin
  if (countryCode.isBlank() || year.isBlank()) {
      Toast.makeText(this, "请输入国家代码和年份", Toast.LENGTH_SHORT).show()
      return@setOnClickListener
  }
  ```
- 网络异常捕获与用户友好提示
- JSON解析异常保护，确保数据格式错误不会导致崩溃

### 性能优化

- 使用kotlinx-coroutines-android 1.7.3实现高效异步处理
- 网络操作与JSON解析在后台线程执行，保证UI响应性
- 通过数据本地持久化减少重复网络请求

### 技术栈总结

- Kotlin 1.9.22
- AndroidX核心库与生命周期组件
- OkHttp 4.12.0网络库
- Kotlin协程处理异步任务
- ViewBinding优化视图交互
- 最低支持Android 8.0（API 26）

---

### **提交信息**
**提交人**：易率  
**完成时间**：2025年3月20日  

---

