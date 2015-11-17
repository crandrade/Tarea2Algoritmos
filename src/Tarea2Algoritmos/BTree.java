package Tarea2Algoritmos;

import java.io.IOException;
import java.util.LinkedList;

public class BTree implements DiskMemoryManager{
	
	private DiskSimulator dSimulator;
	private int diskPage;
	private LinkedList<BTree> hijos;
	private BTree padre;
	
	public BTree(DiskSimulator dSimulator, BTree padre) {
		this.dSimulator = dSimulator;
		this.diskPage = dSimulator.getNextFreePage();
		this.hijos = new LinkedList<>();
		this.padre = padre;
	}

	@Override
	public float getOccupation() {
		return dSimulator.getOccupation();
	}

	@Override
	public int getIOs() {
		return dSimulator.getIOs();
	}

	@Override
	public void resetIOs() {
		dSimulator.resetIOs();
	}
	
	public BTree findTreeForChain(String chain) {
		BTree toReturn = this;
		try {
			LinkedList<String> strings = Utilitarian.allStringsOnBytes(dSimulator.getPage(diskPage));
			int index = 0;
			for (String string : strings) {
				if (string.equals(chain))
					return this;
				if (string.compareTo(chain) > 0) {
					/* Entonces string ya esta mas adelante que el valor que busco */
					return hijos.get(index).findTreeForChain(chain);
				}
				index++;
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Error buscando tree for chain");
		}
		return toReturn;
	}

	@Override
	public String find(String chain) {
		try {
			BTree bTree = findTreeForChain(chain);
			return Utilitarian.searchChainInBytes(chain, dSimulator.getPage(bTree.diskPage));
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Error buscando el string");
		}
		return null;
	}

	@Override
	public boolean delete(String chain) {
		BTree bTree = findTreeForChain(chain);
		if (bTree.hijos.size() > 0) {
			// Estoy en un nodo interno
			// Busco un antesesor y reemplazo
			try {
				LinkedList<String> strings = null;
				int index = 0;
				do {
					strings = Utilitarian.allStringsOnBytes(dSimulator.getPage(bTree.diskPage));
					index = 0;
					for (String string : strings) {
						if (string.compareTo(chain) > 0) {
							/* Entonces string ya esta mas adelante que el valor que busco */
							bTree = bTree.hijos.getLast();
							break;
						}
						index++;
					}
				}
				while (bTree.hijos.size() > 0);
				String toReplace = strings.get(index);
				BTree mTree = findTreeForChain(chain);
				LinkedList<String> toBeReplaced = Utilitarian.allStringsOnBytes(dSimulator.getPage(mTree.diskPage));
				int indexToBeReplaced = toBeReplaced.indexOf(chain);
				toBeReplaced.add(indexToBeReplaced, toReplace);
				dSimulator.writePage(mTree.diskPage, Utilitarian.stringListToByte(toBeReplaced));
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("Error en busqueda BTree");
			}
		} else {
			// Estoy en un nodo externo
			try {
				LinkedList<String> strings = Utilitarian.allStringsOnBytes(dSimulator.getPage(bTree.diskPage));
				strings.remove(chain);
				// Si quede con menos de b/2 elementos debo buscar en mis vecinos
				
				// Si mis vecinos no tienen debo aunarme con un vecino y con el separador del padre
				
				// Verificar caso que quede con menos de b/2 elementos
				dSimulator.writePage(bTree.diskPage, Utilitarian.stringListToByte(strings));
				return true;
			} catch (IOException e) {
				System.out.println("Error al adaptar arbol");
			}
		}
		
		
		return false;
	}
	
	public void insert(String chain) throws IOException {
		boolean willOverflow = Utilitarian.willOverflow(dSimulator.getPage(diskPage), chain.getBytes());
		LinkedList<String> strings = Utilitarian.allStringsOnBytes(dSimulator.getPage(diskPage));
		if (!willOverflow) {
			int index = 0;
			for (String string : strings) {
				if (string.equals(chain)) {
					/* La clave que estoy insertando ya existe */
					return;
				}
				if (string.compareTo(chain) > 0) {
					/* Entonces string ya esta mas adelante que el valor que busco */
					break;
				}
				index++;
			}
			strings.add(index, chain);
			dSimulator.writePage(diskPage, Utilitarian.stringListToByte(strings));
		} else {
			// Sacar la mediana e insertarla en el padre
			int indexMediana = strings.size()/2;
			String mediana = strings.get(indexMediana);
			strings.remove(indexMediana);
			int index = 0;
			for (String string : strings) {
				if (string.equals(chain)) {
					/* La clave que estoy insertando ya existe */
					return;
				}
				if (string.compareTo(chain) > 0) {
					/* Entonces string ya esta mas adelante que el valor que busco */
					break;
				}
				index++;
			}
			strings.add(index, chain);
			dSimulator.writePage(diskPage, Utilitarian.stringListToByte(strings));
			if (padre != null) {
				// Debo insertar mediana en el padre
				padre.add(mediana);
			} else {
				// Debo agregar padre e insertar mediana
				padre = new BTree(dSimulator, null);
				padre.hijos.add(this);
				padre.add(chain);
			}
		}
	}

	@Override
	public void add(String chain) {
		BTree bTree = findTreeForChain(chain);
		bTree.add(chain);
			
	}

}
