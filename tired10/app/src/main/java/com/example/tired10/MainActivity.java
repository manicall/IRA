package com.example.tired10;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tired10.R;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ArrayList<Car> cars = new ArrayList<>();
    private ArrayList<String> marks = new ArrayList<>();
    private ArrayList<View> informationFields = new ArrayList<>();

    private int currentCarId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // открытие базы данных
        SQLiteDatabase db =
                getBaseContext().openOrCreateDatabase("app.db", MODE_PRIVATE, null);
        // создание записей в базе данных
        addRecords(db);
        // выборка всех записей из таблицы student
        Cursor carCursor = db.rawQuery("SELECT * FROM cars;", null);

        // добавление записей в список
        carCursor.moveToFirst();
        while(!carCursor.isAfterLast()){
            // добавление имени студента
            cars.add(new Car(
                    carCursor.getInt(0),
                    carCursor.getString(1),
                    carCursor.getString(2),
                    carCursor.getBlob(3)));
            carCursor.moveToNext();
        }
        carCursor.close();

        TextView startPrice = findViewById(R.id.startPrice);
        TextView endPrice = findViewById(R.id.endPrice);



        setTextViews();

        hideInformations();
        showInformations();

        setCarPicture(cars.get(0).getPicture());

        marks.add("Test");
        Spinner spinner = (Spinner) findViewById(R.id.markSpinner);
        spinnerSetAdapter(spinner);
    }

    private void setTextViews() {
        informationFields.add(findViewById(R.id.markName));
        informationFields.add(findViewById(R.id.markValue));
        informationFields.add(findViewById(R.id.priceName));
        informationFields.add(findViewById(R.id.priceValue));
        informationFields.add(findViewById(R.id.carPicture));
        informationFields.add(findViewById(R.id.buttonPrevious));
        informationFields.add(findViewById(R.id.buttonNext));
    }

    private void hideInformations() {
        for (View informationField : informationFields)
            informationField.setVisibility(View.INVISIBLE);
    }

    private void showInformations() {
        for (View informationField : informationFields)
            informationField.setVisibility(View.VISIBLE);
    }

    private void spinnerSetAdapter(Spinner spinner) {
        //Создаем адаптер ArrayAdapter с помощью массива строк и стандартной разметки элемета spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(spinner.getContext(),
                android.R.layout.simple_spinner_item, marks);
        // Определяем разметку для использования при выборе элемента
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Применяем адаптер к элементу spinner
        spinner.setAdapter(adapter);
    }

    private void createTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS cars (" +
                "id INTEGER PRIMARY KEY, " +
                "mark TEXT NOT NULL, " +
                "price TEXT NOT NULL, " +
                "picture BLOB)");
    }



    private void addCar(SQLiteDatabase db, String mark, String price, byte[] picture) {
        ContentValues cv = new  ContentValues();
        cv.put("mark",  "'" + mark + "'");
        cv.put("price", "'" + price + " руб.'");
        cv.put("picture", picture);

        db.insert("cars", null, cv );
    }

    private void setDataFromCursor() {
        /*cars.get(0);
        textName.setText(name);
        textDescription.setText(description);
        Bitmap bitmapPhoto = BitmapFactory.decodeByteArray(photo, 8, photo.length - 8);
        photoImage.setImageBitmap(bitmapPhoto);
        */
    }

    // создаем записи таблицы
    private void addRecords(SQLiteDatabase db) {
        db.execSQL("drop table if exists cars");
        createTable(db);


        addCar(db, "'audi'", "'1 000 000'", drawableToByteArray(R.mipmap.ic_audi2));
    }

    private byte[] drawableToByteArray(int id){
        Drawable d = getDrawable(id);
        Bitmap bitmap = ((BitmapDrawable)d).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        bitmap.recycle();
        return byteArray;
    }
    private void setCarPicture(byte[] blob){
        Bitmap bitmap = BitmapFactory.decodeByteArray(blob, 0,blob.length);
        ImageView carPicture = findViewById(R.id.carPicture);
        carPicture.setImageBitmap(bitmap);
    }

    private class Car {
        int id;
        String mark;
        String price;
        byte[] picture;

        Car(int id, String mark, String price, byte[] picture) {
            this.id = id;
            this.mark = mark;
            this.price = price;
            this.picture = picture;
        }

        public int getId() {
            return id;
        }

        public String getMark() {
            return mark;
        }

        public String getPrice() {
            return price;
        }

        public byte[] getPicture() {
            return picture;
        }
    }
}