package com.example.projectcommerce.classes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

public class MyDBManager implements BaseColumns {

	/*------------------------------------ПЕРЕМЕННЫЕ-----------------------------------------*/

	// Переменная тега для логов
	private final String LOG_TAG = "myLogs";

	// Техническая информация создаваемой базы данных
	private static final String DB_NAME = "myDB";
	private static final int DB_VERSION = 2;
	private static final String DB_TABLE_PRODUCTS = "ProductsTable";	

	// Имена столбцов таблицы ProductsTable
	public static final String PRODUCTS_ID = BaseColumns._ID;
	public static final String PRODUCTS_NAME = "Name"; 
	public static final String PRODUCTS_COUNT = "Count";
	public static final String PRODUCTS_LOT = "Lot";
	public static final String PRODUCTS_UNIT = "Unit";

	// Имена столбцов таблицы CategoryTable
	public static final String PRODUCTS_CATEGORY = "CategoryName";	

	// Массивы начальных данных таблицы ProductsTable
	String[] Names = { "Масло", "Молоко", "Сметана", "Творог", "Яйца", "Мясо",
			"Рыба", "Картошка", "Морковь", "Яблоки" };

	String[] Category = { "Молочная продукция", "Молочная продукция",
			"Молочная продукция", "Молочная продукция", "Молочная продукция",
			"Мясо и рыба", "Мясо и рыба", "Овощи и фрукты", "Овощи и фрукты",
			"Овощи и фрукты" };
	int[] Lot = { 1, 1, 1, 1, 1, 2, 2, 2, 3, 3 };
	float[] Count = { (float) 0.25, 1, (float) 0.25, (float) 0.3, 10,
			(float) 0.5, (float) 0.5, 1, 1, 1 };
	String[] Unit = { "кг", "л", "кг", "кг", "шт", "кг", "кг", "кг", "шт", "шт" };

	// Запрос на создание таблицы ProductsTable
	private static final String tableCreateProducts = "CREATE TABLE "
			+ DB_TABLE_PRODUCTS + " (" + PRODUCTS_ID
			+ " integer primary key autoincrement, " + PRODUCTS_NAME
			+ " text, " + PRODUCTS_CATEGORY + " text, " + PRODUCTS_LOT
			+ " integer, " + PRODUCTS_COUNT + " real, " + PRODUCTS_UNIT
			+ " text);";

	// Объявление служебных переменных для работы с БД
	private Context mCtx;
	private DBHelper mdbHelper;
	private SQLiteDatabase mDB;

	/*------------------------------------МЕТОДЫ-----------------------------------------*/

	/**
	 * Конструктор Здесь реализована передача контекста экземпляру класса, а
	 * также вызов метода открытия базы данных
	 * 
	 * @param context
	 *            - по-умолчанию передавать контекст активити
	 */
	public MyDBManager(Context context) {
		mCtx = context;
		open();
	}

	/**
	 * Приватный метод открытия базы данных. Вызывается непосредственно из
	 * конструктора. Создается экземпляр класса DBHelper с параметрами:
	 * контекст, имя и версия БД. Затем база открывается на редактирование
	 */
	private void open() {
		mdbHelper = new DBHelper(mCtx, DB_NAME, null, DB_VERSION);
		mDB = mdbHelper.getWritableDatabase();
	}

	/**
	 * Метод проверяет существование открытой базы данных и закрывает её
	 */
	public void close() {
		if (mDB != null)
			mdbHelper.close();

	}

	/**
	 * Метод выбирает все строки из таблицы базы данных
	 * 
	 * @return возвращает объект типа Cursor
	 */
	public Cursor getData() {
		return mDB.rawQuery("SELECT * FROM " + DB_TABLE_PRODUCTS, null);
	}

	/**
	 * Метод выбирает строку из базы с заданному ID
	 * 
	 * @param id
	 *            строки, данные из которой необходимо вернуть
	 * @return метод возвращает объект типа Cursor
	 */
	public Cursor getData(long id) {
		return mDB.rawQuery("SELECT * FROM " + DB_TABLE_PRODUCTS + " WHERE "
				+ PRODUCTS_ID + "=" + id, null);
	}

