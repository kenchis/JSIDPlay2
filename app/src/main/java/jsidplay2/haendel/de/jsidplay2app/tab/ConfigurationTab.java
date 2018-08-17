package jsidplay2.haendel.de.jsidplay2app.tab;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import jsidplay2.haendel.de.jsidplay2app.R;
import jsidplay2.haendel.de.jsidplay2app.common.TabBase;
import jsidplay2.haendel.de.jsidplay2app.common.UIHelper;
import jsidplay2.haendel.de.jsidplay2app.config.IConfiguration;
import jsidplay2.haendel.de.jsidplay2app.request.FiltersRequest;

import static jsidplay2.haendel.de.jsidplay2app.config.IConfiguration.DEFAULT_SECURE_PORT;
import static jsidplay2.haendel.de.jsidplay2app.config.IConfiguration.DECIMATE;
import static jsidplay2.haendel.de.jsidplay2app.config.IConfiguration.DEFAULT_BUFFER_SIZE;
import static jsidplay2.haendel.de.jsidplay2app.config.IConfiguration.DEFAULT_BUFFER_SIZE_WLAN;
import static jsidplay2.haendel.de.jsidplay2app.config.IConfiguration.DEFAULT_CBR;
import static jsidplay2.haendel.de.jsidplay2app.config.IConfiguration.DEFAULT_CONNECTION_TYPE;
import static jsidplay2.haendel.de.jsidplay2app.config.IConfiguration.DEFAULT_DIGI_BOOSTED_8580;
import static jsidplay2.haendel.de.jsidplay2app.config.IConfiguration.DEFAULT_ENABLE_DATABASE;
import static jsidplay2.haendel.de.jsidplay2app.config.IConfiguration.DEFAULT_FADE_IN;
import static jsidplay2.haendel.de.jsidplay2app.config.IConfiguration.DEFAULT_FADE_OUT;
import static jsidplay2.haendel.de.jsidplay2app.config.IConfiguration.DEFAULT_FAKE_STEREO;
import static jsidplay2.haendel.de.jsidplay2app.config.IConfiguration.DEFAULT_FILTER_6581;
import static jsidplay2.haendel.de.jsidplay2app.config.IConfiguration.DEFAULT_FILTER_8580;
import static jsidplay2.haendel.de.jsidplay2app.config.IConfiguration.DEFAULT_LOOP;
import static jsidplay2.haendel.de.jsidplay2app.config.IConfiguration.DEFAULT_MAIN_BALANCE;
import static jsidplay2.haendel.de.jsidplay2app.config.IConfiguration.DEFAULT_MAIN_DELAY;
import static jsidplay2.haendel.de.jsidplay2app.config.IConfiguration.DEFAULT_MAIN_VOLUME;
import static jsidplay2.haendel.de.jsidplay2app.config.IConfiguration.DEFAULT_PLAY_LENGTH;
import static jsidplay2.haendel.de.jsidplay2app.config.IConfiguration.DEFAULT_PORT;
import static jsidplay2.haendel.de.jsidplay2app.config.IConfiguration.DEFAULT_RESIDFP_FILTER_6581;
import static jsidplay2.haendel.de.jsidplay2app.config.IConfiguration.DEFAULT_RESIDFP_FILTER_8580;
import static jsidplay2.haendel.de.jsidplay2app.config.IConfiguration.DEFAULT_SECOND_BALANCE;
import static jsidplay2.haendel.de.jsidplay2app.config.IConfiguration.DEFAULT_SECOND_DELAY;
import static jsidplay2.haendel.de.jsidplay2app.config.IConfiguration.DEFAULT_SECOND_VOLUME;
import static jsidplay2.haendel.de.jsidplay2app.config.IConfiguration.DEFAULT_SINGLE_SONG;
import static jsidplay2.haendel.de.jsidplay2app.config.IConfiguration.DEFAULT_THIRD_BALANCE;
import static jsidplay2.haendel.de.jsidplay2app.config.IConfiguration.DEFAULT_THIRD_DELAY;
import static jsidplay2.haendel.de.jsidplay2app.config.IConfiguration.DEFAULT_THIRD_VOLUME;
import static jsidplay2.haendel.de.jsidplay2app.config.IConfiguration.DEFAULT_VBR;
import static jsidplay2.haendel.de.jsidplay2app.config.IConfiguration.HTTP;
import static jsidplay2.haendel.de.jsidplay2app.config.IConfiguration.HTTPS;
import static jsidplay2.haendel.de.jsidplay2app.config.IConfiguration.MOS6581;
import static jsidplay2.haendel.de.jsidplay2app.config.IConfiguration.MOS8580;
import static jsidplay2.haendel.de.jsidplay2app.config.IConfiguration.PAR_BUFFER_SIZE;
import static jsidplay2.haendel.de.jsidplay2app.config.IConfiguration.PAR_BUFFER_SIZE_WLAN;
import static jsidplay2.haendel.de.jsidplay2app.config.IConfiguration.PAR_CBR;
import static jsidplay2.haendel.de.jsidplay2app.config.IConfiguration.PAR_CONNECTION_TYPE;
import static jsidplay2.haendel.de.jsidplay2app.config.IConfiguration.PAR_DEFAULT_MODEL;
import static jsidplay2.haendel.de.jsidplay2app.config.IConfiguration.PAR_DEFAULT_PLAY_LENGTH;
import static jsidplay2.haendel.de.jsidplay2app.config.IConfiguration.PAR_DIGI_BOOSTED_8580;
import static jsidplay2.haendel.de.jsidplay2app.config.IConfiguration.PAR_EMULATION;
import static jsidplay2.haendel.de.jsidplay2app.config.IConfiguration.PAR_ENABLE_DATABASE;
import static jsidplay2.haendel.de.jsidplay2app.config.IConfiguration.PAR_FADE_IN;
import static jsidplay2.haendel.de.jsidplay2app.config.IConfiguration.PAR_FADE_OUT;
import static jsidplay2.haendel.de.jsidplay2app.config.IConfiguration.PAR_FAKE_STEREO;
import static jsidplay2.haendel.de.jsidplay2app.config.IConfiguration.PAR_FILTER_6581;
import static jsidplay2.haendel.de.jsidplay2app.config.IConfiguration.PAR_FILTER_8580;
import static jsidplay2.haendel.de.jsidplay2app.config.IConfiguration.PAR_FREQUENCY;
import static jsidplay2.haendel.de.jsidplay2app.config.IConfiguration.PAR_LOOP;
import static jsidplay2.haendel.de.jsidplay2app.config.IConfiguration.PAR_MAIN_BALANCE;
import static jsidplay2.haendel.de.jsidplay2app.config.IConfiguration.PAR_MAIN_DELAY;
import static jsidplay2.haendel.de.jsidplay2app.config.IConfiguration.PAR_MAIN_VOLUME;
import static jsidplay2.haendel.de.jsidplay2app.config.IConfiguration.PAR_PORT;
import static jsidplay2.haendel.de.jsidplay2app.config.IConfiguration.PAR_RESIDFP_FILTER_6581;
import static jsidplay2.haendel.de.jsidplay2app.config.IConfiguration.PAR_RESIDFP_FILTER_8580;
import static jsidplay2.haendel.de.jsidplay2app.config.IConfiguration.PAR_RESIDFP_STEREO_FILTER_6581;
import static jsidplay2.haendel.de.jsidplay2app.config.IConfiguration.PAR_RESIDFP_STEREO_FILTER_8580;
import static jsidplay2.haendel.de.jsidplay2app.config.IConfiguration.PAR_RESIDFP_THIRD_FILTER_6581;
import static jsidplay2.haendel.de.jsidplay2app.config.IConfiguration.PAR_RESIDFP_THIRD_FILTER_8580;
import static jsidplay2.haendel.de.jsidplay2app.config.IConfiguration.PAR_SAMPLING_METHOD;
import static jsidplay2.haendel.de.jsidplay2app.config.IConfiguration.PAR_SECOND_BALANCE;
import static jsidplay2.haendel.de.jsidplay2app.config.IConfiguration.PAR_SECOND_DELAY;
import static jsidplay2.haendel.de.jsidplay2app.config.IConfiguration.PAR_SECOND_VOLUME;
import static jsidplay2.haendel.de.jsidplay2app.config.IConfiguration.PAR_SINGLE_SONG;
import static jsidplay2.haendel.de.jsidplay2app.config.IConfiguration.PAR_STEREO_FILTER_6581;
import static jsidplay2.haendel.de.jsidplay2app.config.IConfiguration.PAR_STEREO_FILTER_8580;
import static jsidplay2.haendel.de.jsidplay2app.config.IConfiguration.PAR_THIRD_BALANCE;
import static jsidplay2.haendel.de.jsidplay2app.config.IConfiguration.PAR_THIRD_DELAY;
import static jsidplay2.haendel.de.jsidplay2app.config.IConfiguration.PAR_THIRD_FILTER_6581;
import static jsidplay2.haendel.de.jsidplay2app.config.IConfiguration.PAR_THIRD_FILTER_8580;
import static jsidplay2.haendel.de.jsidplay2app.config.IConfiguration.PAR_THIRD_VOLUME;
import static jsidplay2.haendel.de.jsidplay2app.config.IConfiguration.PAR_VBR;
import static jsidplay2.haendel.de.jsidplay2app.config.IConfiguration.RESAMPLE;
import static jsidplay2.haendel.de.jsidplay2app.config.IConfiguration.RESID;
import static jsidplay2.haendel.de.jsidplay2app.config.IConfiguration.RESIDFP;
import static jsidplay2.haendel.de.jsidplay2app.config.IConfiguration._44100;
import static jsidplay2.haendel.de.jsidplay2app.config.IConfiguration._48000;
import static jsidplay2.haendel.de.jsidplay2app.config.IConfiguration._96000;
public class ConfigurationTab extends TabBase {
	private static final String PREFIX_FILTER_6581 = "RESID_MOS6581_";
	private static final String PREFIX_FILTER_8580 = "RESID_MOS8580_";
	private static final String PREFIX_RESIDFP_FILTER_6581 = "RESIDFP_MOS6581_";
	private static final String PREFIX_RESIDFP_FILTER_8580 = "RESIDFP_MOS8580_";

