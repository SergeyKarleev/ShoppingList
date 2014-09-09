package com.example.projectcommerce.classes;

import java.security.spec.MGF1ParameterSpec;

import com.example.projectcommerce.R;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
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

	private long mId;
	private String mName;
	private float mPrice;
	private int mCount;

	private final int BUY_OPERATION_START = 1;
	private final int BUY_OPERATION_FINISH_TRUE = 2;
	private final int BUY_OPERATION_FINISH_FALSE = 0;

	private Handler h;

	TextView tvModelPager;
	TextView tvPricePager;
	TextView tvCountPager;
	Button btnBuyPager;

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

		tvModelPager = (TextView) v.findViewById(R.id.tvModelPager);
		tvPricePager = (TextView) v.findViewById(R.id.tvPricePager);
		tvCountPager = (TextView) v.findViewById(R.id.tvCountPager);
		btnBuyPager = (Button) v.findViewById(R.id.btnBuyPager);

		mSetVariables(position);

		// ��������� Handler, ���������� � ����� ������ ���������
		// ������� ������� ��������� - ��������� ������� ��� ��������� �����
		// ������
		// TODO: ������� �������� - ���������� ��������� (���� ��?)

		h = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case BUY_OPERATION_START:
					btnBuyPager.setEnabled(false);
					break;
				case BUY_OPERATION_FINISH_TRUE:
					btnBuyPager.setEnabled(true);
					notifyDataSetChanged();
					Toast.makeText(activity, "���������� ������ �������",
							Toast.LENGTH_SHORT).show();
					break;
				case BUY_OPERATION_FINISH_FALSE:
					Toast.makeText(activity,
							"���������� ������ ����������� ��������",
							Toast.LENGTH_SHORT).show();
					break;
				}
			}

		};

		tvModelPager.setText(mName);
		tvPricePager.setText(mPrice + " ������");
		tvCountPager.setText(mCount + " ����");

		// ��������� ��� View �� ViewPager
		((ViewPager) container).addView(v);

		btnBuyPager.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mCount > 0) {
					// ���������� � ���������� �������� ����� ������� ��������
					mSetVariables(((ViewPager) container).getCurrentItem());

					// ������� ����� ��� ���������� ������� � ��������� ������ �
					// ��
					Thread thread = new Thread(new Runnable() {

						@Override
						public void run() {

							// �������� ��������� Handler � ������ ����������
							// (������������ ������ "������")
							h.sendEmptyMessage(BUY_OPERATION_START);

							// � ���� �������� ���������� ���������� ������,
							// �������� ����� ���������� � ������ ���������
							// "�����", ����� ��������� "�������"

							try {
								mDataBase.editRecord(mId, mName, mPrice,
										mCount - 1);
								mData = mDataBase.getDataNonEmpty();
								mDataCount = mData.getCount();
								h.sendEmptyMessage(BUY_OPERATION_FINISH_TRUE);
							} catch (Exception e) {
								h.sendEmptyMessage(BUY_OPERATION_FINISH_FALSE);
							}

						}
					});
					thread.start();
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
