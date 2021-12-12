package com.example.lab13;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;

public class ThirdActivity extends AppCompatActivity {

    private SensorManager sensorManager;//Объект для работы с датчиком
    private boolean color = false;//Индикатор текущего цвета: false-зеленый, true - красный.
    private View view;//Ссылка на TextView
    private long lastUpdate;//Время последнего изменения состояния датчика

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third);

        //Создаем объект, для работы с датчиками
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        //Регистрируем класс, где будет реализован метод, вызываевый при изменении
        //состояния датчика.
        sensorManager.registerListener(listenerLight,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);

        lastUpdate = System.currentTimeMillis();
    }

    SensorEventListener listenerLight = new SensorEventListener() {

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                float[] values = event.values;

                // проекции ускорения на оси системы координат
                float x = values[0];
                float y = values[1];
                float z = values[2];

                // квадрат модуля ускорения телефона, деленный на квадрат
                //ускорения свободного падения
                float accelationSquareRoot = (x * x + y * y + z * z)
                        / (SensorManager.GRAVITY_EARTH * SensorManager.GRAVITY_EARTH);

                //Текущее время
                long actualTime = System.currentTimeMillis();

                //Если тряска сильная
                if (accelationSquareRoot >= 2) {
                    if (actualTime - lastUpdate < 200) {
                        //Если с момента начала тряски прошло меньше 200
                        // миллисекунд - выходим из обработчика
                        return;
                    }

                    finish();

                }
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();

        //Регистрируем класс, где будет реализован метод, вызываевый при изменении
        //состояния датчика.
        sensorManager.registerListener(listenerLight,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(listenerLight);
    }

}