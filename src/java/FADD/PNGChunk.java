/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package FADD;

import java.util.zip.CRC32;
import java.util.zip.DataFormatException;

/**
 * <p>Contiene la información de un segmento ({@code chunk}) de un archivo de imagen PNG. Cada chunk está constituido por 3 ó 4 partes:</p>
 * <ul>
 *  <li>Longitud del contenido. 4 bytes.</li>
 *  <li>Nombre del chunk. 4 bytes.</li>
 *  <li>Contenido (si la longitud es 0, esta parte no existe). La longitud que se haya leído.</li>
 *  <li>CRC (código de redundancia cíclica). 4 bytes.</li>
 * </ul>
 * <p>El CRC del chunk es usado para corroborar la integridad de la información del mismo. Si el CRC del chunk no concuerda con el calculado
 * de acuerdo con su nombre y contenido, se marca como información corrupta.</p>
 * 
 * @author Juan Carlos López Pimentel
 * @author Luis Enrique Cuevas Díaz
 * @author Ramón Antonio Carrillo Gutiérrez
 * @author Samara Anaid Montoya Anzueto.
 * @version 1.0, 21/08/11
 * @since   JDK1.6
 */
public class PNGChunk{
    private byte[] byteChunk; //El arreglo byte que representa al chunk completo
    private int longitud=0; //Longitud del contenido del chunk
    private String nombre=""; //Nombre del chunk
    private byte[] contenido; //Contenido del chunk
    private int CRC=0; //Código de Redundancia Cíclica (CRC) del chunk
    private boolean isValid=false; //Si el chunk es válido o no según su CRC
    
    /**
     * <p>Construye un objeto {@code PNG} a partir del arreglo de bytes especificado
     * decodificando los datos necesarios (longitud, nombre, contenido y CRC).</p>
     * 
     * @param chunk El arreglo de bytes a ser decodificado.
     * @exception DataFormatException Si el CRC no concuerda con el CRC calculado de acuerdo a los datos del chunk.
     */
    public PNGChunk(byte[] chunk) throws DataFormatException{
        if(!decodeChunk(chunk))
            throw new DataFormatException("El chunk contiene información corrompida.");
        byteChunk=chunk;
    }
    
    /**
     * <p>Construye un nuevo objeto {@code PNG} de acuerdo al nombre y contenido especificados. La longitud y el CRC son calculados
     * a partir de la información proporcionada.</p>
     * 
     * @param nombreCadena El nombre del chunk a crear.
     * @param contenido El contenido del nuevo chunk; si es nulo, se creará un chunk sin contenido.
     */
    public PNGChunk(String nombreCadena, byte[] contenido){
        //Medida de seguridad por si el contenido es nulo
        if(contenido==null)
            contenido=new byte[]{};
        //Obtenemos los datos correctos del chunk
        byte[] chunkName=convNombre(nombreCadena);
        byte[] chunkLength=intToDWord(contenido.length);
        CRC=calcularCRC(chunkName, contenido);
        byte[] crc=intToDWord(CRC);
        //El crc debe ser puesto al revés de como es interpretado como un dword
        crc=new byte[]{crc[3],crc[2],crc[1],crc[0]};
        //Creamos el nuevo arreglo de bytes para el chunk
        byte[] chunk=new byte[12+contenido.length];
        System.arraycopy(chunkLength, 0, chunk, 0, 4); //Longitud
        System.arraycopy(chunkName, 0, chunk, 4, 4); //Nombre
        System.arraycopy(contenido, 0, chunk, 8, contenido.length); //Contenido
        System.arraycopy(crc, 0, chunk, 8+contenido.length, 4); //CRC
        //Actualizamos los atributos del objeto
        byteChunk=chunk;
        longitud=contenido.length;
        nombre=nombreCadena;
        this.contenido=contenido;
    }
    
    /**
     * <p>Devuelve la longitud del contenido de un chunk para poderlo leer correctamente. La longitud total de un chunk
     * es de la longitud leída más 12 bytes adicionales (longitud, nombre y CRC).</p>
     * 
     * @param longitud Un arreglo de 4 bytes que representa la longitud del chunk a ser leído.
     * @return La longitud del chunk interpretada como un entero.
     */
    public static int getLength(byte[] longitud){
        return dwordToInt(longitud);
    }
    
    /**
     * <p>Devuelve la longitud del contenido, en forma de entero, de este chunk.</p>
     * 
     * @return La longitud del contenido de este chunk.
     */
    public int getLength(){
        return longitud;
    }
    
    /**
     * <p>Devuelve el nombre de este chunk como un <code>String</code>.</p>
     * 
     * @return El nombre de este chunk.
     */
    public String getName(){
        return nombre;
    }
    
