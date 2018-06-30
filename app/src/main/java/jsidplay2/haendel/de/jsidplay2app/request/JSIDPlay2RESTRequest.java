package jsidplay2.haendel.de.jsidplay2app.request;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.PasswordAuthentication;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.os.AsyncTask;
import android.util.Log;
import android.util.Pair;

import jsidplay2.haendel.de.jsidplay2app.config.IConfiguration;

public abstract class JSIDPlay2RESTRequest<ResultType> extends AsyncTask<String, Void, ResultType> {

    interface IKeyLocalizer {
        /**
         * Get localized tune info name
         *
         * @param key tune info name
         * @return localized string
         */
        String getString(String key);
    }

    public enum RequestType {
        DOWNLOAD(REST_DOWNLOAD_URL), CONVERT(REST_CONVERT_URL), DIRECTORY(REST_DIRECTORY_URL), PHOTO(
                REST_PHOTO_URL), INFO(REST_INFO), FILTERS(REST_FILTERS_URL);

        private String url;

        RequestType(String url) {
            this.url = url;
        }

        public String getUrl() {
            return url;
        }
    }

    private static final String UTF_8 = "UTF-8";
    private static final String CONTEXT_ROOT = "/jsidplay2service";
    private static final String ROOT_PATH = "/JSIDPlay2REST";
    private static final String ROOT_URL = CONTEXT_ROOT + ROOT_PATH;

    private static final String REST_DOWNLOAD_URL = ROOT_URL + "/download";
    private static final String REST_CONVERT_URL = ROOT_URL + "/convert";
    private static final String REST_DIRECTORY_URL = ROOT_URL + "/directory";
    private static final String REST_PHOTO_URL = ROOT_URL + "/photo";
    private static final String REST_INFO = ROOT_URL + "/info";
    private static final String REST_FILTERS_URL = ROOT_URL + "/filters";

    private static final int RETRY_PERIOD_S = 10;

    private final String appName;
    private IConfiguration configuration;
    private String url;
    private Map<String, String> properties;

    JSIDPlay2RESTRequest(String appName, IConfiguration configuration, RequestType type, String url,
                         Map<String, String> properties) {
        this.appName = appName;
        this.configuration = configuration;
        this.url = type.getUrl() + url;
        this.properties = properties;
    }

    @Override
    protected ResultType doInBackground(String... params) {
        while (true) {
            try {
                StringBuilder query = new StringBuilder();
                if (properties != null) {
                    for (Entry<String, String> property : properties.entrySet()) {
                        query.append("&").append(property.getKey()).append("=").append(property.getValue());
                    }
                }
                URI myUri = new URI("http", null, configuration.getHostname(), Integer.valueOf(configuration.getPort()),
                        url, query.toString(), null);

                Log.d(appName, "HTTP-GET: " + myUri);

                Authenticator.setDefault(new Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(configuration.getUsername(),
                                configuration.getPassword().toCharArray());
                    }
                });
                HttpURLConnection conn = (HttpURLConnection) myUri.toURL().openConnection();
                conn.setUseCaches(false);
                conn.setRequestMethod("GET");
                int statusCode = conn.getResponseCode();
                if (statusCode == HttpURLConnection.HTTP_OK) {
                    return getResult(conn);
                } else {
                    Log.e(appName, String.format("URL: '%s', HTTP status: '%d', Retry in '%d' seconds!",
                            myUri.toString(), statusCode, RETRY_PERIOD_S));
                }
            } catch (IOException | URISyntaxException e) {
                Log.e(appName, e.getMessage(), e);
            }
            try {
                Thread.sleep(RETRY_PERIOD_S * 1000);
            } catch (InterruptedException e) {
                Log.e(appName, "Interrupted while sleeping!", e);
            }
        }
    }

    protected abstract ResultType getResult(URLConnection httpEntity) throws IllegalStateException, IOException;

    private static String[] splitJSONToken(String line, String sep) {
        String otherThanQuote = " [^\"] ";
        String quotedString = String.format(" \" %s* \" ", otherThanQuote);
        String regex = String.format("(?x) " + // enable comments,
                        // ignore white spaces
                        sep + "                         " + // match a comma
                        "(?=                       " + // start positive look
                        // ahead
                        "  (                       " + // start group 1
                        "    %s*                   " + // match 'otherThanQuote'
                        // zero or more times
                        "    %s                    " + // match 'quotedString'
                        "  )*                      " + // end group 1 and repeat
                        // it zero or more times
                        "  %s*                     " + // match 'otherThanQuote'
                        "  $                       " + // match the end of the
                        // string
                        ")                         ", // stop positive look
                // ahead
                otherThanQuote, quotedString, otherThanQuote);
        return line.split(regex, -1);
    }


    ArrayList<String> receiveList(URLConnection connection) throws IllegalStateException, IOException {
        ArrayList<String> result = new ArrayList<>();
        InputStream content = connection.getInputStream();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] b = new byte[4096];
        int n;
        do {
            n = content.read(b);
            if (n > 0)
                out.write(b, 0, n);
        } while (n > 0);

        String trimmed = out.toString(UTF_8).trim();
        String line = trimmed.substring(1, trimmed.length() - 1);

        for (String child : splitJSONToken(line, ",")) {
            child = child.trim();
            if (child.length() > 2) {
                result.add(child.substring(1, child.length() - 1));
            }
        }
        return result;
    }

    List<Pair<String, String>> receiveListOfKeyValues(URLConnection connection, IKeyLocalizer localizer)
            throws IllegalStateException, IOException {
        List<Pair<String, String>> result = new ArrayList<>();
        InputStream content = connection.getInputStream();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] b = new byte[4096];
        int n;
        do {
            n = content.read(b);
            if (n > 0)
                out.write(b, 0, n);
        } while (n > 0);

        String trimmed = out.toString(UTF_8).trim();

        String mapToken = trimmed.substring(1, trimmed.length() - 1);
        String[] line = JSIDPlay2RESTRequest.splitJSONToken(mapToken, ",");

        for (String mapEntryToken : line) {
            String tuneInfoName = null;
            String tuneInfoValue = "";
            for (String nextToken : splitJSONToken(mapEntryToken, ":")) {
                nextToken = nextToken.trim();
                String keyOrValue = nextToken.substring(1, nextToken.length() - 1).replaceAll("\\\\n", "\n");
                if (tuneInfoName == null) {
                    tuneInfoName = localizer.getString(keyOrValue);
                } else {
                    tuneInfoValue = keyOrValue;
                }
            }
            if (!tuneInfoValue.isEmpty()) {
                result.add(new Pair<>(tuneInfoName, tuneInfoValue));
            }
        }
        return result;
    }
}