	protected class ConfigurationUIHelper extends UIHelper {

		private ConfigurationUIHelper(SharedPreferences preferences) {
			super(preferences);
		}

		@Override
		protected void spinnerUpdated(final String parName,
				final String newValue) {
			switch (parName) {
				case PAR_EMULATION:
					boolean isReSid = newValue.equals("RESID");
					updateFiltersVisibility(
							new View[]{filter6581txt, filter6581, filter8580txt,
									filter8580, stereoFilter6581txt,
									stereoFilter6581, stereoFilter8580txt,
									stereoFilter8580, thirdFilter6581txt,
									thirdFilter6581, thirdFilter8580txt,
									thirdFilter8580}, isReSid);

					boolean isReSidFp = newValue.equals("RESIDFP");
					updateFiltersVisibility(new View[]{reSIDfpFilter6581txt,
							reSIDfpFilter6581, reSIDfpFilter8580txt,
							reSIDfpFilter8580, reSIDfpStereoFilter6581txt,
							reSIDfpStereoFilter6581, reSIDfpStereoFilter8580txt,
							reSIDfpStereoFilter8580, reSIDfpThirdFilter6581txt,
							reSIDfpThirdFilter6581, reSIDfpThirdFilter8580txt,
							reSIDfpThirdFilter8580}, isReSidFp);
					configuration.setDefaultEmulation(newValue);
					break;
				case PAR_DEFAULT_MODEL:
					configuration.setDefaultModel(newValue);
					break;
				case PAR_FREQUENCY:
					configuration.setFrequency(newValue);
					break;
				case PAR_SAMPLING_METHOD:
					configuration.setSamplingMethod(newValue);
					break;
				case PAR_CONNECTION_TYPE:
					configuration.setConnectionType(newValue);
					if (newValue.equals(HTTPS)) {
						port.setText(DEFAULT_SECURE_PORT);
					} else {
						port.setText(DEFAULT_PORT);
					}
					break;
				case PAR_FILTER_6581:
					configuration.setFilter6581(newValue);
					break;
				case PAR_FILTER_8580:
					configuration.setFilter8580(newValue);
					break;
				case PAR_RESIDFP_FILTER_6581:
					configuration.setReSIDfpFilter6581(newValue);
					break;
				case PAR_RESIDFP_FILTER_8580:
					configuration.setReSIDfpFilter8580(newValue);
					break;
				case PAR_STEREO_FILTER_6581:
					configuration.setStereoFilter6581(newValue);
					break;
				case PAR_STEREO_FILTER_8580:
					configuration.setStereoFilter8580(newValue);
					break;
				case PAR_RESIDFP_STEREO_FILTER_6581:
					configuration.setReSIDfpStereoFilter6581(newValue);
					break;
				case PAR_RESIDFP_STEREO_FILTER_8580:
					configuration.setReSIDfpStereoFilter8580(newValue);
					break;
				case PAR_THIRD_FILTER_6581:
					configuration.setThirdFilter6581(newValue);
					break;
				case PAR_THIRD_FILTER_8580:
					configuration.setThirdFilter8580(newValue);
					break;
				case PAR_RESIDFP_THIRD_FILTER_6581:
					configuration.setReSIDfpThirdFilter6581(newValue);
					break;
				case PAR_RESIDFP_THIRD_FILTER_8580:
					configuration.setReSIDfpThirdFilter8580(newValue);
					break;
				case PAR_MAIN_VOLUME:
					configuration.setMainVolume(newValue);
					break;
				case PAR_SECOND_VOLUME:
					configuration.setSecondVolume(newValue);
					break;
				case PAR_THIRD_VOLUME:
					configuration.setThirdVolume(newValue);
					break;

				case PAR_MAIN_BALANCE:
					configuration.setMainBalance(newValue);
					break;
				case PAR_SECOND_BALANCE:
					configuration.setSecondBalance(newValue);
					break;
				case PAR_THIRD_BALANCE:
					configuration.setThirdBalance(newValue);
					break;

				case PAR_MAIN_DELAY:
					configuration.setMainDelay(newValue);
					break;
				case PAR_SECOND_DELAY:
					configuration.setSecondDelay(newValue);
					break;
				case PAR_THIRD_DELAY:
					configuration.setThirdDelay(newValue);
					break;
			}
		}


