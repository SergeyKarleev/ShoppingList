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

	public Object instantiateItem(final ViewGroup container, final int position) {

		LayoutInflater inflater = LayoutInflater.from(activity);
		View v = inflater.inflate(R.layout.fr_pager, null);

		// Извлекаем значения из строки в БД, номер которой совпадает с позицией
		// страницы во ViewPager
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
					Toast.makeText(activity, "На складе нет данной модели",
							Toast.LENGTH_SHORT).show();
				}
			}
		});

		// Записываем в элементы значения из БД
		tvModelPager.setText(mName);
		tvPricePager.setText(mPrice + " рублей");		
		tvCountPager.setText(mCount + setSign(mCount));

		// Добавляем наш View на ViewPager
		((ViewPager) container).addView(v);
		return v;
	}

	/** Определяет слово "штука" в правильную форму
	 * @param cnt количество
	 * @return слово в правильной форме
	 */
	private String setSign(int cnt) {
		int rem = cnt%10;
		if (rem==1){
			return " штука";
		}else if ((rem==2)||(rem==3)||(rem==4)) {
			return " штуки";
		}		
		return " штук";
	}

	// Обработчик нажатия кнопки "Купить"
	OnClickListener onClickBuy = new OnClickListener() {
		@Override
		public void onClick(View v) {

		}

	};

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
		// ошибка отработки метода при использовании AsyncTask
		return POSITION_NONE;
	}

	// Вспомогательный класс, создание потока для реализации действий с базой
	// данных
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
			// деактивируем кнопку Купить
			bufferView.setEnabled(false);
			
			//устанавливаем значения переменных (параметров текущего устройства)
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
			} catch (Exception e) {// TODO:как отловить здесь ошибку?

			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			bufferView.setEnabled(true);
			notifyDataSetChanged();
			Toast.makeText(activity, "Покупка совершена", Toast.LENGTH_SHORT)
					.show();
		}

	}

}
