package com.afranques.quovis;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    DatabaseHelper myDb;
    List<String> items = new ArrayList<String>();
    List<Integer> itemsID = new ArrayList<Integer>();
    List<String> catItems = new ArrayList<String>();
    List<Integer> catItemsID = new ArrayList<Integer>();
    private static final int REQ_CODE_TAKE_PICTURE = 1;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.topcorner_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_database_id:
                // User chose the "Erase all data" item, delete all database...
                deleteMyDatabase(findViewById(android.R.id.content));
                return true;

            case R.id.add_place_id:
                // User chose the "Add new place"
                startNewPlace();
                return true;
//
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        //getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Array of choices for the spinner
        //we read all values from the table Categories and add them on a list
        myDb = new DatabaseHelper(this);
        Cursor res = myDb.getAllData();
        catItemsID.add(0);
        catItems.add("All categories");
        while (res.moveToNext()) {
            catItemsID.add(res.getInt(0)); //to get the category_id
            catItems.add(res.getString(1)); //to get the category_name
        }
        // Selection of the spinner
        Spinner spinner = (Spinner) findViewById(R.id.spinner_nav);
        // Application of the Array to the Spinner
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this,   android.R.layout.simple_spinner_item, catItems);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
        spinner.setAdapter(spinnerArrayAdapter);
        // Every time a choice is selected (including when the activity starts)
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View view, int position, long myID) {
                //Toast.makeText(view.getContext(), Integer.toString(catItemsID.get(position)), Toast.LENGTH_SHORT).show();
                showPlacesCategory(view, position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });

        //this is to get the height of the task bar (if defined) in pixels
        TypedValue tv = new TypedValue();
        Integer actionBarHeight = 0;
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data,getResources().getDisplayMetrics());
        }
        //verticalMarginInPixels is the value from @dimen/activity_vertical_margin
        int verticalMarginInPixels = (int) getResources().getDimension(R.dimen.activity_vertical_margin);
        int horizontalMarginInPixels = (int) getResources().getDimension(R.dimen.activity_horizontal_margin);
        //so the padding of the content_main_id layout is the taskbar height + the vertical margin
        LinearLayout ln = (LinearLayout) this.findViewById(R.id.content_main_id);
        ln.setPadding(horizontalMarginInPixels,verticalMarginInPixels + actionBarHeight,horizontalMarginInPixels,verticalMarginInPixels);

        FloatingActionButton fab_add_button = (FloatingActionButton) findViewById(R.id.fab_add_button_main);
        fab_add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Click "add place" action
                startNewPlace();
            }
        });

        // TODO I never close the database, and I should do that because otherwise i get this:
        // A SQLiteConnection object for database '/data/data/com.afranques.quovis/databases/QuovisDB.db'
        // was leaked! Please fix your application to end transactions in progress properly and to
        // close the database when it is no longer needed.

    }

    private void showPlacesCategory (View view, int position) {
        items = new ArrayList<String>();
        itemsID = new ArrayList<Integer>();
        Cursor res = myDb.getPlacesByCat(position);
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

    public void startNewPlace() {
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
