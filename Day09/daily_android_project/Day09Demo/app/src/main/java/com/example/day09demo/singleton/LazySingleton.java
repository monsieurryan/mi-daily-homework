package com.example.day09demo.singleton;

/**
 * 懒汉式单例
 * 特点：
 * 1. 线程不安全
 * 2. 延迟加载，节省内存
 */
public class LazySingleton {
    private static LazySingleton instance;
    
    // 私有构造函数，防止外部创建实例
    private LazySingleton() {}
    
    // 提供公共访问方法
    public static LazySingleton getInstance() {
        if (instance == null) {
            instance = new LazySingleton();
        }
        return instance;
    }
} 