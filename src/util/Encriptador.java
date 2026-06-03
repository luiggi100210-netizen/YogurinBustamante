package util;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Utilidad para encriptar contrasenas usando el algoritmo MD5.
 * Se usa al registrar usuarios y al validar credenciales de login.
 *
 * @author Luiggi
 */
public class Encriptador {

    /** Constructor privado — clase de solo metodos estaticos */
    private Encriptador() {}

    /**
     * Convierte un texto plano en su hash MD5 de 32 caracteres.
     *
     * @param texto Texto a encriptar (contrasena en plano)
     * @return Hash MD5 del texto, o null si ocurre un error interno
     */
    public static String encriptarMD5(String texto) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] bytes = md.digest(texto.getBytes());
            BigInteger numero = new BigInteger(1, bytes);
            String hash = numero.toString(16);

            // Rellenar con ceros a la izquierda hasta completar 32 caracteres
            while (hash.length() < 32) {
                hash = "0" + hash;
            }
            return hash;

        } catch (NoSuchAlgorithmException e) {
            System.err.println("Error al encriptar: " + e.getMessage());
            return null;
        }
    }

    /**
     * Verifica si un texto plano coincide con un hash MD5 almacenado.
     *
     * @param textoCrudo  Texto en plano ingresado por el usuario
     * @param hashAlmacenado Hash MD5 guardado en la base de datos
     * @return true si coinciden, false en caso contrario
     */
    public static boolean verificar(String textoCrudo, String hashAlmacenado) {
        String hashGenerado = encriptarMD5(textoCrudo);
        return hashGenerado != null && hashGenerado.equals(hashAlmacenado);
    }
}
