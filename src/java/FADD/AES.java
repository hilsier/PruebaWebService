package FADD;

import java.security.InvalidKeyException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.MessageDigest;

/**
 *
 * @author RACG
 */
public class AES implements Ciphering{
	//Última versión probada por Ramón
    
    public byte[] encripta(String valor, String cve, String metodo){
		SecretKeySpec skeySpec = null;
        byte[] result = valor.getBytes();
        //byte[] result = toByteArray(valor);
        byte[] encrypted = null;
        Cipher cipher = null;
        try{
			cve=halfCode(md5(cve));
            cipher = Cipher.getInstance("AES");
            skeySpec = createKey(cve);
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
            encrypted = cipher.doFinal(result);
        }catch(NoSuchAlgorithmException ex){
            Logger.getLogger(AES.class.getName()).log(Level.SEVERE, null, ex);
        }catch(NoSuchPaddingException ex){
            Logger.getLogger(AES.class.getName()).log(Level.SEVERE, null, ex);
        }catch(InvalidKeyException ex){
            Logger.getLogger(AES.class.getName()).log(Level.SEVERE, null, ex);
        }catch(IllegalBlockSizeException ex){
            Logger.getLogger(AES.class.getName()).log(Level.SEVERE, null, ex);
        }catch(BadPaddingException ex){
            Logger.getLogger(AES.class.getName()).log(Level.SEVERE, null, ex);
        }
        return encrypted;
    }
    
    public String decripta(byte[] arr, String cve, String metodo){
		Cipher cipher=null;
        SecretKeySpec skeySpec = null;
        byte[] original = null;
        try{
			cve=halfCode(md5(cve));
            cipher = Cipher.getInstance("AES");
            skeySpec = createKey(cve);
            cipher.init(Cipher.DECRYPT_MODE, skeySpec);
            original = cipher.doFinal(arr);
        }catch(NoSuchAlgorithmException ex){
            Logger.getLogger(AES.class.getName()).log(Level.SEVERE, null, ex);
        }catch(NoSuchPaddingException ex){
            Logger.getLogger(AES.class.getName()).log(Level.SEVERE, null, ex);
        }catch(InvalidKeyException ex){
            Logger.getLogger(AES.class.getName()).log(Level.SEVERE, null, ex);
        }catch(IllegalBlockSizeException ex){
            Logger.getLogger(AES.class.getName()).log(Level.SEVERE, null, ex);
        }catch(BadPaddingException ex){
            Logger.getLogger(AES.class.getName()).log(Level.SEVERE, null, ex);
        }
	original=original!=null?original:new String("Esta imagen no fue firmada con el m&eacute;todo AES.").getBytes();
        String originalString = new String(original);
	return originalString;
    }
    
    private SecretKeySpec createKey(String password) throws NoSuchAlgorithmException{
        byte[] raw=new byte[password.length()];
        password.getBytes(0,password.length(),raw,0);
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        return skeySpec;
    }
    
    //Calcula el MD5 de un string
    //El MD5 tiene una longitud de 32 caracteres
    private String md5(String texto) throws NoSuchAlgorithmException{
        MessageDigest md=MessageDigest.getInstance("MD5");
        byte[] digerido=md.digest(texto.getBytes());
        int size=digerido.length;
        StringBuilder sFinal=new StringBuilder(size);
        //Algoritmo y arreglo md5
        for (int i=0; i<size; i++){
			int num=digerido[i] & 255;
            if(num<16){
                sFinal.append("0").append(Integer.toHexString(num));
            }else{
                sFinal.append(Integer.toHexString(num));
            }
        }
        //Clave encriptada
        return sFinal.toString();
    }
    
    private String halfCode(String oldString){
        int inicio=0;
        StringBuilder newString=new StringBuilder(oldString.length()/2);
        if(((int)oldString.charAt(0))%2!=0)
            inicio++;
        for(int i=inicio; i<oldString.length(); i+=0x2){
            newString.append(oldString.charAt(i));
        }
        return newString.toString();
    }
}