package com.example.rest.app.fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.text.format.Time;
import android.view.View;
import android.widget.TextView;
import com.example.rest.app.config.Config;
import com.example.rest.app.provider.AppContract;

public class MainFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor>, Config {

	private SimpleCursorAdapter adapter;

	/**
	 * Projection for querying the content provider.
	 */
	private static final String[] PROJECTION = new String[]{
			AppContract.Entities._ID,
			AppContract.Entities.TITLE,
			AppContract.Entities.LINK,
			AppContract.Entities.PUBLISHED
	};

	/**
	 * List of Cursor columns to read from when preparing an adapter to populate the ListView.
	 */
	private static final String[] FROM_COLUMNS = new String[]{
			AppContract.Entities.TITLE,
			AppContract.Entities.PUBLISHED
	};

	/**
	 * List of Views which will be populated by Cursor data.
	 */
	private static final int[] TO_FIELDS = new int[]{
			android.R.id.text1,
			android.R.id.text2};

	public MainFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		adapter = new SimpleCursorAdapter(
				getActivity(),       // Current context
				android.R.layout.simple_list_item_activated_2,  // Layout for individual rows
				null,                // Cursor
				FROM_COLUMNS,        // Cursor columns to use
				TO_FIELDS,           // Layout fields to use
				0                    // No flags
		);
		adapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
			@Override
			public boolean setViewValue(View view, Cursor cursor, int i) {
				if (i == 3) {
					// Convert timestamp to human-readable date
					Time t = new Time();
					t.set(cursor.getLong(i));
					((TextView) view).setText(t.format("%Y-%m-%d %H:%M"));
					return true;
				} else {
					// Let SimpleCursorAdapter handle other fields automatically
					return false;
				}
			}
		});
		setListAdapter(adapter);

		getLoaderManager().initLoader(0, null, this);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		return new CursorLoader(getActivity(),  // Context
				AppContract.Entities.CONTENT_URI,   // URI
				PROJECTION,                         // Projection
				null,                               // Selection
				null,                               // Selection args
				AppContract.Entities.PUBLISHED + " desc"); // Sort
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		adapter.changeCursor(data);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		adapter.changeCursor(null);
	}
}
