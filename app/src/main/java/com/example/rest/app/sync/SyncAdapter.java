package com.example.rest.app.sync;

import android.accounts.Account;
import android.annotation.TargetApi;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncResult;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.rest.app.config.Config;
import com.example.rest.app.parsers.Parser;
import com.example.rest.app.parsers.ParserFactory;
import com.example.rest.app.provider.AppContract;
import com.example.rest.app.provider.RestProcessor;
import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.Collection;

public class SyncAdapter extends AbstractThreadedSyncAdapter implements Config {

	static RequestQueue requestQueue = null;

	public SyncAdapter(Context context, boolean autoInitialize) {
		super(context, autoInitialize);
		if (requestQueue == null) {
			//requestQueue = Volley.newRequestQueue(context, new ExtHttpClientStack());
			requestQueue = Volley.newRequestQueue(context);
		}
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public SyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
		super(context, autoInitialize, allowParallelSyncs);
		if (requestQueue == null) {
			//requestQueue = Volley.newRequestQueue(context, new ExtHttpClientStack());
			requestQueue = Volley.newRequestQueue(context);
		}
	}

	@Override
	public void onPerformSync(Account account, final Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {

		Response.Listener<String> listener = new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				Log.d("SyncAdapter", "Success Response: " + response);

				Parser parser = ParserFactory.GetParserByUriString(extras.getString(RestProcessor.URI_KEY));
				assert parser != null;
				Collection<ContentValues> items = null;
				try {
					items = parser.parse(new ByteArrayInputStream(response.getBytes()));
				} catch (XmlPullParserException | IOException | ParseException e) {
					e.printStackTrace();
				}

				for (ContentValues cv : items) {
					cv.put(AppContract.VALIDITY, extras.getLong(RestProcessor.VALIDITY_KEY));
					getContext().getContentResolver().insert(Uri.parse(extras.getString(RestProcessor.URI_KEY)), cv);
				}
			}
		};

		Response.ErrorListener errorListener = new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				if (error.networkResponse != null) {
					Log.d("SyncAdapter", "Error Response code: " + error.networkResponse.statusCode);
					Log.d("SyncAdapter", "Error Response: " + (new String(error.networkResponse.data)));
				}
			}
		};

		Log.d("SyncAdapter", extras.toString());
		StringRequest request = new StringRequest(
				Request.Method.GET,
				extras.getString(RestProcessor.URI_KEY),
				listener,
				errorListener
		);

		requestQueue.add(request);
/*
		try {
			//FIXME missing force gzip content
			CloseableHttpClient client = HttpClients.custom()
					.setUserAgent(USER_AGENT)
					.setRedirectStrategy(new LaxRedirectStrategy())
					.build();
			client.log.enableDebug(true);
			HttpRequestBase request;

			L.d(extras);

			switch (extras.getString(RestProcessor.METHOD_KEY)) {
			case RestProcessor.GET_METHOD:
				request = new HttpGet(extras.getString(RestProcessor.URL_KEY));
				break;

			case RestProcessor.POST_METHOD:
				throw new UnsupportedOperationException("Not yet implemented! Sorry!");

			default:
				throw new IllegalArgumentException("Valid REST method missing");
			}

			HttpResponse response = client.execute(request);
			//FIXME missing response.getStatusLine().getStatusCode() handling

			Parser parser = ParserFactory.GetParserByUriString(extras.getString(RestProcessor.URI_KEY));
			Collection<ContentValues> items = parser.parse(response.getEntity().getContent());

			for (ContentValues cv : items) {
				cv.put(AppContract.VALIDITY, extras.getLong(RestProcessor.VALIDITY_KEY));
				getContext().getContentResolver().insert(Uri.parse(extras.getString(RestProcessor.URI_KEY)), cv);
			}

		} catch (IOException | XmlPullParserException | ParseException e) {
			e.printStackTrace();
		}
*/


	}
}
