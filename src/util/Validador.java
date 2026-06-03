package util;

/**
 * Utilidad de validacion de campos del sistema.
 * Centraliza todas las reglas de validacion para evitar codigo duplicado
 * en controladores y vistas.
 *
 * @author Luiggi
 * @version 1.0
 * @since 2026
 */
public class Validador {

    /** Constructor privado — clase de solo metodos estaticos */
    private Validador() {}

    // ── Validaciones generales ────────────────────────────────────────────────

    /**
     * Verifica que un texto no sea nulo ni este vacio.
     *
     * @param texto Cadena a evaluar
     * @return {@code true} si el texto tiene contenido real
     */
    public static boolean noEstaVacio(String texto) {
        return texto != null && !texto.trim().isEmpty();
    }

    /**
     * Verifica que un texto este vacio o sea nulo.
     * Inverso de {@link #noEstaVacio(String)}.
     *
     * @param texto Cadena a evaluar
     * @return {@code true} si el texto es nulo o esta vacio
     */
    public static boolean estaVacio(String texto) {
        return !noEstaVacio(texto);
    }

    /**
     * Verifica que un texto contenga solo digitos numericos (0-9).
     *
     * @param texto Cadena a evaluar
     * @return {@code true} si el texto es numerico puro
     */
    public static boolean esSoloNumeros(String texto) {
        return noEstaVacio(texto) && texto.matches("\\d+");
    }

    /**
     * Verifica que un texto tenga al menos {@code min} caracteres.
     *
     * @param texto Cadena a evaluar
     * @param min   Longitud minima requerida
     * @return {@code true} si el texto tiene la longitud minima
     */
    public static boolean tieneLongitudMinima(String texto, int min) {
        return noEstaVacio(texto) && texto.trim().length() >= min;
    }

    // ── Validaciones de negocio ───────────────────────────────────────────────

    /**
     * Verifica que una cadena sea un numero entero positivo valido.
     *
     * @param texto Cadena que representa un entero
     * @return {@code true} si es entero mayor que cero
     */
    public static boolean esEnteroPositivo(String texto) {
        try {
            return Integer.parseInt(texto) > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Verifica que una cadena sea un numero decimal positivo valido.
     *
     * @param texto Cadena que representa un decimal
     * @return {@code true} si es decimal mayor que cero
     */
    public static boolean esDecimalPositivo(String texto) {
        try {
            return Double.parseDouble(texto) > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Verifica que un DNI tenga exactamente 8 digitos numericos.
     *
     * @param dni Cadena con el DNI a validar
     * @return {@code true} si el DNI es valido
     */
    public static boolean esDniValido(String dni) {
        return dni != null && dni.matches("\\d{8}");
    }

    /**
     * Verifica que un numero de telefono tenga entre 7 y 15 digitos.
     *
     * @param telefono Cadena con el telefono a validar
     * @return {@code true} si el telefono es valido
     */
    public static boolean esTelefonoValido(String telefono) {
        return telefono != null && telefono.matches("\\d{7,15}");
    }

    /**
     * Verifica que una contrasena tenga al menos 6 caracteres.
     *
     * @param clave Contrasena a validar
     * @return {@code true} si cumple la longitud minima de seguridad
     */
    public static boolean esClaveValida(String clave) {
        return tieneLongitudMinima(clave, 6);
    }
}
