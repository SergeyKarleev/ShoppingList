package com.example.projectcommerce.classes;

import java.net.URI;

import com.example.projectcommerce.fragments.MyFragmentBackend;


interface Import{	
	boolean ImportData(int mode, URI webLoad);	
}

interface Export{
	boolean ExportData();
}

public abstract class MyAbstractMigrator implements Import, Export{
	//—писок Import mode
	public final int MODE_ACTION_ADD = 1;
	public final int MODE_ACTION_UPDATE = 2;
	
	//—сылки на базу данных и фрагмент Backend
	protected static MyDBManager mDataBase;
	protected static MyFragmentBackend mFragmentBackend;
	
	@Override
	public boolean ImportData(int mode, URI webLoad) {
		// TODO Auto-generated method stub
		return false;
	}
	
	public boolean ImportData(int mode){
		return (ImportData(mode, null));
	}

	@Override
	public boolean ExportData() {
		return false;
	}
	
	
}
