package FADD;

/**
 *
 * @author RACG
 * @author SAMA
 */

import javax.swing.JLabel;
import javax.swing.JTextField;

public class Text_XML implements Convert{
    
	public String toXML(JLabel[] jl, JTextField[] jtf){
        String xml="";
        if(jl.length<=0)
            return "";
        for(int i=0;i<jl.length;i++){
            xml=xml+"\t\t<"+sin2Puntos(jl[i].getText())+">"+jtf[i].getText().trim()+"<\\"+sin2Puntos(jl[i].getText())+">\n";
        }
        xml = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>"+"\n\t<firma>\n"+xml+"\t</firma>";
        return xml;
    }
    
    public String toXML(String x, String sp1, String sp2){
        String xml="";
        String []arrecadenas, arresubcadenas;
        arrecadenas=x.split(sp2);
        arresubcadenas = new String [arrecadenas.length];
        for(int i=0;i<arrecadenas.length;i++){
            arresubcadenas = arrecadenas[i].split(sp1);
            xml=xml+"\t\t<"+arresubcadenas[0].trim()+">"+arresubcadenas[1].trim()+"</"+arresubcadenas[0].trim()+">\n";
        }
        xml = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>"+"\n\t<firma>\n"+xml+"\t</firma>";
        return xml;
    }
    
    private String sin2Puntos(String texto){
        if(texto.trim().endsWith(":"))
            texto=texto.replace(':', ' ').trim();
        return texto.trim();
    }

	public String toText(String xml, String sp){
        String text="";
        int tam, tam2;
        String[] cadena, subcadena;
        cadena=xml.split(sp);
        subcadena=new String[cadena.length];
        for(int i=2;i<cadena.length-1;i++){
            subcadena=cadena[i].split(">");
            tam=subcadena[0].length();
            tam2=subcadena[1].length();
            subcadena[0]=subcadena[0].replace('<', ' ').trim();
            //System.out.println(subcadena[0]);
            subcadena[1]=subcadena[1].substring(0, tam2-tam+1);
            //System.out.println(subcadena[1]);
            text+=subcadena[0].trim()+": "+subcadena[1].trim()+"\n";
            //text = text+cadena[i];
        }
        return text;
    }

	public String [] getLabels_from_Text(String x, String sp){
		String [] partes = new String [50];
		partes = x.split("\n");
		String [] subpartes = new String [5];
		String [] labels = new String [partes.length];

		for(int i=0;i<partes.length;i++){
			subpartes=partes[i].split(sp);
			labels[i]=subpartes[0];
		}
		return labels;
	}
	public String [] getFields_from_Text(String x, String sp){
		String [] partes = new String [50];
		partes = x.split("\n");
		String [] subpartes = new String [5];
		String [] fields = new String [partes.length];

		for(int i=0;i<partes.length;i++){
			subpartes=partes[i].split(sp);
			fields[i]=subpartes[1];
		}
		return fields;
	}

	public String [] getLabels_from_XML(String x){
		String [] partes = new String [50];
                String stlabels="";
		partes = x.split("\n");
                String [] subpartes = new String [5];
                 String [] labels = new String [50];

		for(int i=2;i<partes.length-1;i++){
			subpartes=partes[i].split(">");
			stlabels=stlabels+subpartes[0].replace("<", " ").trim()+":::";
		}
                 labels=stlabels.split(":::");
		return labels;
	}
	public String [] getFields_from_XML(String x){
		String [] partes = new String [50];
                String stfields="";
		partes = x.split("\n");
                String [] subpartes = new String [5];
                 String [] fields = new String [50];

		for(int i=2;i<partes.length-1;i++){
			subpartes=partes[i].split(">");
			stfields=stfields+subpartes[1].substring(0, subpartes[1].length()-subpartes[0].length()+1)+":::";
		}
                 fields=stfields.split(":::");
		return fields;
	}
}