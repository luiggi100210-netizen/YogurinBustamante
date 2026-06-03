package modelo;

import java.util.Date;

/**
 * Entidad que representa una venta realizada.
 * Cada venta esta asociada a un cliente y al usuario que la registro.
 *
 * @author Luiggi
 */
public class Venta {

    /** Identificador unico de la venta */
    private int id;

    /** Fecha y hora de la venta */
    private Date fecha;

    /** Monto total de la venta */
    private double total;

    /** Cliente al que se le realizo la venta */
    private Cliente cliente;

    /** Usuario (vendedor) que registro la venta */
    private Usuario usuario;

    /** Constructor vacio requerido por los DAOs */
    public Venta() {}

    /**
     * Constructor completo para instanciar desde la base de datos.
     *
     * @param id      Identificador unico
     * @param fecha   Fecha de la venta
     * @param total   Monto total
     * @param cliente Cliente comprador
     * @param usuario Vendedor registrador
     */
    public Venta(int id, Date fecha, double total, Cliente cliente, Usuario usuario) {
        this.id = id;
        this.fecha = fecha;
        this.total = total;
        this.cliente = cliente;
        this.usuario = usuario;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Date getFecha() { return fecha; }
    public void setFecha(Date fecha) { this.fecha = fecha; }

    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }

    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    @Override
    public String toString() {
        return "Venta #" + id + " — S/ " + String.format("%.2f", total);
    }
}
