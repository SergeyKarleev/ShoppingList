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

		/*
		 * Data содержит выборку всех строк из БД, где Count>0;
		 * mData.moveToPosition(position) перемещает курсор на строку с
		 * позицией, совпадающей со значением номера текущей страницы во
		 * ViewPager
		 */

		// TODO:обернуть строки заполнения TextView в отдельный метод
		// TODO:настроить обработчик нажатия кнопки "Купить"

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
		tvPricePager.setText(mPrice + " рублей");
		tvCountPager.setText(mCount + " штук");

		// Добавляем наш View на ViewPager
		((ViewPager) container).addView(v);

		// TODO: Вынести кнопку в MyFragmentStorefront или реализовать обратный
		// вызов?
		btnBuyPager.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mCount > 0) {

					// записываем в переменные значения полей текущей страницы
					mSetVariables(((ViewPager) container).getCurrentItem());
					
					mDataBase.editRecord(mId, mName, mPrice, mCount - 1);
					mData = mDataBase.getDataNonEmpty();
					mDataCount = mData.getCount();
					Toast.makeText(activity, "Заказ успешно проведён",
							Toast.LENGTH_SHORT).show();
					notifyDataSetChanged();
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
		return POSITION_NONE;
	}
}
