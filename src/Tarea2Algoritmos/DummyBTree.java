package Tarea2Algoritmos;


public class DummyBTree implements DiskMemoryManager{

	public DummyBTree(){}
	
	public DummyBTree(DiskSimulator dSimulator, DummyBTree padre){	}

	@Override
	public float getOccupation() {
		return 0;
	}

	@Override
	public int getIOs() {
		return 0;
	}

	@Override
	public void resetIOs() {}
	
	public DummyBTree findTreeForChain(String chain) {
		return null;
	}

	@Override
	public String find(String chain) {	
		return "";
	}

	@Override
	public boolean delete(String chain) {
		return false;
	}
	
	public void insert(String chain){}

	@Override
	public void add(String chain){}

}
