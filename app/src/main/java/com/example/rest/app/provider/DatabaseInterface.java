package com.example.rest.app.provider;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public interface DatabaseInterface {
	public void createTable(SQLiteDatabase db);
	public void upgradeTable(SQLiteDatabase db, int oldVersion, int newVersion);
	public Cursor query(SQLiteDatabase db, Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder);
	public Uri insert(SQLiteDatabase db, Uri uri, ContentValues values);
	public int delete(SQLiteDatabase db, Uri uri, String selection, String[] selectionArgs);
	public int update(SQLiteDatabase db, Uri uri, ContentValues values, String selection, String[] selectionArgs);
}
