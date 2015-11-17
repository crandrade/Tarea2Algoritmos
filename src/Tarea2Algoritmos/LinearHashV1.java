package Tarea2Algoritmos;

import java.io.IOException;
import java.util.LinkedList;

public class LinearHashV1 extends LinearHash implements DiskMemoryManager {
	
	
	private int uncheckedInsertOperations = 0;
	private int uncheckedDeleteOperations = 0;
	
	public LinearHashV1() {
		try {
			dSimulator = new DiskSimulator();
		} catch (IOException e) {
			System.out.println("Linear Hash File Not found" +  e.toString());
		}
		buckets = new LinkedList<>();
		buckets.add(new Bucket(dSimulator));
	}
	
	
	/* No expando a cada operacion, saber el nivel de ocupacion es caro */
	private void checkMaybeExpand() {
		if (uncheckedInsertOperations > 20) {
			uncheckedInsertOperations = 0;
			if (dSimulator.getOccupation() > 0.8)
				expand();
		} else {
			uncheckedInsertOperations++;
		}
	}
	
	private void checkMaybeCompress() {
		if (uncheckedDeleteOperations > 20) {
			uncheckedDeleteOperations = 0;
			if (dSimulator.getOccupation() < 0.6)
				compress();
		} else {
			uncheckedDeleteOperations++;
		}
	}
	
	
	@Override
	public String find(String chain) {
		Bucket bToSearch = getBucket(chain);
		return bToSearch.searchInBucket(chain);
	}

	@Override
	public boolean delete(String chain) {
		/* Cuando borro debo chequear la tasa de ocupacion para contraerme*/
		Bucket bToDelete = getBucket(chain);
		boolean toReturn = bToDelete.deleteInBucket(chain);
		
		checkMaybeCompress();
		return toReturn;
	}

	@Override
	public void add(String chain) {
		/* Cuando agrego chequeo tasa de ocupacion para expandirme */
		Bucket bToInsert = getBucket(chain);
		bToInsert.insertInBucket(chain);
		
		/* Check ocupation - expand */
		checkMaybeExpand();
	}
	

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
