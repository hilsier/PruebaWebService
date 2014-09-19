/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package FADD;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.DataFormatException;

/**
 * <p>Clase que guarda, manipula e interpreta los datos necesarios para la creación de imágenes <code>PNG</code>.
 * El archivo comienza con una firma que debe ser siempre la misma: 89 50 4E 47 0D 0A 1A 0A
 * (expresada en hexadecimal). El archivo se divide en distintos segmentos llamados <em>chunks</em>,
 * uno de ellos es el <em>IHDR</em> ("image header") y contiene los datos de la cabecera de la imagen.
 * Los chunks subsecuentes se dividen en críticos (IHDR se incluye como un chunk crítico) y auxiliares.</p>
 * 
 * <p>A continuación se muestra una tabla con la información de los chunks que contempla
 * la estandarización internacional.</p>
 * 
 * <table border="1" cellpadding="5" style="float:both; margin: auto; border-collapse:collapse">
 * <tr><th>Nombre</th><th>Crítico/auxiliar</th><th>Multiplicidad</th><th>Función</th></tr>
 * <tr><td>IHDR</td><td>Crítico</td><td>1</td><td>Cabecera de la imagen</td></tr>
 * <tr><td>PLTE</td><td>Crítico (opcional)</td><td>0 ó 1</td><td>Paleta (lista de colores)</td></tr>
 * <tr><td>IDAT</td><td>Crítico</td><td>1 o más</td><td>La imagen a ser mostrada</td></tr>
 * <tr><td>IEND</td><td>Crítico</td><td>1</td><td>Fin del archivo</td></tr>
 * <tr><td>cHRM</td><td>Auxiliar</td><td>0 ó 1</td><td>Balance de blanco</td></tr>
 * <tr><td>gAMA</td><td>Auxiliar</td><td>0 ó 1</td><td>Especifica la gamma de la imagen</td></tr>
 * <tr><td>iCCP</td><td>Auxiliar</td><td>0 ó 1</td><td>Perfil ICC de color</td></tr>
 * <tr><td>sBIT</td><td>Auxiliar</td><td>0 ó 1</td><td>(Bits significativos) indican la exactitud de los colores en la imagen</td></tr>
 * <tr><td>sRGB</td><td>Auxiliar</td><td>0 ó 1</td><td>Indica que se usa el estándar sRBG color space</td></tr>
 * <tr><td>bKGD</td><td>Auxiliar</td><td>0 ó 1</td><td>El color de fondo por defecto, se usa cuando no hay un mejor color disponible para mostrar</td></tr>
 * <tr><td>hIST</td><td>Auxiliar</td><td>0 ó 1</td><td>El histograma o cantidad total de cada color en la imagen</td></tr>
 * <tr><td>tRNS</td><td>Auxiliar</td><td>0 ó 1</td><td>Información sobre la transparencia</td></tr>
 * <tr><td>pHYs</td><td>Auxiliar</td><td>0 ó 1</td><td>El tamaño previsto del píxel y/o el ratio de la imagen</td></tr>
 * <tr><td>sPLT</td><td>Auxiliar</td><td>0 ó más</td><td>Sugiere una paleta para usar en caso de que el rango completo de colores no esté disponible</td></tr>
 * <tr><td>tIME</td><td>Auxiliar</td><td>0 ó 1</td><td>Fecha de la última modificación</td></tr>
 * <tr><td>iTXt</td><td>Auxiliar</td><td>0 ó más</td><td>Texto (UTF-8) comprimido o no</td></tr>
 * <tr><td>tEXt</td><td>Auxiliar</td><td>0 ó más</td><td>Texto que puede ser representado en ISO 8859-1 con un nombre=valor para cada sección</td></tr>
 * <tr><td>zTXt</td><td>Auxiliar</td><td>0 ó más</td><td>Texto comprimido con los mismos límites que tEXt</td></tr>
 * </table>
 * 
 * <p>Los chunks deben aparecer en un orden específico; por ejemplo, el IHDR debe aparece al principio y el IEND (un chunk vacío) al final;
 * igualmente, si aparce el chunk iCCP, no debe aparecer el chunk sRGB y viceversa. Los chunks IDAT contienen la información propia de la imagen.
 * Los demás chunks proporcionan información adicional sobre el archivo y la forma en que debe procesarse la imagen.</p>
 *
 * @author  Juan Carlos López Pimentel
 * @author  Luis Enrique Cuevas Díaz
 * @author  Ramón Antonio Carrillo Gutiérrez
 * @author  Samara Anaid Montoya Anzueto.
 * @version 1.0, 21/08/11
 * @see     PNGChunk
 * @since   JDK1.6
 */
