package modelo;

import java.util.Date;
import java.util.Map;

/**
 * Entidad que representa un lote de produccion.
 * Registra la fecha, el producto elaborado, las unidades producidas
 * y los insumos utilizados con sus respectivas cantidades.
 *
 * @author Luiggi
 */
public class LoteProduccion {

    /** Identificador unico del lote */
    private int id;

    /** Fecha en que se realizo la produccion */
    private Date fecha;

    /** Producto elaborado en este lote */
    private Producto producto;

    /** Cantidad de unidades producidas */
    private int unidades;

    /**
     * Mapa de insumos utilizados: clave = Insumo, valor = cantidad usada.
     * Representa los insumos descontados del inventario.
     */
    private Map<Insumo, Double> insumosUsados;

    /** Constructor vacio requerido por los DAOs */
    public LoteProduccion() {}

    /**
     * Constructor completo para instanciar desde la base de datos.
     *
     * @param id           Identificador unico
     * @param fecha        Fecha del lote
     * @param producto     Producto fabricado
     * @param unidades     Cantidad producida
     * @param insumosUsados Mapa de insumos y cantidades usadas
     */
    public LoteProduccion(int id, Date fecha, Producto producto, int unidades,
                          Map<Insumo, Double> insumosUsados) {
        this.id = id;
        this.fecha = fecha;
        this.producto = producto;
        this.unidades = unidades;
        this.insumosUsados = insumosUsados;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Date getFecha() { return fecha; }
    public void setFecha(Date fecha) { this.fecha = fecha; }

    public Producto getProducto() { return producto; }
    public void setProducto(Producto producto) { this.producto = producto; }

    public int getUnidades() { return unidades; }
    public void setUnidades(int unidades) { this.unidades = unidades; }

    public Map<Insumo, Double> getInsumosUsados() { return insumosUsados; }
    public void setInsumosUsados(Map<Insumo, Double> insumosUsados) {
        this.insumosUsados = insumosUsados;
    }

    @Override
    public String toString() {
        return "Lote #" + id + " — " + producto.getNombre() + " x" + unidades + " uds.";
    }
}
