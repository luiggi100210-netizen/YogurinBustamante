package controlador;

import dao.InsumoDAO;
import modelo.Insumo;
import util.Validador;

import java.sql.SQLException;
import java.util.List;

/**
 * Controlador del modulo de insumos.
 * Gestiona el CRUD de insumos y las alertas de stock critico.
 *
 * @author Luiggi
 */
public class InsumosController {

    private final InsumoDAO insumoDAO;

    public InsumosController() {
        this.insumoDAO = new InsumoDAO();
    }

    /**
     * Retorna la lista completa de insumos activos.
     *
     * @return Lista de insumos
     */
    public List<Insumo> listarInsumos() {
        try {
            return insumoDAO.listarTodos();
        } catch (SQLException e) {
            System.err.println("Error al listar insumos: " + e.getMessage());
            return List.of();
        }
    }

    /**
     * Registra un nuevo insumo en el sistema.
     *
     * @param nombre      Nombre del insumo
     * @param unidad      Unidad de medida (kg, litros, etc.)
     * @param stock       Stock inicial (texto del campo)
     * @param stockMinimo Stock minimo (texto del campo)
     * @return Mensaje de error si los datos no son validos, null si fue exitoso
     */
    public String guardarInsumo(String nombre, String unidad, String stock, String stockMinimo) {
        if (!Validador.noEstaVacio(nombre)) {
            return "El nombre es obligatorio.";
        }
        if (!Validador.noEstaVacio(unidad)) {
            return "La unidad de medida es obligatoria.";
        }
        if (!Validador.esDecimalPositivo(stock)) {
            return "El stock debe ser un numero positivo.";
        }
        if (!Validador.esDecimalPositivo(stockMinimo)) {
            return "El stock minimo debe ser un numero positivo.";
        }

        try {
            Insumo insumo = new Insumo(0, nombre.trim(), unidad.trim(),
                Double.parseDouble(stock),
                Double.parseDouble(stockMinimo));

            insumoDAO.insertar(insumo);
            return null;

        } catch (SQLException e) {
            return "Error al guardar insumo: " + e.getMessage();
        }
    }

    /**
     * Actualiza los datos de un insumo existente.
     *
     * @param id          ID del insumo
     * @param nombre      Nuevo nombre
     * @param unidad      Nueva unidad
     * @param stock       Nuevo stock (texto)
     * @param stockMinimo Nuevo stock minimo (texto)
     * @return Mensaje de error si falla, null si fue exitoso
     */
    public String actualizarInsumo(int id, String nombre, String unidad,
                                   String stock, String stockMinimo) {
        if (!Validador.noEstaVacio(nombre) || !Validador.noEstaVacio(unidad)) {
            return "Nombre y unidad son obligatorios.";
        }
        if (!Validador.esDecimalPositivo(stock) || !Validador.esDecimalPositivo(stockMinimo)) {
            return "Stock y stock minimo deben ser numeros positivos.";
        }

        try {
            Insumo insumo = new Insumo(id, nombre.trim(), unidad.trim(),
                Double.parseDouble(stock),
                Double.parseDouble(stockMinimo));

            insumoDAO.actualizar(insumo);
            return null;

        } catch (SQLException e) {
            return "Error al actualizar insumo: " + e.getMessage();
        }
    }

    /**
     * Desactiva un insumo (baja logica).
     *
     * @param id ID del insumo a eliminar
     * @return Mensaje de error si falla, null si fue exitoso
     */
    public String eliminarInsumo(int id) {
        try {
            insumoDAO.desactivar(id);
            return null;
        } catch (SQLException e) {
            return "Error al eliminar insumo: " + e.getMessage();
        }
    }
}
