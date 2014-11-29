package ru.sergeykarleev.shoppinglist.activities;

import ru.sergeykarleev.shoppinglist.R;
import ru.sergeykarleev.shoppinglist.dialogues.MyFragmentDialogTemplates;
import ru.sergeykarleev.shoppinglist.fragments.MyFragmentStorefront;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

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
		quitDialog.setTitle("Выход: Вы уверены?");

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
}
