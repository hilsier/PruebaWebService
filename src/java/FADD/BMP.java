package FADD;

import java.awt.Button;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.awt.image.MemoryImageSource;
import java.awt.image.PixelGrabber;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * import java.awt.*; import java.awt.image.*; import java.io.*; import
 * java.util.*; import java.util.Vector.*;
 */
/**
 * Clase que guarda, manipula e interpreta datos para la creación de imágenes
 * BMP.
 *
 * @author Juan Carlos López Pimentel
 * @author Luis Enrique Cuevas Díaz
 * @author Samara Anaid Montoya Anzueto
 * @version 1.0, 24/08/10
 * @since JDK1.6
 */
public class BMP extends HeaderBMP {

    private byte[] header = new byte[54];
    private boolean bm = false;
    private int nsize = 0;
    private int nwidth = 0;
    private int nheight = 0;
    private int nplanes = 0;
    private int nbitcount = 0;
    private int ncompression = 0;
    private int nsizeimage = 0;
    private int nxpm = 0;
    private int nypm = 0;
    private int nclrused = 0;
    private int nclrimp = 0;
    //private Image img=null;
    private File source = null;
    private int[][] pixeles = null;
    private FileOutputStream fo;

    /**
     * Inicializa un objeto de la clase BMP a partir del archivo especicado por
     * filebmp.
     *
     * @param filebmp El archivo para inicializar las propiedades del objeto
     * BMP.
     */
    public BMP(File filebmp) {
        super(filebmp);
        header = getHeader();
        if (isBMP()) { //Comprueba si los 2 primeros bytes indican "BM"
            source = filebmp;
            nsize = getSize();
            nwidth = getWidth();
            nheight = getHeight();
            nplanes = getPlanes();
            nbitcount = getBitCount();
            ncompression = getCompression();
            nsizeimage = getSizeImage();
            nxpm = getResX();
            nypm = getResY();
            nclrused = getColorUsed();
            nclrimp = getColorImportant();
            setMapaBits(filebmp, source.getAbsolutePath());
        }
    }

    /**
     * Inicializa un objeto de la clase BMP a partir de la cabecera y el mapa de
     * bits especificados.
     *
     * @param cabecera La cabecera de 54 bytes de la imagen BMP.
     * @param mapaDeBits La matriz que indica el color de cada pixel.
     */
    public BMP(HeaderBMP cabecera, int[][] mapaDeBits) {
        super(cabecera.getHeader());
        pixeles = mapaDeBits;
    }

    /**
     * Escribe la información de fileBMP en el mapa de pixeles de la imagen BMP.
     *
     * @param fileBMP La matriz de pixeles.
     */
    public void setMapaBits(int[][] fileBMP) {
        this.pixeles = fileBMP;
    }

    /**
     * Devuelve la información del mapa de pixeles de la imagen BMP.
     *
     * @return La matriz de pixeles de la imagen BMP.
     */
    public int[][] getMapaBits() {
        return pixeles;
    }

