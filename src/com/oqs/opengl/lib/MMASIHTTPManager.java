package com.oqs.opengl.lib;

import android.content.Context;



import java.util.concurrent.ThreadPoolExecutor;

import org.apache.http.client.CookieStore;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.HttpEntity;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class MMASIHTTPManager extends Object {

	private static AsyncHttpClient _client = new AsyncHttpClient();
	private static AsyncHttpClient getAsyncHttpClient() {return _client;}

	public static void cancelRequests(Context context,boolean mayInterruptIfRunning) {getAsyncHttpClient().cancelRequests(context,mayInterruptIfRunning);}

	public static void delete(String url,AsyncHttpResponseHandler responseHandler) {getAsyncHttpClient().delete(url,responseHandler);}
	public static void delete(Context context,String url,AsyncHttpResponseHandler responseHandler) {getAsyncHttpClient().delete(context,url,responseHandler);}

	public static void get(String url,AsyncHttpResponseHandler responseHandler) {getAsyncHttpClient().get(url,responseHandler);}
	public static void get(String url,RequestParams params,AsyncHttpResponseHandler responseHandler) {getAsyncHttpClient().get(url,params,responseHandler);}
	public static void get(Context context,String url,AsyncHttpResponseHandler responseHandler) {getAsyncHttpClient().get(context,url,responseHandler);}
	public static void get(Context context,String url,RequestParams params,AsyncHttpResponseHandler responseHandler) {getAsyncHttpClient().get(context,url,params,responseHandler);}         

		public static void post(String url,AsyncHttpResponseHandler responseHandler) {getAsyncHttpClient().post(url,responseHandler);}
		public static void post(String url,RequestParams params,AsyncHttpResponseHandler responseHandler) {getAsyncHttpClient().post(url,params,responseHandler);}
		public static void post(Context context,String url,RequestParams params,AsyncHttpResponseHandler responseHandler) {getAsyncHttpClient().post(context,url,params,responseHandler);}
		public static void post(Context context,String url,HttpEntity entity,String contentType,AsyncHttpResponseHandler responseHandler) {getAsyncHttpClient().post(context,url,entity,contentType,responseHandler);}

		public static void put(String url,AsyncHttpResponseHandler responseHandler) {getAsyncHttpClient().put(url,responseHandler);}
		public static void put(String url,RequestParams params,AsyncHttpResponseHandler responseHandler) {getAsyncHttpClient().put(url,params,responseHandler);}
		public static void put(Context context,String url,RequestParams params,AsyncHttpResponseHandler responseHandler) {getAsyncHttpClient().put(context,url,params,responseHandler);}
		public static void put(Context context,String url,HttpEntity entity,String contentType,AsyncHttpResponseHandler responseHandler) {getAsyncHttpClient().put(context,url,entity,contentType,responseHandler);}

		public static void setCookieStore(CookieStore cookieStore) {getAsyncHttpClient().setCookieStore(cookieStore);}
		public static void setThreadPool(ThreadPoolExecutor threadPool) {getAsyncHttpClient().setThreadPool(threadPool);}
		public static void setUserAgent(String userAgent) {getAsyncHttpClient().setUserAgent(userAgent);}
		public static void setSSLSocketFactory(SSLSocketFactory sslSocketFactory) {getAsyncHttpClient().setSSLSocketFactory(sslSocketFactory);}

}
