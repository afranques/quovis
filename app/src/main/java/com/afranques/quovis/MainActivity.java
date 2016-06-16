package com.afranques.quovis;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    DatabaseHelper myDb;
    List<String> catItems = new ArrayList<String>();
    List<Integer> catItemsID = new ArrayList<Integer>();
    private static final int REQ_CODE_TAKE_PICTURE = 1;
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        //getSupportActionBar().setDisplayShowTitleEnabled(false);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

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

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    // BEGINNING TAB FUNCTIONS
    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        // Array of choices for the spinner
        //we read all values from the table Categories and add them on a list
        myDb = new DatabaseHelper(this);
        Cursor res = myDb.getAllData();
        catItemsID.add(0);
        catItems.add("All");
        adapter.addFragment(new MainContentTabs(0), "All");
        while (res.moveToNext()) {
            catItemsID.add(res.getInt(0)); //to get the category_id
            catItems.add(res.getString(1)); //to get the category_name
            adapter.addFragment(new MainContentTabs(res.getInt(0)), res.getString(1));
        }

        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
    // END TAB FUNCTIONS

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
