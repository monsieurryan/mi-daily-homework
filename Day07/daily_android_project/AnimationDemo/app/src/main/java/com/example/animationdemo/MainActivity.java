package com.example.animationdemo;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private ImageView animationView;
    private Button startButton;
    private RadioGroup animationTypeGroup;
    private RadioGroup implementationTypeGroup;
    private AnimatorSet animatorSet;
    private int repeatCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        animationView = findViewById(R.id.animationView);
        startButton = findViewById(R.id.startButton);
        animationTypeGroup = findViewById(R.id.animationTypeGroup);
        implementationTypeGroup = findViewById(R.id.implementationTypeGroup);
        
        // 设置动画类型选择监听器
        animationTypeGroup.setOnCheckedChangeListener((group, checkedId) -> {
            // 当选择动画3时显示实现方式选择
            implementationTypeGroup.setVisibility(
                checkedId == R.id.animation3Radio ? View.VISIBLE : View.GONE
            );
        });
        
        // 设置按钮点击事件
        startButton.setOnClickListener(v -> {
            startButton.setEnabled(false); // 动画开始时禁用按钮
            resetViewState(); // 重置View状态
            repeatCount = 0; // 重置重复计数
            
            // 根据选择的动画类型执行不同的动画
            if (animationTypeGroup.getCheckedRadioButtonId() == R.id.animation1Radio) {
                setupAnimation1();
            } else if (animationTypeGroup.getCheckedRadioButtonId() == R.id.animation2Radio) {
                setupAnimation2();
            } else {
                setupAnimation3();
            }
        });
    }

    private void resetViewState() {
        // 重置所有动画属性到初始状态
        animationView.setRotationX(0f);
        animationView.setRotation(0f);
        animationView.setScaleX(1f);
        animationView.setScaleY(1f);
        animationView.setTranslationX(0f);
        animationView.setAlpha(1f);
        // 重置背景为默认颜色
        animationView.setBackgroundColor(Color.parseColor("#FF4081"));
    }

    private void setupAnimation1() {
        // 创建缩放动画
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(animationView, "scaleX", 1f, 1.5f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(animationView, "scaleY", 1f, 1.5f);

        // 创建旋转动画
        ObjectAnimator rotation = ObjectAnimator.ofFloat(animationView, "rotation", 0f, -720f);

        // 创建透明度动画
        ObjectAnimator alpha = ObjectAnimator.ofFloat(animationView, "alpha", 1f, 0.8f);

        // 创建动画集合
        animatorSet = new AnimatorSet();
        animatorSet.playTogether(scaleX, scaleY, rotation, alpha);
        animatorSet.setDuration(2000);
        animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());

        // 添加动画监听器
        animatorSet.addListener(new android.animation.AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(android.animation.Animator animation) {
                Log.d(TAG, "Animation1 start");
            }

            @Override
            public void onAnimationEnd(android.animation.Animator animation) {
                if (repeatCount < 2) {
                    repeatCount++;
                    Log.d(TAG, "Animation1 repeat " + repeatCount);
                    animation.start();
                } else {
                    Log.d(TAG, "Animation1 end");
                    startButton.setEnabled(true);
                }
            }
        });

        // 开始动画
        animatorSet.start();
    }

    private void setupAnimation2() {
        // 创建X轴旋转动画
        ObjectAnimator rotationX = ObjectAnimator.ofFloat(animationView, "rotationX", 0f, 360f);
        rotationX.setDuration(1000);
        rotationX.setInterpolator(new CustomInterpolator());

        // 创建水平移动动画
        ObjectAnimator translationX = ObjectAnimator.ofFloat(animationView, "translationX", 0f, 120f);
        translationX.setDuration(1000);
        translationX.setEvaluator(new CustomEvaluator());

        // 创建透明度动画
        ObjectAnimator alpha = ObjectAnimator.ofFloat(animationView, "alpha", 1f, 0.5f);
        alpha.setDuration(500);
        alpha.setInterpolator(new AccelerateDecelerateInterpolator());

        // 创建动画集合
        animatorSet = new AnimatorSet();
        
        // 设置动画顺序
        animatorSet.play(rotationX).with(translationX); // 旋转和移动同时进行
        animatorSet.play(alpha).after(translationX);     // 透明度动画在移动之后进行

        // 添加动画监听器
        animatorSet.addListener(new android.animation.AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(android.animation.Animator animation) {
                Log.d(TAG, "Animation 2 start");
            }

            @Override
            public void onAnimationEnd(android.animation.Animator animation) {
                Log.d(TAG, "Animation 2 end");
                startButton.setEnabled(true);
            }
        });

        // 开始动画
        animatorSet.start();
    }

    private void setupAnimation3() {
        // 根据选择的实现方式设置背景
        if (implementationTypeGroup.getCheckedRadioButtonId() == R.id.xmlImplementationRadio) {
            // 方式1：使用XML方式实现渐变背景
            animationView.setBackgroundResource(R.drawable.gradient_shape);
        } else {
            // 方式2：使用Java代码实现渐变背景
            GradientDrawable gradientDrawable = new GradientDrawable();
            gradientDrawable.setShape(GradientDrawable.RECTANGLE);
            gradientDrawable.setCornerRadius(10 * getResources().getDisplayMetrics().density); // 10dp转换为像素
            gradientDrawable.setGradientType(GradientDrawable.LINEAR_GRADIENT);
            gradientDrawable.setGradientCenter(0.5f, 0.5f);
            gradientDrawable.setGradientRadius(0f);
            gradientDrawable.setColors(new int[]{
                    Color.RED,
                    Color.YELLOW
            });

            // 设置背景
            animationView.setBackground(gradientDrawable);
        }

        // 动画结束后启用按钮
        startButton.setEnabled(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (animatorSet != null) {
            animatorSet.cancel(); // 取消动画，避免内存泄漏
        }
    }
} 