package Tarea2Algoritmos;
import java.io.*;
import java.util.concurrent.ThreadLocalRandom;

public class Generador {

	static public void main(String [] args) throws IOException{
		String dir = "fakedna.txt";
		System.err.println("Opening file at  "+dir);
		File fDir = new File(dir);
		PrintWriter printer = new PrintWriter(new FileWriter(fDir,true));
		
		int i = (int) Math.pow(2, 25);
		String [] bases = {"G","C","A","T"};
		System.err.println("Max iterations: "+i);
		String line="";
		for(int j=0; j<i; j++){
			for(int k=0; k<15; k++){
				line+="" + bases[ThreadLocalRandom.current().nextInt(0, 4)];
			}
			printer.println(line);
			line="";
		}
		printer.close();
	}
	
}
