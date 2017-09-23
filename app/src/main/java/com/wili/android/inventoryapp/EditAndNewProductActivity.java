package com.wili.android.inventoryapp;

import android.content.ContentValues;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.wili.android.inventoryapp.data.InventoryContract.InventoryEntry;

public class EditAndNewProductActivity extends AppCompatActivity {

    private EditText productNameEditText;
    private EditText productPriceEditText;
    private EditText productQuantityEditText;
    private EditText productSupplierEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_and_new_product);

        productNameEditText = (EditText) findViewById(R.id.edit_product_name);
        productPriceEditText = (EditText) findViewById(R.id.edit_product_price);
        productQuantityEditText = (EditText) findViewById(R.id.edit_product_quantity);
        productSupplierEditText = (EditText) findViewById(R.id.edit_product_supplier);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_and_add_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save:
                saveProduct();
                return true;
            case R.id.delete:
                // TODO: 9/23/2017 deleteProduct()
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveProduct() {
        //Get values from EditTexts
        String productName = productNameEditText.getText().toString();
        String productPrice = productPriceEditText.getText().toString();
        String productQuantity = productQuantityEditText.getText().toString();
        String productSupplier = productSupplierEditText.getText().toString();
        if (productName.isEmpty() ||
                productPrice.isEmpty() ||
                productQuantity.isEmpty() ||
                productSupplier.isEmpty()) {
            Toast.makeText(this, getString(R.string.save_error), Toast.LENGTH_SHORT).show();
            return;
        }

        ContentValues values = new ContentValues();
        values.put(InventoryEntry.COLUMN_PRODUCT_NAME, productName);
        values.put(InventoryEntry.COLUMN_PRODUCT_PRICE, productPrice);
        values.put(InventoryEntry.COLUMN_PRODUCT_QUANTITY, productQuantity);
        values.put(InventoryEntry.COLUMN_PRODUCT_SUPPLIER, productSupplier);

        getContentResolver().insert(InventoryEntry.CONTENT_URI, values);
        finish();
    }


}
