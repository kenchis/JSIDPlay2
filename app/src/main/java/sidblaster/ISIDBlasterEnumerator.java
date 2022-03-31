package sidblaster;

import android.content.Context;

import com.ftdi.j2xx.D2xxManager;

public interface ISIDBlasterEnumerator {

	int deviceCount(Context context) throws D2xxManager.D2xxException;

	ISIDBlaster createInterface(int deviceID);

	void releaseInterface(ISIDBlaster sidblaster);

}
