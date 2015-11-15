package Tarea2Algoritmos;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

public class Main {
	static protected char[] realDNA;
	static protected char[] fakeDNA;
	
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
		Option iter = new Option("iterations", "Number of iterations");
		iter.setArgs(1);
		iter.setRequired(true);
		Option power = new Option("i", "Log (Min lenght of patron)");
		power.setArgs(1);
		power.setRequired(true);
		Option power2 = new Option("I", "Log (Max lenght of patron)");
		power2.setArgs(1);
		power2.setRequired(true);
		Option rDNA = new Option("rd", "Real DNA input file");
		rDNA.setArgs(0);
		Option fDNA = new Option("fd", "Fake DNA input file");
		fDNA.setArgs(0);
	
		Options options = new Options();
		options.addOption(iter);
		options.addOption(power);
		options.addOption(power2);
		options.addOption(rDNA);
		options.addOption(fDNA);
		
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
	
		String dir = new SimpleDateFormat("YYYYMMDD HH:mm:ss.S").format(new Date())+" output.txt";
		System.err.println("Opening file at  "+dir);
		File fDir = new File(dir);
		PrintWriter printer = new PrintWriter(new FileWriter(fDir,true));
		
		int l=20;
		if (cmd.hasOption("i")) {
			int nn = Integer.parseInt(cmd.getOptionValue(power.getOpt()));
			if(nn>19 && nn<=25)
			l=nn;
		}
		System.err.println(l);
		int L=25;
		if (cmd.hasOption("I")) {
			int nn = Integer.parseInt(cmd.getOptionValue(power2.getOpt()));
			if(nn>=20 && nn<=25 && nn>=l)
			L=nn;
		}
		System.err.println(L);
		int max_it=10000;
		if (cmd.hasOption("iterations")) {
			int nn = Integer.parseInt(cmd.getOptionValue(iter.getOpt()));
			if(nn>=100 && nn<=10000)
			max_it=nn;
		}
		boolean random = true;
		boolean extracted = false;
		long t=0;
		SummaryStatistics BTreeOcc, BTreeOps;
		SummaryStatistics ExtHOcc, ExtHOps;
		SummaryStatistics LinH1Occ, LinH1Ops;
		SummaryStatistics LinH2Occ, LinH2Ops;
		
	
		// test real DNA
		if (cmd.hasOption("rd")) {
			realDNA = init("realDNA.txt");
			printer.println("Real DNA Text");
			DiskMemmoryManager btree = new BTree(realDNA);
			DiskMemmoryManager exthash = new ExtendibleHash(realDNA);
			DiskMemmoryManager linhash1 = new LinearHash1(realDNA);
			DiskMemmoryManager linhash2 = new LinearHash1(realDNA);
			for(int i=l; i<=L; i++){
				System.err.println("2^"+i);
				printer.println("2^"+i); 
				BTreeOcc = new SummaryStatistics();
				BTreeOps = new SummaryStatistics();
				ExtHOcc = new SummaryStatistics();
				ExtHOps = new SummaryStatistics();
				LinH1Occ = new SummaryStatistics();
				LinH1Ops = new SummaryStatistics();
				LinH2Occ = new SummaryStatistics();
				LinH2Ops = new SummaryStatistics();
				for(int iterations=1; true; iterations++){
					char [] patron = generatePatron(extracted, realDNA, (int)Math.pow(2, i));
					t = System.currentTimeMillis();
					BFsum.addValue((double)(brute.search(patron)));
					BFtime.addValue((double)(System.currentTimeMillis() - t));
					if(iterations%10000 == 0){
						System.out.println(""+iterations);
						System.err.println("Resultados realDNA BF");
						System.err.println("Promedio tiempo: "+(BFtime.getMean()/1000)+" seg.");
						System.err.println("DEstandar tiempo: "+(BFtime.getStandardDeviation()/1000)+" seg.");
						System.err.println("Error tiempo: "+((2*BFtime.getStandardDeviation())/
								Math.sqrt(iterations)*1000)+" seg.");
						System.err.println("Promedio comps: "+(BFsum.getMean())+" comps.");
						System.err.println("DEstandar comps: "+(BFsum.getStandardDeviation())+" comps.");
						System.err.println("Error comps: "+((2*BFsum.getStandardDeviation())/
								Math.sqrt(iterations))+" comps.");
					}
					if(acceptableError(BFsum, iterations) 
							&& acceptableError(BFtime, iterations) || iterations >= max_it){
						printer.println("BF Iterations: "+iterations);
						printer.println("BFtime:\t"+BFtime.getMean()+"\t"+BFtime.getVariance()+"\t"+BFtime.getStandardDeviation());
						printer.println("BFsum:\t"+BFsum.getMean()+"\t"+BFsum.getVariance()+"\t"+BFsum.getStandardDeviation());
						break;
					}
				}
			destroy(realDNA);
		}	
		// test fake DNA
		if (cmd.hasOption("fd")) {
			fakeDNA = init("fakeDNA.txt");
			printer.println("Fake DNA Text");
			GenericTextSearch brute = new BruteForceSearch(fakeDNA);
			GenericTextSearch kmp = new KnuthMorrisPrattSearch(fakeDNA);
			GenericTextSearch bmh = new BoyerMooreHorspoolSearch(fakeDNA);
			for(int i=l; i<=L; i++){
				System.err.println("2^"+i);
				printer.println("2^"+i);
				BFsum = new SummaryStatistics();
				BFtime = new SummaryStatistics();
				KMPsum = new SummaryStatistics();
				KMPtime = new SummaryStatistics();
				BMHsum = new SummaryStatistics();
				BMHtime = new SummaryStatistics();
				for(int iterations=1; true; iterations++){
					char [] patron = generatePatron(extracted, 
							fakeDNA, (int)Math.pow(2, i));
					t = System.currentTimeMillis();
					BFsum.addValue((double)(brute.search(patron)));
					BFtime.addValue((double)(System.currentTimeMillis() - t));
					if(iterations%10000 == 0){
						System.out.println(""+iterations);
						System.err.println("Resultados fakeDNA BF");
						System.err.println("Promedio tiempo: "+(BFtime.getMean()/1000)+" seg.");
						System.err.println("DEstandar tiempo: "+(BFtime.getStandardDeviation()/1000)+" seg.");
						System.err.println("Error tiempo: "+((2*BFtime.getStandardDeviation())/
								Math.sqrt(iterations)*1000)+" seg.");
						System.err.println("Promedio comps: "+(BFsum.getMean())+" comps.");
						System.err.println("DEstandar comps: "+(BFsum.getStandardDeviation())+" comps.");
						System.err.println("Error comps: "+((2*BFsum.getStandardDeviation())/
								Math.sqrt(iterations))+" comps.");
					}
					if(acceptableError(BFsum, iterations) 
							&& acceptableError(BFtime, iterations) || iterations >= max_it){
						printer.println("BF Iterations: "+iterations);
						printer.println("BFtime:\t"+BFtime.getMean()+"\t"+BFtime.getVariance()+"\t"+BFtime.getStandardDeviation());
						printer.println("BFsum:\t"+BFsum.getMean()+"\t"+BFsum.getVariance()+"\t"+BFsum.getStandardDeviation());
						break;
					}
				}
				for(int iterations=1; true; iterations++){
					char [] patron = generatePatron(extracted, 
							fakeDNA, (int)Math.pow(2, i));
					t = System.currentTimeMillis();
					KMPsum.addValue((double)(kmp.search(patron)));
					KMPtime.addValue((double)(System.currentTimeMillis() - t));
					if(iterations%10000 == 0){
						System.out.println(""+iterations);
						System.err.println("Resultados fakeDNA KMP");
						System.err.println("Promedio tiempo: "+(KMPtime.getMean()/1000)+" seg.");
						System.err.println("DEstandar tiempo: "+(KMPtime.getStandardDeviation()/1000)+" seg.");
						System.err.println("Error tiempo: "+((2*KMPtime.getStandardDeviation())/
								Math.sqrt(iterations)*1000)+" seg.");
						System.err.println("Promedio comps: "+(KMPsum.getMean())+" comps.");
						System.err.println("DEstandar comps: "+(KMPsum.getStandardDeviation())+" comps.");
						System.err.println("Error comps: "+((2*KMPsum.getStandardDeviation())/
								Math.sqrt(iterations))+" comps.");
					}
					if(acceptableError(KMPsum, iterations) 
							&& acceptableError(KMPtime, iterations) || iterations >= max_it){
						printer.println("KMP Iterations: "+iterations);
						printer.println("KMPtime:\t"+KMPtime.getMean()+"\t"+KMPtime.getVariance()+"\t"+KMPtime.getStandardDeviation());
						printer.println("KMPsum:\t"+KMPsum.getMean()+"\t"+KMPsum.getVariance()+"\t"+KMPsum.getStandardDeviation());
						break;
					}
				}
				for(int iterations=1; true; iterations++){
					char [] patron = generatePatron(extracted, 
							fakeDNA, (int)Math.pow(2, i));
					t = System.currentTimeMillis();
					BMHsum.addValue((double)(bmh.search(patron)));
					BMHtime.addValue((double)(System.currentTimeMillis() - t));
					if(iterations%10000 == 0){
						System.out.println(""+iterations);
						System.err.println("Resultados fakeDNA BMH");
						System.err.println("Promedio tiempo: "+(BMHtime.getMean()/1000)+" seg.");
						System.err.println("DEstandar tiempo: "+(BMHtime.getStandardDeviation()/1000)+" seg.");
						System.err.println("Error tiempo: "+((2*BMHtime.getStandardDeviation())/
								Math.sqrt(iterations)*1000)+" seg.");
						System.err.println("Promedio comps: "+(BMHsum.getMean())+" comps.");
						System.err.println("DEstandar comps: "+(BMHsum.getStandardDeviation())+" comps.");
						System.err.println("Error comps: "+((2*BMHsum.getStandardDeviation())/
								Math.sqrt(iterations))+" comps.");
					}
					if(acceptableError(BMHsum, iterations) 
							&& acceptableError(BMHtime, iterations) || iterations >= max_it){
						printer.println("BMH Iterations: "+iterations);
						printer.println("BMHtime:\t"+BMHtime.getMean()+"\t"+BMHtime.getVariance()+"\t"+BMHtime.getStandardDeviation());
						printer.println("BMHsum:\t"+BMHsum.getMean()+"\t"+BMHsum.getVariance()+"\t"+BMHsum.getStandardDeviation());
						break;
					}
				}
			}
			destroy(fakeDNA);
		}
	}
}
