package modelo;

/**
 * Entidad que representa a un cliente del negocio.
 *
 * @author Luiggi
 */
public class Cliente {

    /** Identificador unico del cliente */
    private int id;

    /** Nombre completo del cliente */
    private String nombre;

    /** Documento Nacional de Identidad (8 digitos) */
    private String dni;

    /** Numero de telefono de contacto */
    private String telefono;

    /** Constructor vacio requerido por los DAOs */
    public Cliente() {}

    /**
     * Constructor completo para instanciar desde la base de datos.
     *
     * @param id       Identificador unico
     * @param nombre   Nombre completo
     * @param dni      DNI del cliente
     * @param telefono Telefono de contacto
     */
    public Cliente(int id, String nombre, String dni, String telefono) {
        this.id = id;
        this.nombre = nombre;
        this.dni = dni;
        this.telefono = telefono;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDni() { return dni; }
    public void setDni(String dni) { this.dni = dni; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    @Override
    public String toString() {
        return nombre + " — DNI: " + dni;
    }
}
