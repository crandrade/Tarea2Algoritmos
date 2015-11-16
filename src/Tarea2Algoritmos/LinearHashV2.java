package Tarea2Algoritmos;

import java.io.IOException;
import java.util.LinkedList;

public class LinearHashV2 implements DiskMemoryManager {
	
	private DiskSimulator dSimulator;
	
	private LinkedList<Bucket> buckets;
	
	private int nBucketsReales = 1;
	private int s2BucketsVirtuales = 2;
	
	private int uncheckedInsertOperations = 0;
	private int uncheckedDeleteOperations = 0;
	
	public LinearHashV2() {
		try {
			dSimulator = new DiskSimulator();
		} catch (IOException e) {
			System.out.println("Linear Hash File Not found" +  e.toString());
		}
		buckets = new LinkedList<>();
		buckets.add(new Bucket());
	}
	
	

	private class Bucket {
		
		LinkedList<Integer> diskPages = new LinkedList<>();
		
		public Bucket() {
			diskPages = new LinkedList<>();
			diskPages.add(dSimulator.getNextFreePage());
		}
		
		public void insertInBucket(String chain) {
			try {
				byte[] pageData = dSimulator.getPage(diskPages.size());
				boolean success = Utilitarian.readAndInsertAfter0(pageData, chain.getBytes());
				if (success) {
					dSimulator.writePage(diskPages.size(), pageData);
				} else {
					int nextFreePage = dSimulator.getNextFreePage();
					diskPages.add(nextFreePage);
					byte[] rPageData = dSimulator.getPage(nextFreePage);
					Utilitarian.readAndInsertAfter0(rPageData, chain.getBytes());
					dSimulator.writePage(nextFreePage, rPageData);
				}
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("Error inserting in bucket");
			}
		}
		
		public String searchInBucket(String chain) {
		    for (int diskPage : diskPages) {
		    	try {
					byte[] pageData = dSimulator.getPage(diskPage);
					String result = Utilitarian.searchChainInBytes(chain, pageData);
					if (result != null)
						return result;
				} catch (IOException e) {
					e.printStackTrace();
					System.out.println("Error searching in bucket");
				}
		    }
			return null;
		}
		
		
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
	
	private void expand() {
		/* Expandir */
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
		/* Cuando borro debo chequear la tasa de ocupacion para contraerme*/
		// TODO Auto-generated method stub
		return false;
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
