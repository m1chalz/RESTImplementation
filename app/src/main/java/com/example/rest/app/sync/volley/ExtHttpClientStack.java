package com.example.rest.app.sync.volley;


import android.util.Log;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Request.Method;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpStack;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;

public class ExtHttpClientStack implements HttpStack {

	protected final CloseableHttpClient client;
	private final static String HEADER_CONTENT_TYPE = "Content-Type";

	public ExtHttpClientStack() throws VolleyError {
		try {
			SSLContext sslcontext = SSLContexts.custom()
					.loadTrustMaterial(null, new TrustStrategy() {
						@Override
						public boolean isTrusted(
								final X509Certificate[] chain,
								final String authType) throws CertificateException {
							return true;
						}
					}).build();

			client = HttpClients.custom()
					//.setUserAgent(USER_AGENT)
					.setRedirectStrategy(new LaxRedirectStrategy())
					.setSslcontext(sslcontext)
					.build();
		} catch (NoSuchAlgorithmException | KeyManagementException | KeyStoreException e) {
			throw new VolleyError("Cannot create HttpClient");
		}
	}

	@Override
	public HttpResponse performRequest(Request<?> request, Map<String, String> additionalHeaders) throws IOException, AuthFailureError {
		HttpUriRequest httpRequest = createHttpRequest(request, additionalHeaders);
		addHeaders(httpRequest, additionalHeaders);
		addHeaders(httpRequest, request.getHeaders());
		onPrepareRequest(httpRequest);
		HttpParams httpParams = httpRequest.getParams();
		int timeoutMs = request.getTimeoutMs();
		// TODO: Reevaluate this connection timeout based on more wide-scale
		// data collection and possibly different for wifi vs. 3G.
		HttpConnectionParams.setConnectionTimeout(httpParams, 5000);
		HttpConnectionParams.setSoTimeout(httpParams, timeoutMs);

		Log.d("ExtHttpClientStack", "executing request...");
		return client.execute(httpRequest);
	}

	private static void addHeaders(HttpUriRequest httpRequest, Map<String, String> headers) {
		for (String key : headers.keySet()) {
			httpRequest.setHeader(key, headers.get(key));
		}
	}

	/**
	 * Creates the appropriate subclass of HttpUriRequest for passed in request.
	 */
	@SuppressWarnings("deprecation")
	static HttpUriRequest createHttpRequest(Request<?> request, Map<String, String> additionalHeaders) throws AuthFailureError {
		switch (request.getMethod()) {
		case Method.DEPRECATED_GET_OR_POST:
			// This is the deprecated way that needs to be handled for backwards compatibility.
			// If the request's post body is null, then the assumption is that the request is
			// GET.  Otherwise, it is assumed that the request is a POST.
			byte[] postBody = request.getPostBody();
			if (postBody != null) {
				HttpPost postRequest = new HttpPost(request.getUrl());
				postRequest.addHeader(HEADER_CONTENT_TYPE, request.getPostBodyContentType());
				HttpEntity entity;
				entity = new ByteArrayEntity(postBody);
				postRequest.setEntity(entity);
				return postRequest;
			} else {
				return new HttpGet(request.getUrl());
			}

		case Method.GET:
			return new HttpGet(request.getUrl());

		case Method.DELETE:
			return new HttpDelete(request.getUrl());

		case Method.POST:
			HttpPost postRequest = new HttpPost(request.getUrl());
			postRequest.addHeader(HEADER_CONTENT_TYPE, request.getBodyContentType());
			setEntityIfNonEmptyBody(postRequest, request);
			return postRequest;

		case Method.PUT:
			HttpPut putRequest = new HttpPut(request.getUrl());
			putRequest.addHeader(HEADER_CONTENT_TYPE, request.getBodyContentType());
			setEntityIfNonEmptyBody(putRequest, request);
			return putRequest;

		default:
			throw new IllegalStateException("Unknown request method.");
		}
	}

	private static void setEntityIfNonEmptyBody(HttpEntityEnclosingRequestBase httpRequest, Request<?> request) throws AuthFailureError {
		byte[] body = request.getBody();
		if (body != null) {
			HttpEntity entity = new ByteArrayEntity(body);
			httpRequest.setEntity(entity);
		}
	}

	/**
	 * Called before the request is executed using the underlying HttpClient.
	 *
	 * <p>
	 * Overwrite in subclasses to augment the request.
	 * </p>
	 */
	protected void onPrepareRequest(HttpUriRequest request) throws IOException {
		// Nothing.
	}
}
