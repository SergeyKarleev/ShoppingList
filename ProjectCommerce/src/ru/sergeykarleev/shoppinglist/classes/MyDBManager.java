package ru.sergeykarleev.shoppinglist.classes;

import ru.sergeykarleev.shoppinglist.activities.MainActivity;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

public class MyDBManager implements BaseColumns {

	/*------------------------------------����������-----------------------------------------*/

	// ���������� ���� ��� �����
	private final String LOG_TAG = "myLogs";

	// ����������� ���������� ����������� ���� ������
	private static final String DB_NAME = "myDB";
	private static final int DB_VERSION = 2;
	private static final String DB_TABLE_PRODUCTS = "ProductsTable";

	// ����� �������� ������� ProductsTable
	public static final String PRODUCTS_ID = BaseColumns._ID;
	public static final String PRODUCTS_NAME = "Name";
	public static final String PRODUCTS_COUNT = "Count";
	public static final String PRODUCTS_LOT = "Lot";
	public static final String PRODUCTS_UNIT = "Unit";

	// ������� ���������� ����������
	public static final int ORDER_BY_NONE = 0;
	public static final int ORDER_BY_NAME_ASC = 1;
	public static final int ORDER_BY_NAME_DESC = 2;
	public static final int ORDER_BY_CATEGORY_ASC = 3;
	public static final int ORDER_BY_CATEGORY_DESC = 4;

	// ����� �������� ������� CategoryTable
	public static final String PRODUCTS_CATEGORY = "CategoryName";

	// ������� ��������� ����������
	private static int orderState = ORDER_BY_NONE;

	// ������� ��������� ������ ������� ProductsTable
	String[] Names = { "�����", "������", "�������", "������", "����", "����",
			"����", "��������", "�������", "������" };

	String[] Category = { "�������� ���������", "�������� ���������",
			"�������� ���������", "�������� ���������", "�������� ���������",
			"���� � ����", "���� � ����", "����� � ������", "����� � ������",
			"����� � ������" };
	int[] Lot = { 1, 1, 1, 1, 1, 2, 2, 2, 3, 3 };
	float[] Count = { (float) 0.25, 1, (float) 0.25, (float) 0.3, 10,
			(float) 0.5, (float) 0.5, 1, 1, 1 };
	String[] Unit = { "��", "�", "��", "��", "��", "��", "��", "��", "��", "��" };

	// ������ �� �������� ������� ProductsTable
	private static final String tableCreateProducts = "CREATE TABLE "
			+ DB_TABLE_PRODUCTS + " (" + PRODUCTS_ID
			+ " integer primary key autoincrement, " + PRODUCTS_NAME
			+ " text, " + PRODUCTS_CATEGORY + " text, " + PRODUCTS_LOT
			+ " integer, " + PRODUCTS_COUNT + " real, " + PRODUCTS_UNIT
			+ " text);";

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
	 * ����� �������� ������ �� ������, � ������� ���������� ����� ������ ����
	 * 
	 * @return ������ ���� Cursor
	 */
	public Cursor getDataNonEmpty() {
		String query = "SELECT * FROM " + DB_TABLE_PRODUCTS + " WHERE "
				+ PRODUCTS_LOT + ">0";
		return getData(query, orderState);
	}

	/**
	 * ����� �������� ��� ������ �� ������� ���� ������ ��� ����������
	 * 
	 * @return ���������� ������ ���� Cursor
	 */
	public Cursor getData() {
		String query = "SELECT * FROM " + DB_TABLE_PRODUCTS;
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

	/**���������� ������� ����������
	 * @return integer �������� ������������� MyDBManager.ORDER_BY ���������� ������
	 */
	public static int getOrderState() {
		return orderState;
	}

	/**������������� ������� ���������� �������� � �������� �� ��
	 * @param state ������������� ����� MyDBManager.ORDER_BY ���������� ������
	 */
	public void setOrderState(int state) {
		orderState = state;
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
			// �������� ������� ��������� � �������� ���������� � ����
			Log.d(LOG_TAG, "�������� ������� " + DB_TABLE_PRODUCTS);
			Log.d(LOG_TAG, "������ ��������: " + tableCreateProducts);
			try {
				db.execSQL(tableCreateProducts);
				Log.d(LOG_TAG, "��������� ������� �������� ������� "
						+ DB_TABLE_PRODUCTS);
			} catch (Exception e) {
				Log.d(LOG_TAG, "������� � �������� ������� "
						+ DB_TABLE_PRODUCTS);
				e.printStackTrace();
			}

			// ���������� ������� DB_TABLE_PRODUCTS ��������������� �������
			ContentValues cv = new ContentValues();
			Log.d(LOG_TAG, "��������� ��������� ������� " + DB_TABLE_PRODUCTS);
			try {
				for (int i = 0; i < Names.length; i++) {
					cv.clear();
					cv.put(PRODUCTS_NAME, Names[i]);
					cv.put(PRODUCTS_CATEGORY, Category[i]);
					cv.put(PRODUCTS_LOT, Lot[i]);
					cv.put(PRODUCTS_COUNT, Count[i]);
					cv.put(PRODUCTS_UNIT, Unit[i]);
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
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.d(LOG_TAG, "���������� ���� ������ " + DB_NAME);
			mDB.execSQL("DROP TABLE " + DB_TABLE_PRODUCTS + ";");
			Log.d(LOG_TAG, "��������� �������� ������" + " �� ���� " + DB_NAME);
			onCreate(mDB);
		}

	}
}
