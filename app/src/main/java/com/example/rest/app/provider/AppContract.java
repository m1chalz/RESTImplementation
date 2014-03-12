package com.example.rest.app.provider;

import android.net.Uri;
import android.provider.BaseColumns;

public final class AppContract {
	private AppContract() {
	}

	/**
	 * This authority is used for writing to or querying from the database
	 * provider. Note: This is set at first run and cannot be changed without
	 * breaking apps that access the provider.
	 */
	public static final String AUTHORITY = "com.example.rest.app.provider";

	/**
	 * The content:// style URL for the top-level database authority
	 */
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY);

	public static final String VALIDITY = "_validity";

	protected interface SyncColumns {
		/**
		 * State of REST request.
		 * 0 - ok
		 * 1 - posting
		 * 2 - updating
		 * 3 - deleting
		 * <P>Type: INTEGER</P>
		 */
		public static final String _STATE = "_state";
		/**
		 * Validity timestamp in miliseconds.
		 * <P>Type: INTEGER (long)</P>
		 */
		public static final String _VALIDITY = VALIDITY;

		/**
		 * Data. Response stream.
		 * <P>Type: TEXT</P>
		 */
		public static final String _DATA = "_data";
	}

	protected interface EntityColumns {
		/**
		 * Atom ID. (Note: Not to be confused with the database primary key, which is _ID.
		 */
		public static final String ENTRY_ID = "entry_id";
		/**
		 * Article title
		 */
		public static final String TITLE = "title";
		/**
		 * Article hyperlink. Corresponds to the rel="alternate" link in the
		 * Atom spec.
		 */
		public static final String LINK = "link";
		/**
		 * Date article was published.
		 */
		public static final String PUBLISHED = "published";
	}

	public static final class Entities implements BaseColumns, SyncColumns, EntityColumns {
		public static final String TABLE_NAME = "entities";
		public static final Uri CONTENT_URI = Uri.parse(AppContract.CONTENT_URI + "/" + TABLE_NAME);
		public static final String[] TABLE_COLUMNS = new String[] {
				_ID,
				_STATE,
				_VALIDITY,
				_DATA,
				ENTRY_ID,
				TITLE,
				LINK,
				PUBLISHED
		};

		/**
		 * This utility class cannot be instantiated
		 */
		private Entities() {}
	}
}
