package com.wili.android.inventoryapp;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;

import com.wili.android.inventoryapp.data.InventoryContract.InventoryEntry;

public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int EXISTING_LOADER = 0;
    private TextView viewProductName;
    private TextView viewProductPrice;
    private TextView viewProductQuantity;
    private TextView viewProductSupplier;
    private TextView viewProductSales;
    private Button quantityPlus;
    private Button quantityMinus;
    private Uri currentProductUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        //Get current product uri
        currentProductUri = getIntent().getData();

        //Bind views
        viewProductName = (TextView) findViewById(R.id.product_name);
        viewProductPrice = (TextView) findViewById(R.id.product_price);
        viewProductQuantity = (TextView) findViewById(R.id.product_quantity);
        viewProductSupplier = (TextView) findViewById(R.id.product_supplier);
        viewProductSales = (TextView) findViewById(R.id.product_sales);

        quantityMinus = (Button) findViewById(R.id.minus_quantity);
        quantityPlus = (Button) findViewById(R.id.plus_quantity);

        getLoaderManager().initLoader(EXISTING_LOADER, null, this);

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                InventoryEntry._ID,
                InventoryEntry.COLUMN_PRODUCT_NAME,
                InventoryEntry.COLUMN_PRODUCT_PRICE,
                InventoryEntry.COLUMN_PRODUCT_QUANTITY,
                InventoryEntry.COLUMN_PRODUCT_SALES,
                InventoryEntry.COLUMN_PRODUCT_SUPPLIER
        };
        return new CursorLoader(this, currentProductUri, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data == null || data.getCount() < 1)
            return;

        if (data.moveToFirst()) {
            //Find the columns indexes for attributes
            int columnIndexName = data.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_NAME);
            int columnIndexPrice = data.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_PRICE);
            int columnIndexQuantity = data.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_QUANTITY);
            int columnIndexSales = data.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_SALES);
            int columnIndexSupplier = data.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_SUPPLIER);

            //Get values from cursor
            String productName = data.getString(columnIndexName);
            double productPrice = data.getDouble(columnIndexPrice);
            int productQuantity = data.getInt(columnIndexQuantity);
            int productSales = data.getInt(columnIndexSales);
            String productSupplier = data.getString(columnIndexSupplier);

            //Update the views
            viewProductName.setText(productName);
            viewProductPrice.setText(Double.toString(productPrice));
            viewProductQuantity.setText(Integer.toString(productQuantity));
            viewProductSales.setText(Integer.toString(productSales));
            viewProductSupplier.setText(productSupplier);
        }


    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        viewProductName.setText(null);
        viewProductPrice.setText(null);
        viewProductQuantity.setText(null);
        viewProductSales.setText(null);
        viewProductSupplier.setText(null);
    }
}
