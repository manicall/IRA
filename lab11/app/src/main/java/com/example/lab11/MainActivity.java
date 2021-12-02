package com.example.lab11;

/*
Программа имеет текстовое поле многострочного ввода и
    меню с режимами: Записать, прочитать, Настройки.
Настройки задают цвет фона текстового поля, цвет шрифта,
    размер шрифта, имя папки для записи, имя файла для записи.
В поле вводится текст и при нажатии меню записывается в файл или читается из файла.
Настройки сохраняются автоматически.
*/

/*
* рассказать:
*  1) про то как было создано и работает окно настроек
*  2) про key и defValue в PreferenceManager.getDefaultSharedPreferences
*  3) где найти файл настроек
*  4) про обработку исключений
*  5) про дополнительные разрешения в манифесте
* */

/*
* ответ:
*  1) правой кнопкой мыши на MainActivity -> new -> Activity -> Setting Activity -> finish
*  2)
*   метод getString класса SharedPreferences имеет два параметра:
*   * key - ключевое поле, которое можно задать в свойствах элементов настроек,
*   используется для извлечения значения настройки;
*   * defValue - используется вместо возвращаемого значения,
*   если файл с настройками еще не был создан.
*  3)
*   Чтобы открыть обозреватель файлов: View -> Tool Windows -> Device File Explorer
*   файл с настройками можно найти по директории:
*      /data/data/com.example.lab11/shared_prefs/com.example.lab11_preferences.xml
*  4) меня спросил делал ли я обработку исключения, если sd карта в устройстве отсутствует
*  5)
*   в файл манифеста AndroidManifest.xml
*   были добавлены команды:
        <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
        <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
*   чтобы разрешить чтение и запись файлов с устройства.
*
* */

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    // вызывается при возобновления MainActivity
    @Override
    protected void onResume() {
        setSettings();
        super.onResume();
    }

    // устанавливает настройки, которые были выбраны в соответствующем активити
    private void setSettings() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);

        EditText editText = findViewById(R.id.editText);
        // устанавливаем свойства текстового поля из настроек
        editText.setBackgroundColor(sp.getInt("background_color", Color.WHITE));
        editText.setTextColor(sp.getInt("font_color", Color.BLACK));
        setTextSize(editText, sp.getString("text_size", "14"));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuWrite:   write();   break;
            case R.id.menuRead:    read();    break;
            case R.id.menuSettings: settings(); break;
        }
        return true;
    }

    void write() {
        try {
            // создание файлового потока для записи
            FileOutputStream fos = new FileOutputStream (getPath());
            EditText editText = findViewById(R.id.editText);
            // запись из текстового поля в файл с преобразованием текста в массив байт
            fos.write(editText.getText().toString().getBytes(StandardCharsets.UTF_8));
            fos.close();
        // перехват ошибок
        } catch (FileNotFoundException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    void read() {
        try {
            // создание файлового потока для чтения
            FileInputStream fis = new FileInputStream(getPath());
            // создание массива байт для чтения информации из файла
            byte[] bytes = new byte[fis.available()];
            // чтение байт
            fis.read(bytes);
            fis.close();

            EditText editText = findViewById(R.id.editText);
            // преобразование массива байт к типу String
            // и вывод в текстовое поле
            editText.setText(new String(bytes));
        // перехват ошибок
        } catch (FileNotFoundException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    // получает файл из настроек
    private String getPath(){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        String folderName = sp.getString("folder_name", "/storage/sdcard1/Download/");
        String fileName = sp.getString("file_name", "defaultFile.txt");

        return folderName + fileName;
    }

    // вызывает активити с настройками
    private void settings() {
        startActivity(new Intent(this,SettingsActivity.class));
    }

    // устанавливает размер шрифта с проверкой на корректные данные в поле настроек
    private void setTextSize(EditText editText, String textSize){
        if (textSize.matches("^\\d+$")) { // если textSize это целое число
            editText.setTextSize(Integer.valueOf(textSize));
        } else {
            Toast.makeText(this,
                    "Размер шрифта должен быть целым числом",
                    Toast.LENGTH_SHORT).show();
        }
    }

}