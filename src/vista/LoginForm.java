package vista;

import controlador.LoginController;
import controlador.LoginController.ResultadoLogin;
import util.Tema;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * Pantalla de inicio de sesion del sistema YogurinBustamante.
 * Disenio dividido: logo a la izquierda, formulario a la derecha.
 * Incluye campo de contrasena con boton para mostrar/ocultar.
 * El rol del usuario lo determina la base de datos al autenticar.
 * Toda la logica de autenticacion delega en {@link LoginController}.
 *
 * @author Luiggi
 * @version 1.0
 * @since 2026
 */
public class LoginForm extends JFrame {

    // ── Campos del formulario ─────────────────────────────────────────────────

    private JTextField     txtUsuario;
    private JPasswordField txtClave;
    private JCheckBox      chkMostrarClave;
    private JButton        btnIngresar;
    private JLabel         lblMensaje;

    private final LoginController controller;

    /** Inicializa el controlador y construye la interfaz */
    public LoginForm() {
        this.controller = new LoginController();
        inicializarComponentes();
        configurarVentana();
    }

    // ── Construccion de la interfaz ───────────────────────────────────────────

    private void inicializarComponentes() {
        setLayout(new BorderLayout());

        add(crearBarraTitulo(),     BorderLayout.NORTH);
        add(crearPanelLogo(),       BorderLayout.WEST);
        add(crearPanelFormulario(), BorderLayout.CENTER);
    }

