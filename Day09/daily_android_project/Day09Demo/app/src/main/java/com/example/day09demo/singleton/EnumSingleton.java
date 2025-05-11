package com.example.day09demo.singleton;

/**
 * 枚举单例
 * 特点：
 * 1. 线程安全
 * 2. 自动支持序列化机制
 * 3. 防止反射攻击
 * 4. 最简单的单例实现方式
 */
public enum EnumSingleton {
    INSTANCE;
    
    // 可以添加其他方法
    public void doSomething() {
        // 实现具体业务逻辑
    }
} 