package ru.sergeykarleev.shoppinglist.fragments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.sergeykarleev.shoppinglist.R;
import ru.sergeykarleev.shoppinglist.activities.MainActivity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class MyFragmentStorefront extends Fragment {

	private final static String LOG_TAG = "myLogs";
	
	
	Button btnTemplate;
	Button btnTransfer;
	Button btnPlan;
	Button btnProducts;

	ListView lvMyProductList;
	//SimpleAdapter sAdapter;
	MainActivity mActivity;
	MyListAdapter sAdapter;
	
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
		sAdapter = new MyListAdapter(getActivity(), listProducts, R.layout.item_storefront, from, to);	
		lvMyProductList = (ListView) v.findViewById(R.id.lvMyProductList);
		lvMyProductList.setAdapter(sAdapter);
		
		return v;
	}
	
	private class MyListAdapter extends SimpleAdapter implements OnLongClickListener  {

		public MyListAdapter(Context context,
				List<? extends Map<String, ?>> data, int resource,
				String[] from, int[] to) {
			super(context, data, resource, from, to);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = super.getView(position, convertView, parent);
			
			final TextView tvName = (TextView) view.findViewById(R.id.tvSItemName);
			final EditText etComment = (EditText) view.findViewById(R.id.tvSItemComment);
			
			listProducts = mActivity.getListProducts();
			etComment.addTextChangedListener(new TextWatcher() {				
				
				@Override
				public void afterTextChanged(Editable s) {
					for (HashMap<String, String> i : listProducts) {
						if (i.get(MyFragmentBackend.ATTRIBUT_NAME_PRODUCT)==tvName.getText().toString()){
							i.put(MyFragmentBackend.ATTRIBUT_COMMENT_PRODUCT, s.toString());
							mActivity.setListProducts(listProducts);
						}							
					}
					
				}

				@Override
				public void beforeTextChanged(CharSequence s, int start,
						int count, int after) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void onTextChanged(CharSequence s, int start,
						int before, int count) {
					// TODO Auto-generated method stub
					
				}
			});
			
			tvName.setOnLongClickListener(this);
			return view;
		}

		@Override
		public boolean onLongClick(View v) {
			TextView tvName = (TextView) v.findViewById(R.id.tvSItemName);
			String name = tvName.getText().toString();
			
			for (HashMap<String, String> i : listProducts) {
				if (i.get(MyFragmentBackend.ATTRIBUT_NAME_PRODUCT)==name){
					listProducts.remove(i);
					//mActivity.setListProducts(listProducts);
					sAdapter.notifyDataSetChanged();
					return true;
				}							
			}
			return false;
		}
		
		
		

	}

}
