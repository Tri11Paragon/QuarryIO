package me.tpgc.quarryio.util;

import net.minecraftforge.energy.EnergyStorage;

public class QuarryEnergyStorage extends EnergyStorage {

	public QuarryEnergyStorage(int capacity, int maxReceive) {
		super(capacity, maxReceive, 0, 0);
	}
	
	public boolean consumeEnergy(int amount) {
		if (energy < amount)
			return false;
        energy -= amount;
        return true;
	}

}
