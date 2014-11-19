package ru.sergeykarleev.shoppinglist.fragments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.sergeykarleev.shoppinglist.R;
import ru.sergeykarleev.shoppinglist.activities.MainActivity;
import ru.sergeykarleev.shoppinglist.classes.MyDBManager;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
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

	private static final int SAVE_INTO_TEMPLATES = 0;
	private static final int LOAD_FROM_TEMPLATES = 1;

	ListView lvMyProductList;
	MainActivity mActivity;
	MyListAdapter sAdapter;

	String templateName = null;

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

		String[] from = { MyDBManager.ATTRIBUT_NAME_PRODUCT,
				MyDBManager.ATTRIBUT_COMMENT_PRODUCT };

		int[] to = { R.id.tvSItemName, R.id.tvSItemComment };

		sAdapter = new MyListAdapter(getActivity(), listProducts,
				R.layout.item_storefront, from, to);
		lvMyProductList = (ListView) v.findViewById(R.id.lvMyProductList);
		lvMyProductList.setAdapter(sAdapter);
		setHasOptionsMenu(true);
		return v;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		menu.add(1, SAVE_INTO_TEMPLATES, 0, R.string.save_into_templates);
		menu.add(1, LOAD_FROM_TEMPLATES, 1, R.string.load_from_templates);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case SAVE_INTO_TEMPLATES:
			if (!listProducts.isEmpty())
				SaveToTemplate();
			break;
		case LOAD_FROM_TEMPLATES:
			LoadFromTemplates();
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onDestroy() {
		mActivity.setListProducts(listProducts);
		
		super.onDestroy();
	}

	private void LoadFromTemplates() {
		MyDBManager mDB = new MyDBManager(getActivity());
		ArrayList<String> arr = new ArrayList<String>();

		try {
			arr.addAll(mDB.getTemplatesList());
		} catch (Exception e) {
			Toast.makeText(getActivity(), "Нет сохраненных шаблонов",
					Toast.LENGTH_SHORT).show();
			return;
		}

		mDB.close();

		// Log.d(LOG_TAG, "Формируем из массива CharSequence");
		CharSequence[] cs = arr.toArray(new CharSequence[arr.size()]);

		for (CharSequence charSequence : cs) {
			Log.d(LOG_TAG, charSequence.toString());
		}
		// Log.d(LOG_TAG, "CharSequence[] сформирован");
		int selectedTemplate = -1;

		AlertDialog.Builder adb = new Builder(getActivity());
		adb.setTitle(R.string.templates_load_dialog_title);
		adb.setSingleChoiceItems(cs, -1, null);
		adb.setNegativeButton("cancel", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		adb.setPositiveButton("OK", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				
				listProducts.clear();
				ListView lv = ((AlertDialog) dialog).getListView();
				String tName = lv
						.getItemAtPosition(lv.getCheckedItemPosition())
						.toString();
				// TODO: Здесь загружаем готовый список с помощью метода базы
				// данных loadFromTemplates(name)
				MyDBManager mDB = new MyDBManager(getActivity());				
				listProducts = mDB.loadFromTemplates(tName);				
				testListProduct("Проверка после извлечения списка из базы");
				sAdapter.notifyDataSetChanged();
				mDB.close();
				dialog.dismiss();

			}
		});
		adb.create().show();
	}

	private void SaveToTemplate() {
		templateName = null;
		final EditText etName = new EditText(getActivity());

		AlertDialog.Builder adb = new Builder(getActivity());
		adb.setTitle(R.string.templates_create_dialog_title)
				.setMessage(R.string.templates_create_dialog_message)
				.setCancelable(true).setView(etName).setCancelable(false);
		adb.setNegativeButton(R.string.templates_negative_button_dialog,
				new OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});

		adb.setPositiveButton(R.string.templates_positive_button_dialog,
				new OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						if (!etName.getText().toString().isEmpty()) {

							MyDBManager mDB = new MyDBManager(getActivity());
							mDB.saveToTemplate(listProducts, etName.getText()
									.toString());
							mDB.close();						
							
						}
						dialog.dismiss();
					}
				});
		adb.create().show();

	}

	/**
	 * Тестовый метод на состояние локального листа продуктов
	 */
	private void testListProduct(String log){
		Log.d(LOG_TAG,log);
		for (HashMap<String, String> list : listProducts) {
			Log.d(LOG_TAG, list.get(MyDBManager.ATTRIBUT_NAME_PRODUCT) + " - "
					+ list.get(MyDBManager.ATTRIBUT_COMMENT_PRODUCT));

		}
	}
	
	private class MyListAdapter extends SimpleAdapter implements
			OnLongClickListener {

		public MyListAdapter(Context context,
				List<? extends Map<String, ?>> data, int resource,
				String[] from, int[] to) {
			super(context, data, resource, from, to);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = super.getView(position, convertView, parent);

			final TextView tvName = (TextView) view
					.findViewById(R.id.tvSItemName);
			final EditText etComment = (EditText) view
					.findViewById(R.id.tvSItemComment);

			// listProducts = mActivity.getListProducts();
			etComment.addTextChangedListener(new TextWatcher() {

				@Override
				public void afterTextChanged(Editable s) {
					for (HashMap<String, String> i : listProducts) {
						if (i.get(MyDBManager.ATTRIBUT_NAME_PRODUCT) == tvName
								.getText().toString()) {
							i.put(MyDBManager.ATTRIBUT_COMMENT_PRODUCT,
									s.toString());
							// mActivity.setListProducts(listProducts);
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
				if (i.get(MyDBManager.ATTRIBUT_NAME_PRODUCT) == name) {
					listProducts.remove(i);
					// mActivity.setListProducts(listProducts);
					sAdapter.notifyDataSetChanged();
					return true;
				}
			}
			return false;
		}

	}

}
