package com.example.rest.app.authentication;

import android.accounts.Account;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class AuthenticatorService extends Service {

	//public static final String ACCOUNT_NAME = "Account";
	public static final String ACCOUNT_NAME = "Account";
	private Authenticator authenticator;

	@Override
	public void onCreate() {
		authenticator = new Authenticator(this);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return authenticator.getIBinder();
	}

	/**
	 * Obtain a handle to the {@link android.accounts.Account} used for sync in this application.
	 *
	 * <p>It is important that the accountType specified here matches the value in your sync adapter
	 * configuration XML file for android.accounts.AccountAuthenticator (often saved in
	 * res/xml/syncadapter.xml). If this is not set correctly, you'll receive an error indicating
	 * that "caller uid XXXXX is different than the authenticator's uid".
	 *
	 * @param accountType AccountType defined in the configuration XML file for
	 *                    android.accounts.AccountAuthenticator (e.g. res/xml/syncadapter.xml).
	 * @return Handle to application's account (not guaranteed to resolve unless CreateSyncAccount()
	 *         has been called)
	 */
	public static Account GetAccount(String accountType) {
		// Note: Normally the account name is set to the user's identity (username or email
		// address). However, since we aren't actually using any user accounts, it makes more sense
		// to use a generic string in this case.
		//
		// This string should *not* be localized. If the user switches locale, we would not be
		// able to locate the old account, and may erroneously register multiple accounts.
		final String accountName = ACCOUNT_NAME;
		return new Account(accountName, accountType);
	}
}
