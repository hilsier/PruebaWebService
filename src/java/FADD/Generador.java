package FADD;

import java.util.Random;

public class Generador{
	public static String Pass_Generator(int cant){
		Random rnd=new Random();
		String caracteres="0123456789abcdfghjkmnpqrstvwxyzABCDFGHJKMNPQRSTVWXYZ-_";
		String pass="";
		for(int i = 0; i < cant; i++){
			int x=(int)(rnd.nextDouble() * caracteres.length());
			pass+=caracteres.charAt(x);
		}
		return pass;
	}
}
