package com.afranques.quovis;

import android.content.Intent;
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

public class MainActivity extends AppCompatActivity {

    DatabaseHelper myDb;
    List<String> items = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //to reset database
        //this.deleteDatabase("QuovisDB.db");

        myDb = new DatabaseHelper(this);
        Cursor res = myDb.getAllPlaces();
        while (res.moveToNext()) {
            items.add(res.getString(0)+": "+res.getString(1));
        }

        //we set the list to its place
        ArrayAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items);
        ListView listView = (ListView) findViewById(R.id.list_places_id);
        listView.setAdapter(adapter);

        //when we click to an element of the list
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(view.getContext(), ShowPlaceActivity.class);
                intent.putExtra("category_id", position);
                startActivity(intent);
                //Toast.makeText(view.getContext(), Integer.toString(position), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void startNewPlace(View view) {
        Intent intent = new Intent(this, TakePicActivity.class);
        startActivity(intent);
    }
}
