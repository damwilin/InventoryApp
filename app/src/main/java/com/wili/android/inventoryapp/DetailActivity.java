package com.wili.android.inventoryapp;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
    private int currentProductQuantity;

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

        //Set onClickListener on buttons
        quantityPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentProductQuantity++;
                updateQuantity();
            }
        });
        quantityMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentProductQuantity == 0)
                    return;
                currentProductQuantity--;
                updateQuantity();
            }
        });

    }

    private void updateQuantity() {
        ContentValues values = new ContentValues();
        values.put(InventoryEntry.COLUMN_PRODUCT_QUANTITY, currentProductQuantity);
        getContentResolver().update(currentProductUri, values, null, null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int menuId = item.getItemId();
        switch (menuId) {
            case R.id.edit_current_product:
                openEditor();
                return true;
            case R.id.contact_supplier:
                sendMessage();
                return true;
            case R.id.delete_current_product:
                showDeleteConfirmationDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void sendMessage() {
        StringBuilder sBuilder = new StringBuilder();
        sBuilder.append("Dear " + viewProductSupplier.getText().toString() + "\n");
        sBuilder.append("I want to order " + viewProductName.getText().toString() + "\n");
        sBuilder.append("Thank you!");

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, sBuilder.toString());
        startActivity(intent);
    }

    private void deleteProduct() {
        getContentResolver().delete(currentProductUri, null, null);
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_confirmation_question);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteProduct();
                finish();
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

    private void openEditor() {
        Intent intent = new Intent(this, EditAndNewProductActivity.class);
        intent.setData(currentProductUri);
        startActivity(intent);
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
            currentProductQuantity = data.getInt(columnIndexQuantity);
            int productSales = data.getInt(columnIndexSales);
            String productSupplier = data.getString(columnIndexSupplier);

            //Update the views
            viewProductName.setText(productName);
            viewProductPrice.setText(Double.toString(productPrice));
            viewProductQuantity.setText(Integer.toString(currentProductQuantity));
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
