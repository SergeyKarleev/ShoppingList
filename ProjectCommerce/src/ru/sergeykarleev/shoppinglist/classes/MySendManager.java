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
 * ����� �������� �� �������� ������ � ��������� ���� � � ���� �������� �� ����
 * ��������� ��� ���������� ������� (SMS, email, bluetooth...)
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
		adb.setTitle("��������");
		adb.setMessage("������ ��������");
		adb.setPositiveButton("�����", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				sendText();
				dialog.dismiss();
			}
		});
		adb.setNegativeButton("����", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				sendFile();
				dialog.dismiss();
			}
		});
		adb.create().show();

		// switch (sendAction) {
		// case SEND_ACTION_TEXT:
		// sendText();
		// break;
		// case SEND_ACTION_FILE:
		// sendFile();
		// break;
		// default:
		// break;
		// }

	}

	private void sendText() {
		// StringBuilder sb = new StringBuilder(mConverter.convertToString());

		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_TEXT, mConverter.convertToString());

		mActivity.startActivity(intent);
	}

	private void sendFile() {
		Log.d(LOG_TAG, "��������� XML ������");
		String s = mConverter.convertToXML();
		Log.d(LOG_TAG, s);

		// TODO: �������� � ���� �������� ���� ��� ��������� (���������� � ���)
		// ��������� ���:
		File f = null;

		try {
			f = File.createTempFile("listProduct", ".mlp",mActivity.getExternalCacheDir());
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

		/** ������� ������������ ������ ��������� �� ArrayList � String */
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

		/** ������� ������������ ������ ��������� �� ArrayList � String */
		private String convertToXML() {
			StringBuilder sb = new StringBuilder();
			sb.append("<data>");
			for (HashMap<String, String> item : listProducts) {
				sb.append("<product name='");
				sb.append(item.get(MyDBManager.ATTRIBUT_NAME_PRODUCT)
						.toString() + "'");
				if (item.get(MyDBManager.ATTRIBUT_COMMENT_PRODUCT).isEmpty())
					sb.append("/>");

				else {
					sb.append(" comment='"
							+ item.get(MyDBManager.ATTRIBUT_COMMENT_PRODUCT)
									.toString() + "'/>");
				}

			}

			sb.append("</data>");
			// TODO: �������� ����� ������� ������������ ����
			return sb.toString();

		}
	}
}
