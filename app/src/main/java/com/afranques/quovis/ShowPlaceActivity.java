package com.afranques.quovis;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ShowPlaceActivity extends AppCompatActivity {

    DatabaseHelper myDb;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_place);

        //to get the parameters sent from the previous intent
        Intent prevIntent = getIntent();
        int category_id = prevIntent.getIntExtra("category_id", -1);

        myDb = new DatabaseHelper(this);
        Cursor res = myDb.getPlace(category_id);
        if (res.getCount() != 0) {
            res.moveToNext();

            TextView placeTitle = (TextView) findViewById(R.id.show_place_title);
            placeTitle.setText(res.getString(1));

            TextView placeDescription = (TextView) findViewById(R.id.show_place_description);
            placeDescription.setText(res.getString(2));

            TextView placeCategory = (TextView) findViewById(R.id.show_place_category);
            placeCategory.setText(res.getString(3));

            TextView placeLatLong = (TextView) findViewById(R.id.show_place_latlong);
            placeLatLong.setText("Lat: " + res.getString(4) + " Long: " + res.getString(5));

            ImageView img = (ImageView) findViewById(R.id.show_thePicture);
            String pic_location = res.getString(6);
            Toast.makeText(this, pic_location, Toast.LENGTH_SHORT).show();

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            Bitmap bitmap = BitmapFactory.decodeFile(pic_location, options);
            img.setImageBitmap(bitmap);
        }
    }

    public void editPlace(View view) {
        //
    }

    public void goToMain(View view) {
        //just go back to Main activity
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
