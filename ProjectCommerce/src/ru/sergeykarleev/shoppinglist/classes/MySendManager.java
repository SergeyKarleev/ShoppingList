package ru.sergeykarleev.shoppinglist.classes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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

	MainActivity mActivity;

	/**
	 * @param sendMode
	 */
	public MySendManager(MainActivity mActivity, int sendMode,
			ArrayList<HashMap<String, String>> listProducts) {
		super();
		this.mActivity = mActivity;

		switch (sendMode) {
		case SEND_TO_SMS:

			sendToSMS(listProducts);
			break;

		default:
			break;
		}
	}

	private void sendToSMS(ArrayList<HashMap<String, String>> listProducts) {
		Intent smsIntent = new Intent(Intent.ACTION_SENDTO,Uri.parse("sms:"));
		
		StringBuilder sb = new StringBuilder();
		for (HashMap<String, String> item : listProducts) {
			sb.append(item.get(MyDBManager.ATTRIBUT_NAME_PRODUCT).toString());
			if (item.get(MyDBManager.ATTRIBUT_COMMENT_PRODUCT).isEmpty())
				sb.append(";\n");
			else {
				sb.append(": "
						+ item.get(MyDBManager.ATTRIBUT_COMMENT_PRODUCT)
								.toString() + ";\n");
			}
		}
		smsIntent.putExtra("sms_body",sb.toString());
		mActivity.startActivity(smsIntent);
	}

}
