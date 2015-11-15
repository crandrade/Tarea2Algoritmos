package Tarea2Algoritmos;

public class Utilitarian {
	
	public final static int CHAIN_SIZE = 15;

	public static String searchChainInBytes(String chain, byte[] bytes) {
		String haystack = new String(bytes);
		int counter = 0;
		while (counter < haystack.length() - CHAIN_SIZE) {
			String mSubString = haystack.substring(counter, counter + CHAIN_SIZE);
			System.out.println("Subcadena: " + mSubString);
			if (chain.equals(mSubString))
				return chain;
			counter = counter + CHAIN_SIZE;
		}
		return null;
	}
	
	public static byte[] concatBytes(byte[] a, byte[] b) {
		   int aLen = a.length;
		   int bLen = b.length;
		   byte[] c = new byte[aLen+bLen];
		   System.arraycopy(a, 0, c, 0, aLen);
		   System.arraycopy(b, 0, c, aLen, bLen);
		   return c;
		}
	
}
