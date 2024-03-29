package hardsid;

import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Implements hardsid.dll api calls Written by Sandor Téli
 * 
 * Java port by Ken Händel
 * 
 * @author ken
 */
public class HardSIDUSB {

	private static final Logger LOGGER = Logger.getLogger(HardSIDUSB.class.getName());

	private static final int TIMEOUT = 0;

	private static final int USB_PACKAGE_SIZE = 512;

	private static final int WRITEBUFF_SIZE = USB_PACKAGE_SIZE;
	private static final int WRITEBUFF_SIZE_SYNC = 0x800;

	private static final int MAX_DEVCOUNT = 4;
	private static final int READBUFF_SIZE = 64;
	private static final int HW_BUFFBEG = 0x2000;
	private static final int HW_BUFFSIZE = 0x2000;
	private static final int HW_FILLRATIO = 0x1000;

	private static final int VENDOR_ID = 0x6581;
	private static final int HS4U_PRODUCT_ID = 0x8580;
	private static final int HSUP_PRODUCT_ID = 0x8581;
	private static final int HSUNO_PRODUCT_ID = 0x8582;

	private UsbInterface iface;
	private UsbDevice[] devhandles = new UsbDevice[MAX_DEVCOUNT];
	private UsbDeviceConnection conn;
	private UsbEndpoint[] inPipeBulk = new UsbEndpoint[MAX_DEVCOUNT];
	private UsbEndpoint[] outPipeBulk = new UsbEndpoint[MAX_DEVCOUNT];

	private DevType[] deviceTypes = new DevType[MAX_DEVCOUNT];
	private ByteBuffer[] writeBuffer = new ByteBuffer[MAX_DEVCOUNT];
	private byte lastaccsids[] = new byte[MAX_DEVCOUNT];

	private boolean initialized, error, sync, buffChk;

	private int deviceCount, bufferSize, pkgCount, playCursor, circBuffCursor;

	private short sysMode;

	private long lastRelaySwitch;

	public HardSIDUSB() {
		for (int i = 0; i < deviceTypes.length; i++) {
			deviceTypes[i] = DevType.UNKNOWN;
		}
		buffChk = true;
		bufferSize = WRITEBUFF_SIZE;
		playCursor = HW_BUFFBEG;
		circBuffCursor = HW_BUFFBEG;
	}

	/**
	 * initializes the management library
	 * 
	 * @param syncmode synchronous or asynchronous mode
	 * @param sysmode  SIDPLAY or VST
	 * @return init was ok or failed
	 */
	public boolean hardsid_usb_init(UsbManager usbManager, boolean syncmode, SysMode sysmode) {
		try {
			if (sysmode != SysMode.SIDPLAY) {
				throw new RuntimeException("Only SIDPLAY mode currently supported!");
			}
			if (!syncmode) {
				throw new RuntimeException("Only synchronous mode currently supported!");
			}
			boolean fnd = false;
			sync = syncmode;

			if (sync) {
				bufferSize = WRITEBUFF_SIZE_SYNC;
			} else {
				bufferSize = WRITEBUFF_SIZE;
			}

			if (initialized) {
				hardsid_usb_close();
			}

			initialized = true;
			error = false;

			deviceCount = 0;
			Log.d("HardSIDUSB", "get device list");
			Map<String, UsbDevice> deviceList = usbManager.getDeviceList();
			Iterator<UsbDevice> it = deviceList.values().iterator();
			while (it.hasNext()) {
				UsbDevice device = it.next();
				Log.d("HardSIDUSB", "device found");
				if (device.getVendorId() == VENDOR_ID && device.getProductId() == HS4U_PRODUCT_ID) {
					addDevice(usbManager, device, DevType.HS4U);
					fnd = true;
					break;
				} else if (device.getVendorId() == VENDOR_ID && device.getProductId() == HSUP_PRODUCT_ID) {
					addDevice(usbManager, device, DevType.HSUP);
					fnd = true;
					break;
				} else if (device.getVendorId() == VENDOR_ID && device.getProductId() == HSUNO_PRODUCT_ID) {
					addDevice(usbManager, device, DevType.HSUNO);
					fnd = true;
					break;
				}
			}
			Log.d("HardSIDUSB", "check for errors");
			if (!error && deviceCount > 0) {
				sync = true;
				hardsid_usb_setmode(0, sysmode); // incomplete device number handling...
				sync = syncmode;
			}
			Log.d("HardSIDUSB", "found = " + fnd);
			return fnd;
		} catch (IllegalArgumentException | SecurityException e) {
			e.printStackTrace();
			error = true;
			return false;
		}
	}

