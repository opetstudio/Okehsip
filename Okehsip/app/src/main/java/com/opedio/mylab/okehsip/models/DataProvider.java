package com.opedio.mylab.okehsip.models;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

public class DataProvider extends ContentProvider{
	String TAG = "DataProvider";
	public static final String CONTENT_AUTHORITY = "com.opedio.mylab.okehsip";
	private DatabaseHelper dbHelper;
		private static final UriMatcher uriMatcher;
		static {
			uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		}
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		int count = 0;
		//untuk query delete
		return count;
	}

	@Override
	public String getType(Uri uri) {
		return null;
	}	

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		long id = 0;
		//untuk insert data
		return null;
	}

	@Override
	public boolean onCreate() {
		dbHelper = new DatabaseHelper(getContext());
		return true;
	}
	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		//untuk query
		return null;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		//update data
		return 0;
	}







}
