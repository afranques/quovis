package com.afranques.quovis;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by afranques44 on 6/5/2016.
 */

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "QuovisDB.db";
    public static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE Categories (" +
                "category_id INTEGER primary key AUTOINCREMENT," +
                "category_name TEXT)");

        db.execSQL("CREATE TABLE Places (" +
                "place_id INTEGER primary key AUTOINCREMENT," +
                "place_title TEXT," +
                "place_description TEXT," +
                "category_id INTEGER," +
                "latitude FLOAT(10,7)," +
                "longitude FLOAT(10,7)," +
                "pic_location TEXT )");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Categories");
        onCreate(db);
    }

    public boolean insertData(String category) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("category_name", category);
        long result = db.insert("Categories", null, contentValues);
        if(result == -1)
            return false;
        else
            return true;
    }

    public Integer deleteCategory(int cat_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("Categories", "category_id = "+cat_id, null);
    }

    public boolean insertPlace(String place_title, String place_description, int category_id,
                               double latitude, double longitude, String pic_location) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("place_title", place_title);
        contentValues.put("place_description", place_description);
        contentValues.put("category_id", category_id);
        contentValues.put("latitude", latitude);
        contentValues.put("longitude", longitude);
        contentValues.put("pic_location", pic_location);
        long result = db.insert("Places", null, contentValues);
        if(result == -1)
            return false;
        else
            return true;
    }

    public Cursor getAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from Categories;", null);
        return res;
    }

    public Cursor getAllPlaces() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from Places;", null);
        return res;
    }

    public Cursor getPlace(int position) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from Places WHERE place_id="+(position+1), null);
        return res;
    }
}
