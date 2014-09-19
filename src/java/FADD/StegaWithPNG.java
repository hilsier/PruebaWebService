/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package FADD;

import java.io.File;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.awt.image.DataBufferByte;
import javax.imageio.ImageIO;

/**
 * <p>Aplica un proceso esteganográico para ocultar información cifrada en una imagen de tipo {@code PNG}.
 * Esta clase también se encarga de llevar a cabo el proceso inverso para recuperar la información oculta en una imagen.
 * La imagen con la se trabajará se recibe desde el constructor.</p>
 *
 * @author  Juan Carlos Lápez Pimentel
 * @author  Luis Enrique Cuevas Díaz
 * @author  Ramón Antonio Carrillo Gutiérrez.
 * @version 1.0, 24/08/11
 * @see     PNG
 * @see     StegaWithBMP
 * @since   JDK1.6
 */
public class StegaWithPNG{
    private PNG pngImage=null; //Imagen PNG
    
    /**
     * <p>Inicializa esta clase a partir del objecto de imagen {@code PNG} especificado.
     * La imagen {@code PNG} será utilizada como base para el proceso de esteganografía.</p>
     * 
     * @param imagen La imagen {@code PNG} que se usará para realizar el proceso de esteganografía.
     */
    public StegaWithPNG(PNG imagen){
        if(imagen.isValid())
            pngImage=imagen;
    }

    /*
     Encrypt an image with text, the output file will be of type .png
      
     @param path The path (folder) containing the image to modify
     @param original The name of the image to modify
     @param ext1 The extension type of the image to modify (jpg, png)
     @param stegan The output name of the file
     @param message The text to hide in the image
     @param type integer representing either basic or advanced encoding
     */
    //public boolean execStega(String path, String original, String ext1, String stegan, String message){
    /**
     * <p>Método para ocultar información a través de un proceso esteganográfico en una imagen {@code PNG}.</p>
     * 
     * @param nFile Archivo a utilizar
     * @param message Mensaje para ocultar
     * @return 
     */
    public boolean execStega(String nFile, byte[] msg, String alg){
        String message=new String(msg);
        BufferedImage imageOrig = getImage(pngImage.getFile());

        //El espacio de usuario no es necesario para cifrar
        BufferedImage image = userSpace(imageOrig);
        image = hideInfo(image,message);
        
        return setImage(image,new File(nFile),"png");
    }

    /*
     Decrypt assumes the image being used is of type .png, extracts the hidden text from an image
     @param path The path (folder) containing the image to extract the message from
     @param name The name of the image to extract the message from
     @param type integer representing either basic or advanced encoding
     */
    
    /**
     * <p>Extrae la información oculta de la imagen, asumiendo que es un archivo {@code PNG}.</p>
     * 
     * @param imagen Archivo de la imagen
     * @param alg Algoritmo a seguir para el proceso de esteganografía
     * @return 
     */
    public byte[] getInfo(PNG imagen, String alg){
        byte[] decode;
        try{
            //El espacio de usuario es necesario para descifrar
            BufferedImage image  = userSpace(getImage(pngImage.getFile()));
            decode = decodeText(getByteData(image));
            return decode;//infOculta(new String(decode));
        }catch(java.lang.Exception e){
            //No hay información oculta en esta imagen
            e.printStackTrace();
            System.out.println("Error (StegaWithPNG->getInfo) " + e.getMessage());
            return null;
        }
    }
    
    /**
     * <p>Método que regresa el archivo {@code PNG} con el que trabaja esta clase.
     * Después de aplicar el proceso esteganográfico con el método {@code execStega()},
     * la imagen devuelta contiene la información oculta que haya sido indicada.</p>
     * 
     * @return El archivo {@code PNG} con el que trabaja esta clase.
     * @see StegaWithPNG#execStega(java.lang.String, java.lang.String)
     */
    public PNG getPNG(){
        return pngImage;
    }
    
    /**
     * Método "getter" que regresa un archivo de imagen
     * 
     * @param archivo La ruta y el nombre completos de la imagen
     * @return Un BufferredImage de la ruta del archivo suministrado
     * @see StegaWithBMP#imagePath(String, String, String)
     */
    public BufferedImage getImage(File archivo){
        BufferedImage image = null;
        try{
            image = ImageIO.read(archivo);
        }catch(java.lang.Exception ex){
            //La imagen no pudo ser leída
            System.out.println("Error " + ex.getMessage());
            return null;
        }
        return image;
    }
    
