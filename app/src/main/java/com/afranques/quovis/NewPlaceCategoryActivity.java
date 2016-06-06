package com.afranques.quovis;

import android.database.Cursor;
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

        myDb = new DatabaseHelper(this);

//        myDb.insertData("Restaurants");
//        myDb.insertData("Supermarkets");

        Cursor res = myDb.getAllData();
        if (res.getCount() == 0) {
            Toast.makeText(getApplicationContext(),"No categories yet",Toast.LENGTH_SHORT).show();
        }
        else {
            while (res.moveToNext()) {
                items.add(res.getString(1));
            }
        }

        ArrayAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items);

        ListView listView = (ListView) findViewById(R.id.id_new_place_category_view);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Toast.makeText(getApplicationContext(),
                        "Click ListItem Number " + position, Toast.LENGTH_LONG)
                        .show();
            }
        });
    }
}
