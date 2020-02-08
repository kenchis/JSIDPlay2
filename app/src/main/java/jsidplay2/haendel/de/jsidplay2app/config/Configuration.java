package jsidplay2.haendel.de.jsidplay2app.config;

public class Configuration implements IConfiguration {

	private String hostname;

	public final String getHostname() {
		return hostname;
	}

	public final void setHostname(String hostname) {
		this.hostname = hostname;
	}

	private String port;

	public final String getPort() {
		return port;
	}

	public final void setPort(String port) {
		this.port = port;
	}

	private String username;

	public final String getUsername() {
		return username;
	}

	public final void setUsername(String username) {
		this.username = username;
	}

	private String password;

	public final String getPassword() {
		return password;
	}

	public final void setPassword(String password) {
		this.password = password;
	}

	private String emulation;

	@Override
	public String getDefaultEmulation() {
		return emulation;
	}

	@Override
	public void setDefaultEmulation(String emulation) {
		this.emulation = emulation;
	}

	private boolean enableDatabase;

	@Override
	public boolean isEnableDatabase() {
		return enableDatabase;
	}

	@Override
	public void setEnableDatabase(boolean enableDatabase) {
		this.enableDatabase = enableDatabase;
	}

	private String defaultLength;

	@Override
	public String getDefaultLength() {
		return defaultLength;
	}

	private String startTime;

	@Override
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	@Override
	public String getStartTime() {
		return startTime;
	}

	@Override
	public void setDefaultLength(String defaultLength) {
		this.defaultLength = defaultLength;
	}

	private String fadeIn;

	@Override
	public String getFadeIn() {
		return fadeIn;
	}

	@Override
	public void setFadeIn(String fadeIn) {
		this.fadeIn = fadeIn;
	}

	private String fadeOut;

	@Override
	public String getFadeOut() {
		return fadeOut;
	}

	@Override
	public void setFadeOut(String fadeOut) {
		this.fadeOut = fadeOut;
	}

	private String defaultModel;

	@Override
	public String getDefaultModel() {
		return defaultModel;
	}

	@Override
	public void setDefaultModel(String defaultModel) {
		this.defaultModel = defaultModel;
	}

	private boolean singleSong;

	@Override
	public boolean isSingleSong() {
		return singleSong;
	}

	@Override
	public void setSingleSong(boolean singleSong) {
		this.singleSong = singleSong;
	}

	private boolean loop;

	@Override
	public boolean isLoop() {
		return loop;
	}

	@Override
	public void setLoop(boolean loop) {
		this.loop = loop;

	}

	private boolean fakeStereo;

	@Override
	public boolean isFakeStereo() {
		return fakeStereo;
	}

	@Override
	public void setFakeStereo(boolean fakeStereo) {
		this.fakeStereo = fakeStereo;

	}

	private boolean reverb;

	@Override
	public boolean isReverb() {
		return reverb;
	}

	@Override
	public void setReverb(boolean reverb) {
		this.reverb = reverb;

	}

	private String filter6581;

	@Override
	public String getFilter6581() {
		return filter6581;
	}

	@Override
	public void setFilter6581(String filter6581) {
		this.filter6581 = filter6581;

	}

	private String filter8580;

	@Override
	public String getFilter8580() {
		return filter8580;
	}

	@Override
	public void setFilter8580(String filter8580) {
		this.filter8580 = filter8580;
	}

	private String reSIDfpFilter6581;

	@Override
	public String getReSIDfpFilter6581() {
		return reSIDfpFilter6581;
	}

	@Override
	public void setReSIDfpFilter6581(String residFpFilter6581) {
		this.reSIDfpFilter6581 = residFpFilter6581;
	}

	private String reSIDfpFilter8580;

	@Override
	public String getReSIDfpFilter8580() {
		return reSIDfpFilter8580;
	}

	@Override
	public void setReSIDfpFilter8580(String residFpFilter8580) {
		this.reSIDfpFilter8580 = residFpFilter8580;
	}

	private String stereoFilter6581;

	@Override
	public String getStereoFilter6581() {
		return stereoFilter6581;
	}

	@Override
	public void setStereoFilter6581(String filter6581) {
		this.stereoFilter6581 = filter6581;

	}

	private String stereoFilter8580;

	@Override
	public String getStereoFilter8580() {
		return stereoFilter8580;
	}

	@Override
	public void setStereoFilter8580(String filter8580) {
		this.stereoFilter8580 = filter8580;
	}

	private String reSIDfpStereoFilter6581;

	@Override
	public String getReSIDfpStereoFilter6581() {
		return reSIDfpStereoFilter6581;
	}

	@Override
	public void setReSIDfpStereoFilter6581(String residFpFilter6581) {
		this.reSIDfpStereoFilter6581 = residFpFilter6581;
	}

	private String reSIDfpStereoFilter8580;

	@Override
	public String getReSIDfpStereoFilter8580() {
		return reSIDfpStereoFilter8580;
	}

