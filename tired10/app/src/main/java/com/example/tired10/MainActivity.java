package com.example.tired10;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    // хранит записи о машинах по заданному фильтру
    private ArrayList<Car> cars = new ArrayList<>();
    // хранит элементы отображающие записи об автомобилях
    private ArrayList<View> informationElements = new ArrayList<>();
    // указатель на текущую запись об автомобилях
    private int currentCarId = 0;
    // экземпляр базы данных
    SQLiteDatabase db;

    boolean isVisible = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // открытие базы данных
        db = getBaseContext().openOrCreateDatabase("app.db", MODE_PRIVATE, null);
        // создание записей в базе данных
        addRecords(db);

        // заполняем фильтр подсказками
        initializateFilter();
        // запоминаем элементы, которые управляют выводом информации об автомобиле
        // для того чтобы скрывать и выводить их в нужный нам момент
        setInformationElements();
        // устанавливаем слушатели на кнопки
        setOnClickListeners();
        // скрываем информацию об автомобиле,
        // так как пользователь еще не задал параметры фильтра
        hideInformation();

    }

    void initializateFilter(){
        // HashSet - для исключения повторяющихся элементов
        // хранит множество всех марок автомобилей из бд
        HashSet<String> marks = new HashSet<>();
        // хранит список всех цен автомобилей из бд
        ArrayList<Double> prices = new ArrayList<>();
        // запрос на выборку всех данных из таблицы cars
        Cursor carCursor = db.rawQuery("SELECT * FROM cars;", null);

        // переход к первой записи в бд
        carCursor.moveToFirst();
        // извлекаем записи из бд
        while(!carCursor.isAfterLast()){
            // формируем список возможных марок автомобилей с удалением кавычек
            marks.add(deleteQuotes(carCursor.getString(1)));
            // формируем список возможных цен автомобилей
            prices.add(carCursor.getDouble(2));
            // переход на следующую запись
            carCursor.moveToNext();
        }
        // закрываем курсор
        carCursor.close();

        // устанавливаем записи о марках автомобилей в выпадающий список
        Spinner spinner = (Spinner) findViewById(R.id.markSpinner);
        spinnerSetAdapter(spinner, new ArrayList<>(marks));

        // устанавливаем подсказки о доступных ценах автомобилей
        setHintPrice(prices);
    }
    // устанавливает записи о марках автомобилей в выпадающий список
    private void spinnerSetAdapter(Spinner spinner, ArrayList<String> marks) {
        //Создаем адаптер ArrayAdapter с помощью массива строк и стандартной разметки элемета spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(spinner.getContext(),
                android.R.layout.simple_spinner_item, marks);
        // Определяем разметку для использования при выборе элемента
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Применяем адаптер к элементу spinner
        spinner.setAdapter(adapter);
    }

    // устанавливает подсказки о доступных ценах автомобилей
    void setHintPrice(ArrayList<Double> prices){
        double [] minMax = minMax(prices);

        TextView startPrice = findViewById(R.id.startPrice);
        TextView endPrice = findViewById(R.id.endPrice);

        startPrice.setHint(priceToString(minMax[0]));
        endPrice.setHint(priceToString(minMax[1]));
    }

    private double[] minMax(ArrayList<Double> prices){
        double min = prices.get(0);
        double max = prices.get(0);
        for (Double price : prices) {
            if (min > price) min = price;
            if (max < price) max = price;
        }
        return new double[]{min, max};
    }

     private void setOnClickListeners(){
        Button bPrevious = findViewById(R.id.buttonPrevious);
        Button bNext = findViewById(R.id.buttonNext);
        Button bFind = findViewById(R.id.buttonFind);

        bPrevious.setOnClickListener(this);
        bNext.setOnClickListener(this);
        bFind.setOnClickListener(this);
    }

    private void setInformationElements() {
        informationElements.add(findViewById(R.id.markName));
        informationElements.add(findViewById(R.id.markValue));
        informationElements.add(findViewById(R.id.priceName));
        informationElements.add(findViewById(R.id.priceValue));
        informationElements.add(findViewById(R.id.carPicture));
        informationElements.add(findViewById(R.id.buttonPrevious));
        informationElements.add(findViewById(R.id.buttonNext));
    }

    private void hideInformation() {
        if (isVisible) {
            for (View informationElement : informationElements)
                informationElement.setVisibility(View.INVISIBLE);
            isVisible = false;
        }
    }

    private void showInformation() {
        if (!isVisible) {
        for (View informationElement: informationElements)
            informationElement.setVisibility(View.VISIBLE);
            isVisible = true;
        }
    }

    private void createTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS cars (" +
                "id INTEGER PRIMARY KEY, " +
                "mark TEXT NOT NULL, " +
                "price REAL NOT NULL, " +
                "picture BLOB)");
    }

    private void addCar(SQLiteDatabase db, String mark, double price, byte[] picture) {
        ContentValues cv = new  ContentValues();
        cv.put("mark",  "'" + mark + "'");
        cv.put("price", price);
        cv.put("picture", picture);

        db.insert("cars", null, cv );
    }

    private void setDataFromCursor() {
        Car car = cars.get(currentCarId);

        TextView mark = findViewById(R.id.markValue);
        TextView price = findViewById(R.id.priceValue);

        mark.setText(car.mark);
        price.setText(priceToString(car.price));
        setCarPicture(car.picture);
    }



    private void setCarPicture(byte[] blob){
        Bitmap bitmap = BitmapFactory.decodeByteArray(blob, 0,blob.length);
        ImageView carPicture = findViewById(R.id.carPicture);
        carPicture.setImageBitmap(bitmap);
    }

    // создаем записи таблицы
    private void addRecords(SQLiteDatabase db) {
        db.execSQL("drop table if exists cars");
        createTable(db);

        addCar(db, "'audi'", 1000000.00, drawableToByteArray(R.mipmap.ic_audi2_foreground));
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


    @Override
    public void onClick(View view) {
        if(isValidate()) {
            switch (view.getId()) {
                case R.id.buttonPrevious: currentCarId--; break;
                case R.id.buttonNext:     currentCarId++; break;
            }
        }
        switch (view.getId()) {
            case R.id.buttonFind: find(); break;
        }
        Log.d("TAG", "onClick: " + currentCarId);
    }

    private boolean isValidate(){
        return currentCarId >= 0 && currentCarId < cars.size();
    }

    public void find() {
        currentCarId = 0;
        TextView startPrice = findViewById(R.id.startPrice);
        TextView endPrice = findViewById(R.id.endPrice);

        Log.d("TAG", "find: " + priceToDouble(startPrice.getText()));

        fillCars(priceToDouble(startPrice.getText()), priceToDouble(endPrice.getText()));
        setDataFromCursor();
        showInformation();
    }

    void fillCars(double startPrice, double endPrice){
        cars.clear();
        Cursor carCursor = db.rawQuery("SELECT * FROM cars WHERE price BETWEEN "
                + startPrice + " AND " + endPrice + ";", null);
        // добавление записей в список
        carCursor.moveToFirst();
        while(!carCursor.isAfterLast()){
            cars.add(new Car(
                    carCursor.getInt(0),
                    deleteQuotes(carCursor.getString(1)),
                    carCursor.getDouble(2),
                    carCursor.getBlob(3)));
            carCursor.moveToNext();
        }
        carCursor.close();
    }

    // для удобного хранения записей об автомобиле
    private class Car {
        private int id;
        String mark;
        double price;
        private byte[] picture;

        Car(int id, String mark, double price, byte[] picture) {
            this.id = id;
            this.mark = mark;
            this.price = price;
            this.picture = picture;
        }
    }

    // переводим цену в удобочитаемый формат
    private String priceToString(double price){
        return String.format(Locale.ROOT,"%,.2f", price) + "р.";
    }
    // переводим цену из удобочитаемого формата в double
    private double priceToDouble(CharSequence price){
        return priceToDouble(price.toString());
    }
    // переводим цену из удобочитаемого формата в double
    private double priceToDouble(String price){
        Log.d("TAG", "priceToDouble: " + price);
        price = price.replace(",", "");
        price = price.replace("p.", "");
        Log.d("TAG", "priceToDouble: " + price);
        return Double.valueOf(price);
    }

    private String deleteQuotes(String mark){
        return mark.replace("\'", "");
    }
}