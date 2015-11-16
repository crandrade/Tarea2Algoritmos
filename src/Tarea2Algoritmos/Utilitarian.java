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
	
	
}
