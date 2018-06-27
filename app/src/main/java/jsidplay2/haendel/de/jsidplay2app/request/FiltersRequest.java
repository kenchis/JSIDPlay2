package jsidplay2.haendel.de.jsidplay2app.request;

import java.io.IOException;
import java.net.URLConnection;
import java.util.List;

import jsidplay2.haendel.de.jsidplay2app.config.IConfiguration;
import jsidplay2.haendel.de.jsidplay2app.config.IConfiguration;

public class FiltersRequest extends JSIDPlay2RESTRequest<List<String>> {

	public FiltersRequest(String appName, IConfiguration configuration, RequestType type, String url) {
		super(appName, configuration, type, url, null);
	}

	@Override
	protected List<String> getResult(URLConnection connection) throws IllegalStateException, IOException {
		return receiveList(connection);
	}
}
