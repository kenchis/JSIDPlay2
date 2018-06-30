package jsidplay2.haendel.de.jsidplay2app.tab;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.EditText;
import android.widget.TabHost;
import jsidplay2.haendel.de.jsidplay2app.R;
import jsidplay2.haendel.de.jsidplay2app.common.TabBase;
import jsidplay2.haendel.de.jsidplay2app.common.UIHelper;
import jsidplay2.haendel.de.jsidplay2app.config.IConfiguration;

public class GeneralTab extends TabBase {

	private static final String PAR_HOSTNAME = "hostname";
	private static final String PAR_PORT = "port";
	private static final String PAR_USERNAME = "username";
	private static final String PAR_PASSWORD = "password";

	private static final String DEFAULT_HOSTNAME = "haendel.ddns.net";
	private static final String DEFAULT_PORT = "8080";
	private static final String DEFAULT_USERNAME = "jsidplay2";
	private static final String DEFAULT_PASSWORD = "jsidplay2!";

	protected class GeneralUIHelper extends UIHelper {
		private GeneralUIHelper(final SharedPreferences preferences) {
			super(preferences);
		}

		@Override
		protected void editTextUpdated(final String parName,
				final String newValue) {
			switch (parName) {
				case PAR_HOSTNAME:
					configuration.setHostname(newValue);
					break;
				case PAR_PORT:
					configuration.setPort(newValue);
					break;
				case PAR_USERNAME:
					configuration.setUsername(newValue);
					break;
				case PAR_PASSWORD:
					configuration.setPassword(newValue);
					break;
			}
		}
	}

	public GeneralTab(final Activity activity, final String appName,
					  final IConfiguration configuration, TabHost tabHost) {
		super(activity, appName, configuration, tabHost);
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);
		UIHelper ui = new GeneralUIHelper(preferences);
		tabHost.addTab(tabHost.newTabSpec(GeneralTab.class.getSimpleName())
				.setIndicator(activity.getString(R.string.tab_connection))
				.setContent(R.id.general));

		EditText hostname = activity.findViewById(R.id.hostname);
		EditText port = activity.findViewById(R.id.port);
		EditText username = activity.findViewById(R.id.username);
		EditText password = activity.findViewById(R.id.password);

		ui.setupEditText(hostname, PAR_HOSTNAME, DEFAULT_HOSTNAME);
		ui.setupEditText(port, PAR_PORT, DEFAULT_PORT);
		ui.setupEditText(username, PAR_USERNAME, DEFAULT_USERNAME);
		ui.setupEditText(password, PAR_PASSWORD, DEFAULT_PASSWORD);

	}

}
