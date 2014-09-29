package com.example.projectcommerce.classes;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URI;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Environment;
import android.provider.ContactsContract.Directory;
import android.util.Log;
import android.widget.Toast;

import com.example.projectcommerce.R;
import com.example.projectcommerce.fragments.MyFragmentBackend;

/**
 * Класс является реализацией паттерна Адаптер, выполняющей работу по
 * импорту/экспорту данных
 */
public class MyMigratorXML extends MyAbstractMigrator {

	final String LOG_TAG = "myLogs";

	// путь и имя файла импорта/экспорта
	private final String FILEPATH = "/ProjectCommerce/";
	private final String FILENAME = "db.xml";

	// TODO: Техническое задание:
	// 1. Доступ к импорту/экспорту осуществляется через меню фрагмента Backend
	// 2. Импорт
	// ~ осуществить режим добавления и полной замены (с предупреждением)
	// устройств
	// ~ режим импорта передавать аргументом и обрабатывать в одном методе
	// ~ должен включать реализацию XML (опц. - csv, binary, json)
	// ~ файл импорта находится на SD карте (опц. - загрузка через Web)
	// ~ для каждого типа файла реализуем свой отдельный метод
	// 3. Экспорт
	// ~ должен включать реализацию XML (опц. - binary, json, csv)
	// ~ экспорт на SD карту в определённую директорию
	// 4. Все методы и/э реализованы в данном классе (паттерн "Адаптер")
	// 5. Все методы и/э типа boolean. True - успешное выполнение, false - нет
	// это нужно для обновления ListView адаптера во фрагменте Backend
	// 6. В Backend фрагменте доступ к импорту/экспорту осуществлен через меню

	/**
	 * Через конструктор устанавливаем статическую ссылку на базу данных,
	 * открытую в FragmentBackend
	 * 
	 * @param mDataBase
	 *            ссылка на базу данных
	 * @param mActivity
	 *            ссылка на активити для вызова диалогов
	 * @param mFragmentBackend
	 *            ссылка на FragmentBackend для перезаписи адаптера ListView
	 */
	public MyMigratorXML(MyDBManager mDataBase,
			MyFragmentBackend mFragmentBackend) {
		super();
		this.mDataBase = mDataBase;
		this.mFragmentBackend = mFragmentBackend;
	}

