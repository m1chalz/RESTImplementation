package com.example.rest.app.provider;

import android.content.Context;

public abstract class Processor implements DatabaseInterface {
	protected String name;
	protected int code;
	private Context context;

	protected Processor(Context context, int code, String name) {
		this();
		this.code = code;
		this.context = context;
		this.name = name;
	}

	public Processor(Context context, int code) {}

	private Processor() {}

	public boolean containsCode(int code) {
		return this.code == code;
	}

	protected Context getContext() {
		return context;
	}
}