		@Override
		protected void editTextUpdated(String parName, String newValue) {
			switch (parName) {
				case PAR_BUFFER_SIZE:
					configuration.setBufferSize(newValue);
					break;
				case PAR_BUFFER_SIZE_WLAN:
					configuration.setBufferSizeWlan(newValue);
					break;
				case PAR_DEFAULT_PLAY_LENGTH:
					configuration.setDefaultLength(newValue);
					break;
				case PAR_FADE_IN:
					configuration.setFadeIn(newValue);
					break;
				case PAR_FADE_OUT:
					configuration.setFadeOut(newValue);
					break;
				case PAR_CBR:
					configuration.setCbr(newValue);
					break;
				case PAR_VBR:
					configuration.setVbr(newValue);
					break;
				case PAR_PORT:
					configuration.setPort(newValue);
					break;
			}
		}

		@Override
		protected void checkBoxUpdated(String parName, boolean newValue) {
			switch (parName) {
				case PAR_DIGI_BOOSTED_8580:
					configuration.setDigiBoosted8580(newValue);
					break;
				case PAR_ENABLE_DATABASE:
					configuration.setEnableDatabase(newValue);
					break;
				case PAR_LOOP:
					configuration.setLoop(newValue);
					break;
				case PAR_FAKE_STEREO:
					configuration.setFakeStereo(newValue);
					break;
				case PAR_SINGLE_SONG:
					configuration.setSingleSong(newValue);
					break;
			}
		}

