package ru.sergeykarleev.shoppinglist.classes;

import java.util.ArrayList;
import java.util.HashMap;

import ru.sergeykarleev.shoppinglist.activities.MainActivity;
import ru.sergeykarleev.shoppinglist.fragments.MyFragmentBackend;
import ru.sergeykarleev.shoppinglist.fragments.MyFragmentStorefront;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;
import android.widget.Toast;

/**
 * @author skar011
 * 
 */
public class MyDBManager implements BaseColumns {

	/*------------------------------------����������-----------------------------------------*/

	// ���������� ���� ��� �����
	private final String LOG_TAG = "myLogs";

	// ����������� ���������� ����������� ���� ������
	private static final String DB_NAME = "myDB.db";
	private static final int DB_VERSION = 2;

	// �������� ������ � ���� ������
	private static final String DB_TABLE_PRODUCTS = "ProductsTable";
	private static final String DB_TABLE_CATEGORIES = "CategoryTable";
	private static final String DB_TABLE_TEMPLATES = "TemplateTable";

	// ����� �������� ������� DB_TABLE_PRODUCTS
	public static final String PRODUCTS_ID = BaseColumns._ID;
	public static final String PRODUCTS_NAME = "Name_product";
	public static final String PRODUCTS_CATEGORY = "ID_category";

	// ����� �������� ������� DB_TABLE_CATEGORIES
	public static final String CATEGORY_ID = BaseColumns._ID;
	public static final String CATEGORY_NAME = "Name_category";

	// ����� �������� ������� DB_TABLE_TEMPLATES
	public static final String TEMPLATE_RECORD = BaseColumns._ID;
	public static final String TEMPLATE_NAME = "Name_template";
	public static final String TEMPLATE_PRODUCT = "Product_template";
	public static final String TEMPLATE_COMMENT = "Comment_tamplate";

	// ������� ���������� ����������
	public static final int ORDER_BY_NONE = 0;
	public static final int ORDER_BY_NAME_ASC = 1;
	public static final int ORDER_BY_NAME_DESC = 2;
	public static final int ORDER_BY_CATEGORY_ASC = 3;
	public static final int ORDER_BY_CATEGORY_DESC = 4;

	// ����� ����������
	private static final String SORT_NONE = "��� ����������";
	private static final String SORT_NAME_ASC = "�� ����� (����)";
	private static final String SORT_NAME_DESC = "�� ����� (����)";
	private static final String SORT_CATEGORY_ASC = "��������� (����)";
	private static final String SORT_CATEGORY_DESC = "��������� (����)";

	// ������� ��������� ����������
	private static int orderState = ORDER_BY_NONE;
	private static String orderName = SORT_NONE;

	// ����� ���������
	public final static String ATTRIBUT_NAME_PRODUCT = "NameProduct";
	public final static String ATTRIBUT_COMMENT_PRODUCT = "CommentProduct";

	// ������� ��������� ������ ������� DB_TABLE_CATEGORIES
	String[] Cat_names = { "��� ���������", "�������� ���������",
			"���� � ����", "����� � ������", "��������" };

	// ������� ��������� ������ ������� DB_TABLE_PRODUCTS
	String[] Prod_names = { "�����", "������", "�������", "������", "����",
			"����", "����", "��������", "�������", "������" };
	int[] Prod_cat = { 2, 2, 2, 2, 2, 3, 3, 4, 4, 4 };

	// ������ �� �������� ������� DB_TABLE_PRODUCTS
	private static final String tableCreateProducts = "CREATE TABLE "
			+ DB_TABLE_PRODUCTS + " (" + PRODUCTS_ID
			+ " integer primary key autoincrement, " + PRODUCTS_NAME
			+ " text, " + PRODUCTS_CATEGORY + " integer);";

	// ������ �� �������� ������� DB_TABLE_CATEGORIES
	private static final String tableCreateCategory = "CREATE TABLE "
			+ DB_TABLE_CATEGORIES + " (" + CATEGORY_ID
			+ " integer primary key autoincrement, " + CATEGORY_NAME
			+ " text);";

