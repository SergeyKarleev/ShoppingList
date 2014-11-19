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

	/*------------------------------------ПЕРЕМЕННЫЕ-----------------------------------------*/

	// Переменная тега для логов
	private final String LOG_TAG = "myLogs";

	// Техническая информация создаваемой базы данных
	private static final String DB_NAME = "myDB.db";
	private static final int DB_VERSION = 2;

	// Названия таблиц в базе данных
	private static final String DB_TABLE_PRODUCTS = "ProductsTable";
	private static final String DB_TABLE_CATEGORIES = "CategoryTable";
	private static final String DB_TABLE_TEMPLATES = "TemplateTable";

	// Имена столбцов таблицы DB_TABLE_PRODUCTS
	public static final String PRODUCTS_ID = BaseColumns._ID;
	public static final String PRODUCTS_NAME = "Name_product";
	public static final String PRODUCTS_CATEGORY = "ID_category";

	// Имена столбцов таблицы DB_TABLE_CATEGORIES
	public static final String CATEGORY_ID = BaseColumns._ID;
	public static final String CATEGORY_NAME = "Name_category";

	// Имена столбцов таблицы DB_TABLE_TEMPLATES
	public static final String TEMPLATE_RECORD = BaseColumns._ID;
	public static final String TEMPLATE_NAME = "Name_template";
	public static final String TEMPLATE_PRODUCT = "Product_template";
	public static final String TEMPLATE_COMMENT = "Comment_tamplate";

	// Порядок сортировки результата
	public static final int ORDER_BY_NONE = 0;
	public static final int ORDER_BY_NAME_ASC = 1;
	public static final int ORDER_BY_NAME_DESC = 2;
	public static final int ORDER_BY_CATEGORY_ASC = 3;
	public static final int ORDER_BY_CATEGORY_DESC = 4;

	// Имена сортировки
	private static final String SORT_NONE = "без сортировки";
	private static final String SORT_NAME_ASC = "по имени (возр)";
	private static final String SORT_NAME_DESC = "по имени (убыв)";
	private static final String SORT_CATEGORY_ASC = "категория (возр)";
	private static final String SORT_CATEGORY_DESC = "категория (убыв)";

	// Текущее состояние сортировки
	private static int orderState = ORDER_BY_NONE;
	private static String orderName = SORT_NONE;

	// Имена атрибутов
	public final static String ATTRIBUT_NAME_PRODUCT = "NameProduct";
	public final static String ATTRIBUT_COMMENT_PRODUCT = "CommentProduct";

	// Массивы начальных данных таблицы DB_TABLE_CATEGORIES
	String[] Cat_names = { "Без категории", "Молочная продукция",
			"Мясо и рыба", "Овощи и фрукты", "Сладости" };

	// Массивы начальных данных таблицы DB_TABLE_PRODUCTS
	String[] Prod_names = { "Масло", "Молоко", "Сметана", "Творог", "Яйца",
			"Мясо", "Рыба", "Картошка", "Морковь", "Яблоки" };
	int[] Prod_cat = { 2, 2, 2, 2, 2, 3, 3, 4, 4, 4 };

	// Запрос на создание таблицы DB_TABLE_PRODUCTS
	private static final String tableCreateProducts = "CREATE TABLE "
			+ DB_TABLE_PRODUCTS + " (" + PRODUCTS_ID
			+ " integer primary key autoincrement, " + PRODUCTS_NAME
			+ " text, " + PRODUCTS_CATEGORY + " integer);";

	// Запрос на создание таблицы DB_TABLE_CATEGORIES
	private static final String tableCreateCategory = "CREATE TABLE "
			+ DB_TABLE_CATEGORIES + " (" + CATEGORY_ID
			+ " integer primary key autoincrement, " + CATEGORY_NAME
			+ " text);";

	// Запрос на создание таблицы DB_TABLE_TEMPLATES
	private static final String tableCreateTemplates = "CREATE TABLE "
			+ DB_TABLE_TEMPLATES + " (" + TEMPLATE_RECORD
			+ " integer primary key autoincrement, " + TEMPLATE_NAME
			+ " text NOT NULL, " + TEMPLATE_PRODUCT + " text NOT NULL, "
			+ TEMPLATE_COMMENT + " text);";

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
	 * Метод выбирает все строки из таблицы базы данных без сортировки
	 * 
	 * @return возвращает объект типа Cursor
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
	 * Метод выбирает строку из базы с заданному ID
	 * 
	 * @param id
	 *            строки, данные из которой необходимо вернуть
	 * @return метод возвращает объект типа Cursor
	 */
	public Cursor getData(long id) {
		String query = "SELECT * FROM " + DB_TABLE_PRODUCTS + " WHERE "
				+ PRODUCTS_ID + "=" + id;
		return getData(query, ORDER_BY_NONE);
	}

	/**
	 * Метод возвращает курсор с таблицей категорий
	 * 
	 * @return Cursor
	 */
	public Cursor getCategories() {
		String query = "SELECT * FROM " + DB_TABLE_CATEGORIES;
		return getData(query, ORDER_BY_NONE);
	}

	/**
	 * Метод возвращает все продукты определённой категории
	 * 
	 * @param ID_category
	 *            категория продуктов
	 * @return Cursor
	 */
	public Cursor getProducsCategories(long ID_category) {
		String query = "SELECT * FROM " + DB_TABLE_PRODUCTS + " WHERE "
				+ PRODUCTS_CATEGORY + "=" + ID_category;
		return getData(query, ORDER_BY_NONE);
	}

	/**
	 * Главны метод выборки
	 * 
	 * @param query
	 *            строка запроса
	 * @param order
	 *            порядок сортировки
	 * @return объект Cursor с выборкой данных
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
	 * Возвращает текущую сортировку
	 * 
	 * @return integer значение соответствует MyDBManager.ORDER_BY переменным
	 *         класса
	 */
	public static int getOrderState() {
		return orderState;
	}

	/**
	 * Устанавливает порядок сортировки значений в выгрузке из БД и её название
	 * 
	 * @param state
	 *            устанавливать через MyDBManager.ORDER_BY переменную класса
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
	 * Возвращает текущее имя сортировки
	 * 
	 * @return значение типа String
	 */
	public String getOrderName() {
		return orderName;
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

	/**
	 * Метод сохраняет список в базу данных как шаблон
	 * 
	 * @param mArray
	 *            массив векторов с названиями продуктов и комментариями к ним
	 * @param mTemplateName
	 *            имя нового шаблона
	 */
	public void saveToTemplate(ArrayList<HashMap<String, String>> mArray,
			String mTemplateName) {

	
		// Создаем курсор уникальных шаблонов
		ArrayList<String> mTemplates = new ArrayList<String>();
		try {
			mTemplates.addAll(getTemplatesList());
	
			for (String string : mTemplates) {
				if (string == mTemplateName) {
					Toast.makeText(mCtx,
							"Шаблон с таким именем уже существует",
							Toast.LENGTH_SHORT).show();
					return;
				}

			}
		} catch (Exception e) {
			Log.d(LOG_TAG, "Этот шаблон будет первым");
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

			Toast.makeText(mCtx, "Шаблон '" + mTemplateName + "' сохранён",
					Toast.LENGTH_SHORT).show();
		} catch (Exception e) {
			Toast.makeText(mCtx, "Ошибка сохранения шаблона",
					Toast.LENGTH_SHORT).show();
		}

	}

	/**
	 * Метод возвращает список уникальных названий шаблонов из БД
	 * 
	 * @return Cursor из одного столбца типа String
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
		// TODO: реализовать загрузку списка из выбранного шаблона
		ArrayList<HashMap<String, String>> arr = new ArrayList<HashMap<String, String>>();		

		String query = "SELECT DISTINCT " + TEMPLATE_PRODUCT + ", "
				+ TEMPLATE_COMMENT + " FROM " + DB_TABLE_TEMPLATES + " WHERE "
				+ TEMPLATE_NAME + " LIKE " + "'" + name + "'";
		Cursor c = mDB.rawQuery(query, null);

		Log.d(LOG_TAG, "Проверка на базе. При заполнении массива arr");
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
				
		Log.d(LOG_TAG, "Проверка на базе. Перед передачей массива arr");
		for (HashMap<String, String> hashMap : arr) {
			Log.d(LOG_TAG, hashMap.get(ATTRIBUT_NAME_PRODUCT)+" - "+hashMap.get(ATTRIBUT_COMMENT_PRODUCT));
		}
		
		return arr;
	}

	public void clearTemplates() {
		String query = "DELETE FROM " + DB_TABLE_TEMPLATES;
		mDB.execSQL(query);
		Toast.makeText(mCtx, "Таблица шаблонов очищена", Toast.LENGTH_SHORT)
				.show();
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

			// Создание таблиц базы данных
			createTable(db, DB_TABLE_PRODUCTS, tableCreateProducts);
			createTable(db, DB_TABLE_CATEGORIES, tableCreateCategory);
			createTable(db, DB_TABLE_TEMPLATES, tableCreateTemplates);

			// Заполнение таблицы DB_TABLE_PRODUCTS первоначальными данными
			ContentValues cv = new ContentValues();
			Log.d(LOG_TAG, "Наполняем контентом таблицу " + DB_TABLE_PRODUCTS);
			try {
				for (int i = 0; i < Prod_names.length; i++) {
					cv.clear();
					cv.put(PRODUCTS_NAME, Prod_names[i]);
					cv.put(PRODUCTS_CATEGORY, Prod_cat[i]);
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

			// Заполнение таблицы DB_TABLE_CATEGORIES первоначальными данными
			Log.d(LOG_TAG, "Наполняем контентом таблицу " + DB_TABLE_CATEGORIES);
			try {
				for (int i = 0; i < Cat_names.length; i++) {
					cv.clear();
					cv.put(CATEGORY_NAME, Cat_names[i]);
					db.insert(DB_TABLE_CATEGORIES, null, cv);
				}
				Log.d(LOG_TAG,
						"Выполнено успешно наполнение контентом таблицы "
								+ DB_TABLE_CATEGORIES);
			} catch (Exception e) {
				Log.d(LOG_TAG, "Неудачное наполнение контентом таблицы "
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
			Log.d(LOG_TAG, "Запрос создания: " + tableName);
			try {
				db.execSQL(sql);
				Log.d(LOG_TAG, "Выполнено успешно создание таблицы "
						+ tableName);
			} catch (Exception e) {
				Log.d(LOG_TAG, "Неудача в создании таблицы " + tableName);
				e.printStackTrace();
			}
		}

		private void dropTable(SQLiteDatabase db, String tableName) {
			try {
				db.execSQL("DROP TABLE " + tableName + ";");
				Log.d(LOG_TAG, "Таблица " + tableName + " удалена успешно");
			} catch (Exception e) {
				Log.d(LOG_TAG, "Таблица " + tableName + " не удалена. Ошибка "
						+ e);
			}
		}

	}
}
