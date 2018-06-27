package jsidplay2.haendel.de.jsidplay2app.common;

import android.app.Activity;
import android.widget.TabHost;
import jsidplay2.haendel.de.jsidplay2app.config.IConfiguration;
import jsidplay2.haendel.de.jsidplay2app.config.IConfiguration;

public class TabBase {

	protected Activity activity;
	protected String appName;
	protected IConfiguration configuration;
	protected TabHost tabHost;

	protected TabBase(final Activity activity, final String appName,
			final IConfiguration configuration, final TabHost tabHost) {
		this.activity = activity;
		this.appName = appName;
		this.configuration = configuration;
		this.tabHost = tabHost;
	}
}
