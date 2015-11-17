package Tarea2Algoritmos;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
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
	
	static public String[] init(String filename, int k) throws FileNotFoundException{
		int i = (int)Math.pow(2, k);
		String line [] = new String[i];
		File file = new File(filename);
		@SuppressWarnings("resource")
		Scanner scanner = new Scanner(file);
		int j=0;
		line[j++]= scanner.nextLine();
		while (scanner.hasNextLine() && j<i) {
		       line[j++]= scanner.nextLine();
		       if(j%1000000 == 0){
		    	   System.err.print(",");
		       }
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

	public static void main(String[] args) throws IOException{
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
		
		int l=10; // minimal measure
		int L=25; // maximal measure
		int max_it=100;
		if (cmd.hasOption("iterations")) {
			int nn = Integer.parseInt(cmd.getOptionValue(iter.getOpt()));
			if(nn>=1 && nn<=10000)
			max_it=nn;
		}
		if (cmd.hasOption("i")) {
			int nn = Integer.parseInt(cmd.getOptionValue(power.getOpt()));
			if(nn>=10 && nn<=25)
			l=nn;
		}
		if (cmd.hasOption("I")) {
			int nn = Integer.parseInt(cmd.getOptionValue(power2.getOpt()));
			if(nn>=l && nn<=25)
			L=nn;
		}
		boolean random = false;
		boolean extracted = true;
		/*
		Medir Ocupación:
		Al entrar 2^20, 2^21, 2^22, 2^23, 2^24, 2^25
		Al salir 2^24, 2^23, 2^22, 2^21, 2^20
		*/

		SummaryStatistics BTree_OccIn[] = new SummaryStatistics[6];
		SummaryStatistics BTree_OccOut[] = new SummaryStatistics[5];
		SummaryStatistics ExtHash_OccIn[] = new SummaryStatistics[6];
		SummaryStatistics ExtHash_OccOut[] = new SummaryStatistics[5];
		SummaryStatistics LinHashV1_OccIn[] = new SummaryStatistics[6];
		SummaryStatistics LinHashV1_OccOut[] = new SummaryStatistics[5];
		SummaryStatistics LinHashV2_OccIn[] = new SummaryStatistics[6];
		SummaryStatistics LinHashV2_OccOut[] = new SummaryStatistics[5];
		
		/*Medir IO
		for i in [20, 25]
			Luego de insertar 2^i
			Tras realizar 10000 búsquedas "exitosas"
			Tras realizar 10000 búsquedas "infructuosas"
		for i in [25, 21]
			Luego de borrar entre i e i-1
		Luego de borrar todo
		*/
		SummaryStatistics BTree_IO_insert[] = new SummaryStatistics[6];
		SummaryStatistics BTree_IO_successfulSearch[] = new SummaryStatistics[6];
		SummaryStatistics BTree_IO_unfavorableSearch[] = new SummaryStatistics[6];
		SummaryStatistics BTree_IO_deleting[] = new SummaryStatistics[5];
		SummaryStatistics BTree_IO_erased = new SummaryStatistics(); // resetear
		SummaryStatistics ExtHash_IO_insert[] = new SummaryStatistics[6];
		SummaryStatistics ExtHash_IO_successfulSearch[] = new SummaryStatistics[6];
		SummaryStatistics ExtHash_IO_unfavorableSearch[] = new SummaryStatistics[6];
		SummaryStatistics ExtHash_IO_deleting[] = new SummaryStatistics[5];
		SummaryStatistics ExtHash_IO_erased = new SummaryStatistics(); // resetear
		SummaryStatistics LinHashV1_IO_insert[] = new SummaryStatistics[6];
		SummaryStatistics LinHashV1_IO_successfulSearch[] = new SummaryStatistics[6];
		SummaryStatistics LinHashV1_IO_unfavorableSearch[] = new SummaryStatistics[6];
		SummaryStatistics LinHashV1_IO_deleting[] = new SummaryStatistics[5];
		SummaryStatistics LinHashV1_IO_erased = new SummaryStatistics(); // resetear
		SummaryStatistics LinHashV2_IO_insert[] = new SummaryStatistics[6];
		SummaryStatistics LinHashV2_IO_successfulSearch[] = new SummaryStatistics[6];
		SummaryStatistics LinHashV2_IO_unfavorableSearch[] = new SummaryStatistics[6];
		SummaryStatistics LinHashV2_IO_deleting[] = new SummaryStatistics[5];
		SummaryStatistics LinHashV2_IO_erased = new SummaryStatistics(); //resetear
		/*SOMEHOW*/
	
		// test real DNA
		printer.println("Iteracion\tNombre\tPromedio\tDesviacion\tError(2sigma/raiz(n)*promedio)");
		if (cmd.hasOption("rd")) {
			int bhelper_low = 0,
				bhelper_high = 0,
				exthelper_low = 0,
				exthelper_high = 0,
				lin1helper_low = 0,
				lin1helper_high = 0,
				lin2helper_low = 0,
				lin2helper_high = 0;
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
				realDNA = init("realDNA"+r+".txt", L);
				System.err.print(".");
				DiskMemoryManager btree = new DummyBTree();
				System.err.print(".");
				DiskMemoryManager exthash = new ExtendibleHash();
				System.err.print(".");
				DiskMemoryManager linhashV1 = new LinearHashV1();
				System.err.print(".");
				DiskMemoryManager linhashV2 = new LinearHashV2();
				System.err.print(".");
				int actual=0;
				int max = 0;
				System.err.print("\nFilling >>");
				for(int i=l; i<=L; i++){
					System.err.print("2^"+i);
					max = (int)Math.pow(2,i);
					for(int j=actual; j<max; j++){
						btree.add(realDNA[j]);
						exthash.add(realDNA[j]);
						linhashV1.add(realDNA[j]);
						linhashV2.add(realDNA[j]);
					}
					System.err.print(".");
					// measure Occupation
					BTree_OccIn[i-l].addValue(btree.getOccupation());
					ExtHash_OccIn[i-l].addValue(exthash.getOccupation());
					LinHashV1_OccIn[i-l].addValue(linhashV1.getOccupation());
					LinHashV2_OccIn[i-l].addValue(linhashV2.getOccupation());
					//measure IO
					bhelper_high = btree.getIOs();
					exthelper_high = exthash.getIOs();
					lin1helper_high = linhashV1.getIOs();
					lin2helper_high = linhashV1.getIOs();
					BTree_IO_insert[i-l].addValue(bhelper_high + bhelper_low);
					ExtHash_IO_insert[i-l].addValue(exthelper_high + exthelper_low);
					LinHashV1_IO_insert[i-l].addValue(lin1helper_high + lin1helper_low);
					LinHashV2_IO_insert[i-l].addValue(lin2helper_high + lin2helper_low);
					btree.resetIOs();
					exthash.resetIOs();
					linhashV1.resetIOs();
					linhashV2.resetIOs();
					//end measure IO
					actual = max;
					// find successful
					String [] patron = generateChains(extracted, realDNA, l);
					for(int iterations=0; iterations<10000; iterations++){
						btree.find(patron[iterations]);
						exthash.find(patron[iterations]);
						linhashV1.find(patron[iterations]);
						linhashV2.find(patron[iterations]);
					}
					System.err.print(".");
					// measure IOs
					BTree_IO_successfulSearch[i-l].addValue(btree.getIOs());
					ExtHash_IO_successfulSearch[i-l].addValue(exthash.getIOs());
					LinHashV1_IO_successfulSearch[i-l].addValue(linhashV1.getIOs());
					LinHashV2_IO_successfulSearch[i-l].addValue(linhashV1.getIOs());
					btree.resetIOs();
					exthash.resetIOs();
					linhashV1.resetIOs();
					linhashV2.resetIOs();
					//end measure IOs
					// find unfavorable
					patron = generateChains(random, realDNA, l);
					for(int iterations=0; iterations<10000; iterations++){
						btree.find(patron[iterations]);
						exthash.find(patron[iterations]);
						linhashV1.find(patron[iterations]);
						linhashV2.find(patron[iterations]);
					}
					System.err.print(".");
					//measure IOs
					BTree_IO_unfavorableSearch[i-l].addValue(btree.getIOs());
					ExtHash_IO_unfavorableSearch[i-l].addValue(exthash.getIOs());
					LinHashV1_IO_unfavorableSearch[i-l].addValue(linhashV1.getIOs());
					LinHashV2_IO_unfavorableSearch[i-l].addValue(linhashV1.getIOs());
					btree.resetIOs();
					exthash.resetIOs();
					linhashV1.resetIOs();
					linhashV2.resetIOs();
					// migrate helpers => low accumulates all inserting IOs
					bhelper_low += bhelper_high;
					exthelper_low += exthelper_high;
					lin1helper_low+= lin1helper_high;
					lin2helper_low += lin2helper_high;
				}
				System.err.print("\nErasing >>");
				actual = (int)Math.pow(2,L);
				int min = 0;
				bhelper_low = bhelper_high = 0;
				exthelper_low = exthelper_high = 0;
				lin1helper_low = lin1helper_high = 0;
				lin2helper_low = lin2helper_high = 0;

				// randomize erasing
				List<String> a = Arrays.asList(realDNA);
				Collections.shuffle(a); 
				realDNA = (String[])a.toArray();
				
				System.gc(); // clean old realDNA
				for(int i=L; i>l; i--){
					System.err.print("2^"+i+"->2^"+(i-1)+" >> ");
					min = (int)Math.pow(2,i-1);
					for(int j=actual-1; j>=min; j--){
						btree.delete(realDNA[j]);
						exthash.delete(realDNA[j]);
						linhashV1.delete(realDNA[j]);
						linhashV2.delete(realDNA[j]);
					}
					System.err.print(".");
					actual = min;
					// measure Occupation
					BTree_OccOut[i-l-1].addValue(btree.getOccupation());
					ExtHash_OccOut[i-l-1].addValue(exthash.getOccupation());
					LinHashV1_OccOut[i-l-1].addValue(linhashV1.getOccupation());
					LinHashV2_OccOut[i-l-1].addValue(linhashV2.getOccupation());
					//measure IOs
					BTree_IO_deleting[i-l-1].addValue(btree.getIOs());
					ExtHash_IO_deleting[i-l-1].addValue(exthash.getIOs());
					LinHashV1_IO_deleting[i-l-1].addValue(linhashV1.getOccupation());
					LinHashV2_IO_deleting[i-l-1].addValue(linhashV2.getOccupation());
					
				}
				for(int i=actual-1; i>=0; i--){
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
				printer.println("Occupation In");
				for(int k=0; k<6; k++){
					printer.println(r+"\t"+"BTree_OccIn(2^"+(k+l)+")"+"\t"+BTree_OccIn[k].getMean() +"\t"
							+BTree_OccIn[k].getStandardDeviation()+"\t"
							+(2*BTree_OccIn[k].getStandardDeviation()/(Math.sqrt(r)*BTree_OccIn[k].getMean())));
					printer.println(r+"\t"+"ExtHash_OccIn(2^"+(k+l)+")"+"\t"+ExtHash_OccIn[k].getMean() +"\t"
							+ExtHash_OccIn[k].getStandardDeviation()+"\t"
							+(2*ExtHash_OccIn[k].getStandardDeviation()/(Math.sqrt(r)*ExtHash_OccIn[k].getMean())));
					printer.println(r+"\t"+"LinHashV1_OccIn(2^"+(k+l)+")"+"\t"+LinHashV1_OccIn[k].getMean() +"\t"
							+LinHashV1_OccIn[k].getStandardDeviation()+"\t"
							+(2*LinHashV1_OccIn[k].getStandardDeviation()/(Math.sqrt(r)*LinHashV1_OccIn[k].getMean())));
					printer.println(r+"\t"+"LinHashV2_OccIn(2^"+(k+l)+")"+"\t"+LinHashV2_OccIn[k].getMean() +"\t"
							+LinHashV2_OccIn[k].getStandardDeviation()+"\t"
							+(2*LinHashV2_OccIn[k].getStandardDeviation()/(Math.sqrt(r)*LinHashV2_OccIn[k].getMean())));
				}
				printer.println("Occupation Out");
				for(int k=4; k>=0; k++){
					printer.println(r+"\t"+"BTree_OccOut(2^"+(k+l)+")"+"\t"+BTree_OccIn[k].getMean() +"\t"
							+BTree_OccOut[k].getStandardDeviation()+"\t"
							+(2*BTree_OccOut[k].getStandardDeviation()/(Math.sqrt(r)*BTree_OccOut[k].getMean())));
					printer.println(r+"\t"+"ExtHash_OccOut(2^"+(k+l)+")"+"\t"+ExtHash_OccOut[k].getMean() +"\t"
							+ExtHash_OccOut[k].getStandardDeviation()+"\t"
							+(2*ExtHash_OccOut[k].getStandardDeviation()/(Math.sqrt(r)*ExtHash_OccOut[k].getMean())));
					printer.println(r+"\t"+"LinHashV1_OccIn(2^"+(k+l)+")"+"\t"+LinHashV1_OccOut[k].getMean() +"\t"
							+LinHashV1_OccOut[k].getStandardDeviation()+"\t"
							+(2*LinHashV1_OccOut[k].getStandardDeviation()/(Math.sqrt(r)*LinHashV1_OccOut[k].getMean())));
					printer.println(r+"\t"+"LinHashV2_OccIn(2^"+(k+l)+")"+"\t"+LinHashV2_OccOut[k].getMean() +"\t"
							+LinHashV2_OccOut[k].getStandardDeviation()+"\t"
							+(2*LinHashV2_OccOut[k].getStandardDeviation()/(Math.sqrt(r)*LinHashV2_OccOut[k].getMean())));
				}
				printer.println("IO in");
				for(int k=0; k<6; k++){
					printer.println(r+"\t"+"BTree_IO_insert(2^"+(k+l)+")"+"\t"+BTree_IO_insert[k].getMean() +"\t"
							+BTree_IO_insert[k].getStandardDeviation()+"\t"
							+(2*BTree_IO_insert[k].getStandardDeviation()/(Math.sqrt(r)*BTree_IO_insert[k].getMean())));
					printer.println(r+"\t"+"ExtHash_IO_insert(2^"+(k+l)+")"+"\t"+ExtHash_IO_insert[k].getMean() +"\t"
							+ExtHash_IO_insert[k].getStandardDeviation()+"\t"
							+(2*ExtHash_IO_insert[k].getStandardDeviation()/(Math.sqrt(r)*ExtHash_IO_insert[k].getMean())));
					printer.println(r+"\t"+"LinHashV1_IO_insert(2^"+(k+l)+")"+"\t"+LinHashV1_IO_insert[k].getMean() +"\t"
							+LinHashV1_IO_insert[k].getStandardDeviation()+"\t"
							+(2*LinHashV1_IO_insert[k].getStandardDeviation()/(Math.sqrt(r)*LinHashV1_IO_insert[k].getMean())));
					printer.println(r+"\t"+"LinHashV2_IO_insert(2^"+(k+l)+")"+"\t"+LinHashV2_IO_insert[k].getMean() +"\t"
							+LinHashV2_IO_insert[k].getStandardDeviation()+"\t"
							+(2*LinHashV2_IO_insert[k].getStandardDeviation()/(Math.sqrt(r)*LinHashV2_IO_insert[k].getMean())));
				}
				printer.println("IO out");
				for(int k=4; k>=0; k++){
					printer.println(r+"\t"+"BTree_IO_deleting(2^"+(k+l)+")"+"\t"+BTree_IO_deleting[k].getMean() +"\t"
							+BTree_IO_deleting[k].getStandardDeviation()+"\t"
							+(2*BTree_IO_deleting[k].getStandardDeviation()/(Math.sqrt(r)*BTree_IO_deleting[k].getMean())));
					printer.println(r+"\t"+"ExtHash_IO_deleting(2^"+(k+l)+")"+"\t"+ExtHash_IO_deleting[k].getMean() +"\t"
							+ExtHash_IO_deleting[k].getStandardDeviation()+"\t"
							+(2*ExtHash_IO_deleting[k].getStandardDeviation()/(Math.sqrt(r)*ExtHash_IO_deleting[k].getMean())));
					printer.println(r+"\t"+"LinHashV1_IO_deleting(2^"+(k+l)+")"+"\t"+LinHashV1_IO_deleting[k].getMean() +"\t"
							+LinHashV1_IO_deleting[k].getStandardDeviation()+"\t"
							+(2*LinHashV1_IO_deleting[k].getStandardDeviation()/(Math.sqrt(r)*LinHashV1_IO_deleting[k].getMean())));
					printer.println(r+"\t"+"LinHashV2_IO_deleting(2^"+(k+l)+")"+"\t"+LinHashV2_IO_deleting[k].getMean() +"\t"
							+LinHashV2_IO_deleting[k].getStandardDeviation()+"\t"
							+(2*LinHashV2_IO_deleting[k].getStandardDeviation()/(Math.sqrt(r)*LinHashV2_IO_deleting[k].getMean())));
				}
				printer.println(r+"\t"+"BTree_IO_erased"+"\t"+BTree_IO_erased.getMean() +"\t"
						+BTree_IO_erased.getStandardDeviation()+"\t"
						+(2*BTree_IO_erased.getStandardDeviation()/(Math.sqrt(r)*BTree_IO_erased.getMean())));
				printer.println(r+"\t"+"ExtHash_IO_erased"+"\t"+ExtHash_IO_erased.getMean() +"\t"
						+ExtHash_IO_erased.getStandardDeviation()+"\t"
						+(2*ExtHash_IO_erased.getStandardDeviation()/(Math.sqrt(r)*ExtHash_IO_erased.getMean())));
				printer.println(r+"\t"+"LinHashV1_IO_erased"+"\t"+LinHashV1_IO_erased.getMean() +"\t"
						+LinHashV1_IO_erased.getStandardDeviation()+"\t"
						+(2*LinHashV1_IO_erased.getStandardDeviation()/(Math.sqrt(r)*LinHashV1_IO_erased.getMean())));
				printer.println(r+"\t"+"LinHashV2_IO_erased"+"\t"+LinHashV2_IO_erased.getMean() +"\t"
						+LinHashV2_IO_erased.getStandardDeviation()+"\t"
						+(2*LinHashV2_IO_erased.getStandardDeviation()/(Math.sqrt(r)*LinHashV2_IO_erased.getMean())));
				printer.println("IO successful search");
				for(int k=0; k<6; k++){
					printer.println(r+"\t"+"BTree_IO_insert(2^"+(k+l)+")"+"\t"+BTree_IO_insert[k].getMean() +"\t"
							+BTree_IO_insert[k].getStandardDeviation()+"\t"
							+(2*BTree_IO_insert[k].getStandardDeviation()/(Math.sqrt(r)*BTree_IO_insert[k].getMean())));
					printer.println(r+"\t"+"ExtHash_IO_insert(2^"+(k+l)+")"+"\t"+ExtHash_IO_insert[k].getMean() +"\t"
							+ExtHash_IO_insert[k].getStandardDeviation()+"\t"
							+(2*ExtHash_IO_insert[k].getStandardDeviation()/(Math.sqrt(r)*ExtHash_IO_insert[k].getMean())));
					printer.println(r+"\t"+"LinHashV1_IO_insert(2^"+(k+l)+")"+"\t"+LinHashV1_IO_insert[k].getMean() +"\t"
							+LinHashV1_IO_insert[k].getStandardDeviation()+"\t"
							+(2*LinHashV1_IO_insert[k].getStandardDeviation()/(Math.sqrt(r)*LinHashV1_IO_insert[k].getMean())));
					printer.println(r+"\t"+"LinHashV2_IO_insert(2^"+(k+l)+")"+"\t"+LinHashV2_IO_insert[k].getMean() +"\t"
							+LinHashV2_IO_insert[k].getStandardDeviation()+"\t"
							+(2*LinHashV2_IO_insert[k].getStandardDeviation()/(Math.sqrt(r)*LinHashV2_IO_insert[k].getMean())));
				}
				printer.println("IO unfavorable search");
				for(int k=0; k<6; k++){
					printer.println(r+"\t"+"BTree_IO_unfavorableSearch(2^"+(k+l)+")"+"\t"+BTree_IO_unfavorableSearch[k].getMean() +"\t"
							+BTree_IO_unfavorableSearch[k].getStandardDeviation()+"\t"
							+(2*BTree_IO_unfavorableSearch[k].getStandardDeviation()/(Math.sqrt(r)*BTree_IO_unfavorableSearch[k].getMean())));
					printer.println(r+"\t"+"ExtHash_IO_unfavorableSearch(2^"+(k+l)+")"+"\t"+ExtHash_IO_unfavorableSearch[k].getMean() +"\t"
							+ExtHash_IO_unfavorableSearch[k].getStandardDeviation()+"\t"
							+(2*ExtHash_IO_unfavorableSearch[k].getStandardDeviation()/(Math.sqrt(r)*ExtHash_IO_unfavorableSearch[k].getMean())));
					printer.println(r+"\t"+"LinHashV1_IO_unfavorableSearch(2^"+(k+l)+")"+"\t"+LinHashV1_IO_unfavorableSearch[k].getMean() +"\t"
							+LinHashV1_IO_unfavorableSearch[k].getStandardDeviation()+"\t"
							+(2*LinHashV1_IO_unfavorableSearch[k].getStandardDeviation()/(Math.sqrt(r)*LinHashV1_IO_unfavorableSearch[k].getMean())));
					printer.println(r+"\t"+"LinHashV2_IO_insert(2^"+(k+l)+")"+"\t"+LinHashV2_IO_unfavorableSearch[k].getMean() +"\t"
							+LinHashV2_IO_unfavorableSearch[k].getStandardDeviation()+"\t"
							+(2*LinHashV2_IO_unfavorableSearch[k].getStandardDeviation()/(Math.sqrt(r)*LinHashV2_IO_unfavorableSearch[k].getMean())));
				}
			}
			/* print to file */
			// occupacy			
		}
		// test fake DNA
		BTree_IO_erased = new SummaryStatistics(); // reset
		ExtHash_IO_erased = new SummaryStatistics(); // reset
		LinHashV1_IO_erased = new SummaryStatistics(); // reset
		LinHashV2_IO_erased = new SummaryStatistics(); //reset
		if (cmd.hasOption("fd")){
			int bhelper_low = 0,
				bhelper_high = 0,
				exthelper_low = 0,
				exthelper_high = 0,
				lin1helper_low = 0,
				lin1helper_high = 0,
				lin2helper_low = 0,
				lin2helper_high = 0;
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
				fakeDNA = init("fakeDNA"+r+".txt", L);
				System.err.print(".");
				DiskMemoryManager btree = new DummyBTree();
				System.err.print(".");
				DiskMemoryManager exthash = new ExtendibleHash();
				System.err.print(".");
				DiskMemoryManager linhashV1 = new LinearHashV1();
				System.err.print(".");
				DiskMemoryManager linhashV2 = new LinearHashV2();
				System.err.print(".");
				int actual=0;
				int max = 0;
				System.err.print("\nFilling >>");
				for(int i=l; i<=L; i++){
					System.err.print("2^"+i);
					max = (int)Math.pow(2,i);
					for(int j=actual; j<max; j++){
						btree.add(fakeDNA[j]);
						System.out.print("1");
						exthash.add(fakeDNA[j]);
						System.out.print("2");
						linhashV1.add(fakeDNA[j]);
						System.out.print("3");
						linhashV2.add(fakeDNA[j]);
						System.out.print("4");
					}
					System.err.print(".");
					// measure Occupation
					BTree_OccIn[i-l].addValue(btree.getOccupation());
					ExtHash_OccIn[i-l].addValue(exthash.getOccupation());
					LinHashV1_OccIn[i-l].addValue(linhashV1.getOccupation());
					LinHashV2_OccIn[i-l].addValue(linhashV2.getOccupation());
					System.out.println("Finished ocupation");
					//measure IO
					bhelper_high = btree.getIOs();
					exthelper_high = exthash.getIOs();
					lin1helper_high = linhashV1.getIOs();
					lin2helper_high = linhashV1.getIOs();
					BTree_IO_insert[i-l].addValue(bhelper_high + bhelper_low);
					ExtHash_IO_insert[i-l].addValue(exthelper_high + exthelper_low);
					LinHashV1_IO_insert[i-l].addValue(lin1helper_high + lin1helper_low);
					LinHashV2_IO_insert[i-l].addValue(lin2helper_high + lin2helper_low);
					btree.resetIOs();
					exthash.resetIOs();
					linhashV1.resetIOs();
					linhashV2.resetIOs();
					System.out.println("Finish IO");
					//end measure IO
					actual = max;
					//find successful
					String [] patron = generateChains(extracted, fakeDNA, l);
					for(int iterations=0; iterations<10000; iterations++){
						btree.find(patron[iterations]);
						System.out.print("F1");
						exthash.find(patron[iterations]);
						System.out.print("F2");
						linhashV1.find(patron[iterations]);
						System.out.print("F3");
						linhashV2.find(patron[iterations]);
						System.out.print("F4");
					}
					System.err.print(".");
					// measure IOs
					BTree_IO_successfulSearch[i-l].addValue(btree.getIOs());
					ExtHash_IO_successfulSearch[i-l].addValue(exthash.getIOs());
					LinHashV1_IO_successfulSearch[i-l].addValue(linhashV1.getIOs());
					LinHashV2_IO_successfulSearch[i-l].addValue(linhashV1.getIOs());
					btree.resetIOs();
					exthash.resetIOs();
					linhashV1.resetIOs();
					linhashV2.resetIOs();
					//end measure IOs
					//find unfavorable
					patron = generateChains(random, fakeDNA, l);
					for(int iterations=0; iterations<10000; iterations++){
						btree.find(patron[iterations]);
						exthash.find(patron[iterations]);
						linhashV1.find(patron[iterations]);
						linhashV2.find(patron[iterations]);
					}
					System.err.print(".");
					//measure IOs
					BTree_IO_unfavorableSearch[i-l].addValue(btree.getIOs());
					ExtHash_IO_unfavorableSearch[i-l].addValue(exthash.getIOs());
					LinHashV1_IO_unfavorableSearch[i-l].addValue(linhashV1.getIOs());
					LinHashV2_IO_unfavorableSearch[i-l].addValue(linhashV1.getIOs());
					btree.resetIOs();
					exthash.resetIOs();
					linhashV1.resetIOs();
					linhashV2.resetIOs();
					// migrate helpers => low accumulates all inserting IOs
					bhelper_low += bhelper_high;
					exthelper_low += exthelper_high;
					lin1helper_low+= lin1helper_high;
					lin2helper_low += lin2helper_high;
				}
				System.err.print("\nErasing >>");
				actual = (int)Math.pow(2,L);
				int min = 0;
				bhelper_low = bhelper_high = 0;
				exthelper_low = exthelper_high = 0;
				lin1helper_low = lin1helper_high = 0;
				lin2helper_low = lin2helper_high = 0;

				// randomize erasing
				List<String> a = Arrays.asList(fakeDNA);
				Collections.shuffle(a); 
				fakeDNA = (String[])a.toArray();
				
				System.gc(); // clean old fakeDNA
				for(int i=L; i>l; i--){
					System.err.print("2^"+i+"->2^"+(i-1)+" >> ");
					min = (int)Math.pow(2,i-1);
					for(int j=actual-1; j>=min; j--){
						btree.delete(fakeDNA[i]);
						System.out.print("D1");
						exthash.delete(fakeDNA[i]);
						System.out.print("D2");
						linhashV1.delete(fakeDNA[i]);
						System.out.print("D3");
						linhashV2.delete(fakeDNA[i]);
						System.out.print("D4");
					}
					System.err.print(".");
					actual = min;
					// measure Occupation
					BTree_OccOut[i-l-1].addValue(btree.getOccupation());
					ExtHash_OccOut[i-l-1].addValue(exthash.getOccupation());
					LinHashV1_OccOut[i-l-1].addValue(linhashV1.getOccupation());
					LinHashV2_OccOut[i-l-1].addValue(linhashV2.getOccupation());
					//measure IOs
					BTree_IO_deleting[i-l-1].addValue(btree.getIOs());
					ExtHash_IO_deleting[i-l-1].addValue(exthash.getIOs());
					LinHashV1_IO_deleting[i-l-1].addValue(linhashV1.getOccupation());
					LinHashV2_IO_deleting[i-l-1].addValue(linhashV2.getOccupation());
					
				}
				for(int i=actual-1; i>=0; i--){
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
				printer.println("Occupation In");
				for(int k=0; k<6; k++){
					printer.println(r+"\t"+"BTree_OccIn(2^"+(k+l)+")"+"\t"+BTree_OccIn[k].getMean() +"\t"
							+BTree_OccIn[k].getStandardDeviation()+"\t"
							+(2*BTree_OccIn[k].getStandardDeviation()/(Math.sqrt(r)*BTree_OccIn[k].getMean())));
					printer.println(r+"\t"+"ExtHash_OccIn(2^"+(k+l)+")"+"\t"+ExtHash_OccIn[k].getMean() +"\t"
							+ExtHash_OccIn[k].getStandardDeviation()+"\t"
							+(2*ExtHash_OccIn[k].getStandardDeviation()/(Math.sqrt(r)*ExtHash_OccIn[k].getMean())));
					printer.println(r+"\t"+"LinHashV1_OccIn(2^"+(k+l)+")"+"\t"+LinHashV1_OccIn[k].getMean() +"\t"
							+LinHashV1_OccIn[k].getStandardDeviation()+"\t"
							+(2*LinHashV1_OccIn[k].getStandardDeviation()/(Math.sqrt(r)*LinHashV1_OccIn[k].getMean())));
					printer.println(r+"\t"+"LinHashV2_OccIn(2^"+(k+l)+")"+"\t"+LinHashV2_OccIn[k].getMean() +"\t"
							+LinHashV2_OccIn[k].getStandardDeviation()+"\t"
							+(2*LinHashV2_OccIn[k].getStandardDeviation()/(Math.sqrt(r)*LinHashV2_OccIn[k].getMean())));
				}
				printer.println("Occupation Out");
				// TODO
				for(int k=4; k>=0; k--){
					printer.println(r+"\t"+"BTree_OccOut(2^"+(k+l)+")"+"\t"+BTree_OccIn[k].getMean() +"\t"
							+BTree_OccOut[k].getStandardDeviation()+"\t"
							+(2*BTree_OccOut[k].getStandardDeviation()/(Math.sqrt(r)*BTree_OccOut[k].getMean())));
					printer.println(r+"\t"+"ExtHash_OccOut(2^"+(k+l)+")"+"\t"+ExtHash_OccOut[k].getMean() +"\t"
							+ExtHash_OccOut[k].getStandardDeviation()+"\t"
							+(2*ExtHash_OccOut[k].getStandardDeviation()/(Math.sqrt(r)*ExtHash_OccOut[k].getMean())));
					printer.println(r+"\t"+"LinHashV1_OccIn(2^"+(k+l)+")"+"\t"+LinHashV1_OccOut[k].getMean() +"\t"
							+LinHashV1_OccOut[k].getStandardDeviation()+"\t"
							+(2*LinHashV1_OccOut[k].getStandardDeviation()/(Math.sqrt(r)*LinHashV1_OccOut[k].getMean())));
					printer.println(r+"\t"+"LinHashV2_OccIn(2^"+(k+l)+")"+"\t"+LinHashV2_OccOut[k].getMean() +"\t"
							+LinHashV2_OccOut[k].getStandardDeviation()+"\t"
							+(2*LinHashV2_OccOut[k].getStandardDeviation()/(Math.sqrt(r)*LinHashV2_OccOut[k].getMean())));
				}
				printer.println("IO in");
				for(int k=0; k<6; k++){
					printer.println(r+"\t"+"BTree_IO_insert(2^"+(k+l)+")"+"\t"+BTree_IO_insert[k].getMean() +"\t"
							+BTree_IO_insert[k].getStandardDeviation()+"\t"
							+(2*BTree_IO_insert[k].getStandardDeviation()/(Math.sqrt(r)*BTree_IO_insert[k].getMean())));
					printer.println(r+"\t"+"ExtHash_IO_insert(2^"+(k+l)+")"+"\t"+ExtHash_IO_insert[k].getMean() +"\t"
							+ExtHash_IO_insert[k].getStandardDeviation()+"\t"
							+(2*ExtHash_IO_insert[k].getStandardDeviation()/(Math.sqrt(r)*ExtHash_IO_insert[k].getMean())));
					printer.println(r+"\t"+"LinHashV1_IO_insert(2^"+(k+l)+")"+"\t"+LinHashV1_IO_insert[k].getMean() +"\t"
							+LinHashV1_IO_insert[k].getStandardDeviation()+"\t"
							+(2*LinHashV1_IO_insert[k].getStandardDeviation()/(Math.sqrt(r)*LinHashV1_IO_insert[k].getMean())));
					printer.println(r+"\t"+"LinHashV2_IO_insert(2^"+(k+l)+")"+"\t"+LinHashV2_IO_insert[k].getMean() +"\t"
							+LinHashV2_IO_insert[k].getStandardDeviation()+"\t"
							+(2*LinHashV2_IO_insert[k].getStandardDeviation()/(Math.sqrt(r)*LinHashV2_IO_insert[k].getMean())));
				}
				printer.println("IO out");
				//TODO
				for(int k=4; k>=0; k--){
					printer.println(r+"\t"+"BTree_IO_deleting(2^"+(k+l)+")"+"\t"+BTree_IO_deleting[k].getMean() +"\t"
							+BTree_IO_deleting[k].getStandardDeviation()+"\t"
							+(2*BTree_IO_deleting[k].getStandardDeviation()/(Math.sqrt(r)*BTree_IO_deleting[k].getMean())));
					printer.println(r+"\t"+"ExtHash_IO_deleting(2^"+(k+l)+")"+"\t"+ExtHash_IO_deleting[k].getMean() +"\t"
							+ExtHash_IO_deleting[k].getStandardDeviation()+"\t"
							+(2*ExtHash_IO_deleting[k].getStandardDeviation()/(Math.sqrt(r)*ExtHash_IO_deleting[k].getMean())));
					printer.println(r+"\t"+"LinHashV1_IO_deleting(2^"+(k+l)+")"+"\t"+LinHashV1_IO_deleting[k].getMean() +"\t"
							+LinHashV1_IO_deleting[k].getStandardDeviation()+"\t"
							+(2*LinHashV1_IO_deleting[k].getStandardDeviation()/(Math.sqrt(r)*LinHashV1_IO_deleting[k].getMean())));
					printer.println(r+"\t"+"LinHashV2_IO_deleting(2^"+(k+l)+")"+"\t"+LinHashV2_IO_deleting[k].getMean() +"\t"
							+LinHashV2_IO_deleting[k].getStandardDeviation()+"\t"
							+(2*LinHashV2_IO_deleting[k].getStandardDeviation()/(Math.sqrt(r)*LinHashV2_IO_deleting[k].getMean())));
				}
				printer.println(r+"\t"+"BTree_IO_erased"+"\t"+BTree_IO_erased.getMean() +"\t"
						+BTree_IO_erased.getStandardDeviation()+"\t"
						+(2*BTree_IO_erased.getStandardDeviation()/(Math.sqrt(r)*BTree_IO_erased.getMean())));
				printer.println(r+"\t"+"ExtHash_IO_erased"+"\t"+ExtHash_IO_erased.getMean() +"\t"
						+ExtHash_IO_erased.getStandardDeviation()+"\t"
						+(2*ExtHash_IO_erased.getStandardDeviation()/(Math.sqrt(r)*ExtHash_IO_erased.getMean())));
				printer.println(r+"\t"+"LinHashV1_IO_erased"+"\t"+LinHashV1_IO_erased.getMean() +"\t"
						+LinHashV1_IO_erased.getStandardDeviation()+"\t"
						+(2*LinHashV1_IO_erased.getStandardDeviation()/(Math.sqrt(r)*LinHashV1_IO_erased.getMean())));
				printer.println(r+"\t"+"LinHashV2_IO_erased"+"\t"+LinHashV2_IO_erased.getMean() +"\t"
						+LinHashV2_IO_erased.getStandardDeviation()+"\t"
						+(2*LinHashV2_IO_erased.getStandardDeviation()/(Math.sqrt(r)*LinHashV2_IO_erased.getMean())));
				printer.println("IO successful search");
				for(int k=0; k<6; k++){
					printer.println(r+"\t"+"BTree_IO_insert(2^"+(k+l)+")"+"\t"+BTree_IO_insert[k].getMean() +"\t"
							+BTree_IO_insert[k].getStandardDeviation()+"\t"
							+(2*BTree_IO_insert[k].getStandardDeviation()/(Math.sqrt(r)*BTree_IO_insert[k].getMean())));
					printer.println(r+"\t"+"ExtHash_IO_insert(2^"+(k+l)+")"+"\t"+ExtHash_IO_insert[k].getMean() +"\t"
							+ExtHash_IO_insert[k].getStandardDeviation()+"\t"
							+(2*ExtHash_IO_insert[k].getStandardDeviation()/(Math.sqrt(r)*ExtHash_IO_insert[k].getMean())));
					printer.println(r+"\t"+"LinHashV1_IO_insert(2^"+(k+l)+")"+"\t"+LinHashV1_IO_insert[k].getMean() +"\t"
							+LinHashV1_IO_insert[k].getStandardDeviation()+"\t"
							+(2*LinHashV1_IO_insert[k].getStandardDeviation()/(Math.sqrt(r)*LinHashV1_IO_insert[k].getMean())));
					printer.println(r+"\t"+"LinHashV2_IO_insert(2^"+(k+l)+")"+"\t"+LinHashV2_IO_insert[k].getMean() +"\t"
							+LinHashV2_IO_insert[k].getStandardDeviation()+"\t"
							+(2*LinHashV2_IO_insert[k].getStandardDeviation()/(Math.sqrt(r)*LinHashV2_IO_insert[k].getMean())));
				}
				printer.println("IO unfavorable search");
				for(int k=0; k<6; k++){
					printer.println(r+"\t"+"BTree_IO_unfavorableSearch(2^"+(k+l)+")"+"\t"+BTree_IO_unfavorableSearch[k].getMean() +"\t"
							+BTree_IO_unfavorableSearch[k].getStandardDeviation()+"\t"
							+(2*BTree_IO_unfavorableSearch[k].getStandardDeviation()/(Math.sqrt(r)*BTree_IO_unfavorableSearch[k].getMean())));
					printer.println(r+"\t"+"ExtHash_IO_unfavorableSearch(2^"+(k+l)+")"+"\t"+ExtHash_IO_unfavorableSearch[k].getMean() +"\t"
							+ExtHash_IO_unfavorableSearch[k].getStandardDeviation()+"\t"
							+(2*ExtHash_IO_unfavorableSearch[k].getStandardDeviation()/(Math.sqrt(r)*ExtHash_IO_unfavorableSearch[k].getMean())));
					printer.println(r+"\t"+"LinHashV1_IO_unfavorableSearch(2^"+(k+l)+")"+"\t"+LinHashV1_IO_unfavorableSearch[k].getMean() +"\t"
							+LinHashV1_IO_unfavorableSearch[k].getStandardDeviation()+"\t"
							+(2*LinHashV1_IO_unfavorableSearch[k].getStandardDeviation()/(Math.sqrt(r)*LinHashV1_IO_unfavorableSearch[k].getMean())));
					printer.println(r+"\t"+"LinHashV2_IO_insert(2^"+(k+l)+")"+"\t"+LinHashV2_IO_unfavorableSearch[k].getMean() +"\t"
							+LinHashV2_IO_unfavorableSearch[k].getStandardDeviation()+"\t"
							+(2*LinHashV2_IO_unfavorableSearch[k].getStandardDeviation()/(Math.sqrt(r)*LinHashV2_IO_unfavorableSearch[k].getMean())));
				}
			}
			/* print to file */
		}
		printer.close();
	}
}

