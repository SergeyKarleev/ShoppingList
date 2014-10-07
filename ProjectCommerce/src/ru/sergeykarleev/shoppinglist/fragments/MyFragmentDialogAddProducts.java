package ru.sergeykarleev.shoppinglist.fragments;

import java.util.ArrayList;
import java.util.Arrays;

import ru.sergeykarleev.shoppinglist.R;
import ru.sergeykarleev.shoppinglist.classes.MyDBManager;

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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class MyFragmentDialogAddProducts extends DialogFragment implements
		OnClickListener {

	//действие с продуктом: добавление нового или модификаци€/удаление текущего
	public final static int ACTION_ADD = 1;
	public final static int ACTION_UPDATE = 2;
	
	// пол€ ввода данных о модели
	private EditText etName;
	private Spinner spCategory;
	private CheckBox chkDeleteProduct;

	// действие диалога: добавление или изменение элемента
	private int mAction = 0;

	// содержит значение выбранного устройства из фрагмента
	private static Cursor sData;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		getDialog().setTitle(R.string.dialogTitle);
		View v = inflater.inflate(R.layout.fragment_add_product, null);
		v.findViewById(R.id.btnCancel).setOnClickListener(this);
		v.findViewById(R.id.btnSave).setOnClickListener(this);

		etName = (EditText) v.findViewById(R.id.etName);
		spCategory = (Spinner) v.findViewById(R.id.spCategory);
		chkDeleteProduct = (CheckBox) v.findViewById(R.id.chkDeleteProduct);

		// восстанавливаем значение Act (тип действи€: добавить/изменить) и ID
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
			// spCategory.setSelection();

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

			// обработка клика на кнопке —охранить (метод onGetRow в fr_backend)
			if (etName.getText().length() != 0) {

				ContentValues cv = new ContentValues();
				if (mAction == ACTION_UPDATE)
					cv.put(MyDBManager.PRODUCTS_ID, sData.getInt(sData
							.getColumnIndex(MyDBManager.PRODUCTS_ID)));
				cv.put(MyDBManager.PRODUCTS_NAME, etName.getText().toString());
				cv.put(MyDBManager.PRODUCTS_CATEGORY, spCategory
						.getSelectedItem().toString());				

				etName.setText(null);
				spCategory.setSelection(0);				
				// sContext.onGetRow(cv, mAction);
				dismiss();
			} else
				Toast.makeText(getActivity(), "¬се пол€ должны быть заполнены",
						Toast.LENGTH_SHORT).show();
		default:
			break;
		}
	}

}
