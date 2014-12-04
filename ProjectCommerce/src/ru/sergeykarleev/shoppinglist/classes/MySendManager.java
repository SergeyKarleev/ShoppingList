package ru.sergeykarleev.shoppinglist.classes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ru.sergeykarleev.shoppinglist.R;
import ru.sergeykarleev.shoppinglist.activities.MainActivity;
import ru.sergeykarleev.shoppinglist.fragments.MyFragmentStorefront;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;

public class MySendManager {

	private final static String LOG_TAG = "myLogs";

	public final static int SEND_TO_SMS = 21;
	public final static int SEND_TO_BLUETOOTH = 22;
	public final static int SEND_TO_EMAIL = 23;

	private static MainActivity mActivity;
	private static MyConvertHelper mConverter;

	/**
	 * @param sendMode
	 */
	public MySendManager(MainActivity mActivity, int sendMode,
			ArrayList<HashMap<String, String>> listProducts) {
		super();
		this.mActivity = mActivity;
		mConverter = new MyConvertHelper(listProducts);

		switch (sendMode) {
		case SEND_TO_SMS:
			sendToSMS();
			break;
		case SEND_TO_EMAIL:
			sendToEmail();
		default:
			break;
		}
	}

	private void sendToEmail() {
//		Intent intent = new Intent(Intent.ACTION_SEND);
//		intent.setType("text/plain");
//		intent.putExtra(Intent.EXTRA_EMAIL, "");
//		intent.putExtra(Intent.EXTRA_SUBJECT, R.string.mail_subject);
//		intent.putExtra(Intent.EXTRA_TEXT, mConverter.convertToString());
//		intent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://"));
		
		Log.d(LOG_TAG, "Формируем XML строку");
		String s = mConverter.convertToXML();
		// TODO: добавить в виде вложения файл для программы (сохранение в КЭШ)
		Log.d(LOG_TAG, s);
		

		// mActivity.startActivity(intent);

	}

	private void sendToSMS() {
		Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("sms:"));
		intent.putExtra("sms_body", mConverter.convertToString());
		mActivity.startActivity(intent);
	}

	private class MyConvertHelper {
		ArrayList<HashMap<String, String>> listProducts;

		public MyConvertHelper(ArrayList<HashMap<String, String>> listProducts) {
			super();
			this.listProducts = listProducts;
		}

		/** Функция конвертирует список продуктов из ArrayList в String */
		public String convertToString() {

			StringBuilder sb = new StringBuilder();
			for (HashMap<String, String> item : listProducts) {
				sb.append(item.get(MyDBManager.ATTRIBUT_NAME_PRODUCT)
						.toString());
				if (item.get(MyDBManager.ATTRIBUT_COMMENT_PRODUCT).isEmpty())
					sb.append(";\n");
				else {
					sb.append(": "
							+ item.get(MyDBManager.ATTRIBUT_COMMENT_PRODUCT)
									.toString() + ";\n");
				}
			}
			return sb.toString();
		}

		/** Функция конвертирует список продуктов из ArrayList в String  */
		private String convertToXML() {
			StringBuilder sb = new StringBuilder();
			sb.append("<data>");
			for (HashMap<String, String> item : listProducts) {
				sb.append("<product name='");
				sb.append(item.get(MyDBManager.ATTRIBUT_NAME_PRODUCT)
						.toString()+"'");
				if (item.get(MyDBManager.ATTRIBUT_COMMENT_PRODUCT).isEmpty())
					sb.append("/>");				
				
				else {
					sb.append(" comment='"
							+ item.get(MyDBManager.ATTRIBUT_COMMENT_PRODUCT)
									.toString() + "'/>");
				}
				
			}
			
			sb.append("</data>");
			//TODO: возможно здесь уместно сформировать файл
			return sb.toString();

		}
	}
}
