package FADD;

/**
 *
 * @author LECD
 */
public interface Ciphering{
	/**
	 * Cifra una cadena en un arreglo byte.
	 * @param valor El texto a cifrar.
	 * @param cve La contraseña a usar.
	 * @param metodo El método a ejecutarse.
	 * @return El texto cifrado como un arreglo de bytes.
	 */

	public byte[] encripta(String valor, String cve, String metodo);

	/**
	 * Decifra un arreglo byte en una cadena.
	 * @param arr El arreglo byte a descifrar.
	 * @param cve La contraseña a usar.
	 * @param metodo El método a ejecutarse.
	 * @return El texto decifrado.
	 */

	public String decripta(byte[] arr, String cve, String metodo);
}

//LECD 27/abril/2011