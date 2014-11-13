package ru.sergeykarleev.shoppinglist.fragments;

import java.util.ArrayList;
import java.util.HashMap;

import ru.sergeykarleev.shoppinglist.R;
import ru.sergeykarleev.shoppinglist.activities.MainActivity;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class MyFragmentStorefront extends Fragment {

	private final static String LOG_TAG = "myLogs";
	
	
	Button btnTemplate;
	Button btnTransfer;
	Button btnPlan;
	Button btnProducts;

	ListView lvMyProductList;
	SimpleAdapter sAdapter;
	MainActivity mActivity;
	
	ArrayList<HashMap<String, String>> listProducts;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_storefront, null);
		btnTemplate = (Button) v.findViewById(R.id.btnTemplate);
		btnTransfer = (Button) v.findViewById(R.id.btnTransfer);
		btnPlan = (Button) v.findViewById(R.id.btnPlan);
		btnProducts = (Button) v.findViewById(R.id.btnProductBase);

		mActivity = (MainActivity) getActivity();
		listProducts = mActivity.getListProducts();
		
		String[] from = { MyFragmentBackend.ATTRIBUT_NAME_PRODUCT,
				MyFragmentBackend.ATTRIBUT_COMMENT_PRODUCT };
		
		int[] to = { R.id.tvSItemName, R.id.tvSItemComment };
		
		
		Toast.makeText(getActivity(), "Список состоит из "+listProducts.size(), Toast.LENGTH_SHORT).show();
		sAdapter = new SimpleAdapter(getActivity(), listProducts, R.layout.item_storefront, from, to);	
		//lvMyProductList = (ListView) v.findViewById(R.id.lvMyProductList);
		
		return v;
	}

	
	private void onUpdateAdapter(){
		mActivity.setListProducts(listProducts);
	}

}
