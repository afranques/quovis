package com.afranques.quovis;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class NewPlaceCategoryActivity extends AppCompatActivity {
    DatabaseHelper myDb;
    List<String> items = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_place_category);

        //to get the parameters sent from the previous intent
        Intent prevIntent = getIntent();
        //consider that maybe there's no picture
        final Bitmap bmp = (Bitmap) prevIntent.getParcelableExtra("the_picture");

        //we read all values from the table Categories and add them on a list
        myDb = new DatabaseHelper(this);
        Cursor res = myDb.getAllData();
        while (res.moveToNext()) {
            items.add(res.getString(1));
        }
        items.add("New category");

        //we set the list to its place
        ArrayAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items);
        ListView listView = (ListView) findViewById(R.id.id_new_place_category_view);
        listView.setAdapter(adapter);

        //when we click to an element of the list
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == (items.size() - 1)) {
                    //code to add category
                    //myDb.insertData("Restaurants");
                    //myDb.insertData("Supermarkets");
                } else {
                    //save category number and picture, and go to next screen (map location)
                    Intent intent = new Intent(NewPlaceCategoryActivity.this, SetLocationActivity.class);
                    intent.putExtra("the_picture", bmp);
                    intent.putExtra("category_id", position);
                    startActivity(intent);
                }
            }
        });
    }
}
