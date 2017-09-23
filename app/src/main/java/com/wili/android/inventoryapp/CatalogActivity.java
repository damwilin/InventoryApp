package com.wili.android.inventoryapp;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import static com.wili.android.inventoryapp.data.InventoryContract.InventoryEntry;

public class CatalogActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {


    InventoryCursorAdapter mInventoryCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        //Setup FAB
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.new_product_button);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CatalogActivity.this, EditAndNewProductActivity.class));
            }
        });

        //Find the view, which contains ListView
        ListView productsListView = (ListView) findViewById(R.id.products_list);
        //Find the view, which contains empty view
        TextView emptyView = (TextView) findViewById(R.id.empty_view);
        //Set that view as empty view in ListView
        productsListView.setEmptyView(emptyView);
        mInventoryCursorAdapter = new InventoryCursorAdapter(this, null);
        productsListView.setAdapter(mInventoryCursorAdapter);

        productsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getBaseContext(), DetailActivity.class);
                Uri currentProductUri = ContentUris.withAppendedId(InventoryEntry.CONTENT_URI, id);
                intent.setData(currentProductUri);
                startActivity(intent);
            }
        });

        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.insert_dummy_data:
                insertDummyData();
                return true;
            case R.id.delete_all_data:
                showDeleteConfirmationDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void insertDummyData() {
        ContentValues values = new ContentValues();
        values.put(InventoryEntry.COLUMN_PRODUCT_NAME, "New Product");
        values.put(InventoryEntry.COLUMN_PRODUCT_PRICE, 2.54);
        values.put(InventoryEntry.COLUMN_PRODUCT_QUANTITY, 10);
        values.put(InventoryEntry.COLUMN_PRODUCT_SUPPLIER, "Old Supplier");
        getContentResolver().insert(InventoryEntry.CONTENT_URI, values);
        Toast.makeText(this, R.string.insert_dummy, Toast.LENGTH_SHORT).show();
    }

    private void deleteAllData() {
        int rowsDeleted = getContentResolver().delete(InventoryEntry.CONTENT_URI, null, null);
        Toast.makeText(this, rowsDeleted + R.string.deleted_all, Toast.LENGTH_SHORT).show();
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_all_confirmation_question);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteAllData();
            }
        });
        builder.setNegativeButton(R.string.cencel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialog != null)
                    dialog.dismiss();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, InventoryEntry.CONTENT_URI, InventoryEntry.STANDARD_PROJECTION, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mInventoryCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mInventoryCursorAdapter.swapCursor(null);
    }
}