	/**
	 * Импорт XML
	 * 
	 * @param mode
	 *            добавление или перезапись
	 * @param webLoad
	 *            адрес xml файла в сети
	 * @return true если импорт выполнен без ошибок
	 */
	public boolean ImportData(int mode, URI webLoad) {
		if (webLoad == null) {
			try {
				XmlPullParser xpp = null;
				try {
					// создаем парсер xml из нашего файла в ресурсах
					XmlPullParserFactory factory = XmlPullParserFactory
							.newInstance();
					xpp = factory.newPullParser();
					// InputStream isr =
					// mFragmentBackend.getResources().openRawResource(R.raw.data);
					File file = new File(
							Environment.getExternalStorageDirectory()
									+ FILEPATH + FILENAME);
					FileInputStream fis = new FileInputStream(file);
					xpp.setInput(new InputStreamReader(fis));

					if (mode == MODE_ACTION_UPDATE)
						mDataBase.delRecord();
					Log.d(LOG_TAG, "Данные в БД перед импортом очищены");
				} catch (Exception e) {
					e.printStackTrace();
					Log.d(LOG_TAG, "Ошибка импорта " + e.toString());
				}

				ContentValues cv = new ContentValues();

				// пока не увидим тег конца документа
				while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
					if ((xpp.getEventType() == XmlPullParser.START_TAG)
							&& (xpp.getName().equals("Product"))) {
//						String _name = xpp.getAttributeValue(null,
//								MyDBManager.PRODUCTS_NAME);
//						String _category = xpp.getAttributeValue(null,
//								MyDBManager.PRODUCTS_CATEGORY);
//						int _lot = Integer.valueOf(xpp.getAttributeValue(null,
//								MyDBManager.PRODUCTS_LOT));
//						Float _count = Float.valueOf(xpp.getAttributeValue(
//								null, MyDBManager.PRODUCTS_COUNT));
//						String _unit = xpp.getAttributeValue(null,
//								MyDBManager.PRODUCTS_UNIT);

						cv.clear();
						cv.put(MyDBManager.PRODUCTS_NAME, xpp
								.getAttributeValue(null,
										MyDBManager.PRODUCTS_NAME));
						cv.put(MyDBManager.PRODUCTS_CATEGORY, xpp
								.getAttributeValue(null,
										MyDBManager.PRODUCTS_CATEGORY));
						cv.put(MyDBManager.PRODUCTS_LOT, Integer.valueOf(xpp
								.getAttributeValue(null,
										MyDBManager.PRODUCTS_LOT)));
						cv.put(MyDBManager.PRODUCTS_COUNT, Float.valueOf(xpp
								.getAttributeValue(null,
										MyDBManager.PRODUCTS_COUNT)));
						cv.put(MyDBManager.PRODUCTS_UNIT, xpp
								.getAttributeValue(null,
										MyDBManager.PRODUCTS_UNIT));

						mDataBase.addRecord(cv);
						Log.d(LOG_TAG, "Импорт из XML файла прошел успешно");
					}
					xpp.next();
				}
				Log.d(LOG_TAG, "Возвращаем true из импорта");
				return true;

			} catch (XmlPullParserException e) {
				e.printStackTrace();
				Log.d(LOG_TAG, "Ошибка импорта " + e.toString());
			} catch (IOException e) {
				e.printStackTrace();
				Log.d(LOG_TAG, "Ошика импорта " + e.toString());
			} catch (Throwable t) {
				Log.d(LOG_TAG, "Ошибка загрузки файла " + t.toString() + "\n"
						+ t.getStackTrace());
			}
		} else {
			// TODO: В РАЗРАБОТКЕ. Здесь будет реализация импорта XML через Web
		}
		return false;
	}

	/**
	 * Экспорт базы в виде файла XML на SD-карту
	 * 
	 * @return успешный или неудачный экспорт
	 */
	public boolean ExportData() {
		Cursor mData = mDataBase.getData();

		// проверяем, доступна ли карта памяти
		if (!Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			Log.d(LOG_TAG,
					"SD-карта не доступна: "
							+ Environment.getExternalStorageState());
			return false;
		}
		if (mData != null) {

			// создаем стрингбилдер для постепенного заполнения xml строки
			StringBuilder xmlString = new StringBuilder();
			// записываем начальный тег
			xmlString.append("<data>");
			// каждый элемент из таблицы в БД записываем в отдельный тег с
			// аргументами
			mData.moveToFirst();
			do {
				xmlString.append("<Product "+MyDBManager.PRODUCTS_NAME+"='");
				xmlString.append(mData.getString(mData
						.getColumnIndex(mDataBase.PRODUCTS_NAME)));
				xmlString.append("' "+MyDBManager.PRODUCTS_CATEGORY+"='");
				xmlString.append(mData.getString(mData.getColumnIndex(MyDBManager.PRODUCTS_CATEGORY)));
				xmlString.append("' "+MyDBManager.PRODUCTS_LOT+"='");
				xmlString.append(String.valueOf(mData.getInt(mData
						.getColumnIndex(mDataBase.PRODUCTS_LOT))));
				xmlString.append("' "+MyDBManager.PRODUCTS_COUNT+"='");
				xmlString.append(String.valueOf(mData.getFloat(mData
						.getColumnIndex(mDataBase.PRODUCTS_COUNT))));
				xmlString.append("' "+MyDBManager.PRODUCTS_UNIT+"='");
				xmlString.append(mData.getString(mData.getColumnIndex(MyDBManager.PRODUCTS_UNIT)));				
				xmlString.append("'/>");
			} while (mData.moveToNext());
			// дописываем конечный тег
			xmlString.append("</data>");
			Log.d(LOG_TAG, "Лог из экспорта " + xmlString.toString());
			return writeFile(xmlString.toString());
		}
		return false;
	}

	/**
	 * В данном методе происходит непосредственная запись xml строки в файл на
	 * диске
	 * 
	 * @param saveString
	 *            строка с элементами базы данных
	 * @param type
	 *            расширение файла (выбрать из констант)
	 * @return результат записи: успешно или нет
	 */
	private boolean writeFile(String saveString) {

		try {
			// Создаем каталог миграции данных на карте памяти
			File sdPath = Environment.getExternalStorageDirectory();
			sdPath = new File(sdPath.getAbsolutePath() + "/" + FILEPATH);
			sdPath.mkdirs();

			File sdFile = new File(Environment.getExternalStorageDirectory()
					+ FILEPATH, FILENAME);

			// отрываем поток для записи
			BufferedWriter bw = new BufferedWriter(new FileWriter(sdFile));
			// пишем данные
			bw.write(saveString);
			// закрываем поток
			bw.close();
			Toast.makeText(mFragmentBackend.getActivity(),
					"Экспортировано в " + sdFile.getAbsoluteFile().toString(),
					Toast.LENGTH_SHORT).show();
			return true;
		} catch (FileNotFoundException e) {
			Log.d(LOG_TAG, "FileNotFoundException: " + e.toString());
			e.printStackTrace();
		} catch (IOException e) {
			Toast.makeText(mFragmentBackend.getActivity(),
					"Ошибка записи на SD-карту", Toast.LENGTH_SHORT).show();
			Log.d(LOG_TAG, "EXPORT_XML: IOException " + e.toString());
			e.printStackTrace();
		}

		return false;
	}
}
