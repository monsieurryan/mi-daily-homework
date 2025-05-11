# **第八次作业：自定义View**

## **作业说明**

1. 完成"标签云"控件，包括自定义属性
2. 实现View跟随手指滑动效果

---

#### **作业目录结构**

```markdown
Day08/  
├── README.md
├── assets
│   ├── 作业-debug.apk
│   ├── 作业要求.jpg
│   ├── 程序界面截图.png
│   └── 任务一和任务二演示视频.mov
└── daily_android_project
    └── TagCloudDemo
        ├── build.gradle.kts
        ├── settings.gradle.kts
        ├── app/
        │   ├── build.gradle.kts
        │   ├── src/
        │   │   ├── main/
        │   │   │   ├── AndroidManifest.xml
        │   │   │   ├── java/
        │   │   │   │   └── com/
        │   │   │   │       └── example/
        │   │   │   │           └── tagclouddemo/
        │   │   │   │               ├── MainActivity.kt  # 主活动
        │   │   │   │               └── TagCloudView.kt  # 自定义标签云视图
        │   │   │   ├── res/
        │   │   │   │   ├── layout/
        │   │   │   │   │   └── activity_main.xml  # 主活动布局
        │   │   │   │   ├── values/
        │   │   │   │   │   └── attrs.xml  # 自定义属性
        │   │   │   │   └── ...
        │   │   │   └── ...
        │   │   └── ...
        │   └── ...
        └── ...
```

---

## 实现细节

本作业将两个要求结合在一起写了一个可拖动排序的标签云

### 1. 完成“标签云”控件，包括自定义属性

`TagCloudView` 类是一个自定义视图，用于以云状布局显示一组标签。标签按行排列，每个标签可以通过填充、间距和文本大小进行自定义。

#### 关键方法和属性：

- **`layoutTags()`**：此方法计算标签的位置，并将它们存储在 `RectF` 对象的列表中。它确保标签按行排列，并在当前行已满时处理换行。

- **`onDraw(canvas: Canvas)`**：此方法负责在画布上绘制标签。它遍历标签列表，并在计算的位置绘制每个标签。如果适用，还会处理绘制占位符和被拖动的标签。

- **`onTouchEvent(event: MotionEvent)`**：此方法处理触摸事件以启用标签拖动。它检测标签何时被触摸、移动或释放，并相应地更新标签的位置。

- **自定义属性**：可以使用 XML 属性自定义视图的标签填充、间距、文本大小和背景颜色。

### 自定义属性

`TagCloudView` 类包含多个自定义属性，允许您自定义标签云的外观和行为。这些属性在 `res/values/attrs.xml` 文件中定义，并在 `TagCloudView` 类中使用。

#### 定义自定义属性

在 `res/values/attrs.xml` 文件中定义自定义属性：

```xml
<resources>
    <declare-styleable name="TagCloudView">
        <attr name="tagPadding" format="dimension" />
        <attr name="tagSpacing" format="dimension" />
        <attr name="tagTextSize" format="dimension" />
        <attr name="tagBackgroundColor" format="color" />
    </declare-styleable>
</resources>
```

#### 在 `TagCloudView` 中使用自定义属性

在 `TagCloudView` 类中，获取并使用这些自定义属性：

```kotlin
class TagCloudView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var tagPadding: Float = 0f
    private var tagSpacing: Float = 0f
    private var tagTextSize: Float = 0f
    private var tagBackgroundColor: Int = Color.LTGRAY

    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.TagCloudView,
            0, 0
        ).apply {
            try {
                tagPadding = getDimension(R.styleable.TagCloudView_tagPadding, 8f)
                tagSpacing = getDimension(R.styleable.TagCloudView_tagSpacing, 4f)
                tagTextSize = getDimension(R.styleable.TagCloudView_tagTextSize, 14f)
                tagBackgroundColor = getColor(R.styleable.TagCloudView_tagBackgroundColor, Color.LTGRAY)
            } finally {
                recycle()
            }
        }
    }

    // 其他方法和实现细节...
}
```

这些自定义属性实现通过 XML 配置 `TagCloudView` 中标签的填充、间距、文本大小和背景颜色。

### 2. 实现视图跟随手指滑动效果

`TagCloudView` 类还支持通过手指滑动效果拖动标签。当长按并拖动标签时，它会跟随手指在屏幕上的移动。

#### 关键方法和属性：

- **`onTouchEvent(event: MotionEvent)`**：此方法对于检测触摸事件和更新被拖动标签的位置至关重要。它处理三个主要操作：
  - **`ACTION_DOWN`**：检测初始触摸并检查是否触摸了某个标签。
  - **`ACTION_MOVE`**：在手指移动时更新被拖动标签的位置。
  - **`ACTION_UP`**：完成拖动操作，可能会在新位置插入标签。

- **`findTagAtPosition(x: Float, y: Float): Int`**：此方法检查触摸事件是否发生在任何标签的边界内，并返回被触摸标签的索引。

- **`findInsertPosition(x: Float, y: Float): Int`**：此方法根据当前触摸坐标计算最近的插入位置。

- **`updatePlaceholderRect()`**：此方法更新占位符矩形，以指示被拖动标签将插入的位置。

---

### **提交信息**

**提交人**：Ryan  
**完成时间**：2025年3月24日  

---

