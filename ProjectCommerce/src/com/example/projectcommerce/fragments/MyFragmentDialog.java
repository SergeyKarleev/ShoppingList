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

	// ���� ����� ������ � ������
	private EditText etName;
	private EditText etCategory;
	private EditText etLot;
	private EditText etCount;
	private EditText etUnit;
	

	// �������� �������: ���������� ��� ��������� ��������
	private int mAction = 0;
	private final int ACTION_ADD = 1;
	private final int ACTION_UPDATE = 2;

	// ��������� ��� ������ ������ getRow ���������
	private static MyFragmentBackend sContext;

	// �������� �������� ���������� ���������� �� ���������
	private static Cursor sData;

	/**
	 * ����������� ���������� ��� �������� ������
	 */
	public MyFragmentDialog() {
		super();
	}

	/**
	 * ������ ����������� ���������� ��� �������� ������ ��������
	 * 
	 * @param context
	 *            ��-��������� ���������� �������� ��������� Backend (this)
	 */
	public MyFragmentDialog(MyFragmentBackend context) {
		super();
		this.sContext = context;
		mAction = ACTION_ADD;
	}

	/**
	 * ������ ����������� ���������� ��� �������������� ������������� ��������
	 * 
	 * @param context
	 *            ��-��������� ���������� �������� ��������� (this)
	 * @param data
	 *            ������ � ������� �� ��������� ������
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

		// ��������������� �������� Act (��� ��������: ��������/��������) � ID
		// ����������
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
			// ��������� ����� �� ������ ��������� (����� onGetRow � fr_backend)
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
				Toast.makeText(getActivity(), "��� ���� ������ ���� ���������",
						Toast.LENGTH_SHORT).show();
		default:
			break;
		}
	}

}