	// ������ �� �������� ������� DB_TABLE_TEMPLATES
	private static final String tableCreateTemplates = "CREATE TABLE "
			+ DB_TABLE_TEMPLATES + " (" + TEMPLATE_RECORD
			+ " integer primary key autoincrement, " + TEMPLATE_NAME
			+ " text NOT NULL, " + TEMPLATE_PRODUCT + " text NOT NULL, "
			+ TEMPLATE_COMMENT + " text);";

	// ���������� ��������� ���������� ��� ������ � ��
	private Context mCtx;
	private DBHelper mdbHelper;
	private SQLiteDatabase mDB;

	/*------------------------------------������-----------------------------------------*/

	/**
	 * ����������� ����� ����������� �������� ��������� ���������� ������, �
	 * ����� ����� ������ �������� ���� ������
	 * 
	 * @param context
	 *            - ��-��������� ���������� �������� ��������
	 */
	public MyDBManager(Context context) {
		mCtx = context;
		open();
	}

	/**
	 * ��������� ����� �������� ���� ������. ���������� ��������������� ��
	 * ������������. ��������� ��������� ������ DBHelper � �����������:
	 * ��������, ��� � ������ ��. ����� ���� ����������� �� ��������������
	 */
	private void open() {
		mdbHelper = new DBHelper(mCtx, DB_NAME, null, DB_VERSION);
		mDB = mdbHelper.getWritableDatabase();
	}

	/**
	 * ����� ��������� ������������� �������� ���� ������ � ��������� �
	 */
	public void close() {
		if (mDB != null)
			mdbHelper.close();

	}

	/**
	 * ����� �������� ��� ������ �� ������� ���� ������ ��� ����������
	 * 
	 * @return ���������� ������ ���� Cursor
	 */
	public Cursor getData() {
		String query = "SELECT " + DB_TABLE_PRODUCTS + "." + PRODUCTS_ID + ", "
				+ DB_TABLE_PRODUCTS + "." + PRODUCTS_NAME + ", "
				+ DB_TABLE_CATEGORIES + "." + CATEGORY_NAME + " FROM "
				+ DB_TABLE_PRODUCTS + " JOIN " + DB_TABLE_CATEGORIES + " ON "
				+ DB_TABLE_PRODUCTS + "." + PRODUCTS_CATEGORY + "="
				+ DB_TABLE_CATEGORIES + "." + CATEGORY_ID + ";";
		return getData(query, orderState);
	}

	/**
	 * ����� �������� ������ �� ���� � ��������� ID
	 * 
	 * @param id
	 *            ������, ������ �� ������� ���������� �������
	 * @return ����� ���������� ������ ���� Cursor
	 */
	public Cursor getData(long id) {
		String query = "SELECT * FROM " + DB_TABLE_PRODUCTS + " WHERE "
				+ PRODUCTS_ID + "=" + id;
		return getData(query, ORDER_BY_NONE);
	}

	/**
	 * ����� ���������� ������ � �������� ���������
	 * 
	 * @return Cursor
	 */
	public Cursor getCategories() {
		String query = "SELECT * FROM " + DB_TABLE_CATEGORIES;
		return getData(query, ORDER_BY_NONE);
	}

	/**
	 * ����� ���������� ��� �������� ����������� ���������
	 * 
	 * @param ID_category
	 *            ��������� ���������
	 * @return Cursor
	 */
	public Cursor getProducsCategories(long ID_category) {
		String query = "SELECT * FROM " + DB_TABLE_PRODUCTS + " WHERE "
				+ PRODUCTS_CATEGORY + "=" + ID_category;
		return getData(query, ORDER_BY_NONE);
	}

