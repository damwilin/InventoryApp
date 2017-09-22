package com.wili.android.inventoryapp;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import static com.wili.android.inventoryapp.data.InventoryContract.InventoryEntry;

public class CatalogActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        //Setup FAB
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.new_product_button);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CatalogActivity.this, EditActivity.class));
            }
        });

        //Find the view, which contains ListView
        ListView productsListView = (ListView) findViewById(R.id.products_list);
        //Find the view, which contains empty view
        TextView emptyView = (TextView) findViewById(R.id.empty_view);
        //Set that view as empty view in ListView
        productsListView.setEmptyView(emptyView);
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
                deleteAllData();
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
        getContentResolver().insert(InventoryEntry.CONTENT_URI, values);
        Toast.makeText(this, R.string.insert_dummy, Toast.LENGTH_SHORT).show();
    }

    private void deleteAllData() {
        int rowsDeleted = getContentResolver().delete(InventoryEntry.CONTENT_URI, null, null);
        Toast.makeText(this, rowsDeleted + R.string.deleted_all, Toast.LENGTH_SHORT).show();
    }
}
