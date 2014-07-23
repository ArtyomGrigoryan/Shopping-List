package ru.itbu.shoppinglist;

import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.ContentValues;
import android.content.Context;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class MainActivity extends Activity implements LoaderCallbacks<Cursor> {

	byte flag = 0;
	Dialog dialog;
	EditText edText;
	ListView lvMain;
	ItemsDB itemsDB;
	SQLiteDatabase db;
	Button btnDeleteAll;
	InputMethodManager imm;
	AdapterContextMenuInfo acmi;
	MySimpleCursorAdapter adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		String[] from = new String[] { ItemsDB.ITEM_NAME };
		int[] to = new int[] { R.id.tvText };
		// получаем доступ к клавиатуре. Метод возвращает Object
		imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);  
		
		edText = (EditText) findViewById(R.id.edText);
		lvMain = (ListView) findViewById(R.id.lvMain);
		btnDeleteAll = (Button) findViewById(R.id.btnDeleteAll); 
		
		itemsDB = new ItemsDB(this);
		db = itemsDB.getWritableDatabase();
		
		adapter = new MySimpleCursorAdapter(this, R.layout.item, null, from, to, 0, itemsDB, edText);
		lvMain.setAdapter(adapter);
		
		dialog = new Dialog();
		dialog.setDb(itemsDB);
		
		getLoaderManager().initLoader(0, null, this); // создаст и вернет нам лоадер. id лоадера и класс, где реализованы методы интерфейса
				
		btnDeleteAll.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dialog.show(getFragmentManager(), "dialog");
			}
		});
		
		edText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			
			@Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) { 	
            	if (actionId == EditorInfo.IME_ACTION_DONE && !edText.getText().toString().equals("")) {
            		if (getCurrentFocus() != null) {
            			flag = MySimpleCursorAdapter.getFlag();
            			Object id = MySimpleCursorAdapter.getId(); 
            			ContentValues cv = new ContentValues();
        				cv.put(ItemsDB.ITEM_NAME, edText.getText().toString()); // указываем в какое поле таблицы и какое значение хотим запихнуть
            			if (flag == 0) 
            				db.insert(ItemsDB.ITEM_TABLE, null, cv); 
            			else if(flag == 1) {
                            db.update(ItemsDB.ITEM_TABLE, cv, ItemsDB.ID + "=?", new String[] { String.valueOf(id) });
                            MySimpleCursorAdapter.setFlag();
            			}
            			edText.setText("");
            			imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            			getLoaderManager().getLoader(0).forceLoad();
	                }
	            }    	
                return false;
            }
        });
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	// вызывается, когда нужно создать лоадер (initLoader). На вход передается id лоадера
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle arg1) {
		return new MyCursorLoader(this, itemsDB);
	}
	// вызывается, когда лоадер закончил работу и вернул результат. На вход - лоадер и результат его работы
	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		adapter.swapCursor(cursor); // отдаем курсор с данными адаптеру
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
	}
	
	public void onDestroy() {
		super.onDestroy();
		db.close();
	}
}
