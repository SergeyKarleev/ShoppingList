package ru.sergeykarleev.shoppinglist.classes;

import java.net.URI;

import ru.sergeykarleev.shoppinglist.fragments.MyFragmentBackend;


public abstract class MyAbstractMigrator {
	// ������ ������� ��� import mode
	public final int MODE_ACTION_ADD = 1;
	public final int MODE_ACTION_UPDATE = 2;

	// ������ �� ���� ������ � �������� Backend
	protected static MyDBManager mDataBase;
	protected static MyFragmentBackend mFragmentBackend;

	/**
	 * ����� ������� ������
	 * 
	 * @param mode
	 *            �������� ������ ��� �������� ��? ���������� ���������������
	 *            ��������� ������
	 * @param webLoad ������� �����, ���� ����� ��������� �� ���������
	 * @return ��������� �������: ����� ��� �������
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
