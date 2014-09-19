/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package WService;

import FADD.AES;
import FADD.Ciphering;
import FADD.PNG;
import FADD.StegaWithPNG;
import FADD.Text_XML;
import FADD.XOR;
import com.google.zxing.WriterException;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;

/**
 *
 * @author Yarib
 */
@WebService(serviceName = "Prueba")
public class Prueba {
    
    String FileName;
    public String Firma(String bse64, String NameFile, String message) throws IOException, WriterException {
       
        
//        
//        System.err.println("la base 64:"+bse64);
//        System.err.println("el nombre de archivo:"+NameFile);
//        System.err.println("el mensaje:"+message);
//        
        
        
        String Password="123";
       FileAux fa=new FileAux();
       String zipfile=fa.CreateFile(NameFile,bse64,"zip");
       
      String imagen=fa.unzipfile(zipfile,fa.getpath()+"/Images/");
        System.err.println( getExt(imagen));
      
      switch(getExt(imagen)){
          
          case "jpg":
              System.err.println("archivo es jpg");
              imagen=fa.jpgToPng(imagen);
             
              break;
      
      
      }
      
      
      this.FileName=fa.ImageName;
       
       
       
       String rutafirmada=firmar(message,Password,imagen,"AES");
       String resultado=fa.getb64(rutafirmada);
       return resultado;
       //return imagen;
    }
    
    private String firmar(String info, String pass, String nomArch, String cifradoM){
        try {
            File archivoBMP = new File(nomArch);
            PNG img = new PNG(archivoBMP);
 
            Ciphering cifrador = null;
            int metCifrado = 0;
            if (cifradoM.equalsIgnoreCase("XOR")) {
                metCifrado = 1;
                cifrador = new XOR();
            } else if (cifradoM.equalsIgnoreCase("AES")) {
                metCifrado = 2;
                cifrador = new AES();
            } 
            byte[] mensajeCifrado = cifrador.encripta(info, pass, "XOR");
            
            //Proceso de BMP
            if (getExt(archivoBMP.getName()).equalsIgnoreCase("png")) {
                StegaWithPNG stega = new StegaWithPNG(img);
                System.out.println("Is valid: "+img.isValid());
                String ruta=System.getProperty("user.home") + "/Documents/NetbeansProjects/PruebaWebService/web/Imagenes/"+FileName+"Firmada.png";
                boolean execStega = stega.execStega(ruta, mensajeCifrado, "LSBs");
                System.out.println("execStega: "+execStega);
                if (execStega) {//img.savePNG(nFile, stega.getPNG().getChunks())
                    return ruta;
                } else {
                    return "No se pudo salvar la imagen";
                }
            }
        } catch (java.lang.Exception ex) {
            ex.printStackTrace();
            return "Error: al abrir "+nomArch;
        }
        return "Todo bien";
    }
    
    
     private String consultar(String pass, String nomArch, String cifradoM){
        try {
            File archivoBMP = new File(nomArch);
            PNG img = new PNG(archivoBMP);
            byte[] cifrado=new StegaWithPNG(img).getInfo(img, "XOR");
            Ciphering cifrador=null;
            int metCifrado=0;
            if(cifradoM.equalsIgnoreCase("XOR")){
                metCifrado=1; cifrador=new XOR();
            }else if(cifradoM.equalsIgnoreCase("AES")){
                metCifrado=2; cifrador=new AES();
            }else{
                //error
            }
            String mensaje_txt=" -Error- ";
            String mensaje_xml=" -Error- ";
            mensaje_xml=cifrador.decripta(cifrado, pass,"LSBs");
            mensaje_xml=mensaje_xml!=null?mensaje_xml:"";

            if(mensaje_xml.startsWith("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>")){
                Text_XML txtxml = new Text_XML();
                mensaje_txt = txtxml.toText(mensaje_xml, "\n");
                mensaje_txt = mensaje_txt!=null?mensaje_txt:"ERROR";
                return mensaje_txt;
            }
            return mensaje_xml;
        } catch (java.lang.Exception ex) {
            ex.printStackTrace();
            //Logger.getLogger(WSFADD.class.getName()).log(Level.SEVERE, null, ex);
            return "Error ocurrido: "+ex.getMessage();
        }
        
        //return "Final..";
    }
    
    private String getExt(String fileName) {
        if (fileName.length() < 0) {
            return null;
        }
        //Devolverá si el DD es de tipo BMP, PNG, etc...
        char[] nChar = new char[]{fileName.charAt(fileName.length() - 3), fileName.charAt(fileName.length() - 2), fileName.charAt(fileName.length() - 1)};
        return new String(nChar, 0, 3);
    }

        public String ConsultaFirma(String Password, String base64,String nameFile,String typeAlgo) {
        FileAux fa=new FileAux();
        byte []nte= fa.decodeBase64(base64);
        String pathImage=fa.CreateFile(nameFile,base64,"png");
        String resultado=consultar(Password,pathImage,"XOR");
        File fichero = new File(pathImage);
        if(fichero.delete()){System.out.println(pathImage+" Deleted");}
        else{System.out.println("Failed to delete "+pathImage);}
         return  resultado; 
    }

    /**
     * Web service operation
     * @param Message
     * @return 
     */
    public String CreateQR(String Message) {
        String result = "";
        try {
            FileAux fa=new FileAux();
            
                result =fa.CreateQR(Message);
                
            
        } catch (WriterException ex) {
            Logger.getLogger(Prueba.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Prueba.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }
    
}
