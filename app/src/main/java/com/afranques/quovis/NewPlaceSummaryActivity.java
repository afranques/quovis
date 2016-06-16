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
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

public class NewPlaceSummaryActivity extends AppCompatActivity {
    DatabaseHelper myDb;

    private EditText placeTitle;
    private EditText placeDescription;
    private Bitmap bmp;
    private int category_id;
    private double latitude;
    private double longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_place_summary);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.new_place_summary_toolbar);
        setSupportActionBar(myToolbar);

        EditText editText = (EditText) findViewById(R.id.place_title);
        //it's focus by default, but just in case we specify that we want to focus on the title, not description
        editText.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

        //to get the parameters sent from the previous intent
        Intent prevIntent = getIntent();
        //consider that maybe there's no picture
        bmp = (Bitmap) prevIntent.getParcelableExtra("the_picture");
        category_id = prevIntent.getIntExtra("category_id", -1);
        if (category_id == -1) {
            Toast.makeText(this, "Error: Category not detected", Toast.LENGTH_SHORT).show();
        }
        latitude = prevIntent.getDoubleExtra("latitude", 0);
        longitude = prevIntent.getDoubleExtra("longitude", 0);
        //Toast.makeText(this, "Latitude: "+latitude+" Longitude: "+longitude, Toast.LENGTH_SHORT).show();

        ImageView img = (ImageView) findViewById(R.id.thePicture_summary);
        img.setImageBitmap(bmp);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.topcorner_menu_summary, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save_summary:
                savePlace();
                return true;

            case R.id.action_discard_summary:
                discardPlace();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }


    public void savePlace() {
        placeTitle = (EditText) findViewById(R.id.place_title);
        placeDescription = (EditText) findViewById(R.id.place_description);

        //save picture to internal storage and get the path
        Calendar cal = Calendar.getInstance();
        Date rightNow = cal.getTime();
        String fileName = rightNow.toString().replaceAll("[:\\s]+", "");
        fileName += ".jpg";
        String pic_location = saveToInternalStorage(bmp, fileName);

        myDb = new DatabaseHelper(this);
        String finalPath = pic_location+"/"+fileName;
        myDb.insertPlace(placeTitle.getText().toString(), placeDescription.getText().toString(),
                category_id, latitude, longitude, finalPath);


        //after saving the new place jump to the main activity
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void discardPlace() {
        //discard place
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void onPause() {
        super.onPause();
        //hide keyboard because we forced it on previously and it still might me on
        InputMethodManager imm = (InputMethodManager) getSystemService(NewPlaceSummaryActivity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getWindow().getDecorView().getRootView().getWindowToken(), 0);
    }

    @Override
    public void onStop() {
        super.onStop();
        //hide keyboard because we forced it on previously and it still might me on
        InputMethodManager imm = (InputMethodManager) getSystemService(NewPlaceSummaryActivity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getWindow().getDecorView().getRootView().getWindowToken(), 0);
    }

    private String saveToInternalStorage(Bitmap bitmapImage, String file_name){
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath=new File(directory, file_name);

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return directory.getAbsolutePath();
    }
}
