package controlador;

import dao.UsuarioDAO;
import modelo.Usuario;
import util.Encriptador;
import util.Sesion;
import util.Validador;

import java.sql.SQLException;

/**
 * Controlador de la pantalla de inicio de sesion.
 * Valida las credenciales del usuario, encripta la clave y gestiona
 * el inicio de sesion a traves de {@link Sesion}.
 * No contiene referencias a componentes Swing.
 *
 * @author Luiggi
 * @version 1.0
 * @since 2026
 */
public class LoginController {

    private final UsuarioDAO usuarioDAO;

    /** Inicializa el DAO requerido para la autenticacion */
    public LoginController() {
        this.usuarioDAO = new UsuarioDAO();
    }

    /**
     * Intenta autenticar al usuario con las credenciales proporcionadas.
     * Si la autenticacion es exitosa, registra al usuario en la sesion activa.
     *
     * @param usuario Nombre de usuario ingresado en el formulario
     * @param clave   Contrasena en texto plano ingresada en el formulario
     * @return {@link ResultadoLogin} con el estado y mensaje del intento
     */
    public ResultadoLogin login(String usuario, String clave) {
        if (Validador.estaVacio(usuario) || Validador.estaVacio(clave)) {
            return new ResultadoLogin(false, "Complete todos los campos.");
        }

        try {
            String claveEncriptada = Encriptador.encriptarMD5(clave);
            Usuario encontrado = usuarioDAO.autenticar(usuario.trim(), claveEncriptada);

            if (encontrado == null) {
                return new ResultadoLogin(false, "Usuario o contrasena incorrectos.");
            }

            Sesion.getInstance().setUsuario(encontrado);
            return new ResultadoLogin(true, "Bienvenido, " + encontrado.getNombre());

        } catch (SQLException e) {
            return new ResultadoLogin(false, "Error de conexion: " + e.getMessage());
        }
    }

    // ── Clase interna de resultado ────────────────────────────────────────────

    /**
     * Encapsula el resultado de un intento de inicio de sesion.
     * Contiene el estado (exitoso/fallido) y un mensaje descriptivo.
     */
    public static class ResultadoLogin {

        private final boolean exitoso;
        private final String  mensaje;

        /**
         * @param exitoso {@code true} si la autenticacion fue correcta
         * @param mensaje Mensaje de exito o error para mostrar al usuario
         */
        public ResultadoLogin(boolean exitoso, String mensaje) {
            this.exitoso = exitoso;
            this.mensaje = mensaje;
        }

        /** @return {@code true} si el login fue exitoso */
        public boolean isExitoso() { return exitoso; }

        /** @return Mensaje de resultado del login */
        public String getMensaje() { return mensaje; }
    }
}
