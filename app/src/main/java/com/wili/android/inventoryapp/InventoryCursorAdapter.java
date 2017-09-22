package com.wili.android.inventoryapp;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import static com.wili.android.inventoryapp.data.InventoryContract.InventoryEntry.*;

/**
 * Created by Damian on 9/22/2017.
 */

public class InventoryCursorAdapter extends CursorAdapter {


    public InventoryCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.product_list_item, parent, false);
    }

    @Override
    public void bindView(final View view, Context context, Cursor cursor) {
        //Find the views
        TextView productNameTextView =(TextView) view.findViewById(R.id.product_name);
        TextView productPriceTextView =(TextView) view.findViewById(R.id.product_price);
        TextView productQuantityTextView =(TextView) view.findViewById(R.id.product_quantity);
        ImageView productImageView = (ImageView) view.findViewById(R.id.product_picture);
        Button productBuyButton = (Button) view.findViewById(R.id.new_product_button);

        //Find the columns indexes
        int indexProductId = cursor.getColumnIndex(_ID);
        int indexProductName = cursor.getColumnIndex(COLUMN_PRODUCT_NAME);
        int indexProductPrice = cursor.getColumnIndex(COLUMN_PRODUCT_PRICE);
        int indexProductQuantity = cursor.getColumnIndex(COLUMN_PRODUCT_QUANTITY);
        int indexProductImage = cursor.getColumnIndex(COLUMN_PRODUCT_IMAGE);

        //Get informations from cursor
        int productId = cursor.getInt(indexProductId);
        String productName = cursor.getString(indexProductName);
        String productPrice = cursor.getString(indexProductPrice);
        final int productQuantity = cursor.getInt(indexProductQuantity);
        String productPicture = cursor.getString(indexProductImage);

        //Create Uri for current product
        final Uri currentProductUri = ContentUris.withAppendedId(CONTENT_URI,productId);

        //Update views
        productNameTextView.setText(productName);
        productPriceTextView.setText(productPrice);
        productQuantityTextView.setText(productQuantity);
        // TODO: 9/22/2017 add update view to product picture

        //Update button
        productBuyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (productQuantity >0){
                    int currentQuantity = productQuantity;
                    ContentValues values = new ContentValues();
                    values.put(COLUMN_PRODUCT_QUANTITY, --currentQuantity);
                    view.getContext().getContentResolver().update(currentProductUri,values,null,null);
                }
                else
                    Toast.makeText(view.getContext(),R.string.no_products,Toast.LENGTH_SHORT).show();
            }
        });

    }
}
