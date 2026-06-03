package controlador;

import util.Conexion;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Controlador del modulo de respaldo de datos.
 * Genera copias de seguridad de la base de datos MySQL
 * usando mysqldump directamente desde la aplicacion.
 *
 * @author Luiggi
 */
public class RespaldoController {

    /** Nombre de la base de datos a respaldar */
    private static final String BASE_DATOS = "yogurin_db";

    /** Usuario de la base de datos */
    private static final String DB_USUARIO = "root";

    /** Contrasena de la base de datos */
    private static final String DB_CONTRASENA = "";

    /**
     * Ejecuta un respaldo completo de la base de datos.
     * El archivo se guarda con la fecha y hora en el nombre.
     *
     * @param carpetaDestino Ruta de la carpeta donde se guardara el archivo SQL
     * @return Ruta del archivo generado, o null si ocurrio un error
     */
    public String ejecutarRespaldo(String carpetaDestino) {
        String nombreArchivo = generarNombreArchivo();
        String rutaCompleta = carpetaDestino + File.separator + nombreArchivo;

        try {
            // Construir el comando mysqldump
            String comando = String.format(
                "mysqldump -u%s %s %s > \"%s\"",
                DB_USUARIO,
                DB_CONTRASENA.isEmpty() ? "" : "-p" + DB_CONTRASENA,
                BASE_DATOS,
                rutaCompleta
            );

            ProcessBuilder pb = new ProcessBuilder("cmd.exe", "/c", comando);
            pb.redirectErrorStream(true);
            Process proceso = pb.start();
            int codigoSalida = proceso.waitFor();

            if (codigoSalida == 0) {
                Conexion.cerrar(); // Liberar conexion antes de cerrar
                return rutaCompleta;
            } else {
                return null;
            }

        } catch (IOException | InterruptedException e) {
            System.err.println("Error al ejecutar respaldo: " + e.getMessage());
            Thread.currentThread().interrupt();
            return null;
        }
    }

    /**
     * Verifica que mysqldump este disponible en el sistema.
     *
     * @return true si mysqldump puede ejecutarse
     */
    public boolean mysqldumpDisponible() {
        try {
            Process p = new ProcessBuilder("mysqldump", "--version").start();
            return p.waitFor() == 0;
        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    /**
     * Genera el nombre del archivo de respaldo con formato:
     * yogurin_backup_yyyyMMdd_HHmmss.sql
     *
     * @return Nombre del archivo SQL
     */
    private String generarNombreArchivo() {
        String timestamp = LocalDateTime.now()
            .format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        return "yogurin_backup_" + timestamp + ".sql";
    }
}
