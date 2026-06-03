package controlador;

import dao.ReporteDAO;
import util.Validador;

import java.sql.SQLException;
import java.util.Map;

/**
 * Controlador del modulo de reportes.
 * Delega al ReporteDAO las consultas agrupadas y gestiona la validacion de fechas.
 *
 * @author Luiggi
 */
public class ReportesController {

    private final ReporteDAO reporteDAO;

    public ReportesController() {
        this.reporteDAO = new ReporteDAO();
    }

    /**
     * Retorna las ventas totales por dia en el rango indicado.
     *
     * @param desde Fecha de inicio (yyyy-MM-dd)
     * @param hasta Fecha de fin (yyyy-MM-dd)
     * @return Mapa de fecha -> total, o mapa vacio si hay error
     */
    public Map<String, Double> getVentasPorDia(String desde, String hasta) {
        try {
            return reporteDAO.ventasPorDia(desde, hasta);
        } catch (SQLException e) {
            System.err.println("Error al generar reporte por dia: " + e.getMessage());
            return Map.of();
        }
    }

    /**
     * Retorna los 10 productos mas vendidos en el rango indicado.
     *
     * @param desde Fecha de inicio (yyyy-MM-dd)
     * @param hasta Fecha de fin (yyyy-MM-dd)
     * @return Mapa de producto -> cantidad vendida
     */
    public Map<String, Integer> getProductosMasVendidos(String desde, String hasta) {
        try {
            return reporteDAO.productosMasVendidos(desde, hasta);
        } catch (SQLException e) {
            System.err.println("Error al generar reporte de productos: " + e.getMessage());
            return Map.of();
        }
    }

    /**
     * Retorna el total vendido por cada vendedor en el rango indicado.
     *
     * @param desde Fecha de inicio (yyyy-MM-dd)
     * @param hasta Fecha de fin (yyyy-MM-dd)
     * @return Mapa de vendedor -> monto total
     */
    public Map<String, Double> getVentasPorVendedor(String desde, String hasta) {
        try {
            return reporteDAO.ventasPorVendedor(desde, hasta);
        } catch (SQLException e) {
            System.err.println("Error al generar reporte por vendedor: " + e.getMessage());
            return Map.of();
        }
    }

    /**
     * Valida que ambas fechas esten presentes y que "desde" no sea mayor a "hasta".
     *
     * @param desde Fecha de inicio
     * @param hasta Fecha de fin
     * @return Mensaje de error si son invalidas, null si son correctas
     */
    public String validarRango(String desde, String hasta) {
        if (!Validador.noEstaVacio(desde) || !Validador.noEstaVacio(hasta)) {
            return "Seleccione ambas fechas del rango.";
        }
        if (desde.compareTo(hasta) > 0) {
            return "La fecha de inicio no puede ser mayor a la fecha de fin.";
        }
        return null;
    }
}
