package dao;

import modelo.Cliente;
import modelo.DetalleVenta;
import modelo.Producto;
import modelo.Usuario;
import modelo.Venta;
import util.Conexion;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO (Data Access Object) para la entidad Venta.
 * Gestiona el registro de ventas, detalles y el descuento de stock.
 * Usa transacciones para garantizar consistencia de datos.
 *
 * @author Luiggi
 */
public class VentaDAO {

    /**
     * Registra una venta completa con todos sus detalles.
     * Opera dentro de una transaccion: si algo falla, revierte todo.
     *
     * @param venta    Cabecera de la venta
     * @param detalles Lista de productos y cantidades vendidas
     * @return true si la venta se registro correctamente
     * @throws SQLException si ocurre un error en la base de datos
     */
    public boolean registrarVenta(Venta venta, List<DetalleVenta> detalles) throws SQLException {
        Connection con = Conexion.getInstancia();
        con.setAutoCommit(false);

        try {
            int idVenta = insertarCabecera(con, venta);
            insertarDetalles(con, idVenta, detalles);
            descontarStockProductos(con, detalles);

            con.commit();
            return true;

        } catch (SQLException e) {
            con.rollback();
            throw e;
        } finally {
            con.setAutoCommit(true);
        }
    }

    /**
     * Retorna el historial de ventas del dia actual.
     *
     * @return Lista de ventas de hoy
     * @throws SQLException si ocurre un error en la base de datos
     */
    public List<Venta> listarVentasHoy() throws SQLException {
        List<Venta> lista = new ArrayList<>();
        String sql = "SELECT v.id, v.fecha, v.total, "
                   + "c.id AS cid, c.nombre AS cnombre, c.dni, c.telefono, "
                   + "u.id AS uid, u.nombre AS unombre, u.usuario, u.clave, u.rol "
                   + "FROM ventas v "
                   + "JOIN clientes c ON v.id_cliente = c.id "
                   + "JOIN usuarios u ON v.id_usuario = u.id "
                   + "WHERE DATE(v.fecha) = CURDATE() "
                   + "ORDER BY v.fecha DESC";

        try (Connection con = Conexion.getInstancia();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                lista.add(mapearVenta(rs));
            }
        }
        return lista;
    }

    /**
     * Retorna las ventas dentro de un rango de fechas.
     *
     * @param desde Fecha de inicio (formato: yyyy-MM-dd)
     * @param hasta Fecha de fin (formato: yyyy-MM-dd)
     * @return Lista de ventas en el rango dado
     * @throws SQLException si ocurre un error en la base de datos
     */
    public List<Venta> listarPorRango(String desde, String hasta) throws SQLException {
        List<Venta> lista = new ArrayList<>();
        String sql = "SELECT v.id, v.fecha, v.total, "
                   + "c.id AS cid, c.nombre AS cnombre, c.dni, c.telefono, "
                   + "u.id AS uid, u.nombre AS unombre, u.usuario, u.clave, u.rol "
                   + "FROM ventas v "
                   + "JOIN clientes c ON v.id_cliente = c.id "
                   + "JOIN usuarios u ON v.id_usuario = u.id "
                   + "WHERE DATE(v.fecha) BETWEEN ? AND ? "
                   + "ORDER BY v.fecha DESC";

        try (Connection con = Conexion.getInstancia();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, desde);
            ps.setString(2, hasta);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearVenta(rs));
                }
            }
        }
        return lista;
    }

    /**
     * Calcula el total de ventas del dia actual.
     *
     * @return Suma de todos los totales del dia
     * @throws SQLException si ocurre un error en la base de datos
     */
    public double getTotalVentasHoy() throws SQLException {
        String sql = "SELECT COALESCE(SUM(total), 0) AS total_dia FROM ventas "
                   + "WHERE DATE(fecha) = CURDATE()";

        try (Connection con = Conexion.getInstancia();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getDouble("total_dia");
            }
        }
        return 0.0;
    }

    /**
     * Retorna el historial de compras de un cliente especifico.
     *
     * @param idCliente Identificador del cliente
     * @return Lista de ventas realizadas por ese cliente, ordenadas por fecha descendente
     * @throws SQLException si ocurre un error en la base de datos
     */
    public List<Venta> listarPorCliente(int idCliente) throws SQLException {
        List<Venta> lista = new ArrayList<>();
        String sql = "SELECT v.id, v.fecha, v.total, "
                   + "c.id AS cid, c.nombre AS cnombre, c.dni, c.telefono, "
                   + "u.id AS uid, u.nombre AS unombre, u.usuario, u.clave, u.rol "
                   + "FROM ventas v "
                   + "JOIN clientes c ON v.id_cliente = c.id "
                   + "JOIN usuarios u ON v.id_usuario = u.id "
                   + "WHERE v.id_cliente = ? "
                   + "ORDER BY v.fecha DESC";

        try (Connection con = Conexion.getInstancia();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idCliente);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearVenta(rs));
                }
            }
        }
        return lista;
    }

    // -------------------------------------------------------------------------
    // Metodos privados de apoyo
    // -------------------------------------------------------------------------

    /** Inserta la cabecera de la venta y retorna el ID generado. */
    private int insertarCabecera(Connection con, Venta venta) throws SQLException {
        String sql = "INSERT INTO ventas (fecha, total, id_cliente, id_usuario) "
                   + "VALUES (NOW(), ?, ?, ?)";

        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setDouble(1, venta.getTotal());
            ps.setInt(2, venta.getCliente().getId());
            ps.setInt(3, venta.getUsuario().getId());
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        }
        throw new SQLException("No se pudo obtener el ID de la venta registrada.");
    }

    /** Inserta cada linea de detalle de la venta. */
    private void insertarDetalles(Connection con, int idVenta, List<DetalleVenta> detalles)
            throws SQLException {
        String sql = "INSERT INTO detalle_ventas (id_venta, id_producto, cantidad, subtotal) "
                   + "VALUES (?, ?, ?, ?)";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            for (DetalleVenta detalle : detalles) {
                ps.setInt(1, idVenta);
                ps.setInt(2, detalle.getProducto().getId());
                ps.setInt(3, detalle.getCantidad());
                ps.setDouble(4, detalle.getSubtotal());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    /**
     * Descuenta el stock de cada producto vendido.
     * Si algun producto no tiene stock suficiente, el UPDATE no afecta
     * ninguna fila y se lanza una excepcion para revertir toda la venta.
     */
    private void descontarStockProductos(Connection con, List<DetalleVenta> detalles)
            throws SQLException {
        String sql = "UPDATE productos SET stock = stock - ? WHERE id = ? AND stock >= ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            for (DetalleVenta detalle : detalles) {
                ps.setInt(1, detalle.getCantidad());
                ps.setInt(2, detalle.getProducto().getId());
                ps.setInt(3, detalle.getCantidad());
                ps.addBatch();
            }
            int[] filasAfectadas = ps.executeBatch();

            for (int i = 0; i < filasAfectadas.length; i++) {
                if (filasAfectadas[i] == 0) {
                    throw new SQLException("Stock insuficiente para el producto: "
                            + detalles.get(i).getProducto().getNombre());
                }
            }
        }
    }

    /** Mapea una fila del ResultSet a un objeto Venta con Cliente y Usuario. */
    private Venta mapearVenta(ResultSet rs) throws SQLException {
        Cliente cliente = new Cliente(
            rs.getInt("cid"), rs.getString("cnombre"),
            rs.getString("dni"), rs.getString("telefono")
        );
        Usuario usuario = new Usuario(
            rs.getInt("uid"), rs.getString("unombre"),
            rs.getString("usuario"), rs.getString("clave"), rs.getString("rol")
        );
        return new Venta(
            rs.getInt("id"),
            rs.getTimestamp("fecha"),
            rs.getDouble("total"),
            cliente,
            usuario
        );
    }
}
