
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
	private static final int DB_VERSION = 1;
	private static final String DB_TABLE = "Products";

	// Имена столбцов таблицы Products
	public static final String PRODUCTS_ID = BaseColumns._ID;
	public static final String PRODUCTS_NAME = "Name";
	public static final String PRODUCTS_PRICE = "Price";
	public static final String PRODUCTS_COUNT = "Count";

	// Массивы начальных данных таблицы Products
	String[] Names = { "Apple iPod touch 5 32Gb",
			"Samsung Galaxy S Duos S7562", "Canon EOS 600D Kit",
			"Samsung Galaxy Tab 2 10.1 P5100 16Gb", "PocketBook Touch",
			"Samsung Galaxy Note II 16Gb", "Nikon D3100 Kit",
			"Canon EOS 1100D Kit", "Sony Xperia acro S", "Lenovo G580" };

	float[] Prices = { (float) 8888, (float) 7230, (float) 15659,
			(float) 13290, (float) 5197, (float) 17049.50, (float) 12190,
			(float) 10985, (float) 11800.99, (float) 8922 };
	int[] Count = { 5, 2, 4, 9, 2, 2, 4, 2, 1, 1 };

	// Запрос на создание таблицы Products
	private static final String tableCreate = "CREATE TABLE " + DB_TABLE + " ("
			+ BaseColumns._ID + " integer primary key autoincrement, "
			+ PRODUCTS_NAME + " text, " + PRODUCTS_PRICE + " real, "
			+ PRODUCTS_COUNT + " integer);";

	// Объявление служебных переменных для работы с БД
	private Context mCtx;
	private DBHelper mdbHelper;
	private SQLiteDatabase mDB;

	/*------------------------------------МЕТОДЫ-----------------------------------------*/
	
	/**Конструктор
	 * Здесь реализована передача контекста экземпляру класса,
	 *а также вызов метода открытия базы данных 
	 * @param context - по-умолчанию передавать контекст активити
	 */
	public MyDBManager(Context context) {
		mCtx = context;
		open();
	}
	
	
	/**Приватный метод открытия базы данных. 
	 * Вызывается непосредственно из конструктора.
	 *Создается экземпляр класса DBHelper с параметрами: контекст, имя и версия БД.
	 *Затем база открывается на редактирование 
	 */
	private void open() {
		mdbHelper = new DBHelper(mCtx, DB_NAME, null, DB_VERSION);
		mDB = mdbHelper.getWritableDatabase();
	}

	
	/**Метод проверяет существование открытой базы данных и закрывает её 
	 */
	public void close() {
		if (mDB != null)
			mdbHelper.close();

	}
	
	/** Метод выбирает все строки из таблицы базы данных
	 * @return возвращает объект типа Cursor
	 */
	public Cursor getData() {
		return mDB.rawQuery("SELECT * FROM " + DB_TABLE, null);
	}
	

	/** Метод выбирает строку из базы с заданному ID
	 * @param id строки, данные из которой необходимо вернуть
	 * @return метод возвращает объект типа Cursor
	 */
	public Cursor getData(long id){				
		return mDB.rawQuery("SELECT * FROM "+DB_TABLE+" WHERE "+PRODUCTS_ID+"="+id, null);		
	}

	/**Метод выбирает только те строки, в которых количество моделей на складе больше нуля
	 * @return объект типа Cursor
	 */
	public Cursor getDataNonEmpty(){
		return mDB.rawQuery("SELECT * FROM "+DB_TABLE+" WHERE "+PRODUCTS_COUNT+">0", null);
	}
	
	/**Метод добавляет запись в БД
	 * @param model - строковое название модели
	 * @param price - вещественный тип цена
	 * @param count - целочисленный тип количество (на складе)
	 */
	public void addRecord(String model, float price, int count) {
		ContentValues cv = new ContentValues();
		cv.put(PRODUCTS_NAME, model);
		cv.put(PRODUCTS_PRICE, price);
		cv.put(PRODUCTS_COUNT, count);
		mDB.insert(DB_TABLE, null, cv);
	}
	
	
	/**Метод изменяет запись в базе по ID
	 * @param id - идентификационный номер строки
	 * @param model - новое наименование модели
	 * @param price - новая цена
	 * @param count - новое количество на складе
	 */
	public void editRecord(long id,String model, float price, int count){
		ContentValues cv = new ContentValues();
		cv.put(PRODUCTS_NAME, model);
		cv.put(PRODUCTS_PRICE, price);
		cv.put(PRODUCTS_COUNT, count);
		mDB.update(DB_TABLE, cv, "_id ="+id,null);		
	}

	
	/**Метод удаляет запись по ID
	 * @param ID - идентификационный номер строки
	 */
	public void delRecord(long ID) {
		mDB.delete(DB_TABLE, PRODUCTS_ID + "=" + ID, null);
	}
	
	/**Метод удаляет все записи из таблицы
	 */
	public void delRecord(){
		mDB.delete(DB_TABLE, null, null);
	}

	/**Метод возвращает количество элементов в базе
	 * @return объект типа int
	 */
	public int getCount(){
		Cursor c = mDB.rawQuery("SELECT * FROM "+DB_TABLE, null);
		//c.moveToFirst();		
		return c.getCount();		
	}
	
	public void importData(){
		//TODO:Реализация импорта
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
			// Создание таблицы и внесение информации в логи
			Log.d(LOG_TAG, "Создание таблицы " + DB_TABLE);
			db.execSQL(tableCreate);
			Log.d(LOG_TAG, "Выполнено успешно создание таблицы " + DB_TABLE);

			// Заполнение таблицы DB_TABLE первоначальными данными
			ContentValues cv = new ContentValues();
			Log.d(LOG_TAG, "Наполняем контентом таблицу " + DB_TABLE);
			for (int i = 0; i < Names.length; i++) {
				cv.clear();
				cv.put(PRODUCTS_NAME, Names[i]);
				cv.put(PRODUCTS_PRICE, Prices[i]);
				cv.put(PRODUCTS_COUNT, Count[i]);
				db.insert(DB_TABLE, null, cv);
			}
			Log.d(LOG_TAG, "Выполнено успешно наполнение контентом таблицы "
					+ DB_TABLE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.d(LOG_TAG, "Обновление базы данных " + DB_NAME);
			mDB.execSQL("DROP TABLE " + DB_TABLE);
			Log.d(LOG_TAG, "Выполнено удаление таблицы " + DB_TABLE
					+ " из базы " + DB_NAME);
			onCreate(mDB);
		}

	}
}