	/**
	 * ������ ����� �������
	 * 
	 * @param query
	 *            ������ �������
	 * @param order
	 *            ������� ����������
	 * @return ������ Cursor � �������� ������
	 */
	private Cursor getData(String query, int order) {
		switch (order) {
		case ORDER_BY_NAME_ASC:
			return mDB.rawQuery(query + " ORDER BY " + PRODUCTS_NAME, null);
		case ORDER_BY_NAME_DESC:
			return mDB.rawQuery(query + " ORDER BY " + PRODUCTS_NAME + " desc",
					null);
		case ORDER_BY_CATEGORY_ASC:
			return mDB.rawQuery(query + " ORDER BY " + PRODUCTS_CATEGORY, null);
		case ORDER_BY_CATEGORY_DESC:
			return mDB.rawQuery(query + " ORDER BY " + PRODUCTS_CATEGORY
					+ " desc", null);
		default:
			return mDB.rawQuery(query, null);
		}

	}

	public long getIDProduct(String productName) {
		String query = "SELECT DISTINCT " + PRODUCTS_ID + " FROM "
				+ DB_TABLE_PRODUCTS + " WHERE " + PRODUCTS_NAME + "='"
				+ productName + "';";
		Cursor cursor = mDB.rawQuery(query, null);
		cursor.moveToFirst();
		try {
			long IDProduct = cursor.getLong(cursor
					.getColumnIndex(MyDBManager.PRODUCTS_ID));
			return IDProduct;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return (Long) null;
	}

	/**
	 * ���������� ������� ����������
	 * 
	 * @return integer �������� ������������� MyDBManager.ORDER_BY ����������
	 *         ������
	 */
	public static int getOrderState() {
		return orderState;
	}

	/**
	 * ������������� ������� ���������� �������� � �������� �� �� � � ��������
	 * 
	 * @param state
	 *            ������������� ����� MyDBManager.ORDER_BY ���������� ������
	 */
	public void setOrderState(int state) {
		orderState = state;
		switch (state) {
		case ORDER_BY_NONE:
			orderName = SORT_NONE;
			break;
		case ORDER_BY_NAME_ASC:
			orderName = SORT_NAME_ASC;
			break;
		case ORDER_BY_NAME_DESC:
			orderName = SORT_NAME_DESC;
			break;
		case ORDER_BY_CATEGORY_ASC:
			orderName = SORT_CATEGORY_ASC;
			break;
		case ORDER_BY_CATEGORY_DESC:
			orderName = SORT_CATEGORY_DESC;
			break;
		default:
			break;
		}
	}

	/**
	 * ���������� ������� ��� ����������
	 * 
	 * @return �������� ���� String
	 */
	public String getOrderName() {
		return orderName;
	}

	/**
	 * ����� ��������� ������ � ��
	 * 
	 * @param model
	 *            - ��������� �������� ������
	 * @param price
	 *            - ������������ ��� ����
	 * @param count
	 *            - ������������� ��� ���������� (�� ������)
	 */

	public void addRecord(ContentValues cv) {
		mDB.insert(DB_TABLE_PRODUCTS, null, cv);
	}

	/**
	 * ����� �������� ������ � ���� �� ID
	 * 
	 * @param id
	 *            - ����������������� ����� ������
	 * @param model
	 *            - ����� ������������ ������
	 * @param price
	 *            - ����� ����
	 * @param count
	 *            - ����� ���������� �� ������
	 */
	public void editRecord(ContentValues cv) {
		mDB.update(DB_TABLE_PRODUCTS, cv, "_id =" + cv.getAsLong(PRODUCTS_ID),
				null);
	}

	/**
	 * ����� ������� ������ �� ID
	 * 
	 * @param ID
	 *            - ����������������� ����� ������
	 */
	public void delRecord(long ID) {
		mDB.delete(DB_TABLE_PRODUCTS, PRODUCTS_ID + "=" + ID, null);
	}

	/**
	 * ����� ������� ��� ������ �� �������
	 */
	public void delRecord() {
		mDB.delete(DB_TABLE_PRODUCTS, null, null);
	}

	/**
	 * ����� ���������� ���������� ��������� � ������� Products
	 * 
	 * @return ������ ���� int
	 */
	public int getCount() {
		Cursor c = mDB.rawQuery("SELECT * FROM " + DB_TABLE_PRODUCTS, null);
		return c.getCount();
	}

	/**
	 * ����� ��������� ������ � ���� ������ ��� ������
	 * 
	 * @param mArray
	 *            ������ �������� � ���������� ��������� � ������������� � ���
	 * @param mTemplateName
	 *            ��� ������ �������
	 */
	public void saveToTemplate(ArrayList<HashMap<String, String>> mArray,
			String mTemplateName) {

	
		// ������� ������ ���������� ��������
		ArrayList<String> mTemplates = new ArrayList<String>();
		try {
			mTemplates.addAll(getTemplatesList());
	
			for (String string : mTemplates) {
				if (string == mTemplateName) {
					Toast.makeText(mCtx,
							"������ � ����� ������ ��� ����������",
							Toast.LENGTH_SHORT).show();
					return;
				}

			}
		} catch (Exception e) {
			Log.d(LOG_TAG, "���� ������ ����� ������");
		}

		
		try {

			for (HashMap<String, String> hashMap : mArray) {

				ContentValues cv = new ContentValues();

				cv.put(TEMPLATE_NAME, mTemplateName);
				cv.put(TEMPLATE_PRODUCT, hashMap.get(ATTRIBUT_NAME_PRODUCT));
				cv.put(TEMPLATE_COMMENT, hashMap.get(ATTRIBUT_COMMENT_PRODUCT));

				Log.d(LOG_TAG,
						cv.getAsString(TEMPLATE_NAME) + ", "
								+ cv.getAsString(TEMPLATE_PRODUCT) + ", "
								+ cv.getAsString(TEMPLATE_COMMENT));
				mDB.insert(DB_TABLE_TEMPLATES, null, cv);
			}

			Toast.makeText(mCtx, "������ '" + mTemplateName + "' �������",
					Toast.LENGTH_SHORT).show();
		} catch (Exception e) {
			Toast.makeText(mCtx, "������ ���������� �������",
					Toast.LENGTH_SHORT).show();
		}

	}

	/**
	 * ����� ���������� ������ ���������� �������� �������� �� ��
	 * 
	 * @return Cursor �� ������ ������� ���� String
	 */
	public ArrayList<String> getTemplatesList() {

		ArrayList<String> mList = new ArrayList<String>();

		String query = "SELECT DISTINCT " + TEMPLATE_NAME + " FROM "
				+ DB_TABLE_TEMPLATES;

		Cursor c = mDB.rawQuery(query, null);

		if (c.getCount() == 0)
			return null;

		c.moveToFirst();
		do {
			mList.add(c.getString(c.getColumnIndex(TEMPLATE_NAME)));

		} while (c.moveToNext());

		return mList;
	}

	public ArrayList<HashMap<String, String>> loadFromTemplates(String name) {
		// TODO: ����������� �������� ������ �� ���������� �������
		ArrayList<HashMap<String, String>> arr = new ArrayList<HashMap<String, String>>();		

		String query = "SELECT DISTINCT " + TEMPLATE_PRODUCT + ", "
				+ TEMPLATE_COMMENT + " FROM " + DB_TABLE_TEMPLATES + " WHERE "
				+ TEMPLATE_NAME + " LIKE " + "'" + name + "'";
		Cursor c = mDB.rawQuery(query, null);

		Log.d(LOG_TAG, "�������� �� ����. ��� ���������� ������� arr");
		c.moveToFirst();
		do {
			HashMap<String, String> hm = new HashMap<String, String>();
			hm.put(ATTRIBUT_NAME_PRODUCT,
					c.getString(c.getColumnIndex(TEMPLATE_PRODUCT)));
			hm.put(ATTRIBUT_COMMENT_PRODUCT,
					c.getString(c.getColumnIndex(TEMPLATE_COMMENT)));
			Log.d(LOG_TAG, hm.get(ATTRIBUT_NAME_PRODUCT)+" - "+hm.get(ATTRIBUT_COMMENT_PRODUCT));
			arr.add(hm);
		} while (c.moveToNext());
				
		Log.d(LOG_TAG, "�������� �� ����. ����� ��������� ������� arr");
		for (HashMap<String, String> hashMap : arr) {
			Log.d(LOG_TAG, hashMap.get(ATTRIBUT_NAME_PRODUCT)+" - "+hashMap.get(ATTRIBUT_COMMENT_PRODUCT));
		}
		
		return arr;
	}

	public void clearTemplates() {
		String query = "DELETE FROM " + DB_TABLE_TEMPLATES;
		mDB.execSQL(query);
		Toast.makeText(mCtx, "������� �������� �������", Toast.LENGTH_SHORT)
				.show();
	}

	// ��������������� �����, ����������� ����������� � ����������� ���� ������
	// � ���������, ��������� ������� ���� �� ��������� ������
	// getWritableDatabase()

	private class DBHelper extends SQLiteOpenHelper {

		public DBHelper(Context context, String name, CursorFactory factory,
				int version) {
			super(context, name, factory, version);

		}

		@Override
		public void onCreate(SQLiteDatabase db) {

			// �������� ������ ���� ������
			createTable(db, DB_TABLE_PRODUCTS, tableCreateProducts);
			createTable(db, DB_TABLE_CATEGORIES, tableCreateCategory);
			createTable(db, DB_TABLE_TEMPLATES, tableCreateTemplates);

			// ���������� ������� DB_TABLE_PRODUCTS ��������������� �������
			ContentValues cv = new ContentValues();
			Log.d(LOG_TAG, "��������� ��������� ������� " + DB_TABLE_PRODUCTS);
			try {
				for (int i = 0; i < Prod_names.length; i++) {
					cv.clear();
					cv.put(PRODUCTS_NAME, Prod_names[i]);
					cv.put(PRODUCTS_CATEGORY, Prod_cat[i]);
					db.insert(DB_TABLE_PRODUCTS, null, cv);
				}
				Log.d(LOG_TAG,
						"��������� ������� ���������� ��������� ������� "
								+ DB_TABLE_PRODUCTS);
			} catch (Exception e) {
				Log.d(LOG_TAG, "��������� ���������� ��������� ������� "
						+ DB_TABLE_PRODUCTS);
				e.printStackTrace();
			}

			// ���������� ������� DB_TABLE_CATEGORIES ��������������� �������
			Log.d(LOG_TAG, "��������� ��������� ������� " + DB_TABLE_CATEGORIES);
			try {
				for (int i = 0; i < Cat_names.length; i++) {
					cv.clear();
					cv.put(CATEGORY_NAME, Cat_names[i]);
					db.insert(DB_TABLE_CATEGORIES, null, cv);
				}
				Log.d(LOG_TAG,
						"��������� ������� ���������� ��������� ������� "
								+ DB_TABLE_CATEGORIES);
			} catch (Exception e) {
				Log.d(LOG_TAG, "��������� ���������� ��������� ������� "
						+ DB_TABLE_CATEGORIES);
				e.printStackTrace();
			}

		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

			dropTable(db, DB_TABLE_PRODUCTS);
			dropTable(db, DB_TABLE_CATEGORIES);
			dropTable(db, DB_TABLE_TEMPLATES);

			onCreate(db);
		}

		private void createTable(SQLiteDatabase db, String tableName, String sql) {
			Log.d(LOG_TAG, "������ ��������: " + tableName);
			try {
				db.execSQL(sql);
				Log.d(LOG_TAG, "��������� ������� �������� ������� "
						+ tableName);
			} catch (Exception e) {
				Log.d(LOG_TAG, "������� � �������� ������� " + tableName);
				e.printStackTrace();
			}
		}

		private void dropTable(SQLiteDatabase db, String tableName) {
			try {
				db.execSQL("DROP TABLE " + tableName + ";");
				Log.d(LOG_TAG, "������� " + tableName + " ������� �������");
			} catch (Exception e) {
				Log.d(LOG_TAG, "������� " + tableName + " �� �������. ������ "
						+ e);
			}
		}

	}
}
