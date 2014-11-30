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

		// �������� ����������� ����������
		fragmentF = new MyFragmentStorefront();

		// ���� ������ ������, ��������� �������� StoreFront �� ��������
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

		quitDialog.setPositiveButton("��", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		quitDialog.setNegativeButton("���", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
			}
		});

		quitDialog.create().show();
	}

	public void openHelpDialog() {
		AlertDialog.Builder helpDialog = new Builder(this);		
		helpDialog.setPositiveButton("Ok", null);
		//View view = getLayoutInflater().inflate(R.layout.help_dialog, null);		
		
		WebView wView = new WebView(this);
		wView.setBackgroundColor(Color.BLACK);
		wView.getSettings().setSupportZoom(true);
		wView.getSettings().setBuiltInZoomControls(true);
		wView.setPadding(0, 0, 0, 0);
		wView.setScrollbarFadingEnabled(true);
		wView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);		
		wView.loadUrl("file:///android_res/drawable/help_screen.png");
		
		//wView.getSettings().setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);		
		wView.getSettings().setUseWideViewPort(true);
		//wView.getSettings().setLoadWithOverviewMode(true);
		
		helpDialog.setView(wView);		
		helpDialog.create().show();	
		
	}
}
