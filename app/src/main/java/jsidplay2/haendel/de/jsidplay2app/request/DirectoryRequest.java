package jsidplay2.haendel.de.jsidplay2app.request;

import java.io.IOException;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jsidplay2.haendel.de.jsidplay2app.config.IConfiguration;

public abstract class DirectoryRequest extends JSIDPlay2RESTRequest<List<String>> {

	private static final String FILTER_PAR = "filter";
	private static final String TUNE_FILTER = ".%2A%5C.%28sid%7Cdat%7Cmus%7Cstr%7Cmp3%7Cmp4%7Cjpg%7Cprg%7Cd64%29%24";

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
