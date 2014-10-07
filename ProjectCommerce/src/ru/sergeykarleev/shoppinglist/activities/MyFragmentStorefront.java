package ru.sergeykarleev.shoppinglist.activities;

import ru.sergeykarleev.shoppinglist.R;
import ru.sergeykarleev.shoppinglist.classes.MyDBManager;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v4.content.CursorLoader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

public class MyFragmentStorefront extends ListFragment implements
		OnClickListener {

	private final static String LOG_TAG = "myLogs";

	Button btnTemplate;
	Button btnTransfer;
	Button btnPlan;
	Button btnProducts;

	MyDBManager mDB;
	SimpleCursorAdapter scAdapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_storefront, null);

		// Подключаемся к БД
		mDB = new MyDBManager(getActivity());

		// Формируем столбцы сопоставления
		String[] from = new String[] { MyDBManager.PRODUCTS_NAME,
				MyDBManager.PRODUCTS_CATEGORY };
		int[] to = new int[] { android.R.id.text1, android.R.id.text2 };

		// создаем адаптер и настраиваем списки
		scAdapter = new SimpleCursorAdapter(getActivity(),
				android.R.layout.two_line_list_item, null, from, to);
		setListAdapter(scAdapter);
		scAdapter.changeCursor(mDB.getData());
		
		return v;
	}

	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

	}

}
