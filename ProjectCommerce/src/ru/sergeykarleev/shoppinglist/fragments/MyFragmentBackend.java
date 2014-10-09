package ru.sergeykarleev.shoppinglist.fragments;


import ru.sergeykarleev.shoppinglist.R;
import ru.sergeykarleev.shoppinglist.classes.MyDBManager;
import ru.sergeykarleev.shoppinglist.dialogues.MyFragmentDialogProducts;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ExpandableListView;
import android.widget.SimpleCursorTreeAdapter;
import android.widget.Toast;

public class MyFragmentBackend extends Fragment implements OnClickListener, OnItemLongClickListener {

	
	Button btnAdd;
	
	ExpandableListView elProducts;
	Cursor cursor;

	MyDBManager mDB;
	protected MyTreeAdapter treeAdapter;

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_backend, null);
		
		btnAdd = (Button) v.findViewById(R.id.btnAdd);		
		btnAdd.setOnClickListener(this);		
				
		// ������������ � ��
		mDB = new MyDBManager(getActivity());

		// ������ � �������� �������
		cursor = mDB.getCategories();

		// ��������� ������� ������������� ��� �����
		String[] groupFrom = new String[] { MyDBManager.CATEGORY_NAME };
		int[] groupTo = new int[] { android.R.id.text1 };

		// ��������� ������� ������������� ��� ���������
		String[] childFrom = new String[] { MyDBManager.PRODUCTS_NAME };
		int[] childTo = new int[] { android.R.id.text1 };

		treeAdapter = new MyTreeAdapter(getActivity(), cursor,
				android.R.layout.simple_expandable_list_item_1, groupFrom,
				groupTo, android.R.layout.simple_list_item_multiple_choice,
				childFrom, childTo);		
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
			MyFragmentDialogProducts dialog = new MyFragmentDialogProducts(MyFragmentDialogProducts.ACTION_ADD);
			dialog.show(getActivity().getSupportFragmentManager(), null);			
			break;
		default:
			break;
		}
	}	
	
	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {		
		Toast.makeText(getActivity(), "View "+view , Toast.LENGTH_SHORT).show();
		return false;
	}
	
	private class MyTreeAdapter extends SimpleCursorTreeAdapter {

		public MyTreeAdapter(Context context, Cursor cursor, int groupLayout,
				String[] groupFrom, int[] groupTo, int childLayout,
				String[] childFrom, int[] childTo) {
			super(context, cursor, groupLayout, groupFrom, groupTo,
					childLayout, childFrom, childTo);
			// TODO Auto-generated constructor stub
		}

		@Override
		protected Cursor getChildrenCursor(Cursor groupCursor) {
			int idColumn = groupCursor.getColumnIndex(MyDBManager.CATEGORY_ID);
			return mDB.getProducsCategories(groupCursor.getInt(idColumn));
		}
	}

	

	
}