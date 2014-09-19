package FADD;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.io.File;

public class Conexion {
    //Atributos de conexión

    public static final String passDB = "jvvlab11";
    public static final String userDB = "root";
    public static final String projectDB = "authweb";
    public static String rutaTomcat = System.getenv("CATALINA_HOME") + File.separator + "webapps" + File.separator + "ROOT";
    //Atributo de error
    private String errorActual = "";

    //Insertar o borrar registros
    public boolean insOrDel(String instruccion) {
        try {
            Class.forName("org.gjt.mm.mysql.Driver");
            Connection conexion = DriverManager.getConnection("jdbc:mysql://localhost/" + projectDB, userDB, passDB);
            if (!conexion.isClosed()) {
                // La consulta en la BD
                Statement st = conexion.createStatement();
                //Ejecuta los commandos SQL
                st.execute(instruccion);

                // cierre de la conexión
                conexion.close();
            } else {
                // Error en la conexión
                errorActual = "Error al conectarse con la BD.";
                return false;
            }
        } catch (java.lang.Exception ex) {
            errorActual = "Error: " + ex.getMessage();
            return false;
        }
        return true;
    }

    //Actualizar un registro
    public boolean actualizar(String instruccion) {
        try {
            Class.forName("org.gjt.mm.mysql.Driver");
            Connection conexion = DriverManager.getConnection("jdbc:mysql://localhost/" + projectDB, userDB, passDB);
            if (!conexion.isClosed()) {
                // La consulta en la BD
                Statement st = conexion.createStatement();
                //Ejecuta el comando de actualización SQL
                st.executeUpdate(instruccion);

                // cierre de la conexión
                conexion.close();
            } else {
                // Error en la conexión
                errorActual = "Error al conectarse con la BD.";
                return false;
            }
        } catch (java.lang.Exception ex) {
            errorActual = "Error: " + ex.getMessage();
            return false;
        }
        return true;
    }

    //Buscar registros
    public ResultSet buscar(String instruccion) {
        try {
            Class.forName("org.gjt.mm.mysql.Driver");
            Connection conexion = DriverManager.getConnection("jdbc:mysql://localhost/" + projectDB, userDB, passDB);
            if (!conexion.isClosed()) {
                // La consulta en la BD
                Statement st = conexion.createStatement();
                //Ejecuta el comando de actualización SQL
                ResultSet rs = st.executeQuery(instruccion);
                if (!rs.next()) {
                    errorActual = "No hay resultados que coincidan";
                    return null;
                }

                //El cierre de conexión debe ser hecho con el objeto obtenido a través de
                //este método <ResultSet>.getStatement().getConnection().close();
                return rs;
            } else {
                // Error en la conexión
                errorActual = "Error al conectarse con la BD.";
                return null;
            }
        } catch (java.lang.Exception ex) {
            errorActual = "Error: " + ex.getMessage();
            return null;
        }
    }

    //Añadir y ejecutar batch
    public boolean ejecutar(String instruccion) {
        try {
            Class.forName("org.gjt.mm.mysql.Driver");
            Connection conexion = DriverManager.getConnection("jdbc:mysql://localhost/" + projectDB, userDB, passDB);
            if (!conexion.isClosed()) {
                // La consulta en la BD
                Statement st = conexion.createStatement();
                //Ejecuta los commandos SQL
                st.addBatch(instruccion);
                if (st.executeBatch().length == 0) {
                    errorActual = "No se hicieron cambios";
                    return false;
                }

                // cierre de la conexión
                conexion.close();
            } else {
                // Error en la conexión
                errorActual = "Error al conectarse con la BD.";
                return false;
            }
        } catch (java.lang.Exception ex) {
            errorActual = "Error: " + ex.getMessage();
            return false;
        }
        return true;
    }

    //Ver el error actual
    public String getError() {
        return errorActual;
    }

    public static boolean borrarDirectorio(File directorio) {
        File[] ficheros = directorio.listFiles();
        try {
            for (int x = 0; x < ficheros.length; x++) {
                if (ficheros[x].isDirectory()) {
                    if (!borrarDirectorio(ficheros[x])) {
                        return false;
                    }
                } else {
                    ficheros[x].delete();
                }
            }
            return true;
        } catch (java.lang.Exception ex) {
            return false;
        }
    }

    public static int contarDirectorio(File directorio) {
        File[] ficheros = directorio.listFiles();
        int numTotal = 0;
        try {
            for (int x = 0; x < ficheros.length; x++) {
                if (ficheros[x].isDirectory()) {
                    numTotal += contarDirectorio(ficheros[x]);
                } else {
                    ficheros[x].delete();
                    numTotal++;
                }
            }
            return numTotal;
        } catch (java.lang.Exception ex) {
            return 0;
        }
    }
}
//LECD 21-julio-11