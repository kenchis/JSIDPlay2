package jsidplay2.haendel.de.jsidplay2app.request;

import android.util.Pair;

import java.io.IOException;
import java.net.URLConnection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import jsidplay2.haendel.de.jsidplay2app.config.IConfiguration;

public abstract class FavoritesRequest extends JSIDPlay2RESTRequest<List<String>>
		implements JSIDPlay2RESTRequest.IKeyLocalizer {
	protected FavoritesRequest(String appName, IConfiguration configuration, RequestType type, String url) {
		super(appName, configuration, type, url, null);
	}

	@Override
	protected List<String> getResult(URLConnection connection) throws IllegalStateException, IOException {
		return receiveList(connection);
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
