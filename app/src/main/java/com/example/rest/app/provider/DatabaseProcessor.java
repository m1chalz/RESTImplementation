package com.example.rest.app.provider;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public abstract class DatabaseProcessor extends Processor {
	protected DatabaseProcessor(Context context, int code, String name) {
		super(context, code, name);
	}

	public DatabaseProcessor(Context context, int code) {
		super(context, code);
	}

	@Override
	public int delete(SQLiteDatabase db, Uri uri, String selection, String[] selectionArgs) {
		return db.delete(name, selection, selectionArgs);
	}


	public int update(SQLiteDatabase db, Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		return db.update(
				name,           // The database table name.
				values,         // A map of column names and new values to use.
				selection,      // The where clause column names.
				selectionArgs   // The where clause column values to select on.
		);
	}
}
