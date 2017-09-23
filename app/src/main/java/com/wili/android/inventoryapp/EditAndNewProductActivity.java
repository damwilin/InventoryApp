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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.wili.android.inventoryapp.data.InventoryContract.InventoryEntry;

public class EditAndNewProductActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int EXISTING_LOADER_ID = 0;
    private static final int RESULT_SELECT_PICTURE = 1;
    private EditText productNameEditText;
    private EditText productPriceEditText;
    private EditText productQuantityEditText;
    private EditText productSupplierEditText;
    private Button productChooseImage;
    private Uri currentProductUri;
    private Uri productImageUri;
    private ImageView productImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_and_new_product);

        productNameEditText = (EditText) findViewById(R.id.edit_product_name);
        productPriceEditText = (EditText) findViewById(R.id.edit_product_price);
        productQuantityEditText = (EditText) findViewById(R.id.edit_product_quantity);
        productSupplierEditText = (EditText) findViewById(R.id.edit_product_supplier);
        productImageView = (ImageView) findViewById(R.id.productImageView);

        productChooseImage = (Button) findViewById(R.id.button_image);
        productChooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickImage();
            }
        });

        //Check if edit mode or create mode
        currentProductUri = getIntent().getData();
        if (currentProductUri != null) {
            setTitle(getString(R.string.product_edit));
            getLoaderManager().initLoader(EXISTING_LOADER_ID, null, this);
        } else
            setTitle(getString(R.string.product_add));
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
                showDeleteConfirmationDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (currentProductUri == null) {
            MenuItem menuItem = menu.findItem(R.id.delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        showCancelConfirmationDialog();
    }

    private void showCancelConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Do you want to discard changes?");
        builder.setPositiveButton(R.string.discard_confirmation, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.setNegativeButton(R.string.cencel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialog != null)
                    return;
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
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
        if (productImageUri != null)
            values.put(InventoryEntry.COLUMN_PRODUCT_IMAGE, productImageUri.toString());

        if (currentProductUri == null) {
            Uri insertedRow = getContentResolver().insert(InventoryEntry.CONTENT_URI, values);
            if (insertedRow != null)
                Toast.makeText(this, R.string.add_successful, Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(this, R.string.add_error, Toast.LENGTH_SHORT).show();
        } else {
            int updatedRow = getContentResolver().update(currentProductUri, values, null, null);
            if (updatedRow != 0)
                Toast.makeText(this, R.string.update_successful, Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(this, R.string.edit_update_error, Toast.LENGTH_SHORT).show();

        }
        finish();
    }

    private void deleteProduct() {
        if (currentProductUri != null)
            getContentResolver().delete(currentProductUri, null, null);
    }


    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_confirmation_question);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteProduct();
                startActivity(new Intent(EditAndNewProductActivity.this, CatalogActivity.class));
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

    private void pickImage() {
        Intent intentImage = new Intent();
        intentImage.setType("image/*");
        intentImage.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intentImage, "Select image"), RESULT_SELECT_PICTURE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK)
            if (requestCode == RESULT_SELECT_PICTURE) {
                productImageUri = data.getData();
                Picasso.with(getBaseContext()).load(productImageUri).into(productImageView);
            }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, currentProductUri, InventoryEntry.STANDARD_PROJECTION, null, null, null);
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
            int columnIndexSupplier = data.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_SUPPLIER);
            int columnIndexImage = data.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_IMAGE);

            //Get attributes from cursor
            String productName = data.getString(columnIndexName);
            double productPrice = data.getDouble(columnIndexPrice);
            int productQuantity = data.getInt(columnIndexQuantity);
            String productSupplier = data.getString(columnIndexSupplier);
            String productImage = data.getString(columnIndexImage);
            if (productImage != null)
                productImageUri = Uri.parse(data.getString(columnIndexImage));

            //Set attributes to views
            productNameEditText.setText(productName);
            productPriceEditText.setText(Double.toString(productPrice));
            productQuantityEditText.setText(Integer.toString(productQuantity));
            productSupplierEditText.setText(productSupplier);
            if (productImageUri != null) {
                Picasso.with(getBaseContext()).load(productImageUri).into(productImageView);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        productNameEditText.setText(null);
        productPriceEditText.setText(null);
        productQuantityEditText.setText(null);
        productSupplierEditText.setText(null);
    }
}

