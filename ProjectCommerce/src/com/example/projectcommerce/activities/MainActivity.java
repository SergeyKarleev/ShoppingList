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

		// ������� ���� ������ � ���������� ���������� � ����� ���������
		MyDBManager mDataBase = new MyDBManager(this);
		mDataBase.close();

		btnStoreFront = (Button) findViewById(R.id.btnStoreFront);
		btnStoreFront.setOnClickListener(this);

		btnBackEnd = (Button) findViewById(R.id.btnBackEnd);
		btnBackEnd.setOnClickListener(this);

		// �������� ����������� ����������
		fragmentF = new MyFragmentStorefront();
		fragmentB = new MyFragmentBackend();

		// ���� ������ ������, ��������� �������� StoreFront �� ��������
		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.string.fragment_universal, fragmentF).commit();
		}

	}

	@Override
	public void onClick(View v) {
		fTrans = getSupportFragmentManager().beginTransaction();

		switch (v.getId()) {
		case R.id.btnStoreFront:

			// ���� ������� ����� �������� � ����������, �� ����������
			// ��������������
			// ����� ��������� �������� StoreFront � ���������
			try {
				MyFragmentStorefront fragment_f = (MyFragmentStorefront) getSupportFragmentManager()
						.findFragmentById(R.string.fragment_universal);
				Toast.makeText(this, "Front ���������", Toast.LENGTH_SHORT)
						.show();
			} catch (Exception e) {
				fTrans.replace(R.string.fragment_universal, fragmentF).commit();
			}

			break;

		case R.id.btnBackEnd:
			try {
				MyFragmentBackend fragment_b = (MyFragmentBackend) getSupportFragmentManager()
						.findFragmentById(R.string.fragment_universal);
				Toast.makeText(this, "Back ���������", Toast.LENGTH_SHORT)
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
