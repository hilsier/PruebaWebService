package FADD;

/**
 * Clase que cifra y descifra información a partir de cadenas a arreglos byte y viceversa.
 *
 * @author  Luis Enrique Cuevas Díaz
 * @author  Samara Anaid Montoya Anzueto.
 * @version 1.0, 24/08/10
 * @see     <a href="http://download.oracle.com/javase/6/docs/api/javax/swing/JFrame.html" title="Revisar documentación sobre JFrame" target="_self">JFrame</a>
 * @see     <a href="http://download-llnw.oracle.com/javase/6/docs/api/javax/swing/JComponent.html" title="Revisar documentación sobre JComponent" target="_self">JComponent</a>
 * @since   JDK1.6
 */

public class XOR implements Ciphering{

	/**
	 * Cifra una cadena en un arreglo byte.
	 * @param valor El texto a cifrar.
	 * @param cve La contraseña a usar.
	 * @param metodo El método a ejecutarse.
	 * @return El texto cifrado como un arreglo de bytes.
	 */

	public byte[] encripta(String valor, String cve, String metodo){ //Método para cifrar
		byte[] nValor=toByteArray(valor), nClave=toByteArray(invertirCadena(String.valueOf(cve.hashCode())));
		//System.out.println("Se utilizar\u00A0 " + metodo + " para cifrar.");
		int x=0;
		for(int i=0; i<nValor.length; i++){
			nValor[i]=(byte) (nValor[i] ^ nClave[x]);
			x=(x<(nClave.length-1)?x+1:0);
		}
		return nValor;
	}

	/**
	 * Decifra un arreglo byte en una cadena.
	 * @param arr El arreglo byte a descifrar.
	 * @param cve La contraseña a usar.
	 * @param metodo El método a ejecutarse.
	 * @return El texto decifrado.
	 */

	public String decripta(byte[] arr, String cve, String metodo){ //Método para descifrar
		String nValor="";
		byte[] nClave=toByteArray(invertirCadena(String.valueOf(cve.hashCode())));
		int x=0;
		for(int i=0; i<arr.length; i++){
			nValor+=(char) (arr[i] ^ nClave[x]);
			x=(x<(nClave.length-1)?x+1:0);
		}
		//System.out.println("Se utilizar\u00A0 " + metodo + " para decifrar.");
		return (toNewString(nValor));
	}

	private byte[] toByteArray(String valor){ //Método para convertir una cadena en un arreglo byte
		byte[] arreglo=new byte[valor.length()];
		String[] nValor=toStringArray(valor);
		for(int i=0; i<valor.length(); i++)
			arreglo[i]=Byte.decode(nValor[i]);
		return arreglo;
	}

	private String[] toStringArray(String valor){ //Método para convertir una cadena en un arreglo de cadenas
		String[] arreglo=new String[valor.length()];
		for(int i=0; i<valor.length(); i++)
			arreglo[i]=String.valueOf(((int) valor.charAt(i))-128);
		return arreglo;
	}

	private String toNewString(String valor){
		String nValor="";
		for(int i=0; i<valor.length(); i++)
			nValor+= (char) (valor.charAt(i) + 128); //Se le suma los 128 restados al pasarse a tipo byte
		return nValor;
	}

	private String invertirCadena(String cadena){ //Método que invierte el orden de una cadena
		String nuevaCadena="";
		for (int i=cadena.length()-1;i>=0;i--)
			nuevaCadena+=cadena.charAt(i);
		return nuevaCadena;
	}
}

//Programación Orientada a Objetos
//Proyecto de Validación Web
//Desarrolladores:
//Luis Enrique Cuevas Díaz
//Samara Anaid Montoya Anzueto
//04-julio-2010