    /**
     * <p>Devuelve el contenido del chunk tal como es leído, un arreglo byte sin interpretar.</p>
     * 
     * @return El contenido de este chunk.
     */
    public byte[] getContent(){
        return contenido;
    }
    
    /**
     * <p>Devuelve el CRC calculado del chunk en formato de número <code>long</code>.</p>
     * 
     * @return El CRC de este chunk.
     */
    public long getCRC(){
        return (long) CRC;
    }

    /**
     * <p>Devuelve el chunk completo en forma de arreglo byte.</p>
     * 
     * @return El arreglo byte que representa a este chunk.
     */
    public byte[] getChunk(){
        return byteChunk;
    }
    
    /**
     * <p>Asigna un nuevo arreglo byte a este {@code PNGChunk} para que su información sea decodificada.</p>
     * 
     * @param nChunk El arreglo byte que contiene la nueva información para construir el chunk y todos sus datos.
     * @exception DataFormatException Si el CRC no concuerda con el CRC calculado de acuerdo a los datos del chunk.
     */
    public void setChunk(byte[] nChunk) throws DataFormatException{
        if(!decodeChunk(nChunk))
            throw new DataFormatException("El chunk contiene información corrompida.");
        byteChunk=nChunk;
    }
    
    //Decodifica la información de un arreglo byte para obtener todos los datos del chunk
    private boolean decodeChunk(byte[] chunk){
        //Datos del chunk
        longitud=dwordToInt(new byte[]{chunk[3], chunk[2], chunk[1], chunk[0]});
        
        for(int i=0; i<4; i++)
            nombre=nombre.concat(asciiToString(chunk[i+4]));
        
        contenido=new byte[longitud];
        System.arraycopy(chunk, 8, contenido, 0, longitud);

        byte[] nCRC=new byte[4];
        System.arraycopy(chunk, 8+longitud, nCRC, 0, 4);
        CRC=dwordToInt(new byte[]{chunk[11+longitud], chunk[10+longitud], chunk[9+longitud], chunk[8+longitud]});
        
        isValid=comprobarCRC(new byte[]{chunk[4], chunk[5], chunk[6], chunk[7]}, contenido, CRC);
        if(!isValid) //Si la información no es válida (por comprobación del CRC), se devuelve falso
            return false;
        
        return true; //El CRC y demás datos del chunk son válidos
    }
    
    //Convierte un arreglo de 4 bytes en int
    private static int dwordToInt(byte dword[]){
        int num=(((int)dword[3]&0xff)<<24)
        | (((int)dword[2]&0xff)<<16)
        | (((int)dword[1]&0xff)<<8)
        | (int)dword[0]&0xff;
        
        return num;
    }
    
    //Convierte un int en un arreglo de 4 bytes
    private byte[] intToDWord(int parValue){
        byte retValue[] = new byte[4];
        retValue[0] = (byte) (parValue & 0x00FF);
        retValue[1] = (byte) ((parValue >> 8) & 0x000000FF);
        retValue[2] = (byte) ((parValue >> 16) & 0x000000FF);
        retValue[3] = (byte) ((parValue >> 24) & 0x000000FF);
        return retValue;
    }
    
    //Convierte un byte con signo a su equivalente char sin signo
    private String asciiToString(byte ascii){
        char letra=(char) (ascii&0xFF);
        String letraString="";
        letraString+=letra;
        return letraString;
    }
    
    //Calcula el CRC de los datos enviados, el primer dato a tomar en cuenta es "tipoData"
    private int calcularCRC(byte tipoData[], byte contenido[]){
        CRC32 crc=new CRC32();
        crc.update(tipoData);
        crc.update(contenido);
        return (int)crc.getValue();
    }
    
    //Comprueba si el CRC enviado concuerda con el CRC calculado de acuerdo a los datos especificados 
    private boolean comprobarCRC(byte tipoData[], byte contenido[], int crc){
        return calcularCRC(tipoData, contenido)==crc;
    }
    
    //Convertir un String en un arreglo de 4 bytes
    private byte[] convNombre(String nombre){
        //Si no es un String válido, regresa null
        if(nombre.length()!=4)
            return null;

        byte[] nNombre=new byte[4];
        //Convertimos cada carácter en su equivalente byte con signo
        for(int i=0; i<nNombre.length; i++)
            nNombre[i]=(byte) nombre.charAt(i);
        
        return nNombre;
    }
}

//Estancia (6º/7º)
//Proyecto FADD (Firmador y Autentificador de Documentos Digitales)
//Desarrolladores:
    //Luis Enrique Cuevas Díaz
    //Ramón Antonio Carrillo Gutiérrez
//Investigación adicional:
    //Samara Anaid Montoya Anzueto