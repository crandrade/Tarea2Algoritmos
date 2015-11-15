package Tarea2Algoritmos;

public interface DiskMemmoryManager {

	/**
	 * Busca en la estructura de datos el String chain
	 * @param chain El string a buscar
	 * @return chain si lo encuentra, si no null
	 */
	public String find(String chain);
	/**
	 * Borra una cadena de la estructura. Retorna true si efectivamente fue borrado el elemento
	 * Retorna false si no encuentra la cadena
	 * @param chain
	 */
	public boolean delete(String chain);
	/**
	 * Agrega una cadena a la estrucura
	 * @param chain
	 */
	public void add(String chain);
}
