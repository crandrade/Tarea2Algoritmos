package Tarea2Algoritmos;

public class Utilitarian {
	
	public final static int CHAIN_SIZE = 15;

	public static String searchChainInBytes(String chain, byte[] bytes) {
		String haystack = new String(bytes);
		int counter = 0;
		while (counter < haystack.length() - CHAIN_SIZE) {
			String mSubString = haystack.substring(counter, counter + CHAIN_SIZE);
			if (chain.equals(mSubString))
				return chain;
			counter = counter + CHAIN_SIZE;
		}
		return null;
	}
	
	public static boolean readAndInsertAfter0(byte[] pageData, byte[] chainBytes) {
		for (int i = 0; i < DiskSimulator.BLOCK_SIZE_BYTES - chainBytes.length; i++) {
			if (pageData[i] == 0) {
				for (int j = 0; j < chainBytes.length; j++) {
					pageData[i+j] = chainBytes[j];
				}
				return true;
			}
		}
		return false;
	}
	
	
}
