package ru.sergeykarleev.shoppinglist.activities;

import ru.sergeykarleev.shoppinglist.R;
import ru.sergeykarleev.shoppinglist.dialogues.MyFragmentDialogTemplates;
import ru.sergeykarleev.shoppinglist.fragments.MyFragmentStorefront;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

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

		// �������� ����������� ����������
		fragmentF = new MyFragmentStorefront();

		// ���� ������ ������, ��������� �������� StoreFront �� ��������
		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.string.fragment_universal, fragmentF).commit();
		}
	}

}
