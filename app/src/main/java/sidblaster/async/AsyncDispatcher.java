package sidblaster.async;

import android.content.Context;

import com.ftdi.j2xx.D2xxManager;

import sidblaster.Command;
import sidblaster.CommandEnum;
import sidblaster.ICommandDispatcher;

public class AsyncDispatcher implements ICommandDispatcher {

	private ThreadCommandReceiver receiver;
	private Thread sidWriteThread;
	private boolean isInitialized;
	private Context context;

	public AsyncDispatcher(Context context) {
		this.context = context;
	}

	@Override
	public int sendCommand(Command cmd) throws InterruptedException, D2xxManager.D2xxException {
		ensureInitialized(context);
		int retval = 0;
		while (!sidWriteThread.isAlive()) {
			Thread.yield();
		}
		if (cmd.getCommand() == CommandEnum.Flush) {
			receiver.flush();

			while (!receiver.queueIsEmpty()) {
				Thread.yield();
			}
			return 0;
		}
		receiver.tryPutCommand(cmd);

		if (cmd.getCommand() == CommandEnum.Read) {
			// XXX blocking
			while (!receiver.isReadResultReady()) {
				Thread.yield();
			}
			retval = receiver.readResult();
		}
		return retval;
	}

	@Override
	public void initialize() {
		isInitialized = false;
	}

	public void ensureInitialized(Context context) throws D2xxManager.D2xxException {
		if (!isInitialized) {
			assert (receiver == null);
			receiver = new ThreadCommandReceiver(context);
			sidWriteThread = new Thread(receiver);
			sidWriteThread.setDaemon(true);
			sidWriteThread.start();
			if (sidWriteThread.isAlive()) {
				while (!receiver.isDevicesAvailable()) {
					Thread.yield();
				}
			}
			isInitialized = true;
		}
	}

	@Override
	public void uninitialize() throws InterruptedException {
		if (isInitialized) {
			receiver.setAbortSIDWriteThread();
			sidWriteThread.join();
			receiver.uninitialize();
			receiver = null;
			isInitialized = false;
		}
	}

	@Override
	public boolean isAsync() {
		return true;
	}

	@Override
	public int deviceCount() throws D2xxManager.D2xxException {
		ensureInitialized(context);
		return receiver.deviceCount();
	}

	@Override
	public void setWriteBufferSize(int bufferSize) throws D2xxManager.D2xxException {
		ensureInitialized(context);
		receiver.setWriteBufferSize(bufferSize);
	}

}
