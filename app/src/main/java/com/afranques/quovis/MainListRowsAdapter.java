package com.afranques.quovis;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by AMFG on 6/15/2016.
 */
public class MainListRowsAdapter extends ArrayAdapter<ArrayList<String>> {
    private final Context context;
    private List<String> items; //we store the titles
    private List<String> itemsDes; //we store the descriptions
    private List<String> itemsPicLoc; //we store the pic locations
    ArrayList<ArrayList<String>> listOfLists = new ArrayList<ArrayList<String>>();

    public MainListRowsAdapter(Context context, ArrayList<ArrayList<String>> listOfLists) {
        super(context, -1, listOfLists);
        this.context = context;
        this.listOfLists = listOfLists;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.main_list_row, parent, false);
        TextView textView = (TextView) rowView.findViewById(R.id.title_row);
        TextView textView2 = (TextView) rowView.findViewById(R.id.rating_row);
        TextView textView3 = (TextView) rowView.findViewById(R.id.genre_row);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.thumbnail_row);
        textView.setText(listOfLists.get(position).get(0));
        textView2.setText(listOfLists.get(position).get(1));
        textView3.setText("Urbana, US");

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bitmap = BitmapFactory.decodeFile(listOfLists.get(position).get(2), options);
        imageView.setImageBitmap(bitmap);

        return rowView;
    }
}

