package com.example.rest.app.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import com.example.rest.app.utils.SyncUtils;

public abstract class AppActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SyncUtils.CreateSyncAccount(this);
	}
}
