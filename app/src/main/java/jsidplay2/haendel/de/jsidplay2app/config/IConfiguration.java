package jsidplay2.haendel.de.jsidplay2app.config;

public interface IConfiguration {
	String PAR_BUFFER_SIZE = "bufferSize";
	String PAR_BUFFER_SIZE_WLAN = "bufferSizeWlan";
	String PAR_EMULATION = "defaultEmulation";
	String PAR_ENABLE_DATABASE = "enableSidDatabase";
	String PAR_DEFAULT_PLAY_LENGTH = "defaultLength";
	String PAR_FADE_IN = "fadeIn";
	String PAR_FADE_OUT = "fadeOut";
	String PAR_DEFAULT_MODEL = "defaultModel";
	String PAR_SINGLE_SONG = "single";
	String PAR_LOOP = "loop";
	String PAR_FAKE_STEREO = "fakeStereo";
	String PAR_SAMPLING_METHOD = "sampling";
	String PAR_CONNECTION_TYPE= "connectionType";
	String PAR_FREQUENCY = "frequency";
	String PAR_FILTER_6581 = "filter6581";
	String PAR_STEREO_FILTER_6581 = "stereoFilter6581";
	String PAR_THIRD_FILTER_6581 = "thirdFilter6581";
	String PAR_FILTER_8580 = "filter8580";
	String PAR_STEREO_FILTER_8580 = "stereoFilter8580";
	String PAR_THIRD_FILTER_8580 = "thirdFilter8580";
	String PAR_RESIDFP_FILTER_6581 = "reSIDfpFilter6581";
	String PAR_RESIDFP_STEREO_FILTER_6581 = "reSIDfpStereoFilter6581";
	String PAR_RESIDFP_THIRD_FILTER_6581 = "reSIDfpThirdFilter6581";
	String PAR_RESIDFP_FILTER_8580 = "reSIDfpFilter8580";
	String PAR_RESIDFP_STEREO_FILTER_8580 = "reSIDfpStereoFilter8580";
	String PAR_RESIDFP_THIRD_FILTER_8580 = "reSIDfpThirdFilter8580";

	String PAR_MAIN_VOLUME = "mainVolume";
	String PAR_SECOND_VOLUME = "secondVolume";
	String PAR_THIRD_VOLUME = "thirdVolume";

	String PAR_MAIN_BALANCE = "mainBalance";
	String PAR_SECOND_BALANCE = "secondBalance";
	String PAR_THIRD_BALANCE = "thirdBalance";

	String PAR_MAIN_DELAY = "mainDelay";
	String PAR_SECOND_DELAY = "secondDelay";
	String PAR_THIRD_DELAY = "thirdDelay";
	String PAR_PORT = "port";

	String PAR_DIGI_BOOSTED_8580 = "digiBoosted8580";
	String PAR_IS_VBR = "vbr";
	String PAR_CBR = "cbr";
	String PAR_VBR = "vbrQuality";

	String HTTP = "HTTP";
	String HTTPS = "HTTPS";

	String RESID = "RESID";
	String RESIDFP = "RESIDFP";

	String MOS6581 = "MOS6581";
	String MOS8580 = "MOS8580";

	String DECIMATE = "DECIMATE";
	String RESAMPLE = "RESAMPLE";

	String _44100 = "LOW";
	String _48000 = "MEDIUM";
	String _96000 = "HIGH";

	String DEFAULT_BUFFER_SIZE = "6400000";
	String DEFAULT_BUFFER_SIZE_WLAN = "2500";
	String DEFAULT_PLAY_LENGTH = "180";
	String DEFAULT_FADE_IN = "5";
	String DEFAULT_FADE_OUT = "5";
	String DEFAULT_ENABLE_DATABASE = Boolean.TRUE.toString();
	String DEFAULT_SINGLE_SONG = Boolean.TRUE.toString();
	String DEFAULT_LOOP = Boolean.FALSE.toString();
	String DEFAULT_FAKE_STEREO = Boolean.FALSE.toString();
	String DEFAULT_DIGI_BOOSTED_8580 = Boolean.FALSE.toString();
	String DEFAULT_FILTER_6581 = "FilterAverage6581";
	String DEFAULT_FILTER_8580 = "FilterAverage8580";
	String DEFAULT_RESIDFP_FILTER_6581 = "FilterAlankila6581R4AR_3789";
	String DEFAULT_RESIDFP_FILTER_8580 = "FilterTrurl8580R5_3691";
	String DEFAULT_CONNECTION_TYPE = "HTTP";
	String DEFAULT_PORT = "8080";
	String DEFAULT_SECURE_PORT = "8443";

