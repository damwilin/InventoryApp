package com.wili.android.inventoryapp;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

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
        switch (item.getItemId()){
            case R.id.insert_dummy_data:
                // TODO: 9/21/2017 InsertDummyData()
                return true;
            case R.id.delete_all_data:
                // TODO: 9/21/2017 deleteAllData()
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
