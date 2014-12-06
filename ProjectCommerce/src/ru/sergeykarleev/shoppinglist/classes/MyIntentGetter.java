package ru.sergeykarleev.shoppinglist.classes;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

public class MyIntentGetter {
	private final static String LOG_TAG = "myLogs";	
	Intent intent;
	
	public MyIntentGetter(Intent intent) {
		//super();
		this.intent = intent;
		Log.d(LOG_TAG, "MyIntentGetter");
	}

	public void getList() {
		Log.d(LOG_TAG, intent.getExtras().toString());
		String s = intent.getExtras().toString();
		Log.d(LOG_TAG, s);
		//return null;
	}

}
