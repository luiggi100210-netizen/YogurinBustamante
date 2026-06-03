package controlador;

import dao.ClienteDAO;
import dao.ProductoDAO;
import dao.VentaDAO;
import modelo.Cliente;
import modelo.DetalleVenta;
import modelo.Producto;
import modelo.Venta;
import util.Sesion;
import util.Validador;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Controlador del modulo de ventas.
 * Gestiona la logica de negocio: busqueda de clientes,
 * seleccion de productos y registro de ventas.
 *
 * @author Luiggi
 */
public class VentasController {

    private final VentaDAO ventaDAO;
    private final ProductoDAO productoDAO;
    private final ClienteDAO clienteDAO;

    /** Carrito de compra temporal de la venta en progreso */
    private final List<DetalleVenta> carrito;

    public VentasController() {
        this.ventaDAO = new VentaDAO();
        this.productoDAO = new ProductoDAO();
        this.clienteDAO = new ClienteDAO();
        this.carrito = new ArrayList<>();
    }

    /**
     * Retorna la lista de productos disponibles para la venta.
     *
     * @return Lista de productos activos con stock > 0
     */
    public List<Producto> getProductosDisponibles() {
        try {
            return productoDAO.listarTodos();
        } catch (SQLException e) {
            System.err.println("Error al cargar productos: " + e.getMessage());
            return List.of();
        }
    }

    /**
     * Busca un cliente por DNI. Si no existe, retorna null.
     *
     * @param dni DNI del cliente
     * @return Cliente encontrado o null
     */
    public Cliente buscarClientePorDni(String dni) {
        if (!Validador.esDniValido(dni)) {
            return null;
        }
        try {
            return clienteDAO.buscarPorDni(dni);
        } catch (SQLException e) {
            System.err.println("Error al buscar cliente: " + e.getMessage());
            return null;
        }
    }

    /**
     * Agrega un producto al carrito de venta.
     *
     * @param producto Producto a agregar
     * @param cantidad Cantidad solicitada
     * @return Mensaje de error si no es posible, null si se agrego correctamente
     */
    public String agregarAlCarrito(Producto producto, int cantidad) {
        if (cantidad <= 0) {
            return "La cantidad debe ser mayor a cero.";
        }
        if (cantidad > producto.getStock()) {
            return "Stock insuficiente. Disponible: " + producto.getStock();
        }

        DetalleVenta detalle = new DetalleVenta();
        detalle.setProducto(producto);
        detalle.setCantidad(cantidad);
        detalle.calcularSubtotal();
        carrito.add(detalle);
        return null;
    }

    /**
     * Elimina un detalle del carrito por su indice.
     *
     * @param indice Posicion del elemento a eliminar
     */
    public void eliminarDelCarrito(int indice) {
        if (indice >= 0 && indice < carrito.size()) {
            carrito.remove(indice);
        }
    }

    /**
     * Retorna el carrito actual (solo lectura para la vista).
     *
     * @return Lista inmutable del carrito
     */
    public List<DetalleVenta> getCarrito() {
        return List.copyOf(carrito);
    }

    /**
     * Calcula el total acumulado del carrito.
     *
     * @return Suma de todos los subtotales
     */
    public double calcularTotal() {
        return carrito.stream().mapToDouble(DetalleVenta::getSubtotal).sum();
    }

    /**
     * Registra la venta con el cliente dado y los productos del carrito.
     * Limpia el carrito al finalizar si es exitosa.
     *
     * @param cliente Cliente comprador
     * @return Mensaje de resultado (exito o error)
     */
    public String registrarVenta(Cliente cliente) {
        if (carrito.isEmpty()) {
            return "Agregue al menos un producto.";
        }
        if (cliente == null) {
            return "Seleccione o registre un cliente.";
        }

        try {
            Venta venta = new Venta();
            venta.setCliente(cliente);
            venta.setUsuario(Sesion.getInstance().getUsuario());
            venta.setTotal(calcularTotal());

            ventaDAO.registrarVenta(venta, carrito);
            carrito.clear();
            return null;

        } catch (SQLException e) {
            return "Error al registrar la venta: " + e.getMessage();
        }
    }

    /**
     * Limpia el carrito sin registrar la venta.
     */
    public void limpiarCarrito() {
        carrito.clear();
    }
}
