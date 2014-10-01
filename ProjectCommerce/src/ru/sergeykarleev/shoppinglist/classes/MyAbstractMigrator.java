package ru.sergeykarleev.shoppinglist.classes;

import java.net.URI;

import ru.sergeykarleev.shoppinglist.fragments.MyFragmentBackend;


public abstract class MyAbstractMigrator {
	// Список режимов для import mode
	public final int MODE_ACTION_ADD = 1;
	public final int MODE_ACTION_UPDATE = 2;

	// Ссылки на базу данных и фрагмент Backend
	protected static MyDBManager mDataBase;
	protected static MyFragmentBackend mFragmentBackend;

	/**
	 * Метож импорта данных
	 * 
	 * @param mode
	 *            добавить данные или заменить всё? Используем соответствующие
	 *            константы класса
	 * @param webLoad передаём адрес, если хотим загрузить из Интернета
	 * @return результат импорта: успех или неудача
	 */
	public boolean ImportData(int mode, URI webLoad) {
		return false;
	}

	public boolean ImportData(int mode) {
		return (ImportData(mode, null));
	}

	public boolean ExportData() {
		return false;
	}

}
