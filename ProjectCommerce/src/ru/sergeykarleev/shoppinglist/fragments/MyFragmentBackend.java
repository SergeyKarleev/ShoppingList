package ru.sergeykarleev.shoppinglist.fragments;

import java.lang.reflect.Array;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ExpandableListView;
import android.widget.SimpleCursorTreeAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class MyFragmentBackend extends Fragment implements OnClickListener,
		OnItemLongClickListener {

	private final static String LOG_TAG = "myLogs";

	Button btnAdd;

	ExpandableListView elProducts;
	Cursor cursor;

	MyDBManager mDB;
	MyTreeAdapter treeAdapter;

	// HashMap<String, Boolean> mItems;

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_backend, null);

		btnAdd = (Button) v.findViewById(R.id.btnAdd);
		btnAdd.setOnClickListener(this);

		// Подключаемся к БД
		mDB = new MyDBManager(getActivity());

		// Курсор с группами товаров
		cursor = mDB.getCategories();

		// Объявляем массив элементов: имя продукта, отмечен/неотмечен
		// mItems = new HashMap<String, Boolean>();

		// Заполняем массив элементов: название продукта, отмечен/не отмечен
		// updateArray(mDB.getData());

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
		elProducts.setOnItemLongClickListener(this);
		elProducts.setChoiceMode(ExpandableListView.CHOICE_MODE_MULTIPLE);
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
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
		if (ExpandableListView.getPackedPositionType(id) == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
			int childID = ExpandableListView.getPackedPositionChild(id);

			MyFragmentDialogProducts dialog = new MyFragmentDialogProducts(
					this, childID);
			dialog.show(getActivity().getSupportFragmentManager(), null);

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

		@Override
		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {

			final View view = super.getChildView(groupPosition, childPosition,
					isLastChild, convertView, parent);
			
			((CheckBox) view.findViewById(R.id.chkItemBackend)).setOnCheckedChangeListener(new OnCheckedChangeListener() {

						@Override
						public void onCheckedChanged(CompoundButton buttonView,
								boolean isChecked) {
							if (isChecked) {
								view.setTag(1);	
								Toast.makeText(getActivity(), ""+view.getTag(), Toast.LENGTH_SHORT).show();
							} else {
								view.setTag(0);								
							}
						}
					});

			return view;
		}

	}

	public void updateAdapter() {
		treeAdapter.notifyDataSetChanged();
	}

	// private void updateArray(Cursor cursor) {
	// cursor.moveToFirst();
	// mItems.clear();
	// do {
	// Log.d(LOG_TAG, cursor.getString(cursor
	// .getColumnIndex(MyDBManager.PRODUCTS_ID)));
	// //
	// mItems.put(cursor.getString(cursor.getColumnIndex(MyDBManager.PRODUCTS_NAME)),false);
	// } while (cursor.moveToNext());
	// }

}
