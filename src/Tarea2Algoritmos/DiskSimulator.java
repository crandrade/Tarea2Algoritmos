package Tarea2Algoritmos;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class DiskSimulator {

	public final static int BLOCK_SIZE_BYTES = 512;
	private static int namer = 0;
	private RandomAccessFile rFile;
	private String fileName;
	
	private int nextFreePage = 1;
	
	public DiskSimulator() throws FileNotFoundException {
		File file = new File("disk" + namer);
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
		return toReturn;
	}
	
	
	public boolean writePage(int pageNumber, byte[] bytes) throws IOException {
		System.out.println("Size bytes" + bytes.length);
		if (bytes.length > BLOCK_SIZE_BYTES)
			return false;
		rFile.seek((long) pageNumber * BLOCK_SIZE_BYTES);
		rFile.write(bytes);
		return true;
	}
	
	
	
}
