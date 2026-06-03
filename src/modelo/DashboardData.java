package modelo;

/**
 * Objeto de transferencia de datos (DTO) para los indicadores del panel principal.
 * Agrupa las cuatro metricas de resumen del dia que se muestran en el dashboard.
 * No contiene logica de negocio — solo atributos, constructor y accesores.
 *
 * @author Luiggi
 * @version 1.0
 * @since 2026
 */
public class DashboardData {

    /** Total en soles de ventas realizadas hoy */
    private double ventasDelDia;

    /** Cantidad de transacciones de venta registradas hoy */
    private int ventasRegistradas;

    /** Unidades de producto producidas en lotes del dia */
    private int unidadesProducidas;

    /** Cantidad de insumos con stock por debajo del minimo */
    private int alertasStock;

    /** Constructor vacio requerido para instanciacion por defecto con valores en cero */
    public DashboardData() {}

    /**
     * Constructor completo con los cuatro indicadores.
     *
     * @param ventasDelDia       Total en soles vendido hoy
     * @param ventasRegistradas  Numero de ventas del dia
     * @param unidadesProducidas Unidades producidas en lotes del dia
     * @param alertasStock       Insumos con stock critico
     */
    public DashboardData(double ventasDelDia, int ventasRegistradas,
                         int unidadesProducidas, int alertasStock) {
        this.ventasDelDia       = ventasDelDia;
        this.ventasRegistradas  = ventasRegistradas;
        this.unidadesProducidas = unidadesProducidas;
        this.alertasStock       = alertasStock;
    }

    // ── Getters ───────────────────────────────────────────────────────────────

    /** @return Total de ventas del dia en soles */
    public double getVentasDelDia()       { return ventasDelDia; }

    /** @return Numero de ventas registradas hoy */
    public int    getVentasRegistradas()  { return ventasRegistradas; }

    /** @return Unidades producidas en lotes del dia */
    public int    getUnidadesProducidas() { return unidadesProducidas; }

    /** @return Cantidad de alertas de stock critico */
    public int    getAlertasStock()       { return alertasStock; }

    // ── Setters ───────────────────────────────────────────────────────────────

    /** @param ventasDelDia Total de ventas del dia en soles */
    public void setVentasDelDia(double ventasDelDia)          { this.ventasDelDia = ventasDelDia; }

    /** @param ventasRegistradas Numero de ventas registradas */
    public void setVentasRegistradas(int ventasRegistradas)   { this.ventasRegistradas = ventasRegistradas; }

    /** @param unidadesProducidas Unidades producidas hoy */
    public void setUnidadesProducidas(int unidadesProducidas) { this.unidadesProducidas = unidadesProducidas; }

    /** @param alertasStock Cantidad de alertas de stock */
    public void setAlertasStock(int alertasStock)             { this.alertasStock = alertasStock; }
}
