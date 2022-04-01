package exsid;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/** Clock selection values for exSID_clockselect() */
public enum ClockSelect {
	/**
	 * select PAL clock
	 */
	XS_CL_PAL(0),
	/**
	 * select NTSC clock
	 */
	XS_CL_NTSC(1),
	/**
	 * select 1MHz clock
	 */
	XS_CL_1MHZ(2),;

	private int clockSelect;

	private ClockSelect(int clockSelect) {
		this.clockSelect = clockSelect;
	}

	public int getClockSelect() {
		return clockSelect;
	}

	private static final Map<Integer, ClockSelect> lookup;
	static {
		Map<Integer, ClockSelect> result = new HashMap<>();
		for (ClockSelect audioOp: ClockSelect.values()) {
			result.put(audioOp.getClockSelect(), audioOp);
		}
		lookup = result;
	}

	public static ClockSelect get(int clockSelect) {
		return lookup.get(clockSelect);
	}
}