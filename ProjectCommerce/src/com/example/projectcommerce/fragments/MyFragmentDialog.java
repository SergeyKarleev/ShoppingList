package com.example.projectcommerce.fragments;

import com.example.projectcommerce.R;
import com.example.projectcommerce.classes.MyDBManager;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

public class MyFragmentDialog extends DialogFragment implements OnClickListener {

	// поля ввода данных о модели
	private EditText etName;
	private EditText etCategory;
	private EditText etLot;
	private EditText etCount;
	private EditText etUnit;
	

	// действие диалога: добавление или изменение элемента
	private int mAction = 0;
	private final int ACTION_ADD = 1;
	private final int ACTION_UPDATE = 2;

	// необходим для вызова метода getRow фрагмента
	private static MyFragmentBackend sContext;

	// содержит значение выбранного устройства из фрагмента
	private static Cursor sData;

	/**
	 * Конструктор вызывается при повороте экрана
	 */
	public MyFragmentDialog() {
		super();
	}

	/**
	 * Данный конструктор вызывается при создании нового элемента
	 * 
	 * @param context
	 *            по-умолчанию передавать контекст фрагмента Backend (this)
	 */
	public MyFragmentDialog(MyFragmentBackend context) {
		super();
		this.sContext = context;
		mAction = ACTION_ADD;
	}

	/**
	 * Данный конструктор вызывается при редактировании существующего элемента
	 * 
	 * @param context
	 *            по-умолчанию передавать контекст фрагмента (this)
	 * @param data
	 *            курсор с данными из выбранной строки
	 */
	public MyFragmentDialog(MyFragmentBackend context, Cursor data) {
		super();
		this.sContext = context;
		this.sData = data;
		mAction = ACTION_UPDATE;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		getDialog().setTitle(R.string.dialogTitle);
		View v = inflater.inflate(R.layout.fr_dialog, null);
		v.findViewById(R.id.btnCancel).setOnClickListener(this);
		v.findViewById(R.id.btnSave).setOnClickListener(this);

		etName = (EditText) v.findViewById(R.id.etName);		
		etCount = (EditText) v.findViewById(R.id.etCount);

		// восстанавливаем значение Act (тип действия: добавить/изменить) и ID
		// устройства
		if (savedInstanceState != null)
			mAction = savedInstanceState.getInt("act");

		if (mAction == ACTION_UPDATE) {

			long id = sData.getInt(sData
					.getColumnIndex(MyDBManager.PRODUCTS_ID));
			String name = sData.getString(sData
					.getColumnIndex(MyDBManager.PRODUCTS_NAME));
			float price = sData.getFloat(sData
					.getColumnIndex(MyDBManager.PRODUCTS_PRICE));
			int count = sData.getInt(sData
					.getColumnIndex(MyDBManager.PRODUCTS_COUNT));

			etName.setText(name);
			etPrice.setText(String.valueOf(price));
			etCount.setText(String.valueOf(count));
		}
		return v;
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
			if (etName.getText().length() != 0
					&& etPrice.getText().length() != 0
					&& etCount.getText().length() != 0) {

				ContentValues cv = new ContentValues();
				if (mAction == ACTION_UPDATE)
					cv.put("_id", sData.getInt(sData
							.getColumnIndex(MyDBManager.PRODUCTS_ID)));
				cv.put("name", etName.getText().toString());
				cv.put("price", Float.parseFloat(etPrice.getText().toString()));
				cv.put("count", Integer.parseInt(etCount.getText().toString()));
				etName.setText(null);
				etPrice.setText(null);
				etCount.setText(null);
				sContext.onGetRow(cv, mAction);
				dismiss();
			} else
				Toast.makeText(getActivity(), "Все поля должны быть заполнены",
						Toast.LENGTH_SHORT).show();
		default:
			break;
		}
	}

}
