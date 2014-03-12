package com.example.rest.app.config;

public interface Config {

	/**
	 * Database constants
	 */
	public static final String DB_NAME = "foo.db";
	public static final int DB_VERSION = 1;

	/**
	 * Authenticator constants
	 */
	public static final String ACCOUNT_TYPE = "com.example.rest.app.account";

	/**
	 * Preferences constants
	 */
	public static final String PREF_SETUP_COMPLETE = "setup_complete";

	/**
	 * Network constants
	 */
	public static final String USER_AGENT = "android http client";
}