    /** Barra de titulo superior con fondo oscuro del tema. */
    private JPanel crearBarraTitulo() {
        JPanel barra = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 6));
        barra.setBackground(Tema.SIDEBAR);
        JLabel lblTitulo = new JLabel("Sistema de Gestion — Yogurin Bustamante — Sayan 2026");
        lblTitulo.setForeground(Tema.BLANCO);
        lblTitulo.setFont(Tema.fuente(Font.PLAIN, 11));
        barra.add(lblTitulo);
        return barra;
    }

    /** Panel izquierdo con logo escalado y ubicacion de la empresa. */
    private JPanel crearPanelLogo() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Tema.FONDO);
        panel.setPreferredSize(new Dimension(320, 0));
        panel.setBorder(new EmptyBorder(30, 20, 30, 20));

        JLabel lblLogo = new JLabel(cargarLogo(), SwingConstants.CENTER);
        panel.add(lblLogo, BorderLayout.CENTER);

        JLabel lblCiudad = new JLabel("Sayan — Lima — Peru", SwingConstants.CENTER);
        lblCiudad.setFont(Tema.fuente(Font.ITALIC, 12));
        lblCiudad.setForeground(Tema.TEXTO_GRIS);
        panel.add(lblCiudad, BorderLayout.SOUTH);

        return panel;
    }

    /** Panel derecho con el formulario de autenticacion. */
    private JPanel crearPanelFormulario() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Tema.BLANCO);
        panel.setBorder(new EmptyBorder(40, 40, 40, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill      = GridBagConstraints.HORIZONTAL;
        gbc.gridx     = 0;
        gbc.gridwidth = 1;

        // Titulo
        JLabel lblTitulo = new JLabel("Iniciar Sesion", SwingConstants.CENTER);
        lblTitulo.setFont(Tema.fuente(Font.BOLD, 22));
        lblTitulo.setForeground(Tema.TEXTO);
        gbc.gridy = 0; gbc.insets = new Insets(0, 0, 24, 0);
        panel.add(lblTitulo, gbc);

        // Usuario
        gbc.gridy = 1; gbc.insets = new Insets(6, 0, 2, 0);
        panel.add(Tema.etiquetaCampo("USUARIO"), gbc);
        txtUsuario = new JTextField();
        Tema.estilizarCampo(txtUsuario);
        gbc.gridy = 2; gbc.insets = new Insets(0, 0, 8, 0);
        panel.add(txtUsuario, gbc);

        // Contrasena
        gbc.gridy = 3; gbc.insets = new Insets(6, 0, 2, 0);
        panel.add(Tema.etiquetaCampo("CONTRASENA"), gbc);
        txtClave = new JPasswordField();
        Tema.estilizarCampo(txtClave);
        gbc.gridy = 4; gbc.insets = new Insets(0, 0, 4, 0);
        panel.add(txtClave, gbc);

        // Checkbox mostrar contrasena
        chkMostrarClave = new JCheckBox("Mostrar contrasena");
        chkMostrarClave.setBackground(Tema.BLANCO);
        chkMostrarClave.setForeground(Tema.TEXTO_SUAVE);
        chkMostrarClave.setFont(Tema.fuente(Font.PLAIN, 11));
        chkMostrarClave.addActionListener(e -> alternarVisibilidadClave());
        gbc.gridy = 5; gbc.insets = new Insets(0, 0, 20, 0);
        panel.add(chkMostrarClave, gbc);

        // Boton ingresar
        btnIngresar = new JButton("INGRESAR AL SISTEMA");
        btnIngresar.setBackground(Tema.SIDEBAR);
        btnIngresar.setForeground(Tema.BLANCO);
        btnIngresar.setFont(Tema.fuente(Font.BOLD, 14));
        btnIngresar.setPreferredSize(new Dimension(0, 44));
        btnIngresar.setBorderPainted(false);
        btnIngresar.setFocusPainted(false);
        btnIngresar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnIngresar.addActionListener((ActionEvent e) -> autenticar());
        gbc.gridy = 6; gbc.insets = new Insets(0, 0, 8, 0);
        panel.add(btnIngresar, gbc);

        // Mensaje de error
        lblMensaje = new JLabel("", SwingConstants.CENTER);
        lblMensaje.setForeground(Tema.PELIGRO);
        lblMensaje.setFont(Tema.fuente(Font.PLAIN, 11));
        gbc.gridy = 7; gbc.insets = new Insets(0, 0, 0, 0);
        panel.add(lblMensaje, gbc);

        // Footer
        JLabel lblFooter = new JLabel(
            "<html><center>Sistema Informatico — UTP 2026<br>Proyecto Integrador I</center></html>",
            SwingConstants.CENTER);
        lblFooter.setFont(Tema.fuente(Font.PLAIN, 10));
        lblFooter.setForeground(Tema.TEXTO_GRIS);
        gbc.gridy = 8; gbc.insets = new Insets(20, 0, 0, 0);
        panel.add(lblFooter, gbc);

        // Permitir Enter en el campo de clave
        txtClave.addActionListener((ActionEvent e) -> autenticar());

        return panel;
    }

    // ── Acciones ──────────────────────────────────────────────────────────────

    /**
     * Delega la autenticacion al controlador y reacciona al resultado.
     * Si el login es exitoso abre el Dashboard y cierra este formulario.
     */
    private void autenticar() {
        String usuario = txtUsuario.getText().trim();
        String clave   = new String(txtClave.getPassword());

        ResultadoLogin resultado = controller.login(usuario, clave);
        if (resultado.isExitoso()) {
            new DashboardForm().setVisible(true);
            dispose();
        } else {
            lblMensaje.setText(resultado.getMensaje());
            txtClave.setText("");
        }
    }

    /**
     * Alterna la visibilidad de la contrasena segun el estado del checkbox.
     */
    private void alternarVisibilidadClave() {
        if (chkMostrarClave.isSelected()) {
            txtClave.setEchoChar((char) 0);
        } else {
            txtClave.setEchoChar('•');
        }
    }

    // ── Recursos ──────────────────────────────────────────────────────────────

    /**
     * Carga el logo de la empresa escalado desde el classpath.
     *
     * @return ImageIcon del logo escalado, o icono vacio si no se encuentra
     */
    private ImageIcon cargarLogo() {
        java.net.URL url = getClass().getClassLoader().getResource("img/logo_yogurin.jpg");
        if (url != null) {
            ImageIcon icon = new ImageIcon(url);
            Image img = icon.getImage().getScaledInstance(220, 200, Image.SCALE_SMOOTH);
            return new ImageIcon(img);
        }
        return new ImageIcon();
    }

    // ── Configuracion de ventana ──────────────────────────────────────────────

    private void configurarVentana() {
        setTitle("Yogurin Bustamante — Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(650, 440);
        setLocationRelativeTo(null);
        setResizable(false);
    }

    /**
     * Punto de entrada de la aplicacion.
     * Aplica el tema visual antes de crear cualquier ventana.
     *
     * @param args Argumentos de linea de comandos (no utilizados)
     */
    public static void main(String[] args) {
        Tema.aplicarLookAndFeel();
        SwingUtilities.invokeLater(() -> new LoginForm().setVisible(true));
    }
}