		private void updateFiltersVisibility(View[] views, boolean visible) {
			for (View view : views) {
				view.setVisibility(visible ? View.VISIBLE : View.GONE);
			}
		}

	}

    private class MyFiltersRequest extends FiltersRequest {
        private MyFiltersRequest() {
            super(ConfigurationTab.this.appName, ConfigurationTab.this.configuration, RequestType.FILTERS, "");
        }

        @Override
        protected void onPostExecute(List<String> filters) {
            if (filters == null) {
                return;
            }
            List<String> filter6581List = determineFilterList(filters,
                    PREFIX_FILTER_6581);
            List<String> filter8580List = determineFilterList(filters,
                    PREFIX_FILTER_8580);
            List<String> reSidFpFilter6581List = determineFilterList(
                    filters, PREFIX_RESIDFP_FILTER_6581);
            List<String> reSidFpFilter8580List = determineFilterList(
                    filters, PREFIX_RESIDFP_FILTER_8580);

            ui.setupSpinner(activity, filter6581, filter6581List
                    .toArray(new String[0]), PAR_FILTER_6581, preferences
                    .getString(PAR_FILTER_6581, DEFAULT_FILTER_6581));
            ui.setupSpinner(activity, filter8580, filter8580List
                    .toArray(new String[0]), PAR_FILTER_8580, preferences
                    .getString(PAR_FILTER_8580, DEFAULT_FILTER_8580));
            ui.setupSpinner(activity, reSIDfpFilter6581,
                    reSidFpFilter6581List.toArray(new String[0]),
                    PAR_RESIDFP_FILTER_6581, preferences.getString(
                            PAR_RESIDFP_FILTER_6581,
                            DEFAULT_RESIDFP_FILTER_6581));
            ui.setupSpinner(activity, reSIDfpFilter8580,
                    reSidFpFilter8580List.toArray(new String[0]),
                    PAR_RESIDFP_FILTER_8580, preferences.getString(
                            PAR_RESIDFP_FILTER_8580,
                            DEFAULT_RESIDFP_FILTER_8580));

            ui.setupSpinner(activity, stereoFilter6581, filter6581List
                            .toArray(new String[0]), PAR_STEREO_FILTER_6581,
                    preferences.getString(PAR_STEREO_FILTER_6581,
                            DEFAULT_FILTER_6581));
            ui.setupSpinner(activity, stereoFilter8580, filter8580List
                            .toArray(new String[0]), PAR_STEREO_FILTER_8580,
                    preferences.getString(PAR_STEREO_FILTER_8580,
                            DEFAULT_FILTER_8580));
            ui.setupSpinner(activity, reSIDfpStereoFilter6581,
                    reSidFpFilter6581List.toArray(new String[0]),
                    PAR_RESIDFP_STEREO_FILTER_6581, preferences.getString(
                            PAR_RESIDFP_STEREO_FILTER_6581,
                            DEFAULT_RESIDFP_FILTER_6581));
            ui.setupSpinner(activity, reSIDfpStereoFilter8580,
                    reSidFpFilter8580List.toArray(new String[0]),
                    PAR_RESIDFP_STEREO_FILTER_8580, preferences.getString(
                            PAR_RESIDFP_STEREO_FILTER_8580,
                            DEFAULT_RESIDFP_FILTER_8580));


            ui.setupSpinner(activity, thirdFilter6581, filter6581List
                            .toArray(new String[0]), PAR_THIRD_FILTER_6581,
                    preferences.getString(PAR_THIRD_FILTER_6581,
                            DEFAULT_FILTER_6581));
            ui.setupSpinner(activity, thirdFilter8580, filter8580List
                            .toArray(new String[0]), PAR_THIRD_FILTER_8580,
                    preferences.getString(PAR_THIRD_FILTER_8580,
                            DEFAULT_FILTER_8580));
            ui.setupSpinner(activity, reSIDfpThirdFilter6581,
                    reSidFpFilter6581List.toArray(new String[0]),
                    PAR_RESIDFP_THIRD_FILTER_6581, preferences.getString(
                            PAR_RESIDFP_THIRD_FILTER_6581,
                            DEFAULT_RESIDFP_FILTER_6581));
            ui.setupSpinner(activity, reSIDfpThirdFilter8580,
                    reSidFpFilter8580List.toArray(new String[0]),
                    PAR_RESIDFP_THIRD_FILTER_8580, preferences.getString(
                            PAR_RESIDFP_THIRD_FILTER_8580,
                            DEFAULT_RESIDFP_FILTER_8580));

			ui.setupSpinner(activity, mainVolume,
					new String[] {"-6.0","-5.0","-4.0","-3.0","-2.0","-1.0","0.0","1.0","2.0","3.0","4.0","5.0","6.0"},
					PAR_MAIN_VOLUME, preferences.getString(
							PAR_MAIN_VOLUME,
							DEFAULT_MAIN_VOLUME));
			ui.setupSpinner(activity, secondVolume,
					new String[] {"-6.0","-5.0","-4.0","-3.0","-2.0","-1.0","0.0","1.0","2.0","3.0","4.0","5.0","6.0"},
					PAR_SECOND_VOLUME, preferences.getString(
							PAR_SECOND_VOLUME,
							DEFAULT_SECOND_VOLUME));
			ui.setupSpinner(activity, thirdVolume,
					new String[] {"-6.0","-5.0","-4.0","-3.0","-2.0","-1.0","0.0","1.0","2.0","3.0","4.0","5.0","6.0"},
					PAR_THIRD_VOLUME, preferences.getString(
							PAR_THIRD_VOLUME,
							DEFAULT_THIRD_VOLUME));

			ui.setupSpinner(activity, mainBalance,
					new String[] {"0.0","0.1","0.2","0.3","0.4","0.5","0.6","0.7","0.8","0.9","1.0"},
					PAR_MAIN_BALANCE, preferences.getString(
							PAR_MAIN_BALANCE,
							DEFAULT_MAIN_BALANCE));
			ui.setupSpinner(activity, secondBalance,
					new String[] {"0.0","0.1","0.2","0.3","0.4","0.5","0.6","0.7","0.8","0.9","1.0"},
					PAR_SECOND_BALANCE, preferences.getString(
							PAR_SECOND_BALANCE,
							DEFAULT_SECOND_BALANCE));
			ui.setupSpinner(activity, thirdBalance,
					new String[] {"0.0","0.1","0.2","0.3","0.4","0.5","0.6","0.7","0.8","0.9","1.0"},
					PAR_THIRD_BALANCE, preferences.getString(
							PAR_THIRD_BALANCE,
							DEFAULT_THIRD_BALANCE));

			ui.setupSpinner(activity, mainDelay,
					new String[] {"0","10","20","30","40","50","60","70","80","90","100"},
					PAR_MAIN_DELAY, preferences.getString(
							PAR_MAIN_DELAY,
							DEFAULT_MAIN_DELAY));
			ui.setupSpinner(activity, secondDelay,
					new String[] {"0","10","20","30","40","50","60","70","80","90","100"},
					PAR_SECOND_DELAY, preferences.getString(
							PAR_SECOND_DELAY,
							DEFAULT_SECOND_DELAY));
			ui.setupSpinner(activity, thirdDelay,
					new String[] {"0","10","20","30","40","50","60","70","80","90","100"},
					PAR_THIRD_DELAY, preferences.getString(
							PAR_THIRD_DELAY,
							DEFAULT_THIRD_DELAY));

		}
    }

