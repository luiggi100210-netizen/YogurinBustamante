package vista;

import controlador.LoginController;
import controlador.LoginController.ResultadoLogin;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * Pantalla de inicio de sesion del sistema YogurinBustamante.
 * Disenio dividido: logo a la izquierda, formulario a la derecha.
 * Incluye campo de contrasena con boton para mostrar/ocultar.
 * Toda la logica de autenticacion delega en {@link LoginController}.
 *
 * @author Luiggi
 * @version 1.0
 * @since 2026
 */
public class LoginForm extends JFrame {

    // ── Campos del formulario ─────────────────────────────────────────────────

    private JTextField    txtUsuario;
    private JPasswordField txtClave;
    private JCheckBox     chkMostrarClave;
    private JComboBox<String> cmbRol;
    private JButton       btnIngresar;
    private JLabel        lblMensaje;

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

        add(crearBarraTitulo(),  BorderLayout.NORTH);
        add(crearPanelLogo(),    BorderLayout.WEST);
        add(crearPanelFormulario(), BorderLayout.CENTER);
    }

    /** Barra de titulo superior con fondo oscuro del tema. */
    private JPanel crearBarraTitulo() {
        JPanel barra = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 6));
        barra.setBackground(new Color(0x1A2744));
        JLabel lblTitulo = new JLabel("Sistema de Gestion — Yogurin Bustamante — Sayan 2026");
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setFont(new Font("Arial", Font.PLAIN, 11));
        barra.add(lblTitulo);
        return barra;
    }

    /** Panel izquierdo con logo escalado y ubicacion de la empresa. */
    private JPanel crearPanelLogo() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(0xF0F4F8));
        panel.setPreferredSize(new Dimension(320, 0));
        panel.setBorder(new EmptyBorder(30, 20, 30, 20));

        JLabel lblLogo = new JLabel(cargarLogo(), SwingConstants.CENTER);
        panel.add(lblLogo, BorderLayout.CENTER);

        JLabel lblCiudad = new JLabel("Sayan — Lima — Peru", SwingConstants.CENTER);
        lblCiudad.setFont(new Font("Arial", Font.ITALIC, 12));
        lblCiudad.setForeground(new Color(0x666666));
        panel.add(lblCiudad, BorderLayout.SOUTH);

        return panel;
    }

    /** Panel derecho con el formulario de autenticacion. */
    private JPanel crearPanelFormulario() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(40, 40, 40, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill      = GridBagConstraints.HORIZONTAL;
        gbc.gridx     = 0;
        gbc.gridwidth = 1;

        // Titulo
        JLabel lblTitulo = new JLabel("Iniciar Sesion", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 22));
        lblTitulo.setForeground(new Color(0x1A2744));
        gbc.gridy = 0; gbc.insets = new Insets(0, 0, 24, 0);
        panel.add(lblTitulo, gbc);

        // Usuario
        gbc.gridy = 1; gbc.insets = new Insets(6, 0, 2, 0);
        panel.add(crearEtiqueta("USUARIO"), gbc);
        txtUsuario = crearCampoTexto("admin");
        gbc.gridy = 2; gbc.insets = new Insets(0, 0, 8, 0);
        panel.add(txtUsuario, gbc);

        // Contrasena
        gbc.gridy = 3; gbc.insets = new Insets(6, 0, 2, 0);
        panel.add(crearEtiqueta("CONTRASENA"), gbc);
        txtClave = new JPasswordField();
        estilizarCampo(txtClave);
        gbc.gridy = 4; gbc.insets = new Insets(0, 0, 4, 0);
        panel.add(txtClave, gbc);

        // Checkbox mostrar contrasena
        chkMostrarClave = new JCheckBox("Mostrar contrasena");
        chkMostrarClave.setBackground(Color.WHITE);
        chkMostrarClave.setForeground(new Color(0x555555));
        chkMostrarClave.setFont(new Font("Arial", Font.PLAIN, 11));
        chkMostrarClave.addActionListener(e -> alternarVisibilidadClave());
        gbc.gridy = 5; gbc.insets = new Insets(0, 0, 10, 0);
        panel.add(chkMostrarClave, gbc);

        // Rol de acceso
        gbc.gridy = 6; gbc.insets = new Insets(6, 0, 2, 0);
        panel.add(crearEtiqueta("ROL DE ACCESO"), gbc);
        cmbRol = new JComboBox<>(new String[]{"Administrador", "Vendedor"});
        cmbRol.setFont(new Font("Arial", Font.PLAIN, 13));
        cmbRol.setBackground(new Color(0x2C3E6B));
        cmbRol.setForeground(Color.WHITE);
        cmbRol.setPreferredSize(new Dimension(0, 38));
        gbc.gridy = 7; gbc.insets = new Insets(0, 0, 20, 0);
        panel.add(cmbRol, gbc);

        // Boton ingresar
        btnIngresar = new JButton("INGRESAR AL SISTEMA");
        btnIngresar.setBackground(new Color(0x1A2744));
        btnIngresar.setForeground(Color.WHITE);
        btnIngresar.setFont(new Font("Arial", Font.BOLD, 14));
        btnIngresar.setPreferredSize(new Dimension(0, 44));
        btnIngresar.setBorderPainted(false);
        btnIngresar.setFocusPainted(false);
        btnIngresar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnIngresar.addActionListener((ActionEvent e) -> autenticar());
        gbc.gridy = 8; gbc.insets = new Insets(0, 0, 8, 0);
        panel.add(btnIngresar, gbc);

        // Mensaje de error
        lblMensaje = new JLabel("", SwingConstants.CENTER);
        lblMensaje.setForeground(Color.RED);
        lblMensaje.setFont(new Font("Arial", Font.PLAIN, 11));
        gbc.gridy = 9; gbc.insets = new Insets(0, 0, 0, 0);
        panel.add(lblMensaje, gbc);

        // Footer
        JLabel lblFooter = new JLabel(
            "<html><center>Sistema Informatico — UTP 2026<br>Proyecto Integrador I</center></html>",
            SwingConstants.CENTER);
        lblFooter.setFont(new Font("Arial", Font.PLAIN, 10));
        lblFooter.setForeground(new Color(0xAAAAAA));
        gbc.gridy = 10; gbc.insets = new Insets(20, 0, 0, 0);
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
            txtClave.setEchoChar('\u2022');
        }
    }

    // ── Helpers de construccion UI ────────────────────────────────────────────

    /**
     * Crea una etiqueta de campo con el estilo del prototipo.
     *
     * @param texto Texto de la etiqueta
     * @return Etiqueta estilizada
     */
    private JLabel crearEtiqueta(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(new Font("Arial", Font.BOLD, 11));
        lbl.setForeground(new Color(0x555555));
        return lbl;
    }

    /**
     * Crea un JTextField con el estilo oscuro del formulario.
     *
     * @param valorInicial Texto inicial del campo
     * @return Campo de texto estilizado
     */
    private JTextField crearCampoTexto(String valorInicial) {
        JTextField campo = new JTextField(valorInicial);
        estilizarCampo(campo);
        return campo;
    }

    /**
     * Aplica el estilo oscuro comun a los campos de texto del formulario.
     *
     * @param campo Campo de texto a estilizar
     */
    private void estilizarCampo(JTextField campo) {
        campo.setBackground(new Color(0x2C3E6B));
        campo.setForeground(Color.WHITE);
        campo.setCaretColor(Color.WHITE);
        campo.setFont(new Font("Arial", Font.PLAIN, 13));
        campo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0x3D5080), 1),
            new EmptyBorder(8, 10, 8, 10)
        ));
        campo.setPreferredSize(new Dimension(0, 38));
    }

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
        setSize(650, 460);
        setLocationRelativeTo(null);
        setResizable(false);
    }

    /**
     * Punto de entrada de la aplicacion.
     *
     * @param args Argumentos de linea de comandos (no utilizados)
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginForm().setVisible(true));
    }
}
