package Tarea2Algoritmos;
import java.io.*;
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
	public static void main(String[] args) throws IOException{
		Option iter = new Option("iterations", "Number of iterations");
		iter.setArgs(1);
		iter.setRequired(true);
		Options options = new Options();
		options.addOption(iter);
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
		generate(max_it);
	}
}
