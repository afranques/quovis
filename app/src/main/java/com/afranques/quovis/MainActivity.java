package com.afranques.quovis;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
    List<Integer> itemsID = new ArrayList<Integer>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myDb = new DatabaseHelper(this);
        // TODO I never close the database, and I should do that because otherwise i get this:
        // A SQLiteConnection object for database '/data/data/com.afranques.quovis/databases/QuovisDB.db'
        // was leaked! Please fix your application to end transactions in progress properly and to
        // close the database when it is no longer needed.

        Cursor res = myDb.getAllPlaces();
        while (res.moveToNext()) {
            itemsID.add(res.getInt(0)); //to get the place_id
            items.add(res.getString(1));
        }

        //we set the list to its place
        ArrayAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items);
        ListView listView = (ListView) findViewById(R.id.list_places_id);
        listView.setAdapter(adapter);

        //when we click to an element of the list
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(view.getContext(), ShowPlaceInMapsActivity.class);
                intent.putExtra("place_id", itemsID.get(position));
                startActivity(intent);
                //Toast.makeText(view.getContext(), Integer.toString(position), Toast.LENGTH_SHORT).show();
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, final View view, final int pos, long id) {
                AlertDialog.Builder alert = new AlertDialog.Builder(view.getContext());
                alert.setTitle("Delete place");
                alert.setMessage("Place name: "+items.get(pos));
                alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        //Toast.makeText(view.getContext(), "cat " + itemsID.get(pos)+" deleted", Toast.LENGTH_SHORT).show();

                        //we delete that place_id (entry) from Places
                        myDb.deletePlace(itemsID.get(pos));

                        //we reload the activity in order to load the new category
                        Intent intent = getIntent();
                        finish();
                        startActivity(intent);
                    }
                });
                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // what ever you want to do with No option.
                        dialog.cancel();
                    }
                });
                alert.show();

                return true;
            }
        });
    }

    public void startNewPlace(View view) {
        Intent intent = new Intent(this, TakePicActivity.class);
        startActivity(intent);
    }

    public void deleteMyDatabase(final View view) {
        AlertDialog.Builder alert = new AlertDialog.Builder(view.getContext());
        alert.setTitle("Delete all data");
        alert.setMessage("Are you sure you want to erase all stored data? This action is irreversible.");
        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //to reset database and reload Main Activity
                view.getContext().deleteDatabase("QuovisDB.db");
                Intent intent = getIntent();
                finish();
                startActivity(intent);
            }
        });
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // what ever you want to do with No option.
                dialog.cancel();
            }
        });
        alert.show();
    }
}
