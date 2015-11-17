package Tarea2Algoritmos;
import java.util.Random;

public class MainJavier {

	public static void main(String[] args) {
		String c1 = "AATT";
		String c2 = "TTAA";
		boolean[] c1b = ADNHasher.hash(c1);
		boolean[] c2b = ADNHasher.hash(c2);
		String cadena2 = "";
		// Extendible hash
		//ExtendibleHash eHash = new ExtendibleHash();
		//eHash.add("GGGGGGGGGGGGGTG");
		//eHash.add("AAAAAAAAAAAAAAA");
		//eHash.add("CCCCCCCCCCCCCCC");
		//eHash.delete("AAAAAAAAAAAAAAA");
		//System.out.println("Deleted? : " + eHash.delete("ABCGGGGGGGGGGGTA"));
		//System.out.println("Is there? : " + eHash.find("ABCDGGGGGGGGGTA"));
		//System.out.println("Other was unaffected s? : " + eHash.find("GGGGGGGGGGGGGTA"));
		/*
		boolean first = true;
		
		Random random = new Random();
		String cadena = "";
		ExtendibleHash eHash = new ExtendibleHash();
		for (int i = 0; i < 200; i++) {
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
			if (first) {
				first = false;
				cadena2 = cadenaToAdd;
				System.out.println("FIRST TIME: " + cadena2);
			}
			eHash.add(cadenaToAdd);
			cadena = cadenaToAdd;
		}
		
		System.out.println("Virtualmente imposible: " + eHash.find("GGGGGGGGGGGGGTG"));
		System.out.println("Terminado");
		System.out.println("cadena: " + cadena);
		System.out.println("100% : " + eHash.find(cadena));
		System.out.println("Cadena 2: " + cadena2);
		System.out.println("100% : " + eHash.find(cadena2));
		
		System.out.println("Ocupacion: " + eHash.getOcupation());
		System.out.println("IOs: " + eHash.getIOs());
		//eHash.findChain(cadena);
		//eHash.deleteChain(cadena);
		 * 
		 */
		
		// Linear hash
				LinearHashV1 eHash = new LinearHashV1();
				eHash.add("GGGGGGGGGGGGGTG");
				eHash.add("AAAAAAAAAAAAAAA");
				eHash.add("CCCCCCCCCCCCCCC");
				System.out.println("Is there? : " + eHash.find("CCCCCCCCCCCCCCC"));
				System.out.println("Deleted? : " + eHash.delete("AAAAAAAAAAAAAAA"));
				System.out.println("Other was unaffected s? : " + eHash.find("CCCCCCCCCCCCCCC"));
				
				boolean first = true;
				
				Random random = new Random();
				String cadena = "";
				//ExtendibleHash eHash = new ExtendibleHash();
				for (int i = 0; i < 200; i++) {
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
					System.out.println("Cadena: " + cadenaToAdd);
					if (first) {
						first = false;
						cadena2 = cadenaToAdd;
						System.out.println("FIRST TIME: " + cadena2);
					}
					eHash.add(cadenaToAdd);
					cadena = cadenaToAdd;
				}
				
				System.out.println("Virtualmente imposible: " + eHash.find("GGGGGGGGGGGGGTG"));
				System.out.println("Terminado");
				System.out.println("cadena: " + cadena);
				System.out.println("100% : " + eHash.find(cadena));
				System.out.println("Cadena 2: " + cadena2);
				System.out.println("100% : " + eHash.find(cadena2));
				
				System.out.println("Ocupacion: " + eHash.getOccupation());
				System.out.println("IOs: " + eHash.getIOs());
				//eHash.findChain(cadena);
				//eHash.deleteChain(cadena);*/
				 
		
		LinearHashV1 linearHash = new LinearHashV1();
		linearHash.add("GGGGGGGGGGGGGGG");
		linearHash.add("CCCCCCCCCCCCCCC");
		System.out.println("Find 1: " + linearHash.find("GGGGGGGGGGGGGGG"));
		System.out.println("Find 2: " + linearHash.find("CCCCCCCCCCCCCCC"));
		
	}
	
	

}
