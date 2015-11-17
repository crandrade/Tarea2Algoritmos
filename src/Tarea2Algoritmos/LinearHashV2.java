package Tarea2Algoritmos;

import java.io.IOException;
import java.util.LinkedList;

public class LinearHashV2 implements DiskMemoryManager {
	
	private DiskSimulator dSimulator;
	
	private LinkedList<Bucket> buckets;
	
	private int nBucketsReales = 1;
	private int s2BucketsVirtuales = 2;
	
	public LinearHashV2() {
		try {
			dSimulator = new DiskSimulator();
		} catch (IOException e) {
			System.out.println("Linear Hash File Not found" +  e.toString());
		}
		buckets = new LinkedList<>();
		buckets.add(new Bucket(dSimulator));
	}
	
	
	private void expand() {
		/* Expandir */
	}
	
	private void compress() {
		/* Compress */
	}
	
	private Bucket getBucket(String chain) {
		int toInsert;
		if (ADNHasher.longHash(chain) % (s2BucketsVirtuales/2) < nBucketsReales % (s2BucketsVirtuales/2))
			toInsert = (int) (ADNHasher.longHash(chain) % s2BucketsVirtuales);
		else
			toInsert = (int) (ADNHasher.longHash(chain) % (s2BucketsVirtuales/2));
		Bucket bToInsert = buckets.get(toInsert);
		return bToInsert;
	}
	
	@Override
	public String find(String chain) {
		Bucket bToSearch = getBucket(chain);
		return bToSearch.searchInBucket(chain);
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
