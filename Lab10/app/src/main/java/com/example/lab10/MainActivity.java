package com.example.lab10;

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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;
import java.io.File;
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

    String currentMark;

    boolean isVisible = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // открытие базы данных
        db = openOrCreateDatabase("app.db", MODE_PRIVATE, null);
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

    void initializateFilter() {
        // HashSet - для исключения повторяющихся элементов
        // хранит множество всех марок автомобилей из бд
        HashSet<String> marks = new HashSet<>();
        // запрос на выборку всех данных из таблицы cars
        Cursor carCursor = db.rawQuery("SELECT * FROM cars;", null);

        // переход к первой записи в бд
        carCursor.moveToFirst();
        // извлекаем записи из бд
        while (!carCursor.isAfterLast()) {
            // формируем список возможных марок автомобилей с удалением кавычек
            marks.add(deleteQuotes(carCursor.getString(1)));
            // переход на следующую запись
            carCursor.moveToNext();
        }
        // закрываем курсор
        carCursor.close();

        ArrayList<String> listMarks = new ArrayList<>(marks);
        // устанавливаем записи о марках автомобилей в выпадающий список
        Spinner spinner = findViewById(R.id.markSpinner);
        spinnerSetAdapter(spinner, listMarks);

        // устанавливаем подсказки о доступных ценах автомобилей
        setHintPrice(listMarks.get(0));
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

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            // обработка выбора элемента из списка
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Получаем выбранный объект
                currentMark = (String)parent.getItemAtPosition(position);
                setHintPrice(currentMark);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    // устанавливает подсказки о доступных ценах автомобилей
    void setHintPrice(String currentMark) {
        // хранит список всех цен автомобилей из бд
        ArrayList<Double> prices = new ArrayList<>();

        Log.d("TAG", "setHintPrice: " + currentMark);

        Cursor carCursor = db.rawQuery("SELECT * FROM cars WHERE mark like '%" + currentMark +"%';", null);
        // переход на первую запись в таблице
        carCursor.moveToFirst();
        while(!carCursor.isAfterLast()){ // пока не конец таблицы
            // формируем список возможных цен автомобилей
            prices.add(carCursor.getDouble(2));
            carCursor.moveToNext(); // переход к следующией записи
        }

        Log.d("TAG", "setHintPrice: " + currentMark);

        double[] minMax = minMax(prices);

        TextView startPrice = findViewById(R.id.startPrice);
        TextView endPrice = findViewById(R.id.endPrice);

        startPrice.setHint(priceToString(minMax[0]));
        endPrice.setHint(priceToString(minMax[1]));
    }

    // алгоритм поиска минимальной и максимальной цены
    private double[] minMax(ArrayList<Double> prices) {
        double min = prices.get(0);
        double max = prices.get(0);
        for (Double price : prices) {
            if (min > price) min = price;
            if (max < price) max = price;
        }
        return new double[]{min, max};
    }
    // устанавливает обработку клика на кнопки
    private void setOnClickListeners() {
        Button bPrevious = findViewById(R.id.buttonPrevious);
        Button bNext = findViewById(R.id.buttonNext);
        Button bFind = findViewById(R.id.buttonFind);

        bPrevious.setOnClickListener(this);
        bNext.setOnClickListener(this);
        bFind.setOnClickListener(this);
    }
    // запоминает элементы отвечающие за вывод информации об автомобиле
    private void setInformationElements() {
        informationElements.add(findViewById(R.id.markName));
        informationElements.add(findViewById(R.id.markValue));
        informationElements.add(findViewById(R.id.priceName));
        informationElements.add(findViewById(R.id.priceValue));
        informationElements.add(findViewById(R.id.carPicture));
        informationElements.add(findViewById(R.id.buttonPrevious));
        informationElements.add(findViewById(R.id.buttonNext));
    }
    // скрывает интерфейс для вывода информации об автомобиле
    private void hideInformation() {
        if (isVisible) {
            for (View informationElement : informationElements)
                informationElement.setVisibility(View.INVISIBLE);
            isVisible = false;
        }
    }
    // показывает интерфейс для вывода информации об автомобиле
    private void showInformation() {
        if (!isVisible) {
            for (View informationElement : informationElements)
                informationElement.setVisibility(View.VISIBLE);
            isVisible = true;
        }
    }
    // создает таблицы в базе данных с информацией об автомобилях
    private void createTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS cars (" +
                "id INTEGER PRIMARY KEY, " +
                "mark TEXT NOT NULL, " +
                "price REAL NOT NULL, " +
                "picture BLOB)");
    }
    // добавляет запись в таблицу об автомобилях
    private void addCar(SQLiteDatabase db, String mark, double price, byte[] picture) {
        ContentValues cv = new ContentValues();
        cv.put("mark", "'" + mark + "'");
        cv.put("price", price);
        cv.put("picture", picture);

        db.insert("cars", null, cv);
    }
    // выводит запись об автомобиле
    private void setDataFromCursor() {
        Car car = cars.get(currentCarId);

        TextView mark = findViewById(R.id.markValue);
        TextView price = findViewById(R.id.priceValue);

        mark.setText(car.mark);
        price.setText(priceToString(car.price));
        setCarPicture(car.picture);

        Button buttonPrevious = findViewById(R.id.buttonPrevious);
        Button buttonNext = findViewById(R.id.buttonNext);

        buttonPrevious.setEnabled(currentCarId == 0 ? false : true);
        buttonNext.setEnabled(currentCarId == cars.size() - 1 ? false : true);
    }

    // устанавливает картинку извлеченную из таблицы SQLite
    private void setCarPicture(byte[] blob){
        // извлекаем bitmap из набора байт
        Bitmap bitmap = BitmapFactory.decodeByteArray(blob, 0,blob.length);
        /* устанавливаем bitmap в ImageView */
        ImageView carPicture = findViewById(R.id.carPicture);
        carPicture.setImageBitmap(bitmap);
    }

    // создаем записи таблицы
    private void addRecords(SQLiteDatabase db) {
        db.execSQL("drop table if exists cars"); // удаляем таблицу cars
        createTable(db); // создаем таблицу cars
        int min = 100;
        int max = 1000;
        // добавляем записи о машинах
        addCar(db, "Audi", rnd(min, max), drawableToByteArray(R.mipmap.ic_audi_q5_foreground));
        addCar(db, "Audi", rnd(min, max), drawableToByteArray(R.mipmap.ic_bmw_x3_foreground));
        addCar(db, "Audi", rnd(min, max), drawableToByteArray(R.mipmap.ic_daewoo_gentra_2_foreground));
        addCar(db, "Ford", rnd(min, max), drawableToByteArray(R.mipmap.ic_ford_focus_foreground));
        addCar(db, "Ford", rnd(min, max), drawableToByteArray(R.mipmap.ic_geely_tugella_foreground));
        addCar(db, "Ford", rnd(min, max), drawableToByteArray(R.mipmap.ic_kia_sportage_foreground));
        addCar(db, "Nissan", rnd(min, max), drawableToByteArray(R.mipmap.ic_mitsubishi_outlander_foreground));
        addCar(db, "Nissan", rnd(min, max), drawableToByteArray(R.mipmap.ic_subaru_outback_foreground));
        addCar(db, "Nissan", rnd(min, max), drawableToByteArray(R.mipmap.ic_uas_patriot_foreground));
        addCar(db, "Nissan", rnd(min, max), drawableToByteArray(R.mipmap.ic_volkswagen_polo_5_foreground));

    }
    // функция возвращаяющая случайное число в заданном диапазоне
    static double rnd(int min, int max) {
        max -= min;
        return Math.round((int)((Math.random() * ++max) + min) * 1000000) / 100;
    }

    // переводит картинку в набор байт, для хранения этого набора в таблице SQLite
    private byte[] drawableToByteArray(int id){
        // получаем картинку из ресурсов
        Drawable d = getDrawable(id);
        // преобразуем картинку в bitmap
        Bitmap bitmap = ((BitmapDrawable)d).getBitmap();
        // создаем переменную хранящую поток байт
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        // преобразуем bitmap в набор байт
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        // возвращаем массив байт
        return stream.toByteArray();
    }

    // обработчик клика на кнопку
    @Override
    public void onClick(View view) {
        if (isVisible) {
            switch (view.getId()) {
                case R.id.buttonPrevious:
                    if (currentCarId != 0) currentCarId--;
                    break;
                case R.id.buttonNext:
                    if (currentCarId != cars.size() - 1) currentCarId++;
                    break;
            }
            setDataFromCursor();
        }
        switch (view.getId()) {
            case R.id.buttonFind: find(); break;
        }
        Log.d("TAG", "onClick: " + currentCarId);
    }


    public void find() {
        TextView startPrice = findViewById(R.id.startPrice);
        TextView endPrice = findViewById(R.id.endPrice);
        Spinner spinner = findViewById(R.id.markSpinner);

        Log.d("TAG", "find: " + currentMark);

        // проверяем, что в поле ввода записано число
        if (!isValid(startPrice, endPrice)) {
            Toast.makeText(this, "Ожидалось число", Toast.LENGTH_SHORT).show();
            return;
        }
        // проверяем, что нашлись записи удовлетворяющие условию
        ArrayList<Car> tempCars =
                getCars("", priceToDouble(startPrice.getText()), priceToDouble(endPrice.getText()));
        if (tempCars.isEmpty()) {
            Toast.makeText(this, "Записи в заданном диапазоне цен не найдены", Toast.LENGTH_SHORT).show();
            return;
        }

        currentCarId = 0;
        cars = tempCars;

        setDataFromCursor();
        showInformation();
    }

    // проверяет есть ли в текстовых полях некорректные символы
    // согласно регулярному выражению в поле можно записать только числа
    boolean isValid(TextView startPrice, TextView endPrice){
        return startPrice.getText().toString().matches("^-?\\d+\\.?\\d{0,2}$") &&
                 endPrice.getText().toString().matches("^-?\\d+\\.?\\d{0,2}$");
    }

    ArrayList<Car> getCars(String mark, double startPrice, double endPrice) {
        ArrayList<Car> cars = new ArrayList<>();
        Cursor carCursor = db.rawQuery("SELECT * FROM cars WHERE mark like '%" + currentMark + "%' and price BETWEEN "
                + startPrice + " AND " + endPrice + ";", null);
        // переход на первую запись в таблице
        carCursor.moveToFirst();
        while(!carCursor.isAfterLast()){ // пока не конец таблицы
            cars.add(new Car(
                    carCursor.getInt(0), // id
                    deleteQuotes(carCursor.getString(1)), // mark
                    carCursor.getDouble(2), // price
                    carCursor.getBlob(3))); // picture
            carCursor.moveToNext(); // переход к следующией записи
        }
        carCursor.close();
        return cars;
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
    private String priceToString(double price) {
        return String.format(Locale.ROOT, "%,.2f", price) + "р.";
    }

    // переводим цену из удобочитаемого формата в double
    private double priceToDouble(CharSequence price) {
        return priceToDouble(price.toString());
    }

    // переводим цену из удобочитаемого формата в double
    private double priceToDouble(String price) {
        Log.d("TAG", "priceToDouble: " + price);
        price = price.replace(",", "");
        price = price.replace("p.", "");
        Log.d("TAG", "priceToDouble: " + price);
        return Double.valueOf(price);
    }

    // удаляет кавычки, которые извлекаются вместе с текстовой записью из таблицы SQLite
    private String deleteQuotes(String mark){
        return mark.replace("\'", "");
    }
}