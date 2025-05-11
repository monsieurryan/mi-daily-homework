package com.example.animationdemo;

import android.view.animation.Interpolator;

public class CustomInterpolator implements Interpolator {
    @Override
    public float getInterpolation(float input) {
        // 实现弹跳效果
        return (float) (1 - Math.pow(2, -10 * input));
    }
} 