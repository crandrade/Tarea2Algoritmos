package Tarea2Algoritmos;

import java.io.IOException;
import java.util.LinkedList;

public class BTree implements DiskMemoryManager{
	
	private DiskSimulator dSimulator;
	

	@Override
	public float getOccupation() {
		return dSimulator.getOccupation();
	}

	@Override
	public int getIOs() {
		return dSimulator.getIOs();
	}

	@Override
	public void resetIOs() {
		dSimulator.resetIOs();
	}

}
