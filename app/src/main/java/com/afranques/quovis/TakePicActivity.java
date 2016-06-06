package com.afranques.quovis;

import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class TakePicActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_pic);

        takePicture();
    }

    private static final int REQ_CODE_TAKE_PICTURE = 1;

    private void takePicture() {
        Intent picIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(picIntent, REQ_CODE_TAKE_PICTURE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent theIntent) {
        if (requestCode == REQ_CODE_TAKE_PICTURE && resultCode == RESULT_OK) {
            Bitmap bmp = (Bitmap) theIntent.getExtras().get("data");
//            ImageView img = (ImageView) findViewById(R.id.thePicture);
//            img.setImageBitmap(bmp);
        }

        Intent intent = new Intent(this, NewPlaceCategoryActivity.class);
        startActivity(intent);
    }
}
