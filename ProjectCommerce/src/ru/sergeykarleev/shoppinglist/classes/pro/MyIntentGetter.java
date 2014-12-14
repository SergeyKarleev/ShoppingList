package ru.sergeykarleev.shoppinglist.classes.pro;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import ru.sergeykarleev.shoppinglist.activities.MainActivity;
import ru.sergeykarleev.shoppinglist.classes.MyDBManager;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.util.Xml;
import android.widget.Toast;

/**
 * Класс отвечает за открытие присланного файла на устройстве Доступен в PRO
 * версии
 * 
 * @author SergeyKarleev
 * 
 */
public class MyIntentGetter {
	private final static String LOG_TAG = "myLogs";
	private final static int ATTRIBUT_NAME = 0;
	private final static int ATTRIBUT_COMMENT = 1;
	private static final int ATTRIBUT_CATEGORY = 2;

	MainActivity mActivity;
	ArrayList<HashMap<String, String>> mList;

	public MyIntentGetter(MainActivity mActivity) {
		super();
		this.mActivity = mActivity;
		mList = new ArrayList<HashMap<String, String>>();
		String data = getData();
		if (data != null) {
			dataParsing(data);
		}
	}

	public ArrayList<HashMap<String, String>> getListProducts() {
		for (HashMap<String, String> item : mList) {
			Log.d(LOG_TAG,
					MyDBManager.ATTRIBUT_NAME_PRODUCT + ": "
							+ item.get(MyDBManager.ATTRIBUT_NAME_PRODUCT));
			try {
				Log.d(LOG_TAG, MyDBManager.ATTRIBUT_COMMENT_PRODUCT + ": "
						+ item.get(MyDBManager.ATTRIBUT_COMMENT_PRODUCT));
			} catch (Exception e) {
				Log.d(LOG_TAG, "Нет комментариев");
			}
		}
		return mList;
	}

	private void dataParsing(String data) {
		try {
			// TODO: Здесь пишем конвертацию String to XML и парсер для
			// ArrayList
			XmlPullParser parser = Xml.newPullParser();
			parser.setInput(new StringReader(data));

			while (parser.getEventType() != XmlPullParser.END_DOCUMENT) {
				HashMap<String, String> hm;

				switch (parser.getEventType()) {
				case XmlPullParser.START_TAG:
					try {
						hm = new HashMap<String, String>();
						hm.put(MyDBManager.ATTRIBUT_NAME_PRODUCT,
								parser.getAttributeValue(ATTRIBUT_NAME));
						try {
							hm.put(MyDBManager.ATTRIBUT_COMMENT_PRODUCT,
									parser.getAttributeValue(ATTRIBUT_COMMENT));
						} catch (Exception e) {
						}

						try {
							hm.put(MyDBManager.ATTRIBUT_CATEGORY_PRODUCT,
									parser.getAttributeValue(ATTRIBUT_CATEGORY));
						} catch (Exception e) {
						}

						mList.add(hm);
					} catch (Exception e) {

					}
					break;
				default:
					break;
				}
				parser.next();
			}
		} catch (Exception e) {
			Log.d(LOG_TAG, "Не удалось распарсить строку\n" + e.toString());
		}

	}

	private String getData() {
		try {
			Intent intent = mActivity.getIntent();
			if (intent.getAction() == Intent.ACTION_MAIN) {
				return null;
			}
			Uri data = intent.getData();
			String path = data.getEncodedPath();

			ContentResolver cr = mActivity.getContentResolver();
			InputStream is = cr.openInputStream(data);
			if (is == null)
				return null;

			StringBuffer mData = new StringBuffer();
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(is));
			String str;
			while ((str = reader.readLine()) != null) {
				mData.append(str + "\n");
			}
			is.close();

			Log.d(LOG_TAG, "Загрузили данные:\n"+mData.toString());
			return mData.toString();

		} catch (Exception e) {
			return e.toString();
		}
	}
}
