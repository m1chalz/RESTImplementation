package com.example.rest.app.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.rest.app.config.Config;

import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper implements Config {

	private List<Processor> tables;

	public DatabaseHelper(Context context, List<Processor> tables) {
		super(context, DB_NAME, null, DB_VERSION);
		this.tables = tables;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

		db.beginTransaction();
		try {
			for (Processor tb : tables) {
				tb.createTable(db);
			}
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.beginTransaction();
		try {
			for (Processor tb : tables) {
				tb.upgradeTable(db, oldVersion, newVersion);
			}
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
	}

	@Override
	public void onOpen(SQLiteDatabase db) {
		super.onOpen(db);
		if (!db.isReadOnly()) {
			// Enable foreign key constraints
			db.execSQL("PRAGMA foreign_keys=ON;");
		}
	}
}
