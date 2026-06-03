package controlador;

import dao.DashboardDAO;
import dao.InsumoDAO;
import modelo.DashboardData;
import modelo.Insumo;
import util.Sesion;

import java.sql.SQLException;
import java.util.List;

/**
 * Controlador del panel principal (Dashboard).
 * Coordina la carga de indicadores del dia, las alertas de stock
 * y la gestion de la sesion activa. No contiene referencias a Swing.
 *
 * @author Luiggi
 * @version 1.0
 * @since 2026
 */
public class DashboardController {

    private final DashboardDAO dashboardDAO;
    private final InsumoDAO    insumoDAO;

    /** Inicializa los DAOs requeridos por el panel principal */
    public DashboardController() {
        this.dashboardDAO = new DashboardDAO();
        this.insumoDAO    = new InsumoDAO();
    }

    /**
     * Carga los cuatro indicadores del panel principal desde la base de datos.
     * Retorna un {@link DashboardData} con valores en cero si ocurre un error.
     *
     * @return Datos del dashboard del dia actual
     */
    public DashboardData cargarDatos() {
        try {
            return dashboardDAO.obtenerDatos();
        } catch (SQLException e) {
            System.err.println("Error al cargar datos del dashboard: " + e.getMessage());
            return new DashboardData();
        }
    }

    /**
     * Retorna la lista de insumos con stock critico para mostrar en la barra de alertas.
     *
     * @return Lista de insumos con stock menor al minimo establecido
     */
    public List<Insumo> getInsumosBajoStock() {
        try {
            return insumoDAO.listarBajoStock();
        } catch (SQLException e) {
            System.err.println("Error al obtener alertas de stock: " + e.getMessage());
            return List.of();
        }
    }

    /**
     * Retorna el nombre completo del usuario actualmente logueado.
     *
     * @return Nombre del usuario activo, o "Desconocido" si no hay sesion
     */
    public String getNombreUsuarioActivo() {
        return Sesion.getInstance().getUsuario() != null
                ? Sesion.getInstance().getUsuario().getNombre()
                : "Desconocido";
    }

    /**
     * Indica si el usuario activo tiene rol de Administrador.
     * Usado para mostrar u ocultar opciones del sidebar segun el rol.
     *
     * @return {@code true} si el usuario es ADMIN
     */
    public boolean esAdmin() {
        return Sesion.getInstance().esAdmin();
    }

    /**
     * Cierra la sesion activa eliminando el usuario de memoria.
     * Debe llamarse antes de mostrar el LoginForm.
     */
    public void cerrarSesion() {
        Sesion.getInstance().cerrarSesion();
    }
}
