package com.wili.android.inventoryapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static android.R.attr.version;
import static com.wili.android.inventoryapp.data.InventoryContract.InventoryEntry.*;

/**
 * Created by Damian on 9/21/2017.
 */

public class InventoryDbHelper extends SQLiteOpenHelper {
    //Database name
    public static final String DATABASE_NAME = "Inventory.db";
    //Database version
    public static final int DATABASE_VERSION = 1;

    public InventoryDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_ENTRIES = "CREATE TABLE " + TABLE_NAME + "(" +
                _ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"+
                COLUMN_PRODUCT_NAME + TEXT_TYPE + COMMA_SEP +
                COLUMN_PRODUCT_IMAGE + TEXT_TYPE + COMMA_SEP +
                COLUMN_PRODUCT_PRICE + INTEGER_TYPE +COMMA_SEP +
                COLUMN_PRODUCT_QUANTITY + INTEGER_TYPE + ")";

        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXIST" + TABLE_NAME);
        onCreate(db);

    }
}
