package Tarea2Algoritmos;
import java.io.*;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class Generador {

	static public void generate(int iterations) throws IOException{
		String dir = "fakeDNA";
		for(int r=0; r<iterations; r++){
			System.err.println("Opening file at "+dir+r+".txt");
			File fDir = new File(dir+r+".txt");
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
	static public void dump(int iterations) throws IOException{
		String dir = "realDNA";
		File f2Dir = new File("human.original.fa");
		@SuppressWarnings("resource")
		Scanner scanner = new Scanner(f2Dir);
		for(int r=0; r<iterations; r++){
			System.err.println("Opening file at "+dir+r+".txt");
			File fDir = new File(dir+r+".txt");
			PrintWriter printer = new PrintWriter(new FileWriter(fDir,true));
			int i = (int) Math.pow(2, 25);
			System.err.println("Max iterations: "+i);
			String line="";
			int j = 0;
			while (j<i){
					if(!scanner.hasNextLine()){
						scanner.close();
						scanner = new Scanner(f2Dir);
					}
					line= scanner.nextLine();
					if(line.indexOf("N")==-1){
						printer.println(line.substring(0,15));
			    		printer.println(line.substring(15,30));
			    		printer.println(line.substring(30,45));
			    		printer.println(line.substring(45,60));
			    		j+=4;
					}
					if(j%1000000==0){
						System.out.println(".");
					}
			}
			printer.close();
		}
	}
	public static void main(String[] args) throws IOException{
		Option iter = new Option("iterations", "Number of iterations");
		iter.setArgs(1);
		iter.setRequired(true);
		Option real = new Option("r", "Real file");
		real.setArgs(0);
		Option fake = new Option("f", "Fake file");
		real.setArgs(0);
		Options options = new Options();
		options.addOption(iter);
		options.addOption(real);
		options.addOption(fake);
		CommandLineParser parser = new BasicParser();
		CommandLine cmd = null;
		try {
			cmd = parser.parse(options, args);
		} catch (ParseException e) {
			System.err.println("***ERROR: " + e.getClass() + ": " + e.getMessage());
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("parameters:", options );
			return;
		}
		int max_it=10;
		if (cmd.hasOption("iterations")) {
			int nn = Integer.parseInt(cmd.getOptionValue(iter.getOpt()));
			if(nn>=1 && nn<=10000)
			max_it=nn;
		}
		if (cmd.hasOption("r")) {
			dump(max_it);
		}
		if(cmd.hasOption("f")){ 
			generate(max_it);
		}
	}
}
