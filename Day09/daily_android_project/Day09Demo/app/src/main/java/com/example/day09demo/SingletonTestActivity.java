package com.example.day09demo;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.day09demo.singleton.*;

public class SingletonTestActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singleton_test);

        TextView resultTextView = findViewById(R.id.resultTextView);
        StringBuilder result = new StringBuilder();

        // 测试饿汉式单例
        HungrySingleton hungry1 = HungrySingleton.getInstance();
        HungrySingleton hungry2 = HungrySingleton.getInstance();
        result.append("饿汉式单例测试：").append(hungry1 == hungry2).append("\n");

        // 测试懒汉式单例
        LazySingleton lazy1 = LazySingleton.getInstance();
        LazySingleton lazy2 = LazySingleton.getInstance();
        result.append("懒汉式单例测试：").append(lazy1 == lazy2).append("\n");

        // 测试双重检查锁单例
        DoubleCheckSingleton doubleCheck1 = DoubleCheckSingleton.getInstance();
        DoubleCheckSingleton doubleCheck2 = DoubleCheckSingleton.getInstance();
        result.append("双重检查锁单例测试：").append(doubleCheck1 == doubleCheck2).append("\n");

        // 测试静态内部类单例
        StaticInnerSingleton staticInner1 = StaticInnerSingleton.getInstance();
        StaticInnerSingleton staticInner2 = StaticInnerSingleton.getInstance();
        result.append("静态内部类单例测试：").append(staticInner1 == staticInner2).append("\n");

        // 测试枚举单例
        EnumSingleton enum1 = EnumSingleton.INSTANCE;
        EnumSingleton enum2 = EnumSingleton.INSTANCE;
        result.append("枚举单例测试：").append(enum1 == enum2).append("\n");

        resultTextView.setText(result.toString());
    }
} 