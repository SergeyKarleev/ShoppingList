package ru.sergeykarleev.shoppinglist.activities;

import java.util.Stack;

import ru.sergeykarleev.shoppinglist.R;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

public class MyFragmentStorefront extends ListFragment implements OnClickListener{

	private final static String LOG_TAG = "myLogs";
		
	Button btnTemplate;
	Button btnTransfer;
	Button btnPlan;
	Button btnProducts;	
	
	String data[] = new String[]{};
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {		
		View v = inflater.inflate(R.layout.fragment_storefront, null);
		
		 ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
			        android.R.layout.simple_list_item_1, data);
			    setListAdapter(adapter);
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
