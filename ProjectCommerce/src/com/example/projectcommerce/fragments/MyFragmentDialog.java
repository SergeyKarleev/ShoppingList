package com.example.projectcommerce.fragments;

import java.util.ArrayList;
import java.util.Arrays;

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
import android.widget.Spinner;
import android.widget.Toast;

public class MyFragmentDialog extends DialogFragment implements OnClickListener {

	// поля ввода данных о модели
	private EditText etName;
	private Spinner spCategory;
	private EditText etLot;
	private EditText etCount;
	private Spinner spUnit;

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
		spCategory = (Spinner) v.findViewById(R.id.spCategory);
		etLot = (EditText) v.findViewById(R.id.etLot);
		etCount = (EditText) v.findViewById(R.id.etCount);
		spUnit = (Spinner) v.findViewById(R.id.spUnit);

		// восстанавливаем значение Act (тип действия: добавить/изменить) и ID
		// устройства
		if (savedInstanceState != null)
			mAction = savedInstanceState.getInt("act");

		if (mAction == ACTION_UPDATE) {

			long id = sData.getInt(sData
					.getColumnIndex(MyDBManager.PRODUCTS_ID));
			String name = sData.getString(sData
					.getColumnIndex(MyDBManager.PRODUCTS_NAME));
			String category = sData.getString(sData
					.getColumnIndex(MyDBManager.PRODUCTS_CATEGORY));
			int lot = sData.getInt(sData
					.getColumnIndex(MyDBManager.PRODUCTS_LOT));
			float count = sData.getFloat(sData
					.getColumnIndex(MyDBManager.PRODUCTS_COUNT));
			String unit = sData.getString(sData
					.getColumnIndex(MyDBManager.PRODUCTS_UNIT));

			int indexCategory = 0;
			String[] cat = getResources().getStringArray(
					R.array.modelCategories);

			for (int i = 0; i < cat.length; i++) {
				if (cat[i].toString() == category)
					indexCategory = i;
			}

			// находим ID выбранной категории в массиве категорий
			int ID_cat = 0;
			String[] categoriesArray = getResources().getStringArray(
					R.array.modelCategories);
			for (int i = 0; i < categoriesArray.length; i++) {
				if (categoriesArray[i].equals(category)) {
					ID_cat = i;
					break;
				}
			}

			// находим ID выбранной меры в массиве единиц измерения
			int ID_unit = 0;
			String[] unitsArray = getResources().getStringArray(
					R.array.modelUnits);
			for (int i = 0; i < unitsArray.length; i++) {
				if (unitsArray[i].equals(unit)) {
					ID_unit = i;
					break;
				}

			}

			etName.setText(name);
			spCategory.setSelection(ID_cat);
			etLot.setText(String.valueOf(lot));
			etCount.setText(String.valueOf(count));
			spUnit.setSelection(ID_unit);

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
			// Преобразование значений пустых полей в значения по-умолчанию
			if (etLot.getText().length() == 0)
				etLot.setText("1");
			if (etCount.getText().length() == 0)
				etCount.setText("1");

			// обработка клика на кнопке Сохранить (метод onGetRow в fr_backend)
			if (etName.getText().length() != 0 && etLot.getText().length() != 0
					&& etCount.getText().length() != 0) {

				ContentValues cv = new ContentValues();
				if (mAction == ACTION_UPDATE)
					cv.put(MyDBManager.PRODUCTS_ID, sData.getInt(sData
							.getColumnIndex(MyDBManager.PRODUCTS_ID)));
				cv.put(MyDBManager.PRODUCTS_NAME, etName.getText().toString());
				cv.put(MyDBManager.PRODUCTS_CATEGORY, spCategory
						.getSelectedItem().toString());
				cv.put(MyDBManager.PRODUCTS_LOT,
						Integer.parseInt(etLot.getText().toString()));
				cv.put(MyDBManager.PRODUCTS_COUNT,
						Float.parseFloat(etCount.getText().toString()));
				cv.put(MyDBManager.PRODUCTS_UNIT, spUnit.getSelectedItem().toString());

				etName.setText(null);
				spCategory.setSelection(0);
				etLot.setText(null);
				etCount.setText(null);
				spUnit.setSelection(0);
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
