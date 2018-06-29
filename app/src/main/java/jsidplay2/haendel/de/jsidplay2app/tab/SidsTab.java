package jsidplay2.haendel.de.jsidplay2app.tab;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TabHost;

import java.io.File;
import java.io.IOException;
import java.util.List;

import jsidplay2.haendel.de.jsidplay2app.R;
import jsidplay2.haendel.de.jsidplay2app.common.TabBase;
import jsidplay2.haendel.de.jsidplay2app.config.IConfiguration;
import jsidplay2.haendel.de.jsidplay2app.request.DirectoryRequest;

public abstract class SidsTab extends TabBase {

	private class MyDirectoryRequest extends DirectoryRequest {

		MyDirectoryRequest(File dir) throws IOException {
			super(SidsTab.this.appName, SidsTab.this.configuration, RequestType.DIRECTORY, dir.getCanonicalPath().isEmpty()?"/":dir.getCanonicalPath());
		}

		@Override
		protected void onPostExecute(List<String> childs) {
			if (childs == null) {

				return;
			}
			viewDirectory(childs);
		}
	}
	private ListView directory;

	protected SidsTab(final Activity activity, final String appName,
				   final IConfiguration configuration, TabHost tabHost) {
		super(activity, appName, configuration, tabHost);
		tabHost.addTab(tabHost.newTabSpec(SidsTab.class.getSimpleName())
				.setIndicator(activity.getString(R.string.tab_sids))
				.setContent(R.id.sids));

		directory = activity.findViewById(R.id.directory);

		try {
			requestDirectory(new File("/"));
		} catch (IOException e) {
			Log.e(appName, e.getMessage(), e);
		}
	}

	private void requestDirectory(final File dir)
			throws IOException {
		new MyDirectoryRequest(dir).execute();
	}

	private void viewDirectory(List<String> childs) {
		directory.setAdapter(new ArrayAdapter<>(activity,
				android.R.layout.simple_list_item_1, childs));
		directory.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, final View view,
					int position, long id) {
				final String dirEntry = (String) parent
						.getItemAtPosition(position);
				File file = new File(dirEntry);
				try {
					if (dirEntry.endsWith("/")) {
						new MyDirectoryRequest(file).execute();
					} else if (dirEntry.endsWith(".sid")||dirEntry.endsWith(".mus")||dirEntry.endsWith(".str")) {
						showSid(file.getPath());
						tabHost.setCurrentTabByTag(SidTab.class.getSimpleName());
					} else {
						showMedia(file.getPath());
					}
				} catch (IOException e) {
					Log.e(appName, e.getMessage(), e);
				}
			}

		});
	}

	protected abstract void showMedia(String canonicalPath);
	protected abstract void showSid(String canonicalPath);

}
