package Tarea2Algoritmos;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;

public class DiskSimulator {

	public final static int BLOCK_SIZE_BYTES = 512;
	private static int namer = 0;
	private RandomAccessFile rFile;
	private String fileName;

	
	private int nextFreePage = 1;
	
	private int IOs = 0;
	
	public DiskSimulator() throws IOException {
		File file = new File("disk" + namer);
		Files.deleteIfExists(file.toPath());
		fileName = "disk" + namer;
		rFile = new RandomAccessFile(file, "rw");
		namer++;
	}
	
	public String getName() {
		return fileName;
	}
	
	public int getNextFreePage() {
		int aux = nextFreePage;
		nextFreePage++;
		return aux;
	}
	
	public byte[] getPage(int pageNumber) throws IOException {
		rFile.seek((long)pageNumber * BLOCK_SIZE_BYTES);
		byte[] toReturn = new byte[BLOCK_SIZE_BYTES];
		rFile.read(toReturn);
		IOs++;
		return toReturn;
	}
	
	
	public boolean writePage(int pageNumber, byte[] bytes) throws IOException {
		if (bytes.length > BLOCK_SIZE_BYTES)
			return false;
		rFile.seek((long) pageNumber * BLOCK_SIZE_BYTES);
		rFile.write(bytes);
		IOs++;
		return true;
	}
	
	public float getOccupation() {
		float ocupationPercentage = 0;
		for (int i = 0; i <= nextFreePage - 1; i++) {
			try {
				byte[] page = getPage(i);
				for (int j = BLOCK_SIZE_BYTES - 1; j >= 0; j--) {
					if (page[j] != 0) {
						ocupationPercentage = ocupationPercentage + ((float)j)/BLOCK_SIZE_BYTES;
						break;
					}
				}
			} catch (IOException e) {
				System.out.println("Fallamos");
				e.printStackTrace();
			}
		}
		System.out.println("Next free page?" + (nextFreePage - 1));
		return (ocupationPercentage/(nextFreePage-1));
	}
	
	public int getIOs() {
		return IOs;
	}
	
	public void resetIOs() {
		this.IOs = 0;
	}
	
	
	
}
