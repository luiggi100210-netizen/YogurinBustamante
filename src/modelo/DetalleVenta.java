package modelo;

/**
 * Entidad que representa el detalle de un producto dentro de una venta.
 * Una venta puede tener multiples detalles (uno por producto vendido).
 *
 * @author Luiggi
 */
public class DetalleVenta {

    /** Identificador unico del detalle */
    private int id;

    /** Venta a la que pertenece este detalle */
    private Venta venta;

    /** Producto incluido en este detalle */
    private Producto producto;

    /** Cantidad de unidades vendidas */
    private int cantidad;

    /** Subtotal = precio unitario x cantidad */
    private double subtotal;

    /** Constructor vacio requerido por los DAOs */
    public DetalleVenta() {}

    /**
     * Constructor completo para instanciar desde la base de datos.
     *
     * @param id       Identificador unico
     * @param venta    Venta contenedora
     * @param producto Producto vendido
     * @param cantidad Unidades vendidas
     * @param subtotal Precio total del detalle
     */
    public DetalleVenta(int id, Venta venta, Producto producto, int cantidad, double subtotal) {
        this.id = id;
        this.venta = venta;
        this.producto = producto;
        this.cantidad = cantidad;
        this.subtotal = subtotal;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Venta getVenta() { return venta; }
    public void setVenta(Venta venta) { this.venta = venta; }

    public Producto getProducto() { return producto; }
    public void setProducto(Producto producto) { this.producto = producto; }

    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }

    public double getSubtotal() { return subtotal; }
    public void setSubtotal(double subtotal) { this.subtotal = subtotal; }

    /**
     * Calcula el subtotal en base al precio del producto y la cantidad.
     * Actualiza el campo subtotal automaticamente.
     */
    public void calcularSubtotal() {
        if (producto != null) {
            this.subtotal = producto.getPrecio() * cantidad;
        }
    }

    @Override
    public String toString() {
        return producto.getNombre() + " x" + cantidad + " = S/ " + String.format("%.2f", subtotal);
    }
}
