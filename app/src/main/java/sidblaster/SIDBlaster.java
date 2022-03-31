package sidblaster;

import static java.lang.Integer.valueOf;
import static java.lang.System.getProperty;

import java.nio.ByteBuffer;
import com.ftdi.j2xx.FT_Device;

import sidblaster.d2xx.D2XXDevice;

public class SIDBlaster extends ISIDBlaster {

	private Integer SIDBLASTERUSB_WRITEBUFFER_SIZE = valueOf(
			getProperty("SIDBLASTERUSB_WRITEBUFFER_SIZE", String.valueOf(DEFAULT_WRITE_BUFFER_SIZE)));

	private ByteBuffer buffer = ByteBuffer.allocate(
			SIDBLASTERUSB_WRITEBUFFER_SIZE != null ? SIDBLASTERUSB_WRITEBUFFER_SIZE : DEFAULT_WRITE_BUFFER_SIZE);

	private final FT_Device sid;

	public SIDBlaster(int deviceID, FT_Device device) {
		super(deviceID);
		sid = device;
	}

	@Override
	public int getWriteBufferSize() {
		return buffer.capacity();
	}

	@Override
	public void setWriteBufferSize(int size) {
		buffer = ByteBuffer.allocate(Math.max(MIN_WRITE_BUFFER_SIZE, Math.min(size, MAX_WRITE_BUFFER_SIZE)));
	}

	@Override
	public void open() {
	}

	@Override
	public void close() {
		if (sid.isOpen()) {
			reset();
			D2XXDevice.close(sid);
		}
	}

	@Override
	public void sync() {
		softFlush();
	}

	@Override
	public byte read(byte reg) {
		softFlush();
		D2XXDevice.send(sid, new byte[] { (byte) (reg | 0xa0) }, 1);
		byte[] received;
		do {
			received = D2XXDevice.receive(sid);
		} while (received.length == 0);
		return received[0];
	}

	@Override
	public void mute(byte ch) {
		softFlush();
		D2XXDevice.send(sid, new byte[] { (byte) ((ch * 7 + 0) | 0xe0), 0, (byte) (ch * 7 + 1), 0 });
	}

	@Override
	public void muteAll() {
		softFlush();
		D2XXDevice.send(sid, new byte[] { (byte) 0xe0, 0, (byte) 0xe1, 0, (byte) 0xe7, 0, (byte) 0xe8, 0, (byte) 0xee,
				0, (byte) 0xef, 0 });
	}

	@Override
	public void reset() {
		flush();
		sync();
		muteAll();
	}

	@Override
	public void write(byte reg, byte data) {
		if (buffer.position() > 0) {
			softFlush();
		}
		D2XXDevice.send(sid, new byte[] { (byte) (reg | 0xe0), data });
	}

	@Override
	public void bufferWrite(byte reg, byte data) {
		if (!buffer.hasRemaining()) {
			softFlush();
		}
		buffer.put(new byte[] { (byte) (reg | 0xe0), data });
	}

	@Override
	public void delay() {
	}

	@Override
	public void flush() {
		buffer.clear();
	}

	@Override
	public void softFlush() {
		D2XXDevice.send(sid, buffer.array(), buffer.position());
		buffer.clear();
	}

	@Override
	public String toString() {
		return sid.getDeviceInfo().serialNumber;
	}
}