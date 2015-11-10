package Tarea2Algoritmos;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;


public class Main {

	static public char [] init(String filename) throws FileNotFoundException{
		String line = "";
		File file = new File(filename);
		@SuppressWarnings("resource")
		Scanner scanner = new Scanner(file);
		line = scanner.nextLine();
		while (scanner.hasNextLine()) {
		       line += scanner.nextLine();
		}
		//line = line.trim().replaceAll("[^A-Za-z ]", " ");
		return line.toCharArray();
	}
	static public void destroy(char[] text){
		text = null;
		System.gc();
	}
	
	static public String generateChain(boolean o, Structure struct, int l){
		String patron = "";
		int j = (int) Math.pow(2, 25);
		if(o){
			patron = struct.obtain();
		}
		else{
			String [] bases = {"G","C","A","T"};
			String line="";
			for(int k=0; k<15; k++){
				line+="" + bases[ThreadLocalRandom.current().nextInt(0, 4)];
			}
			patron = line;
		}
		return patron;
	}
	
	static public boolean acceptableError(SummaryStatistics summary, int iterations){
		if(iterations > 1){
			return (((summary.getStandardDeviation()/summary.getMean()) <= 0.05)
					&& (((2*summary.getStandardDeviation())/Math.sqrt(iterations))>=0.95));
		}
		else return false;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
