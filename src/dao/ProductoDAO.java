package dao;

import modelo.Producto;
import util.Conexion;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO (Data Access Object) para la entidad Producto.
 * Gestiona todas las operaciones CRUD en la tabla "productos".
 *
 * @author Luiggi
 */
public class ProductoDAO {

    /**
     * Retorna la lista completa de productos activos.
     *
     * @return Lista de productos
     * @throws SQLException si ocurre un error en la base de datos
     */
    public List<Producto> listarTodos() throws SQLException {
        List<Producto> lista = new ArrayList<>();
        String sql = "SELECT id, nombre, precio, stock, descripcion FROM productos "
                   + "WHERE activo = 1 ORDER BY nombre";

        try (Connection con = Conexion.getInstancia();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                lista.add(mapearProducto(rs));
            }
        }
        return lista;
    }

    /**
     * Busca un producto por su ID.
     *
     * @param id Identificador del producto
     * @return Producto encontrado, o null si no existe
     * @throws SQLException si ocurre un error en la base de datos
     */
    public Producto buscarPorId(int id) throws SQLException {
        String sql = "SELECT id, nombre, precio, stock, descripcion FROM productos WHERE id = ?";

        try (Connection con = Conexion.getInstancia();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearProducto(rs);
                }
            }
        }
        return null;
    }

    /**
     * Inserta un nuevo producto en la base de datos.
     *
     * @param producto Objeto Producto con los datos a insertar
     * @return true si la insercion fue exitosa
     * @throws SQLException si ocurre un error en la base de datos
     */
    public boolean insertar(Producto producto) throws SQLException {
        String sql = "INSERT INTO productos (nombre, precio, stock, descripcion, activo) "
                   + "VALUES (?, ?, ?, ?, 1)";

        try (Connection con = Conexion.getInstancia();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, producto.getNombre());
            ps.setDouble(2, producto.getPrecio());
            ps.setInt(3, producto.getStock());
            ps.setString(4, producto.getDescripcion());

            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Actualiza los datos de un producto existente.
     *
     * @param producto Objeto Producto con los datos actualizados
     * @return true si la actualizacion fue exitosa
     * @throws SQLException si ocurre un error en la base de datos
     */
    public boolean actualizar(Producto producto) throws SQLException {
        String sql = "UPDATE productos SET nombre = ?, precio = ?, stock = ?, descripcion = ? "
                   + "WHERE id = ?";

        try (Connection con = Conexion.getInstancia();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, producto.getNombre());
            ps.setDouble(2, producto.getPrecio());
            ps.setInt(3, producto.getStock());
            ps.setString(4, producto.getDescripcion());
            ps.setInt(5, producto.getId());

            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Descuenta stock de un producto tras una venta.
     *
     * @param idProducto Identificador del producto
     * @param cantidad   Unidades a descontar
     * @return true si el descuento fue exitoso
     * @throws SQLException si ocurre un error en la base de datos
     */
    public boolean descontarStock(int idProducto, int cantidad) throws SQLException {
        String sql = "UPDATE productos SET stock = stock - ? WHERE id = ? AND stock >= ?";

        try (Connection con = Conexion.getInstancia();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, cantidad);
            ps.setInt(2, idProducto);
            ps.setInt(3, cantidad);

            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Desactiva un producto (baja logica).
     *
     * @param id Identificador del producto
     * @return true si la operacion fue exitosa
     * @throws SQLException si ocurre un error en la base de datos
     */
    public boolean desactivar(int id) throws SQLException {
        String sql = "UPDATE productos SET activo = 0 WHERE id = ?";

        try (Connection con = Conexion.getInstancia();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Mapea una fila del ResultSet a un objeto Producto.
     */
    private Producto mapearProducto(ResultSet rs) throws SQLException {
        return new Producto(
            rs.getInt("id"),
            rs.getString("nombre"),
            rs.getDouble("precio"),
            rs.getInt("stock"),
            rs.getString("descripcion")
        );
    }
}
