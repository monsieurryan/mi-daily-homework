# **第二次作业：Fragment**

#### **作业说明**

本次作业要求使用 Android 开发中的 Fragment 和 ViewPager 组件搭建一个包含多个页面的应用。任务包括实现一个首页 Activity，该页面包含多个 Fragment，并实现底部 Tab 切换和 Fragment 左右滑动功能。

具体要求如下：

​	1.	**搭建App首页**，一个 Activity 中包含多个 Fragment，点击底部 Tab 切换 Fragment，每个 Fragment 中显示一个简单的文本。

​	2.	**点击按钮跳转到另一个Fragment**，并在该 Fragment 中显示不同内容。

​	3.	使用 **ViewPager** 实现左右滑动切换 Fragment。

---

#### **作业目录结构**

```markdown
Day02/  
├── daily_android_project/      
│   └── MyDay2Application/      # Android Studio 项目源码  
│       ├── app/  
│       │    ├── src/main/  
│       │    │    ├── java/com/example/myday2application/  
│       │    │    │   ├── MainActivity.kt  # 主逻辑代码  
│       │    │    │   ├── Fragment1.kt    # Fragment1 代码  
│       │    │    │   ├── Fragment2.kt    # Fragment2 代码  
│       │    │    │   ├── Fragment3.kt    # Fragment3 代码  
│       │    │    │   └── FragmentPagerAdapter.kt    # FPA 代码 
│       │    │    └── res/                # 布局与资源文件  
│       │    │         ├── layout/activity_main.xml  
│       │    │         ├── layout/fragment1.xml  
│       │    │         ├── layout/fragment2.xml  
│       │    │         ├── layout/fragment3.xml  
│       │    │         └── menu/bottom_nav_menu.xml  
│       ├── build.gradle.kts   # 项目构建配置  
│       └── ...  
├── assets/          # 作业资产目录  
│   ├── 作业2-debug.apk      # 作业构建输出的apk
│   └── 作业2-演示视频.mov      # 演示视频
└── README.md        # 本说明文档  
```

---

**作业完成情况**

​	1.	**搭建并配置 Android Studio 环境**

​	•	创建了一个新的 Android 项目并成功配置。

​	•	实现了基本的界面布局和 Fragment 切换功能。

​	2.	**Fragment切换与按钮跳转**

​	•	使用 Fragment 组件展示不同的界面，每个 Fragment 显示一个文本内容。

​	•	在每个 Fragment 中添加按钮，点击按钮可跳转到下一个 Fragment。

​	3.	**使用 ViewPager 实现 Fragment 左右滑动切换**

​	•	通过 ViewPager2 实现了横向滑动切换 Fragment，并同步更新底部 Tab 的选中状态。

​	4.	**底部导航与 Tab 切换**

​	•	在底部实现了 BottomNavigationView，用户可以通过点击底部 Tab 切换不同的 Fragment。

​	5.	**代码提交与 GitLab 仓库管理**

​	•	完成了作业代码的编写，并将项目代码成功推送至个人 GitLab 仓库，确保作业提交的完整性。

​	6.	**模拟器运行验证**

​	•	使用模拟器进行了测试，验证了应用的基本功能，包括底部导航、Fragment 切换和按钮跳转。

---

**提交人**：Ryan

**完成时间：** 2025年3月18日

----

