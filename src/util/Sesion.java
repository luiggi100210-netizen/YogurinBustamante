package util;

import modelo.Usuario;

/**
 * Gestiona en memoria el usuario autenticado durante la sesion activa.
 * Implementa el patron Singleton para garantizar una unica instancia global.
 * Se inicializa al hacer login y se limpia al cerrar sesion.
 *
 * @author Luiggi
 * @version 1.0
 * @since 2026
 */
public class Sesion {

    /** Unica instancia de la clase (Singleton) */
    private static Sesion instancia;

    /** Usuario actualmente logueado */
    private Usuario usuarioActivo;

    /** Constructor privado — acceso exclusivo por getInstance() */
    private Sesion() {}

    /**
     * Retorna la unica instancia de Sesion.
     * La crea si aun no existe (lazy initialization).
     *
     * @return Instancia Singleton de Sesion
     */
    public static Sesion getInstance() {
        if (instancia == null) {
            instancia = new Sesion();
        }
        return instancia;
    }

    /**
     * Registra el usuario autenticado en la sesion activa.
     *
     * @param usuario Usuario que acaba de autenticarse correctamente
     */
    public void setUsuario(Usuario usuario) {
        this.usuarioActivo = usuario;
    }

    /**
     * Retorna el usuario actualmente logueado.
     *
     * @return Usuario activo, o {@code null} si no hay sesion abierta
     */
    public Usuario getUsuario() {
        return usuarioActivo;
    }

    /**
     * Verifica si existe una sesion activa.
     *
     * @return {@code true} si hay un usuario logueado
     */
    public boolean estaActiva() {
        return usuarioActivo != null;
    }

    /**
     * Verifica si el usuario activo tiene rol de Administrador.
     *
     * @return {@code true} si el usuario es ADMIN
     */
    public boolean esAdmin() {
        return estaActiva() && "ADMIN".equalsIgnoreCase(usuarioActivo.getRol());
    }

    /**
     * Cierra la sesion eliminando el usuario activo de memoria.
     */
    public void cerrarSesion() {
        this.usuarioActivo = null;
    }
}
