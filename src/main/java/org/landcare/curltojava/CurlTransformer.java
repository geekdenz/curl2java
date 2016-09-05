/*
 * © Copyright 2015
 * Landcare Research
 * 
 * Dual License with
 * 
 * GPL v3 - See http://www.gnu.org/licenses/gpl-3.0.txt
 * 
 * Any derivative work needs to be contributed back to this project
 * unless otherwise agreed with Landcare Research, New Zealand.
 */
package org.landcare.curltojava;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import net.sf.json.JSONObject;
import net.sf.json.processors.JsonVerifier;

/**
 *
 * @author heuert@landcareresearch.co.nz
 */
public class CurlTransformer {

	private String splitCommand[] = null;
	private String headers[] = null;
	private final String curlCommand;

	public CurlTransformer(String curlCommand) {
		this.curlCommand = curlCommand;
		this.splitCommand = curlCommand.split(" +");
		this.headers = getHeaders();
	}

	public static void main(String[] args) {
		//String response = new CurlTransformer("curl 'http://test.smap.landcareresearch.co.nz/services/point_query/json?_dc=1472707898014&layers=smap_soil_drainage&longitude=5161420.2021421&latitude=1585716.6820873&epsg=2193' -d 'hello=post&another=variable' -H 'Pragma: no-cache' -H 'Accept-Encoding: gzip, deflate, sdch' -H 'Accept-Language: en-US,en;q=0.8,en-NZ;q=0.6,de;q=0.4' -H 'User-Agent: Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.110 Safari/537.36' -H 'Accept: */*' -H 'Referer: http://test.smap.landcareresearch.co.nz/smap' -H 'X-Requested-With: XMLHttpRequest' -H 'Cookie: ys-help=b%3A1; ASP.NET_SessionId=sdkcvkyfj32003g5xj3wqtxy; __utma=137900973.1821734114.1441574815.1456870503.1467086067.9; __utmc=137900973; __utmz=137900973.1467086067.9.7.utmcsr=google|utmccn=(organic)|utmcmd=organic|utmctr=(not%20provided); __utma=167366204.1821734114.1441574815.1472162378.1472165353.20; __utmc=167366204; __utmz=167366204.1472101237.18.3.utmcsr=google|utmccn=(organic)|utmcmd=organic|utmctr=(not%20provided); ys-terms=b%3A1; ys-help=b%3A1; ys-rhs_panel=o%3Acollapsed%3Db%253A0; ys-terms=b%3A1; ys-rhs_panel=o%3Acollapsed%3Db%253A0; _ga=GA1.3.1821734114.1441574815; _gat=1' -H 'Connection: keep-alive' -H 'Cache-Control: no-cache' --compressed").getJson().toString(2);
		//String response = new CurlTransformer("curl http://scooterlabs.com/echo.json?foo=bar -d 'hello=world&body=json'").getJson().toString(2);
		String response = new CurlTransformer("curl https://dcoder.nz/echo/ --data 'polygon=hello you'").getResponse();
		System.out.println("RESPONSE: " + response);
	}

	private SSLSocketFactory getLenientSSLSocketFactory() {
		TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return null;
			}

			public void checkClientTrusted(X509Certificate[] certs, String authType) {
			}

			public void checkServerTrusted(X509Certificate[] certs, String authType) {
			}

		}};
		SSLContext sc = null;
		try {
			sc = SSLContext.getInstance("SSL");
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
		} catch (KeyManagementException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return sc.getSocketFactory();
	}

	public String getResponse() {
		try {
			String urlString = getUrl();
			URL url = new URL(urlString);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			HttpsURLConnection sConn = null;
			String postParameters = getPostParameters();
			if (conn instanceof HttpsURLConnection) {
				sConn = ((HttpsURLConnection) conn);
				sConn.setHostnameVerifier(getLenientHostnameVerifier()); // DANGEROUS! Don't use in production code!
				sConn.setSSLSocketFactory(getLenientSSLSocketFactory()); // disable some more security
				System.out.println("NOT VERIFYING HTTPS");
			}
			if (postParameters != null) {
				conn.setRequestMethod("POST");
				byte[] postData = postParameters.getBytes(StandardCharsets.UTF_8);
				int postDataLength = postData.length;
				conn.setDoOutput(true);
				conn.setInstanceFollowRedirects(false);
				conn.setRequestMethod("POST");
				if (JsonVerifier.isValidJsonValue(postData)) {
					conn.setRequestProperty("Content-Type", "application/json");
				} else {
					conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
				}
				conn.setRequestProperty("charset", "utf-8");
				conn.setRequestProperty("Content-Length", Integer.toString(postDataLength));
				conn.setUseCaches(false);
				try (DataOutputStream wr = new DataOutputStream(conn.getOutputStream())) {
					wr.write(postData);
				}
			} else {
				conn.setRequestMethod("GET");
			}
			InputStream stream = conn.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(stream));
			String inputLine = null;
			StringBuffer response = new StringBuffer();
			while ((inputLine = br.readLine()) != null) {
				response.append(inputLine);
			}
			return response.toString();
		} catch (MalformedURLException ex) {
			Logger.getLogger(CurlTransformer.class.getName()).log(Level.SEVERE, null, ex);
		} catch (IOException ex) {
			Logger.getLogger(CurlTransformer.class.getName()).log(Level.SEVERE, null, ex);
		}
		return "";
	}

	private String getUrl() {
		String url = splitCommand[1];
		if (url.charAt(0) == '\'') {
			return url.substring(1, url.length() - 1);
		}
		return url;
	}

	public JSONObject getJson() {
		return JSONObject.fromObject(this.getResponse());
	}

	private static final String VALUE_PATTERN = "'(([^']|\\')+)'";

	private String getPostParameters() {
		Pattern pattern = Pattern.compile("--?d(ata)? +" + VALUE_PATTERN);
		Matcher matcher = pattern.matcher(curlCommand);
		//Map<String, String> postParameters = null;
		while (matcher.find()) {
			String query = matcher.group(2);
			return query;
		}
		return null;
	}

	public String[] getHeaders() {
		Pattern pattern = Pattern.compile("-H +" + VALUE_PATTERN);
		Matcher matcher = pattern.matcher(curlCommand);
		while (matcher.find()) {
			System.out.println("group: " + matcher.group(1));
		}
		return null;
	}

	public Map<String, String> parse(String query) throws UnsupportedEncodingException {
		final Map<String, String> query_pairs = new LinkedHashMap<String, String>();
		final String[] pairs = query.split("&");
		for (String pair : pairs) {
			final int idx = pair.indexOf("=");
			final String key = idx > 0 ? URLDecoder.decode(pair.substring(0, idx), "UTF-8") : pair;
			final String value = idx > 0 && pair.length() > idx + 1 ? URLDecoder.decode(pair.substring(idx + 1), "UTF-8") : null;
			query_pairs.put(key, value);
		}
		return query_pairs;
	}

	private HostnameVerifier getLenientHostnameVerifier() {
		HostnameVerifier allHostsValid = new HostnameVerifier() {
			public boolean verify(String hostname, SSLSession session) {
				return true;
			}
		};
		return allHostsValid;
	}

}