    /**
     * Método "setter" para guardar un archivo de imagen
     * 
     * @param image El archivo de imagen a ser guardado
     * @param file El archivo para guardar la imagen
     * @param ext La extensión y, por tanto, el formato del archivo a ser guardado
     * @return Regresa verdadero si el guardado es exitoso
     */
    public boolean setImage(BufferedImage image, File file, String ext){
        try{
            file.delete(); //Borrar las fuentes usadas por el archivo
            ImageIO.write(image,ext,file);
            return true;
        }catch(java.lang.Exception e){
            //El archivo no pudo ser guardado
            System.out.println("Error: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Controla la insersión de texto en una imagen.
     * 
     * @param image La imagen donde se va a agregar texto oculto.
     * @param text El texto a ocultar en la imagen.
     * @return Regresa la imagen con el texto embebido en ella.
     */
    public BufferedImage hideInfo(BufferedImage image, String text){
        //Convierte todo los objetos a arreglos byte: image, message, message length
        byte img[] = getByteData(image);
        byte msg[] = text.getBytes();
        byte len[] = bitConversion(msg.length); /**/
        try{
            encodeText(img, len, 0); //Primer posicionamiento 0
            encodeText(img, msg, 32); //4 bytes de espacio para la longitud: 4bytes*8bit = 32 bits
        }catch(java.lang.Exception e){
            //El archivo de salida no puede soportar el mensaje
            System.out.println("Error " + e.getMessage());
            return null;
        }
        return image;
    }
    
    /**
     * Crea la versión del usuario sobre el espacio de un objeto BufferedImage, 
     * para editar y guardar bytes.
     * 
     * @param image La imagen a poner dentro del espacio del usuario, quita las interferencias de compresión.
     * @return La versión del usuario sobre el espacio de la imagen proporcionada.
     */
    public BufferedImage userSpace(BufferedImage image){
        //Crea newImg con los atributos de la imagen
        BufferedImage newImg  = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
        Graphics2D graphics = newImg.createGraphics();
        graphics.drawRenderedImage(image, null);
        graphics.dispose(); //Libera toda la memoria asignada para esta imagen
        return newImg;
    }
    
    /**
     * Obtiene el arreglo de bytes de una imagen.
     * 
     * @param image La imagen de la cual se va a extraer la información.
     * @return Regresa el arreglo de bytes de la imagen proporcionada.
     * @see java.awt.image.BufferedImage
     * @see java.awt.image.WritableRaster
     * @see java.awt.image.DataBufferByte
     */
    private byte[] getByteData(BufferedImage image){
        WritableRaster raster = image.getRaster();
        DataBufferByte buffer = (DataBufferByte)raster.getDataBuffer();
        return buffer.getData();
    }
    
    /**
     * Genera el formato byte adecuado de un entero
     * 
     * @param i El entero a convertir.
     * @return Regresa un arreglo de 4 bytes que representa al entero proporcionado.
     */
    private byte[] bitConversion(int i){
        //originally integers (ints) cast into bytes
        //byte byte7 = (byte)((i & 0xFF00000000000000L) >>> 56);
        //byte byte6 = (byte)((i & 0x00FF000000000000L) >>> 48);
        //byte byte5 = (byte)((i & 0x0000FF0000000000L) >>> 40);
        //byte byte4 = (byte)((i & 0x000000FF00000000L) >>> 32);

        //Sólo usando 4 bytes
        byte byte3 = (byte)((i & 0xFF000000) >>> 24); //0
        byte byte2 = (byte)((i & 0x00FF0000) >>> 16); //0
        byte byte1 = (byte)((i & 0x0000FF00) >>> 8 ); //0
        byte byte0 = (byte)((i & 0x000000FF));
        //{0,0,0,byte0} es equivalente, ya que todo desplazamiento >=8 será 0
        return new byte[]{byte3,byte2,byte1,byte0};
    }
    
    /**
     * Codifica un arreglo de bytes en otro arreglo de bytes en la compensación dada.
     * 
     * @param image Arreglo de datos representando una imagen.
     * @param addition El arreglo de información a agregar en el arreglo de datos de la imagen.
     * @param offset La compensación en el arreglo de la imagen para agregar la información adicional.
     * @return Regresa el arreglo fusionado de datos de la imagen y la información adicional.
     * @throws IllegalArgumentException 
     */
    public byte[] encodeText(byte[] image, byte[] addition, int offset) throws IllegalArgumentException{
        /*LECD: Corregir (idioma)*/
        //Verifica que data + offset pueda introducirse en la imagem
        if(addition.length + offset > image.length)
            throw new IllegalArgumentException("El archivo no es lo suficientemente grande");
        
        //loop through each addition byte
        for(int i=0; i<addition.length; ++i){
            //loop through the 8 bits of each byte
            int add = (int) addition[i];
            for(int bit=7; bit>=0; --bit, offset++){ //ensure the new offset value carries on through both loops
                //assign an integer to b, shifted by bit spaces AND 1
                //a single bit of the current byte
                int b = (add >>> bit) & 0x1;
                //assign the bit by taking: [(previous byte value) AND 0xfe] OR bit to add
                //changes the last bit of the byte in the image to be the bit of addition
                image[offset] = (byte)((image[offset] & 0xFE) | b );
            }
        }
        return image;
    }
    
    /**
     * Recupera texto oculto de una imagen.
     * 
     * @param image Arreglo de datos representando una imagen.
     * @return Un arreglo de datos que contiene el texto oculto.
     */
    public byte[] decodeText(byte[] image){
        int length = 0;
        int offset = 32;
        //loop through 32 bytes of data to determine text length
        for(int i=0; i<32; ++i){ //i=24 will also work, as only the 4th byte contains real data
            length = (length << 1) | (image[i] & 1);
        }

        System.out.println(length+" --- "+Integer.toBinaryString(length));
        byte[] result = new byte[length];

        //loop through each byte of text
        for(int b=0; b<result.length; ++b ){
            //loop through each bit within a byte of text
            for(int i=0; i<8; ++i, ++offset){
                //assign bit: [(new byte value) << 1] OR [(text byte) AND 1]
                result[b] = (byte)((result[b] << 1) | (image[offset] & 1));
            }
        }
        return result;
    }

    private byte[] infOculta(String oculto) {
        byte[] mensaje=new byte[oculto.length()/8];
        for(int i=0; i<oculto.length(); i+=8)
            mensaje[i/8]=(new Integer(Integer.parseInt(oculto.substring(i, i+8), 2))).byteValue();
        return mensaje;
    }
}