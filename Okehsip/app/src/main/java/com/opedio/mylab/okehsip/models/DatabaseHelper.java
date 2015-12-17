package com.opedio.mylab.okehsip.models;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class DatabaseHelper extends SQLiteOpenHelper {
	private static final String TAG = DatabaseHelper.class.getSimpleName();
	static final String DATABASE = "okehsip.db";
	static final int VERSION = 1; //
	private Context context2 ;
	public static final String COL_ID = "_id";
	public DatabaseHelper(Context context) {
		super(context, DATABASE, null, VERSION);
		context2 = context;
	}
	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.i(TAG,"onCreate");
	}
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.i(TAG,"onUpgrade from "+oldVersion+" to "+newVersion);
	}
}
