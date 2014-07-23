package ru.itbu.shoppinglist;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ItemsDB extends SQLiteOpenHelper {

	public static final String DB_NAME = "ShoppingList";
	public static final String ITEM_TABLE = "items";
	public static final String ID = "_id";
	public static final String ITEM_NAME = "itemName";
	public static final int DB_VERSION = 1;
	
	public static final String CREATE_DB = "CREATE TABLE "
			+ ITEM_TABLE + " (" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ ITEM_NAME + " text" + ");";
	
	public ItemsDB(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_DB);
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
	}
}
