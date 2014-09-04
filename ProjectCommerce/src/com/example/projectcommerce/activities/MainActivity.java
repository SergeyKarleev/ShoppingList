package com.example.projectcommerce.activities;

import com.example.projectcommerce.R;
import com.example.projectcommerce.classes.MyDBManager;
import com.example.projectcommerce.fragments.MyFragmentBackend;
import com.example.projectcommerce.fragments.MyFragmentStorefront;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends FragmentActivity implements OnClickListener {

	final String LOG_TAG = "myLogs";

	Button btnStoreFront, btnBackEnd;

	FragmentTransaction fTrans;
	MyFragmentBackend fragmentB;
	MyFragmentStorefront fragmentF;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);		
		
		//создаем базу данных с начальными значениями и сразу закрываем
		MyDBManager mDataBase = new MyDBManager(this);
		mDataBase.close();

		btnStoreFront = (Button) findViewById(R.id.btnStoreFront);
		btnStoreFront.setOnClickListener(this);

		btnBackEnd = (Button) findViewById(R.id.btnBackEnd);
		btnBackEnd.setOnClickListener(this);

		// создание экземпляров фрагментов
		fragmentF = new MyFragmentStorefront();
		fragmentB = new MyFragmentBackend();

		// если первый запуск, добавляем фрагмент StoreFront на активити
		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.string.fragment_universal, fragmentF).commit();
		}

	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onDestroy() {
		Log.d(LOG_TAG, "MainActivity onDestroy");
		super.onDestroy();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View v) {
		fTrans = getSupportFragmentManager().beginTransaction();

		switch (v.getId()) {
		case R.id.btnStoreFront:

			// Если удается найти фрагмент в контейнере, то отображаем
			// предупреждение
			// иначе добавляем фрагмент StoreFront в контейнер
			try {
				MyFragmentStorefront fragment_f = (MyFragmentStorefront) getSupportFragmentManager()
						.findFragmentById(R.string.fragment_universal);
				Toast.makeText(this, "Front отображен", Toast.LENGTH_SHORT)
						.show();
			} catch (Exception e) {
				fTrans.replace(R.string.fragment_universal, fragmentF).commit();
			}

			break;

		case R.id.btnBackEnd:
			try {
				MyFragmentBackend fragment_b = (MyFragmentBackend) getSupportFragmentManager()
						.findFragmentById(R.string.fragment_universal);
				Toast.makeText(this, "Back отображен", Toast.LENGTH_SHORT)
						.show();
			} catch (Exception e) {
				fTrans.replace(R.string.fragment_universal, fragmentB).commit();
			}
			break;
		default:
			break;
		}
	}
}
