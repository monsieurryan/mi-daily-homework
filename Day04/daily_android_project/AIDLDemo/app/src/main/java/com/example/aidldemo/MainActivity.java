package com.example.aidldemo;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    private ICalculator calculatorService;
    private ServiceConnection serviceConnection;
    private EditText number1, number2;
    private TextView result;
    private Button calculateButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // 初始化视图
        number1 = findViewById(R.id.number1);
        number2 = findViewById(R.id.number2);
        result = findViewById(R.id.result);
        calculateButton = findViewById(R.id.calculateButton);

        // 设置服务连接
        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                calculatorService = ICalculator.Stub.asInterface(service);
                Toast.makeText(MainActivity.this, "服务已连接", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                calculatorService = null;
                Toast.makeText(MainActivity.this, "服务已断开", Toast.LENGTH_SHORT).show();
            }
        };

        // 绑定服务（使用显式Intent）
        Intent intent = new Intent(this, CalculatorService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);

        // 设置计算按钮点击事件
        calculateButton.setOnClickListener(v -> {
            if (calculatorService != null) {
                try {
                    int a = Integer.parseInt(number1.getText().toString());
                    int b = Integer.parseInt(number2.getText().toString());
                    int sum = calculatorService.add(a, b);
                    result.setText("结果: " + sum);
                } catch (RemoteException e) {
                    Toast.makeText(MainActivity.this, "计算失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                } catch (NumberFormatException e) {
                    Toast.makeText(MainActivity.this, "请输入有效的数字", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(MainActivity.this, "服务未连接", Toast.LENGTH_SHORT).show();
            }
        });

        // 设置窗口边距
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (serviceConnection != null && calculatorService != null) {
            unbindService(serviceConnection);
        }
    }
}