public class PNG{
    private byte[] image=null; //bytes de la imagen
    private boolean isValid=false; //Si tiene la firma inicial o no
    private File archivoOrigen;
    private PNGChunk[] pngChunks=null; //Chunks del PNG
    //Métodos usados en la firma
    private int mCifrado=0;
    private int mStega=0;
    
    //Sin corchetes: Es crítico.
    //Corchetes: Es auxiliar.
    //Comillas angulares: Puede aparecer múltiples veces.
    //Paréntesis: Si tiene 2 ó más nombres, sólo 1 puede aparecer; si hay sólo 1, es opcional.
    //IHDR, (PLTE), «IDAT», IEND, [cHRM], [gAMA], ([iCCP]/[sRGB]), [sBIT], [bKGD],
    //[hIST], [tRNS], [pHYs], «[sPLT]», [tIME], «[iTXt]», «[tEXt]», «[zTXt]»
    
    /**
     * <p>Inicializa un objeto de la clase {@code PNG} a partir del arreglo de chunks especificado por <code>chunks</code>.</p>
     * @param chunks El arreglo de chunks para inicializar las propiedades del objeto {@code PNG}.
     */
    public PNG(PNGChunk[] chunks){
        this.pngChunks=chunks;
    }
    
    /**
     * <p>Inicializa un objeto de la clase {@code PNG} a partir del arreglo de bytes especificado por <code>image</code>.</p>
     * @param image El arreglo byte para inicializar las propiedades del objeto {@code PNG}.
     * @param fileName Nombre del archivo a utilizarse.
     */

    public PNG(byte[] image, String fileName){
        archivoOrigen=new File(fileName);
        setDatos(image);
    }
    
    /**
     * <p>Inicializa un objeto de la clase {@code PNG} a partir del archivo <code>File</code> especificado por <code>image</code>.</p>
     * @param image El archivo para inicializar las propiedades del objeto {@code PNG}.
     */
    public PNG(File image){
        setDatos(image);
    }
    
    //Inicializa este objeto a partir de un archivo
    private void setDatos(File image){
        try{
            archivoOrigen=image;
            RandomAccessFile archivo=new RandomAccessFile(image, "r");
            byte[] nheader=new byte[(int)image.length()];
            archivo.read(nheader);
            setDatos(nheader);
            archivo.close();
        }catch(IOException e){System.out.println("Error al abrir la imagen."+e.getMessage());}
    }
    
    //Inicializa este objeto a partir del arreglo byte especificado
    private void setDatos(byte[] nImage){
        image=nImage; //El arreglo de bytes que representa al PNG
        boolean finDeCabecera=false; //Si se ha hallado o no el chunk IEND
        if(!leerFirma(image))
            return;
        else
            isValid=true; //La imagen es válida
        
        while(!finDeCabecera){
            byte[] longitud=new byte[]{image[3],image[2],image[1],image[0]};
            int contenidoLength=PNGChunk.getLength(longitud);
            //La longitud total de un chunk son la longitud que tiene guardada
            //más 4 (longitud), 4 (tipo) y 4 (CRC), que dan 12 bytes extras en total.
            byte[] chunk=new byte[contenidoLength+12];
            //Copiamos el fragmento de image que necesitamos a chunk
            System.arraycopy(image, 0, chunk, 0, chunk.length);
            PNGChunk pngChunk=null;
            try{
                pngChunk=new PNGChunk(chunk);
                if(pngChunk.getName().equals("idSF"))
                    interpretarChunkFADD(pngChunk);
            }catch (DataFormatException ex){
                Logger.getLogger(PNG.class.getName()).log(Level.SEVERE, null, ex);
                isValid=false; System.out.println("Chunk corrupto.");
                break;
            }
            image=recortarArray(image, chunk.length); //Quitamos los bytes leídos
            System.out.println(pngChunk.getName() + " --- " + pngChunk.getChunk().length);
            
            //Si es el chunk final, ya se ha leído toda la imagen
            if(pngChunk.getName().equals("IEND"))
                finDeCabecera=true;
            
            pngChunks=agregarElemento(pngChunk, pngChunks);
        }
        System.out.println();
    }
    
