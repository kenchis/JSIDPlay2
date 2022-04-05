package jsidplay2.haendel.de.jsidplay2app;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TabHost;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Locale;

import jsidplay2.haendel.de.jsidplay2app.JSIDPlay2Service.JSIDPlay2Binder;
import jsidplay2.haendel.de.jsidplay2app.JSIDPlay2Service.PlayListener;
import jsidplay2.haendel.de.jsidplay2app.config.Configuration;
import jsidplay2.haendel.de.jsidplay2app.config.IConfiguration;
import jsidplay2.haendel.de.jsidplay2app.request.FavoritesRequest;
import jsidplay2.haendel.de.jsidplay2app.request.JSIDPlay2RESTRequest.RequestType;
import jsidplay2.haendel.de.jsidplay2app.tab.ConfigurationTab;
import jsidplay2.haendel.de.jsidplay2app.tab.GeneralTab;
import jsidplay2.haendel.de.jsidplay2app.tab.PlayListEntry;
import jsidplay2.haendel.de.jsidplay2app.tab.PlayListTab;
import jsidplay2.haendel.de.jsidplay2app.tab.SidTab;
import jsidplay2.haendel.de.jsidplay2app.tab.SidsTab;

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
import static jsidplay2.haendel.de.jsidplay2app.config.IConfiguration.PAR_IS_VBR;
import static jsidplay2.haendel.de.jsidplay2app.config.IConfiguration.PAR_MUTE_VOICE1;
import static jsidplay2.haendel.de.jsidplay2app.config.IConfiguration.PAR_MUTE_VOICE2;
import static jsidplay2.haendel.de.jsidplay2app.config.IConfiguration.PAR_MUTE_VOICE3;
import static jsidplay2.haendel.de.jsidplay2app.config.IConfiguration.PAR_MUTE_VOICE4;
import static jsidplay2.haendel.de.jsidplay2app.config.IConfiguration.PAR_MUTE_STEREO_VOICE1;
import static jsidplay2.haendel.de.jsidplay2app.config.IConfiguration.PAR_MUTE_STEREO_VOICE2;
import static jsidplay2.haendel.de.jsidplay2app.config.IConfiguration.PAR_MUTE_STEREO_VOICE3;
import static jsidplay2.haendel.de.jsidplay2app.config.IConfiguration.PAR_MUTE_STEREO_VOICE4;
import static jsidplay2.haendel.de.jsidplay2app.config.IConfiguration.PAR_MUTE_3SID_VOICE1;
import static jsidplay2.haendel.de.jsidplay2app.config.IConfiguration.PAR_MUTE_3SID_VOICE2;
import static jsidplay2.haendel.de.jsidplay2app.config.IConfiguration.PAR_MUTE_3SID_VOICE3;
import static jsidplay2.haendel.de.jsidplay2app.config.IConfiguration.PAR_MUTE_3SID_VOICE4;
import static jsidplay2.haendel.de.jsidplay2app.config.IConfiguration.PAR_REVERB;
import static jsidplay2.haendel.de.jsidplay2app.config.IConfiguration.PAR_FILTER_6581;
import static jsidplay2.haendel.de.jsidplay2app.config.IConfiguration.PAR_FILTER_8580;
import static jsidplay2.haendel.de.jsidplay2app.config.IConfiguration.PAR_FREQUENCY;
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

public class MainActivity extends Activity implements PlayListener {

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private final static int REQUEST_BATTERY_OPTIMIZATION = 113;

    private static final String ACTION_USB_PERMISSION =
            "com.android.example.USB_PERMISSION";

    private class MyFavoritesDownloadRequest extends FavoritesRequest {
        private MyFavoritesDownloadRequest() {
            super(appName, configuration, RequestType.FAVORITES, "");
        }

        public String getString(String key) {
            return key;
        }

        @Override
        protected void onPostExecute(List<String> favorites) {
            if (favorites == null) {
                return;
            }
            for (String favorite : favorites) {
                playListTab.addRow(jsidplay2service.add(favorite));
            }
            try {
                jsidplay2service.save();
            } catch (IOException e) {
                Log.e(appName, e.getMessage(), e);
            }
        }
    }

    private String appName;
    private Configuration configuration;

    private TabHost tabHost;
    private SidTab sidTab;
    private PlayListTab playListTab;

    private boolean randomized;
    private JSIDPlay2Service jsidplay2service;
    private Intent playIntent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        appName = getApplication().getString(R.string.app_name);
        configuration = new Configuration();

        tabHost = findViewById(android.R.id.tabhost);
        tabHost.setup();

