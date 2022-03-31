package sidblaster.d2xx;

import com.ftdi.j2xx.D2xxManager;
import com.ftdi.j2xx.FT_Device;

import sidblaster.SIDType;

public class D2XXDevice {

	private final static int FT_READ_TIMEOUT = 1000;
	private final static int FT_WRITE_TIMEOUT = 1000;
	private final static int FT_BAUD_RATE = 500000; // FT_BAUD_115200

	public static void close(FT_Device device) {
		device.close();
	}

	public static int read(FT_Device device, byte[] buffer, int count) {
		return device.read(buffer, 0, count);
	}

	public static void initialize(FT_Device device) {
		device.setBaudRate(FT_BAUD_RATE);
		device.setDataCharacteristics(D2xxManager.FT_DATA_BITS_8, D2xxManager.FT_STOP_BITS_1, D2xxManager.FT_PARITY_NONE);
	}

	public static SIDType getSIDType(FT_Device device) {
		if (device.getDeviceInfo().description.startsWith("SIDBlaster/USB/")) {
			String sidType = device.getDeviceInfo().description.substring("SIDBlaster/USB/".length());
			if (sidType.equals("6581")) {
				return SIDType.MOS6581;
			} else if (sidType.equals("8580")) {
				return SIDType.MOS8580;
			}
		}
		return SIDType.NONE;
	}

	public static void displayInfo(FT_Device device) {
		System.out.printf("%18s%s\n", "FT Device type: ", device.getDeviceInfo().type);
		System.out.printf("%18s%s\n", "Serial number: ", device.getDeviceInfo().serialNumber);
		System.out.printf("%18s%s\n", "Description: ", device.getDeviceInfo().description);
		System.out.printf("%18s0x%08X\n", "VID&PID: ", device.getDeviceInfo().id);
		System.out.printf("%18s%d\n", "Is opened: ", device.isOpen() ? 1 : 0);
		System.out.printf("%18s0x%08X\n", "Location ID: ", device.getDeviceInfo().location);
	}

	public static void send(FT_Device device, byte[] buffer) {
		send(device, buffer, buffer.length);
	}

	public static void send(FT_Device device, byte[] buffer, int count) {
		if (count != 0) {
			device.write(buffer, count);
		}
	}

	public static byte[] receive(FT_Device device) {
		int rxBytes = device.getQueueStatus();
		if (rxBytes > 0) {
			byte buffer[] = new byte[rxBytes];
			read(device, buffer, rxBytes);
			return buffer;
		}
		return new byte[0];
	}
}
