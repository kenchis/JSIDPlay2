package jsidplay2.haendel.de.jsidplay2app.tab;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Process;
import android.util.Log;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import exsid.AudioOp;
import exsid.ChipSelect;
import exsid.ClockSelect;
import exsid.ExSID;
import sidblaster.hardsid.HardSIDImpl;
import sidblaster.hardsid.WState;

public abstract class HardwarePlayer extends AsyncTask<Uri, Void, Boolean> {

    private static HardSIDImpl hardSID;
    private static ExSID exSID;
    private static HardwarePlayerType type;

    private volatile State state = State.QUIT;

    private Throwable throwable;

    public static HardwarePlayerType getType(Context context) {
        if (type == null) {
            determineType(context);
        }
        return type;
    }

    @Override
    protected Boolean doInBackground(Uri... uris) {
        Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND + Process.THREAD_PRIORITY_MORE_FAVORABLE);

        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(uris[0].toString()).openConnection();
            conn.setUseCaches(false);
            conn.setRequestMethod("GET");
            conn.setChunkedStreamingMode(8192);

            int statusCode = conn.getResponseCode();
            if (statusCode == HttpURLConnection.HTTP_OK) {

                state = State.INIT;
                if (type == HardwarePlayerType.EXSID) {
                    exSID.exSID_audio_op(AudioOp.XS_AU_MUTE.getAudioOp());
                    exSID.exSID_clockselect(ClockSelect.XS_CL_PAL.getClockSelect());
                    exSID.exSID_audio_op(AudioOp.XS_AU_6581_8580.getAudioOp());
                    exSID.exSID_audio_op(AudioOp.XS_AU_UNMUTE.getAudioOp());
                    exSID.exSID_reset((byte) 0x0f);
                    exSID.exSID_chipselect(ChipSelect.XS_CS_BOTH.getChipSelect());
                } else {
                    hardSID.HardSID_Lock((byte) 0);
                    hardSID.HardSID_Reset((byte) 0);
                }

                state = State.PLAY;
                try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()), 8192)) {
                    String line = br.readLine();
                    while ((line = br.readLine()) != null && state == State.PLAY) {
                        String[] cols = line.split(",");
                        int cycles = Integer.parseInt(cols[1].trim().substring(1, cols[1].length() - 1));
                        int reg = Integer.parseInt(cols[2].trim().substring(2, cols[2].length() - 1), 16);
                        int data = Integer.parseInt(cols[3].trim().substring(2, cols[3].length() - 1), 16);

                        if (type == HardwarePlayerType.EXSID) {
                            exSID.exSID_clkdwrite(cycles, (byte) (reg & 0x1f), (byte) data);
                        } else {
                            while (hardSID.HardSID_Try_Write((byte) 0, (short) cycles, (byte) (reg & 0x1f),
                                    (byte) (data)) == WState.BUSY)
                                ;
                        }
                    }
                }
                return state == State.ABORT;
            }
        } catch (Throwable e) {
            this.throwable = e;
        } finally {
            if (type == HardwarePlayerType.EXSID) {
                exSID.exSID_reset((byte) 0x00);
            } else {
                if (state != State.ABORT) {
                    hardSID.HardSID_Flush((byte) 0);
                }
                hardSID.HardSID_Reset((byte) 0);
                hardSID.HardSID_Unlock((byte) 0);
            }
            state = State.QUIT;
        }
        return true;
    }

    protected void onPostExecute(Boolean aborted) {
        if (throwable != null) {
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            throwable.printStackTrace(new PrintWriter(bout));
            Log.e(HardwarePlayer.class.getSimpleName(), new String(bout.toByteArray()), throwable);
        } else {
            if (Boolean.FALSE.equals(aborted)) {
                end();
            }
        }
    }

    public void terminate() {
        while(state != State.QUIT) {
            state = State.ABORT;
            Thread.yield();
        }
    }

    private static void determineType(Context context) {
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
    }

    public abstract void end();

}
