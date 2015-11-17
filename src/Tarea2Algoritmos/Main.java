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
	static protected String[] realDNA;
	static protected String[] fakeDNA;
	
	static public String[] init(String filename) throws FileNotFoundException{
		int i = (int)Math.pow(2, 25);
		String line [] = new String[i];
		File file = new File(filename);
		@SuppressWarnings("resource")
		Scanner scanner = new Scanner(file);
		line[i++]= scanner.nextLine();
		while (scanner.hasNextLine()) {
		       line[i++]= scanner.nextLine();
		}
		return line;
	}
	static public void destroy(String[] text){
		text = null;
		System.gc();
	}
	
	static public String [] generateChains(boolean o, String [] chain, int l){
		String [] patron = new String [10000];
		if(o){ // o == true
			for(int j = 0; j< 10000; j++){
				int i = ThreadLocalRandom.current().nextInt(0, (int)Math.pow(2,l));
				patron[j] = chain[i];
			}
		}
		else{ //o == false
			String [] bases = {"G","C","A","T"};
			for(int j = 0; j< 10000; j++){
				String line="";
				for(int k=0; k<15; k++){
					line+="" + bases[ThreadLocalRandom.current().nextInt(0, 4)];
				}
				patron[j] = line;
			}
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

	public static void main(String[] args){
		// TODO Auto-generated method stub
		Option iter = new Option("iterations", "Number of iterations");
		iter.setArgs(1);
		iter.setRequired(true);
		Option power = new Option("i", "Log (Min lenght of file (lines))");
		power.setArgs(1);
		power.setRequired(true);
		Option power2 = new Option("I", "Log (Max lenght of file (lines)");
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
	
		String dir = new SimpleDateFormat("yyyyMMdd HH:mm:ss").format(new Date())+" output.txt";
		System.err.println("Opening file at  "+dir);
		File fDir = new File(dir);
		PrintWriter printer = new PrintWriter(new FileWriter(fDir,true));
		
		int l=20; // minimal measure
		int L=25; // maximal measure
		int max_it=100;
		if (cmd.hasOption("iterations")) {
			int nn = Integer.parseInt(cmd.getOptionValue(iter.getOpt()));
			if(nn>=1 && nn<=10000)
			max_it=nn;
		}
		boolean random = false;
		boolean extracted = true;
		long t=0;
		/*
		Medir Ocupación:
		Al entrar 2^20, 2^21, 2^22, 2^23, 2^24, 2^25
		Al salir 2^24, 2^23, 2^22, 2^21, 2^20
		*/

		SummaryStatistics BTree_OccIn[6];
		SummaryStatistics BTree_OccOut[5];
		SummaryStatistics ExtHash_OccIn[6];
		SummaryStatistics ExtHash_OccOut[5];
		SummaryStatistics LinHashV1_OccIn[6];
		SummaryStatistics LinHashV1_OccOut[5];
		SummaryStatistics LinHashV2_OccIn[6];
		SummaryStatistics LinHashV2_OccOut[5];
		
		/*Medir IO
		for i in [20, 25]
			Luego de insertar 2^i
			Tras realizar 10000 búsquedas "exitosas"
			Tras realizar 10000 búsquedas "infructuosas"
		for i in [25, 21]
			Luego de borrar entre i e i-1
		Luego de borrar todo
		*/
		int bhelper_low = 0,
			bhelper_high = 0,
			exthelper_low = 0,
			exthelper_high = 0,
			lin1helper_low = 0,
			lin1helper_high = 0,
			lin2helper_low = 0,
			lin2helper_high = 0;
		SummaryStatistics BTree_IO_insert[6];
		SummaryStatistics BTree_IO_successfulSearch[6];
		SummaryStatistics BTree_IO_unfavorableSearch[6];
		SummaryStatistics BTree_IO_deleting[5];
		SummaryStatistics BTree_IO_erased = new SummaryStatistics();
		SummaryStatistics ExtHash_IO_insert[6];
		SummaryStatistics ExtHash_IO_successfulSearch[6];
		SummaryStatistics ExtHash_IO_unfavorableSearch[6];
		SummaryStatistics ExtHash_IO_deleting[5];
		SummaryStatistics ExtHash_IO_erased = new SummaryStatistics();
		SummaryStatistics LinHashV1_IO_insert[6];
		SummaryStatistics LinHashV1_IO_successfulSearch[6];
		SummaryStatistics LinHashV1_IO_unfavorableSearch[6];
		SummaryStatistics LinHashV1_IO_deleting[5];
		SummaryStatistics LinHashV1_IO_erased = new SummaryStatistics();
		SummaryStatistics LinHashV2_IO_insert[6];
		SummaryStatistics LinHashV2_IO_successfulSearch[6];
		SummaryStatistics LinHashV2_IO_unfavorableSearch[6];
		SummaryStatistics LinHashV2_IO_deleting[5];
		SummaryStatistics LinHashV2_IO_erased = new SummaryStatistics();
		/*SOMEHOW*/
	
		// test real DNA
		if (cmd.hasOption("rd")) {
			for(int i=0; i<6; i++){
				BTree_OccIn[i] = new SummaryStatistics();
				ExtHash_OccIn[i] = new SummaryStatistics();
				LinHashV1_OccIn[i] = new SummaryStatistics();
				LinHashV2_OccIn[i] = new SummaryStatistics();
				BTree_IO_insert[i] = new SummaryStatistics();
				BTree_IO_successfulSearch[i] = new SummaryStatistics();
				BTree_IO_unfavorableSearch[i] = new SummaryStatistics();
				ExtHash_IO_insert[i] = new SummaryStatistics();
				ExtHash_IO_successfulSearch[i] = new SummaryStatistics();
				ExtHash_IO_unfavorableSearch[i] = new SummaryStatistics();
				LinHashV1_IO_insert[i] = new SummaryStatistics();
				LinHashV1_IO_successfulSearch[i] = new SummaryStatistics();
				LinHashV1_IO_unfavorableSearch[i] = new SummaryStatistics();
				LinHashV2_IO_insert[i] = new SummaryStatistics();
				LinHashV2_IO_successfulSearch[i] = new SummaryStatistics();
				LinHashV2_IO_unfavorableSearch[i] = new SummaryStatistics();
			}
			for(int i=0; i<5; i++){
				BTree_OccOut[i] = new SummaryStatistics();
				ExtHash_OccOut[i] = new SummaryStatistics();;
				LinHashV1_OccOut[i] = new SummaryStatistics();
				LinHashV2_OccOut[i] = new SummaryStatistics();
				BTree_IO_deleting[i] = new SummaryStatistics();
				ExtHash_IO_deleting[i] = new SummaryStatistics();
				LinHashV1_IO_deleting[i] = new SummaryStatistics();
				LinHashV2_IO_deleting[i] = new SummaryStatistics();
			}
			for(int r=0; r<max_it; r++){
				printer.println("Real DNA test "+r);
				System.err.print("Setting up Real DNA test "+r);
				realDNA = init("realDNA"+r+".txt");
				System.err.print(".");
				DiskMemoryManager btree = new BTree();
				System.err.print(".");
				DiskMemoryManager exthash = new ExtendibleHash();
				System.err.print(".");
				DiskMemoryManager linhashV1 = new LinearHashV1();
				System.err.print(".");
				DiskMemoryManager linhashV2 = new LinearHashV2();
				System.err.print(".");
				int actual=0;
				int max = 0;
				System.err.print("Filling >>");
				for(int i=l; i<=L; i++){
					System.err.print("2^"+i);
					max = (int)Math.pow(2,i);
					for(int j=actual; j<max; j++){
						btree.add(realDNA[i]);
						exthash.add(realDNA[i]);
						linhashV1.add(realDNA[i]);
						linhashV2.add(realDNA[i]);
					}
					System.err.print(".");
					// measure Occupation
					BTree_OccIn[i-20].addValue(btree.getOccupation());
					ExtHash_OccIn[i-20].addValue(exthash.getOccupation());
					LinHashV1_OccIn[i-20].addValue(linhashV1.getOccupation());
					LinHashV2_OccIn[i-20].addValue(linhashV2.getOccupation());
					actual = max;
					String [] patron = generateChains(extracted, realDNA, l);
					for(int iterations=0; iterations<10000; iterations++){
						btree.find(patron[iterations]);
						exthash.find(patron[iterations]);
						linhashV1.find(patron[iterations]);
						linhashV2.find(patron[iterations]);
					}
					System.err.print(".");
					// measure each 
					patron = generateChains(random, realDNA, l);
					for(int iterations=0; iterations<10000; iterations++){
						btree.find(patron[iterations]);
						exthash.find(patron[iterations]);
						linhashV1.find(patron[iterations]);
						linhashV2.find(patron[iterations]);
					}
					System.err.print(".");
					//measure each
				}
				System.err.print("\nErasing >>");
				actual = (int)Math.pow(2,L);
				int min = 0;

				List<String> a = Arrays.asList(realDNA);
				Collections.shuffle(a); // randomize erasing
				realDNA = (String)a.toArray();
				
				System.gc(); // clean old realDNA
				for(int i=L; i>l; i--){
					System.err.print("2^"+i+"->2^"+(i-1)+" >> ");
					min = (int)Math.pow(2,i-1);
					for(int j=actual-1; j>=min; j--){
						btree.delete(realDNA[i]);
						exthash.delete(realDNA[i]);
						linhashV1.delete(realDNA[i]);
						linhashV2.delete(realDNA[i]);
					}
					System.err.print(".");
					actual = min;
					// measure Occupation
					BTree_OccOut[i-21].addValue(btree.getOccupation());
					ExtHash_OccOut[i-21].addValue(exthash.getOccupation());
					LinHashV1_OccOut[i-21].addValue(linhashV1.getOccupation());
					LinHashV2_OccOut[i-21].addValue(linhashV2.getOccupation());
					//measure each
				}
				btree.resetIOs();
				exthash.resetIOs();
				linhashV1.resetIOs();
				linhashV2.resetIOs();
				for(int j=actual-1; j>=0; j--){
					btree.delete(realDNA[i]);
					exthash.delete(realDNA[i]);
					linhashV1.delete(realDNA[i]);
					linhashV2.delete(realDNA[i]);
				}
				//measure
				BTree_IO_erased.addValue(btree.getIOs());
				ExtHash_IO_erased.addValue(exthash.getIOs());
				LinHashV1_IO_erased.addValue(linhashV1.getIOs());
				LinHashV2_IO_erased.addValue(linhashV2.getIOs());
				//end
				destroy(realDNA);
				/*	calculate error
					if reasonable, quit
				*/
			}
			/* print to file */
		}
		// test fake DNA
		if (cmd.hasOption("fd")){
			for(int i=0; i<6; i++){
				BTree_OccIn[i] = new SummaryStatistics();
				ExtHash_OccIn[i] = new SummaryStatistics();
				LinHashV1_OccIn[i] = new SummaryStatistics();
				LinHashV2_OccIn[i] = new SummaryStatistics();
				BTree_IO_insert[i] = new SummaryStatistics();
				BTree_IO_successfulSearch[i] = new SummaryStatistics();
				BTree_IO_unfavorableSearch[i] = new SummaryStatistics();
				ExtHash_IO_insert[i] = new SummaryStatistics();
				ExtHash_IO_successfulSearch[i] = new SummaryStatistics();
				ExtHash_IO_unfavorableSearch[i] = new SummaryStatistics();
				LinHashV1_IO_insert[i] = new SummaryStatistics();
				LinHashV1_IO_successfulSearch[i] = new SummaryStatistics();
				LinHashV1_IO_unfavorableSearch[i] = new SummaryStatistics();
				LinHashV2_IO_insert[i] = new SummaryStatistics();
				LinHashV2_IO_successfulSearch[i] = new SummaryStatistics();
				LinHashV2_IO_unfavorableSearch[i] = new SummaryStatistics();
			}
			for(int i=0; i<5; i++){
				BTree_OccOut[i] = new SummaryStatistics();
				ExtHash_OccOut[i] = new SummaryStatistics();;
				LinHashV1_OccOut[i] = new SummaryStatistics();
				LinHashV2_OccOut[i] = new SummaryStatistics();
				BTree_IO_deleting[i] = new SummaryStatistics();
				ExtHash_IO_deleting[i] = new SummaryStatistics();
				LinHashV1_IO_deleting[i] = new SummaryStatistics();
				LinHashV2_IO_deleting[i] = new SummaryStatistics();
			}
			for(int r=0; r<max_it; r++){
				printer.println("Fake DNA test "+r);
				System.err.print("Setting up Fake DNA test "+r);
				realDNA = init("fakeDNA"+r+".txt");
				System.err.print(".");
				DiskMemoryManager btree = new BTree();
				System.err.print(".");
				DiskMemoryManager exthash = new ExtendibleHash();
				System.err.print(".");
				DiskMemoryManager linhashV1 = new LinearHashV1();
				System.err.print(".");
				DiskMemoryManager linhashV2 = new LinearHashV2();
				System.err.print(".");
				int actual=0;
				int max = 0;
				System.err.print("Filling >>");
				
				btree.resetIOs();
				exthash.resetIOs();
				linhashV1.resetIOs();
				linhashV2.resetIOs();

				for(int i=l; i<=L; i++){
					System.err.print("2^"+i);
					max = (int)Math.pow(2,i);
					for(int j=actual; j<max; j++){
						btree.add(fakeDNA[i]);
						exthash.add(fakeDNA[i]);
						linhashV1.add(fakeDNA[i]);
						linhashV2.add(fakeDNA[i]);
					}
					System.err.print(".");
					// measure Occupation
					BTree_OccIn[i-20].addValue(btree.getOccupation());
					ExtHash_OccIn[i-20].addValue(exthash.getOccupation());
					LinHashV1_OccIn[i-20].addValue(linhashV1.getOccupation());
					LinHashV2_OccIn[i-20].addValue(linhashV2.getOccupation());
					actual = max;
					String [] patron = generateChains(extracted, fakeDNA, l);


					btree.resetIOs();
					exthash.resetIOs();
					linhashV1.resetIOs();
					linhashV2.resetIOs();
					for(int iterations=0; iterations<10000; iterations++){
						btree.find(patron[iterations]);
						exthash.find(patron[iterations]);
						linhashV1.find(patron[iterations]);
						linhashV2.find(patron[iterations]);
					}
					System.err.print(".");
					// measure each 
					patron = generateChains(random, fakeDNA, l);


					btree.resetIOs();
					exthash.resetIOs();
					linhashV1.resetIOs();
					linhashV2.resetIOs();
					for(int iterations=0; iterations<10000; iterations++){
						btree.find(patron[iterations]);
						exthash.find(patron[iterations]);
						linhashV1.find(patron[iterations]);
						linhashV2.find(patron[iterations]);
					}
					System.err.print(".");
					//measure each
				}
				System.err.print("\nErasing >>");
				actual = (int)Math.pow(2,L);
				int min = 0;

				List<String> a = Arrays.asList(fakeDNA);
				Collections.shuffle(a); // randomize erasing
				fakeDNA = (String)a.toArray();
				
				System.gc(); // clean old realDNA
				for(int i=L; i>l; i--){
					System.err.print("2^"+i+"->2^"+(i-1)+" >> ");
					min = (int)Math.pow(2,i-1);

					btree.resetIOs();
					exthash.resetIOs();
					linhashV1.resetIOs();
					linhashV2.resetIOs();
					for(int j=actual-1; j>=min; j--){
						btree.delete(fakeDNA[i]);
						exthash.delete(fakeDNA[i]);
						linhashV1.delete(fakeDNA[i]);
						linhashV2.delete(fakeDNA[i]);
					}
					// measure Occupation
					BTree_OccOut[i-21].addValue(btree.getOccupation());
					ExtHash_OccOut[i-21].addValue(exthash.getOccupation());
					LinHashV1_OccOut[i-21].addValue(linhashV1.getOccupation());
					LinHashV2_OccOut[i-21].addValue(linhashV2.getOccupation());
					System.err.print(".");
					actual = min;
					//measure each
				}
				btree.resetIOs();
				exthash.resetIOs();
				linhashV1.resetIOs();
				linhashV2.resetIOs();
				for(int j=actual-1; j>=0; j--){
					btree.delete(fakeDNA[i]);
					exthash.delete(fakeDNA[i]);
					linhashV1.delete(fakeDNA[i]);
					linhashV2.delete(fakeDNA[i]);
				}
				//measure
				BTree_IO_erased.addValue(btree.getIOs());
				ExtHash_IO_erased.addValue(exthash.getIOs());
				LinHashV1_IO_erased.addValue(linhashV1.getIOs());
				LinHashV2_IO_erased.addValue(linhashV2.getIOs());
				//end
				destroy(fakeDNA);
				/*	calculate error
					if reasonable, quit
				*/
			}
			/* print to file */
		}
	}
}

