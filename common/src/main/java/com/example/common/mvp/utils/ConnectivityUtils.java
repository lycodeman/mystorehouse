package com.example.common.mvp.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.net.HttpURLConnection;
import java.net.URL;

/**
 *网络连接工具类
 */
public final class ConnectivityUtils {

	private ConnectivityUtils() {

	}

	public static ConnectivityManager getConnectivityManager(final Context pContext) {
		return (ConnectivityManager) pContext.getSystemService(Context.CONNECTIVITY_SERVICE);
	}

	/**
	 * @param pContext
	 * @param pNetworkType {@link ConnectivityManager#TYPE_WIFI}, etc...
	 * @return
	 */
	public static NetworkInfo getNetworkInfo(final Context pContext, final int pNetworkType) {
		return ConnectivityUtils.getConnectivityManager(pContext).getNetworkInfo(pNetworkType);
	}

	public static boolean isNetworkAvailable() {
	    ConnectivityManager manager = (ConnectivityManager) ActivityManager.appContext()
				.getSystemService(Context.CONNECTIVITY_SERVICE);
	    if (manager == null) {
	        return false;
	    }
	    NetworkInfo networkinfo = manager.getActiveNetworkInfo();

		return !(networkinfo == null || !networkinfo.isAvailable());
	}

	/**
	 * @param pContext
	 * @param pNetworkType {@link ConnectivityManager#TYPE_WIFI}, etc...
	 * @return
	 */
	public static boolean isNetworkAvailable(final Context pContext, final int pNetworkType) {
		return ConnectivityUtils.getNetworkInfo(pContext, pNetworkType).isAvailable();
	}

	/**
	 * 通过Ping的方式来监测网络连接
	 * @return
	 */
    public static boolean isNetworkAvailable(String urlPath) {
    	boolean flag = false;
		HttpURLConnection httpconn = null;
		try {
			URL url = new URL(urlPath);
			httpconn = (HttpURLConnection) url.openConnection();
			httpconn.setConnectTimeout(1000);
			httpconn.setRequestProperty("Connection", "close");
			httpconn.connect();

			if (httpconn.getResponseCode() == HttpURLConnection.HTTP_OK) {
				httpconn.disconnect();
				flag = true;
			}
		} catch (Throwable e) {
			e.printStackTrace();
		} finally {
			if(httpconn != null) {
				httpconn.disconnect();
				httpconn = null;
			}
		}
		return flag;
    }

	/**
	 * @param pContext
	 * @param pNetworkType {@link ConnectivityManager#TYPE_WIFI}, etc...
	 * @return
	 */
	public static boolean isNetworkConnected(final Context pContext, final int pNetworkType) {
		return ConnectivityUtils.getNetworkInfo(pContext, pNetworkType).isConnected();
	}

	/**
	 * @param pContext
	 * @param pNetworkType {@link ConnectivityManager#TYPE_WIFI}, etc...
	 * @return
	 */
	public static boolean isNetworkConnectedOrConnecting(final Context pContext, final int pNetworkType) {
		return ConnectivityUtils.getNetworkInfo(pContext, pNetworkType).isConnectedOrConnecting();
	}

	public static boolean isWifiAvailable(final Context pContext) {
		return ConnectivityUtils.isNetworkAvailable(pContext, ConnectivityManager.TYPE_WIFI);
	}

	public static boolean isWifiConnected(final Context pContext) {
		return ConnectivityUtils.isNetworkConnected(pContext, ConnectivityManager.TYPE_WIFI);
	}

	public static boolean isWifiConnectedOrConnecting(final Context pContext) {
		return ConnectivityUtils.isNetworkConnectedOrConnecting(pContext, ConnectivityManager.TYPE_WIFI);
	}
	
    /**
     * 结合Ping和API的方式来监测网络连接
     * @return
     */
    public static boolean detect(Context context, String urlPath) {
		return isNetworkAvailable() && isNetworkAvailable(urlPath);
	}

}
