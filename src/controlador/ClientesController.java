package controlador;

import dao.ClienteDAO;
import dao.VentaDAO;
import modelo.Cliente;
import modelo.Venta;
import util.Validador;

import java.sql.SQLException;
import java.util.List;

/**
 * Controlador del modulo de clientes.
 * Gestiona el CRUD de clientes y la busqueda por DNI.
 *
 * @author Luiggi
 */
public class ClientesController {

    private final ClienteDAO clienteDAO;
    private final VentaDAO ventaDAO;

    public ClientesController() {
        this.clienteDAO = new ClienteDAO();
        this.ventaDAO   = new VentaDAO();
    }

    /**
     * Retorna la lista completa de clientes registrados.
     *
     * @return Lista de clientes
     */
    public List<Cliente> listarClientes() {
        try {
            return clienteDAO.listarTodos();
        } catch (SQLException e) {
            System.err.println("Error al listar clientes: " + e.getMessage());
            return List.of();
        }
    }

    /**
     * Busca un cliente por su DNI.
     *
     * @param dni DNI del cliente
     * @return Cliente encontrado, o null si no existe o el DNI no es valido
     */
    public Cliente buscarPorDni(String dni) {
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
     * Registra un nuevo cliente en el sistema.
     *
     * @param nombre   Nombre completo
     * @param dni      DNI del cliente
     * @param telefono Telefono de contacto
     * @return Mensaje de error si los datos son invalidos, null si fue exitoso
     */
    public String guardarCliente(String nombre, String dni, String telefono) {
        if (!Validador.noEstaVacio(nombre)) {
            return "El nombre es obligatorio.";
        }
        if (!Validador.esDniValido(dni)) {
            return "El DNI debe tener exactamente 8 digitos.";
        }
        if (!Validador.esTelefonoValido(telefono)) {
            return "El telefono debe tener entre 7 y 15 digitos.";
        }

        try {
            // Verificar que el DNI no este ya registrado
            if (clienteDAO.buscarPorDni(dni) != null) {
                return "Ya existe un cliente con ese DNI.";
            }

            Cliente cliente = new Cliente(0, nombre.trim(), dni.trim(), telefono.trim());
            clienteDAO.insertar(cliente);
            return null;

        } catch (SQLException e) {
            return "Error al guardar cliente: " + e.getMessage();
        }
    }

    /**
     * Retorna el historial de compras de un cliente.
     *
     * @param idCliente Identificador del cliente
     * @return Lista de ventas del cliente, ordenadas por fecha descendente
     */
    public List<Venta> getHistorialCompras(int idCliente) {
        try {
            return ventaDAO.listarPorCliente(idCliente);
        } catch (SQLException e) {
            System.err.println("Error al obtener historial: " + e.getMessage());
            return List.of();
        }
    }

    /**
     * Actualiza los datos de un cliente existente.
     *
     * @param id       ID del cliente
     * @param nombre   Nuevo nombre
     * @param dni      Nuevo DNI
     * @param telefono Nuevo telefono
     * @return Mensaje de error si falla, null si fue exitoso
     */
    public String actualizarCliente(int id, String nombre, String dni, String telefono) {
        if (!Validador.noEstaVacio(nombre)) {
            return "El nombre es obligatorio.";
        }
        if (!Validador.esDniValido(dni)) {
            return "El DNI debe tener exactamente 8 digitos.";
        }

        try {
            Cliente cliente = new Cliente(id, nombre.trim(), dni.trim(), telefono.trim());
            clienteDAO.actualizar(cliente);
            return null;
        } catch (SQLException e) {
            return "Error al actualizar cliente: " + e.getMessage();
        }
    }
}
