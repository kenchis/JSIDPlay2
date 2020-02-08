package jsidplay2.haendel.de.jsidplay2app;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import jsidplay2.haendel.de.jsidplay2app.config.IConfiguration;
import jsidplay2.haendel.de.jsidplay2app.request.JSIDPlay2RESTRequest.RequestType;

import static android.media.MediaPlayer.MEDIA_ERROR_SERVER_DIED;
import static android.media.MediaPlayer.MEDIA_ERROR_UNKNOWN;
import static jsidplay2.haendel.de.jsidplay2app.config.IConfiguration.PAR_BUFFER_SIZE;
import static jsidplay2.haendel.de.jsidplay2app.config.IConfiguration.PAR_CBR;
import static jsidplay2.haendel.de.jsidplay2app.config.IConfiguration.PAR_DEFAULT_MODEL;
import static jsidplay2.haendel.de.jsidplay2app.config.IConfiguration.PAR_DEFAULT_PLAY_LENGTH;
import static jsidplay2.haendel.de.jsidplay2app.config.IConfiguration.PAR_DEFAULT_START_TIME;
import static jsidplay2.haendel.de.jsidplay2app.config.IConfiguration.PAR_DIGI_BOOSTED_8580;
import static jsidplay2.haendel.de.jsidplay2app.config.IConfiguration.PAR_EMULATION;
import static jsidplay2.haendel.de.jsidplay2app.config.IConfiguration.PAR_ENABLE_DATABASE;
import static jsidplay2.haendel.de.jsidplay2app.config.IConfiguration.PAR_FADE_IN;
import static jsidplay2.haendel.de.jsidplay2app.config.IConfiguration.PAR_FADE_OUT;
import static jsidplay2.haendel.de.jsidplay2app.config.IConfiguration.PAR_FAKE_STEREO;
import static jsidplay2.haendel.de.jsidplay2app.config.IConfiguration.PAR_REVERB;
import static jsidplay2.haendel.de.jsidplay2app.config.IConfiguration.PAR_FILTER_6581;
import static jsidplay2.haendel.de.jsidplay2app.config.IConfiguration.PAR_FILTER_8580;
import static jsidplay2.haendel.de.jsidplay2app.config.IConfiguration.PAR_FREQUENCY;
import static jsidplay2.haendel.de.jsidplay2app.config.IConfiguration.PAR_IS_VBR;
import static jsidplay2.haendel.de.jsidplay2app.config.IConfiguration.PAR_LOOP;
import static jsidplay2.haendel.de.jsidplay2app.config.IConfiguration.PAR_MAIN_BALANCE;
import static jsidplay2.haendel.de.jsidplay2app.config.IConfiguration.PAR_MAIN_DELAY;
import static jsidplay2.haendel.de.jsidplay2app.config.IConfiguration.PAR_MAIN_VOLUME;
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

public class JSIDPlay2Service extends Service implements OnPreparedListener, OnErrorListener, OnCompletionListener {

    private static final String JSIDPLAY2_JS2 = "jsidplay2.js2";

    public interface PlayListener {
        void play(int currentSong, PlayListEntry entry);
    }

    public static class PlayListEntry {

        private String resource;

        PlayListEntry(String resource) {
            this.resource = resource;
        }

        public String getResource() {
            return resource;
        }
    }

    public class JSIDPlay2Binder extends Binder {
        public JSIDPlay2Service getService() {
            return JSIDPlay2Service.this;
        }
    }

    private final IBinder jsidplay2Binder = new JSIDPlay2Binder();
    private IConfiguration configuration;
    private boolean randomized;
    private PlayListener listener;

    private Random rnd;
    private PlayListEntry currentEntry;
    private List<PlayListEntry> playList;
    private MediaPlayer player;

    public void setConfiguration(IConfiguration configuration) {
        this.configuration = configuration;
    }

    public void setRandomized(boolean randomized) {
        this.randomized = randomized;
    }

    public void addPlayListener(PlayListener listener) {
        this.listener = listener;
    }

    public List<PlayListEntry> getPlayList() {
        return this.playList;
    }

    public void onCreate() {
        super.onCreate();

        rnd = new Random(System.currentTimeMillis());
        playList = new ArrayList<>();
        currentEntry = null;
        player = createMediaPlayer();
    }

