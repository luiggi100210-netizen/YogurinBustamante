package dao;

import modelo.Insumo;
import util.Conexion;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO (Data Access Object) para la entidad Insumo.
 * Gestiona todas las operaciones CRUD en la tabla "insumos".
 *
 * @author Luiggi
 */
public class InsumoDAO {

    /**
     * Retorna la lista completa de insumos activos.
     *
     * @return Lista de insumos
     * @throws SQLException si ocurre un error en la base de datos
     */
    public List<Insumo> listarTodos() throws SQLException {
        List<Insumo> lista = new ArrayList<>();
        String sql = "SELECT id, nombre, unidad, stock, stock_minimo FROM insumos "
                   + "WHERE activo = 1 ORDER BY nombre";

        try (Connection con = Conexion.getInstancia();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                lista.add(mapearInsumo(rs));
            }
        }
        return lista;
    }

    /**
     * Retorna los insumos con stock por debajo del minimo requerido.
     *
     * @return Lista de insumos con stock critico
     * @throws SQLException si ocurre un error en la base de datos
     */
    public List<Insumo> listarBajoStock() throws SQLException {
        List<Insumo> lista = new ArrayList<>();
        String sql = "SELECT id, nombre, unidad, stock, stock_minimo FROM insumos "
                   + "WHERE activo = 1 AND stock <= stock_minimo ORDER BY stock ASC";

        try (Connection con = Conexion.getInstancia();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                lista.add(mapearInsumo(rs));
            }
        }
        return lista;
    }

    /**
     * Inserta un nuevo insumo en la base de datos.
     *
     * @param insumo Objeto Insumo con los datos a insertar
     * @return true si la insercion fue exitosa
     * @throws SQLException si ocurre un error en la base de datos
     */
    public boolean insertar(Insumo insumo) throws SQLException {
        String sql = "INSERT INTO insumos (nombre, unidad, stock, stock_minimo, activo) "
                   + "VALUES (?, ?, ?, ?, 1)";

        try (Connection con = Conexion.getInstancia();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, insumo.getNombre());
            ps.setString(2, insumo.getUnidad());
            ps.setDouble(3, insumo.getStock());
            ps.setDouble(4, insumo.getStockMinimo());

            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Actualiza los datos de un insumo existente.
     *
     * @param insumo Objeto Insumo con los datos actualizados
     * @return true si la actualizacion fue exitosa
     * @throws SQLException si ocurre un error en la base de datos
     */
    public boolean actualizar(Insumo insumo) throws SQLException {
        String sql = "UPDATE insumos SET nombre = ?, unidad = ?, stock = ?, stock_minimo = ? "
                   + "WHERE id = ?";

        try (Connection con = Conexion.getInstancia();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, insumo.getNombre());
            ps.setString(2, insumo.getUnidad());
            ps.setDouble(3, insumo.getStock());
            ps.setDouble(4, insumo.getStockMinimo());
            ps.setInt(5, insumo.getId());

            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Descuenta la cantidad usada de un insumo tras un lote de produccion.
     *
     * @param idInsumo  Identificador del insumo
     * @param cantidad  Cantidad a descontar
     * @return true si el descuento fue exitoso
     * @throws SQLException si ocurre un error en la base de datos
     */
    public boolean descontarStock(int idInsumo, double cantidad) throws SQLException {
        String sql = "UPDATE insumos SET stock = stock - ? WHERE id = ? AND stock >= ?";

        try (Connection con = Conexion.getInstancia();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setDouble(1, cantidad);
            ps.setInt(2, idInsumo);
            ps.setDouble(3, cantidad);

            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Desactiva un insumo (baja logica).
     *
     * @param id Identificador del insumo
     * @return true si la operacion fue exitosa
     * @throws SQLException si ocurre un error en la base de datos
     */
    public boolean desactivar(int id) throws SQLException {
        String sql = "UPDATE insumos SET activo = 0 WHERE id = ?";

        try (Connection con = Conexion.getInstancia();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Mapea una fila del ResultSet a un objeto Insumo.
     */
    private Insumo mapearInsumo(ResultSet rs) throws SQLException {
        return new Insumo(
            rs.getInt("id"),
            rs.getString("nombre"),
            rs.getString("unidad"),
            rs.getDouble("stock"),
            rs.getDouble("stock_minimo")
        );
    }
}
