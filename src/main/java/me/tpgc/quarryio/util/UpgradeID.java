package me.tpgc.quarryio.util;

public enum UpgradeID {

	EFFICIENCY1(0),
	EFFICIENCY2(1),
	EFFICIENCY3(2),
	EFFICIENCY4(3),
	EFFICIENCY5(4),
	SILKTOUCH(5),
	FORTUNE1(6),
	FORTUNE2(7),
	FORTUNE3(8);
	
	int id;
	UpgradeID(int id){
		this.id = id;
	}
	
	public int getID() {
		return id;
	}
	
}
