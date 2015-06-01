package com.fiktivo.restros;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.fiktivo.restros.PlacesContract.PlacesEntry;

public class PlacesDBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 2;
    static final String DATABASE_NAME = "restros.db";

    public PlacesDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_PLACES_TABLE = "CREATE TABLE " + PlacesEntry.TABLE_NAME + " (" +
                PlacesEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                PlacesEntry.COLUMN_NAME_Coupons_Number + " REAL NOT NULL, " +
                PlacesEntry.COLUMN_NAME_Place_ID + " TEXT NOT NULL, " +
                PlacesEntry.COLUMN_NAME_Place_Name + " TEXT NOT NULL, " +
                PlacesEntry.COLUMN_NAME_Logo_URL + " TEXT NOT NULL, " +
                PlacesEntry.COLUMN_NAME_Place_Latitude + " REAL NOT NULL, " +
                PlacesEntry.COLUMN_NAME_Place_Longitude + " REAL NOT NULL, " +
                "UNIQUE (" + PlacesEntry.COLUMN_NAME_Place_ID + ") ON CONFLICT REPLACE);";

        db.execSQL(SQL_CREATE_PLACES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + PlacesEntry.TABLE_NAME);
        onCreate(db);
    }
}
