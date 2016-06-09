package com.afranques.quovis;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class NewPlaceCategoryActivity extends AppCompatActivity {
    DatabaseHelper myDb;
    List<String> items = new ArrayList<String>();
    List<Integer> itemsID = new ArrayList<Integer>();

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
            itemsID.add(res.getInt(0)); //to get the category_id
            items.add(res.getString(0)+": "+res.getString(1)); //to get the category_name
        }

        //we set the list to its place
        ArrayAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items);
        ListView listView = (ListView) findViewById(R.id.id_new_place_category_view);
        listView.setAdapter(adapter);

        //when we click to an element of the list
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                //save category number and picture, and go to next screen (map location)
                Intent intent = new Intent(NewPlaceCategoryActivity.this, SetLocationActivity.class);
                intent.putExtra("the_picture", bmp); //we pass the picture to the next activity
                intent.putExtra("category_id", itemsID.get(position)); //we pass the category_id to next activity
                //Toast.makeText(view.getContext(), Integer.toString(itemsID.get(position)), Toast.LENGTH_SHORT).show();
                startActivity(intent);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, final View view, final int pos, long id) {
                AlertDialog.Builder alert = new AlertDialog.Builder(view.getContext());
                //alert.setMessage("Delete category");
                alert.setTitle("Delete category");
                alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        //Toast.makeText(view.getContext(), "cat " + itemsID.get(pos)+" deleted", Toast.LENGTH_SHORT).show();

                        //we delete that category_id (entry) from Categories
                        myDb.deleteCategory(itemsID.get(pos));

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

    public void addCategory(View view) {
        //code to add category
        AlertDialog.Builder alert = new AlertDialog.Builder(view.getContext());
        final EditText edittext = new EditText(view.getContext());
        alert.setMessage("Enter new category name");
        alert.setTitle("New category");

        alert.setView(edittext);

        alert.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //What ever you want to do with the value
                String newCategoryName = edittext.getText().toString();
                //Toast.makeText(view.getContext(), newCategoryName, Toast.LENGTH_SHORT).show();

                //we insert the new category into de DB
                myDb.insertData(newCategoryName);

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
    }
}
