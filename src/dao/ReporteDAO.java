package dao;

import util.Conexion;

import java.sql.*;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * DAO (Data Access Object) para generacion de reportes.
 * Contiene consultas agrupadas por fecha, producto y usuario.
 *
 * @author Luiggi
 */
public class ReporteDAO {

    /**
     * Retorna el total de ventas agrupado por dia en un rango de fechas.
     * Clave: fecha (yyyy-MM-dd), Valor: total vendido ese dia.
     *
     * @param desde Fecha de inicio (formato: yyyy-MM-dd)
     * @param hasta Fecha de fin (formato: yyyy-MM-dd)
     * @return Mapa ordenado de fecha -> total
     * @throws SQLException si ocurre un error en la base de datos
     */
    public Map<String, Double> ventasPorDia(String desde, String hasta) throws SQLException {
        Map<String, Double> resultado = new LinkedHashMap<>();
        String sql = "SELECT DATE(fecha) AS dia, SUM(total) AS total_dia "
                   + "FROM ventas WHERE DATE(fecha) BETWEEN ? AND ? "
                   + "GROUP BY DATE(fecha) ORDER BY dia ASC";

        try (Connection con = Conexion.getInstancia();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, desde);
            ps.setString(2, hasta);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    resultado.put(rs.getString("dia"), rs.getDouble("total_dia"));
                }
            }
        }
        return resultado;
    }

    /**
     * Retorna los productos mas vendidos en un rango de fechas.
     * Clave: nombre del producto, Valor: total de unidades vendidas.
     *
     * @param desde Fecha de inicio
     * @param hasta Fecha de fin
     * @return Mapa ordenado de producto -> cantidad vendida
     * @throws SQLException si ocurre un error en la base de datos
     */
    public Map<String, Integer> productosMasVendidos(String desde, String hasta)
            throws SQLException {
        Map<String, Integer> resultado = new LinkedHashMap<>();
        String sql = "SELECT p.nombre, SUM(dv.cantidad) AS total_vendido "
                   + "FROM detalle_ventas dv "
                   + "JOIN productos p ON dv.id_producto = p.id "
                   + "JOIN ventas v ON dv.id_venta = v.id "
                   + "WHERE DATE(v.fecha) BETWEEN ? AND ? "
                   + "GROUP BY p.nombre ORDER BY total_vendido DESC LIMIT 10";

        try (Connection con = Conexion.getInstancia();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, desde);
            ps.setString(2, hasta);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    resultado.put(rs.getString("nombre"), rs.getInt("total_vendido"));
                }
            }
        }
        return resultado;
    }

    /**
     * Retorna el total vendido por cada usuario (vendedor) en un rango de fechas.
     * Clave: nombre del usuario, Valor: total vendido.
     *
     * @param desde Fecha de inicio
     * @param hasta Fecha de fin
     * @return Mapa de vendedor -> monto total
     * @throws SQLException si ocurre un error en la base de datos
     */
    public Map<String, Double> ventasPorVendedor(String desde, String hasta) throws SQLException {
        Map<String, Double> resultado = new LinkedHashMap<>();
        String sql = "SELECT u.nombre, SUM(v.total) AS total_vendedor "
                   + "FROM ventas v "
                   + "JOIN usuarios u ON v.id_usuario = u.id "
                   + "WHERE DATE(v.fecha) BETWEEN ? AND ? "
                   + "GROUP BY u.nombre ORDER BY total_vendedor DESC";

        try (Connection con = Conexion.getInstancia();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, desde);
            ps.setString(2, hasta);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    resultado.put(rs.getString("nombre"), rs.getDouble("total_vendedor"));
                }
            }
        }
        return resultado;
    }

    /**
     * Retorna el resumen del cierre de caja del dia actual.
     * Incluye total de ventas, numero de transacciones y monto total.
     *
     * @return Arreglo con: [0]=num_ventas, [1]=total_dia
     * @throws SQLException si ocurre un error en la base de datos
     */
    public double[] resumenCierreCaja() throws SQLException {
        String sql = "SELECT COUNT(*) AS num_ventas, COALESCE(SUM(total), 0) AS total_dia "
                   + "FROM ventas WHERE DATE(fecha) = CURDATE()";

        try (Connection con = Conexion.getInstancia();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            if (rs.next()) {
                return new double[]{rs.getInt("num_ventas"), rs.getDouble("total_dia")};
            }
        }
        return new double[]{0, 0};
    }
}
