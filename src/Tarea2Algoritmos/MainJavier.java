package Tarea2Algoritmos;
import java.util.Random;

public class MainJavier {

	public static void main(String[] args) {
		String c1 = "AATT";
		String c2 = "TTAA";
		boolean[] c1b = ADNHasher.hash(c1);
		boolean[] c2b = ADNHasher.hash(c2);
		//ExtendibleHash eHash = new ExtendibleHash();
		//eHash.add("GGGGGGGGGGGGGTG");
		//eHash.add("AAAAAAAAAAAAAAA");
		//eHash.add("CCCCCCCCCCCCCCC");
		//eHash.delete("AAAAAAAAAAAAAAA");
		//System.out.println("Deleted? : " + eHash.delete("ABCGGGGGGGGGGGTA"));
		//System.out.println("Is there? : " + eHash.find("ABCDGGGGGGGGGTA"));
		//System.out.println("Other was unaffected s? : " + eHash.find("GGGGGGGGGGGGGTA"));
		
		
		
		Random random = new Random();
		String cadena = "";
		ExtendibleHash eHash = new ExtendibleHash();
		for (int i = 0; i < 1000; i++) {
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
			eHash.add(cadenaToAdd);
			cadena = cadenaToAdd;
		}
		
		System.out.println("Virtualmente imposible: " + eHash.find("GGGGGGGGGGGGGTG"));
		System.out.println("Terminado");
		System.out.println("cadena: " + cadena);
		System.out.println("100% : " + eHash.find(cadena));
		System.out.println("Ocupacion: " + eHash.getOcupation());
		System.out.println("IOs: " + eHash.getIOs());
		//eHash.findChain(cadena);
		//eHash.deleteChain(cadena);
		
	}
	
	

}
