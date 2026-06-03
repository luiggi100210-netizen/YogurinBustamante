package modelo;

/**
 * Entidad que representa a un usuario del sistema.
 * Los roles disponibles son: ADMIN y VENDEDOR.
 *
 * @author Luiggi
 */
public class Usuario {

    /** Identificador unico del usuario */
    private int id;

    /** Nombre completo del usuario */
    private String nombre;

    /** Nombre de usuario para el login */
    private String usuario;

    /** Clave encriptada en MD5 */
    private String clave;

    /** Rol del usuario: ADMIN o VENDEDOR */
    private String rol;

    /** Constructor vacio requerido por los DAOs */
    public Usuario() {}

    /**
     * Constructor completo para instanciar desde la base de datos.
     *
     * @param id      Identificador unico
     * @param nombre  Nombre completo
     * @param usuario Nombre de usuario
     * @param clave   Clave encriptada MD5
     * @param rol     Rol del usuario (ADMIN / VENDEDOR)
     */
    public Usuario(int id, String nombre, String usuario, String clave, String rol) {
        this.id = id;
        this.nombre = nombre;
        this.usuario = usuario;
        this.clave = clave;
        this.rol = rol;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }

    public String getClave() { return clave; }
    public void setClave(String clave) { this.clave = clave; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }

    @Override
    public String toString() {
        return nombre + " [" + rol + "]";
    }
}
