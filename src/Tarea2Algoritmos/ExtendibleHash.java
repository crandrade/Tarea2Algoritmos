package Tarea2Algoritmos;
import java.io.FileNotFoundException;
import java.io.IOException;

public class ExtendibleHash {
	
	private DiskSimulator dSimulator;
	private MyTree myTree;
	
	public ExtendibleHash() {
		try {
			dSimulator = new DiskSimulator();
		} catch (FileNotFoundException e) {
			System.out.println("Extendible Hash File Not found" +  e.toString());
		}
		myTree = new MyTree(0, 0);
	}
	
	private MyTree getMyTreeForChain(String chain) {
		int currIndex = 0;
		boolean[] hash = ADNHasher.hash(chain);
		MyTree pageTree = myTree;
		while (pageTree.hasSons) {
			if (hash[currIndex] == false)
				pageTree = pageTree.getLeft();
			else
				pageTree = pageTree.getRight();
		}
		return pageTree;
	}
	
	public String findChain(String chain) {
		int diskPage = getMyTreeForChain(chain).getDiskPage();
		try {
			byte[] pageBytes = dSimulator.getPage(diskPage);
			return Utilitarian.searchChainInBytes(chain, pageBytes);
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Problema IO Find!");
		}
		return null;
	}
	
	public void insertChain(String chain) {
		MyTree pageTree = getMyTreeForChain(chain);
		int diskPage = pageTree.getDiskPage();
		int deepness = pageTree.getDeepness();
		try {
			byte[] pageData = dSimulator.getPage(diskPage);
			byte[] chainBytes = chain.getBytes();
			boolean found = false;
			// Leo hasta encontrar 0 y ahi inserto;
			for (int i = 0; i < DiskSimulator.BLOCK_SIZE_BYTES - chainBytes.length; i++) {
				if (pageData[i] == 0) {
					for (int j = 0; j < chainBytes.length; j++) {
						pageData[i+j] = chainBytes[j];
					}
					found = true;
					break;
				}
			}
			if (found) {
				//System.out.println("Is no replacemtent");
				dSimulator.writePage(diskPage, pageData);
				//System.out.println("End no replacement");
				return;
			}
			System.out.println("Not succesfull");
			/* Se debe hacer el split de paginas */
			byte[] stringsPage0 = new byte[DiskSimulator.BLOCK_SIZE_BYTES];
			byte[] stringsPage1 = new byte[DiskSimulator.BLOCK_SIZE_BYTES];
			int pointer0 = 0;
			int pointer1 = 0;
			MyTree treeRight = new MyTree(deepness + 1, dSimulator.getNextFreePage());
			MyTree treeLeft = new MyTree(deepness + 1, dSimulator.getNextFreePage());
			pageTree.setRightLeft(treeRight, treeLeft);
			String pageDataString = new String(pageData);
			int counter = 0;
			while (counter < pageDataString.length() - Utilitarian.CHAIN_SIZE) {
				String toAdd = pageDataString.substring(counter, counter + Utilitarian.CHAIN_SIZE);
				boolean[] toAddBooleans = ADNHasher.hash(toAdd);
				if (toAddBooleans[deepness] == false) {
					for (int j = 0; j < chainBytes.length; j++) {
						stringsPage0[pointer0+j] = chainBytes[j];
					}
					pointer0 = pointer0 + chainBytes.length;
				} else {
					for (int j = 0; j < chainBytes.length; j++) {
						stringsPage1[pointer1+j] = chainBytes[j];
					}
					pointer1 = pointer1 + chainBytes.length;
				}
				// TODO y quizas mas 1
				counter = counter + Utilitarian.CHAIN_SIZE;
			}
			boolean[] mChainBooleans = ADNHasher.hash(chain);
			if (mChainBooleans[deepness] == false) {
				stringsPage0 = Utilitarian.concatBytes(stringsPage0, chain.getBytes());
			} else {
				stringsPage1 = Utilitarian.concatBytes(stringsPage1, chain.getBytes());
			}
			dSimulator.writePage(treeLeft.getDiskPage(), stringsPage0);
			dSimulator.writePage(treeRight.getDiskPage(), stringsPage1);
			
			
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Problema IO Insert!");
		}
	}
	
	public void deleteChain(String chain) {
		MyTree pageTree = getMyTreeForChain(chain);
		int diskPage = pageTree.getDiskPage();
		try {
			byte[] pageData = dSimulator.getPage(diskPage);
			byte[] newStringBytes = new byte[0];
			String pageDataString = new String(pageData);
			int counter = 0;
			while (counter < pageDataString.length()) {
				String toAdd = pageDataString.substring(counter, counter + Utilitarian.CHAIN_SIZE);
				if (!toAdd.equals(chain))
					newStringBytes = Utilitarian.concatBytes(newStringBytes, chain.getBytes());
				counter = counter + Utilitarian.CHAIN_SIZE + 1;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Problema IO delete");
		}
	}
	
	private class MyTree {
		
		private int deepness;
		private int diskPage;
		private MyTree left;
		private MyTree right;
		private boolean hasSons;
		
		public MyTree(int deepness, int diskPage) {
			this.deepness = deepness;
			this.diskPage = diskPage;
			this.hasSons = false;
		}
		
		public int getDeepness() {
			return deepness;
		}
		
		public int getDiskPage() {
			return diskPage;
		}
		
		public MyTree getLeft() {
			return left;
		}
		
		public MyTree getRight() {
			return right;
		}
		
		public void setRightLeft(MyTree right, MyTree left) {
			this.right = right;
			this.left = left;
			this.hasSons = true;
		}
	}
	
	
}
