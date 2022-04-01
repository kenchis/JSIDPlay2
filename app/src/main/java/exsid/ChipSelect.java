package exsid;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/** Chip selection values for exSID_chipselect() */
public enum ChipSelect {
	/**
	 * 6581
	 */
	XS_CS_CHIP0(0),
	/**
	 * 8580
	 */
	XS_CS_CHIP1(1),
	/**
	 * Both chips. XXX Invalid for reads: undefined behavior!
	 */
	XS_CS_BOTH(2),;

	private int chipSelect;

	private ChipSelect(int chipSelect) {
		this.chipSelect = chipSelect;
	}

	public int getChipSelect() {
		return chipSelect;
	}

	private static final Map<Integer, ChipSelect> lookup;
	static {
		Map<Integer, ChipSelect> result = new HashMap<>();
		for (ChipSelect audioOp: ChipSelect.values()) {
			result.put(audioOp.getChipSelect(), audioOp);
		}
		lookup = result;
	}

	public static ChipSelect get(int chipSelect) {
		return lookup.get(chipSelect);
	}
}