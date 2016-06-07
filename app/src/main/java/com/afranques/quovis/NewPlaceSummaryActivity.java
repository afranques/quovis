package com.afranques.quovis;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class NewPlaceSummaryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_place_summary);

        //to get the parameters sent from the previous intent
        Intent prevIntent = getIntent();
        //consider that maybe there's no picture
        final Bitmap bmp = (Bitmap) prevIntent.getParcelableExtra("the_picture");
        final int category_id = prevIntent.getIntExtra("category_id", -1);
        if (category_id == -1) {
            Toast.makeText(this, "Error: Category not detected", Toast.LENGTH_SHORT).show();
        }
        final double latitude = prevIntent.getDoubleExtra("latitude", 0);
        final double longitude = prevIntent.getDoubleExtra("longitude", 0);
        Toast.makeText(this, "Latitude: "+latitude+" Longitude: "+longitude, Toast.LENGTH_SHORT).show();

    }
}
