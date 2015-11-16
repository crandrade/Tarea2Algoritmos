package Tarea2Algoritmos;

public class ADNHasher {

	public static boolean[] hash(String ADNChain) {
		boolean[] toReturn = new boolean[ADNChain.length() * 2];
		int j = 0;
		for (int i = 0; i < ADNChain.length(); i++) {
			char mchar = ADNChain.charAt(i);
			switch (mchar) {
			case 'G' : 
				toReturn[j] = false;
				toReturn[j+1] = false;
				break;
			case 'C' :
				toReturn[j] = false;
				toReturn[j+1] = true;
				break;
			case 'A' :
				toReturn[j] = true;
				toReturn[j+1] = false;
				break;
			case 'T' :
				toReturn[j] = true;
				toReturn[j+1] = true;
				break;
			}
			j = j + 2;
		}
		return toReturn;
	}
	
}
