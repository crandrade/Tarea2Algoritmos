package Tarea2Algoritmos;

import java.util.LinkedList;

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
	
	public static LinkedList<String> allStringsOnBytes(byte[] bytes) {
		LinkedList<String> allChains = new LinkedList<>();
		String haystack = new String(bytes);
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
		return allChains;
	}
	
	public static byte[] stringListToByte(LinkedList<String> strings) {
		String concatString = "";
		for (String string : strings) {
			concatString = concatString + string;
		}
		return concatString.getBytes();
	}
	/**
	 * Retorna si esque una pagina de disco hara overflow al insertarle cierto string
	 * @param pageData Los bytes de la pagina en disco
	 * @param chainBytes Los bytes del string a insertar
	 * @return
	 */
	public static boolean willOverflow(byte[] pageData, byte[] chainBytes) {
		for (int i = 0; i < DiskSimulator.BLOCK_SIZE_BYTES - chainBytes.length; i++) {
			if (pageData[i] == 0) {
				return false;
			}
		}
		return true;
	}
	
	
}
