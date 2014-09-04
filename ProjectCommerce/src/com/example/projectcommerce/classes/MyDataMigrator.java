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
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.example.projectcommerce.R;
import com.example.projectcommerce.fragments.MyFragmentBackend;

/**
 * ����� �������� ����������� �������� �������, ����������� ������ ��
 * �������/�������� ������
 */
public class MyDataMigrator {

	final String LOG_TAG = "myLogs";

	private static MyDBManager mDataBase;
	private static MyFragmentBackend mFragmentBackend;

	// ����� ������� - ��� ���������� ������� (1) ��� ������� ���������� (2)
	public final int MODE_ACTION_ADD = 1;
	public final int MODE_ACTION_UPDATE = 2;
	
	//���������� ��� ������ ��������� �����
	private final String TYPE_XML = ".xml";
	private final String TYPE_CSV = ".csv";

	// ���� � ��� ����� �������/��������
	private final String FILEPATH = "/ProjectCommerce/";
	private final String FILENAME = "db";

	// TODO: ����������� �������:
	// 1. ������ � �������/�������� �������������� ����� ���� ��������� Backend
	// 2. ������
	// ~ ����������� ����� ���������� � ������ ������ (� ���������������) ���������
	// ~ ����� ������� ���������� ���������� � ������������ � ����� ������
	// ~ ������ �������� ���������� XML (���. - csv, binary, json)
	// ~ ���� ������� ��������� �� SD ����� (���. - �������� ����� Web)
	// ~ ��� ������� ���� ����� ��������� ���� ��������� �����
	// 3. �������
	// ~ ������ �������� ���������� XML (���. - binary, json, csv)
	// ~ ������� �� SD ����� � ����������� ����������
	// 4. ��� ������ �/� ����������� � ������ ������ (������� "�������")
	// 5. ��� ������ �/� ���� boolean. True - �������� ����������, false - ���
	// ��� ����� ��� ���������� ListView �������� �� ��������� Backend
	// 6. � Backend ��������� ������ � �������/�������� ����������� ����� ����

	/**
	 * ����� ����������� ������������� ����������� ������ �� ���� ������,
	 * �������� � FragmentBackend
	 * 
	 * @param mDataBase
	 *            ������ �� ���� ������
	 * @param mActivity
	 *            ������ �� �������� ��� ������ ��������
	 * @param mFragmentBackend
	 *            ������ �� FragmentBackend ��� ���������� �������� ListView
	 */
	public MyDataMigrator(MyDBManager mDataBase,
			MyFragmentBackend mFragmentBackend) {
		super();
		this.mDataBase = mDataBase;
		this.mFragmentBackend = mFragmentBackend;
	}

