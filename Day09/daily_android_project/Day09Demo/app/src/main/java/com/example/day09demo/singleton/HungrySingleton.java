package com.example.day09demo.singleton;

/**
 * 饿汉式单例
 * 特点：
 * 1. 线程安全
 * 2. 类加载时就创建实例，可能会造成不必要的内存浪费
 */
public class HungrySingleton {
    // 在类加载时就创建实例
    private static final HungrySingleton instance = new HungrySingleton();
    
    // 私有构造函数，防止外部创建实例
    private HungrySingleton() {}
    
    // 提供公共访问方法
    public static HungrySingleton getInstance() {
        return instance;
    }
} 