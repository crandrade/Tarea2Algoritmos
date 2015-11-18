package Tarea2Algoritmos;

import java.io.IOException;
import java.util.LinkedList;


public class LinearHashV2 extends LinearHash implements DiskMemoryManager {
	
	private long averageQueryTime = 0;
	private long numberOfQueries = 0;
	
	public LinearHashV2() {
		try {
			dSimulator = new DiskSimulator();
		} catch (IOException e) {
			System.out.println("Linear Hash File Not found" +  e.toString());
		}
		buckets = new LinkedList<>();
		buckets.add(new Bucket(dSimulator));
	}
	
	
	@Override
	public String find(String chain) {
		long initTime = System.nanoTime();
		Bucket bToSearch = getBucket(chain);
		String toReturn = bToSearch.searchInBucket(chain);
		if (toReturn != null) {
			long finalTime = System.nanoTime();
			long diffTime = finalTime - initTime;
			//System.out.println("Me demore en buscar: " + diffTime);
			numberOfQueries++;
			averageQueryTime = (averageQueryTime * (numberOfQueries - 1) + diffTime) / numberOfQueries;
			checkMaybeExpandOrCompress();
		}
		return toReturn;
	}
	
	private void checkMaybeExpandOrCompress() {
		System.out.print("*" + averageQueryTime + "*");
		if (averageQueryTime > 32000) {
			expand();
		} else if (averageQueryTime < 20000){
			compress();
		}
	}

	@Override
	public boolean delete(String chain) {
		Bucket bToDelete = getBucket(chain);
		return bToDelete.deleteInBucket(chain);
	}

	@Override
	public void add(String chain) {
		Bucket bToInsert = getBucket(chain);
		bToInsert.insertInBucket(chain);
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
