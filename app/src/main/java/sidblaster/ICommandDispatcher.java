package sidblaster;

import com.ftdi.j2xx.D2xxManager;

public abstract interface ICommandDispatcher {

	public abstract int sendCommand(Command cmd) throws InterruptedException, D2xxManager.D2xxException;

	public abstract boolean isAsync();

	public abstract void initialize();

	public abstract void uninitialize() throws InterruptedException;

	public abstract int deviceCount() throws D2xxManager.D2xxException;

	public abstract void setWriteBufferSize(int bufferSize);

}
