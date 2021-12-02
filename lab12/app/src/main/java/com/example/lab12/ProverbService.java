package com.example.lab12;

import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.TextView;

public class ProverbService extends Service {
    private WindowManager windowManager;
    TextView textView;
    Button button;
    Thread thread;
    Handler h;
    public void onCreate() {
        super.onCreate();

        textView = new TextView(getApplicationContext());
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        textView.setText(Proverbs.getRandomProverb());
        textView.setTextSize(20);
        textView.setTextColor(Color.CYAN);
        addView(textView,Gravity.CENTER);

        button = new Button(getApplicationContext());
        button.setText("Остановить службу");
        addView(button, Gravity.RIGHT | Gravity.TOP);

        button.setOnClickListener(view -> stopSelf());

        h = new Handler() {
            public void handleMessage(android.os.Message msg) {
                // обновляем TextView
                textView.setText(Proverbs.getRandomProverb());
            };
        };

        Thread t = new Thread(new Runnable() {
            public void run() {
                while (true) {
                    // долгий процесс
                    SystemClock.sleep(1000 + rnd(1, 10) * 100);
                    h.sendEmptyMessage(0);
                }
            }
        });
        t.start();
    }

    private void addView(View view, int gravity){
        int LAYOUT_FLAG;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_PHONE;
        }

        final WindowManager.LayoutParams myParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                LAYOUT_FLAG,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        myParams.gravity = gravity;

        windowManager.addView(view, myParams);
    }

    // функция возвращаяющая случайное число в заданном диапазоне
    private int rnd(int min, int max) {
        max -= min;
        return ((int) (Math.random() * ++max) + min);
    }

    // уничтожение потока и элементов управления
    // при завершении работы службы
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (thread != null) {
            Thread dummy = thread;
            thread = null;
            dummy.interrupt();
        }
        windowManager.removeView(textView);
        windowManager.removeView(button);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }
}