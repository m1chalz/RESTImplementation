package com.example.rest.app.utils;

import android.os.Bundle;
import android.util.Log;

import java.util.Set;

public class L {

	private enum LogState {
		INFO, ERROR, ALL
	}

	public static final LogState CURRENT_STATE = LogState.ALL;

	public static void e(final String msg) {

		switch (CURRENT_STATE) {
			case ALL:
			case ERROR:

				final Throwable t = new Throwable();
				final StackTraceElement[] elements = t.getStackTrace();

				final String callerClassName = elements[1].getClassName();
				final String callerMethodName = elements[1].getMethodName();

				Log.e(callerClassName, "[" + callerMethodName + "] " + msg);
			case INFO:
				break;
			default:
				break;
		}
	}

	public static void w(final String msg) {

		switch (CURRENT_STATE) {
			case ALL:
			case ERROR:

				final Throwable t = new Throwable();
				final StackTraceElement[] elements = t.getStackTrace();

				final String callerClassName = elements[1].getClassName();
				final String callerMethodName = elements[1].getMethodName();

				Log.w(callerClassName, "[" + callerMethodName + "] " + msg);
			case INFO:
				break;
			default:
				break;
		}
	}

	public static void i(final String msg) {

		switch (CURRENT_STATE) {
			case ALL:
			case INFO:

				final Throwable t = new Throwable();
				final StackTraceElement[] elements = t.getStackTrace();

				final String callerClassName = elements[1].getClassName();
				final String callerMethodName = elements[1].getMethodName();

				Log.i(callerClassName, "[" + callerMethodName + "] " + msg);
			case ERROR:
				break;
			default:
				break;
		}
	}

	public static void d(final String msg) {

		switch (CURRENT_STATE) {
			case ALL:
			case INFO:

				final Throwable t = new Throwable();
				final StackTraceElement[] elements = t.getStackTrace();

				final String callerClassName = elements[1].getClassName();
				final String callerMethodName = elements[1].getMethodName();

				Log.d(callerClassName, "[" + callerMethodName + "] " + msg);
			case ERROR:
				break;
			default:
				break;
		}
	}

	public static void d(Bundle b) {
		StringBuilder sb = new StringBuilder();
		final Set<String> keySet = b.keySet();

		for (final String key: keySet) {
			sb.append('\"');
			sb.append(key);
			sb.append("\"=\"");
			sb.append(b.get(key));
			sb.append("\", ");
		}
		d(sb.toString());
	}

	public static void vv(final String msg) {

		switch (CURRENT_STATE) {
			case ALL:
			case INFO:

				final Throwable t = new Throwable();
				final StackTraceElement[] elements = t.getStackTrace();

			for (int i = 1; i < elements.length; i++) {
				if (i == 1)
					Log.d(elements[i].getClassName(), "[" + elements[i].getMethodName() + "] " + msg);
				else
					Log.d(elements[i].getClassName(), "[" + elements[i].getMethodName() + "]");
			}
		case ERROR:
			break;
		default:
			break;
		}
	}

}
