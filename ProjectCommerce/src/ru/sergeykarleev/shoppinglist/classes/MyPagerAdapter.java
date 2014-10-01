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
					Toast.makeText(activity, "На складе нет данной модели",
							Toast.LENGTH_SHORT).show();
				}
			}
		});

		// Записываем в элементы значения из БД
		tvModelPager.setText(mName);
		tvCategoryPager.setText(mCategory);
		tvCountPager.setText(mLot + " x "+mCount+" ("+mUnit+")");

		// Добавляем наш View на ViewPager
		((ViewPager) container).addView(v);
		return v;
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
		// ошибка отработки метода при использовании AsyncTask
		return POSITION_NONE;
	}

	// Вспомогательный класс, создание потока для реализации действий с базой
	// данных
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
			// деактивируем кнопку Купить
			bufferView.setEnabled(false);

			// устанавливаем значения переменных (параметров текущего
			// устройства)			
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
