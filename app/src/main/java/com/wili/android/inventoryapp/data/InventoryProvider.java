package com.wili.android.inventoryapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.wili.android.inventoryapp.R;

import static com.wili.android.inventoryapp.data.InventoryContract.CONTENT_AUTHORITY;
import static com.wili.android.inventoryapp.data.InventoryContract.InventoryEntry;

/**
 * Created by Damian on 9/22/2017.
 */

public class InventoryProvider extends ContentProvider {
    //URI matcher code for content URI for inventory table
    public static final int PRODUCTS_ALL = 100;
    //URI matcher code for content URI for single product
    public static final int PRODUCT = 101;
    //URI matcher code to match a context URI to code
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    //Static initializer
    static {
        sUriMatcher.addURI(CONTENT_AUTHORITY, InventoryContract.PATH_PRODUCT, PRODUCTS_ALL);
        sUriMatcher.addURI(CONTENT_AUTHORITY, InventoryContract.PATH_PRODUCT + "/#", PRODUCT);
    }

    //Tag for log messages
    public final String LOG_TAG = InventoryProvider.class.getSimpleName();
    public InventoryDbHelper mInventoryDbHelper;

    @Override
    public boolean onCreate() {
        mInventoryDbHelper = new InventoryDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = mInventoryDbHelper.getReadableDatabase();
        Cursor cursor;
        int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS_ALL:
                cursor = db.query(InventoryEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case PRODUCT:
                selection = InventoryEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = db.query(InventoryEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException(Resources.getSystem().getString(R.string.unkown_uri));
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS_ALL:
                return InventoryEntry.CONTENT_LIST_TYPE;
            case PRODUCT:
                return InventoryEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException(Resources.getSystem().getString(R.string.unkown_uri));
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS_ALL:
                return insertProduct(uri, values);
            default:
                throw new IllegalArgumentException(Resources.getSystem().getString(R.string.insert_error) + uri);
        }
    }

    private Uri insertProduct(Uri uri, ContentValues values) {
        SQLiteDatabase db = mInventoryDbHelper.getWritableDatabase();

        //Extract attributes from ContentValues
        String productName = values.getAsString(InventoryEntry.COLUMN_PRODUCT_NAME);
        if (productName == null)
            throw new IllegalArgumentException(String.valueOf(R.string.null_name));

        Double price = values.getAsDouble(InventoryEntry.COLUMN_PRODUCT_PRICE);
        if (price == null || price <= 0)
            throw new IllegalArgumentException(String.valueOf(R.string.null_price));

        Integer quantity = values.getAsInteger(InventoryEntry.COLUMN_PRODUCT_QUANTITY);
        if (quantity == null || quantity == 0)
            throw new IllegalArgumentException(String.valueOf(R.string.null_quantity));

        String productSupplier = values.getAsString(InventoryEntry.COLUMN_PRODUCT_SUPPLIER);
        if (productSupplier == null)
            throw new IllegalArgumentException(String.valueOf(R.string.null_supplier));

        long id = db.insert(InventoryEntry.TABLE_NAME, null, values);
        if (id == -1) {
            Log.e(LOG_TAG, String.valueOf(R.string.insert_error) + uri);
            return null;
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mInventoryDbHelper.getWritableDatabase();
        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS_ALL:
                rowsDeleted = db.delete(InventoryEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case PRODUCT:
                selection = InventoryEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = db.delete(InventoryEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException(String.valueOf(R.string.delete_error) + uri);
        }
        if (rowsDeleted != 0)
            getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS_ALL:
                return updateInventory(uri, values, selection, selectionArgs);
            case PRODUCT:
                selection = InventoryEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateInventory(uri, values, selection, selectionArgs);
            default:
                throw new IllegalArgumentException(String.valueOf(R.string.update_error) + uri);
        }
    }

    private int updateInventory(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mInventoryDbHelper.getWritableDatabase();
        int rowsUpdated;

        if (values.containsKey(InventoryEntry.COLUMN_PRODUCT_NAME)) {
            String name = values.getAsString(InventoryEntry.COLUMN_PRODUCT_NAME);
            if (name == null)
                return 0;
        }

        if (values.containsKey(InventoryEntry.COLUMN_PRODUCT_PRICE)) {
            Double price = values.getAsDouble(InventoryEntry.COLUMN_PRODUCT_PRICE);
            if (price == null || price == 0)
                return 0;
        }

        if (values.size() == 0)
            return 0;

        rowsUpdated = db.update(InventoryEntry.TABLE_NAME, values, selection, selectionArgs);

        if (rowsUpdated != 0)
            getContext().getContentResolver().notifyChange(uri, null);

        return rowsUpdated;
    }
}
