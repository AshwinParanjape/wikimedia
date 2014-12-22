package org.wikimedia.west1.traces;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.json.JSONException;
import org.json.JSONObject;

public class Pageview {
	public JSONObject json;
	public long time;
	public long seq;
	public String url;
	public String referer;

	public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
	private static final String UTF8 = "UTF-8";

	public Pageview(JSONObject json) throws JSONException, ParseException {
		this.json = json;
		this.time = DATE_FORMAT.parse(json.getString("dt")).getTime();
		this.seq = json.getLong("sequence");
		// URL-decode the URI path. It is stored as modified in the JSON object.
		try {
			json.put("uri_path", URLDecoder.decode(json.getString("uri_path"), UTF8));
			this.url = String.format("%s%s%s", json.getString("uri_host"), json.getString("uri_path"),
			    json.getString("uri_query"));
		} catch (UnsupportedEncodingException e) {
			// This should never happen, since the encoding is hard-coded as "UTF-8".
		}
		// Strip the protocol from the referer and URL decode the path, so it's comparable to the URL.
		try {
			try {
				URL ref = new URL(json.getString("referer"));
				String q = ref.getQuery();
				q = q == null ? "" : "?" + q;
				// NB: anchor info ("#...") is omitted.
				this.referer = String.format("%s%s%s", ref.getAuthority().replace("//", ""),
				    URLDecoder.decode(ref.getPath(), UTF8), q);
			} catch (MalformedURLException e) {
				this.referer = URLDecoder.decode(json.getString("referer").split("://")[1], UTF8);
			}
		} catch (UnsupportedEncodingException e) {
			// This should never happen, since the encoding is hard-coded as "UTF-8".
		}
	}

	public String toString() {
		return json.toString();
	}

	public String toString(int indentFactor) {
		try {
			return json.toString(indentFactor);
		} catch (JSONException e) {
			System.err.println(json);
			return "[JSONException]";
		}
	}
}