	/**
	 * closes the management library
	 */
	public void hardsid_usb_close() {
		try {
			int d = 0;
			if (initialized) {
//			if (!sync)
//				IsoStream(devhandles[0], true);

				for (d = 0; d < deviceCount; d++) {

					if (deviceTypes[d] == DevType.HSUP || deviceTypes[d] == DevType.HSUNO) {
						// wait 5ms
						while (hardsid_usb_delay(d, 5000) == WState.BUSY) {
							Thread.sleep(0);
						}
						// switch 5V on, start reset (POWER_DIS=0;RESET_DIS=1;MUTE_ENA=1)
						while (hardsid_usb_write_direct(d, (byte) 0xf0, (byte) 6) == WState.BUSY) {
							Thread.sleep(0);
						}
						// wait 60ms
						while (hardsid_usb_delay(d, 60000) == WState.BUSY) {
							Thread.sleep(0);
						}
						// switch 5V off (POWER_DIS=1;RESET_DIS=1;MUTE_ENA=1)
						while (hardsid_usb_write_direct(d, (byte) 0xf0, (byte) 7) == WState.BUSY) {
							Thread.sleep(0);
						}
						while (hardsid_usb_flush(d) == WState.BUSY) {
							Thread.sleep(0);
						}
						lastaccsids[d] = (byte) 0xff;
					}
					conn.releaseInterface(iface);
					conn.close();
				}
				initialized = false;
			}
		} catch (IllegalArgumentException | InterruptedException e) {
			e.printStackTrace();
			error = true;
		}
	}

	/**
	 * returns the number of USB HardSID devices plugged into the computer
	 * 
	 * @return number of USB HardSID devices
	 */
	public int hardsid_usb_getdevcount() {
		if (!initialized || error) {
			return 0;
		}
		return deviceCount;
	}

	/**
	 * returns the number of detected SID chips on the given device
	 * 
	 * @return number of detected SID chips on the given device
	 */
	public int hardsid_usb_getsidcount(int deviceId) {
		if (!initialized || error) {
			return 0;
		}
		switch (deviceTypes[deviceId]) {
		case HS4U:
			return 4;
		case HSUP:
			return 2;
		case HSUNO:
			return 1;
		default:
			return 0;
		}
	}

	private boolean addDevice(UsbManager usbManager, UsbDevice device, DevType devType) {

		Log.d("HardSIDUSB", "add device");
		iface = device.getInterface(0);

		if (devType == DevType.UNKNOWN) {
			error = true;
			return false;
		}
		deviceTypes[deviceCount] = devType;
		lastaccsids[deviceCount] = (byte) 0xff;

		int endpointCount = iface.getEndpointCount();
		Log.d("HardSIDUSB", String.valueOf(endpointCount));
		for (int i=0;i<endpointCount;i++) {
			UsbEndpoint endpoint = iface.getEndpoint(i);
			if (endpoint.getAddress() == 0x02) {
				outPipeBulk[deviceCount] = iface.getEndpoint(i);
			} else if (endpoint.getAddress() == 0x81) {
				inPipeBulk[deviceCount] = iface.getEndpoint(i);
			}
			Log.d("HardSIDUSB", String.valueOf(i));
			Log.d("HardSIDUSB", endpoint.toString());
			Log.d("HardSIDUSB", String.valueOf(endpoint.getAddress()));
		}

		conn = usbManager.openDevice(device);

		// try to forcefully claim USB device, if supported!
		if (!conn.claimInterface(iface, true)) {
			if (!conn.claimInterface(iface, false))  {
				error = true;
				return false;
			}
		}
		devhandles[deviceCount] = device;

		writeBuffer[deviceCount] = ByteBuffer.allocate(bufferSize).order(ByteOrder.LITTLE_ENDIAN);
		deviceCount++;
		return true;
	}

