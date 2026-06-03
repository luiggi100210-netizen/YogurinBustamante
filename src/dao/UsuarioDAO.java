package dao;

import modelo.Usuario;
import util.Conexion;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO (Data Access Object) para la entidad Usuario.
 * Gestiona todas las operaciones CRUD en la tabla "usuarios".
 *
 * @author Luiggi
 */
public class UsuarioDAO {

    /**
     * Busca un usuario por su nombre de usuario y clave encriptada.
     * Se usa para autenticar el login.
     *
     * @param usuario Nombre de usuario
     * @param clave   Clave encriptada MD5
     * @return Usuario si las credenciales son correctas, null si no coinciden
     * @throws SQLException si ocurre un error en la base de datos
     */
    public Usuario autenticar(String usuario, String clave) throws SQLException {
        String sql = "SELECT id, nombre, usuario, clave, rol FROM usuarios "
                   + "WHERE usuario = ? AND clave = ? AND activo = 1";

        try (Connection con = Conexion.getInstancia();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, usuario);
            ps.setString(2, clave);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearUsuario(rs);
                }
            }
        }
        return null;
    }

    /**
     * Retorna la lista completa de usuarios registrados.
     *
     * @return Lista de usuarios
     * @throws SQLException si ocurre un error en la base de datos
     */
    public List<Usuario> listarTodos() throws SQLException {
        List<Usuario> lista = new ArrayList<>();
        String sql = "SELECT id, nombre, usuario, clave, rol FROM usuarios WHERE activo = 1 ORDER BY nombre";

        try (Connection con = Conexion.getInstancia();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                lista.add(mapearUsuario(rs));
            }
        }
        return lista;
    }

    /**
     * Inserta un nuevo usuario en la base de datos.
     *
     * @param usuario Objeto Usuario con los datos a insertar
     * @return true si la insercion fue exitosa
     * @throws SQLException si ocurre un error en la base de datos
     */
    public boolean insertar(Usuario usuario) throws SQLException {
        String sql = "INSERT INTO usuarios (nombre, usuario, clave, rol, activo) VALUES (?, ?, ?, ?, 1)";

        try (Connection con = Conexion.getInstancia();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, usuario.getNombre());
            ps.setString(2, usuario.getUsuario());
            ps.setString(3, usuario.getClave());
            ps.setString(4, usuario.getRol());

            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Actualiza los datos de un usuario existente.
     *
     * @param usuario Objeto Usuario con los datos actualizados
     * @return true si la actualizacion fue exitosa
     * @throws SQLException si ocurre un error en la base de datos
     */
    public boolean actualizar(Usuario usuario) throws SQLException {
        String sql = "UPDATE usuarios SET nombre = ?, usuario = ?, clave = ?, rol = ? WHERE id = ?";

        try (Connection con = Conexion.getInstancia();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, usuario.getNombre());
            ps.setString(2, usuario.getUsuario());
            ps.setString(3, usuario.getClave());
            ps.setString(4, usuario.getRol());
            ps.setInt(5, usuario.getId());

            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Desactiva un usuario (baja logica). No elimina el registro fisicamente.
     *
     * @param id Identificador del usuario a desactivar
     * @return true si la operacion fue exitosa
     * @throws SQLException si ocurre un error en la base de datos
     */
    public boolean desactivar(int id) throws SQLException {
        String sql = "UPDATE usuarios SET activo = 0 WHERE id = ?";

        try (Connection con = Conexion.getInstancia();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Mapea una fila del ResultSet a un objeto Usuario.
     *
     * @param rs ResultSet posicionado en la fila a mapear
     * @return Usuario con los datos de la fila
     * @throws SQLException si falla la lectura de columnas
     */
    private Usuario mapearUsuario(ResultSet rs) throws SQLException {
        return new Usuario(
            rs.getInt("id"),
            rs.getString("nombre"),
            rs.getString("usuario"),
            rs.getString("clave"),
            rs.getString("rol")
        );
    }
}
