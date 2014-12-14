package ru.sergeykarleev.shoppinglist.classes;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ru.sergeykarleev.shoppinglist.R;
import ru.sergeykarleev.shoppinglist.activities.MainActivity;
import ru.sergeykarleev.shoppinglist.fragments.MyFragmentStorefront;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.sax.StartElementListener;
import android.telephony.TelephonyManager;
import android.util.Log;

/**
 * Класс отвечает за отправку списка в текстовом виде и в виде вложения по всем
 * доступным для устройства каналам (SMS, email, bluetooth...)
 * 
 * @author SergeyKarleev
 * 
 */
public class MySendManager {

	private final static String LOG_TAG = "myLogs";
	private static MainActivity mActivity;
	private static MyConvertHelper mConverter;

	public final static int SEND_ACTION_TEXT = 0;
	public final static int SEND_ACTION_FILE = 1;
	int sendAction;

	public MySendManager(MainActivity mActivity,
			ArrayList<HashMap<String, String>> listProducts) {
		super();
		this.mActivity = mActivity;
		mConverter = new MyConvertHelper(listProducts);

		AlertDialog.Builder adb = new Builder(mActivity);
		adb.setTitle(mActivity.getResources().getString(
				R.string.dialogSendTitle));
		adb.setMessage(mActivity.getResources().getString(
				R.string.dialogSendMessage));
		adb.setPositiveButton(
				mActivity.getResources().getString(
						R.string.dialogSendSimpleText), new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						sendText();
						dialog.dismiss();
					}
				});
		adb.setNegativeButton(
				mActivity.getResources().getString(R.string.dialogSendInAPP),
				new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						sendFile();
						dialog.dismiss();
					}
				});
		adb.create().show();
	}

	private void sendText() {
		// StringBuilder sb = new StringBuilder(mConverter.convertToString());

		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_TEXT, mConverter.convertToString());

		mActivity.startActivity(intent);
	}

	private void sendFile() {
		String s = mConverter.convertToXML();

		// TODO: добавить в виде вложения файл для программы (сохранение в КЭШ)
		// Разбираем код:
		File f = null;

		try {
			f = File.createTempFile("listProduct", ".mlp",
					mActivity.getExternalCacheDir());
			BufferedWriter bw = new BufferedWriter(new FileWriter(f));
			bw.write(s);
			bw.close();

			Intent intent = new Intent(Intent.ACTION_SEND);
			intent.setType("application/*");
			intent.putExtra(Intent.EXTRA_SUBJECT, mActivity.getResources()
					.getString(R.string.mail_subject));
			intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(f));
			mActivity.startActivity(intent);

		} catch (IOException e) {
			e.printStackTrace();
			Log.d(LOG_TAG, e.toString());
		}

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
					sb.append("\n");
				else {
					sb.append(" ("
							+ item.get(MyDBManager.ATTRIBUT_COMMENT_PRODUCT)
									.toString() + ")\n");
				}
			}
			return sb.substring(0, sb.length() - 1).toString();
		}

		/** Функция конвертирует список продуктов из ArrayList в XMLString */
		private String convertToXML() {
			StringBuilder sb = new StringBuilder();
			sb.append("<data>");
			for (HashMap<String, String> item : listProducts) {
				
				sb.append("<product name='");
				sb.append(item.get(MyDBManager.ATTRIBUT_NAME_PRODUCT)
						.toString() + "'");
				//if (!item.get(MyDBManager.ATTRIBUT_COMMENT_PRODUCT).isEmpty())
				sb.append(" comment='"
							+ item.get(MyDBManager.ATTRIBUT_COMMENT_PRODUCT)
									.toString() + "'");
				if (item.get(MyDBManager.ATTRIBUT_CATEGORY_PRODUCT).isEmpty())
					sb.append("/>");
				else {
					sb.append(" category='"
							+ item.get(MyDBManager.ATTRIBUT_CATEGORY_PRODUCT)
									.toString() + "'/>");
				}

			}

			sb.append("</data>");
			return sb.toString();

		}
	}
}