	/**
	 * Read state of USB device.
	 * 
	 * @param deviceId device ID
	 * @return state of USB device
	 */
	private WState hardsid_usb_readstate(int deviceId) {
		try {
			if (!initialized || error || !sync) {
				return WState.ERROR;
			}

			byte[] readbuff = new byte[READBUFF_SIZE];
			int nBytesRead = conn.bulkTransfer(inPipeBulk[deviceId], readbuff, readbuff.length, TIMEOUT);

			if (nBytesRead == 0) {
				error = true;
				return WState.ERROR;
			} else {
				ByteBuffer b = ByteBuffer.wrap(readbuff).order(ByteOrder.LITTLE_ENDIAN);
				ShortBuffer s = b.asShortBuffer();
//				int comm_reset_cnt = readbuff[0] & 0xffff;
//				int error_shadow = readbuff[1] & 0xffff;
//				int error_addr_shadow = readbuff[2] & 0xffff;
				pkgCount = s.get(12) & 0xffff;
				playCursor = s.get(13) & 0xffff;
				circBuffCursor = s.get(14) & 0xffff;
				sysMode = s.get(15);
				return WState.OK;
			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			error = true;
			return WState.ERROR;
		}
	}

	/**
	 * Sync with USB device.
	 * 
	 * @param deviceId device ID
	 * @return state of USB device
	 */
	private WState hardsid_usb_sync(int deviceId) {
		if (!initialized || error || !sync) {
			return WState.ERROR;
		}

		if (hardsid_usb_readstate(deviceId) != WState.OK) {
			error = true;
			return WState.ERROR;
		} else {
			int freespace;

			if (playCursor < circBuffCursor)
				freespace = playCursor + HW_BUFFSIZE - circBuffCursor;
			else if (playCursor > circBuffCursor)
				freespace = playCursor - circBuffCursor;
			else
				freespace = HW_BUFFSIZE;

			if (freespace < HW_FILLRATIO)
				return WState.BUSY;

			return WState.OK;
		}
	}

	/**
	 * perform the communication in async or sync mode
	 * 
	 * @param deviceId device ID
	 * @return state of USB device
	 */
	private WState hardsid_usb_write_internal(int deviceId) {
		try {
			if (!initialized || error || writeBuffer[deviceId].position() == 0) {
				return WState.ERROR;
			}

			int pkgstowrite = ((writeBuffer[deviceId].position() - 1) / USB_PACKAGE_SIZE) + 1;
			int writesize = pkgstowrite * USB_PACKAGE_SIZE;
			writeBuffer[deviceId].clear();

			if (!sync) {
				// stream based async Isoch stream feed
//			DeviceIoControl(devhandles[0],
//							IOCTL_ISOUSB_FEED_ISO_STREAM,
//							writebuffs[0],
//							writesize,
//							&dummy, //&gpStreamObj, //pointer to stream object initted when stream was started
//							sizeof( PVOID),
//							&nBytesWrite,
//							NULL);//&(ovls[oix]));
			} else {
				// sync mode direct file write

				byte[] toWrite = new byte[writesize];
				System.arraycopy(writeBuffer[deviceId].array(), 0, toWrite, 0, writesize);

				int sent = conn.bulkTransfer(outPipeBulk[deviceId], toWrite, writesize, TIMEOUT);
				if (sent != writesize) {
					throw new RuntimeException("Sent error!");
				}
			}
			return WState.OK;
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			error = true;
			return WState.ERROR;
		}
	}

	/**
	 * schedules a write command
	 * 
	 * @param deviceId device ID
	 * @param reg      register (chip select as high byte)
	 * @param data     register value
	 * @return state of USB device
	 */
	private WState hardsid_usb_write_direct(int deviceId, byte reg, byte data) {
		if (!initialized || error) {
			return WState.ERROR;
		}

		if (sync && (writeBuffer[deviceId].position() == (bufferSize - 2))) {
			WState ws = hardsid_usb_sync(deviceId);
			if (ws != WState.OK)
				return ws;
		}

		writeBuffer[deviceId].putShort((short) (((reg & 0xff) << 8) | (data & 0xff)));
		if (writeBuffer[deviceId].position() == bufferSize) {
			return hardsid_usb_write_internal(deviceId);
		}

		return WState.OK;
	}

	/**
	 * Write to USB device
	 * 
	 * @param deviceId device ID
	 * @param reg      register (chip select as high byte)
	 * @param data     register value
	 * @return state of USB device
	 */
	public WState hardsid_usb_write(int deviceId, byte reg, byte data) {
		try {
			byte newsidmask;

			switch (deviceTypes[deviceId]) {
			case HS4U:
				return hardsid_usb_write_direct(deviceId, reg, data);
			case HSUP:
				if ((reg & 0xc0) != 0) {
					// invalid SID number
					return WState.ERROR;
				} else {
					if (lastaccsids[deviceId] != (reg & 0x20)) {
						// writing to a new SID
						lastaccsids[deviceId] = (byte) (reg & 0x20);
						if ((reg & 0x20) != 0) {
							newsidmask = (byte) 0xc0;
						} else {
							newsidmask = (byte) 0xa0;
						}

						while (lastRelaySwitch > 0 && (System.currentTimeMillis() - lastRelaySwitch) < 250) {
							Thread.sleep(0);
						}
						// timediff = GetTickCount() - lastrelayswitch;
						lastRelaySwitch = System.currentTimeMillis();

						// runtime = GetTickCount();

						// wait 4usecs (not a real delay, but an init. delay command)
						while (hardsid_usb_delay(deviceId, 4) == WState.BUSY) {
							Thread.sleep(0);
						}
						// mute on (POWER_DIS=0;RESET_DIS=1;MUTE_ENA=1)
						while (hardsid_usb_write_direct(deviceId, (byte) 0xf0, (byte) 6) == WState.BUSY) {
							Thread.sleep(0);
						}
						// wait 60ms
						while (hardsid_usb_delay(deviceId, 60000) == WState.BUSY) {
							Thread.sleep(0);
						}
						// switch 5V off (POWER_DIS=1;RESET_DIS=1;MUTE_ENA=1)
						while (hardsid_usb_write_direct(deviceId, (byte) 0xf0, (byte) 7) == WState.BUSY) {
							Thread.sleep(0);
						}
						// wait 30ms
						while (hardsid_usb_delay(deviceId, 30000) == WState.BUSY) {
							Thread.sleep(0);
						}
						// relay switch
						while (hardsid_usb_write_direct(deviceId, newsidmask, (byte) 0) == WState.BUSY) {
							Thread.sleep(0);
						}
						// wait 30ms
						while (hardsid_usb_delay(deviceId, 30000) == WState.BUSY) {
							Thread.sleep(0);
						}
						// turn off the relay
						while (hardsid_usb_write_direct(deviceId, (byte) 0x80, (byte) 0) == WState.BUSY) {
							Thread.sleep(0);
						}
						// wait 30ms
						while (hardsid_usb_delay(deviceId, 30000) == WState.BUSY) {
							Thread.sleep(0);
						}
						// switch 5V on, start reset (POWER_DIS=0;RESET_DIS=0;MUTE_ENA=1)
						while (hardsid_usb_write_direct(deviceId, (byte) 0xf0, (byte) 4) == WState.BUSY) {
							Thread.sleep(0);
						}
						// wait 60ms
						while (hardsid_usb_delay(deviceId, 60000) == WState.BUSY) {
							Thread.sleep(0);
						}
						// end reset (POWER_DIS=0;RESET_DIS=1;MUTE_ENA=0)
						while (hardsid_usb_write_direct(deviceId, (byte) 0xf0, (byte) 2) == WState.BUSY) {
							Thread.sleep(0);
						}
						// security 10usec wait
						while (hardsid_usb_delay(deviceId, 10) == WState.BUSY) {
							Thread.sleep(0);
						}

						// send this all down to the hardware
						while (hardsid_usb_flush(deviceId) == WState.BUSY) {
							Thread.sleep(0);
						}

						/*
						 * timediff = GetTickCount() - runtime; if (timediff>=240) { Thread.sleep(0);
						 * //for breakpoint purposes }
						 */

						// writing to the SID
						return hardsid_usb_write_direct(deviceId, (byte) ((reg & 0x1f) | 0x80), data);
					} else
						// writing to the same SID as last time..
						return hardsid_usb_write_direct(deviceId, (byte) ((reg & 0x1f) | 0x80), data);
				}
			case HSUNO:
				if (lastaccsids[deviceId] == 0xff) {

					// first write, we need the 5V

					// indicate that we've enabled the 5V
					lastaccsids[deviceId] = 0x01;

					// wait 4usecs (not a real delay, but an init. delay command)
					while (hardsid_usb_delay(deviceId, 4) == WState.BUSY) {
						Thread.sleep(0);
					}
					// wait 5ms
					while (hardsid_usb_delay(deviceId, 5000) == WState.BUSY) {
						Thread.sleep(0);
					}
					// switch 5V on, start reset (POWER_DIS=0;RESET_DIS=0;MUTE_ENA=1)
					while (hardsid_usb_write_direct(deviceId, (byte) 0xf0, (byte) 4) == WState.BUSY) {
						Thread.sleep(0);
					}
					// wait 60ms
					while (hardsid_usb_delay(deviceId, 60000) == WState.BUSY) {
						Thread.sleep(0);
					}
					// end reset (POWER_DIS=0;RESET_DIS=1;MUTE_ENA=0)
					while (hardsid_usb_write_direct(deviceId, (byte) 0xf0, (byte) 2) == WState.BUSY) {
						Thread.sleep(0);
					}
					// security 10usec wait
					while (hardsid_usb_delay(deviceId, 10) == WState.BUSY) {
						Thread.sleep(0);
					}

					// send this all down to the hardware
					while (hardsid_usb_flush(deviceId) == WState.BUSY) {
						Thread.sleep(0);
					}

					// writing to the SID
					return hardsid_usb_write_direct(deviceId, (byte) ((reg & 0x1f) | 0x80), data);
				} else
					// writing to the SID normally..
					return hardsid_usb_write_direct(deviceId, (byte) ((reg & 0x1f) | 0x80), data);
			default:
				return WState.ERROR;
			}
		} catch (IllegalArgumentException
				| InterruptedException e) {
			e.printStackTrace();
			error = true;
			return WState.ERROR;
		}
	}

	/**
	 * schedules a delay command
	 * 
	 * @param deviceId device ID
	 * @param cycles   cycles to delay
	 * @return state of USB device
	 */
	public WState hardsid_usb_delay(int deviceId, int cycles) {
		try {
			if (!initialized || error) {
				return WState.ERROR;
			}

			if (cycles == 0) {
				// no command for zero delay
			} else if (cycles < 0x100) {
				// short delay
				return hardsid_usb_write_direct(deviceId, (byte) 0xee, (byte) (cycles & 0xff)); // short delay command
			} else if ((cycles & 0xff) == 0) {
				// long delay without low order byte
				return hardsid_usb_write_direct(deviceId, (byte) 0xef, (byte) (cycles >> 8)); // long delay command
			} else {
				// long delay with low order byte
				if (sync && (writeBuffer[deviceId].position() == (bufferSize - 2))) {
					WState ws;
					ws = hardsid_usb_write_direct(deviceId, (byte) 0xff, (byte) 0xff);
					if (ws != WState.OK)
						return ws;
				} else if (sync && (writeBuffer[deviceId].position() == (bufferSize - 4))) {
					WState ws;
					ws = hardsid_usb_sync(deviceId);
					if (ws != WState.OK)
						return ws;
				}
				hardsid_usb_write_direct(deviceId, (byte) 0xef, (byte) (cycles >> 8)); // long delay command
				hardsid_usb_write_direct(deviceId, (byte) 0xee, (byte) (cycles & 0xff)); // short delay command
			}

			return WState.OK;
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			error = true;
			return WState.ERROR;
		}
	}

	/**
	 * sends a partial package to the hardware
	 * 
	 * @param deviceId device ID
	 * @return state of USB device
	 */
	public WState hardsid_usb_flush(int deviceId) {
		try {
			if (!initialized || error) {
				return WState.ERROR;
			}

			if (writeBuffer[deviceId].position() > 0) {
				if (sync && buffChk) {
					WState ws;
					ws = hardsid_usb_sync(deviceId);
					if (ws != WState.OK)
						return ws;
				}
				if ((writeBuffer[deviceId].position() % bufferSize) > 0) {
					writeBuffer[deviceId].putShort((short) 0xffff);
				}
				hardsid_usb_write_internal(deviceId);
			}

			return WState.OK;
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			error = true;
			return WState.ERROR;
		}
	}

	/**
	 * aborts the playback ASAP
	 * 
	 * @param deviceId device ID
	 */
	public void hardsid_usb_abortplay(int deviceId) {

		try {
			// fixed: 2010.01.26 - after Wilfred's testcase
			// abort the software buffer anyway
			writeBuffer[deviceId].clear();

			if (hardsid_usb_readstate(deviceId) != WState.OK) {
				error = true;
				return;
			}

			if (pkgCount == 0) {
				return;
			}
			hardsid_usb_write_direct(deviceId, (byte) 0xff, (byte) 0xff);
			hardsid_usb_write_direct(deviceId, (byte) 0xff, (byte) 0xff);
			hardsid_usb_write_internal(deviceId);
			while (true) {
				if (hardsid_usb_readstate(deviceId) != WState.OK) {
					error = true;
					break;
				} else {
					if (pkgCount == 0) {
						break;
					}
				}
			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			error = true;
		}
	}

	/**
	 * selects one of the sysmodes on the device
	 * 
	 * @param deviceId   device ID
	 * @param newsysmode SIDPLAY or VST
	 * @return state of USB device
	 */
	public WState hardsid_usb_setmode(int deviceId, SysMode newsysmode) {
		try {
			if (newsysmode != SysMode.SIDPLAY) {
				throw new RuntimeException("Only SIDPLAY mode currently supported!");
			}
			if (newsysmode == SysMode.VST) {
				error = true;
				return WState.ERROR;
			}

			if (hardsid_usb_readstate(deviceId) != WState.OK) {
				error = true;
				return WState.ERROR;
			}

			if ((sysMode & 0x0f) == newsysmode.getSysMode())
				return WState.OK;

			hardsid_usb_abortplay(deviceId);

			hardsid_usb_write_direct(deviceId, (byte) 0xff, (byte) 0xff);
			hardsid_usb_write_direct(deviceId, (byte) 0x00, (byte) newsysmode.getSysMode());
			hardsid_usb_write_internal(deviceId);
			while (true) {
				if (hardsid_usb_readstate(deviceId) != WState.OK) {
					error = true;
					break;
				} else {
					if (sysMode == (newsysmode.getSysMode() | 0x80)) {
						break;
					}
				}
			}
			if (error)
				return WState.ERROR;
			else
				return WState.OK;
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			error = true;
			return WState.ERROR;
		}
	}

}