	/**
	 * ������ XML
	 * 
	 * @param mode
	 *            ���������� ��� ����������
	 * @param webLoad
	 *            ����� xml ����� � ����
	 * @return true ���� ������ �������� ��� ������
	 */
	public boolean ImportXML(int mode, URI webLoad) {
		if (webLoad == null) {
			try {
				XmlPullParser xpp = null;
				try {
					// ������� ������ xml �� ������ ����� � ��������
					XmlPullParserFactory factory = XmlPullParserFactory
							.newInstance();
					xpp = factory.newPullParser();
					// InputStream isr =
					// mFragmentBackend.getResources().openRawResource(R.raw.data);
					File file = new File(
							Environment.getExternalStorageDirectory()
									+ FILEPATH + FILENAME + TYPE_XML);
					FileInputStream fis = new FileInputStream(file);
					xpp.setInput(new InputStreamReader(fis));

					if (mode == MODE_ACTION_UPDATE)
						mDataBase.delRecord();
					Log.d(LOG_TAG, "������ � �� ����� �������� �������");
				} catch (Exception e) {
					e.printStackTrace();
					Log.d(LOG_TAG, "������ ������� " + e.toString());
				}

				// ���� �� ������ ��� ����� ���������
				while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
					if ((xpp.getEventType() == XmlPullParser.START_TAG)
							&& (xpp.getName().equals("Product"))) {
						String _name = xpp.getAttributeValue(null, "name");
						Float _price = Float.valueOf(xpp.getAttributeValue(
								null, "price"));
						int _count = Integer.valueOf(xpp.getAttributeValue(
								null, "count"));
						mDataBase.addRecord(_name, _price, _count);
						Log.d(LOG_TAG, "������ �� XML ����� ������ �������");
					}
					xpp.next();
				}
				Log.d(LOG_TAG, "���������� true �� �������");
				return true;

			} catch (XmlPullParserException e) {
				e.printStackTrace();
				Log.d(LOG_TAG, "������ ������� " + e.toString());
			} catch (IOException e) {
				e.printStackTrace();
				Log.d(LOG_TAG, "����� ������� " + e.toString());
			} catch (Throwable t) {
				Log.d(LOG_TAG, "������ �������� ����� " + t.toString() + "\n"
						+ t.getStackTrace());
			}
		} else {
			// TODO: � ����������. ����� ����� ���������� ������� XML ����� Web
		}
		return false;
	}

	/**
	 * ���������� ������ ������� ��� ���������� ����� �� SD-�����
	 * 
	 * @param mode
	 *            ���������� ��� ����������
	 */
	public boolean ImportXML(int mode) {
		return ImportXML(mode, null);
	}

	/**
	 * ������� ���� � ���� ����� XML �� SD-�����
	 * 
	 * @return �������� ��� ��������� �������
	 */
	public boolean ExportXML() {
		Cursor mData = mDataBase.getData();

		// ���������, �������� �� ����� ������
		if (!Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			Log.d(LOG_TAG,
					"SD-����� �� ��������: "
							+ Environment.getExternalStorageState());
			return false;
		}
		if (mData != null) {
			// ������� ������������ ��� ������������ ���������� xml ������
			StringBuilder xmlString = new StringBuilder();
			// ���������� ��������� ���
			xmlString.append("<data>");
			// ������ ������� �� ������� � �� ���������� � ��������� ��� �
			// �����������
			mData.moveToFirst();
			do {
				xmlString.append("<Product name='");
				xmlString.append(mData.getString(mData
						.getColumnIndex(mDataBase.PRODUCTS_NAME)));
				xmlString.append("' price='");
				xmlString.append(String.valueOf(mData.getFloat(mData
						.getColumnIndex(mDataBase.PRODUCTS_PRICE))));
				xmlString.append("' count='");
				xmlString.append(String.valueOf(mData.getInt(mData
						.getColumnIndex(mDataBase.PRODUCTS_COUNT))));
				xmlString.append("'/>");
			} while (mData.moveToNext());
			// ���������� �������� ���
			xmlString.append("</data>");
			Log.d(LOG_TAG, "��� �� �������� " + xmlString.toString());
			return writeFile(xmlString.toString(),TYPE_XML);
		}
		return false;
	}

	/**
	 * � ������ ������ ���������� ���������������� ������ xml ������ � ���� ��
	 * �����
	 * 
	 * @param saveString
	 *            ������ � ���������� ���� ������
	 * @param type
	 * 			  ���������� ����� (������� �� ��������)
	 * @return ��������� ������: ������� ��� ���
	 */
	private boolean writeFile(String saveString, String type) {

		try {
			File sdFile = new File(Environment.getExternalStorageDirectory()
					+ FILEPATH, FILENAME + TYPE_XML);
			// �������� ����� ��� ������
			BufferedWriter bw = new BufferedWriter(new FileWriter(sdFile));
			// ����� ������
			bw.write(saveString);
			// ��������� �����
			bw.close();
			Toast.makeText(mFragmentBackend.getActivity(),
					"�������������� � " + sdFile.getAbsoluteFile().toString(),
					Toast.LENGTH_SHORT).show();
			return true;
		} catch (FileNotFoundException e) {
			Log.d(LOG_TAG, "FileNotFoundException: " + e.toString());
			e.printStackTrace();
		} catch (IOException e) {
			Toast.makeText(mFragmentBackend.getActivity(),
					"������ ������ �� SD-�����", Toast.LENGTH_SHORT).show();
			Log.d(LOG_TAG, "EXPORT_XML: IOException " + e.toString());
			e.printStackTrace();
		}

		return false;
	}
}
