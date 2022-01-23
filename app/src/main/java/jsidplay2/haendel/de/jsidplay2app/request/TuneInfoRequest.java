package jsidplay2.haendel.de.jsidplay2app.request;

import java.io.IOException;
import java.net.URLConnection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.util.Pair;
import jsidplay2.haendel.de.jsidplay2app.config.IConfiguration;

public abstract class TuneInfoRequest extends JSIDPlay2RESTRequest<List<Pair<String, String>>>
		implements JSIDPlay2RESTRequest.IKeyLocalizer {
	protected TuneInfoRequest(String appName, IConfiguration configuration, RequestType type, String url) {
		super(appName, configuration, type, url, null);
	}

	@Override
	protected List<Pair<String, String>> getResult(URLConnection connection) throws IllegalStateException, IOException {
		return receiveListOfKeyValues(connection, this);
	}

	/**
	 * Get localized tune info name
	 * 
	 * @param key
	 *            tune info name
	 * @return localized string
	 */
	public abstract String getString(String key);
}
