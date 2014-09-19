package FADD;

import java.io.RandomAccessFile;
import java.io.File;
import java.io.IOException;

/**
 * Lee e interpreta los datos de la cabecera de una imagen BMP.
 *
 * @author  Luis Enrique Cuevas Díaz
 * @author  Samara Anaid Montoya Anzueto
 * @version 1.0, 24/08/10
 * @see     BMP
 * @since   JDK1.6
 */

public class HeaderBMP{
	private byte[] header=new byte[54];
	private boolean bm=false;
	private int size=0;
	private int width=0;
	private int height=0;
	private int nplanes=0;
	private int nbitcount=0;
	private int ncompression=0;
	private int nsizeimage=0;
	private int nxpm=0;
	private int nypm=0;
	private int nclrused=0;
	private int nclrimp=0;

	/**
	 * Inicializa un objeto de la clase HeaderBMP a partir del archivo especificado por bitmap.
	 * @param bitmap El archivo para inicializar las propiedades del objeto HeaderBMP.
	 */

	public HeaderBMP(File bitmap){
		setDatos(bitmap);
	}

	/**
	 * Inicializa un objeto de la clase HeaderBMP a partir del arreglo de bytes especificado por bitmap.
	 * @param bitmap El arreglo bytes para inicializar las propiedades del objeto HeaderBMP.
	 */

	public HeaderBMP(byte[] bitmap){
		setDatos(bitmap);
	}

	private void setDatos(File bitmap){
		try{
			RandomAccessFile archivo=new RandomAccessFile(bitmap, "r");
			byte[] nheader=new byte[54];
			archivo.read(nheader);
			setDatos(nheader);
			archivo.close();
		}catch(IOException e){System.out.println("Error al abrir la imagen.");}
	}

	private void setDatos(byte[] bitmap){
		header=bitmap;

		//Tipo de fichero
		bm=(char)header[0]=='B' & (char)header[1]=='M';

		//Tamaño del archivo
		size=(((int)header[5]&0xff)<<24)
		| (((int)header[4]&0xff)<<16)
		| (((int)header[3]&0xff)<<8)
		| (int)header[2]&0xff;

		//Ancho en px
		width=(((int)header[21]&0xff)<<24)
		| (((int)header[20]&0xff)<<16)
		| (((int)header[19]&0xff)<<8)
		| (int)header[18]&0xff;

		//Alto en px
		height=(((int)header[25]&0xff)<<24)
		| (((int)header[24]&0xff)<<16)
		| (((int)header[23]&0xff)<<8)
		| (int)header[22]&0xff;

		//Número de planos
		nplanes=(((int)header[27]&0xff)<<8) | (int)header[26]&0xff;

		//Tamaño de cada punto
		nbitcount=(((int)header[29]&0xff)<<8) | (int)header[28]&0xff;

		//Compresión (0 = no comprimido)
		ncompression=(((int)header[33])<<24)
		| (((int)header[32])<<16)
		| (((int)header[31])<<8)
		| (int)header[30];

		//Tamaño de la imagen
		nsizeimage=(((int)header[37]&0xff)<<24)
		| (((int)header[36]&0xff)<<16)
		| (((int)header[35]&0xff)<<8)
		| (int)header[34]&0xff;

		//Resolución horizontal
		nxpm=(((int)header[41]&0xff)<<24)
		| (((int)header[40]&0xff)<<16)
		| (((int)header[39]&0xff)<<8)
		| (int)header[38]&0xff;

		//Resolución vertical
		nypm=(((int)header[45]&0xff)<<24)
		| (((int)header[44]&0xff)<<16)
		| (((int)header[43]&0xff)<<8)
		| (int)header[42]&0xff;

		//Tamaño de la tabla de color
		nclrused=(((int)header[49]&0xff)<<24)
		| (((int)header[48]&0xff)<<16)
		| (((int)header[47]&0xff)<<8)
		| (int)header[46]&0xff;

		//Contador de colores importantes
		nclrimp=(((int)header[53]&0xff)<<24)
		| (((int)header[52]&0xff)<<16)
		| (((int)header[51]&0xff)<<8)
		| (int)header[50]&0xff;
	}

	/**
	 * Indica si la cabecera del archivo corresponde a una imagen BMP o no.
	 * @return Verdadero o falso si la imagen es o no una imagen BMP.
	 */

	public boolean isBMP(){
		return bm;
	}

	/**
	 * Devuelve la cabecera de la imagen BMP como un objeto HeaderBMP.
	 * @return Un objeto HeaderBMP con los datos de la cabecera de la imagen BMP que se usó para inicializarla.
	 */

	public HeaderBMP getBMPHeader(){
		return this;
	}

	/**
	 * Devuelve la cabecera de la imagen BMP como un arreglo de bytes.
	 * @return El arreglo de bytes que representa esta cabecera.
	 */

	public byte[] getHeader(){
		return header;
	}

	/**
	 * Devuelve el tamaño especificado de la imagen BMP.
	 * @return El tamaño de la imagen BMP.
	 */

	public int getSize(){
		return size;
	}

	/**
	 * Devuelve el ancho especificado de la imagen BMP.
	 * @return El ancho de la imagen BMP.
	 */

	public int getWidth(){
		return width;
	}

	/**
	 * Devuelve la altura especificada de la imagen BMP.
	 * @return La altura de la imagen BMP.
	 */

	public int getHeight(){
		return height;
	}

	public int getPlanes(){
		return nplanes;
	}

	public int getBitCount(){
		return nbitcount;
	}

	public int getCompression(){
		return ncompression;
	}

	public int getSizeImage(){
		return nsizeimage;
	}

	public int getResX(){
		return nxpm;
	}

	public int getResY(){
		return nypm;
	}

	public int getColorUsed(){
		return nclrused;
	}

	public int getColorImportant(){
		return nclrimp;
	}
}

//Programación Orientada a Objetos
//Proyecto de Validación Web
//Desarrolladores:
//Luis Enrique Cuevas Díaz
//Samara Anaid Montoya Anzueto
//10-julio-2010