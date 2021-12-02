package com.example.lab12;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/*
* в манифест добавлены строки
* <uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW"/>
* <service android:name="com.example.lab12.SnowFlakeService" />
*
* */

public class MainActivity extends AppCompatActivity {
    Intent myService;
    boolean isRunning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myService = new Intent(this, ProverbService.class);

        Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isRunning) {
                    startService(myService);
                    button.setText("Остановить службу");
                } else {
                    stopService(myService);
                    button.setText("Запустить службу");
                }
                isRunning = !isRunning;
            }
        });

    }




}