	/**
	 * Метод выбирает только те строки, в которых количество лотов больше нуля
	 * 
	 * @return объект типа Cursor
	 */
	public Cursor getDataNonEmpty() {
		return mDB.rawQuery("SELECT * FROM " + DB_TABLE_PRODUCTS + " WHERE "
				+ PRODUCTS_LOT + ">0", null);
	}

	/**
	 * Метод добавляет запись в БД
	 * 
	 * @param model
	 *            - строковое название модели
	 * @param price
	 *            - вещественный тип цена
	 * @param count
	 *            - целочисленный тип количество (на складе)
	 */
	public void addRecord(ContentValues cv) {
		mDB.insert(DB_TABLE_PRODUCTS, null, cv);
	}

	/**
	 * Метод изменяет запись в базе по ID
	 * 
	 * @param id
	 *            - идентификационный номер строки
	 * @param model
	 *            - новое наименование модели
	 * @param price
	 *            - новая цена
	 * @param count
	 *            - новое количество на складе
	 */
	public void editRecord(ContentValues cv) {
		mDB.update(DB_TABLE_PRODUCTS, cv, "_id =" + cv.getAsLong(PRODUCTS_ID),
				null);
	}

	/**
	 * Метод удаляет запись по ID
	 * 
	 * @param ID
	 *            - идентификационный номер строки
	 */
	public void delRecord(long ID) {
		mDB.delete(DB_TABLE_PRODUCTS, PRODUCTS_ID + "=" + ID, null);
	}

	/**
	 * Метод удаляет все записи из таблицы
	 */
	public void delRecord() {
		mDB.delete(DB_TABLE_PRODUCTS, null, null);
	}

	/**
	 * Метод возвращает количество элементов в таблице Products
	 * 
	 * @return объект типа int
	 */
	public int getCount() {
		Cursor c = mDB.rawQuery("SELECT * FROM " + DB_TABLE_PRODUCTS, null);
		return c.getCount();
	}

	// Вспомогательный класс, позволяющий оперировать с информацией базы данных
	// в частности, позволяет открыть базу на изменение данных
	// getWritableDatabase()

	private class DBHelper extends SQLiteOpenHelper {

		public DBHelper(Context context, String name, CursorFactory factory,
				int version) {
			super(context, name, factory, version);

		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			// Создание таблицы продуктов и внесение информации в логи
			Log.d(LOG_TAG, "Создание таблицы " + DB_TABLE_PRODUCTS);
			Log.d(LOG_TAG, "запрос создания: "+tableCreateProducts);
			try {
				db.execSQL(tableCreateProducts);
				Log.d(LOG_TAG, "Выполнено успешно создание таблицы "
						+ DB_TABLE_PRODUCTS);
			} catch (Exception e) {
				Log.d(LOG_TAG, "Неудача в создании таблицы "
						+ DB_TABLE_PRODUCTS);
				e.printStackTrace();
			}

			// Заполнение таблицы DB_TABLE_PRODUCTS первоначальными данными
			ContentValues cv = new ContentValues();
			Log.d(LOG_TAG, "Наполняем контентом таблицу " + DB_TABLE_PRODUCTS);
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
						"Выполнено успешно наполнение контентом таблицы "
								+ DB_TABLE_PRODUCTS);
			} catch (Exception e) {
				Log.d(LOG_TAG, "Неудачное наполнение контентом таблицы "
						+ DB_TABLE_PRODUCTS);
				e.printStackTrace();
			}
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.d(LOG_TAG, "Обновление базы данных " + DB_NAME);
			mDB.execSQL("DROP TABLE "+DB_TABLE_PRODUCTS+";");
			Log.d(LOG_TAG, "Выполнено удаление таблиц"+ " из базы " + DB_NAME);
			onCreate(mDB);
		}

	}
}
