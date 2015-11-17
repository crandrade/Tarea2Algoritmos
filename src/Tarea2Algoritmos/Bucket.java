package Tarea2Algoritmos;

import java.io.IOException;
import java.util.LinkedList;

public class Bucket {
	
	LinkedList<Integer> diskPages = new LinkedList<>();
	DiskSimulator dSimulator;
	
	public Bucket(DiskSimulator dSimulator) {
		this.dSimulator = dSimulator;
		diskPages = new LinkedList<>();
		diskPages.add(dSimulator.getNextFreePage());
	}
	
	public LinkedList<String> getAllChains() {
		LinkedList<String> allChains = new LinkedList<>();
		for (Integer diskPage : diskPages) {
			try {
				byte[] pageData = dSimulator.getPage(diskPage);
				String haystack = new String(pageData);
				int counter = 0;
				while (counter < haystack.length() - Utilitarian.CHAIN_SIZE) {
					String mSubString = haystack.substring(counter, counter + Utilitarian.CHAIN_SIZE);
					System.out.println("Expanding: " + mSubString);
					if (mSubString.trim().equals("")) {
						break;
					}
					allChains.add(mSubString);
					counter = counter + Utilitarian.CHAIN_SIZE;
				}
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("Error al leer todas las cadenas del bucket");
			}
		}
		return allChains;
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
	
	public boolean deleteInBucket(String chain) {
		boolean found = false;
		try {
			for (Integer diskPage : diskPages) {
			byte[] pageData = dSimulator.getPage(diskPage);
			byte[] newStringBytes = new byte[DiskSimulator.BLOCK_SIZE_BYTES];
			String pageDataString = new String(pageData);
			int counter = 0;
			int writerPointer = 0;
			while (counter < pageDataString.length() - Utilitarian.CHAIN_SIZE) {
				String toAdd = pageDataString.substring(counter, counter + Utilitarian.CHAIN_SIZE);
				if (toAdd.equals(chain)) {
					found = true;
				} else {
					for (int j = 0; j < toAdd.getBytes().length; j++) {
						newStringBytes[writerPointer+j] = toAdd.getBytes()[j];
					}
					writerPointer = writerPointer + Utilitarian.CHAIN_SIZE;
				}
				counter = counter + Utilitarian.CHAIN_SIZE;
			}
			dSimulator.writePage(diskPage, newStringBytes);
			if (found)
				break;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Problema IO delete");
		}
		return found;
	}

	public void free() {
		// TODO Auto-generated method stub
		
	}
	
	
}