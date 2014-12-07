package ru.sergeykarleev.shoppinglist.activities;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import ru.sergeykarleev.shoppinglist.R;
import ru.sergeykarleev.shoppinglist.classes.MyIntentGetter;
import ru.sergeykarleev.shoppinglist.dialogues.MyFragmentDialogTemplates;
import ru.sergeykarleev.shoppinglist.fragments.MyFragmentStorefront;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

public class MainActivity extends FragmentActivity {

	final String LOG_TAG = "myLogs";
	private final static String ARRAY_LIST = "listProducts";

	FragmentTransaction fTrans;
	MyFragmentStorefront fragmentF;
	// MyFragmentDialogTemplates dialogTemplate;
	

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
		AlertDialog.Builder helpDialog = new Builder(
				this,
				android.R.style.Theme_DeviceDefault_Light_NoActionBar_Fullscreen);
		helpDialog.setPositiveButton("Ok", null);
		helpDialog.setNeutralButton("��������� ��\nGoogle.Play",
				new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						Intent browserIntent = new Intent(
								Intent.ACTION_VIEW,
								Uri.parse("https://play.google.com/store/apps/details?id=ru.sergeykarleev.shoppinglist"));
						startActivity(browserIntent);
						dialog.cancel();
					}
				});

		ImageView iView = new ImageView(this);
		iView.setImageResource(R.drawable.help_screen);
		iView.setPadding(2, 2, 2, 2);
		iView.setScaleType(ScaleType.FIT_XY);

		helpDialog.setView(iView);
		helpDialog.create().show();

	}
}
