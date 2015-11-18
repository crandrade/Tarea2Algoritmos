package Tarea2Algoritmos;

import java.util.LinkedList;

public abstract class LinearHash {
	
	protected LinkedList<Bucket> buckets;
	protected DiskSimulator dSimulator;
	
	protected int nBucketsReales = 1;
	protected int s2BucketsVirtuales = 2;
	
	protected void expand() {
		/* Expandir */
		Bucket toExpand = buckets.get(nBucketsReales - s2BucketsVirtuales/2);
		Bucket newBucket = new Bucket(dSimulator);
		buckets.add(newBucket);
		LinkedList<String> allChains = toExpand.getAllChains();
		for (String chain : allChains) {
			getBucket(chain).insertInBucket(chain);
		}
		//rehash con h(x) mod 2s
		
		nBucketsReales++;
		if (nBucketsReales == s2BucketsVirtuales)
			s2BucketsVirtuales = s2BucketsVirtuales * 2;
		
	}
	
	protected void compress() {
		/* Compress */
		// No puedo comprimirme mas que uno
		//System.out.println("Buckets reales " + nBucketsReales);
		//System.out.println("S2 Buckets" + s2BucketsVirtuales);
		if (nBucketsReales == 2)
			return;
		nBucketsReales--;
		//n <- n-1
		//agregar la pagina n a la 
		Bucket bucketToRemove = buckets.get(nBucketsReales);
		Bucket bucketToAdd = buckets.get(nBucketsReales - s2BucketsVirtuales/2);
		LinkedList<String> allChains = bucketToRemove.getAllChains();
		for (String chain : allChains) {
			bucketToAdd.insertInBucket(chain);
		}
		bucketToRemove.free();
		//mBucket.diskPages;
		//	n-s
		if (nBucketsReales == s2BucketsVirtuales/2)
			s2BucketsVirtuales = s2BucketsVirtuales/2;
	}
	
	protected Bucket getBucket(String chain) {
		int toInsert;
		if (ADNHasher.longHash(chain) % (s2BucketsVirtuales/2) < nBucketsReales % (s2BucketsVirtuales/2))
			toInsert = (int) (ADNHasher.longHash(chain) % s2BucketsVirtuales);
		else
			toInsert = (int) (ADNHasher.longHash(chain) % (s2BucketsVirtuales/2));
		Bucket bToInsert = buckets.get(toInsert);
		return bToInsert;
	}
	

}