    @Override
    public IBinder onBind(Intent intent) {
        try {
            load();
        } catch (IllegalArgumentException | SecurityException | IllegalStateException | IOException e) {
            Log.e(JSIDPlay2Service.class.getSimpleName(), e.getMessage(), e);
        }
        return jsidplay2Binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        stopMediaPlayer(player);
        destroyMediaPlayer(player);
        listener = null;
        return false;
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Log.e(getPackageName(), String.format("Error(%s%s)", what, extra));
        switch (what) {
            case MEDIA_ERROR_SERVER_DIED:
                Toast.makeText(this, "MEDIA_ERROR_SERVER_DIED", Toast.LENGTH_SHORT).show();
                break;
            case MEDIA_ERROR_UNKNOWN:
                Toast.makeText(this, "MEDIA_ERROR_UNKNOWN", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
        player.reset();
        return true;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        playNextSong();
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        mediaPlayer.start();
        listener.play(playList.indexOf(currentEntry), currentEntry);
    }

    public void playSong(PlayListEntry entry) {
        this.currentEntry = entry;

        File file = new File(currentEntry.getResource());
        Toast.makeText(this, file.getName(), Toast.LENGTH_SHORT).show();

        player.reset();

        try {
            byte[] toEncrypt = (configuration.getUsername() + ":" + configuration.getPassword()).getBytes();
            String encoded = Base64.encodeToString(toEncrypt, Base64.DEFAULT);
            HashMap<String, String> headers = new HashMap<>();
            headers.put("Authorization", "Basic " + encoded);

            Uri uri = getURI(configuration, currentEntry.getResource());
            player.setDataSource(getApplicationContext(), uri, headers);
        } catch (Exception e) {
            Log.e(JSIDPlay2Service.class.getSimpleName(), "Error setting data source!", e);
        }
        player.prepareAsync();
    }

    public void playNextSong() {
        int currentSong = currentEntry == null ? -1 : playList.indexOf(currentEntry);
        if (randomized) {
            currentSong = rnd.nextInt(playList.size());
        } else {
            currentSong = currentSong + 1 < playList.size() ? currentSong + 1 : 0;
        }
        if (currentEntry != null && currentEntry.getResource().endsWith("mp3")) {
            return;
        }
        playSong(playList.get(currentSong));
    }

    public void stop() {
        Toast.makeText(this, "JSIDPlay2 Stopped...", Toast.LENGTH_SHORT).show();
        stopMediaPlayer(player);
    }

    public PlayListEntry add(String resource) {
        PlayListEntry entry = new PlayListEntry(resource);
        playList.add(entry);
        return entry;
    }

    public void removeLast() {
        PlayListEntry entry = getLast();
        if (entry != null) {
            playList.remove(entry);
        }
    }

    public void removeAll() throws IOException {
        playList.clear();
        save();
    }

    private MediaPlayer createMediaPlayer() {
        MediaPlayer mp = new MediaPlayer();
        mp.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mp.setOnPreparedListener(this);
        mp.setOnCompletionListener(this);
        mp.setOnErrorListener(this);
        return mp;
    }

    private void stopMediaPlayer(MediaPlayer mp) {
        if (mp.isPlaying()) {
            mp.stop();
        }
    }

    private void destroyMediaPlayer(MediaPlayer mp) {
        mp.release();
    }

    private Uri getURI(IConfiguration configuration, String resource) throws URISyntaxException {
        StringBuilder query = new StringBuilder();
        if (isWifiConnected()) {
            query.append(PAR_BUFFER_SIZE).append("=").append(configuration.getBufferSizeWlan()).append("&");
        } else {
            query.append(PAR_BUFFER_SIZE).append("=").append(configuration.getBufferSize()).append("&");
        }
        query.append(PAR_EMULATION).append("=").append(configuration.getDefaultEmulation()).append("&");
        query.append(PAR_ENABLE_DATABASE).append("=").append(configuration.isEnableDatabase()).append("&");
        query.append(PAR_DEFAULT_PLAY_LENGTH).append("=").append(getTime(getNumber(configuration.getDefaultLength()))).append("&");
        query.append(PAR_DEFAULT_START_TIME).append("=").append(getTime(getNumber(configuration.getStartTime()))).append("&");
        query.append(PAR_FADE_IN).append("=").append(getTime(getNumber(configuration.getFadeIn()))).append("&");
        query.append(PAR_FADE_OUT).append("=").append(getTime(getNumber(configuration.getFadeOut()))).append("&");
        query.append(PAR_DEFAULT_MODEL).append("=").append(configuration.getDefaultModel()).append("&");
        query.append(PAR_SINGLE_SONG).append("=").append(configuration.isSingleSong()).append("&");
        query.append(PAR_LOOP).append("=").append(configuration.isLoop()).append("&");
        query.append(PAR_FAKE_STEREO).append("=").append(configuration.isFakeStereo()).append("&");
        query.append(PAR_REVERB).append("=").append(configuration.isReverb()).append("&");

        query.append(PAR_FILTER_6581).append("=").append(configuration.getFilter6581()).append("&");
        query.append(PAR_FILTER_8580).append("=").append(configuration.getFilter8580()).append("&");
        query.append(PAR_RESIDFP_FILTER_6581).append("=").append(configuration.getReSIDfpFilter6581()).append("&");
        query.append(PAR_RESIDFP_FILTER_8580).append("=").append(configuration.getReSIDfpFilter8580()).append("&");

        query.append(PAR_STEREO_FILTER_6581).append("=").append(configuration.getStereoFilter6581()).append("&");
        query.append(PAR_STEREO_FILTER_8580).append("=").append(configuration.getStereoFilter8580()).append("&");
        query.append(PAR_RESIDFP_STEREO_FILTER_6581).append("=").append(configuration.getReSIDfpStereoFilter6581()).append("&");
        query.append(PAR_RESIDFP_STEREO_FILTER_8580).append("=").append(configuration.getReSIDfpStereoFilter8580()).append("&");

        query.append(PAR_THIRD_FILTER_6581).append("=").append(configuration.getThirdFilter6581()).append("&");
        query.append(PAR_THIRD_FILTER_8580).append("=").append(configuration.getThirdFilter8580()).append("&");
        query.append(PAR_RESIDFP_THIRD_FILTER_6581).append("=").append(configuration.getReSIDfpThirdFilter6581()).append("&");
        query.append(PAR_RESIDFP_THIRD_FILTER_8580).append("=").append(configuration.getReSIDfpThirdFilter8580()).append("&");

        query.append(PAR_MAIN_VOLUME).append("=").append(configuration.getMainVolume()).append("&");
        query.append(PAR_SECOND_VOLUME).append("=").append(configuration.getSecondVolume()).append("&");
        query.append(PAR_THIRD_VOLUME).append("=").append(configuration.getThirdVolume()).append("&");

        query.append(PAR_MAIN_BALANCE).append("=").append(configuration.getMainBalance()).append("&");
        query.append(PAR_SECOND_BALANCE).append("=").append(configuration.getSecondBalance()).append("&");
        query.append(PAR_THIRD_BALANCE).append("=").append(configuration.getThirdBalance()).append("&");

        query.append(PAR_MAIN_DELAY).append("=").append(configuration.getMainDelay()).append("&");
        query.append(PAR_SECOND_DELAY).append("=").append(configuration.getSecondDelay()).append("&");
        query.append(PAR_THIRD_DELAY).append("=").append(configuration.getThirdDelay()).append("&");

        query.append(PAR_DIGI_BOOSTED_8580).append("=").append(configuration.isDigiBoosted8580()).append("&");
        query.append(PAR_SAMPLING_METHOD).append("=").append(configuration.getSamplingMethod()).append("&");
        query.append(PAR_FREQUENCY).append("=").append(configuration.getFrequency()).append("&");
        if (isWifiConnected()) {
            query.append(PAR_IS_VBR + "=true&");
        } else {
            query.append(PAR_IS_VBR + "=false&");
        }
        query.append(PAR_CBR).append("=").append(configuration.getCbr()).append("&");
        query.append(PAR_VBR).append("=").append(configuration.getVbr());

        return Uri.parse(new URI(configuration.getConnectionType().toLowerCase(Locale.US), null, configuration.getHostname(), Integer.parseInt(configuration.getPort()),
                RequestType.CONVERT.getUrl() + resource, query.toString(), null).toString());
    }

    private String getTime(int number) {
        return String.format(Locale.US, "%02d:%02d", number / 60, number % 60);
    }

    private boolean isWifiConnected() {
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connManager != null ? connManager.getActiveNetworkInfo() : null;
        return (info != null && info.isConnected() && info.getType() == ConnectivityManager.TYPE_WIFI);
    }

    private int getNumber(String txt) {
        try {
            return Integer.parseInt(txt);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private PlayListEntry getLast() {
        return playList.size() > 0 ? playList.get(playList.size() - 1) : null;
    }

    public void load() throws IOException, IllegalArgumentException, SecurityException,
            IllegalStateException {
        File sdRootDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File playlistFile = new File(sdRootDir, JSIDPLAY2_JS2);
        if (!playlistFile.exists()) {
            if (!playlistFile.createNewFile()) {
                return;
            }
        }
        try (BufferedReader r = new BufferedReader(new InputStreamReader(new FileInputStream(playlistFile), "ISO-8859-1"))) {
            String line;
            while ((line = r.readLine()) != null) {
                playList.add(new PlayListEntry(line));
            }
        }
    }

    public void save() throws IOException {
        File sdRootDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File playlistFile = new File(sdRootDir, JSIDPLAY2_JS2);
        try (BufferedWriter w = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(playlistFile), "ISO-8859-1"))) {
            for (PlayListEntry playListEntry : playList) {
                w.write(playListEntry.getResource());
                w.write('\n');
            }
        }
    }

}
