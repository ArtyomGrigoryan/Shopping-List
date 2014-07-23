package ru.itbu.shoppinglist;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

public class Dialog extends DialogFragment implements OnClickListener {

	SQLiteDatabase db;
	
	public void setDb(ItemsDB db) {
		this.db = db.getWritableDatabase();
	}

	public AlertDialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity()) // ему нужен Context
															.setTitle("Удаление")
															.setPositiveButton("Да", this)
													        .setNegativeButton("Нет", this)
													        .setMessage("Вы действительно хотите удалить все покупки?");
		return dialog.create();
	}

	@Override
	public void onClick(DialogInterface d, int which) {
		switch(which) {
		case AlertDialog.BUTTON_POSITIVE:
			db.delete(ItemsDB.ITEM_TABLE, null, null);
			getActivity().getLoaderManager().getLoader(0).forceLoad();
			break;
		case AlertDialog.BUTTON_NEGATIVE:
			break;
		}
	}
}
