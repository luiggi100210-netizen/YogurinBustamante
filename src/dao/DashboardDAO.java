package dao;

import modelo.DashboardData;
import util.Conexion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * DAO exclusivo del panel principal (Dashboard).
 * Ejecuta las cuatro consultas de indicadores usando una sola conexion
 * para minimizar el overhead y cumplir con el principio de responsabilidad unica.
 *
 * @author Luiggi
 * @version 1.0
 * @since 2026
 */
public class DashboardDAO {

    /**
     * Obtiene los cuatro indicadores del dashboard del dia actual.
     * Todas las consultas se ejecutan sobre la misma conexion Singleton.
     * Los PreparedStatements se cierran individualmente con try-with-resources.
     *
     * @return {@link DashboardData} con los indicadores del dia
     * @throws SQLException si ocurre un error de acceso a la base de datos
     */
    public DashboardData obtenerDatos() throws SQLException {
        Connection conn = Conexion.getInstancia();

        return new DashboardData(
            consultarTotalVentasHoy(conn),
            consultarNumVentasHoy(conn),
            consultarUnidadesProducidasHoy(conn),
            consultarAlertasStock(conn)
        );
    }

    // ── Consultas privadas ────────────────────────────────────────────────────

    /**
     * Suma el total en soles de todas las ventas registradas hoy.
     *
     * @param conn Conexion activa a la base de datos
     * @return Total de ventas del dia, o 0.0 si no hay ventas
     * @throws SQLException si ocurre un error en la consulta
     */
    private double consultarTotalVentasHoy(Connection conn) throws SQLException {
        String sql = "SELECT COALESCE(SUM(total), 0) FROM ventas WHERE DATE(fecha) = CURDATE()";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getDouble(1) : 0.0;
        }
    }

    /**
     * Cuenta el numero de transacciones de venta realizadas hoy.
     *
     * @param conn Conexion activa a la base de datos
     * @return Cantidad de ventas registradas hoy
     * @throws SQLException si ocurre un error en la consulta
     */
    private int consultarNumVentasHoy(Connection conn) throws SQLException {
        String sql = "SELECT COUNT(*) FROM ventas WHERE DATE(fecha) = CURDATE()";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    /**
     * Suma las unidades de producto producidas en lotes del dia actual.
     *
     * @param conn Conexion activa a la base de datos
     * @return Total de unidades producidas hoy
     * @throws SQLException si ocurre un error en la consulta
     */
    private int consultarUnidadesProducidasHoy(Connection conn) throws SQLException {
        String sql = "SELECT COALESCE(SUM(unidades), 0) FROM lotes_produccion WHERE DATE(fecha) = CURDATE()";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    /**
     * Cuenta los insumos activos con stock por debajo del minimo establecido.
     *
     * @param conn Conexion activa a la base de datos
     * @return Cantidad de insumos con stock critico
     * @throws SQLException si ocurre un error en la consulta
     */
    private int consultarAlertasStock(Connection conn) throws SQLException {
        String sql = "SELECT COUNT(*) FROM insumos WHERE stock < stock_minimo AND activo = 1";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }
}
