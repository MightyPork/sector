package net.sector.network.communication;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.sector.Constants;
import net.sector.annotations.Unused;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.AbstractHttpMessage;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;


/**
 * Http connection helper (GET and POST, file download)
 * 
 * @author Ondřej Hruška (MightyPork)
 */
public class HttpHelper {

	private static final String UserAgent = "Sector/HttpHelper";
	private static final int TIMEOUT_CONNECTION = 3000;
	private static final int TIMEOUT_READ = 2000;

	/**
	 * Download file from URL to filesystem
	 * 
	 * @param remoteFileUrl url
	 * @param targetFile target file (the file, not only directory!)
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	public static void downloadFile(String remoteFileUrl, String targetFile) throws MalformedURLException, IOException {
		downloadFile(remoteFileUrl, new File(targetFile));
	}

	/**
	 * Download file from URL to filesystem
	 * 
	 * @param remoteFileUrl url
	 * @param targetFile target file (the file, not only directory!)
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	public static void downloadFile(String remoteFileUrl, File targetFile) throws MalformedURLException, IOException {
		FileUtils.copyURLToFile(new URL(remoteFileUrl), targetFile, TIMEOUT_CONNECTION, TIMEOUT_READ);
	}


	/**
	 * Send a GET request
	 * 
	 * @param url target URL without GET args
	 * @param args GET args in key-value map
	 * @return returned page as input stream
	 * @throws IOException
	 */
	@Unused
	public static InputStream requestGet(String url, Map<String, Object> args) throws IOException {

		// merge the args into a get query
		if (args.size() > 0) {

			url += "?";

			boolean first = true;

			for (Entry<String, Object> arg : args.entrySet()) {
				if (!first) url += "&";
				url += URLEncoder.encode(arg.getKey(), "UTF-8");
				url += "=";
				url += URLEncoder.encode(arg.getValue().toString(), "UTF-8");
				first = false;
			}
		}

		// prepare client
		DefaultHttpClient client = getClient();

		//HttpProtocolParams.setUserAgent(client.getParams(), UserAgent);
		HttpGet request = new HttpGet(url);

		addSectorHeaders(request);


		// connect
		HttpResponse response = client.execute(request);

		// get returned content
		return response.getEntity().getContent();
	}


	private static void addSectorHeaders(AbstractHttpMessage request) {
		request.addHeader("User-Agent", UserAgent);
		request.addHeader("X-SECTOR-VERSION", Constants.VERSION_NUMBER + "");
	}

	private static DefaultHttpClient getClient() {

		DefaultHttpClient client = new DefaultHttpClient();
		HttpParams params = client.getParams();
		HttpConnectionParams.setConnectionTimeout(params, TIMEOUT_CONNECTION);
		HttpConnectionParams.setSoTimeout(params, TIMEOUT_READ);

		return client;

	}

	/**
	 * Send a POST request
	 * 
	 * @param url target URL
	 * @param args POST args in key-value map
	 * @return returned page as input stream
	 * @throws IOException
	 */
	public static InputStream requestPost(String url, Map<String, Object> args) throws IOException {

		// prepare client
		DefaultHttpClient client = getClient();

		HttpPost request = new HttpPost(url);

		// put post arguments into the request		
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

		for (Entry<String, Object> arg : args.entrySet()) {
			nameValuePairs.add(new BasicNameValuePair(arg.getKey(), arg.getValue().toString()));
		}

		request.setEntity(new UrlEncodedFormEntity(nameValuePairs));

		addSectorHeaders(request);

		// connect
		HttpResponse response = client.execute(request);

		// get returned content
		return response.getEntity().getContent();
	}
}
