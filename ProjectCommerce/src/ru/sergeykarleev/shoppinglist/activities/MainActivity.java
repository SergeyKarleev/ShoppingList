package ru.sergeykarleev.shoppinglist.activities;

import ru.sergeykarleev.shoppinglist.R;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

public class MainActivity extends FragmentActivity{

	final String LOG_TAG = "myLogs";


	FragmentTransaction fTrans;
	MyFragmentBackend fragmentB;
	MyFragmentStorefront fragmentF;	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

//		// создаем базу данных с начальными значени€ми и сразу закрываем		
//		MyDBManager mDataBase = new MyDBManager(this);	
//		mDataBase.close();		

		// создание экземпл€ров фрагментов		
		fragmentF = new MyFragmentStorefront();
		fragmentB = new MyFragmentBackend();

		// если первый запуск, добавл€ем фрагмент StoreFront на активити
		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.string.fragment_universal, fragmentF).commit();
		}
	}	
	
	
	public void onClickGlobal(View v) {
			switch (v.getId()) {
			case R.id.btnTemplate:					
				Toast.makeText(this, "btnTamplate", Toast.LENGTH_SHORT).show();
				break;
			case R.id.btnTransfer:
				Toast.makeText(this, "btnTransfer", Toast.LENGTH_SHORT).show();
				break;
			case R.id.btnPlan:
				Toast.makeText(this, "btnPlan", Toast.LENGTH_SHORT).show();
				break;
			case R.id.btnProductBase:
				Toast.makeText(this, "btnProductBase", Toast.LENGTH_SHORT).show();
				getSupportFragmentManager().beginTransaction().replace(R.string.fragment_universal, fragmentB).commit();
				break;
			default:
				break;
			}
			
	}
}
