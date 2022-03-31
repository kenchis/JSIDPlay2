package sidblaster;

public abstract class ISIDBlaster {

	public static final double PAL_CLOCK = 985248.4;
	public static final double NTSC_CLOCK = 1022727.14;

	public static final int MIN_WRITE_BUFFER_SIZE = 0;
	public static final int DEFAULT_WRITE_BUFFER_SIZE = 16;
	public static final int MAX_WRITE_BUFFER_SIZE = 256;

	private int m_DeviceID;

	public ISIDBlaster(int deviceID) {
		m_DeviceID = deviceID;
	}

	public int deviceID() {
		return m_DeviceID;
	}

	public abstract int getWriteBufferSize();

	public abstract void setWriteBufferSize(int size);

	public abstract void open();

	public abstract void close();

	public abstract void reset();

	public abstract byte read(byte reg);

	public abstract void mute(byte ch);

	public abstract void muteAll();

	public abstract void sync();

	public abstract void delay();

	public abstract void write(byte reg, byte data);

	public abstract void bufferWrite(byte reg, byte data);

	public abstract void flush();

	public abstract void softFlush();

}
