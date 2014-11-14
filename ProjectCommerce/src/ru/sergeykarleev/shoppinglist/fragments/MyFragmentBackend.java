package ru.sergeykarleev.shoppinglist.fragments;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ru.sergeykarleev.shoppinglist.R;
import ru.sergeykarleev.shoppinglist.activities.MainActivity;
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
		OnItemLongClickListener, OnChildClickListener {

	private final static String LOG_TAG = "myLogs";

	private final static int CHECK_ON = 1;
	private final static int CHECK_OFF = 0;
	
	public final static String ATTRIBUT_NAME_PRODUCT = "NameProduct";
	public final static String ATTRIBUT_COMMENT_PRODUCT = "CommentProduct";

	Button btnAdd;	

	ExpandableListView elProducts;
	Cursor cursor;

	ArrayList<HashMap<String, String>> productList;
	//public HashMap<String, String> productList;

	MyDBManager mDB;
	MyTreeAdapter treeAdapter;

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_backend, null);

		((Button) v.findViewById(R.id.btnAdd)).setOnClickListener(this);
		((Button) v.findViewById(R.id.btnSort)).setOnClickListener(this);		
		((Button) v.findViewById(R.id.btnInMarket)).setOnClickListener(this);
		
		// Подключаемся к БД
		mDB = new MyDBManager(getActivity());

		// Курсор с группами товаров
		cursor = mDB.getCategories();

		// Объявляем массив названий продуктов
		productList = new ArrayList<HashMap<String,String>>();

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
		elProducts.setOnChildClickListener(this);
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
		case R.id.btnSort:
			Toast.makeText(getActivity(),"Количество отмеченных пунктов равно " + productList.size(),Toast.LENGTH_SHORT).show();
			break;
		case R.id.btnInMarket:
			((MainActivity) getActivity()).goToProductList(productList);			
			break;
		default:
			break;
		}
	}

	@Override
	public boolean onChildClick(ExpandableListView parent, View v,
			int groupPosition, int childPosition, long id) {
		String txt = ((TextView) v.findViewById(R.id.tvItemBackend)).getText()
				.toString();
		HashMap<String, String> hm = new HashMap<String, String>();
		hm.put(ATTRIBUT_NAME_PRODUCT, txt);
		hm.put(ATTRIBUT_COMMENT_PRODUCT, "");		
		productList.add(hm);
		
		Toast.makeText(getActivity(), "В список добавлен продукт: " + txt,
				Toast.LENGTH_SHORT).show();
		return true;
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

	public void updateAdapter() {
		treeAdapter.notifyDataSetChanged();
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

}
