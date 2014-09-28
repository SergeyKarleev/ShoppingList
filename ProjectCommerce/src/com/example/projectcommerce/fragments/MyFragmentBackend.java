package com.example.projectcommerce.fragments;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStreamWriter;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewDebug.ExportedProperty;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.example.projectcommerce.R;
import com.example.projectcommerce.classes.MyAbstractMigrator;
import com.example.projectcommerce.classes.MyDBManager;
import com.example.projectcommerce.classes.MyMigratorXML;

public class MyFragmentBackend extends ListFragment implements OnClickListener {

	final String LOG_TAG = "myLogs";

	// �������� ����� �������� ��������� �������
	final int ACTION_ADD = 1;
	final int ACTION_UPDATE = 2;	

	private Button btnAdd;
	private DialogFragment mDialogFragmentCreate;
	private DialogFragment mDialogFragmentEdit;

	private MyFragmentBackend mContext;
	
	MyDBManager mDataBase;
	private Cursor mData;
	SimpleCursorAdapter scAdapter;
	
	//������ �������/�������� ��������� ����� ���������
	MyMigratorXML mDataMigrator;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View v = inflater.inflate(R.layout.fr_backend, null);
		mDialogFragmentCreate = new MyFragmentDialog(this);
		setHasOptionsMenu(true);

		mContext = this;

		btnAdd = (Button) v.findViewById(R.id.btnAdd);
		btnAdd.setOnClickListener(this);		

		//���������� � ������ � ����� ������
		mDataBase = new MyDBManager(getActivity()); // ���������� ��
		mData = mDataBase.getData(); // ��������� ������ �������� �� ��

		// ��������� ������� �������������
		String[] from = { mDataBase.PRODUCTS_NAME,
				mDataBase.PRODUCTS_COUNT.toString() };
		int[] to = { R.id.tvModel, R.id.tvCount };

		// ������� �������
		// TODO: ����������� ������ ����� CursorLoader
		scAdapter = new SimpleCursorAdapter(getActivity(), R.layout.item,
				mData, from, to);
		setListAdapter(scAdapter);

		// -------------------- ���� ������ � ������/���������-----------//

		mDataMigrator = new MyMigratorXML(mDataBase, this);
		return v;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		// ���������� ����������� ������� �� ������� ������ / �������� ������ ��
		// ���� � ������
		getListView().setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {

				mDataBase.delRecord(id);
				updateListAdapter();
				return false;
			}
		});

		// ���������� ��������� ������� �� ������� / �������������� ��������
		getListView().setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				mData = mDataBase.getData(id);
				if (mData.moveToFirst()) {
					int columnIndex = mData
							.getColumnIndex(MyDBManager.PRODUCTS_ID);
					mDialogFragmentEdit = new MyFragmentDialog(mContext, mData);
					mDialogFragmentEdit.show(getFragmentManager(), null);
				}
			}
		});
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.mymenu, menu);
	}	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {		
		switch (item.getItemId()) {		
		case R.id.importXMLPut:			
			if (mDataMigrator.ImportData(mDataMigrator.MODE_ACTION_ADD))
				updateListAdapter();
			else 
				Toast.makeText(getActivity(), "������ �������", Toast.LENGTH_SHORT).show();
			break;
			
		case R.id.importXMLReplace:			
			if (mDataMigrator.ImportData(mDataMigrator.MODE_ACTION_UPDATE))
				updateListAdapter();
			else
				Toast.makeText(getActivity(), "������ �������", Toast.LENGTH_SHORT).show();
			break;
			
		case R.id.exportXML:
			//TODO: ������� ����� �������� XML
			if (mDataMigrator.ExportData())
				updateListAdapter();
			else
				Toast.makeText(getActivity(), "������ ��������", Toast.LENGTH_SHORT).show();
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onDestroy() {
		mDataBase.close();
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnAdd:
			mDialogFragmentCreate.show(getFragmentManager(), "����������");
			break;		
		default:
			break;
		}

	}

	/**
	 * ������ ������ �� ����������� ContentValues � �� � ����������� listView
	 * 
	 * @param cv
	 *            ������ �������� �������� ������� ������ ��� ��
	 * @param act
	 *            ������������ ��������: ���������� ��� ���������� ��������
	 */
	public void onGetRow(ContentValues cv, int act) {		
		MyThread myThread =  new MyThread(cv, act);
		myThread.execute();		
	}

	/**
	 * ����� ��������� ������� ListView
	 */
	public void updateListAdapter() {
		scAdapter.changeCursor(mDataBase.getData());
	}

	
	private class MyThread extends AsyncTask<Void, Void, Void>{
		
		private ContentValues cv;
		private int act;
		
		/** ����������� �������� ������ ��� �������� ��������� � ����
		 * @param cv �������� �����������/���������� ������
		 * @param act ��������: ���������� ��� ���������
		 */
		public MyThread(ContentValues cv, int act) {
			super();
			this.cv = cv;
			this.act = act;
		}
		

		@Override
		protected Void doInBackground(Void... params) {
			switch (act) {
			case ACTION_ADD:
				mDataBase.addRecord(cv);
				break;
			case ACTION_UPDATE:
				mDataBase.editRecord(cv);
				break;

			default:
				break;
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			updateListAdapter();
		}

		

		

		
		
	}

	

}
