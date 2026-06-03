package modelo;

/**
 * Entidad que representa un insumo o materia prima utilizada en produccion.
 * Ejemplo: Leche (litros), Azucar (kg), Fresas (kg).
 *
 * @author Luiggi
 */
public class Insumo {

    /** Identificador unico del insumo */
    private int id;

    /** Nombre del insumo */
    private String nombre;

    /** Unidad de medida: kg, litros, unidades, etc. */
    private String unidad;

    /** Cantidad actual en almacen */
    private double stock;

    /** Cantidad minima requerida antes de generar alerta */
    private double stockMinimo;

    /** Constructor vacio requerido por los DAOs */
    public Insumo() {}

    /**
     * Constructor completo para instanciar desde la base de datos.
     *
     * @param id          Identificador unico
     * @param nombre      Nombre del insumo
     * @param unidad      Unidad de medida
     * @param stock       Stock actual
     * @param stockMinimo Stock minimo permitido
     */
    public Insumo(int id, String nombre, String unidad, double stock, double stockMinimo) {
        this.id = id;
        this.nombre = nombre;
        this.unidad = unidad;
        this.stock = stock;
        this.stockMinimo = stockMinimo;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getUnidad() { return unidad; }
    public void setUnidad(String unidad) { this.unidad = unidad; }

    public double getStock() { return stock; }
    public void setStock(double stock) { this.stock = stock; }

    public double getStockMinimo() { return stockMinimo; }
    public void setStockMinimo(double stockMinimo) { this.stockMinimo = stockMinimo; }

    /**
     * Indica si el stock actual esta por debajo del minimo requerido.
     *
     * @return true si se necesita reabastecer
     */
    public boolean necesitaReabastecimiento() {
        return stock <= stockMinimo;
    }

    @Override
    public String toString() {
        return nombre + " (" + unidad + ") — Stock: " + stock;
    }
}