	private Spinner filter6581, filter8580, reSIDfpFilter6581,
			reSIDfpFilter8580;
	private TextView filter6581txt, filter8580txt, reSIDfpFilter6581txt,
			reSIDfpFilter8580txt;

	private Spinner stereoFilter6581;
	private Spinner stereoFilter8580;
	private Spinner reSIDfpStereoFilter6581;
	private Spinner reSIDfpStereoFilter8580;
	private Spinner thirdFilter6581;
	private Spinner thirdFilter8580;
	private Spinner reSIDfpThirdFilter6581;
	private Spinner reSIDfpThirdFilter8580;

	private Spinner mainVolume,secondVolume,thirdVolume;
	private Spinner mainBalance,secondBalance,thirdBalance;
	private Spinner mainDelay,secondDelay,thirdDelay;

	private TextView stereoFilter6581txt, stereoFilter8580txt,
	reSIDfpStereoFilter6581txt, reSIDfpStereoFilter8580txt,
	thirdFilter6581txt, thirdFilter8580txt,
			reSIDfpThirdFilter6581txt, reSIDfpThirdFilter8580txt;
	private EditText port;

	private SharedPreferences preferences;
	private UIHelper ui;

	public ConfigurationTab(final Activity activity, final String appName,
			final IConfiguration configuration, TabHost tabHost) {
		super(activity, appName, configuration, tabHost);
		preferences = PreferenceManager.getDefaultSharedPreferences(activity);
		ui = new ConfigurationUIHelper(preferences);

		tabHost.addTab(tabHost
				.newTabSpec(ConfigurationTab.class.getSimpleName())
				.setIndicator(activity.getString(R.string.tab_cfg))
				.setContent(R.id.settings));

		EditText bufferSize = activity.findViewById(R.id.bufferSize);
		EditText bufferSizeWlan = activity.findViewById(R.id.bufferSizeWlan);
		EditText defaultLength = activity.findViewById(R.id.defaultLength);
		port = activity.findViewById(R.id.port);
		EditText fadeIn = activity.findViewById(R.id.fadeIn);
		EditText fadeOut = activity.findViewById(R.id.fadeOut);
		CheckBox enableDatabase = activity.findViewById(R.id.enableDatabase);
		CheckBox singleSong = activity.findViewById(R.id.singleSong);
		CheckBox loop = activity.findViewById(R.id.loop);
		CheckBox fakeStereo = activity.findViewById(R.id.fakeStereo);
		CheckBox digiBoosted8580 = activity
				.findViewById(R.id.digiBoosted8580);
		Spinner emulation = activity.findViewById(R.id.emulation);
		Spinner defaultModel = activity.findViewById(R.id.defaultModel);
		Spinner samplingMethod = activity.findViewById(R.id.samplingMethod);
		Spinner frequency = activity.findViewById(R.id.frequency);

		Spinner connectionType = activity.findViewById(R.id.connectionType);

		filter6581 = activity.findViewById(R.id.filter6581);
		filter6581txt = activity.findViewById(R.id.filter6581txt);
		filter8580 = activity.findViewById(R.id.filter8580);
		filter8580txt = activity.findViewById(R.id.filter8580txt);
		reSIDfpFilter6581 = activity
				.findViewById(R.id.reSIDfpFilter6581);
		reSIDfpFilter6581txt = activity
				.findViewById(R.id.reSIDfpFilter6581txt);
		reSIDfpFilter8580 = activity
				.findViewById(R.id.reSIDfpFilter8580);
		reSIDfpFilter8580txt = activity
				.findViewById(R.id.reSIDfpFilter8580txt);

		stereoFilter6581 = activity
				.findViewById(R.id.stereoFilter6581);
		stereoFilter6581txt = activity
				.findViewById(R.id.stereoFilter6581txt);
		stereoFilter8580 = activity
				.findViewById(R.id.stereoFilter8580);
		stereoFilter8580txt = activity
				.findViewById(R.id.stereoFilter8580txt);
		reSIDfpStereoFilter6581 = activity
				.findViewById(R.id.reSIDfpStereoFilter6581);
		reSIDfpStereoFilter6581txt = activity
				.findViewById(R.id.reSIDfpStereoFilter6581txt);
		reSIDfpStereoFilter8580 = activity
				.findViewById(R.id.reSIDfpStereoFilter8580);
		reSIDfpStereoFilter8580txt = activity
				.findViewById(R.id.reSIDfpStereoFilter8580txt);

		mainVolume = activity
				.findViewById(R.id.mainVolume);
		secondVolume = activity
				.findViewById(R.id.secondVolume);
		thirdVolume = activity
				.findViewById(R.id.thirdVolume);

		mainBalance = activity
				.findViewById(R.id.mainBalance);
		secondBalance = activity
				.findViewById(R.id.secondBalance);
		thirdBalance = activity
				.findViewById(R.id.thirdBalance);

		mainDelay = activity
				.findViewById(R.id.mainDelay);
		secondDelay = activity
				.findViewById(R.id.secondDelay);
		thirdDelay = activity
				.findViewById(R.id.thirdDelay);

		thirdFilter6581 = activity
				.findViewById(R.id.thirdFilter6581);
		thirdFilter6581txt = activity
				.findViewById(R.id.thirdFilter6581txt);
		thirdFilter8580 = activity
				.findViewById(R.id.thirdFilter8580);
		thirdFilter8580txt = activity
				.findViewById(R.id.thirdFilter8580txt);
		reSIDfpThirdFilter6581 = activity
				.findViewById(R.id.reSIDfpThirdFilter6581);
		reSIDfpThirdFilter6581txt = activity
				.findViewById(R.id.reSIDfpThirdFilter6581txt);
		reSIDfpThirdFilter8580 = activity
				.findViewById(R.id.reSIDfpThirdFilter8580);
		reSIDfpThirdFilter8580txt = activity
				.findViewById(R.id.reSIDfpThirdFilter8580txt);

		EditText cbr = activity.findViewById(R.id.cbr);
		EditText vbr = activity.findViewById(R.id.vbr);

		ui.setupEditText(bufferSize, PAR_BUFFER_SIZE, DEFAULT_BUFFER_SIZE);

		ui.setupEditText(bufferSizeWlan, PAR_BUFFER_SIZE_WLAN, DEFAULT_BUFFER_SIZE_WLAN);

		ui.setupEditText(defaultLength, PAR_DEFAULT_PLAY_LENGTH,
				DEFAULT_PLAY_LENGTH);

		ui.setupEditText(fadeIn, PAR_FADE_IN,
				DEFAULT_FADE_IN);
		ui.setupEditText(fadeOut, PAR_FADE_OUT,
				DEFAULT_FADE_OUT);

		ui.setupCheckBox(enableDatabase, PAR_ENABLE_DATABASE,
				DEFAULT_ENABLE_DATABASE);
		ui.setupCheckBox(singleSong, PAR_SINGLE_SONG, DEFAULT_SINGLE_SONG);
		ui.setupCheckBox(loop, PAR_LOOP, DEFAULT_LOOP);
		ui.setupCheckBox(fakeStereo, PAR_FAKE_STEREO, DEFAULT_FAKE_STEREO);
		ui.setupCheckBox(digiBoosted8580, PAR_DIGI_BOOSTED_8580,
				DEFAULT_DIGI_BOOSTED_8580);

		ui.setupSpinner(activity, emulation, new String[] { RESID, RESIDFP },
				PAR_EMULATION, RESIDFP);
		ui.setupSpinner(activity, defaultModel,
				new String[] { MOS6581, MOS8580 }, PAR_DEFAULT_MODEL, MOS6581);
		ui.setupSpinner(activity, samplingMethod, new String[] { DECIMATE,
				RESAMPLE }, PAR_SAMPLING_METHOD, RESAMPLE);
		ui.setupSpinner(activity, frequency, new String[] { _44100, _48000,
				_96000 }, PAR_FREQUENCY, _48000);

		ui.setupSpinner(activity, connectionType, new String[] { HTTP,  HTTPS }, PAR_CONNECTION_TYPE, DEFAULT_CONNECTION_TYPE);
		ui.setupEditText(port, PAR_PORT, DEFAULT_PORT);

		ui.setupEditText(cbr, PAR_CBR, DEFAULT_CBR);
		ui.setupEditText(vbr, PAR_VBR, DEFAULT_VBR);
		requestFilters();
	}

	private void requestFilters() {
		new MyFiltersRequest().execute();
	}

	private List<String> determineFilterList(List<String> filters, String prefix) {
		List<String> result = new ArrayList<>();
		for (String filter : filters) {
			if (filter.startsWith(prefix)) {
				result.add(filter.substring(prefix.length()));
			}
		}
		return result;
	}

}
