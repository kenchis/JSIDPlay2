package jsidplay2.haendel.de.jsidplay2app.tab;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Pair;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.util.List;

import jsidplay2.haendel.de.jsidplay2app.R;
import jsidplay2.haendel.de.jsidplay2app.common.TabBase;
import jsidplay2.haendel.de.jsidplay2app.config.IConfiguration;
import jsidplay2.haendel.de.jsidplay2app.request.PhotoRequest;
import jsidplay2.haendel.de.jsidplay2app.request.TuneInfoRequest;

public class SidTab extends TabBase {

	private class MyPhotoRequest extends PhotoRequest {
		private MyPhotoRequest(String canonicalPath) {
			super(SidTab.this.appName, SidTab.this.configuration, RequestType.PHOTO, canonicalPath);
		}

		@Override
		protected void onPostExecute(byte[] photo) {
			if (photo == null) {
				return;
			}
			viewPhoto(photo);
		}
	}

	private class MyTuneInfoRequest extends TuneInfoRequest {
		private MyTuneInfoRequest(String canonicalPath) {
			super(SidTab.this.appName, SidTab.this.configuration, RequestType.INFO, canonicalPath);
		}

		public String getString(String key) {
			key = key.replaceAll("[.]", "_");
			for (Field field : R.string.class.getDeclaredFields()) {
				if (field.getName().equals(key)) {
					try {
						return activity.getString(field.getInt(null));
					} catch (IllegalArgumentException | IllegalAccessException ignored) {
					}
				}
			}
			return "???";
		}

		@Override
		protected void onPostExecute(List<Pair<String, String>> out) {
			if (out == null) {
				return;
			}
			viewTuneInfos(out);
		}
	}

	private TextView resource;
	private ImageView image;
	private TableLayout table;

	public SidTab(final Activity activity, final String appName,
				  final IConfiguration configuration, TabHost tabHost) {
		super(activity, appName, configuration, tabHost);

		TabSpec newTabSpec = tabHost.newTabSpec(SidTab.class.getSimpleName());
		tabHost.addTab(newTabSpec.setIndicator(
				activity.getString(R.string.tab_tune)).setContent(R.id.tune));

		resource = activity.findViewById(R.id.resource);
		image = activity.findViewById(R.id.image);
		table = activity.findViewById(R.id.table);
	}

	private void viewPhoto(byte[] photo) {
		Bitmap bitmap = BitmapFactory.decodeByteArray(photo, 0, photo.length);
		image.setImageBitmap(bitmap);
	}

	private void viewTuneInfos(List<Pair<String, String>> rows) {
		table.removeAllViews();
		for (Pair<String, String> r : rows) {
			TableRow tr = new TableRow(activity);
			tr.setLayoutParams(new TableRow.LayoutParams(
					TableRow.LayoutParams.MATCH_PARENT,
					TableRow.LayoutParams.MATCH_PARENT));

			TextView b = new TextView(activity);
			b.setText(r.first);
			b.setLayoutParams(new TableRow.LayoutParams(
					TableRow.LayoutParams.MATCH_PARENT,
					TableRow.LayoutParams.WRAP_CONTENT));
			tr.addView(b);

			b = new TextView(activity);
			b.setText(r.second);
			b.setSingleLine(false);
			b.setLayoutParams(new TableRow.LayoutParams(
					TableRow.LayoutParams.MATCH_PARENT,
					TableRow.LayoutParams.WRAP_CONTENT));
			tr.addView(b);
			table.addView(tr, new TableLayout.LayoutParams(
					TableLayout.LayoutParams.MATCH_PARENT,
					TableLayout.LayoutParams.WRAP_CONTENT));
		}

	}

	public String getCurrentTune() {
		return resource.getText().toString();
	}

	private void setCurrentTune(String canonicalPath) {
		resource.setText(canonicalPath);
	}

	public void requestSidDetails(String canonicalPath) {
		setCurrentTune(canonicalPath);
		new MyPhotoRequest(canonicalPath).execute();
		new MyTuneInfoRequest(canonicalPath).execute();
	}

}
