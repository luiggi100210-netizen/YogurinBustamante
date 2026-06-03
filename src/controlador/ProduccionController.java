package controlador;

import dao.InsumoDAO;
import dao.ProduccionDAO;
import dao.ProductoDAO;
import modelo.Insumo;
import modelo.LoteProduccion;
import modelo.Producto;
import util.Validador;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controlador del modulo de produccion.
 * Gestiona el registro de lotes de produccion y el consumo de insumos.
 *
 * @author Luiggi
 */
public class ProduccionController {

    private final ProduccionDAO produccionDAO;
    private final ProductoDAO productoDAO;
    private final InsumoDAO insumoDAO;

    public ProduccionController() {
        this.produccionDAO = new ProduccionDAO();
        this.productoDAO = new ProductoDAO();
        this.insumoDAO = new InsumoDAO();
    }

    /**
     * Retorna los productos disponibles para producir.
     *
     * @return Lista de productos activos
     */
    public List<Producto> getProductos() {
        try {
            return productoDAO.listarTodos();
        } catch (SQLException e) {
            System.err.println("Error al cargar productos: " + e.getMessage());
            return List.of();
        }
    }

    /**
     * Retorna los insumos disponibles en almacen.
     *
     * @return Lista de insumos activos
     */
    public List<Insumo> getInsumos() {
        try {
            return insumoDAO.listarTodos();
        } catch (SQLException e) {
            System.err.println("Error al cargar insumos: " + e.getMessage());
            return List.of();
        }
    }

    /**
     * Retorna el historial de lotes registrados.
     *
     * @return Lista de lotes de produccion
     */
    public List<LoteProduccion> listarLotes() {
        try {
            return produccionDAO.listarTodos();
        } catch (SQLException e) {
            System.err.println("Error al listar lotes: " + e.getMessage());
            return List.of();
        }
    }

    /**
     * Registra un nuevo lote de produccion.
     *
     * @param producto      Producto a fabricar
     * @param unidades      Cantidad a producir (texto del campo)
     * @param insumosUsados Mapa de insumo -> cantidad usada
     * @return Mensaje de error si los datos son invalidos, null si fue exitoso
     */
    public String registrarLote(Producto producto, String unidades,
                                Map<Insumo, Double> insumosUsados) {
        if (producto == null) {
            return "Seleccione el producto a fabricar.";
        }
        if (!Validador.esEnteroPositivo(unidades)) {
            return "Las unidades deben ser un numero positivo.";
        }
        if (insumosUsados == null || insumosUsados.isEmpty()) {
            return "Agregue al menos un insumo utilizado.";
        }

        try {
            LoteProduccion lote = new LoteProduccion(
                0, null, producto,
                Integer.parseInt(unidades),
                insumosUsados
            );
            produccionDAO.registrarLote(lote);
            return null;

        } catch (SQLException e) {
            return "Error al registrar lote: " + e.getMessage();
        }
    }
}
