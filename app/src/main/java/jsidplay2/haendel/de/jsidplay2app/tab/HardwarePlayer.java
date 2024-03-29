package jsidplay2.haendel.de.jsidplay2app.tab;

import android.content.Context;
import android.hardware.usb.UsbManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.util.Pair;

import com.ftdi.j2xx.D2xxManager;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import exsid.AudioOp;
import exsid.ChipSelect;
import exsid.ClockSelect;
import exsid.ExSID;
import hardsid.HardSIDUSB;
import hardsid.SysMode;
import jsidplay2.haendel.de.jsidplay2app.config.IConfiguration;
import sidblaster.hardsid.HardSIDImpl;
import sidblaster.hardsid.WState;

public abstract class HardwarePlayer extends AsyncTask<Uri, Void, Boolean> {

    private static HardSIDImpl hardSID;
    private static ExSID exSID;
    private static HardSIDUSB hardSIDusb;
    private static HardwarePlayerType type;
    private static int chipCount;

    private final UsbManager usbManager;
    private final IConfiguration configuration;

    private List<Pair<String, String>> tuneInfos;

    private int lastBase;

    private Throwable throwable;

    public static HardwarePlayerType getType(Context context, UsbManager usbManager) {
        if (type == null) {
            try {
                determineType(usbManager, context);
            } catch (D2xxManager.D2xxException e) {
                Log.e(HardwarePlayer.class.getSimpleName(), "Error determine type", e);
            }
        }
        return type;
    }

    public HardwarePlayer(UsbManager usbManager, IConfiguration configuration) {
        this.usbManager = usbManager;
        this.configuration = configuration;
    }

    public void setTuneInfos(List<Pair<String, String>> tuneInfos) {
        this.tuneInfos = tuneInfos;
    }

