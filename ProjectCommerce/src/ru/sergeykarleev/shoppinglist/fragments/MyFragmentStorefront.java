package ru.sergeykarleev.shoppinglist.fragments;

import ru.sergeykarleev.shoppinglist.R;
import ru.sergeykarleev.shoppinglist.classes.MyDBManager;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import android.widget.ExpandableListView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorTreeAdapter;
import android.widget.Toast;

public class MyFragmentStorefront extends Fragment {

	private final static String LOG_TAG = "myLogs";

	Button btnTemplate;
	Button btnTransfer;
	Button btnPlan;
	Button btnProducts;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_storefront, null);
		btnTemplate = (Button) v.findViewById(R.id.btnTemplate);
		btnTransfer = (Button) v.findViewById(R.id.btnTransfer);
		btnPlan = (Button) v.findViewById(R.id.btnPlan);
		btnProducts = (Button) v.findViewById(R.id.btnProductBase);
			
		return v;
	}	

	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
	}
}
