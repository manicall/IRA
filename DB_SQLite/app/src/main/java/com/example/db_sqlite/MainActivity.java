package com.example.db_sqlite;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private Cursor cursor;
    private int eventNameIndex;
    private int eventDescriptionIndex;
    private int eventPhotoIndex;
    private int venueNameIndex;
    private int venueMapIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        cursor = db.rawQuery("select " +
                        "events.Event_Name, " +
                        "events.Event_Description, " +
                        "events.Event_Photo, " +
                        "venues.Venue, " +
                        "venues.Venue_Map " +
                        "from " +
                        "events " +
                        "left join venues on events.VenueNo = venues.VenueNo",
                null);

        eventNameIndex = cursor.getColumnIndex("Event_Name");
        eventDescriptionIndex = cursor.getColumnIndex("Event_Description");
        eventPhotoIndex = cursor.getColumnIndex("Event_Photo");
        venueNameIndex = cursor.getColumnIndex("Venue");
        venueMapIndex = cursor.getColumnIndex("Venue_Map");

        if(cursor.moveToFirst()){
            findViewById(R.id.button_prev).setEnabled(!cursor.isFirst());
            findViewById(R.id.button_next).setEnabled(!cursor.isLast());

            setDataFromCursor();
        }
    }

    public void onClick(View view) {
        switch(view.getId()){
            case R.id.button_prev:
                cursor.moveToPrevious();
                break;
            case R.id.button_next:
                cursor.moveToNext();
                break;
        }

        findViewById(R.id.button_prev).setEnabled(!cursor.isFirst());
        findViewById(R.id.button_next).setEnabled(!cursor.isLast());

        setDataFromCursor();
    }

    private void setDataFromCursor(){
        String name = cursor.getString(eventNameIndex);
        String description = cursor.getString(eventDescriptionIndex);
        String venue = cursor.getString(venueNameIndex);
        byte[] photo = cursor.getBlob(eventPhotoIndex);
        byte[] map = cursor.getBlob(venueMapIndex);

        TextView textName = findViewById(R.id.text_name);
        TextView textDescription = findViewById(R.id.text_description);
        TextView textVenue = findViewById(R.id.text_venue);
        ImageView photoImage = findViewById(R.id.eventPhoto);
        ImageView mapImage = findViewById(R.id.venue_map);

        textName.setText(name);
        textDescription.setText(description);
        Bitmap bitmapPhoto = BitmapFactory.decodeByteArray(photo, 8, photo.length - 8);
        photoImage.setImageBitmap(bitmapPhoto);

        if(venue != null){
            textVenue.setText(venue);
            Bitmap bitmapMap = BitmapFactory.decodeByteArray(map, 8, map.length - 8);
            mapImage.setImageBitmap(bitmapMap);
        }
        else{
            textVenue.setText("Не найдено");
            mapImage.setImageResource(R.drawable.not_found);
        }
    }
}
