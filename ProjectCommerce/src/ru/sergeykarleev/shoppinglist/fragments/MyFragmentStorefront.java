package ru.sergeykarleev.shoppinglist.fragments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import ru.sergeykarleev.shoppinglist.R;
import ru.sergeykarleev.shoppinglist.activities.MainActivity;
import ru.sergeykarleev.shoppinglist.classes.MyDBManager;
import ru.sergeykarleev.shoppinglist.classes.MySendManager;
import ru.sergeykarleev.shoppinglist.classes.pro.MyIntentGetter;
import ru.sergeykarleev.shoppinglist.dialogues.MyFragmentDialogProducts;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat.Action.Builder;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleCursorTreeAdapter;
import android.widget.SlidingDrawer;
import android.widget.SlidingDrawer.OnDrawerOpenListener;
import android.widget.TextView;
import android.widget.Toast;

public class MyFragmentStorefront extends Fragment implements
		OnChildClickListener, OnItemLongClickListener, OnDrawerOpenListener,
		android.view.View.OnClickListener {

	private final static String LOG_TAG = "myLogs";

	// Константы функций контекстного меню
	private static final int SAVE_INTO_TEMPLATES = 0;
	private static final int LOAD_FROM_TEMPLATES = 1;
	private static final int SEND_DATA = 2;
	private static final int CLEAR_LIST = 3;
	private static final int HELP_LIST = 4;
	private static final int EXIT_LIST = 5;

	// Временная кнопка обращения к базе данных
	Button btnProducts;

	// Кнопка добавления разновидностей продуктов в базу
	Button btnAdd;

	SlidingDrawer sDrawer;
	MainActivity mActivity;
	MyIntentGetter intentGetter;

	MyDBManager mDB;
	Cursor cursor;

	// Главный список, активити и адаптер для него
	ListView lvMyProductList;
	MyListAdapter sAdapter;
	FrameLayout rlStart;

	// Список базы продуктов и адаптер для него
	ExpandableListView elProducts;
	MyTreeAdapter treeAdapter;

	String templateName = null;

	// Класс для отправки списка на другое устройство
	MySendManager sendManager;

	ArrayList<HashMap<String, String>> listProducts;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_storefront, null);

		// Находим FrameLayout для последующего изменения фона при вызове
		// подсказки
		rlStart = (FrameLayout) v.findViewById(R.id.rlStart);
		// Включаем меню
		setHasOptionsMenu(true);

		// Делаем ссылку на активити
		mActivity = (MainActivity) getActivity();

		// Создание списка продуктов
		listProducts = createListProduct();

		// Объявляем кнопки базы и добавления
		// btnProducts = (Button) v.findViewById(R.id.btnProductBase);
		btnAdd = (Button) v.findViewById(R.id.btnAdd);
		btnAdd.setOnClickListener(this);

		sDrawer = (SlidingDrawer) v.findViewById(R.id.slidingDrawer);
		sDrawer.setOnDrawerOpenListener(this);

		// Запрос списка продуктов из MAIN
		// TODO: переделать логику. Теперь список хранится только здесь
		// listProducts = ((MainActivity)getActivity()).getListProducts();

		// открываем базу данных
		mDB = new MyDBManager(getActivity());

		// Формируем служебные данные для заполнения listView с нашим списком
		// продуктов
		String[] from = { MyDBManager.ATTRIBUT_NAME_PRODUCT,
				MyDBManager.ATTRIBUT_COMMENT_PRODUCT,
				MyDBManager.ATTRIBUT_CATEGORY_PRODUCT };

		int[] to = { R.id.tvSItemName, R.id.tvSItemComment,
				R.id.tvSItemCategory };

		sAdapter = new MyListAdapter(getActivity(), listProducts,
				R.layout.item_storefront, from, to);
		lvMyProductList = (ListView) v.findViewById(R.id.lvMyProductList);
		lvMyProductList.setAdapter(sAdapter);
		lvMyProductList.setItemsCanFocus(true);

		elProducts = (ExpandableListView) v.findViewById(R.id.elProducts);
		elProducts.setOnChildClickListener(this);
		elProducts.setOnItemLongClickListener(this);
		return v;
	}

	private ArrayList<HashMap<String, String>> createListProduct() {		
		// Запускаем процедуру загрузки списка
		if (mActivity.getIntent().getAction() == Intent.ACTION_VIEW) {
			intentGetter = new MyIntentGetter(mActivity);
			return intentGetter.getListProducts();
		}
		return new ArrayList<HashMap<String, String>>();
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		menu.add(1, SAVE_INTO_TEMPLATES, 0, R.string.save_into_templates);
		menu.add(1, LOAD_FROM_TEMPLATES, 1, R.string.load_from_templates);
		menu.add(1, SEND_DATA, 2, R.string.send_data);
		menu.add(1, CLEAR_LIST, 3, R.string.clear_list).setShowAsAction(
				MenuItem.SHOW_AS_ACTION_IF_ROOM);
		menu.add(1, HELP_LIST, 4, R.string.help_list);
		menu.add(1, EXIT_LIST, 5, R.string.exit_list);

	}

	@Override
	public void onDestroy() {
		try {
			mDB.close();
		} catch (Exception e) {
			Log.d(LOG_TAG, "База данных уже закрыта");
		}
		super.onDestroy();
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
		case SEND_DATA:
			if (!listProducts.isEmpty())
				sendManager = new MySendManager(mActivity, listProducts);
			break;
		case CLEAR_LIST:
			if (!listProducts.isEmpty()) {
				listProducts.clear();
				sAdapter.notifyDataSetChanged();
			}
			break;
		case HELP_LIST:
			mActivity.openHelpDialog();
			break;
		case EXIT_LIST:
			mActivity.openQuitDialog();
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public boolean onChildClick(ExpandableListView parent, View v,
			int groupPosition, int childPosition, long id) {
		String txtName = ((TextView) v.findViewById(R.id.tvItemChild))
				.getText().toString();

		String gName = treeAdapter.getGroup(groupPosition).getString(
				treeAdapter.getCursor().getColumnIndex(
						MyDBManager.CATEGORY_NAME));
	
		HashMap<String, String> hm = new HashMap<String, String>();
		hm.put(MyDBManager.ATTRIBUT_NAME_PRODUCT, txtName);
		hm.put(MyDBManager.ATTRIBUT_CATEGORY_PRODUCT, gName);
		hm.put(MyDBManager.ATTRIBUT_COMMENT_PRODUCT, "");
		listProducts.add(hm);
		sAdapter.notifyDataSetChanged();

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

	/**
	 * Загрузка списка из шаблона
	 */
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

		int selectedTemplate = -1;

		AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());
		adb.setTitle(R.string.templates_load_dialog_title);
		adb.setSingleChoiceItems(cs, -1, null);
		adb.setNegativeButton(R.string.templates_negative_button_dialog,
				new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		adb.setPositiveButton(R.string.templates_positive_button_dialog,
				new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {

						listProducts.clear();
						ListView lv = ((AlertDialog) dialog).getListView();
						int position;
						if (lv.getCheckedItemPosition() == -1) {
							position = 0;
						} else {
							position = lv.getCheckedItemPosition();
						}
						String tName = lv.getItemAtPosition(position)
								.toString();
						// TODO: Здесь загружаем готовый список с помощью метода
						// базы
						// данных loadFromTemplates(name)
						MyDBManager mDB = new MyDBManager(getActivity());
						listProducts.addAll(mDB.loadFromTemplates(tName));

						sAdapter.notifyDataSetChanged();
						mDB.close();
						dialog.dismiss();

					}
				});

		adb.setNeutralButton(R.string.templates_neutral_button_dialog,
				new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						ListView lv = ((AlertDialog) dialog).getListView();

						AlertDialog.Builder adbDelete = new AlertDialog.Builder(
								getActivity());
						adbDelete.setTitle("Удаление шаблонов");
						adbDelete
								.setNegativeButton(
										R.string.templates_negative_button_dialog,
										null);

						if (lv.getCheckedItemPosition() == -1) {
							adbDelete.setMessage("Удалить все шаблоны");
							adbDelete.setPositiveButton(
									R.string.templates_positive_button_dialog,
									new OnClickListener() {

										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											MyDBManager mDB = new MyDBManager(
													getActivity());
											mDB.clearTemplate(null);
											mDB.close();
											dialog.dismiss();
										}
									});

						} else {
							final String tName = lv.getItemAtPosition(
									lv.getCheckedItemPosition()).toString();
							adbDelete.setMessage("Удалить шаблон " + tName
									+ "?");
							adbDelete.setPositiveButton(
									R.string.templates_positive_button_dialog,
									new OnClickListener() {

										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											MyDBManager mDB = new MyDBManager(
													getActivity());
											mDB.clearTemplate(tName);
											mDB.close();
											dialog.dismiss();
										}
									});
						}
						adbDelete.create().show();
					}
				});
		adb.create().show();
	}

	/**
	 * Сохранение списка в шаблон
	 */
	private void SaveToTemplate() {
		templateName = null;
		final EditText etName = new EditText(getActivity());

		AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());
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
			EditText etComment = (EditText) view
					.findViewById(R.id.tvSItemComment);

			etComment.addTextChangedListener(new TextWatcher() {

				@Override
				public void afterTextChanged(Editable s) {
					for (HashMap<String, String> i : listProducts) {
						if (i.get(MyDBManager.ATTRIBUT_NAME_PRODUCT) == tvName
								.getText().toString()) {
							i.put(MyDBManager.ATTRIBUT_COMMENT_PRODUCT,
									s.toString());
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

	public void updateAdapter() {
		cursor = mDB.getCategories(MyDBManager.MODE_CATEGORY_NOTNULL);
		treeAdapter.changeCursor(cursor);
	}

	@Override
	public void onDrawerOpened() {
		// Получаем курсор с группами товаров
		cursor = mDB.getCategories(MyDBManager.MODE_CATEGORY_NOTNULL);

		// Заполнение списка-дерева базы товаров
		// Формируем столбцы сопоставления для групп
		String[] groupFrom = new String[] { MyDBManager.CATEGORY_NAME };
		int[] groupTo = new int[] { android.R.id.text1 };

		// Формируем столбцы сопоставления для продуктов
		String[] childFrom = new String[] { MyDBManager.PRODUCTS_NAME };
		int[] childTo = new int[] { R.id.tvItemChild };

		treeAdapter = new MyTreeAdapter(getActivity(), cursor,
				R.layout.item_group, groupFrom, groupTo, R.layout.item_child,
				childFrom, childTo);

		elProducts.setAdapter(treeAdapter);
		updateAdapter();
	}

	@Override
	public void onClick(View v) {
		MyFragmentDialogProducts dialog = new MyFragmentDialogProducts(this);
		dialog.show(getActivity().getSupportFragmentManager(), null);

	}
}
