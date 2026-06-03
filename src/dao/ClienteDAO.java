package dao;

import modelo.Cliente;
import util.Conexion;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO (Data Access Object) para la entidad Cliente.
 * Gestiona las operaciones de consulta e insercion en la tabla "clientes".
 *
 * @author Luiggi
 */
public class ClienteDAO {

    /**
     * Retorna la lista completa de clientes registrados.
     *
     * @return Lista de clientes
     * @throws SQLException si ocurre un error en la base de datos
     */
    public List<Cliente> listarTodos() throws SQLException {
        List<Cliente> lista = new ArrayList<>();
        String sql = "SELECT id, nombre, dni, telefono FROM clientes ORDER BY nombre";

        try (Connection con = Conexion.getInstancia();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                lista.add(mapearCliente(rs));
            }
        }
        return lista;
    }

    /**
     * Busca un cliente por su numero de DNI.
     *
     * @param dni DNI del cliente a buscar
     * @return Cliente encontrado, o null si no existe
     * @throws SQLException si ocurre un error en la base de datos
     */
    public Cliente buscarPorDni(String dni) throws SQLException {
        String sql = "SELECT id, nombre, dni, telefono FROM clientes WHERE dni = ?";

        try (Connection con = Conexion.getInstancia();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, dni);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearCliente(rs);
                }
            }
        }
        return null;
    }

    /**
     * Inserta un nuevo cliente en la base de datos.
     *
     * @param cliente Objeto Cliente con los datos a insertar
     * @return ID generado del nuevo cliente, o -1 si fallo
     * @throws SQLException si ocurre un error en la base de datos
     */
    public int insertar(Cliente cliente) throws SQLException {
        String sql = "INSERT INTO clientes (nombre, dni, telefono) VALUES (?, ?, ?)";

        try (Connection con = Conexion.getInstancia();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, cliente.getNombre());
            ps.setString(2, cliente.getDni());
            ps.setString(3, cliente.getTelefono());

            if (ps.executeUpdate() > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        return keys.getInt(1);
                    }
                }
            }
        }
        return -1;
    }

    /**
     * Actualiza los datos de un cliente existente.
     *
     * @param cliente Objeto Cliente con los datos actualizados
     * @return true si la actualizacion fue exitosa
     * @throws SQLException si ocurre un error en la base de datos
     */
    public boolean actualizar(Cliente cliente) throws SQLException {
        String sql = "UPDATE clientes SET nombre = ?, dni = ?, telefono = ? WHERE id = ?";

        try (Connection con = Conexion.getInstancia();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, cliente.getNombre());
            ps.setString(2, cliente.getDni());
            ps.setString(3, cliente.getTelefono());
            ps.setInt(4, cliente.getId());

            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Mapea una fila del ResultSet a un objeto Cliente.
     */
    private Cliente mapearCliente(ResultSet rs) throws SQLException {
        return new Cliente(
            rs.getInt("id"),
            rs.getString("nombre"),
            rs.getString("dni"),
            rs.getString("telefono")
        );
    }
}
