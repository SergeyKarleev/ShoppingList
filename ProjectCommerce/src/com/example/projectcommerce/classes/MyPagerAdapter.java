package com.example.projectcommerce.classes;

import java.security.spec.MGF1ParameterSpec;

import com.example.projectcommerce.R;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
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

	public Object instantiateItem(final ViewGroup container, final int position) {

		LayoutInflater inflater = LayoutInflater.from(activity);
		View v = inflater.inflate(R.layout.fr_pager, null);

		// ��������� �������� �� ������ � ��, ����� ������� ��������� � ��������
		// �������� �� ViewPager
		mData.moveToPosition(position);
		final String mName = mData.getString(mData
				.getColumnIndex(MyDBManager.PRODUCTS_NAME));
		final float mPrice = mData.getFloat(mData
				.getColumnIndex(MyDBManager.PRODUCTS_PRICE));
		final int mCount = mData.getInt(mData
				.getColumnIndex(MyDBManager.PRODUCTS_COUNT));

		TextView tvModelPager = (TextView) v.findViewById(R.id.tvModelPager);
		TextView tvPricePager = (TextView) v.findViewById(R.id.tvPricePager);
		TextView tvCountPager = (TextView) v.findViewById(R.id.tvCountPager);
		Button btnBuyPager = (Button) v.findViewById(R.id.btnBuyPager);
		btnBuyPager.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mCount > 0) {					
					MyThread myThread = new MyThread(v, position);
					myThread.execute();

				} else {
					Toast.makeText(activity, "�� ������ ��� ������ ������",
							Toast.LENGTH_SHORT).show();
				}
			}
		});

		// ���������� � �������� �������� �� ��
		tvModelPager.setText(mName);
		tvPricePager.setText(mPrice + " ������");		
		tvCountPager.setText(mCount + setSign(mCount));

		// ��������� ��� View �� ViewPager
		((ViewPager) container).addView(v);
		return v;
	}

	/** ���������� ����� "�����" � ���������� �����
	 * @param cnt ����������
	 * @return ����� � ���������� �����
	 */
	private String setSign(int cnt) {
		int rem = cnt%10;
		if (rem==1){
			return " �����";
		}else if ((rem==2)||(rem==3)||(rem==4)) {
			return " �����";
		}		
		return " ����";
	}

	// ���������� ������� ������ "������"
	OnClickListener onClickBuy = new OnClickListener() {
		@Override
		public void onClick(View v) {

		}

	};

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
		// ������ ��������� ������ ��� ������������� AsyncTask
		return POSITION_NONE;
	}

	// ��������������� �����, �������� ������ ��� ���������� �������� � �����
	// ������
	private class MyThread extends AsyncTask<Void, Void, Void> {

		private View bufferView;
		private int position;
		
		private long mId;
		private String mName;
		private float mPrice;
		private int mCount;
				
		public MyThread(View bufferView, int position) {
			super();
			this.bufferView = bufferView;
			this.position = position;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// ������������ ������ ������
			bufferView.setEnabled(false);
			
			//������������� �������� ���������� (���������� �������� ����������)
			mData.moveToPosition(position);
			mId = mData.getLong(mData.getColumnIndex(MyDBManager.PRODUCTS_ID));
			mName = mData.getString(mData
					.getColumnIndex(MyDBManager.PRODUCTS_NAME));
			mPrice = mData.getFloat(mData
					.getColumnIndex(MyDBManager.PRODUCTS_PRICE));
			mCount = mData.getInt(mData
					.getColumnIndex(MyDBManager.PRODUCTS_COUNT));
		}

		protected Void doInBackground(Void... params) {

			try {
				Thread.sleep(5000);
				mDataBase.editRecord(mId, mName, mPrice, mCount - 1);
				mData = mDataBase.getDataNonEmpty();
				mDataCount = mData.getCount();
				return null;
			} catch (Exception e) {// TODO:��� �������� ����� ������?

			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			bufferView.setEnabled(true);
			notifyDataSetChanged();
			Toast.makeText(activity, "������� ���������", Toast.LENGTH_SHORT)
					.show();
		}

	}

}
