package controlador;

import dao.ProductoDAO;
import modelo.Producto;
import util.Validador;

import java.sql.SQLException;
import java.util.List;

/**
 * Controlador del modulo de productos.
 * Gestiona el CRUD de productos y validaciones de campos.
 *
 * @author Luiggi
 */
public class ProductosController {

    private final ProductoDAO productoDAO;

    public ProductosController() {
        this.productoDAO = new ProductoDAO();
    }

    /**
     * Retorna la lista completa de productos activos.
     *
     * @return Lista de productos
     */
    public List<Producto> listarProductos() {
        try {
            return productoDAO.listarTodos();
        } catch (SQLException e) {
            System.err.println("Error al listar productos: " + e.getMessage());
            return List.of();
        }
    }

    /**
     * Registra un nuevo producto con los datos ingresados.
     *
     * @param nombre      Nombre del producto
     * @param precio      Precio unitario (texto del campo)
     * @param stock       Stock inicial (texto del campo)
     * @param descripcion Descripcion del producto
     * @return Mensaje de error si los datos no son validos, null si fue exitoso
     */
    public String guardarProducto(String nombre, String precio, String stock, String descripcion) {
        if (!Validador.noEstaVacio(nombre)) {
            return "El nombre es obligatorio.";
        }
        if (!Validador.esDecimalPositivo(precio)) {
            return "El precio debe ser un numero positivo.";
        }
        if (!Validador.esEnteroPositivo(stock)) {
            return "El stock debe ser un numero positivo.";
        }

        try {
            Producto producto = new Producto(0, nombre.trim(),
                Double.parseDouble(precio),
                Integer.parseInt(stock),
                descripcion.trim());

            productoDAO.insertar(producto);
            return null;

        } catch (SQLException e) {
            return "Error al guardar producto: " + e.getMessage();
        }
    }

    /**
     * Actualiza un producto existente.
     *
     * @param id          ID del producto a actualizar
     * @param nombre      Nuevo nombre
     * @param precio      Nuevo precio (texto del campo)
     * @param stock       Nuevo stock (texto del campo)
     * @param descripcion Nueva descripcion
     * @return Mensaje de error si falla, null si fue exitoso
     */
    public String actualizarProducto(int id, String nombre, String precio,
                                     String stock, String descripcion) {
        if (!Validador.noEstaVacio(nombre)) {
            return "El nombre es obligatorio.";
        }
        if (!Validador.esDecimalPositivo(precio)) {
            return "El precio debe ser un numero positivo.";
        }
        if (!Validador.esEnteroPositivo(stock)) {
            return "El stock debe ser un numero positivo.";
        }

        try {
            Producto producto = new Producto(id, nombre.trim(),
                Double.parseDouble(precio),
                Integer.parseInt(stock),
                descripcion.trim());

            productoDAO.actualizar(producto);
            return null;

        } catch (SQLException e) {
            return "Error al actualizar producto: " + e.getMessage();
        }
    }

    /**
     * Desactiva un producto (baja logica).
     *
     * @param id ID del producto a eliminar
     * @return Mensaje de error si falla, null si fue exitoso
     */
    public String eliminarProducto(int id) {
        try {
            productoDAO.desactivar(id);
            return null;
        } catch (SQLException e) {
            return "Error al eliminar producto: " + e.getMessage();
        }
    }
}
