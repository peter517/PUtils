package com.pengjun.utils;


import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

public class NetWorkUtils {

	private static HttpClient httpClient = new DefaultHttpClient(
			createHttpParams());

	private static HttpParams createHttpParams() {
		HttpParams params = new BasicHttpParams();
		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
		HttpConnectionParams.setConnectionTimeout(params, 30 * 1000);
		HttpConnectionParams.setSoTimeout(params, 30 * 1000);
		HttpConnectionParams.setTcpNoDelay(params, true);
		HttpConnectionParams.setSocketBufferSize(params, 8192);
		return params;
	}

	public static InputStream getInputStremFromUrl(String url) {

		HttpGet httpGet = new HttpGet(url);
		HttpResponse httpResponse = null;
		try {
			httpResponse = httpClient.execute(httpGet);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (httpResponse == null) {
			return null;
		}

		if (httpResponse.getStatusLine() == null
				|| httpResponse.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
			return null;
		}

		HttpEntity httpEntity = httpResponse.getEntity();

		if (httpEntity == null) {
			return null;
		}

		try {
			return httpEntity.getContent();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			httpGet.abort();
		}

		return null;
	}

	public static String getStrFromUrl(String url)
			throws ClientProtocolException, IOException {

		HttpGet httpGet = new HttpGet(url);

		try {
			httpGet.setHeader("User-Agent", "Mozilla/4.5");
			HttpResponse httpResponse = httpClient.execute(httpGet);

			if (httpResponse.getStatusLine() == null
					|| httpResponse.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
				return null;
			}

			// 处理返回的httpResponse信息
			HttpEntity httpEntity = httpResponse.getEntity();
			if (httpEntity == null) {
				return null;
			}
			int length = (int) httpEntity.getContentLength();
			if (length < 0) {
				length = 10000;
			}

			StringBuffer stringBuffer = new StringBuffer(length);
			try {
				InputStreamReader inputStreamReader = new InputStreamReader(
						httpEntity.getContent(), HTTP.UTF_8);
				char buffer[] = new char[length];
				int count;
				while ((count = inputStreamReader.read(buffer, 0, length - 1)) > 0) {
					stringBuffer.append(buffer, 0, count);
				}
				httpGet.abort();
				return stringBuffer.toString();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} finally {
			httpGet.abort();
		}
		return null;
	}

	public static void sendUDPData(String ip, int port, byte[] data)
			throws IOException {
		DatagramSocket ds = new DatagramSocket(port);
		DatagramPacket dp = new DatagramPacket(data, data.length,
				InetAddress.getByName(ip), port);
		ds.send(dp);
		ds.close();
	}

	public static DatagramPacket recvUDPData(int port) throws IOException {
		DatagramSocket ds = new DatagramSocket(port);
		byte[] buf = new byte[1024];
		DatagramPacket dp = new DatagramPacket(buf, 1024);
		ds.receive(dp);
		ds.close();
		return dp;
	}
}
