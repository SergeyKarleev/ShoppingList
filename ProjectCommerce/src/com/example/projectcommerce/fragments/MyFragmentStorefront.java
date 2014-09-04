package com.example.projectcommerce.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.projectcommerce.R;
import com.example.projectcommerce.classes.MyDBManager;
import com.example.projectcommerce.classes.MyPagerAdapter;


public class MyFragmentStorefront extends Fragment {

	private final String LOG_TAG = "myLogs";
	private ViewPager mPager;
	private PagerAdapter pagerAdapter;
	private MyDBManager mDataBase;
	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {		
		View v = inflater.inflate(R.layout.fr_storefront, null);		
		
		// создаем подключение к БД посредством активити
		mDataBase = new MyDBManager(getActivity());		
		
		mPager = (ViewPager) v.findViewById(R.id.pager);
		pagerAdapter = new MyPagerAdapter(getActivity(),mDataBase);		
		mPager.setAdapter(pagerAdapter);		
		
		return v;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		//закрываем подключение к базе при удалении фрагмента StoreFront
		mDataBase.close();
	}
	
}
