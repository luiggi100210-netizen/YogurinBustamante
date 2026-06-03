package dao;

import modelo.Insumo;
import modelo.LoteProduccion;
import util.Conexion;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * DAO (Data Access Object) para la entidad LoteProduccion.
 * Registra lotes de produccion y descuenta insumos usados.
 * Usa transacciones para garantizar consistencia de datos.
 *
 * @author Luiggi
 */
public class ProduccionDAO {

    /**
     * Registra un nuevo lote de produccion y descuenta los insumos utilizados.
     * Opera dentro de una transaccion atomica.
     *
     * @param lote Objeto LoteProduccion con el producto, unidades e insumos usados
     * @return true si el registro fue exitoso
     * @throws SQLException si ocurre un error en la base de datos
     */
    public boolean registrarLote(LoteProduccion lote) throws SQLException {
        Connection con = Conexion.getInstancia();
        con.setAutoCommit(false);

        try {
            int idLote = insertarLote(con, lote);
            registrarInsumosUsados(con, idLote, lote.getInsumosUsados());
            descontarInsumos(con, lote.getInsumosUsados());
            incrementarStockProducto(con, lote.getProducto().getId(), lote.getUnidades());

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
     * Retorna el historial de lotes de produccion ordenados por fecha descendente.
     *
     * @return Lista de lotes registrados (sin insumos detallados)
     * @throws SQLException si ocurre un error en la base de datos
     */
    public List<LoteProduccion> listarTodos() throws SQLException {
        List<LoteProduccion> lista = new ArrayList<>();
        String sql = "SELECT lp.id, lp.fecha, lp.unidades, "
                   + "p.id AS pid, p.nombre AS pnombre, p.precio, p.stock, p.descripcion "
                   + "FROM lotes_produccion lp "
                   + "JOIN productos p ON lp.id_producto = p.id "
                   + "ORDER BY lp.fecha DESC";

        try (Connection con = Conexion.getInstancia();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                lista.add(mapearLote(rs));
            }
        }
        return lista;
    }

    // -------------------------------------------------------------------------
    // Metodos privados de apoyo
    // -------------------------------------------------------------------------

    /** Inserta la cabecera del lote y retorna el ID generado. */
    private int insertarLote(Connection con, LoteProduccion lote) throws SQLException {
        String sql = "INSERT INTO lotes_produccion (fecha, id_producto, unidades) "
                   + "VALUES (NOW(), ?, ?)";

        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, lote.getProducto().getId());
            ps.setInt(2, lote.getUnidades());
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        }
        throw new SQLException("No se pudo obtener el ID del lote registrado.");
    }

    /** Registra el detalle de insumos usados en el lote. */
    private void registrarInsumosUsados(Connection con, int idLote,
            Map<Insumo, Double> insumosUsados) throws SQLException {
        String sql = "INSERT INTO lote_insumos (id_lote, id_insumo, cantidad) VALUES (?, ?, ?)";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            for (Map.Entry<Insumo, Double> entrada : insumosUsados.entrySet()) {
                ps.setInt(1, idLote);
                ps.setInt(2, entrada.getKey().getId());
                ps.setDouble(3, entrada.getValue());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    /** Descuenta los insumos usados del inventario. */
    private void descontarInsumos(Connection con, Map<Insumo, Double> insumosUsados)
            throws SQLException {
        String sql = "UPDATE insumos SET stock = stock - ? WHERE id = ? AND stock >= ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            for (Map.Entry<Insumo, Double> entrada : insumosUsados.entrySet()) {
                ps.setDouble(1, entrada.getValue());
                ps.setInt(2, entrada.getKey().getId());
                ps.setDouble(3, entrada.getValue());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    /** Incrementa el stock del producto fabricado. */
    private void incrementarStockProducto(Connection con, int idProducto, int unidades)
            throws SQLException {
        String sql = "UPDATE productos SET stock = stock + ? WHERE id = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, unidades);
            ps.setInt(2, idProducto);
            ps.executeUpdate();
        }
    }

    /** Mapea una fila del ResultSet a un LoteProduccion basico (sin insumos). */
    private LoteProduccion mapearLote(ResultSet rs) throws SQLException {
        modelo.Producto producto = new modelo.Producto(
            rs.getInt("pid"), rs.getString("pnombre"),
            rs.getDouble("precio"), rs.getInt("stock"), rs.getString("descripcion")
        );
        return new LoteProduccion(
            rs.getInt("id"),
            rs.getTimestamp("fecha"),
            producto,
            rs.getInt("unidades"),
            null
        );
    }
}
