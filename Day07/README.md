# **第七次作业：动画**

## **作业说明**

**作业1：**  

使用补间动画，基于当前View中心点放大1.5倍，同时逆时针旋转720度，由不透明变为透明度0.8，持续2000ms，并且重复动画3次。

**要求：** 任选XML或Java方式实现，动画开始时打印日志“animation start”，动画重复时打印“animation repeat”以及重复了2次，动画结束时打印日志“animation end”。

**作业2：**  

实现属性动画，使用AnimatorSet，先是当前View围绕X轴旋转360度，持续1000ms；然后向右移动120px，持续1000ms；最后从不透明变成透明度0.5，持续500ms。  

**要求：** 使用Java方式实现，需要有2个基础动画同时执行，有1个顺序执行，且实现至少2种不同效果的自定义插值器与估值器。

**作业3：**  

使用ShapeDrawable实现圆角正方形效果，圆角为10dp，背景颜色从红色渐变到黄色，角度90度。  

**要求：** 使用XML和Java代码两种方式分别实现。

---

#### **作业目录结构**

```markdown
Day07/  
├── README.md           # 本说明文件
├── assets
│   ├── 作业-debug.apk      #作业构建生成的apk
│   ├── 任务一Log输出.png    #任务一的Logcat界面输出
│   ├── 作业要求.png
│   ├── 任务一演示视频.mov    #任务一演示视频
│   ├── 任务三演示视频.mov    #任务三演示视频
│   ├── 任务二演示视频.mov    #任务二演示视频
│   └── 程序界面展示截图.png
└── daily_android_project
    └── AnimationDemo
        ├── app/
        │   ├── build.gradle
        │   ├── src/
        │   │   ├── main/
        │   │   │   ├── AndroidManifest.xml
        │   │   │   ├── java/
        │   │   │   │   └── com/
        │   │   │   │       └── example/
        │   │   │   │           └── animationdemo/
        │   │   │   │               ├── MainActivity.java  # 主活动文件，包含动画实现
        │   │   │   │               └── CustomInterpolator.java  # 自定义插值器
        │   │   │   │               └── CustomEvaluator.java  # 自定义估值器
        │   │   │   ├── res/
        │   │   │   │   ├── layout/
        │   │   │   │   │   └── activity_main.xml  # 主活动的布局文件
        │   │   │   │   ├── drawable/
        │   │   │   │   │   └── gradient_shape.xml  # 渐变背景的XML文件
        │   │   │   │   └── values/
        │   │   │   │       └── strings.xml
        │   │   │   │       └── colors.xml
        │   │   │   │       └── styles.xml
        │   ├── proguard-rules.pro
        │   └── build.gradle
        ├── build.gradle
        └── settings.gradle
```

---


## 实现细节

#### 作业1：补间动画

**Java方式实现：**

在`MainActivity.java`中添加以下方法：

```java
private void setupTweenAnimation() {
    // 创建缩放动画
    ScaleAnimation scaleAnimation = new ScaleAnimation(
            1f, 1.5f, 1f, 1.5f,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
    );
    scaleAnimation.setDuration(2000);
    scaleAnimation.setRepeatCount(2);
    scaleAnimation.setRepeatMode(Animation.RESTART);

    // 创建旋转动画
    RotateAnimation rotateAnimation = new RotateAnimation(
            0f, -720f,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
    );
    rotateAnimation.setDuration(2000);
    rotateAnimation.setRepeatCount(2);
    rotateAnimation.setRepeatMode(Animation.RESTART);

    // 创建透明度动画
    AlphaAnimation alphaAnimation = new AlphaAnimation(1f, 0.8f);
    alphaAnimation.setDuration(2000);
    alphaAnimation.setRepeatCount(2);
    alphaAnimation.setRepeatMode(Animation.RESTART);

    // 创建动画集合
    AnimationSet animationSet = new AnimationSet(true);
    animationSet.addAnimation(scaleAnimation);
    animationSet.addAnimation(rotateAnimation);
    animationSet.addAnimation(alphaAnimation);

    // 添加动画监听器
    animationSet.setAnimationListener(new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {
            Log.d(TAG, "animation start");
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
            Log.d(TAG, "animation repeat");
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            Log.d(TAG, "animation end");
            startButton.setEnabled(true);
        }
    });

    // 开始动画
    animationView.startAnimation(animationSet);
}
```

#### 作业2：属性动画

**Java方式实现：**

在`MainActivity.java`中添加以下方法：

```java
private void setupPropertyAnimation() {
    // 创建X轴旋转动画
    ObjectAnimator rotationX = ObjectAnimator.ofFloat(animationView, "rotationX", 0f, 360f);
    rotationX.setDuration(1000);
    rotationX.setInterpolator(new AccelerateDecelerateInterpolator());

    // 创建水平移动动画
    ObjectAnimator translationX = ObjectAnimator.ofFloat(animationView, "translationX", 0f, 120f);
    translationX.setDuration(1000);
    translationX.setInterpolator(new CustomInterpolator());

    // 创建透明度动画
    ObjectAnimator alpha = ObjectAnimator.ofFloat(animationView, "alpha", 1f, 0.5f);
    alpha.setDuration(500);
    alpha.setInterpolator(new AccelerateDecelerateInterpolator());

    // 创建动画集合
    AnimatorSet animatorSet = new AnimatorSet();
    animatorSet.play(rotationX).with(translationX); // 旋转和移动同时进行
    animatorSet.play(alpha).after(translationX);   // 透明度动画在移动之后进行

    // 添加动画监听器
    animatorSet.addListener(new AnimatorListenerAdapter() {
        @Override
        public void onAnimationStart(Animator animation) {
            Log.d(TAG, "Animation start");
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            Log.d(TAG, "Animation end");
            startButton.setEnabled(true);
        }
    });

    // 开始动画
    animatorSet.start();
}
```

#### 作业3：圆角正方形

**XML方式实现：**

在`res/drawable`目录下创建`gradient_shape.xml`文件：

```xml
<shape xmlns:android="http://schemas.android.com/apk/res/android"
    android:shape="rectangle">
    <corners android:radius="10dp" />
    <gradient
        android:startColor="#FF0000"
        android:endColor="#FFFF00"
        android:angle="90" />
</shape>
```

**Java方式实现：**

在`MainActivity.java`中添加以下方法：

```java
private void setupShapeDrawable() {
    GradientDrawable gradientDrawable = new GradientDrawable();
    gradientDrawable.setShape(GradientDrawable.RECTANGLE);
    gradientDrawable.setCornerRadius(10 * getResources().getDisplayMetrics().density); // 10dp转换为像素
    gradientDrawable.setGradientType(GradientDrawable.LINEAR_GRADIENT);
    gradientDrawable.setOrientation(GradientDrawable.Orientation.LEFT_RIGHT);
    gradientDrawable.setColors(new int[]{Color.RED, Color.YELLOW});

    // 设置背景
    animationView.setBackground(gradientDrawable);
}
```


---

### **提交信息**
**提交人**：Ryan  
**完成时间**：2025年3月23日  

---

