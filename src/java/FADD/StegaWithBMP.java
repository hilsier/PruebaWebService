package FADD;

/**
 * Aplica la esteganografía para ocultar información cifrada en una imagen BMP.
 *
 * @author  Luis Enrique Cuevas Díaz
 * @author  Samara Anaid Montoya Anzueto.
 * @version 1.0, 24/08/10
 * @see     <a href="http://download.oracle.com/javase/6/docs/api/javax/swing/JFrame.html" title="Revisar documentación sobre JFrame" target="_self">JFrame</a>
 * @see     <a href="http://download-llnw.oracle.com/javase/6/docs/api/javax/swing/JComponent.html" title="Revisar documentación sobre JComponent" target="_self">JComponent</a>
 * @since   JDK1.6
 */

public class StegaWithBMP{
	private BMP image=null;
        private int longitud=0;

	/**
	 * Inicializa esta clase a partir de la imagen BMP especificada.
	 * @param imagen La imagen BMP que se usará.
	 */

	public StegaWithBMP(BMP imagen){
		this.image=imagen;
	}

	/**
	 * Método para ejecutar el proceso esteganográfico en una imagen BMP.
	 * @param texto Texto cifrado a ocultar.
	 * @param alg Algoritmo a utilizar.
	 */

	public void execStega(byte[] texto, String alg){
		if(alg!=null)
			hideInfo(texto);
		else
			System.out.println("No se especific\u00A3 un algoritmo");
	}

	private void hideInfo(byte[] texto){
		String mensaje=interpretarTexto(texto), bandera="";
		char temporal[]=null;
		int pixeles[][]=image.getMapaBits(), c=0;
		for (int j=0; j<image.getHeight() && c<mensaje.length(); j++){ //Filas
			for (int i=0; i<image.getWidth() && c<mensaje.length(); i++){ //Columnas
				temporal=(Integer.toBinaryString(pixeles[i][j])).toCharArray();
				for(int k=15; k<32 && c<mensaje.length(); k+=8, c++){ //Colores
					temporal[k]=mensaje.charAt(c);
				}
				bandera=new String(temporal);
				pixeles[i][j]=Integer.parseInt(bandera.substring(24, 32),2) | Integer.parseInt(bandera.substring(16, 24),2)<<8 | Integer.parseInt(bandera.substring(8, 16),2)<<16 | Integer.parseInt(bandera.substring(0, 8),2)<<24;
				temporal=null;
			}
		}
                longitud=mensaje.length(); //Para saber cuántos caracteres hay ocultos
		image.setMapaBits(pixeles);
	}

	/**
	 * Recupera la información oculta, mediante esteganografía, de una imagen BMP.
	 * @param img Imagen BMP para obtener el mapa de pixeles a interpretar.
	 * @param alg Algoritmo a utilizar.
	 * @return La información cifrada obtenida de la imagen BMP en formato byte.
	 */

	public static byte[] getInfo(BMP img,String alg){
		int pixeles[][]=img.getMapaBits(), c=0;
		byte[] mensaje=new byte[tamText(pixeles)+12];
		char temporal[]=null;
		String mensajeString="";
		for (int j=0; j<img.getHeight() && c<mensaje.length; j++){ //Filas
			for (int i=0; i<img.getWidth() && c<mensaje.length; i++){ //Columnas
				temporal=(Integer.toBinaryString(pixeles[i][j])).toCharArray();
				for(int k=15; k<32 && c<mensaje.length; k+=8, c++){ //Colores
					mensajeString+=temporal[k];
				}
				temporal=null;
			}
		}
		return infOculta(mensajeString);
	}

	/**
	 * Devuelve la imagen BMP contenida dentro de este objeto.
	 * @return La imagen BMP almacenada dentro del objeto, la cual es modificada al aplicar el método de esteganografía.
	 */

	public BMP getBMP(){
		return image;
	}

	/**
	 * Devuelve la imagen BMP resaltando con un color RGB qué pixeles se han modificado.
	 * @param rgb Número decimal del color RGB a utilizarse.
	 * @return La imagen BMP modificada, señalando qué pixeles han sido alterados.
	 */

	public static BMP getPixelsModified(int rgb, BMP img, String alg){
		if(rgb<0 || rgb>16777215) //No es un color RGB válido
			return null;

		int pixeles[][]=img.getMapaBits(), tamTotal=(int) Math.ceil((double)tamText(img.getMapaBits())/3D), c=0;
		for(int j=0; j<img.getHeight(); j++){ //Filas
			for (int i=0; i<img.getWidth(); i++){ //Columnas
				if(c<tamTotal){
					pixeles[i][j]=rgb | 0xFF<<24;
					c++;
				}else{
					pixeles[i][j]=-1;
				}
			}
		}

		return new BMP(new HeaderBMP(img.getHeader()), pixeles);
	}

	private String interpretarTexto(byte[] texto){
		String mensaje=new String();
		int ml=0; //Longitud del mensaje
		for(int i=0; i<texto.length; i++)
			mensaje+=completarLength(Integer.toBinaryString(texto[i]&0xFF), 8-Integer.toBinaryString(texto[i]&0xFF).length()); //Concatenación
		ml=mensaje.length();
		mensaje=completarLength(Integer.toBinaryString((int) Math.ceil((double)ml/8L)), (12-Integer.toBinaryString((int) Math.ceil((double)ml/8L)).length()))+mensaje;
		return mensaje;
	}

	private static byte[] infOculta(String oculto){
		oculto=oculto.substring(12);
		byte[] mensaje=new byte[oculto.length()/8];
		for(int i=0; i<oculto.length(); i+=8)
			mensaje[i/8]=(new Integer(Integer.parseInt(oculto.substring(i, i+8), 2))).byteValue();
		return mensaje;
	}

	private static int tamText(int pixeles[][]){
		char temporal[]=null;
		String mensajeString="";
		for (int j=0; j<1; j++){ //Filas
			for (int i=0; i<4; i++){ //Columnas
				temporal=(Integer.toBinaryString(pixeles[i][j])).toCharArray();
				for(int k=15; k<32; k+=8){ //Colores
					mensajeString+=temporal[k];
				}
				temporal=null;
			}
		}
		return Integer.parseInt(mensajeString,2)*8;
	}

	private String completarLength(String value, int size){
		String cad="";
		for(int i=0; i<size; i++)
			cad+="0";
		return cad+value;
	}
}

//Programación Orientada a Objetos
//Proyecto de Validación Web
//Desarrolladores:
//Luis Enrique Cuevas Díaz
//Samara Anaid Montoya Anzueto
//04-julio-2010