    /**
     * <p>Devuelve un arreglo con todos los chunks que contiene el archivo {@code PNG}.
     * Se incluyen la cabecera IHDR y el chunk final IEND. La longitud del arreglo devuelto es
     * el mismo número devuelto por el método {@code getChunksCount()}.</p>
     * 
     * @return Todos los chunks de este archivo
     */
    public PNGChunk[] getChunks(){
        PNGChunk[] allChunks=new PNGChunk[getChunksCount()];
        System.arraycopy(pngChunks, 0, allChunks, 0, pngChunks.length);
        /*if(idSF!=null)
            allChunks=agregarElemento(idSF, allChunks);
        allChunks=agregarElemento(IEND, allChunks);*/
        for (int i = 0; i < allChunks.length; i++) {
            if(allChunks[i]!=null)
                System.out.println(i + " " + allChunks[i].getName());
            else
                System.out.println(i + " Nulo.");
        }
        return allChunks;
    }
    
    /**
     * <p>Devuelve el número de chunks que contiene actualmente este {@code PNG},
     * incluyendo la cabecera IHDR y el chunk final IEND.</p>
     * 
     * @return El número de chunks del archivo
     */
    public int getChunksCount(){
        int num=pngChunks.length; //El número de chunks sin contar la cabecera ni el final
        return num;
    }
    
    /**
     * <p>Devuelve el archivo de origen de esta imagen {@code PNG}.</p>
     * 
     * @return El archivo origen del archivo {@code PNG}
     */
    public File getFile(){
        return archivoOrigen;
    }
    
    /**
     * <p>Devuelve el método de cifrado usado para ocultar información en esta imagen {@code PNG}.</p>
     * 
     * @return El método de cifrado usado en la imagen {@code PNG}.
     */
    public int getMethodCifrado(){
        return mCifrado;
    }

    /**
     * <p>Devuelve el método de esteganografía usado para ocultar información en esta imagen {@code PNG}.</p>
     * 
     * @return El método de esteganografía usado en la imagen {@code PNG}.
     */
    public int getMethodEStega(){
        return mStega;
    }
   
    /**
     * <p>Devuelve verdadero si la totalidad de los chunks del archivo son válidos.
     * Si devuelve falso, hay al menos un chunk que contiene información corrupta.</p>
     * 
     * @return Si el archivo es válido o no.
     */
    public boolean isValid(){
        return isValid;
    }
    
    /**
     * <p>Inserta el chunk especificado dentro del archivo en la posición especificada. Las posiciones comienzan a contarse desde 0.
     * Si el chunk a insertar es nulo, la posición a insertar es un número negativo o es un número igual o mayor al número de chunks 
     * actuales, se devolvará <code>false</code>.</p>
     * 
     * @param nChunk Objeto <code>PNGChunk</code> que contiene la información del chunk que va a ser insertado
     * @param posicion La posición en la cual se va a insertar el chunk
     * @return Si el chunk fue insertado correctamente o no
     */
    public boolean insertarChunk(PNGChunk nChunk, int posicion){
        //Prevenciones de seguridad
        if(nChunk==null || posicion<=0 || posicion>=getChunksCount())
            return false;
        
        PNGChunk[] chunksActuales=new PNGChunk[getChunksCount()+1];
        //int cont=0; //Contador para controlar el ciclo
        //boolean insertado=false;
        System.arraycopy(pngChunks, 0, chunksActuales, 0, posicion);
        chunksActuales[posicion]=nChunk;
        System.arraycopy(pngChunks, posicion, chunksActuales, posicion+1, getChunksCount()-posicion);
        /*while(cont<=getChunksCount()){
            int num=insertado?cont-1:cont;
            if(num==0)
                chunksActuales=agregarElemento(IHDR, chunksActuales);
            else if(num==getChunksCount())
                chunksActuales=agregarElemento(IEND, chunksActuales);
            else if(num!=posicion)
                chunksActuales=agregarElemento(pngChunks[num], chunksActuales);
            else{
                chunksActuales=agregarElemento(nChunk, chunksActuales);
                insertado=true;
            }
            cont++;
        }*/
        //Copiamos el arreglo generado al arreglo que contiene todos los chunks
        pngChunks=chunksActuales;
        return true;
    }
    