        new GeneralTab(this, appName, configuration, tabHost);
        new SidsTab(this, appName, configuration, tabHost) {
            @Override
            protected void showSid(String cannonicalPath) {
                sidTab.requestSidDetails(cannonicalPath);
            }

            @Override
            protected void showMedia(String cannonicalPath) {
                try {
                    Uri uri = getURI(configuration, cannonicalPath, false);

                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(uri);
                    startActivity(i);
                } catch (URISyntaxException e) {
                    Log.e(appName, e.getMessage(), e);
                }
            }
        };
        sidTab = new SidTab(this, appName, configuration, tabHost);
        playListTab = new PlayListTab(this, appName, configuration, tabHost) {
            @Override
            protected void play(PlayListEntry entry) {
                jsidplay2service.playSong(entry);
                sidTab.requestSidDetails(entry.getResource());
            }

            @Override
            protected void setRandomized(boolean newValue) {
                MainActivity.this.setRandomized(newValue);
            }
        };
        new ConfigurationTab(this, appName, configuration, tabHost);

        tabHost.setCurrentTabByTag(GeneralTab.class.getSimpleName());

        if (isOverMarshmallow()) {
            int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

            if (permission != PackageManager.PERMISSION_GRANTED) {
                // We don't have permission so prompt the user
                ActivityCompat.requestPermissions(
                        MainActivity.this,
                        PERMISSIONS_STORAGE,
                        REQUEST_EXTERNAL_STORAGE
                );
            }
        }
    }

    private Uri getURI(IConfiguration configuration, String resource, boolean download) throws URISyntaxException {
        StringBuilder query = new StringBuilder();
            query.append(PAR_BUFFER_SIZE).append("=").append(configuration.getBufferSizeWlan()).append("&");
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
        query.append(PAR_MUTE_VOICE1).append("=").append(configuration.isMuteVoice1()).append("&");
        query.append(PAR_MUTE_VOICE2).append("=").append(configuration.isMuteVoice2()).append("&");
        query.append(PAR_MUTE_VOICE3).append("=").append(configuration.isMuteVoice3()).append("&");
        query.append(PAR_MUTE_VOICE4).append("=").append(configuration.isMuteVoice4()).append("&");
        query.append(PAR_MUTE_STEREO_VOICE1).append("=").append(configuration.isMuteStereoVoice1()).append("&");
        query.append(PAR_MUTE_STEREO_VOICE2).append("=").append(configuration.isMuteStereoVoice2()).append("&");
        query.append(PAR_MUTE_STEREO_VOICE3).append("=").append(configuration.isMuteStereoVoice3()).append("&");
        query.append(PAR_MUTE_STEREO_VOICE4).append("=").append(configuration.isMuteStereoVoice4()).append("&");
        query.append(PAR_MUTE_3SID_VOICE1).append("=").append(configuration.isMute3SidVoice1()).append("&");
        query.append(PAR_MUTE_3SID_VOICE2).append("=").append(configuration.isMute3SidVoice2()).append("&");
        query.append(PAR_MUTE_3SID_VOICE3).append("=").append(configuration.isMute3SidVoice3()).append("&");
        query.append(PAR_MUTE_3SID_VOICE4).append("=").append(configuration.isMute3SidVoice4()).append("&");

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
            query.append("vcBitRate=").append(configuration.getVCBitRateWLAN()).append("&");
        } else {
            query.append(PAR_IS_VBR + "=false&");
            query.append("vcBitRate=").append(configuration.getVCBitRateMobile()).append("&");
        }
        query.append(PAR_CBR).append("=").append(configuration.getCbr()).append("&");
        query.append(PAR_VBR).append("=").append(configuration.getVbr());
        if (download) {
            query.append("&download").append("=").append("true");
        }
        String authorization = configuration.getUsername() + ":" + configuration.getPassword();
        return Uri.parse(new URI(configuration.getConnectionType().toLowerCase(Locale.US), authorization, configuration.getHostname(), Integer.parseInt(configuration.getPort()),
                RequestType.CONVERT.getUrl() + resource, query.toString(), null).toString());
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
    private String getTime(int number) {
        return String.format(Locale.US, "%02d:%02d", number / 60, number % 60);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUEST_BATTERY_OPTIMIZATION: {
                finish();
                startActivity(getIntent());
                break;
            }
        }
    }

    @SuppressLint("BatteryLife")
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_EXTERNAL_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // reload my activity with permission granted or use the features what required
                    // the permission
                    try {
                        jsidplay2service.load();
                    } catch (IllegalArgumentException | IllegalStateException | SecurityException | IOException e) {
                        Log.e(JSIDPlay2Service.class.getSimpleName(), e.getMessage(), e);
                    }
                }
                if (isOverMarshmallow()) {
                    PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
                    // http://developer.android.com/intl/ko/training/monitoring-device-state/doze-standby.html#support_for_other_use_cases
                    String packageName = getPackageName();
                    if (pm != null && !pm.isIgnoringBatteryOptimizations(packageName)) {
                        Intent batteryIntent = new Intent();
                        batteryIntent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                        batteryIntent.setData(Uri.parse("package:" + packageName));
                        startActivityForResult(batteryIntent, REQUEST_BATTERY_OPTIMIZATION);
                    }
                }
                break;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_quit:
                unbindService(jsidplay2Connection);
                stopService(playIntent);
                jsidplay2service = null;
                System.exit(0);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private static boolean isOverMarshmallow() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (playIntent == null) {
            playIntent = new Intent(this, JSIDPlay2Service.class);
            bindService(playIntent, jsidplay2Connection, Context.BIND_AUTO_CREATE);
            startService(playIntent);
        }
    }

    @Override
    protected void onDestroy() {
        unbindService(jsidplay2Connection);
        stopService(playIntent);
        jsidplay2service = null;
        super.onDestroy();
    }

    public void removeFavorite(View view) {
        try {
            jsidplay2service.removeLast();
            jsidplay2service.save();
            playListTab.removeLast();
        } catch (IOException e) {
            Log.e(appName, e.getMessage(), e);
        }
    }

    public void next(View view) {
        jsidplay2service.playNextSong();
    }

    public void stop(View view) {
        jsidplay2service.stop();
        stopService(playIntent);
    }

    public void downloadPlayList(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Overwrite Playlist?");
        builder.setCancelable(true);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                try {
                    jsidplay2service.removeAll();
                } catch (IOException e) {
                    Log.e(appName, e.getMessage(), e);
                }
                playListTab.removeAll();
                new MyFavoritesDownloadRequest().execute();
                dialog.cancel();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void setRandomized(boolean randomized) {
        this.randomized = randomized;
        if (jsidplay2service != null) {
            jsidplay2service.setRandomized(randomized);
        }
    }

    public void asSid(View view) {
        try {
            String authorization = configuration.getUsername() + ":" + configuration.getPassword();
            URI myUri = new URI(configuration.getConnectionType().toLowerCase(Locale.US), authorization, configuration.getHostname(),
                    Integer.parseInt(configuration.getPort()), RequestType.DOWNLOAD.getUrl() + sidTab.getCurrentTune(),
                    null, null);

            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.parse(myUri.toString()), "audio/mpeg");

            startActivity(intent);
        } catch (NumberFormatException | URISyntaxException e) {
            Log.e(appName, e.getMessage(), e);
        }
    }

    public void justDownload(View view) {
        try {
            String resource = sidTab.getCurrentTune();
            Uri uri = getURI(configuration, resource, true);

            Intent intent = new Intent(Intent.ACTION_VIEW, uri);

            startActivity(intent);
        } catch (NumberFormatException | URISyntaxException e) {
            Log.e(appName, e.getMessage(), e);
        }
    }

    public void addToPlaylist(View view) {
        try {
            String resource = sidTab.getCurrentTune();
            PlayListEntry entry = jsidplay2service.add(resource);
            jsidplay2service.save();
            playListTab.addRow(entry);
            tabHost.setCurrentTabByTag(PlayListTab.class.getSimpleName());
        } catch (IOException e) {
            Log.e(appName, e.getMessage(), e);
        }
    }

    public void justPlay(View view) {
        String resource = sidTab.getCurrentTune();
        jsidplay2service.playSong(new PlayListEntry(resource));
    }

    // connect to the service
    private final ServiceConnection jsidplay2Connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            JSIDPlay2Binder binder = (JSIDPlay2Binder) service;
            // get service
            jsidplay2service = binder.getService();
            // pass configuration
            jsidplay2service.setConfiguration(configuration);
            jsidplay2service.setRandomized(randomized);
            jsidplay2service.addPlayListener(MainActivity.this);

            for (PlayListEntry entry : jsidplay2service.getPlayList()) {
                playListTab.addRow(entry);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };

    @Override
    public void play(int currentSong, PlayListEntry entry) {
        playListTab.gotoRow(currentSong);
        sidTab.requestSidDetails(entry.getResource());
    }

}
