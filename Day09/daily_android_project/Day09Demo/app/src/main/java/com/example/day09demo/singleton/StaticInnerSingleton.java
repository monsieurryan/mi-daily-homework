package com.example.day09demo.singleton;

/**
 * 静态内部类单例
 * 特点：
 * 1. 线程安全
 * 2. 延迟加载
 * 3. 利用JVM类加载机制保证线程安全
 */
public class StaticInnerSingleton {
    // 私有构造函数，防止外部创建实例
    private StaticInnerSingleton() {}
    
    // 静态内部类
    private static class SingletonHolder {
        private static final StaticInnerSingleton INSTANCE = new StaticInnerSingleton();
    }
    
    // 提供公共访问方法
    public static StaticInnerSingleton getInstance() {
        return SingletonHolder.INSTANCE;
    }
} 