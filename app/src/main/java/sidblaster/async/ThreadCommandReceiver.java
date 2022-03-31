package sidblaster.async;

import android.content.Context;

import com.ftdi.j2xx.D2xxManager;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import sidblaster.Command;
import sidblaster.CommandEnum;

public class ThreadCommandReceiver extends CommandReceiver implements Runnable {

	private BlockingQueue<Command> queue = new ArrayBlockingQueue<Command>(256);

	private boolean flush;

	private volatile boolean devicesAvailable;

	public ThreadCommandReceiver(Context context) throws D2xxManager.D2xxException {
		super(context);
	}

	@Override
	public void run() {
		try {
			for (int deviceNum = 0; deviceNum < deviceCount(); ++deviceNum) {
				executeCommand(new Command(deviceNum, CommandEnum.OpenDevice));
			}
			devicesAvailable = true;
			while (!abortSIDWriteThread) {
				while (commandsPending()) {
					executeCommand(tryGetCommand());
				}
			}
			for (int deviceNum = 0; deviceNum < deviceCount(); ++deviceNum) {
				executeCommand(new Command(deviceNum, CommandEnum.CloseDevice));
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public boolean commandsPending() {
		return !queueIsEmpty() || flush;
	}

	public boolean queueIsEmpty() {
		return queue.isEmpty();
	}

	public void flush() {
		flush = true;
	}

	public void tryPutCommand(Command params) throws InterruptedException {
		queue.put(params);
	}

	private Command tryGetCommand() {
		if (flush) {
			flush = false;
			queue.clear();
			return new Command(0, CommandEnum.Flush);
		}
		return queue.poll();
	}

	public boolean isDevicesAvailable() {
		return devicesAvailable;
	}
}
