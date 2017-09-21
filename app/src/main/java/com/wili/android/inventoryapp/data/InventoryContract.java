package com.wili.android.inventoryapp.data;

import android.provider.BaseColumns;

/**
 * Created by Damian on 9/21/2017.
 */

public final class InventoryContract {
    private InventoryContract(){}

    public static class InventoryEntry implements BaseColumns {
        public static final String TABLE_NAME = "inventory";
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_PRODUCT_NAME = "name";
        public static final String COLUMN_PRODUCT_PRICE = "price";
        public static final String COLUMN_PRODUCT_QUANTITY = "quantity";

        public static final String TEXT_TYPE = " TEXT";
        public static final String COMMA_SEP = ",";
    }
}
