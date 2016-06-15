package com.afranques.quovis;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by AMFG on 6/14/2016.
 */
public class MainContentTabs extends Fragment {

    DatabaseHelper myDb;
    List<String> items = new ArrayList<String>(); //we store the titles
    List<String> itemsDes = new ArrayList<String>(); //we store the descriptions
    List<String> itemsPicLoc = new ArrayList<String>(); //we store the pic locations
    List<Integer> itemsID = new ArrayList<Integer>();
    private int category_id;

    public MainContentTabs(int category_id) {
        // Required empty public constructor
        this.category_id = category_id;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.content_main, container, false);
        showPlacesCategory(v, category_id);
        return v;
    }

    private void showPlacesCategory (View view, int position) {
        items = new ArrayList<String>();
        itemsID = new ArrayList<Integer>();
        myDb = new DatabaseHelper(this.getContext());
        Cursor res = myDb.getPlacesByCat(position);
        ArrayList<ArrayList<String>> listOfLists = new ArrayList<ArrayList<String>>();
        ArrayList<String> rowInfo = new ArrayList<String>();
        while (res.moveToNext()) {
            rowInfo = new ArrayList<String>();
            itemsID.add(res.getInt(0)); //to get the place_id
            items.add(res.getString(1)); //to get the place_title
            rowInfo.add(res.getString(1));
            itemsDes.add(res.getString(2)); //to get the place_description
            rowInfo.add(res.getString(2));
            itemsPicLoc.add(res.getString(6)); //to get the place_pic_location
            rowInfo.add(res.getString(6));
            listOfLists.add(rowInfo);
        }

        //we set the list to its place
        MainListRowsAdapter adapter = new MainListRowsAdapter(view.getContext(), listOfLists);
        ListView listView = (ListView) view.findViewById(R.id.list_places_id);
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
                        Intent intent = getActivity().getIntent();
                        getActivity().finish();
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

}
