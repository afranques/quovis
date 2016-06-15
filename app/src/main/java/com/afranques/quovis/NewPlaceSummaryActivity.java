package com.afranques.quovis;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

public class NewPlaceSummaryActivity extends AppCompatActivity {
    DatabaseHelper myDb;

    private EditText placeTitle;
    private EditText placeDescription;
    private String pic_location;
    private int category_id;
    private double latitude;
    private double longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_place_summary);

        //to get the parameters sent from the previous intent
        Intent prevIntent = getIntent();
        //consider that maybe there's no picture
        pic_location = (String) prevIntent.getStringExtra("the_picture");
        Toast.makeText(this, pic_location, Toast.LENGTH_SHORT).show();
        category_id = prevIntent.getIntExtra("category_id", -1);
        if (category_id == -1) {
            Toast.makeText(this, "Error: Category not detected", Toast.LENGTH_SHORT).show();
        }
        latitude = prevIntent.getDoubleExtra("latitude", 0);
        longitude = prevIntent.getDoubleExtra("longitude", 0);
        //Toast.makeText(this, "Latitude: "+latitude+" Longitude: "+longitude, Toast.LENGTH_SHORT).show();

        final String dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/Quovis/";
        ImageView myImage = (ImageView) findViewById(R.id.thePicture_summary);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bitmap = BitmapFactory.decodeFile(dir+pic_location, options);
        myImage.setImageBitmap(bitmap);
    }


    public void savePlace(View view) {
        placeTitle = (EditText) findViewById(R.id.place_title);
        placeDescription = (EditText) findViewById(R.id.place_description);

        myDb = new DatabaseHelper(this);
        myDb.insertPlace(placeTitle.getText().toString(), placeDescription.getText().toString(),
                category_id, latitude, longitude, pic_location);

        //after saving the new place jump to the main activity
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);

    }

    public void discardPlace(View view) {
        //exit application
    }

    public void saveNExit(View view) {
        savePlace(view);
        //after saving leave application

    }
}
