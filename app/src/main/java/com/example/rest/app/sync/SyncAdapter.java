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
import ch.boye.httpclientandroidlib.HttpResponse;
import ch.boye.httpclientandroidlib.client.methods.HttpGet;
import ch.boye.httpclientandroidlib.client.methods.HttpRequestBase;
import ch.boye.httpclientandroidlib.impl.client.CloseableHttpClient;
import ch.boye.httpclientandroidlib.impl.client.HttpClients;
import ch.boye.httpclientandroidlib.impl.client.LaxRedirectStrategy;
import com.example.rest.app.config.Config;
import com.example.rest.app.parsers.Parser;
import com.example.rest.app.parsers.ParserFactory;
import com.example.rest.app.provider.AppContract;
import com.example.rest.app.provider.RestProcessor;
import com.example.rest.app.utils.L;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.text.ParseException;
import java.util.Collection;

public class SyncAdapter extends AbstractThreadedSyncAdapter implements Config {

	public SyncAdapter(Context context, boolean autoInitialize) {
		super(context, autoInitialize);
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public SyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
		super(context, autoInitialize, allowParallelSyncs);
	}

	@Override
	public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
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


	}
}
