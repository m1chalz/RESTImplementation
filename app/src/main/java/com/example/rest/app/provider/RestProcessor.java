package com.example.rest.app.provider;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.os.Bundle;
import com.example.rest.app.authentication.AuthenticatorService;
import com.example.rest.app.config.Config;

import java.util.Calendar;

public abstract class RestProcessor extends DatabaseProcessor implements Config {
	protected static final String INTERMEDIATE_SYNC_MODE = "intermediate";
	protected static final String FAST_SYNC_MODE = "fast";
	protected static final long INFINITE = Long.MAX_VALUE;
	public static final String URL_KEY = "url";
	public static final String URI_KEY = "uri";
	public static final String REQUEST_KEY = "request";
	public static final String VALIDITY_KEY = "validity";
	public static final String METHOD_KEY = "method";
	public static final String GET_METHOD = "get";
	public static final String POST_METHOD = "post";

	protected RestProcessor(Context context, int code, String name) {
		super(context, code, name);
	}

	public RestProcessor(Context context, int code) {
		super(context, code);
	}

	public Cursor query(SQLiteDatabase db, Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder, Bundle connectionConfig, String syncMode, long validity) {
		purge(db);
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		qb.setTables(name);

		/*
		 * Performs the query. If no problems occur trying to read the database, then a Cursor
                 * object is returned; otherwise, the cursor variable contains null. If no records were
                 * selected, then the Cursor object is empty, and Cursor.getCount() returns 0.
                 */
		Cursor c = qb.query(
				db,            // The database to query
				projection,    // The columns to return from the query
				selection,     // The columns for the where clause
				selectionArgs, // The values for the where clause
				null,          // don't group the rows
				null,          // don't filter by row groups
				sortOrder      // The sort order
		);

		if (c == null || c.getCount() == 0) {
			if (syncMode.contentEquals(INTERMEDIATE_SYNC_MODE)) setIntermidiateSyncBundle(connectionConfig);
			else setFastSyncBundle(connectionConfig);
			connectionConfig.putLong(VALIDITY_KEY, validity);
			connectionConfig.putString(URI_KEY, uri.toString());
			connectionConfig.putString(METHOD_KEY, GET_METHOD);

			ContentResolver.requestSync(
					AuthenticatorService.GetAccount(ACCOUNT_TYPE), // Sync account
					AppContract.AUTHORITY,                         // Content authority
					connectionConfig                               // Extras
			);
		}

		return c;
	}

	protected void purge(SQLiteDatabase db) {
		long now = Calendar.getInstance().getTimeInMillis();
		db.delete(name, AppContract.VALIDITY + "=?", new String[] { String.valueOf(now) });
	}

	/**
	 * Note that SYNC_EXTRAS_MANUAL will cause an immediate sync, without any optimization to
	 * preserve battery life. If you know new data is available (perhaps via a GCM notification),
	 * but the user is not actively waiting for that data, you should omit this flag; this will give
	 * the OS additional freedom in scheduling your sync request.
	 */
	protected void setIntermidiateSyncBundle(Bundle b) {
		b.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
		b.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
	}

	/**
	 * Sync request will be scheduled at the front of the sync request queue and without any delay
	 */
	protected void setFastSyncBundle(Bundle b) {
		b.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
	}
}
