package com.example.projectcommerce.classes;

import java.security.spec.MGF1ParameterSpec;

import com.example.projectcommerce.R;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MyPagerAdapter extends PagerAdapter {

	private final String LOG_TAG = "myLogs";
	private Context activity;

	private int mDataCount;
	private Cursor mData;
	private MyDBManager mDataBase;

	private long mId;
	private String mName;
	private float mPrice;
	private int mCount;

	/**
	 * ����������� ��������
	 * 
	 * @param activity
	 *            �������� ��� ������ � ����� ������
	 * @param mDataBase
	 *            �������� � �������� �� ���������� � Storefront ���������, ����
	 *            ���������� ���� ������ ��� ������ � ���
	 */
	public MyPagerAdapter(Context activity, MyDBManager mDataBase) {
		super();
		this.activity = activity;

		this.mDataBase = mDataBase;
		mData = mDataBase.getDataNonEmpty();
		mDataCount = mData.getCount();
	}

	/*
	 * ����� ���������� View �������� ��� ViewPager
	 */

	public Object instantiateItem(final ViewGroup container, int position) {

		LayoutInflater inflater = LayoutInflater.from(activity);
		View v = inflater.inflate(R.layout.fr_pager, null);

		TextView tvModelPager = (TextView) v.findViewById(R.id.tvModelPager);
		TextView tvPricePager = (TextView) v.findViewById(R.id.tvPricePager);
		TextView tvCountPager = (TextView) v.findViewById(R.id.tvCountPager);
		Button btnBuyPager = (Button) v.findViewById(R.id.btnBuyPager);

		/*
		 * Data �������� ������� ���� ����� �� ��, ��� Count>0;
		 * mData.moveToPosition(position) ���������� ������ �� ������ �
		 * ��������, ����������� �� ��������� ������ ������� �������� ��
		 * ViewPager
		 */

		// TODO:�������� ������ ���������� TextView � ��������� �����
		// TODO:��������� ���������� ������� ������ "������"

		/*
		 * mData.moveToPosition(position);
		 * 
		 * mId = mData.getLong(mData.getColumnIndex(mDataBase.PRODUCTS_ID));
		 * mName = mData
		 * .getString(mData.getColumnIndex(MyDBManager.PRODUCTS_NAME)); mPrice =
		 * mData.getFloat(mData .getColumnIndex(MyDBManager.PRODUCTS_PRICE));
		 * mCount =
		 * mData.getInt(mData.getColumnIndex(MyDBManager.PRODUCTS_COUNT));
		 */

		mSetVariables(position);

//		Log.d(LOG_TAG, "ID = " + mId + ", Name = " + mName + ", Price = "
//				+ mPrice + ", Count = " + mCount + "; getCurrentItem "
//				+ ((ViewPager) container).getCurrentItem());

		tvModelPager.setText(mName);
		tvPricePager.setText(mPrice + " ������");
		tvCountPager.setText(mCount + " ����");

		// ��������� ��� View �� ViewPager
		((ViewPager) container).addView(v);

		// TODO: ������� ������ � MyFragmentStorefront ��� ����������� ��������
		// �����?
		btnBuyPager.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mCount > 0) {

					// ���������� � ���������� �������� ����� ������� ��������
					mSetVariables(((ViewPager) container).getCurrentItem());
					
					mDataBase.editRecord(mId, mName, mPrice, mCount - 1);
					mData = mDataBase.getDataNonEmpty();
					mDataCount = mData.getCount();
					Toast.makeText(activity, "����� ������� �������",
							Toast.LENGTH_SHORT).show();
					notifyDataSetChanged();
				} else {
					Toast.makeText(activity, "�� ������ ��� ������ ������",
							Toast.LENGTH_SHORT).show();
				}

			}
		});

		return v;
	}

	// ��������� ���������� ������ �� ������� �������
	private void mSetVariables(int position) {
		mData.moveToPosition(position);

		mId = mData.getLong(mData.getColumnIndex(mDataBase.PRODUCTS_ID));
		mName = mData
				.getString(mData.getColumnIndex(MyDBManager.PRODUCTS_NAME));
		mPrice = mData.getFloat(mData
				.getColumnIndex(MyDBManager.PRODUCTS_PRICE));
		mCount = mData.getInt(mData.getColumnIndex(MyDBManager.PRODUCTS_COUNT));
	}

	// ������� ���� �������� �� ViewPager
	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		// ������� View �� ViewPager
		((ViewPager) container).removeView((View) object);
	}

	// ��������������� �����. ��������� ����� �����
	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == (View) object;
	}

	@Override
	public int getCount() {
		// ���������� ����� � Count>0 � ����
		return mDataCount;
	}

	@Override
	public int getItemPosition(Object object) {
		return POSITION_NONE;
	}
}
