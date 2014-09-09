package com.example.projectcommerce.classes;

import java.net.URI;

import com.example.projectcommerce.fragments.MyFragmentBackend;

public abstract class MyAbstractMigrator { 
	// —писок режимов дл€ import mode
	public final int MODE_ACTION_ADD = 1;
	public final int MODE_ACTION_UPDATE = 2;

	// —сылки на базу данных и фрагмент Backend
	protected static MyDBManager mDataBase;
	protected static MyFragmentBackend mFragmentBackend;

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
