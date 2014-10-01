package ru.sergeykarleev.shoppinglist.classes;

import java.security.spec.MGF1ParameterSpec;

import ru.sergeykarleev.shoppinglist.R;


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
		final String mCategory = mData.getString(mData
				.getColumnIndex(MyDBManager.PRODUCTS_CATEGORY));
		final int mLot = mData.getInt(mData
				.getColumnIndex(MyDBManager.PRODUCTS_LOT));
		final float mCount = mData.getFloat(mData
				.getColumnIndex(MyDBManager.PRODUCTS_COUNT));
		final String mUnit = mData.getString(mData
				.getColumnIndex(MyDBManager.PRODUCTS_UNIT));

		TextView tvModelPager = (TextView) v.findViewById(R.id.tvModelPager);
		TextView tvCategoryPager = (TextView) v.findViewById(R.id.tvModelCategory);
		TextView tvCountPager = (TextView) v.findViewById(R.id.tvCountPager);

		Button btnBuyPager = (Button) v.findViewById(R.id.btnBuyPager);
		btnBuyPager.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mLot > 0) {
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
		tvCategoryPager.setText(mCategory);
		tvCountPager.setText(mLot + " x "+mCount+" ("+mUnit+")");

		// ��������� ��� View �� ViewPager
		((ViewPager) container).addView(v);
		return v;
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
		// ������ ��������� ������ ��� ������������� AsyncTask
		return POSITION_NONE;
	}

	// ��������������� �����, �������� ������ ��� ���������� �������� � �����
	// ������
	private class MyThread extends AsyncTask<Void, Void, Void> {

		private View bufferView;
		private int position;
		private ContentValues cv;

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

			// ������������� �������� ���������� (���������� ��������
			// ����������)			
			mData.moveToPosition(position);
			cv = new ContentValues();
			cv.clear();
			
			cv.put(MyDBManager.PRODUCTS_ID,mData.getLong(mData.getColumnIndex(MyDBManager.PRODUCTS_ID)));			
			cv.put(MyDBManager.PRODUCTS_NAME, mData.getString(mData
					.getColumnIndex(MyDBManager.PRODUCTS_NAME)));
			cv.put(MyDBManager.PRODUCTS_CATEGORY, mData.getString(mData
					.getColumnIndex(MyDBManager.PRODUCTS_CATEGORY)));
			cv.put(MyDBManager.PRODUCTS_LOT, mData.getInt(mData
					.getColumnIndex(MyDBManager.PRODUCTS_LOT))-1);
			cv.put(MyDBManager.PRODUCTS_COUNT, mData.getFloat(mData
					.getColumnIndex(MyDBManager.PRODUCTS_COUNT)));
			cv.put(MyDBManager.PRODUCTS_UNIT, mData.getString(mData
					.getColumnIndex(MyDBManager.PRODUCTS_UNIT)));			
		
			
		}

		protected Void doInBackground(Void... params) {

			try {
				mDataBase.editRecord(cv);
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