	@Override
	public void setReSIDfpStereoFilter8580(String residFpFilter8580) {
		this.reSIDfpStereoFilter8580 = residFpFilter8580;
	}

	private String thirdFilter6581;

	@Override
	public String getThirdFilter6581() {
		return thirdFilter6581;
	}

	@Override
	public void setThirdFilter6581(String filter6581) {
		this.thirdFilter6581 = filter6581;

	}

	private String thirdFilter8580;

	@Override
	public String getThirdFilter8580() {
		return thirdFilter8580;
	}

	@Override
	public void setThirdFilter8580(String filter8580) {
		this.thirdFilter8580 = filter8580;
	}

	private String reSIDfpThirdFilter6581;

	@Override
	public String getReSIDfpThirdFilter6581() {
		return reSIDfpThirdFilter6581;
	}

	@Override
	public void setReSIDfpThirdFilter6581(String residFpFilter6581) {
		this.reSIDfpThirdFilter6581 = residFpFilter6581;
	}

	private String reSIDfpThirdFilter8580;

	@Override
	public String getReSIDfpThirdFilter8580() {
		return reSIDfpThirdFilter8580;
	}

	@Override
	public void setReSIDfpThirdFilter8580(String residFpFilter8580) {
		this.reSIDfpThirdFilter8580 = residFpFilter8580;
	}


	public String mainVolume;

	@Override
	public String getMainVolume() {
		return mainVolume;
	}

	@Override
	public void setMainVolume(String mainVolume) {
		this.mainVolume=mainVolume;
	}

	public String secondVolume;

	@Override
	public String getSecondVolume() {
		return secondVolume;
	}

	@Override
	public void setSecondVolume(String secondVolume) {
		this.secondVolume = secondVolume;
	}

	public String thirdVolume;

	@Override
	public String getThirdVolume() {
		return thirdVolume;
	}

	@Override
	public void setThirdVolume(String thirdVolume) {
		this.thirdVolume = thirdVolume;
	}



	public String mainBalance;

	@Override
	public String getMainBalance() {
		return mainBalance;
	}

	@Override
	public void setMainBalance(String mainBalance) {
		this.mainBalance=mainBalance;
	}

	public String secondBalance;

	@Override
	public String getSecondBalance() {
		return secondBalance;
	}

	@Override
	public void setSecondBalance(String secondBalance) {
		this.secondBalance = secondBalance;
	}

	public String thirdBalance;

	@Override
	public String getThirdBalance() {
		return thirdBalance;
	}

	@Override
	public void setThirdBalance(String thirdBalance) {
		this.thirdBalance = thirdBalance;
	}



	public String mainDelay;

	@Override
	public String getMainDelay() {
		return mainDelay;
	}

	@Override
	public void setMainDelay(String mainDelay) {
		this.mainDelay = mainDelay;
	}

	public String secondDelay;

	@Override
	public 	String getSecondDelay() {
		return secondDelay;
	}

	@Override
	public void setSecondDelay(String secondDelay) {
		this.secondDelay = secondDelay;
	}

	public String thirdDelay;

	@Override
	public String getThirdDelay() {
		return thirdDelay;
	}

	@Override
	public void setThirdDelay(String thirdDelay) {
		this.thirdDelay = thirdDelay;
	}



	private boolean digiBoosted8580;

	@Override
	public boolean isDigiBoosted8580() {
		return digiBoosted8580;
	}

	@Override
	public void setDigiBoosted8580(boolean digiBoosted) {
		this.digiBoosted8580 = digiBoosted;
	}

	private String samplingMethod;

	@Override
	public String getSamplingMethod() {
		return samplingMethod;
	}

	@Override
	public void setSamplingMethod(String samplingMethod) {
		this.samplingMethod = samplingMethod;
	}

	private String connectionType;

	@Override
	public String getConnectionType() { return connectionType!=null?connectionType:HTTP; }

	@Override
	public void setConnectionType(String connectionType) {
		this.connectionType = connectionType;
	}

	private String frequency;

	@Override
	public String getFrequency() {
		return frequency;
	}

	@Override
	public void setFrequency(String frequency) {
		this.frequency = frequency;
	}

	private String bufferSize;

	@Override
	public String getBufferSize() {
		return bufferSize;
	}

	@Override
	public void setBufferSize(String bufferSize) {
		this.bufferSize = bufferSize;
	}

	private String bufferSizeWlan;

	@Override
	public String getBufferSizeWlan() {
		return bufferSizeWlan;
	}

	@Override
	public void setBufferSizeWlan(String bufferSizeWlan) {
		this.bufferSizeWlan = bufferSizeWlan;
	}

	private String cbr;

	@Override
	public String getCbr() {
		return cbr;
	}

	@Override
	public void setCbr(String cbr) {
		this.cbr = cbr;
	}

	private String vbr;

	@Override
	public String getVbr() {
		return vbr;
	}

	@Override
	public void setVbr(String vbr) {
		this.vbr = vbr;
	}

}
