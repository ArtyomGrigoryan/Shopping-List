package ru.itbu.shoppinglist;

import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class MyCursorLoader extends CursorLoader {

	SQLiteDatabase db;
	public MyCursorLoader(Context context, ItemsDB db) {
		super(context);
		this.db = db.getWritableDatabase();
	}

	@Override
	public Cursor loadInBackground() {
		// объект cursor содержит все данные из таблицы
		Cursor cursor = db.query(ItemsDB.ITEM_TABLE, null, null, null, null, null, null);
		
		return cursor;
	}
}
