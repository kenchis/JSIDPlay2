package sidblaster.async;

import android.content.Context;

import com.ftdi.j2xx.D2xxManager;

import java.util.ArrayList;
import java.util.List;

import sidblaster.Command;
import sidblaster.ISIDBlaster;
import sidblaster.d2xx.SIDBlasterEnumerator;

/**
 * 
 * @author ken
 *
 */
public class CommandReceiver {

	private final List<ISIDBlaster> devices = new ArrayList<>();

	private boolean isReadResultReady;
	private int readResult;

	protected volatile boolean abortSIDWriteThread;

	public CommandReceiver(Context context) throws D2xxManager.D2xxException {
		int deviceCount = SIDBlasterEnumerator.getInstance().deviceCount(context);
		for (int i = 0; i < deviceCount; ++i) {
			devices.add(SIDBlasterEnumerator.getInstance().createInterface(i));
		}
	}

	public void setWriteBufferSize(int bufferSize) {
		for (ISIDBlaster sidBlasterInterface : devices) {
			sidBlasterInterface.setWriteBufferSize(bufferSize);
		}
	}

	public void setAbortSIDWriteThread() {
		abortSIDWriteThread = true;
	}

	public int deviceCount() {
		return devices.size();
	}

	public boolean isReadResultReady() {
		return isReadResultReady;
	}

	public int readResult() {
		isReadResultReady = false;
		return readResult;
	}

	protected int executeCommand(Command command) throws InterruptedException {
		if (command == null || devices.isEmpty()) {
			return 0;
		}
		long nanos = command.getDelay();
		if (nanos > 0) {
			long millis = nanos / 1000000;
			if (millis >= 3) {
				long sleepTime = millis - 3;
				Thread.sleep(sleepTime);
				nanos -= sleepTime * 1000000;
			}
			nsleep(nanos);
		}
		boolean bufferedWrites = devices.get(0).getWriteBufferSize() > 0;
		int retval = 0;
		int device = command.getDevice();
		if (device >= 0 && device < devices.size()) {
			ISIDBlaster sidblaster = devices.get(device);
			switch (command.getCommand()) {
			case OpenDevice:
				sidblaster.open();
				break;
			case CloseDevice:
				sidblaster.close();
				break;
			case Write:
				if (bufferedWrites) {
					sidblaster.bufferWrite(command.getReg(), command.getData());
				} else {
					sidblaster.write(command.getReg(), command.getData());
				}
				break;
			case Read:
				byte data = sidblaster.read(command.getReg());
				readResult = data | (data << 8);
				isReadResultReady = true;
				break;
			case Mute:
				sidblaster.mute(command.getReg());
				break;
			case MuteAll:
				sidblaster.muteAll();
				break;
			case Sync:
				sidblaster.sync();
				break;
			case SoftFlush:
				sidblaster.softFlush();
				break;
			case Flush:
				sidblaster.flush();
				break;
			case Reset:
				sidblaster.reset();
				break;
			default:
			}
		}
		return retval;
	}

	private void nsleep(long delayNs) {
		long start = System.nanoTime();
		long end = 0;
		do {
			end = System.nanoTime();
		} while (start + delayNs >= end);
	}

	public void uninitialize() {
		for (ISIDBlaster sidBlasterInterface : devices) {
			SIDBlasterEnumerator.getInstance().releaseInterface(sidBlasterInterface);
		}
		devices.clear();
	}

}