    /**
     * <p>Inserta el chunk especificado antes del chunk que marca el final de archivo (IEND). Si el chunk a insertar
     * es nulo, se devolverá <code>false</code>.</p>
     * 
     * @param nChunk Objeto <code>PNGChunk</code> que contiene la información del chunk que va a ser insertado
     * @return Si el chunk fue insertado correctamente o no
     */
    public boolean insertarChunk(PNGChunk nChunk){
        return insertarChunk(nChunk, getChunksCount()-1);
    }
    
    /**
     * <p>Remueve todos los chunks encontrados que tengan el nombre especificado. Si no se halla algún chunk con el nombre especificado,
     * se devuelve <code>false</code>.</p>
     * 
     * @param name El nombre del chunk a ser removido
     * @return Si el chunk o los chucnks fueron encontrados y removidos exitosamente o no
     */
    public boolean removerChunk(String name){
        PNGChunk[] chunksActuales=null;
        int cont=0; //Contador para controlar el ciclo
        boolean resultado=false;
        while(cont<getChunksCount()){
            if(!pngChunks[cont].getName().equals(name))
                chunksActuales=agregarElemento(pngChunks[cont], chunksActuales);
            cont++;
        }
        //Si el arreglo de chunks no tiene el mismo número de elementos que el nuevo arreglo,
        //se removió al menos 1 elemento.
        resultado=chunksActuales.length!=pngChunks.length;
        pngChunks=chunksActuales; //Copiamos el nuevo arreglo al arreglo de chunks
        return resultado;
    }
    
