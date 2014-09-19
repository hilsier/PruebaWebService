/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package WService;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Writer;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.imageio.ImageIO;
import sun.misc.PerformanceLogger;

/**
 *
 * @author Yarib
 */
public class FileAux {
    
    
    String path=System.getProperty("user.home") + "/Documents/NetbeansProjects/PruebaWebService/web/Imagenes";
    String ImageName=null;
    public byte[] decodeBase64(String base64){
    
    byte[] btDataFile = null;
        try {
          btDataFile = new sun.misc.BASE64Decoder().decodeBuffer(base64);
       } catch (IOException ex) {
        }
    
    return btDataFile;
    }
    
    
    public String CreateFile(String Nombre,String base64,String ext){
        
        String source=path+"/ZipFiles/"+Nombre+"."+ext;
        File file = new File(source);
        FileOutputStream fos = null;
        byte[] filebyte=this.decodeBase64(base64);
        try {
            fos = new FileOutputStream(file);
        } catch (FileNotFoundException ex) {
            System.out.println(ex.toString());
        }
        try {
            fos.write(filebyte);
            String res= source;
            fos.flush();
            fos.close();
            return res;
        } catch (IOException ex) {
            System.out.println(ex.toString());
            return "failded";
        }
    }// fin del metodo
    
    public String unzipfile(String zpath,String path) throws FileNotFoundException, IOException{
        String fname=null;String fpath=null;
        File destDir = new File(zpath);
        if (!destDir.exists()) {
            destDir.mkdir();
        }
        ZipInputStream zipIn = new ZipInputStream(new FileInputStream(zpath));
        ZipEntry entry = zipIn.getNextEntry();
        while (entry != null) {
            try {
                fname=entry.getName();
                ImageName=fname.substring(0, fname.length()-4);
                fpath = path+entry.getName();
                if (!entry.isDirectory()) {
                    extractFile(zipIn, fpath);
                } else {
                    // if the entry is a directory, make the directory
                    File dir = new File(fpath);
                    dir.mkdir();
                }
                zipIn.closeEntry();
                entry = zipIn.getNextEntry();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        zipIn.close();
        return fpath;
    }
    private final int BUFFER_SIZE=4096;
     private void extractFile(ZipInputStream zipIn, String filePath) throws IOException {
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));
        byte[] bytesIn = new byte[BUFFER_SIZE];
        int read = 0;
        while ((read = zipIn.read(bytesIn)) != -1) {
            bos.write(bytesIn, 0, read);
        }
        bos.close();
    }
     
    public String getpath(){
    return this.path;
    }
    
    public String getb64(String ruta){
        String res=null;
        System.err.println(ruta);
        try {
            res=Base64.encodeFromFile(ruta);
            
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return res;
    }
    
    
   public String jpgToPng(String JPGPath){
   
       
        File output;
        try {
            File input = new File(JPGPath);
            
//Read the file to a BufferedImage
            BufferedImage image = ImageIO.read(input);
            
            
            String ar[]=JPGPath.split("\\.");
            System.err.println(ar.length);
            
            String PNGPath=ar[0];
            
            
            output = new File(PNGPath+".png");
            
//Write the image to the destination as a PNG
            ImageIO.write(image, "png", output);
            
           
        } catch (IOException ex) {
           return ex.toString();
        }
        System.err.println(output.getAbsolutePath());
        System.err.println(output.getPath());
        
         return output.getPath();
   }
   
   public static byte[] createChecksum(String filename) throws Exception {
       InputStream fis =  new FileInputStream(filename);

       byte[] buffer = new byte[1024];
       MessageDigest complete = MessageDigest.getInstance("MD5");
       int numRead;

       do {
           numRead = fis.read(buffer);
           if (numRead > 0) {
               complete.update(buffer, 0, numRead);
           }
       } while (numRead != -1);

       fis.close();
       return complete.digest();
   }
   
   
   public static String getMD5Checksum(String filename) throws Exception {
       byte[] b = createChecksum(filename);
       String result = "";

       for (int i=0; i < b.length; i++) {
           result += Integer.toString( ( b[i] & 0xff ) + 0x100, 16).substring( 1 );
       }
       return result;
   }
   
  public String CreateQR(String message) throws WriterException, FileNotFoundException, IOException{
   BitMatrix bm;
             File dir = new File(System.getProperty("user.home")+"\\Documents\\NetBeansProjects\\PruebaWebService\\web\\Imagenes\\QR");
                  Writer writer = new QRCodeWriter();                                                 
            bm = writer.encode(message, BarcodeFormat.QR_CODE, 170, 170);
                    // Crear un buffer para escribir la imagen
             BufferedImage imagex = new BufferedImage(170, 170, BufferedImage.TYPE_INT_RGB);
                     for (int i = 0; i < 170; i++) {
                         for (int j = 0; j < 170; j++) {
                                int grayValue = (bm.get(j, i) ? 1 : 0) & 0xff;
                                imagex.setRGB(j, i, (grayValue == 0 ? 0 : 0xFFFFFF));
                            }
                         }
                        imagex = invertirColores(imagex);
                        FileOutputStream qrCode = new FileOutputStream(dir+"/fiber.jpg");                        
                        ImageIO.write(imagex, "jpg", qrCode);
                        qrCode.close();  
                        ImageIO.read(new File(dir+"/fiber.jpg"));
             if(!dir.exists())
                    {
                        dir.mkdirs();
                    }    
             
               return "todo bien"; 
           
        }
      

  

  
  
private BufferedImage invertirColores(BufferedImage imagen) {
        for (int i = 0; i < 170; i++) {
            for (int j = 0; j < 170; j++) {
                int rgb = imagen.getRGB(i, j);
                if (rgb == -16777216) {
                    imagen.setRGB(i, j, -1);
                } else {
                    imagen.setRGB(i, j, -16777216);
                }
            }
        }
        return imagen;       
}


    
}//fin de la calse
