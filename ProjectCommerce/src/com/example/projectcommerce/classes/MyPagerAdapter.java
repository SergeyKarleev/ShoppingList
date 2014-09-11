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

	private long mId;
	private String mName;
	private float mPrice;
	private int mCount;

	private final int BUY_OPERATION_START = 1;
	private final int BUY_OPERATION_FINISH_TRUE = 2;
	private final int BUY_OPERATION_FINISH_FALSE = 0;

	View bufferView;

	/**
	 * Конструктор адаптера
	 * 
	 * @param activity
	 *            передать для работы с базой данных
	 * @param mDataBase
	 *            открытие и закрытие БД происходит в Storefront фрагменте, сюда
	 *            передается лишь ссылка для работы с ней
	 */
	public MyPagerAdapter(Context activity, MyDBManager mDataBase) {
		super();
		this.activity = activity;

		this.mDataBase = mDataBase;
		mData = mDataBase.getDataNonEmpty();
		mDataCount = mData.getCount();
	}

	/*
	 * Метод возвращает View страницы для ViewPager
	 */

	public Object instantiateItem(final ViewGroup container, int position) {

		LayoutInflater inflater = LayoutInflater.from(activity);
		View v = inflater.inflate(R.layout.fr_pager, null);

		TextView tvModelPager = (TextView) v.findViewById(R.id.tvModelPager);
		TextView tvPricePager = (TextView) v.findViewById(R.id.tvPricePager);
		TextView tvCountPager = (TextView) v.findViewById(R.id.tvCountPager);
		Button btnBuyPager = (Button) v.findViewById(R.id.btnBuyPager);

		mSetVariables(position);

		tvModelPager.setText(mName);
		tvPricePager.setText(mPrice + " рублей");
		tvCountPager.setText(mCount + " штук");

		// Добавляем наш View на ViewPager
		((ViewPager) container).addView(v);

		btnBuyPager.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mCount > 0) {
					
					// записываем в переменные значения полей текущей страницы
					mSetVariables(((ViewPager) container).getCurrentItem());
					
					bufferView = v;
					 MyThread myThread = new MyThread();
					 myThread.execute();

				} else {
					Toast.makeText(activity, "На складе нет данной модели",
							Toast.LENGTH_SHORT).show();
				}

			}
		});

		return v;
	}

	// Установка переменных исходя из позиции курсора
	private void mSetVariables(int position) {
		mData.moveToPosition(position);

		mId = mData.getLong(mData.getColumnIndex(mDataBase.PRODUCTS_ID));
		mName = mData
				.getString(mData.getColumnIndex(MyDBManager.PRODUCTS_NAME));
		mPrice = mData.getFloat(mData
				.getColumnIndex(MyDBManager.PRODUCTS_PRICE));
		mCount = mData.getInt(mData.getColumnIndex(MyDBManager.PRODUCTS_COUNT));
	}

	// Удаляем нашу страницу из ViewPager
	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		// Удаляем View из ViewPager
		((ViewPager) container).removeView((View) object);
	}

	// Вспомогательный метод. Непонятно зачем нужен
	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == (View) object;
	}

	@Override
	public int getCount() {
		// количество строк с Count>0 в базе
		return mDataCount;
	}

	@Override
	public int getItemPosition(Object object) {
		//ошибка отработки метода при использовании AsyncTask
		return POSITION_NONE;
	}

	// Вспомогательный класс для реализации действий с базой данных
	private class MyThread extends AsyncTask<Void, Void, Void> {
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// деактивируем кнопку Купить
			bufferView.setEnabled(false);		
			
		}
		
		protected Void doInBackground(Void...params) {
			
			try {
				Thread.sleep(5000);
				mDataBase.editRecord(mId, mName, mPrice, mCount - 1);
				mData = mDataBase.getDataNonEmpty();
				mDataCount = mData.getCount();
				return null;
			} catch (Exception e) {// TODO:как отловить здесь ошибку?

			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			bufferView.setEnabled(true);	
			notifyDataSetChanged();
			Toast.makeText(activity, "Покупка совершена", Toast.LENGTH_SHORT).show();
		}

	}

}
