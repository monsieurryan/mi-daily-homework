package com.example.day09demo.singleton;

/**
 * 双重检查锁单例
 * 特点：
 * 1. 线程安全
 * 2. 延迟加载
 * 3. 使用volatile关键字保证多线程下的可见性
 */
public class DoubleCheckSingleton {
    private static volatile DoubleCheckSingleton instance;
    
    // 私有构造函数，防止外部创建实例
    private DoubleCheckSingleton() {}
    
    // 提供公共访问方法
    public static DoubleCheckSingleton getInstance() {
        if (instance == null) {
            synchronized (DoubleCheckSingleton.class) {
                if (instance == null) {
                    instance = new DoubleCheckSingleton();
                }
            }
        }
        return instance;
    }
} 