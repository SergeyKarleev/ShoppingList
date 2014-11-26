package ru.sergeykarleev.shoppinglist.dialogues;

import ru.sergeykarleev.shoppinglist.R;
import ru.sergeykarleev.shoppinglist.classes.MyDBManager;
import ru.sergeykarleev.shoppinglist.fragments.MyFragmentStorefront;
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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class MyFragmentDialogProducts extends DialogFragment implements
		OnClickListener, OnCheckedChangeListener {

	// �����������
	private static final String LOG_TAG = "myLogs";

	// �������� � ���������: ���������� ������ ��� �����������/�������� ��������
	private final static int ACTION_ADD = 1;
	private final static int ACTION_UPDATE = 2;
	private final static int ACTION_DELETE = 3;

	// ������ ������ � ����������/��������
	private Button btnCancel;
	private Button btnSave;

	// ���� ����� ������ � ������
	private EditText etName;
	private Spinner spCategory;
	private CheckBox chkDeleteProduct;

	// �������� �������: ���������� ��� ��������� ��������
	private int mAction = 0;
	private long mIDCurrentProduct;

	// �������� �������� ���������� ���������� �� ���������
	private static Cursor sData;
	private MyDBManager mDB;

	// ������ �� Backend ��������, �� �������� ��� ������ ������
	private static MyFragmentStorefront mFragment;

	/**
	 * ����������� ���������� ��� �������� ������ ��������
	 * 
	 * @param mAction
	 */
	public MyFragmentDialogProducts(MyFragmentStorefront mStorefront) {
		super();
		this.mAction = ACTION_ADD;
		this.mFragment = mStorefront;
	}

	/**
	 * ����������� ���������� ��� ��������� ��������
	 * 
	 * @param mAction
	 */
	public MyFragmentDialogProducts(MyFragmentStorefront myFragmentStorefront,
			long id) {
		super();
		this.mAction = ACTION_UPDATE;
		this.mFragment = myFragmentStorefront;
		this.mIDCurrentProduct = id;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		getDialog().setTitle(R.string.dialogTitle);
		View v = inflater.inflate(R.layout.dialog_product, null);
		v.findViewById(R.id.btnCancel).setOnClickListener(this);

		mDB = new MyDBManager(getActivity());

		etName = (EditText) v.findViewById(R.id.etName);
		spCategory = (Spinner) v.findViewById(R.id.spCategory);
		chkDeleteProduct = (CheckBox) v.findViewById(R.id.chkDeleteProduct);
		spCategory.setAdapter(getAdapter());

		btnSave = (Button) v.findViewById(R.id.btnSave);
		btnSave.setOnClickListener(this);

		// ��������������� �������� Act (��� ��������: ��������/��������) � ID
		// ����������
		if (savedInstanceState != null)
			mAction = savedInstanceState.getInt("act");

		if (mAction == ACTION_UPDATE) {

			// �������� ������ �� ������ ����������� ��������
			sData = mDB.getData(mIDCurrentProduct);
			sData.moveToFirst();

			chkDeleteProduct.setEnabled(true);
			chkDeleteProduct.setOnCheckedChangeListener(this);

			long id = sData.getLong(sData
					.getColumnIndex(MyDBManager.PRODUCTS_ID));
			String name = sData.getString(sData
					.getColumnIndex(MyDBManager.PRODUCTS_NAME));
			long category = sData.getLong(sData
					.getColumnIndex(MyDBManager.PRODUCTS_CATEGORY));
			etName.setText(name);
			spCategory.setSelection((int) category - 1);
		}
		return v;
	}

	private SimpleCursorAdapter getAdapter() {
		String[] from = new String[] { MyDBManager.CATEGORY_NAME };
		int[] to = new int[] { android.R.id.text1 };
		Cursor cursor = mDB.getCategories(MyDBManager.MODE_CATEGORY_FULL);
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

			// ���� ���������� ����� � ��������
			if (chkDeleteProduct.isChecked())
				RecordData(null);

			// ��������� ����� �� ������ ��������� (����� onGetRow � fr_backend)
			if (etName.getText().length() != 0) {

				// TODO: �������� �������� �� ������� ��������� ����� � ��
				String testName = etName.getText().toString();

				ContentValues cv = new ContentValues();
				if (mAction == ACTION_UPDATE)
					cv.put(MyDBManager.PRODUCTS_ID, sData.getLong(sData
							.getColumnIndex(MyDBManager.PRODUCTS_ID)));
				cv.put(MyDBManager.PRODUCTS_NAME, etName.getText().toString());
				cv.put(MyDBManager.PRODUCTS_CATEGORY,
						spCategory.getSelectedItemPosition() + 1);
				etName.setText(null);
				spCategory.setSelection(0);
				RecordData(cv);
				
			} else
				Toast.makeText(getActivity(), "�������� �� ������ ���� ������",
						Toast.LENGTH_SHORT).show();
		default:
			break;
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		switch (buttonView.getId()) {
		case R.id.chkDeleteProduct:
			if (isChecked) {
				btnSave.setText(R.string.btnDelete);
				etName.setEnabled(false);
				spCategory.setEnabled(false);
			} else {
				btnSave.setText(R.string.btnSave);
				etName.setEnabled(true);
				spCategory.setEnabled(true);
			}
			break;

		default:
			break;
		}
	}

	private void RecordData(ContentValues cv) {
		MyDBManager mDB = new MyDBManager(getActivity());
		if (cv == null)
			mAction = ACTION_DELETE;

		switch (mAction) {
		case ACTION_ADD:
			try {
				mDB.addRecord(cv);
			} catch (Exception e) {
				Log.d(LOG_TAG,
						"������ �� ��� ������� � ���� ������.\n" + e.toString());
				e.printStackTrace();
			}
			break;
		case ACTION_UPDATE:
			try {
				mDB.editRecord(cv);
			} catch (Exception e) {
				Log.d(LOG_TAG, "����������� �� ���� ������� � ���� ������.\n"
						+ e.toString());
				e.printStackTrace();
			}
			break;
		case ACTION_DELETE:
			try {
				mDB.delRecord(mIDCurrentProduct);
			} catch (Exception e) {
				Log.d(LOG_TAG,
						"�������� ����������� �������� \n" + e.toString());
				e.printStackTrace();
			}

		default:
			break;
		}
		mFragment.updateAdapter();
		dismiss();
	}

}