    /**
     * Guarda la imagen BMP de acuerdo al archivo especificado.
     *
     * @param f El archivo a utilizarse.
     * @return Verdadero o falso si el archivo se guarda o no correctamente.
     */
    public boolean saveBMP(File f) {
        try {
            int[][][] img = newBMP(this.pixeles, getHeight(), getWidth());
            saveImage(img, f.getPath());
            f = null;
            return true;
        } catch (java.lang.Exception ex) {
            Logger.getLogger(BMP.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    /**
     * Guarda la imagen BMP de acuerdo al archivo, cabecera y matriz de pixeles
     * especificados.
     *
     * @param f El archivo a utilizarse.
     * @param nHeader La cabecera de 54 bytes de la imagen BMP.
     * @param nPixeles La matriz de pixeles.
     * @return Verdadero o falso si el archivo se guarda o no correctamente.
     */
    public boolean saveBMP(File f, HeaderBMP nHeader, int[][] nPixeles) {
        try {
            int[][][] img = newBMP(nPixeles, nHeader.getHeight(), nHeader.getWidth());
            saveImage(img, f.getPath());
            return true;
        } catch (java.lang.Exception e) {
            return false;
        }
    }

    /**
     * Guarda la imagen BMP de acuerdo al archivo e imagen BMP especificados.
     *
     * @param f El archivo donde va a guardarse la nueva imagen BMP.
     * @param bmp La imagen BMP que va a ser guardada.
     * @return Verdadero o falso si el archivo se guarda o no correctamente.
     */
    public boolean saveBMP(File f, BMP bmp) {
        try {
            /*
            this.pixeles=bmp.getMapaBits();
            this.header=bmp.getHeader();
            */
            int[][][] img = newBMP(bmp.getMapaBits(), bmp.getHeight(), bmp.getWidth());
            saveImage(img, f.getPath());
            return true;
        } catch (java.lang.Exception ex) {
            Logger.getLogger(BMP.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    //Interpreta el archivo para adquirir el mapa de pixeles
    private void setMapaBits(File filebmp, String nombreBMP) {
        int npad = (nsizeimage / nheight) - nwidth * 3;

        if (npad == 4) { //Correcciones de un "bug"
            npad = 0;
        }

        byte brgb[] = new byte[(nwidth + npad) * 3 * nheight];
        pixeles = new int[nwidth][nheight];
        try {
            FileInputStream archivo = new FileInputStream(filebmp);
            archivo.read(new byte[54], 0, 54);
            archivo.read(brgb, 0, ((nwidth + npad) * 3 * nheight));
            int nindex = 0;
            for (int j = 0; j < nheight; j++) {
                for (int i = 0; i < nwidth; i++) {
                    pixeles[i][j] = (255 & 0xff) << 24
                            | (((int) brgb[nindex + 2] & 0xff) << 16)
                            | (((int) brgb[nindex + 1] & 0xff) << 8)
                            | (int) brgb[nindex] & 0xff;
                    nindex += 3;
                }
                nindex += npad;
            }
            crearImg(nombreBMP, brgb);
            archivo.close();
        } catch (IOException ex) {
            pixeles = null;
            Logger.getLogger(BMP.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private int[][][] newBMP(int[][] px, int height, int width) {
        Image img = null;
        int npad = (nsizeimage / height) - width * 3;
        if (npad == 4) { //Correcciones de un "bug"
            npad = 0;
        }
        int ndata[] = new int[height * width];
        int nindex = 0;
        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {
                ndata[width * (height - j - 1) + i] = px[i][j];
                nindex += 3;
            }
            nindex += npad;
        }
        img = Toolkit.getDefaultToolkit().createImage(new MemoryImageSource(width, height, ndata, 0, width));
        if (img != null) {
            try {

                /*
                 * Hay que asegurarse de que la imagen se haya leído por
                 * completo Se crea un botón que se usa como parámetro para el
                 * constructor de MediaTracker
                 */
                MediaTracker tracker = new MediaTracker(new Button());
                tracker.addImage(img, 0);
                tracker.waitForID(0);
                // Se crea un "observador", que nos permite usar "getWidth" y "getHeight"
                iObserver observer = new iObserver();
                int bWidth = img.getWidth(observer);
                int bHeight = img.getHeight(observer);
                if (bWidth == -1 || bHeight == -1) { //Imagen no leída
                    img = (Image) null;
                }
            } catch (InterruptedException ex) {
                Logger.getLogger(BMP.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            System.out.println("No se puede leer una imagen del archivo " + source.getName());
            System.out.println("Aseg\u00A3rate de estar escribiendo el nombre de una imagen, \ne incluir la extensi\u00A2n bmp, gif, jpg o jpeg.");
        }

        // Interpretación de la imagen a un arreglo 3D llamado "imagePixels".
        int[][][] imagePixels = getImagePixels(img);
        return imagePixels;
    }

    private int[][][] getImagePixels(Image img) { //Se obtiene el mapa de pixeles
        iObserver observer = new iObserver();
        int width1 = img.getWidth(observer);
        int height1 = img.getHeight(observer);
        int[] rawPixels = getPixels(img, width1, height1);
        int[][] rgbPixels = new int[rawPixels.length][4]; //Cada pixel tiene una representación de 32 bits.
        for (int j = 0; j < rawPixels.length; j++) {
            rgbPixels[j][0] = ((rawPixels[j] >> 16) & 0xff);
            rgbPixels[j][1] = ((rawPixels[j] >> 8) & 0xff);
            rgbPixels[j][2] = (rawPixels[j] & 0xff);
            rgbPixels[j][3] = ((rawPixels[j] >> 24) & 0xff);
        }

        int[][][] imagePixels = new int[height1][width1][4];
        int index = 0;
        for (int row = 0; row < imagePixels.length; row++) {
            for (int col = 0; col < imagePixels[0].length; col++) {
                for (int rgbo = 0; rgbo < 4; rgbo++) {
                    imagePixels[row][col][rgbo] = rgbPixels[index][rgbo];
                }
                index++;
            }
        }
        return imagePixels;
    }

    private int[] getPixels(Image parImage, int parWidth, int parHeight) { //Proceso interno para la obtención del mapa de pixeles
        int[] bitmap = new int[parWidth * parHeight];
        PixelGrabber pg = new PixelGrabber(parImage, 0, 0, parWidth, parHeight, bitmap, 0, parWidth);
        try {
            pg.grabPixels();
        } catch (InterruptedException ex) {
            Logger.getLogger(BMP.class.getName()).log(Level.SEVERE, null, ex);
        }
        return bitmap;
    }

    private void saveBitmap(String parFilename, int[] imagePix, int parWidth, int parHeight) {
        try {
            //Guardar el mapa de pixeles
            int biSizeImage = 0x030000;
            int bfSize = 0;
            int biWidth = 0;
            int biHeight = 0;
            int bitmap[];
            int size;
            int value;
            int j;
            int i;
            int rowCount;
            int rowIndex;
            int lastRowIndex;
            int pad;
            int padCount;
            fo = new FileOutputStream(parFilename);

            bitmap = imagePix;
            pad = (4 - ((parWidth * 3) % 4)) * parHeight;
            if (4 - ((parWidth * 3) % 4) == 4) {
                pad = 0;
            }
            biSizeImage = ((parWidth * parHeight) * 3) + pad;
            bfSize = biSizeImage + 54;
            biWidth = parWidth;
            biHeight = parHeight;

            fo.write(getHeader());

            byte rgb[] = new byte[3];
            size = (biWidth * biHeight) - 1;
            pad = 4 - ((biWidth * 3) % 4);
            if (pad == 4) //Corrección de bug
            {
                pad = 0;
            }
            rowCount = 1;
            padCount = 0;
            rowIndex = size - biWidth;
            lastRowIndex = rowIndex;

            for (j = 0; j < size + 1; j++) {
                if (j < biWidth) {
                    value = bitmap[rowIndex + 1];
                } else {
                    value = bitmap[rowIndex];
                }
                rgb[0] = (byte) (value & 0xFF);
                rgb[1] = (byte) ((value >> 8) & 0xFF);
                rgb[2] = (byte) ((value >> 16) & 0xFF);
                fo.write(rgb);
                if (rowCount == biWidth) {
                    padCount += pad;
                    for (i = 1; i <= pad; i++) {
                        fo.write(0x00);
                    }
                    rowCount = 1;
                    rowIndex = lastRowIndex - biWidth;
                    lastRowIndex = rowIndex;
                } else {
                    rowCount++;
                }
                rowIndex++;
            }
            bfSize += padCount - pad;
            biSizeImage += padCount - pad;

            fo.close();
        } catch (java.lang.Exception ex) {
            Logger.getLogger(BMP.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void saveImage(int[][][] imagePixels, String name) { //Se guarda la imagen
        int nHeight = imagePixels.length;
        int nWidth = imagePixels[0].length;
        int[][] flat = new int[nWidth * nHeight][4];

        //Convertir la imagen en un arreglo 2D
        int index = 0;
        for (int row = 0; row < nHeight; row++) {
            for (int col = 0; col < nWidth; col++) {
                for (int rgbo = 0; rgbo < 4; rgbo++) {
                    flat[index][rgbo] = imagePixels[row][col][rgbo];
                }
                index++;
            } //Para columnas
        }//Para filas
        // Se conbinan los valores de 8 bits RGBO en "words" de 32 bits.
        int[] outPixels = new int[flat.length];
        for (int j = 0; j < flat.length; j++) {
            outPixels[j] = ((flat[j][0] & 0xff) << 16)
                    | ((flat[j][1] & 0xff) << 8)
                    | (flat[j][2] & 0xff)
                    | ((flat[j][3] & 0xff) << 24);
        }
        saveBitmap(name, outPixels, nwidth, nheight);
        //nName=name.split(File.separator);
        //System.out.println(nName.length);
        System.out.println(name.substring(name.lastIndexOf(File.separator) + 1) + " se ha guardado exitosamente."); //Comprobaci�n en l�nea de comandos
    }

    private void crearImg(String name, byte[] content) { //Creación de la imagen BMP con su cabecera y mapa de pixeles
        FileOutputStream archivo = null;
        try {
            archivo = new FileOutputStream(name);
            archivo.write(header);
            archivo.write(content);
        } catch (IOException ex) {
            Logger.getLogger(BMP.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                archivo.close();
            } catch (IOException ex) {
                Logger.getLogger(BMP.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
//Programación Orientada a Objetos
//Proyecto de Validación Web
//Desarrolladores:
//Luis Enrique Cuevas Díaz
//Samara Anaid Montoya Anzueto