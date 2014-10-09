package ru.sergeykarleev.shoppinglist.activities;

import ru.sergeykarleev.shoppinglist.R;
import ru.sergeykarleev.shoppinglist.fragments.MyFragmentBackend;
import ru.sergeykarleev.shoppinglist.fragments.MyFragmentStorefront;
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
				//TODO: здесь взываем диалог загрузки сохранЄнного шаблона
				break;
			case R.id.btnTransfer:
				Toast.makeText(this, "btnTransfer", Toast.LENGTH_SHORT).show();
				//TODO: здесь вызываем диалог экспорта
				break;
			case R.id.btnPlan:
				Toast.makeText(this, "btnPlan", Toast.LENGTH_SHORT).show();
				//TODO: здесь вызываем диалог фрагмента плана магазина
				break;
			case R.id.btnProductBase:				
				getSupportFragmentManager().beginTransaction().replace(R.string.fragment_universal, fragmentB).commit();
				break;
			case R.id.btnInMarket:
				//TODO: необходимо упаковать все отмеченные элементы в объект и передать его в Storefront, затем сн€ть все выделени€
				//TODO: необходимо реализовать кнопку "назад", открыва€ Storefront
				getSupportFragmentManager().beginTransaction().replace(R.string.fragment_universal, fragmentF).commit();
				break;
			default:
				break;
			}
			
	}
}
