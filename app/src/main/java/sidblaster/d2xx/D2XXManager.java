package sidblaster.d2xx;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import com.ftdi.j2xx.D2xxManager;
import com.ftdi.j2xx.FT_Device;

import sidblaster.SIDType;

public class D2XXManager {

	private final static int FT_READ_TIMEOUT = 1000;

	private List<FT_Device> devices = new ArrayList<>();

	private static D2XXManager instance = null;
	
	private D2xxManager manager;

	public static D2XXManager getInstance() {
		if (instance == null)
			instance = new D2XXManager();
		return instance;
	}

	public void createDeviceList(Context mContext) throws D2xxManager.D2xxException {
		manager = D2xxManager.getInstance(mContext);

		int devCount = manager.createDeviceInfoList(mContext);

		D2xxManager.FtDeviceInfoListNode[] deviceList = new D2xxManager.FtDeviceInfoListNode[devCount];
		manager.getDeviceInfoList(devCount, deviceList);
		if (devCount <= 0) {
			return;
		}
		D2xxManager.DriverParameters driveParameters = new D2xxManager.DriverParameters();
		driveParameters.setReadTimeout(FT_READ_TIMEOUT);
		FT_Device ft_device = manager.openByIndex(mContext, 0, driveParameters);
		D2XXDevice.initialize(ft_device);

		displayDevicesInfo();
		devices.add(ft_device);
	}

	public int count() {
		return devices.size();
	}

	public FT_Device getDevice(int index) {
		return devices.get(index);
	}

	public void displayDevicesInfo() {
		System.out.printf("===================================\n");
		System.out.printf("Devices: %d\n", devices.size());
		System.out.printf("===================================\n");
		for (FT_Device ftDevice : devices) {
			D2XXDevice.displayInfo(ftDevice);
			System.out.printf("===================================\n");
		}
	}

	public String GetSerialNo(int index) {
		return devices.get(index).getDeviceInfo().serialNumber;
	}

	public SIDType GetSIDType(int index) {
		return D2XXDevice.getSIDType(devices.get(index));
	}

}
