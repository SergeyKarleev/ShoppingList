package ru.sergeykarleev.shoppinglist.fragments;

import ru.sergeykarleev.shoppinglist.R;
import ru.sergeykarleev.shoppinglist.classes.MyDBManager;
import ru.sergeykarleev.shoppinglist.classes.MyPagerAdapter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MyFragmentStorefront extends Fragment {

	private final String LOG_TAG = "myLogs";
	private ViewPager mPager;
	private PagerAdapter pagerAdapter;
	private MyDBManager mDataBase;
	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {		
		View v = inflater.inflate(R.layout.fr_storefront, null);		
		
		// ������� ����������� � �� ����������� ��������
		mDataBase = new MyDBManager(getActivity());		
		
		mPager = (ViewPager) v.findViewById(R.id.pager);
		pagerAdapter = new MyPagerAdapter(getActivity(),mDataBase);		
		mPager.setAdapter(pagerAdapter);		
		
		return v;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		//��������� ����������� � ���� ��� �������� ��������� StoreFront
		mDataBase.close();
	}
	
}