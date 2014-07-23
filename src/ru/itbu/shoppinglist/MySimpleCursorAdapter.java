package ru.itbu.shoppinglist;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

/*
 * Дело в том, что каждый раз для перезаполнения отображаемой строки списка нам необходимо вызывать метод findViewById()
 * для того, чтобы находить интересующие нас TextEdit‘ы и прочие элементы, в которые мы собираемся писать. 
 * А это очень накладно и негативно влияет на performance. Для решения этой проблемы, был придуман замечательный подход: 
 * записывать информацию о элементах в тег каждого вьюва, используя вспомогательный nested-класс ViewHolder.
 * */

public class MySimpleCursorAdapter extends SimpleCursorAdapter {
	
	Context context;
	Activity activity;
	SQLiteDatabase db;
	EditText editText;
	static byte flag = 0;
	static Object id = 0;
	InputMethodManager imm;
	LayoutInflater inflater;
	
	public MySimpleCursorAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags, ItemsDB db, EditText editText) {
		super(context, layout, c, from, to, flags);
		this.context = context;
		this.editText = editText;
		activity = (Activity) context;
		this.db = db.getWritableDatabase();
		imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE); 
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	static class ViewHolder {
		public TextView tvText;
		public ImageButton btnDeleteItem;
		public ImageButton btnEditItem;
	}
	
	public static byte getFlag() {
		return flag;		
	}
	
	public static void setFlag() {
		flag = 0;		
	}
	
	public static Object getId() {
		return id;
	}
	
	@Override
	// Здесь parent - это сам ListView, а convertView - это null или уже готовая вьюшка, которую надо перезаполнить.
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		final ViewHolder viewHolder;
		if (view == null) {
            view = inflater.inflate(R.layout.item, null);
            viewHolder = new ViewHolder(); 	
            viewHolder.tvText = (TextView) view.findViewById(R.id.tvText);
            viewHolder.btnDeleteItem = (ImageButton) view.findViewById(R.id.btnDeleteItem);
            viewHolder.btnEditItem = (ImageButton) view.findViewById(R.id.btnEditItem);
			view.setTag(viewHolder); // записываем иформацию об элементах в тег каждого вью
		}
		else
			viewHolder = (ViewHolder) view.getTag(); 	
		
		Cursor cursor = getCursor();
		cursor.moveToPosition(position);
		viewHolder.tvText.setText(cursor.getString(cursor.getColumnIndex(ItemsDB.ITEM_NAME)));
		viewHolder.btnDeleteItem.setTag(getItemId(position)); // возвращает уникальный идентификатор элемента по его позиции в списке 
		viewHolder.btnDeleteItem.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				db.delete(ItemsDB.ITEM_TABLE, ItemsDB.ID + "=" + viewHolder.btnDeleteItem.getTag(), null);
				activity.getLoaderManager().getLoader(0).forceLoad();
			}
		});
		
		viewHolder.btnEditItem.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String itemName = viewHolder.tvText.getText().toString();					
				editText.setText(itemName);
				flag = 1;
				id = viewHolder.btnDeleteItem.getTag();
				imm.showSoftInput(editText, 0);
			}
		});
		return view;	
	}
}
