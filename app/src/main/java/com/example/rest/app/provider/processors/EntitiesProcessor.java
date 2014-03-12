package com.example.rest.app.provider.processors;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import com.example.rest.app.provider.AppContract.Entities;
import com.example.rest.app.provider.RestProcessor;
import com.example.rest.app.utils.L;

import java.util.Calendar;

public class EntitiesProcessor extends RestProcessor {

	public EntitiesProcessor(Context context, int code) {
		super(context, code, Entities.TABLE_NAME);
	}

	@Override
	public void createTable(SQLiteDatabase db) {
		StringBuilder sql = new StringBuilder();
		sql.append("CREATE TABLE " + name + " (")
				.append(Entities._ID + " INTEGER PRIMARY KEY, ")
				.append(Entities._STATE + " INTEGER, ")
				.append(Entities._VALIDITY + " NUMERIC, ")
				.append(Entities._DATA + " TEXT, ")
				.append(Entities.ENTRY_ID + " TEXT, ")
				.append(Entities.TITLE + " TEXT, ")
				.append(Entities.LINK + " TEXT, ")
				.append(Entities.PUBLISHED + " INTEGER)");
		db.execSQL(sql.toString());
	}

	@Override
	public void upgradeTable(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

	@Override
	public Cursor query(SQLiteDatabase db, Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

		Bundle config = new Bundle();
		config.putString(URL_KEY, "http://android-developers.blogspot.com/atom.xml");

		Cursor c = query(
				db,
				uri,
				projection,
				selection,
				selectionArgs,
				sortOrder,
				config,
				INTERMEDIATE_SYNC_MODE,
				Calendar.getInstance().getTimeInMillis() + 3_600_000
		);

		// Tells the Cursor what URI to watch, so it knows when its source data changes
		c.setNotificationUri(getContext().getContentResolver(), Entities.CONTENT_URI);
		return c;
	}

	@Override
	public Uri insert(SQLiteDatabase db, Uri uri, ContentValues values) {
		L.d(values.toString());
		long id = db.insertOrThrow(name, null, values);
		return Uri.parse(Entities.CONTENT_URI + "/" + id);
	}
}
