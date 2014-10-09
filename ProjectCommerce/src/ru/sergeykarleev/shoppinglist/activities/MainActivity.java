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

//		// ������� ���� ������ � ���������� ���������� � ����� ���������		
//		MyDBManager mDataBase = new MyDBManager(this);	
//		mDataBase.close();		

		// �������� ����������� ����������		
		fragmentF = new MyFragmentStorefront();
		fragmentB = new MyFragmentBackend();

		// ���� ������ ������, ��������� �������� StoreFront �� ��������
		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.string.fragment_universal, fragmentF).commit();
		}
	}	
	
	
	public void onClickGlobal(View v) {
			switch (v.getId()) {
			case R.id.btnTemplate:					
				Toast.makeText(this, "btnTamplate", Toast.LENGTH_SHORT).show();
				//TODO: ����� ������� ������ �������� ����������� �������
				break;
			case R.id.btnTransfer:
				Toast.makeText(this, "btnTransfer", Toast.LENGTH_SHORT).show();
				//TODO: ����� �������� ������ ��������
				break;
			case R.id.btnPlan:
				Toast.makeText(this, "btnPlan", Toast.LENGTH_SHORT).show();
				//TODO: ����� �������� ������ ��������� ����� ��������
				break;
			case R.id.btnProductBase:				
				getSupportFragmentManager().beginTransaction().replace(R.string.fragment_universal, fragmentB).commit();
				break;
			case R.id.btnInMarket:
				//TODO: ���������� ��������� ��� ���������� �������� � ������ � �������� ��� � Storefront, ����� ����� ��� ���������
				//TODO: ���������� ����������� ������ "�����", �������� Storefront
				getSupportFragmentManager().beginTransaction().replace(R.string.fragment_universal, fragmentF).commit();
				break;
			default:
				break;
			}
			
	}
}
