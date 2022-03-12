package jsidplay2.haendel.de.jsidplay2app.request;

import java.io.IOException;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jsidplay2.haendel.de.jsidplay2app.config.IConfiguration;

public abstract class DirectoryRequest extends JSIDPlay2RESTRequest<List<String>> {

	private static final String FILTER_PAR = "filter";
	private static final String TUNE_FILTER = ".*\\.(sid|dat|mus|str|d64|g64|nib|tap|t64|prg|p00|reu|ima|crt|img|bin|mp3|mp4|dv|vob|txt|jpg)$";

	private static final Map<String, String> parameters = new HashMap<>();
	static {
		parameters.put(FILTER_PAR, TUNE_FILTER);
	}

	protected DirectoryRequest(String appName, IConfiguration configuration, RequestType type, String url) {
		super(appName, configuration, type, url, parameters);
	}

	@Override
	protected List<String> getResult(URLConnection connection) throws IllegalStateException, IOException {
		return receiveList(connection);
	}

	@Override
	protected abstract void onPostExecute(List<String> result);
}
