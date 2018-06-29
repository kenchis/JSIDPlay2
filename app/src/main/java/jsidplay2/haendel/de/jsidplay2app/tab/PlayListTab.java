package jsidplay2.haendel.de.jsidplay2app.tab;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.ScrollView;
import android.widget.TabHost;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import jsidplay2.haendel.de.jsidplay2app.JSIDPlay2Service.PlayListEntry;
import jsidplay2.haendel.de.jsidplay2app.MainActivity;
import jsidplay2.haendel.de.jsidplay2app.R;
import jsidplay2.haendel.de.jsidplay2app.common.TabBase;
import jsidplay2.haendel.de.jsidplay2app.common.UIHelper;
import jsidplay2.haendel.de.jsidplay2app.config.IConfiguration;

public abstract class PlayListTab extends TabBase {

	private static final String PAR_RANDOM = "random";
	private static final String DEFAULT_RANDOM = Boolean.FALSE.toString();
	
	public class PlayListUIHelper extends UIHelper {

		private PlayListUIHelper(SharedPreferences preferences) {
			super(preferences);
		}

		@Override
		protected void checkBoxUpdated(String parName, boolean newValue) {
			if (parName.equals(PAR_RANDOM)) {
				PlayListTab.this.setRandomized(newValue);
			}
		}
	}

	private ScrollView favoritesScroll;
	private TableLayout favorites;

	protected PlayListTab(final MainActivity activity, final String appName,
					   final IConfiguration configuration, TabHost tabHost) {
		super(activity, appName, configuration, tabHost);
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);
		UIHelper ui = new PlayListUIHelper(preferences);

		tabHost.addTab(tabHost.newTabSpec(PlayListTab.class.getSimpleName())
				.setIndicator(activity.getString(R.string.tab_playlist))
				.setContent(R.id.playlist));

		favoritesScroll = activity
				.findViewById(R.id.favoritesScroll);
		favorites = activity.findViewById(R.id.favorites);
		CheckBox random = activity.findViewById(R.id.random);

		ui.setupCheckBox(random, PAR_RANDOM, DEFAULT_RANDOM);

	}

	public void addRow(final PlayListEntry entry) {
		final TableRow row = new TableRow(activity);
		row.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				play(entry);
			}
		});
		row.setLayoutParams(new TableRow.LayoutParams(
				TableRow.LayoutParams.WRAP_CONTENT,
				TableRow.LayoutParams.MATCH_PARENT));

		TextView col = new TextView(activity);
		col.setText(entry.getResource());
		col.setLayoutParams(new TableRow.LayoutParams(
				TableRow.LayoutParams.MATCH_PARENT,
				TableRow.LayoutParams.WRAP_CONTENT));
		row.addView(col);
		row.setBackgroundResource(R.drawable.selector);

		favorites.addView(row, new TableLayout.LayoutParams(
				TableLayout.LayoutParams.MATCH_PARENT,
				TableLayout.LayoutParams.WRAP_CONTENT));

		favoritesScroll.post(new Runnable() {
			@Override
			public void run() {
				favoritesScroll.scrollTo(0, row.getTop());
			}
		});
	}

	public void removeLast() {
		favorites.removeViewAt(favorites.getChildCount() - 1);
	}

	public void removeAll() {
		favorites.removeAllViews();
	}

	public void gotoRow(final int currentSong) {
		if (currentSong==-1)
			return;
		favoritesScroll.post(new Runnable() {
			@Override
			public void run() {
				favoritesScroll.scrollTo(0, favorites.getChildAt(currentSong)
						.getTop());
			}
		});
	}

	protected abstract void play(PlayListEntry entry);
	
	protected abstract void setRandomized(boolean newValue);

}
