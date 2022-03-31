package sidblaster.d2xx;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import com.ftdi.j2xx.D2xxManager;
import com.ftdi.j2xx.FT_Device;

import sidblaster.ISIDBlaster;
import sidblaster.ISIDBlasterEnumerator;
import sidblaster.SIDBlaster;

public class SIDBlasterEnumerator implements ISIDBlasterEnumerator {

	private List<FT_Device> devices;
	private List<Boolean> devicesAllocated;

	private static SIDBlasterEnumerator INSTANCE = new SIDBlasterEnumerator();

	public static SIDBlasterEnumerator getInstance() {
		return INSTANCE;
	}

	@Override
	public int deviceCount(Context context) throws D2xxManager.D2xxException {
		D2XXManager manager = D2XXManager.getInstance();
		manager.createDeviceList(context);
		devices = new ArrayList<>();
		devicesAllocated = new ArrayList<>();
		for (int i = 0; i < manager.count(); ++i) {
			devices.add(manager.getDevice(i));
			devicesAllocated.add(false);
		}
		return devices.size();
	}

	@Override
	public ISIDBlaster createInterface(int deviceID) {
		if (deviceID < devices.size()) {
			assert (!devicesAllocated.get(deviceID));
			FT_Device sid = devices.get(deviceID);
			devicesAllocated.set(deviceID, true);
			return new SIDBlaster(deviceID, sid);
		}
		return null;
	}

	@Override
	public void releaseInterface(ISIDBlaster sidblaster) {
		if (sidblaster.deviceID() < devices.size()) {
			devicesAllocated.set(sidblaster.deviceID(), false);
		}
	}

}
