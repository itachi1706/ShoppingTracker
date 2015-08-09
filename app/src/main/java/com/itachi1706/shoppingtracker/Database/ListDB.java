package com.itachi1706.shoppingtracker.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.itachi1706.shoppingtracker.Objects.ListCategory;
import com.itachi1706.shoppingtracker.Objects.ListItem;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Kenneth on 1/8/2015
 * for Shopping Tracker in package com.itachi1706.shoppingtracker.Database
 */
public class ListDB extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;

    //DB Name
    public static final String DB_NAME = "products.db";

    //DB Table
    public static final String TABLE_PRODUCT = "products";
    public static final String TABLE_CATEGORY = "categories";

    //DB Keys
    public static final String PRODUCT_KEY = "key";
    public static final String PRODUCT_NAME = "title";
    public static final String PRODUCT_BARCODE = "barcode";
    public static final String PRODUCT_CATEGORY = "category";

    public static final String CATEGORY_KEY = "key";
    public static final String CATEGORY_NAME = "name";

    public ListDB(Context context)
    {
        super(context, context.getExternalFilesDir(null) + File.separator + DB_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_PRODUCT_TABLE = "CREATE TABLE " + TABLE_PRODUCT + "(" + PRODUCT_KEY + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + PRODUCT_NAME + " TEXT NOT NULL," + PRODUCT_BARCODE + " TEXT," + PRODUCT_CATEGORY + " INTEGER," +
                " FOREIGN KEY(" + PRODUCT_CATEGORY + ") REFERENCES " + TABLE_CATEGORY + "(" + CATEGORY_KEY + "));";
        String CREATE_CATEGORY_TABLE = "CREATE TABLE " + TABLE_CATEGORY + "(" + CATEGORY_KEY + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + CATEGORY_NAME + " TEXT NOT NULL);";

        db.execSQL(CREATE_CATEGORY_TABLE);
        db.execSQL(CREATE_PRODUCT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORY);
        onCreate(db);
    }

    public void dropEverythingAndRebuild()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORY);
        onCreate(db);
    }


    /*
    Add Category - D
    Add Item - D
    Edit Category - D
    Edit Item - D
    Remove Category - D
    Remove Item - D
    Check if Exists Category - D
    Check if Exists Item - D
    Get All Categories - D
    Get All Products - D
    Get All Products from Category ID - D
    Get Product From ID - D
    Get Category from ID - D
     */



    private long addCategory(ListCategory cat)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(CATEGORY_NAME, cat.getName());

        long value = db.insert(TABLE_CATEGORY, null, cv);
        db.close();

        return value;
    }

    public void updateCategory(ListCategory cat)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(CATEGORY_NAME, cat.getName());
        cv.put(CATEGORY_KEY, cat.getId());

        db.replace(TABLE_CATEGORY, null, cv);
        db.close();
    }

    public void deleteCategory(ListCategory cat)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_CATEGORY, CATEGORY_KEY + "=" + cat.getId(), null);
    }

    public boolean checkCategoryExists(ListCategory cat)
    {
        String category = cat.getName();
        String query = "SELECT * FROM " + TABLE_CATEGORY + " WHERE " + CATEGORY_NAME + "=" + category + ";";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        int count = cursor.getCount();
        cursor.close();
        db.close();
        return count != 0;
    }

    /**
     * If record isn't present in DB, add it
     * @param category Category to check
     * @return Category generated ID
     */
    public long addCategoryToDB(ListCategory category)
    {
        return addCategory(category);
    }


    private long addProduct(ListItem item)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(PRODUCT_NAME, item.getName());
        cv.put(PRODUCT_BARCODE, item.getBarcode());
        cv.put(PRODUCT_CATEGORY, item.getCategory());

        long value = db.insert(TABLE_PRODUCT, null, cv);
        db.close();

        return value;
    }

    public void updateProduct(ListItem item)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(PRODUCT_NAME, item.getName());
        cv.put(PRODUCT_BARCODE, item.getBarcode());
        cv.put(PRODUCT_CATEGORY, item.getCategory());
        cv.put(PRODUCT_KEY, item.getId());

        db.replace(TABLE_PRODUCT, null, cv);
        db.close();
    }

    public void deleteProduct(ListItem item)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_PRODUCT, PRODUCT_KEY + "=" + item.getId(), null);
    }

    public boolean checkProductExists(ListItem cat)
    {
        String category = cat.getName();
        String query = "SELECT * FROM " + TABLE_PRODUCT + " WHERE " + PRODUCT_NAME + "=" + category + ";";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        int count = cursor.getCount();
        cursor.close();
        db.close();
        return count != 0;
    }

    /**
     * If record isn't present in DB, add it
     * @param product Product to check
     * @return Product generated ID
     */
    public long addProductToDB(ListItem product)
    {
        return addProduct(product);
    }


    /**
     * Internal method to parse cursor for list category easy edit next time
     * @param cursor cursor object
     * @return category object
     */
    private ListCategory generateCategoryFromCursor(Cursor cursor)
    {
        ListCategory category = new ListCategory();
        category.setId(cursor.getInt(0));
        category.setName(cursor.getString(1));
        return category;
    }

    /**
     * Internal method to parse cursor for list items for easy edit next time
     * @param cursor cursor object
     * @return item object
     */
    private ListItem generateItemFromCursor(Cursor cursor)
    {
        ListItem item = new ListItem();
        item.setId(cursor.getInt(0));
        item.setName(cursor.getString(1));
        item.setBarcode(cursor.getString(2));

        item.setCategory(cursor.getInt(3));
        return item;
    }

    /**
     * Gets the category object from ID
     * @param id id of cat
     * @return category object
     */
    public ListCategory getCategoryFromId(int id)
    {
        String query = "SELECT * FROM " + TABLE_CATEGORY + " WHERE " + CATEGORY_KEY + "=" + id + ";";
        SQLiteDatabase db = this.getReadableDatabase();
        ListCategory category = new ListCategory();

        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst())
        {
            do
            {
                category = generateCategoryFromCursor(cursor);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return category;
    }

    /**
     * Gets the item object from ID
     * @param id id of item
     * @return item object
     */
    public ListItem getItemFromId(int id)
    {
        String query = "SELECT * FROM " + TABLE_PRODUCT + " WHERE " + PRODUCT_KEY + "=" + id + ";";
        SQLiteDatabase db = this.getReadableDatabase();
        ListItem item = new ListItem();

        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst())
        {
            do
            {
                item = generateItemFromCursor(cursor);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return item;
    }

    public boolean isEmptyItems()
    {
        String query = "SELECT * FROM " + TABLE_PRODUCT;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        int count = cursor.getCount();
        cursor.close();
        db.close();
        return count == 0;
    }

    public boolean isEmptyCategory()
    {
        String query = "SELECT * FROM " + TABLE_CATEGORY;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        int count = cursor.getCount();
        cursor.close();
        db.close();
        return count == 0;
    }

    /**
     * Get All products by category
     * @param category category of product
     * @return list of products with the same category
     */
    public ArrayList<ListItem> getAllItemsByCategory(ListCategory category)
    {
        String query = "SELECT * FROM " + TABLE_PRODUCT + " WHERE " + PRODUCT_CATEGORY + "=" + category.getId() + ";";
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<ListItem> results = new ArrayList<>();

        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst())
        {
            do
            {
                ListItem prod = generateItemFromCursor(cursor);
                results.add(prod);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return results;
    }

    /**
     * Get All products by name
     * @param name name of product
     * @return list of products with the same name
     */
    public ArrayList<ListItem> getAllProducts(String name)
    {
        String query = "SELECT * FROM " + TABLE_PRODUCT + " WHERE " + PRODUCT_NAME + "=" + name + ";";
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<ListItem> results = new ArrayList<>();

        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst())
        {
            do
            {
                ListItem prod = generateItemFromCursor(cursor);
                results.add(prod);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return results;
    }

    /**
     * Get All Products
     * @return list of products with the same name
     */
    public ArrayList<ListItem> getAllItemsByName()
    {
        String query = "SELECT * FROM " + TABLE_PRODUCT + ";";
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<ListItem> results = new ArrayList<>();

        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst())
        {
            do
            {
                ListItem prod = generateItemFromCursor(cursor);
                results.add(prod);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return results;
    }

    /**
     * Get All Categories
     * @return list of products with the same name
     */
    public ArrayList<ListCategory> getAllCategories()
    {
        String query = "SELECT * FROM " + TABLE_CATEGORY + ";";
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<ListCategory> results = new ArrayList<>();

        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst())
        {
            do
            {
                ListCategory prod = generateCategoryFromCursor(cursor);
                results.add(prod);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return results;
    }
}
