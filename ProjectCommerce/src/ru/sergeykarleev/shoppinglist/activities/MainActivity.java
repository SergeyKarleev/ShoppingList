package ru.sergeykarleev.shoppinglist.activities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import ru.sergeykarleev.shoppinglist.R;
import ru.sergeykarleev.shoppinglist.classes.MyDBManager;
import ru.sergeykarleev.shoppinglist.dialogues.MyFragmentDialogTemplates;
import ru.sergeykarleev.shoppinglist.dialogues.MyFragmentDialogTransfer;
import ru.sergeykarleev.shoppinglist.fragments.MyFragmentBackend;
import ru.sergeykarleev.shoppinglist.fragments.MyFragmentStorefront;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class MainActivity extends FragmentActivity{

	final String LOG_TAG = "myLogs";
	private final static String ARRAY_LIST = "listProducts";

	FragmentTransaction fTrans;
	MyFragmentBackend fragmentB;
	MyFragmentStorefront fragmentF;	
	
	MyFragmentDialogTemplates dialogTemplate;
	
	private ArrayList<HashMap<String, String>> listProducts;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// создание экземпл€ров фрагментов		
		fragmentF = new MyFragmentStorefront();
		fragmentB = new MyFragmentBackend();
		
		if (savedInstanceState == null){
			listProducts = new ArrayList<HashMap<String,String>>();
		}else{
			listProducts = (ArrayList<HashMap<String, String>>) savedInstanceState.getSerializable(ARRAY_LIST);
		}
			
		
		// если первый запуск, добавл€ем фрагмент StoreFront на активити
		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.string.fragment_universal, fragmentF).commit();
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable(ARRAY_LIST, listProducts);
	}


	public void onClickGlobal(View v) {
			switch (v.getId()) {
			case R.id.btnTemplate:				
				MyDBManager mDB = new MyDBManager(this);
				mDB.clearTemplates();
				mDB.close();
				break;
			case R.id.btnTransfer:
				Toast.makeText(this, "btnTransfer", Toast.LENGTH_SHORT).show();
				//TODO: здесь вызываем диалог передачи списка
				MyFragmentDialogTransfer dialogTransfer = new MyFragmentDialogTransfer();
				dialogTransfer.show(getSupportFragmentManager(), null);				
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
				//getSupportFragmentManager().beginTransaction().replace(R.string.fragment_universal, fragmentF).commit();
				break;
			default:
				break;
			}
			
	}
	
	public ArrayList<HashMap<String, String>> getListProducts() {
		return listProducts;
	}

	public void setListProducts(ArrayList<HashMap<String, String>> listProducts) {
		this.listProducts=listProducts;
	}
	
	public void goToProductList(ArrayList<HashMap<String, String>> data){
		ArrayList<HashMap<String, String>> mList = getListProducts();
		mList.addAll(data);
		setListProducts(mList);
		getSupportFragmentManager().beginTransaction().replace(R.string.fragment_universal, fragmentF).commit();
	}
}