	String DEFAULT_MAIN_VOLUME = "0.0";
	String DEFAULT_SECOND_VOLUME = "0.0";
	String DEFAULT_THIRD_VOLUME = "0.0";

	String DEFAULT_MAIN_BALANCE = "0.3";
	String DEFAULT_SECOND_BALANCE = "0.7";
	String DEFAULT_THIRD_BALANCE = "0.5";

	String DEFAULT_MAIN_DELAY = "0";
	String DEFAULT_SECOND_DELAY = "20";
	String DEFAULT_THIRD_DELAY = "0";

	String DEFAULT_CBR = "64";
	String DEFAULT_VBR = "0";

	String getHostname();

	void setHostname(String hostname);

	String getConnectionType();

	void setConnectionType(String connectionType);

	String getPort();

	void setPort(String port);

	String getUsername();

	void setUsername(String username);

	String getPassword();

	void setPassword(String password);

	String getBufferSize();

	void setBufferSize(String bufferSize);

	String getBufferSizeWlan();

	void setBufferSizeWlan(String bufferSizeWlan);

	String getDefaultEmulation();

	void setDefaultEmulation(String emulation);

	boolean isEnableDatabase();

	void setEnableDatabase(boolean enableDatabase);

	String getDefaultLength();

	void setDefaultLength(String defaultLength);

	String getFadeIn();

	void setFadeIn(String fadeIn);

	String getFadeOut();

	void setFadeOut(String fadeOut);

	String getDefaultModel();

	void setDefaultModel(String defaultModel);

	boolean isSingleSong();

	void setSingleSong(boolean singleSong);

	boolean isLoop();

	void setLoop(boolean loop);

	boolean isFakeStereo();

	void setFakeStereo(boolean fakeStereo);

	String getFilter6581();

	void setFilter6581(String filter6581);

	String getFilter8580();

	void setFilter8580(String filter8580);

	String getReSIDfpFilter6581();

	void setReSIDfpFilter6581(String residFpFilter6581);

	String getReSIDfpFilter8580();

	void setReSIDfpFilter8580(String residFpFilter8580);

	String getStereoFilter6581();

	void setStereoFilter6581(String filter6581);

	String getStereoFilter8580();

	void setStereoFilter8580(String filter8580);

	String getReSIDfpStereoFilter6581();

	void setReSIDfpStereoFilter6581(String residFpFilter6581);

	String getReSIDfpStereoFilter8580();

	void setReSIDfpStereoFilter8580(String residFpFilter8580);



	String getMainVolume();

	void setMainVolume(String mainVolume);

	String getSecondVolume();

	void setSecondVolume(String secondVolume);

	String getThirdVolume();

	void setThirdVolume(String thirdVolume);



	String getMainBalance();

	void setMainBalance(String mainBalance);

	String getSecondBalance();

	void setSecondBalance(String secondBalance);

	String getThirdBalance();

	void setThirdBalance(String thirdBalance);



	String getMainDelay();

	void setMainDelay(String mainDelay);

	String getSecondDelay();

	void setSecondDelay(String secondDelay);

	String getThirdDelay();

	void setThirdDelay(String thirdDelay);



	String getThirdFilter6581();

	void setThirdFilter6581(String filter6581);

	String getThirdFilter8580();

	void setThirdFilter8580(String filter8580);

	String getReSIDfpThirdFilter6581();

	void setReSIDfpThirdFilter6581(String residFpFilter6581);

	String getReSIDfpThirdFilter8580();

	void setReSIDfpThirdFilter8580(String residFpFilter8580);


	
	boolean isDigiBoosted8580();

	void setDigiBoosted8580(boolean digiBoosted);

	String getSamplingMethod();

	void setSamplingMethod(String samplingMethod);

	String getFrequency();

	void setFrequency(String frequency);

	String getCbr();
	
	void setCbr(String newValue);

	String getVbr();
	
	void setVbr(String newValue);

}
