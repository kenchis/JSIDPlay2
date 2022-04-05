package hardsid;

public enum DevType {
	UNKNOWN(0),
	/**
	 * HardSID4U
	 */
	HS4U(1),
	/**
	 * HardSID UPlay
	 */
	HSUP(2),
	/**
	 * HardSID Uno
	 */
	HSUNO(3), END(4);
	private int devType;

	private DevType(int devType) {
		this.devType = devType;
	}

	public int getDevType() {
		return devType;
	}
}