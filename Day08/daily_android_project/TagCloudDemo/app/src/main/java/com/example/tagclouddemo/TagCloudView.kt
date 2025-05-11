package com.example.tagclouddemo

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import kotlin.math.pow
import kotlin.math.sqrt

class TagCloudView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val rect = RectF()
    
    private var tagTextColor: Int = Color.BLACK
    private var tagBackgroundColor: Int = Color.LTGRAY
    private var tagTextSize: Float = 16f
    private var tagPadding: Float = 16f
    private var tagSpacing: Float = 8f
    private var tagCornerRadius: Float = 8f
    
    private var tags: List<String> = emptyList()
    private var tagRects: List<RectF> = emptyList()
    
    // 拖拽相关变量
    private var draggedTagIndex: Int = -1
    private var draggedTagRect: RectF? = null
    private var draggedTag: String? = null
    private var placeholderRect: RectF? = null
    private var placeholderIndex: Int = -1
    private var lastTouchX: Float = 0f
    private var lastTouchY: Float = 0f
    private var longPressTimeout: Long = 500 // 长按时间阈值（毫秒）
    private var longPressStartTime: Long = 0
    private var isLongPress: Boolean = false

    init {
        context.obtainStyledAttributes(attrs, R.styleable.TagCloudView).apply {
            try {
                tagTextColor = getColor(R.styleable.TagCloudView_tagTextColor, Color.BLACK)
                tagBackgroundColor = getColor(R.styleable.TagCloudView_tagBackgroundColor, Color.LTGRAY)
                tagTextSize = getDimension(R.styleable.TagCloudView_tagTextSize, 16f)
                tagPadding = getDimension(R.styleable.TagCloudView_tagPadding, 16f)
                tagSpacing = getDimension(R.styleable.TagCloudView_tagSpacing, 8f)
                tagCornerRadius = getDimension(R.styleable.TagCloudView_tagCornerRadius, 8f)
            } finally {
                recycle()
            }
        }

        textPaint.apply {
            textSize = tagTextSize
            color = tagTextColor
        }

        // 设置View属性，确保可以绘制到边界外
        setLayerType(LAYER_TYPE_HARDWARE, null)
    }

    fun setTags(newTags: List<String>) {
        tags = newTags
        Log.d("TagCloudView", "设置标签: $newTags")
        requestLayout()
        layoutTags() // 立即重新布局
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desiredWidth = paddingLeft + paddingRight
        val desiredHeight = paddingTop + paddingBottom + calculateTotalHeight()

        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        val width = when (widthMode) {
            MeasureSpec.EXACTLY -> widthSize
            MeasureSpec.AT_MOST -> minOf(desiredWidth, widthSize)
            else -> desiredWidth
        }

        val height = when (heightMode) {
            MeasureSpec.EXACTLY -> heightSize
            MeasureSpec.AT_MOST -> minOf(desiredHeight, heightSize)
            else -> desiredHeight
        }

        Log.d("TagCloudView", "测量尺寸: width=$width, height=$height")
        setMeasuredDimension(width, height)
    }

    private fun calculateTotalHeight(): Int {
        if (tags.isEmpty()) return 0
        
        var currentX = paddingLeft.toFloat()
        var currentY = paddingTop.toFloat()
        var maxHeightInRow = 0f
        var totalHeight = 0f

        tags.forEach { tag ->
            val tagWidth = textPaint.measureText(tag) + tagPadding * 2
            val tagHeight = tagTextSize + tagPadding * 2

            if (currentX + tagWidth > width - paddingRight) {
                currentX = paddingLeft.toFloat()
                currentY += maxHeightInRow + tagSpacing
                maxHeightInRow = 0f
            }

            currentX += tagWidth + tagSpacing
            maxHeightInRow = maxOf(maxHeightInRow, tagHeight)
            totalHeight = currentY + maxHeightInRow
        }

        return totalHeight.toInt()
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        if (changed) {
            layoutTags() // 恢复这行，确保初始布局正确执行
        }
    }

    private fun layoutTags() {
        val tagRects = mutableListOf<RectF>()
        var currentX = paddingLeft.toFloat()
        var currentY = paddingTop.toFloat()
        var maxHeightInRow = 0f

        tags.forEach { tag ->
            val tagWidth = textPaint.measureText(tag) + tagPadding * 2
            val tagHeight = tagTextSize + tagPadding * 2

            if (currentX + tagWidth > width - paddingRight) {
                currentX = paddingLeft.toFloat()
                currentY += maxHeightInRow + tagSpacing
                maxHeightInRow = 0f
            }

            tagRects.add(RectF(
                currentX,
                currentY,
                currentX + tagWidth,
                currentY + tagHeight
            ))

            currentX += tagWidth + tagSpacing
            maxHeightInRow = maxOf(maxHeightInRow, tagHeight)
        }

        this.tagRects = tagRects
        Log.d("TagCloudView", "布局标签: ${tagRects.size}个标签")
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        
        // 保存画布状态
        canvas.save()
        
        // 绘制所有标签
        tags.forEachIndexed { index, tag ->
            if (index != draggedTagIndex) {
                val tagRect = tagRects[index]
                drawTag(canvas, tag, tagRect)
            }
        }
        
        // 绘制占位符
        placeholderRect?.let { rect ->
            paint.color = Color.LTGRAY
            paint.alpha = 128
            canvas.drawRoundRect(rect, tagCornerRadius, tagCornerRadius, paint)
        }
        
        // 恢复画布状态
        canvas.restore()
        
        // 绘制被拖动的标签（使用独立图层）
        draggedTagRect?.let { rect ->
            draggedTag?.let { tag ->
                // 创建一个足够大的图层来容纳拖动的标签
                val layerBounds = RectF(
                    rect.left - tagPadding,
                    rect.top - tagPadding,
                    rect.right + tagPadding,
                    rect.bottom + tagPadding
                )
                
                // 保存图层状态
                val layerId = canvas.saveLayer(
                    layerBounds,
                    null,
                    Canvas.ALL_SAVE_FLAG
                )
                
                // 在独立图层上绘制标签
                drawTag(canvas, tag, rect)
                
                // 恢复图层状态
                canvas.restoreToCount(layerId)
            }
        }
    }
    
    private fun drawTag(canvas: Canvas, tag: String, rect: RectF) {
        // 绘制背景
        paint.color = tagBackgroundColor
        paint.alpha = 255
        canvas.drawRoundRect(rect, tagCornerRadius, tagCornerRadius, paint)
        
        // 绘制文本
        val textX = rect.left + tagPadding
        val textY = rect.top + tagPadding + tagTextSize
        canvas.drawText(tag, textX, textY, textPaint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                lastTouchX = event.x
                lastTouchY = event.y
                longPressStartTime = System.currentTimeMillis()
                isLongPress = false
                
                // 检查是否点击了某个标签
                draggedTagIndex = findTagAtPosition(event.x, event.y)
                if (draggedTagIndex != -1) {
                    draggedTag = tags[draggedTagIndex]
                    draggedTagRect = RectF(tagRects[draggedTagIndex])
                    // 设置拖动标签的初始位置
                    draggedTagRect?.offsetTo(event.x - draggedTagRect!!.width() / 2,
                                          event.y - draggedTagRect!!.height() / 2)
                    return true
                }
                return false
            }
            
            MotionEvent.ACTION_MOVE -> {
                if (draggedTagIndex != -1) {
                    // 检查是否达到长按时间
                    if (!isLongPress && System.currentTimeMillis() - longPressStartTime > longPressTimeout) {
                        isLongPress = true
                    }
                    
                    // 更新拖动标签位置，不受View边界限制
                    val dx = event.x - lastTouchX
                    val dy = event.y - lastTouchY
                    draggedTagRect?.offset(dx, dy)
                    lastTouchX = event.x
                    lastTouchY = event.y
                    
                    // 检查是否在某个位置停留足够长时间
                    if (isLongPress) {
                        placeholderIndex = findInsertPosition(event.x, event.y)
                        if (placeholderIndex != -1) {
                            updatePlaceholderRect()
                        }
                    }
                    
                    invalidate()
                    return true
                }
                return false
            }
            
            MotionEvent.ACTION_UP -> {
                if (draggedTagIndex != -1) {
                    if (placeholderIndex != -1) {
                        // 在占位符位置插入标签
                        val newTags = tags.toMutableList()
                        val draggedTag = newTags.removeAt(draggedTagIndex)
                        newTags.add(placeholderIndex, draggedTag)
                        setTags(newTags)
                    }
                    
                    // 重置所有状态
                    draggedTagIndex = -1
                    draggedTag = null
                    draggedTagRect = null
                    placeholderIndex = -1
                    placeholderRect = null
                    invalidate()
                    return true
                }
                return false
            }
        }
        return super.onTouchEvent(event)
    }
    
    private fun findTagAtPosition(x: Float, y: Float): Int {
        return tagRects.indexOfFirst { rect ->
            rect.contains(x, y)
        }
    }
    
    private fun findInsertPosition(x: Float, y: Float): Int {
        // 找到最近的标签位置
        var minDistance = Float.MAX_VALUE
        var insertIndex = -1
        
        tagRects.forEachIndexed { index, rect ->
            val centerX = rect.left + rect.width() / 2
            val centerY = rect.top + rect.height() / 2
            val distance = sqrt(
                (x - centerX).pow(2) + (y - centerY).pow(2)
            )
            
            if (distance < minDistance) {
                minDistance = distance
                insertIndex = index
            }
        }
        
        return insertIndex
    }
    
    private fun updatePlaceholderRect() {
        if (placeholderIndex >= 0 && placeholderIndex < tagRects.size) {
            placeholderRect = RectF(tagRects[placeholderIndex])
        }
    }
} 