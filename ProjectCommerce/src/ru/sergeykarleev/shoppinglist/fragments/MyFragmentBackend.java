package ru.sergeykarleev.shoppinglist.fragments;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

import ru.sergeykarleev.shoppinglist.R;
import ru.sergeykarleev.shoppinglist.classes.MyDBManager;
import ru.sergeykarleev.shoppinglist.dialogues.MyFragmentDialogProducts;
import android.content.Context;
import android.database.CharArrayBuffer;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.CheckedTextView;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ListView;
import android.widget.SimpleCursorTreeAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class MyFragmentBackend extends Fragment implements OnClickListener,
		OnItemClickListener, OnItemLongClickListener {

	private final static String LOG_TAG = "myLogs";

	private final static int CHECK_ON = 1;
	private final static int CHECK_OFF = 0;

	Button btnAdd;

	ExpandableListView elProducts;
	Cursor cursor;

	ArrayList<String> productList;

	MyDBManager mDB;
	MyTreeAdapter treeAdapter;

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_backend, null);

		btnAdd = (Button) v.findViewById(R.id.btnAdd);
		btnAdd.setOnClickListener(this);

		((Button) v.findViewById(R.id.btnSort))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						Toast.makeText(getActivity(), "Количество отмеченных пунктов равно " + productList.size(), Toast.LENGTH_SHORT).show();
					}
				});

		// Подключаемся к БД
		mDB = new MyDBManager(getActivity());

		// Курсор с группами товаров
		cursor = mDB.getCategories();

		// Объявляем массив названий продуктов
		productList = new ArrayList<String>();

		// Формируем столбцы сопоставления для групп
		String[] groupFrom = new String[] { MyDBManager.CATEGORY_NAME };
		int[] groupTo = new int[] { android.R.id.text1 };

		// Формируем столбцы сопоставления для продуктов
		String[] childFrom = new String[] { MyDBManager.PRODUCTS_NAME };
		int[] childTo = new int[] { R.id.tvItemBackend };

		treeAdapter = new MyTreeAdapter(getActivity(), cursor,
				android.R.layout.simple_expandable_list_item_1, groupFrom,
				groupTo, R.layout.item_backend, childFrom, childTo);
		elProducts = (ExpandableListView) v.findViewById(R.id.elProducts);
		elProducts.setOnItemClickListener(this);
		elProducts.setOnItemLongClickListener(this);
		elProducts.setAdapter(treeAdapter);		
		return v;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnAdd:
			MyFragmentDialogProducts dialog = new MyFragmentDialogProducts(this);
			dialog.show(getActivity().getSupportFragmentManager(), null);
			break;
		default:
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Log.d(LOG_TAG, "Клик на элементе");
		if (ExpandableListView.getPackedPositionType(id) == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
			String txt = ((TextView) view.findViewById(R.id.tvItemBackend))
					.getText().toString();
			productList.add(txt);
			Toast.makeText(getActivity(), txt, Toast.LENGTH_SHORT).show();
		}	
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
		if (ExpandableListView.getPackedPositionType(id) == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
			int childID = ExpandableListView.getPackedPositionChild(id);

			MyFragmentDialogProducts dialog = new MyFragmentDialogProducts(
					this, childID);
			dialog.show(getActivity().getSupportFragmentManager(), null);
			return true;

		}
		return false;
	}

	private class MyTreeAdapter extends SimpleCursorTreeAdapter {

		public MyTreeAdapter(Context context, Cursor cursor, int groupLayout,
				String[] groupFrom, int[] groupTo, int childLayout,
				String[] childFrom, int[] childTo) {
			super(context, cursor, groupLayout, groupFrom, groupTo,
					childLayout, childFrom, childTo);
		}

		@Override
		protected Cursor getChildrenCursor(Cursor groupCursor) {
			long idColumn = groupCursor.getColumnIndex(MyDBManager.CATEGORY_ID);
			return mDB.getProducsCategories(groupCursor.getInt((int) idColumn));
		}	
		
	}

	public void updateAdapter() {
		treeAdapter.notifyDataSetChanged();
	}

}