    /**
     * <p>Guarda la imagen {@code PNG} de acuerdo al archivo especificado en el argumento.
     * Se toman los chunks que tenga actualmente el objeto para construir el nuevo archivo.
     * El método devuelve la información sobre si el archivo fue guardado correctamente o no.</p>
     * 
     * @param archivo El archivo a utilizarse para guardar la imagen {@code PNG}
     * @return Si el archivo se guardó correctamente o no
     */
    public boolean savePNG(File archivo){
        try{
            savePNG(archivo, getChunks());
            archivo=null; return true;
        }catch(java.lang.Exception ex){
            Logger.getLogger(PNG.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
    
    /**
     * <p>Guarda la imagen {@code PNG} de acuerdo al archivo y los chunks especificados en los argumentos.
     * El método devuelve la información sobre si el archivo fue guardado correctamente o no.</p>
     * 
     * @param archivo El archivo a utilizarse para guardar la imagen {@code PNG}
     * @param chunks Arreglo que contiene los chunks para formar el nuevo archivo {@code PNG}
     * @return Si el archivo se guardó correctamente o no
     */
    public boolean savePNG(File archivo, PNGChunk[] chunks){
        try{
            boolean resultado=escribirArchivo(archivo, chunks);
            return resultado;
        }catch(java.lang.Exception ex){
            Logger.getLogger(PNG.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
    
    /**
     * <p>Asigna en los correspondientes campos de la cabecera de la imagen {@code PNG} sobre 
     * los métodos de cifrado y de esteganografía que serán usados para la firma digital.</p>
     * 
     * @param cifrado El método de cifrado
     * @param estega El método de esteganografía
     */
    public void setMetodos(int cifrado, int estega) {
        mCifrado=cifrado;
        mStega=estega;
        byte[] mC=intToDWord(cifrado);
        byte[] mS=intToDWord(estega);
        byte[] contenido=new byte[8];
        System.arraycopy(mC, 0, contenido, 0, mC.length);
        System.arraycopy(mS, 0, contenido, 0, mS.length);
        PNGChunk chunkFADD=new PNGChunk("idSF",contenido);
        insertarChunk(chunkFADD);
    }
    
    //                      M É T O D O S   P R I V A D O S                     //
    
    //Leer la firma inicial de la imagen "137 80 78 71 13 10 26 10",
    //"0x89 0x50 0x4E 0x47 0x0D 0x0A 0x1A 0x0A" en hexadecimal
    private boolean leerFirma(byte[] image) {
        int[] firmaValida=new int[]{137,80,78,71,13,10,26,10};
        byte[] firma=new byte[8];
        System.arraycopy(image, 0, firma, 0, 8);
        for(int i=0; i<firma.length; i++){
            if((firma[i]&0xFF)!=firmaValida[i])
                return false; //Algún carácter no coincide con la firma esperada
        }
        
        //Se eliminan los elementos ya valorados del arreglo
        this.image=recortarArray(image, firma.length);
        return true; //La firma es válida
    }
    
    //Elmina los índices indicados de un arreglo byte
    private byte[] recortarArray(byte[] oldArray, int recortar){
        byte[] arrayAux=new byte[oldArray.length-recortar];
        System.arraycopy(oldArray, recortar, arrayAux, 0, arrayAux.length);
        return arrayAux;
    }

    //Agregar un nuevo elemento a un arreglo de PNGChunks
    private PNGChunk[] agregarElemento(PNGChunk nElemento, PNGChunk[] arreglo){
        PNGChunk[] auxArray; //Arreglo auxiliar
        //Si no es nulo, copiamos el arreglo previo
        if(arreglo!=null){
            auxArray=new PNGChunk[arreglo.length+1];
            System.arraycopy(arreglo, 0, auxArray, 0, arreglo.length);
        }else{
            auxArray=new PNGChunk[1]; //Si es nulo, creamos un nuevo arreglo
        }
        
        auxArray[auxArray.length-1]=nElemento; //Añadimos el nuevo elemento
        return auxArray; //Devolvemos el nuevo arreglo con el elemento añadido
    }

    //Escribe un archivo de acuerdo a la ruta especificada por "archivo"
    //y los chunks indicados (serán tomados en orden).
    private boolean escribirArchivo(File archivo, PNGChunk[] chunks){
        try {
            FileOutputStream fos=new FileOutputStream(archivo);
            fos.write(new byte[]{(byte)137, (byte)80, (byte)78, (byte)71, (byte)13, (byte)10, (byte)26, (byte)10});
            int cont=0;
            while(cont<chunks.length){
                fos.write(chunks[cont].getChunk());
                cont++;
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(PNG.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        } catch (IOException ex) {
            Logger.getLogger(PNG.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        
        return true; //La imagen se escribió correctamente
    }

    private void interpretarChunkFADD(PNGChunk pngChunk){ //Not yet implemented
        byte[] contenido=pngChunk.getContent();
        if(contenido.length==8){
            //byte[] crcByte=new byte[]{contenido[8], contenido[9], contenido[10], contenido[11]};
            //int crcEsperado=dwordToInt(crcByte);
            mCifrado=dwordToInt(new byte[]{contenido[0], contenido[1], contenido[2], contenido[3]});
            mStega=dwordToInt(new byte[]{contenido[4], contenido[5], contenido[6], contenido[7]});
        }
    }
    
    //Convierte un arreglo de 4 bytes en int
    private int dwordToInt(byte dword[]){
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
}

//Estancia (6º/7º)
//Proyecto FADD (Firmador y Autentificador de Documentos Digitales)
//Desarrolladores:
    //Luis Enrique Cuevas Díaz
    //Ramón Antonio Carrillo Gutiérrez
//Investigación adicional:
    //Samara Anaid Montoya Anzueto