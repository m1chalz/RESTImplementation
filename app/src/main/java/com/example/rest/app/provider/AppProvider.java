package com.example.rest.app.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import com.example.rest.app.provider.AppContract.Entities;
import com.example.rest.app.provider.processors.EntitiesProcessor;

import java.util.LinkedList;
import java.util.List;

public class AppProvider extends ContentProvider {

	// Constants used by the Uri matcher to choose an action based on the pattern of the incoming URI
	public static final int ENTITIES = 1;

	private static List<Processor> processors;

	/**
	 * A UriMatcher instance
	 */
	private static final UriMatcher uriMatcher;

	// Handle to a new DatabaseHelper.
	private DatabaseHelper helper;

	static {
		// Creates and initializes the URI matcher
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		uriMatcher.addURI(AppContract.AUTHORITY, Entities.TABLE_NAME, ENTITIES);
	}

	@Override
	public boolean onCreate() {
		processors = new LinkedList<>();

		processors.add(new EntitiesProcessor(getContext(), ENTITIES));

		// Creates a new helper object. Note that the database itself isn't opened until
		// something tries to access it, and it's only created if it doesn't already exist.
		helper = new DatabaseHelper(getContext(), processors);

		return true;
	}

	private Processor findProcessor(Uri uri) {
		int code = uriMatcher.match(uri);
		for (Processor processor : processors) {
			if (processor.containsCode(code)) return processor;
		}
		throw new IllegalArgumentException("Unknown URI " + uri);
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		Processor processor = findProcessor(uri);
		return processor.query(helper.getWritableDatabase(), uri, projection, selection, selectionArgs, sortOrder);
	}

	@Override
	public String getType(Uri uri) {
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		Processor processor = findProcessor(uri);
		Uri result = processor.insert(helper.getWritableDatabase(), uri, values);

		Context context = getContext();
		assert context != null;
		context.getContentResolver().notifyChange(uri, null, false);

		return result;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		Processor processor = findProcessor(uri);
		int count = processor.delete(helper.getWritableDatabase(), uri, selection, selectionArgs);

		Context context = getContext();
		assert context != null;
		context.getContentResolver().notifyChange(uri, null, false);

		return count;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		Processor processor = findProcessor(uri);
		int count = processor.update(helper.getWritableDatabase(), uri, values, selection, selectionArgs);

		Context context = getContext();
		assert context != null;
		context.getContentResolver().notifyChange(uri, null, false);

		return count;
	}

	/*@Override
	public ParcelFileDescriptor openFile(Uri uri, String mode) throws FileNotFoundException {
		L.d("URI " + uri.toString());
		L.d("MODE " + mode);

		return super.openFile(uri, mode);
	}*/
}
