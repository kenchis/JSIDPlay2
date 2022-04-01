package exsid;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/** Hardware model return values for exSID_hwmodel() */
public enum HardwareModel {
	/**
	 * exSID USB
	 */
	XS_MD_STD(0),
	/**
	 * exSID+ USB
	 */
	XS_MD_PLUS(1),;

	private int hardwareModel;

	private HardwareModel(int hardwareModel) {
		this.hardwareModel = hardwareModel;
	}

	public int getHardwareModel() {
		return hardwareModel;
	}

	private static final Map<Integer, HardwareModel> lookup;
	static {
		Map<Integer, HardwareModel> result = new HashMap<>();
		for (HardwareModel audioOp: HardwareModel.values()) {
			result.put(audioOp.getHardwareModel(), audioOp);
		}
		lookup = result;
	}
	public static HardwareModel get(int hardwareModel) {
		return lookup.get(hardwareModel);
	}
}