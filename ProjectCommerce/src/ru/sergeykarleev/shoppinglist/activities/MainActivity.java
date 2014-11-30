package ru.sergeykarleev.shoppinglist.activities;

import java.net.URI;

import ru.sergeykarleev.shoppinglist.R;
import ru.sergeykarleev.shoppinglist.dialogues.MyFragmentDialogTemplates;
import ru.sergeykarleev.shoppinglist.fragments.MyFragmentStorefront;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

public class MainActivity extends FragmentActivity {

	final String LOG_TAG = "myLogs";
	private final static String ARRAY_LIST = "listProducts";

	FragmentTransaction fTrans;
	MyFragmentStorefront fragmentF;

	MyFragmentDialogTemplates dialogTemplate;

	// private ArrayList<HashMap<String, String>> listProducts;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		getActionBar().setDisplayShowTitleEnabled(false);

		// создание экземпляров фрагментов
		fragmentF = new MyFragmentStorefront();

		// если первый запуск, добавляем фрагмент StoreFront на активити
		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.string.fragment_universal, fragmentF).commit();
		}
	}

	@Override
	public void onBackPressed() {
		openQuitDialog();		
	}

	public void openQuitDialog() {
		AlertDialog.Builder quitDialog = new AlertDialog.Builder(this);
		quitDialog.setTitle(R.string.dialogQuitTitle);

		quitDialog.setPositiveButton("Да", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		quitDialog.setNegativeButton("Нет", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
			}
		});

		quitDialog.create().show();
	}

	public void openHelpDialog() {
		AlertDialog.Builder helpDialog = new Builder(this,android.R.style.Theme_DeviceDefault_Light_NoActionBar_Fullscreen);		
		helpDialog.setPositiveButton("Ok", null);
		ImageView iView = new ImageView(this);
		iView.setImageResource(R.drawable.help_screen);
		iView.setPadding(2, 2, 2, 2);
		iView.setScaleType(ScaleType.FIT_XY);
		
		
		helpDialog.setView(iView);		
		helpDialog.create().show();	
		
	}
}
