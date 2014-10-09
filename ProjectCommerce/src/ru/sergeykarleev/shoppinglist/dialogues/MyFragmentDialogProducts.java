package ru.sergeykarleev.shoppinglist.dialogues;

import java.util.ArrayList;
import java.util.Arrays;

import ru.sergeykarleev.shoppinglist.R;
import ru.sergeykarleev.shoppinglist.classes.MyDBManager;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class MyFragmentDialogProducts extends DialogFragment implements
		OnClickListener {

	// Логирование
	private static final String LOG_TAG = "myLogs";

	// действие с продуктом: добавление нового или модификация/удаление текущего
	public final static int ACTION_ADD = 1;
	public final static int ACTION_UPDATE = 2;

	// поля ввода данных о модели
	private EditText etName;
	private Spinner spCategory;
	private CheckBox chkDeleteProduct;

	// действие диалога: добавление или изменение элемента
	private int mAction = 0;

	// содержит значение выбранного устройства из фрагмента
	private static Cursor sData;

	private MyDBManager mDB;
	
	/**
	 * Конструктор создания диалога
	 * 
	 * @param mAction
	 */
	public MyFragmentDialogProducts(int mAction) {
		super();
		this.mAction = mAction;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		getDialog().setTitle(R.string.dialogTitle);
		View v = inflater.inflate(R.layout.dialog_product, null);
		v.findViewById(R.id.btnCancel).setOnClickListener(this);
		v.findViewById(R.id.btnSave).setOnClickListener(this);

		mDB = new MyDBManager(getActivity());
		
		etName = (EditText) v.findViewById(R.id.etName);
		spCategory = (Spinner) v.findViewById(R.id.spCategory);
		chkDeleteProduct = (CheckBox) v.findViewById(R.id.chkDeleteProduct);
		spCategory.setAdapter(getAdapter());

		// восстанавливаем значение Act (тип действия: добавить/изменить) и ID
		// устройства
		if (savedInstanceState != null)
			mAction = savedInstanceState.getInt("act");

		if (mAction == ACTION_UPDATE) {

			chkDeleteProduct.setEnabled(true);

			long id = sData.getInt(sData
					.getColumnIndex(MyDBManager.PRODUCTS_ID));
			String name = sData.getString(sData
					.getColumnIndex(MyDBManager.PRODUCTS_NAME));
			int category = sData.getInt(sData
					.getColumnIndex(MyDBManager.PRODUCTS_CATEGORY));
			etName.setText(name);
			spCategory.setSelection(category);
		}
		return v;
	}

	private SimpleCursorAdapter getAdapter() {
		String[] from = new String[] { MyDBManager.CATEGORY_NAME };
		int[] to = new int[] { android.R.id.text1 };		
		Cursor cursor = mDB.getCategories();
		SimpleCursorAdapter scAdapter = new SimpleCursorAdapter(getActivity(),
				android.R.layout.simple_dropdown_item_1line, cursor, from, to);
		return scAdapter;
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		mDB.close();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putInt("act", mAction);
		super.onSaveInstanceState(outState);

	}

	@Override
	public void onCancel(DialogInterface dialog) {
		super.onCancel(dialog);
		dismiss();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnCancel:
			dismiss();
			break;
		case R.id.btnSave:

			// обработка клика на кнопке Сохранить (метод onGetRow в fr_backend)
			if (etName.getText().length() != 0) {

				String testName = etName.getText().toString();
				
				ContentValues cv = new ContentValues();
				if (mAction == ACTION_UPDATE)
					cv.put(MyDBManager.PRODUCTS_ID, sData.getInt(sData
							.getColumnIndex(MyDBManager.PRODUCTS_ID)));
				cv.put(MyDBManager.PRODUCTS_NAME, etName.getText().toString());
				cv.put(MyDBManager.PRODUCTS_CATEGORY,
						spCategory.getSelectedItemPosition()+1);
				etName.setText(null);
				spCategory.setSelection(0);
				RecordData(cv);
			} else
				Toast.makeText(getActivity(), "Все поля должны быть заполнены",
						Toast.LENGTH_SHORT).show();
		default:
			break;
		}
	}

	private void RecordData(ContentValues cv) {
		MyDBManager mDB = new MyDBManager(getActivity());
		switch (mAction) {
		case ACTION_ADD:
			try {
				mDB.addRecord(cv);
				Log.d(LOG_TAG, "Объект записан в базу данных");
				} catch (Exception e) {
				Log.d(LOG_TAG,
						"Объект не был записан в базу данных.\n" + e.toString());
				e.printStackTrace();
			}
			break;
		case ACTION_UPDATE:
			try {
				mDB.editRecord(cv);
				Log.d(LOG_TAG, "Запись в базе отредактирована");
			} catch (Exception e) {
				Log.d(LOG_TAG, "Исправления не были внесены в базу данных.\n"
						+ e.toString());
				e.printStackTrace();
			}
			break;
		default:			
			break;
		}		
		//TODO: здесь вызываем обновление адаптера в Backend
		dismiss();
	}

}
