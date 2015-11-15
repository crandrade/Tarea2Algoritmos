package Tarea2Algoritmos;
import java.util.Random;

public class MainJavier {

	public static void main(String[] args) {
		String c1 = "AATT";
		String c2 = "TTAA";
		boolean[] c1b = ADNHasher.hash(c1);
		boolean[] c2b = ADNHasher.hash(c2);
		
		
		Random random = new Random();
		String cadena = "";
		ExtendibleHash eHash = new ExtendibleHash();
		for (int i = 0; i < 10000; i++) {
			String cadenaToAdd = "";
			for (int j = 0; j <15; j++) {
				int mint = random.nextInt(4);
				String toAdd;
				switch (mint) {
				case 0:
					toAdd = "A";
					break;
				case 1:
					toAdd = "G";
					break;
				case 2:
					toAdd = "C";
					break;
				default:
					toAdd = "T";
					break;
				}
				cadenaToAdd = cadenaToAdd + toAdd;
			}
			eHash.insertChain(cadenaToAdd);
			cadena = cadenaToAdd;
		}
		
		System.out.println("Virtualmente imposible: " + eHash.findChain("GGGGGGGGGGGGGTG"));
		System.out.println("Terminado");
		System.out.println("cadena: " + cadena);
		System.out.println("100% : " + eHash.findChain(cadena));
		//eHash.findChain(cadena);
		//eHash.deleteChain(cadena);
		
	}
	
	

}
