package com.example.rest.app.utils;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.Context;
import android.os.Build;
import android.preference.PreferenceManager;
import com.example.rest.app.authentication.AuthenticatorService;
import com.example.rest.app.config.Config;
import com.example.rest.app.provider.AppContract;

public class SyncUtils implements Config {
	/**
	 * Create an entry for this application in the system account list, if it isn't already there.
	 *
	 * @param context Context
	 */
	@TargetApi(Build.VERSION_CODES.FROYO)
	public static void CreateSyncAccount(Context context) {
		boolean newAccount = false;
		boolean setupComplete = PreferenceManager
				.getDefaultSharedPreferences(context).getBoolean(PREF_SETUP_COMPLETE, false);

		// Create account, if it's missing. (Either first run, or user has deleted account.)
		Account account = AuthenticatorService.GetAccount(ACCOUNT_TYPE);
		AccountManager accountManager = (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);
		if (accountManager.addAccountExplicitly(account, null, null)) {
			// Inform the system that this account supports sync
			ContentResolver.setIsSyncable(account, AppContract.AUTHORITY, 1);
			// Inform the system that this account is eligible for auto sync when the network is up
			//ContentResolver.setSyncAutomatically(account, AppContract.AUTHORITY, true);
			// Recommend a schedule for automatic synchronization. The system may modify this based
			// on other scheduled syncs and network utilization.
			//ContentResolver.addPeriodicSync(
			//		account, CONTENT_AUTHORITY, new Bundle(),SYNC_FREQUENCY);
			newAccount = true;
		}

		// Schedule an initial sync if we detect problems with either our account or our local
		// data has been deleted. (Note that it's possible to clear app data WITHOUT affecting
		// the account list, so wee need to check both.)
		if (newAccount || !setupComplete) {
			PreferenceManager.getDefaultSharedPreferences(context).edit()
					.putBoolean(PREF_SETUP_COMPLETE, true).commit();
		}
	}
}