    @Override
    protected Boolean doInBackground(Uri... uris) {
        Log.d("HardwarePlayer", "init!");

        boolean stereo = false;
        String model= "MOS6581";
        String stereoModel= "UNKNOWN";

        for (Pair<String, String> r : tuneInfos) {
            if (r.first.equals("HVSCEntry.sidModel1")) {
                model = r.second;
            } else if (r.first.equals("HVSCEntry.sidModel2")) {
                stereoModel = r.second;
            } else if (r.first.equals("HVSCEntry.sidChipBase2")) {
                stereo = !r.second.equals("0");
            }
        }
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(uris[0].toString()).openConnection();
            conn.setUseCaches(false);
            conn.setRequestMethod("GET");
            conn.setChunkedStreamingMode(8192);

            int statusCode = conn.getResponseCode();
            if (statusCode == HttpURLConnection.HTTP_OK) {

                if (type == HardwarePlayerType.EXSID) {
                    exSID.exSID_audio_op(AudioOp.XS_AU_MUTE.getAudioOp());
                    exSID.exSID_clockselect(ClockSelect.XS_CL_PAL.getClockSelect());
                    exSID.exSID_audio_op(AudioOp.XS_AU_6581_8580.getAudioOp());
                    exSID.exSID_audio_op(AudioOp.XS_AU_UNMUTE.getAudioOp());
                    exSID.exSID_reset((byte) 0x0f);

                    if (!stereo && configuration.isFakeStereo()) {
                        exSID.exSID_chipselect(ChipSelect.XS_CS_BOTH.getChipSelect());
                    } else {
                        exSID.exSID_chipselect(model.equals("MOS8580")? ChipSelect.XS_CS_CHIP1.getChipSelect() : ChipSelect.XS_CS_CHIP0.getChipSelect());
                    }
                } else if (type == HardwarePlayerType.SIDBLASTER) {
                    hardSID.HardSID_Lock((byte) 0);
                    hardSID.HardSID_Reset((byte) 0);
                } else {
                    hardSIDusb.hardsid_usb_init(usbManager, true, SysMode.SIDPLAY);
                }

                lastBase = -1;
                int hardsid6581 = Integer.parseInt(configuration.getHardSID6581()) - 1;
                int hardsid8580 = Integer.parseInt(configuration.getHardSID8580()) - 1;

                try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()), 8192)) {
                    String line = br.readLine();
                    while ((line = br.readLine()) != null && !isCancelled()) {
                        String[] cols = line.split(",");
                        int cycles = Integer.parseInt(cols[1].trim().substring(1, cols[1].length() - 1));
                        int base = Integer.parseInt(cols[2].trim().substring(2, cols[2].length() - 2), 16);
                        int reg = Integer.parseInt(cols[2].trim().substring(4, cols[2].length() - 1), 16);
                        int data = Integer.parseInt(cols[3].trim().substring(2, cols[3].length() - 1), 16);

                        if (type == HardwarePlayerType.EXSID) {
                            if ((reg & 0x1f) > 0x18) {
                                // "Ragga Run.sid" denies to work!
                                continue;
                            }
                            if (stereo && lastBase != base) {
                                if (base == 0xD40 || base == 0xD41) {
                                    exSID.exSID_chipselect(model.equals("MOS8580")? ChipSelect.XS_CS_CHIP1.getChipSelect() : ChipSelect.XS_CS_CHIP0.getChipSelect());
                                } else {
                                    exSID.exSID_chipselect(model.equals("MOS8580")? ChipSelect.XS_CS_CHIP0.getChipSelect() : ChipSelect.XS_CS_CHIP1.getChipSelect());
                                }
                                lastBase = base;
                            }
                            exSID.exSID_clkdwrite(cycles, (byte) (reg & 0x1f), (byte) data);
                        } else if (type== HardwarePlayerType.SIDBLASTER) {
                            while (hardSID.HardSID_Try_Write((byte) 0, (short) cycles, (byte) (reg & 0x1f),
                                    (byte) (data)) == WState.BUSY)
                                ;
                        } else {
                            int chipNum = 0;
                            if (base == 0xD40 || base == 0xD41) {
                                chipNum = model.equals("MOS8580")? hardsid8580 : hardsid6581;
                            } else if (chipCount > 1) {
                                chipNum = model.equals("MOS8580")? hardsid6581 : hardsid8580;
                                if (stereoModel.equals(model) && chipCount > 2) {
                                    for (int i = 0; i < chipCount; i++) {
                                        if (i != hardsid6581 && i != hardsid8580) {
                                            chipNum = i;
                                            break;
                                        }
                                    }
                                }
                            } else {
                                continue;
                            }
                            while (cycles > 0xffff) {
                                while (hardSIDusb.hardsid_usb_delay(0, 0xffff) == hardsid.WState.BUSY)
                                    ;
                                cycles -= 0xffff;
                            }
                            while (hardSIDusb.hardsid_usb_delay(0, cycles) == hardsid.WState.BUSY)
                                ;
                            while (hardSIDusb.hardsid_usb_write(0, (byte) ((chipNum << 5) | (reg & 0x1f)), (byte) data) == hardsid.WState.BUSY)
                                ;
                        }
                    }
                }
                Log.d("HardwarePlayer", "about to exit!");
                return isCancelled();
            }
        } catch (Throwable e) {
            this.throwable = e;
        } finally {
            if (type == HardwarePlayerType.EXSID) {
                exSID.exSID_reset((byte) 0x00);
            } else if (type == HardwarePlayerType.SIDBLASTER) {
                if (!isCancelled()) {
                    hardSID.HardSID_Flush((byte) 0);
                }
                hardSID.HardSID_Reset((byte) 0);
                hardSID.HardSID_Unlock((byte) 0);
            } else {
                for (int i=0; i < chipCount; i++) {
                    hardSIDusb.hardsid_usb_abortplay(0);
                    for (int reg = 0; reg < 0x32; reg++) {
                        while (hardSIDusb.hardsid_usb_write(0, (byte) ((i << 5) | reg), (byte) 0) == hardsid.WState.BUSY)
                            ;
                        while (hardSIDusb.hardsid_usb_delay(0, 4) == hardsid.WState.BUSY)
                            ;
                    }
                    while (hardSIDusb.hardsid_usb_write(0, (byte) ((i << 5) | 0xf), (byte) 0) == hardsid.WState.BUSY)
                        ;
                    while (hardSIDusb.hardsid_usb_flush(0) == hardsid.WState.BUSY)
                        ;
                }
            }
            Log.d("HardwarePlayer", "exit!");
        }
        return true;
    }

    protected void onPostExecute(Boolean aborted) {
        Log.d("HardwarePlayer", "onPostExecute init!");
        if (throwable != null) {
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            throwable.printStackTrace(new PrintWriter(bout));
            Log.e(HardwarePlayer.class.getSimpleName(), new String(bout.toByteArray()), throwable);
        } else {
            if (Boolean.FALSE.equals(aborted)) {
                end();
            }
        }
        Log.d("HardwarePlayer", "onPostExecute exit!");
    }

    private static void determineType(UsbManager usbManager, Context context) throws D2xxManager.D2xxException {
        type = HardwarePlayerType.NONE;
        if (hardSID == null) {
            hardSID = new HardSIDImpl(context);
            int deviceCount = hardSID.HardSID_Devices();
            hardSID.HardSID_SetWriteBufferSize((byte) 0);
            if (deviceCount > 0) {
                type = HardwarePlayerType.SIDBLASTER;
            }
        }
        if (exSID == null) {
            exSID = new ExSID();
            int exSidStatus = exSID.exSID_init(context);
            if (exSidStatus == 0) {
                type = HardwarePlayerType.EXSID;
            }
        }
        if (hardSIDusb == null) {
            hardSIDusb = new HardSIDUSB();
            hardSIDusb.hardsid_usb_init(usbManager, true, SysMode.SIDPLAY);
            if (hardSIDusb.hardsid_usb_getdevcount() > 0) {
                type = HardwarePlayerType.HARDSID;
            }
            chipCount = hardSIDusb.hardsid_usb_getsidcount(0);
        }
    }

    public abstract void end();

}
