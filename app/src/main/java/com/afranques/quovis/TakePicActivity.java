package com.afranques.quovis;

import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class TakePicActivity extends AppCompatActivity {

    private static final int REQ_CODE_TAKE_PICTURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_pic);

        //this function invokes the camera
        takePicture();
    }

    //this the function that defines how to invoke the camera
    private void takePicture() {
        Intent picIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(picIntent, REQ_CODE_TAKE_PICTURE);
    }

    //here we say what to do when the camera returns the picture
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent theIntent) {

        //here we call the next activity, passing as a parameter the picture
        Intent intent = new Intent(this, NewPlaceCategoryActivity.class);
        if (requestCode == REQ_CODE_TAKE_PICTURE && resultCode == RESULT_OK) {
            Bitmap bmp = (Bitmap) theIntent.getExtras().get("data");
            //ImageView img = (ImageView) findViewById(R.id.thePicture);
            //img.setImageBitmap(bmp);

            //MAYBE YOU SHOULD CONSIDER COMPRESSING THE IMAGE. CHECK STACKOVERFLOW QUESTION IN FAVS
            intent.putExtra("the_picture", bmp);
        }
        startActivity(intent);
    }
}
