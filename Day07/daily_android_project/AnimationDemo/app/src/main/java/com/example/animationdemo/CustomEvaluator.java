package com.example.animationdemo;

import android.animation.TypeEvaluator;

public class CustomEvaluator implements TypeEvaluator<Float> {
    @Override
    public Float evaluate(float fraction, Float startValue, Float endValue) {
        // 实现非线性变化
        return startValue + (endValue - startValue) * (float) Math.pow(fraction, 2);
    }
} 