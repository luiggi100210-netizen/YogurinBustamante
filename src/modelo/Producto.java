package modelo;

/**
 * Entidad que representa un producto terminado de la empresa.
 * Ejemplo: Yogur de fresa 1L, Yogur natural 500ml.
 *
 * @author Luiggi
 */
public class Producto {

    /** Identificador unico del producto */
    private int id;

    /** Nombre del producto */
    private String nombre;

    /** Precio unitario de venta */
    private double precio;

    /** Unidades disponibles en inventario */
    private int stock;

    /** Descripcion breve del producto */
    private String descripcion;

    /** Constructor vacio requerido por los DAOs */
    public Producto() {}

    /**
     * Constructor completo para instanciar desde la base de datos.
     *
     * @param id          Identificador unico
     * @param nombre      Nombre del producto
     * @param precio      Precio de venta unitario
     * @param stock       Unidades en inventario
     * @param descripcion Descripcion del producto
     */
    public Producto(int id, String nombre, double precio, int stock, String descripcion) {
        this.id = id;
        this.nombre = nombre;
        this.precio = precio;
        this.stock = stock;
        this.descripcion = descripcion;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public double getPrecio() { return precio; }
    public void setPrecio(double precio) { this.precio = precio; }

    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    @Override
    public String toString() {
        return nombre + " — S/ " + String.format("%.2f", precio);
    }
}
