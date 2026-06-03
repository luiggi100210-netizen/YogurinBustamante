package vista;

import controlador.DashboardController;
import modelo.DashboardData;
import modelo.Insumo;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Panel principal del sistema con sidebar lateral, KPIs y tarjetas de modulos.
 * Disenio basado en el prototipo oficial del proyecto.
 * Las opciones del sidebar se muestran u ocultan segun el rol del usuario activo:
 * el Vendedor no puede acceder a Usuarios, Respaldo ni Cierre de Caja.
 * Toda la logica delega en {@link DashboardController}.
 *
 * @author Luiggi
 * @version 1.0
 * @since 2026
 */
public class DashboardForm extends JFrame {

    // ── Paleta de colores del tema ────────────────────────────────────────────

    private static final Color COLOR_SIDEBAR     = new Color(0x1A2744);
    private static final Color COLOR_ACTIVO      = new Color(0x2E4A8A);
    private static final Color COLOR_FONDO       = new Color(0xF0F4F8);
    private static final Color COLOR_BLANCO      = Color.WHITE;
    private static final Color COLOR_ALERTA      = new Color(0xFFF3CD);
    private static final Color COLOR_ALERTA_TEXT = new Color(0x856404);

    // ── Etiquetas de KPIs ─────────────────────────────────────────────────────

    private JLabel lblVentasDia;
    private JLabel lblNumVentas;
    private JLabel lblUnidadesProducidas;
    private JLabel lblAlertasStock;
    private JPanel panelAlertas;

    private final DashboardController controller;

    /** Inicializa el controlador y construye la interfaz */
    public DashboardForm() {
        this.controller = new DashboardController();
        inicializarComponentes();
        cargarDatos();
        configurarVentana();
    }

    // ── Construccion de la interfaz ───────────────────────────────────────────

    private void inicializarComponentes() {
        setLayout(new BorderLayout());
        getContentPane().setBackground(COLOR_FONDO);
        add(crearSidebar(),        BorderLayout.WEST);
        add(crearPanelPrincipal(), BorderLayout.CENTER);
    }

    // ── Sidebar ───────────────────────────────────────────────────────────────

    /**
     * Construye el sidebar lateral con logo, menu de navegacion y boton de cierre.
     * Las secciones visibles dependen del rol del usuario autenticado.
     *
     * @return Panel sidebar configurado
     */
    private JPanel crearSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(COLOR_SIDEBAR);
        sidebar.setPreferredSize(new Dimension(260, 0));
        sidebar.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        sidebar.add(crearLogoSidebar());

        // MENU PRINCIPAL — visible para todos
        sidebar.add(crearSeccionMenu("MENU PRINCIPAL"));
        sidebar.add(crearItemMenu("\u2302  Inicio",        true,  () -> {}));
        sidebar.add(crearItemMenu("\u2699  Produccion",    false, () -> new ProduccionForm().setVisible(true)));
        sidebar.add(crearItemMenu("\u25C6  Ventas",        false, () -> new VentasForm().setVisible(true)));
        sidebar.add(crearItemMenu("\u25A3  Inventario",    false, () -> new InsumosForm().setVisible(true)));
        sidebar.add(crearItemMenu("\u263A  Clientes",      false, () -> new ClientesForm().setVisible(true)));

        // REPORTES — solo administrador ve Cierre de Caja
        sidebar.add(crearSeccionMenu("REPORTES"));
        sidebar.add(crearItemMenu("\u25A4  Ver reportes",  false, () -> new ReportesForm().setVisible(true)));
        if (controller.esAdmin()) {
            sidebar.add(crearItemMenu("\u00A4  Cierre de caja", false, () -> new CierreCajaForm().setVisible(true)));
        }

        // SISTEMA — solo administrador
        if (controller.esAdmin()) {
            sidebar.add(crearSeccionMenu("SISTEMA"));
            sidebar.add(crearItemMenu("\u263B  Usuarios",  false, () -> new UsuariosForm().setVisible(true)));
            sidebar.add(crearItemMenu("\u2756  Respaldo",  false, () -> new RespaldoForm().setVisible(true)));
        }

        sidebar.add(Box.createVerticalGlue());
        sidebar.add(crearBotonCerrarSesion());
        sidebar.add(Box.createVerticalStrut(15));

        return sidebar;
    }

    /** Panel superior del sidebar con logo y nombre de la empresa. */
    private JPanel crearLogoSidebar() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(COLOR_SIDEBAR);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblLogo = new JLabel(cargarLogoSmall(), SwingConstants.CENTER);
        lblLogo.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblLogo.setBorder(new EmptyBorder(10, 0, 8, 0));
        panel.add(lblLogo);

        JLabel lblNombre = new JLabel("Yogurin Bustamante", SwingConstants.CENTER);
        lblNombre.setForeground(Color.WHITE);
        lblNombre.setFont(new Font("Arial", Font.BOLD, 11));
        lblNombre.setHorizontalAlignment(SwingConstants.CENTER);
        lblNombre.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblNombre.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
        lblNombre.setBorder(new EmptyBorder(5, 5, 10, 5));
        panel.add(lblNombre);

        return panel;
    }

    /** Boton rojo de cierre de sesion alineado al fondo del sidebar. */
    private JButton crearBotonCerrarSesion() {
        JButton btn = new JButton("Cerrar Sesion");
        btn.setBackground(new Color(0xC0392B));
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Arial", Font.BOLD, 11));
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(170, 34));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addActionListener(e -> cerrarSesion());
        return btn;
    }

    /**
     * Crea un separador de seccion en el sidebar.
     *
     * @param texto Nombre de la seccion en mayusculas
     * @return Etiqueta estilizada como separador
     */
    private JLabel crearSeccionMenu(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setForeground(new Color(0x8899BB));
        lbl.setFont(new Font("Arial", Font.BOLD, 9));
        lbl.setHorizontalAlignment(SwingConstants.LEFT);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        lbl.setBorder(BorderFactory.createEmptyBorder(8, 20, 4, 10));
        return lbl;
    }

    /**
     * Crea un boton de item de menu en el sidebar.
     *
     * @param texto  Texto con icono Unicode a mostrar
     * @param activo {@code true} si este item es la pantalla activa
     * @param accion Accion a ejecutar al hacer clic
     * @return Boton estilizado para el sidebar
     */
    private JButton crearItemMenu(String texto, boolean activo, Runnable accion) {
        JButton btn = new JButton(texto);
        btn.setBackground(activo ? COLOR_ACTIVO : COLOR_SIDEBAR);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Arial", Font.PLAIN, 13));
        btn.setBorderPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(7, 20, 7, 10));
        btn.setFocusPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setHorizontalTextPosition(SwingConstants.RIGHT);
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        btn.setMinimumSize(new Dimension(240, 36));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) {
                if (!activo) btn.setBackground(new Color(0x243560));
            }
            @Override public void mouseExited(java.awt.event.MouseEvent e) {
                if (!activo) btn.setBackground(COLOR_SIDEBAR);
            }
        });
        btn.addActionListener(e -> accion.run());
        return btn;
    }

    // ── Panel principal ───────────────────────────────────────────────────────

    /**
     * Construye el panel derecho con header, alertas, KPIs y tarjetas de modulos.
     *
     * @return Panel principal configurado
     */
    private JPanel crearPanelPrincipal() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(COLOR_FONDO);
        panel.setBorder(new EmptyBorder(15, 18, 15, 18));

        panel.add(crearHeader(), BorderLayout.NORTH);

        panelAlertas = crearPanelAlertas();
        JPanel kpis  = crearKPIs();

        JPanel norte = new JPanel(new BorderLayout(0, 10));
        norte.setBackground(COLOR_FONDO);
        norte.add(panelAlertas, BorderLayout.NORTH);
        norte.add(kpis,         BorderLayout.CENTER);

        JPanel centro = new JPanel(new BorderLayout(0, 10));
        centro.setBackground(COLOR_FONDO);
        centro.add(norte,                  BorderLayout.NORTH);
        centro.add(crearTarjetasModulos(), BorderLayout.CENTER);

        panel.add(centro, BorderLayout.CENTER);
        return panel;
    }

    /** Cabecera con mensaje de bienvenida, fecha y rol del usuario. */
    private JPanel crearHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(COLOR_FONDO);

        JLabel lblBienvenida = new JLabel("Bienvenido, " + controller.getNombreUsuarioActivo());
        lblBienvenida.setFont(new Font("Arial", Font.BOLD, 20));
        lblBienvenida.setForeground(new Color(0x1A2744));

        String fecha = LocalDate.now().format(DateTimeFormatter.ofPattern(
                "EEEE d 'de' MMMM, yyyy", new java.util.Locale("es", "PE")));
        String rol = controller.esAdmin() ? "Administrador" : "Vendedor";

        JLabel lblFechaRol = new JLabel(fecha + "  |  Rol: " + rol, SwingConstants.RIGHT);
        lblFechaRol.setFont(new Font("Arial", Font.PLAIN, 12));
        lblFechaRol.setForeground(new Color(0x666666));

        header.add(lblBienvenida, BorderLayout.WEST);
        header.add(lblFechaRol,   BorderLayout.EAST);
        return header;
    }

    /** Barra amarilla de alertas de stock critico (oculta si no hay alertas). */
    private JPanel crearPanelAlertas() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        panel.setBackground(COLOR_ALERTA);
        panel.setBorder(BorderFactory.createLineBorder(new Color(0xFFE69C), 1));
        panel.setVisible(false);
        return panel;
    }

    /** Fila de cuatro tarjetas KPI con borde de color lateral. */
    private JPanel crearKPIs() {
        JPanel panel = new JPanel(new GridLayout(1, 4, 12, 0));
        panel.setBackground(COLOR_FONDO);

        lblVentasDia          = new JLabel("S/ 0",  SwingConstants.CENTER);
        lblNumVentas          = new JLabel("0",      SwingConstants.CENTER);
        lblUnidadesProducidas = new JLabel("0",      SwingConstants.CENTER);
        lblAlertasStock       = new JLabel("0",      SwingConstants.CENTER);

        panel.add(crearKPI(lblVentasDia,          "Ventas del dia",        new Color(0x1A6FBA)));
        panel.add(crearKPI(lblNumVentas,          "Ventas registradas",    new Color(0x1A8A4A)));
        panel.add(crearKPI(lblUnidadesProducidas, "Unidades producidas",   new Color(0xE07B00)));
        panel.add(crearKPI(lblAlertasStock,       "Alertas de stock",      new Color(0xCC2200)));
        return panel;
    }

    /**
     * Construye una tarjeta KPI individual con valor grande y titulo.
     *
     * @param lblValor Etiqueta que mostrara el valor numerico
     * @param titulo   Descripcion del indicador
     * @param color    Color del borde lateral y del valor
     * @return Panel de tarjeta KPI
     */
    private JPanel crearKPI(JLabel lblValor, String titulo, Color color) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(COLOR_BLANCO);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 4, 0, 0, color),
            new EmptyBorder(14, 16, 14, 16)
        ));
        card.setPreferredSize(new Dimension(0, 90));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));

        lblValor.setFont(new Font("Arial", Font.BOLD, 28));
        lblValor.setForeground(color);
        lblValor.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("Arial", Font.PLAIN, 11));
        lblTitulo.setForeground(new Color(0x888888));
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(Box.createVerticalGlue());
        card.add(lblValor);
        card.add(Box.createVerticalStrut(4));
        card.add(lblTitulo);
        card.add(Box.createVerticalGlue());
        return card;
    }

    /**
     * Grid 2x3 de tarjetas de acceso rapido a cada modulo del sistema.
     * Cierre de Caja solo se muestra a administradores.
     *
     * @return Panel con las tarjetas de modulos
     */
    private JPanel crearTarjetasModulos() {
        JPanel panel = new JPanel(new GridLayout(2, 3, 12, 12));
        panel.setBackground(COLOR_FONDO);

        panel.add(crearTarjeta("\u2699", "Produccion",     "Registrar lotes diarios",      () -> new ProduccionForm().setVisible(true)));
        panel.add(crearTarjeta("\u25C6", "Ventas",         "Registrar y gestionar ventas", () -> new VentasForm().setVisible(true)));
        panel.add(crearTarjeta("\u25A3", "Inventario",     "Stock de productos e insumos", () -> new InsumosForm().setVisible(true)));
        panel.add(crearTarjeta("\u263A", "Clientes",       "Registro e historial",         () -> new ClientesForm().setVisible(true)));
        panel.add(crearTarjeta("\u25A4", "Reportes",       "Ventas y produccion",          () -> new ReportesForm().setVisible(true)));

        if (controller.esAdmin()) {
            panel.add(crearTarjeta("\u00A4", "Cierre de caja", "Resumen de ingresos", () -> new CierreCajaForm().setVisible(true)));
        } else {
            JPanel vacio = new JPanel();
            vacio.setBackground(COLOR_FONDO);
            panel.add(vacio);
        }

        return panel;
    }

    /**
     * Construye una tarjeta de modulo con icono, titulo y subtitulo centrados.
     *
     * @param icono     Caracter Unicode del icono
     * @param titulo    Nombre del modulo
     * @param subtitulo Descripcion breve del modulo
     * @param accion    Accion a ejecutar al hacer clic
     * @return Panel de tarjeta de modulo
     */
    private JPanel crearTarjeta(String icono, String titulo, String subtitulo, Runnable accion) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(COLOR_BLANCO);
        card.setBorder(new EmptyBorder(12, 10, 12, 10));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel lblIcono = new JLabel(icono, SwingConstants.CENTER);
        lblIcono.setFont(new Font("Arial", Font.PLAIN, 30));
        lblIcono.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblTitulo = new JLabel(titulo, SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 13));
        lblTitulo.setForeground(new Color(0x1A2744));
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSub = new JLabel(subtitulo, SwingConstants.CENTER);
        lblSub.setFont(new Font("Arial", Font.PLAIN, 10));
        lblSub.setForeground(new Color(0x888888));
        lblSub.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(Box.createVerticalGlue());
        card.add(lblIcono);
        card.add(Box.createVerticalStrut(6));
        card.add(lblTitulo);
        card.add(Box.createVerticalStrut(3));
        card.add(lblSub);
        card.add(Box.createVerticalGlue());

        card.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) { accion.run(); }
            @Override public void mouseEntered(java.awt.event.MouseEvent e) { card.setBackground(new Color(0xEEF3FA)); }
            @Override public void mouseExited(java.awt.event.MouseEvent e)  { card.setBackground(COLOR_BLANCO); }
        });
        return card;
    }

    // ── Carga de datos ────────────────────────────────────────────────────────

    /**
     * Solicita los datos del dia al controlador y actualiza los KPIs y alertas.
     */
    private void cargarDatos() {
        DashboardData datos = controller.cargarDatos();

        lblVentasDia.setText("S/ " + String.format("%.0f", datos.getVentasDelDia()));
        lblNumVentas.setText(String.valueOf(datos.getVentasRegistradas()));
        lblUnidadesProducidas.setText(String.valueOf(datos.getUnidadesProducidas()));
        lblAlertasStock.setText(String.valueOf(datos.getAlertasStock()));

        actualizarPanelAlertas();
    }

    /**
     * Actualiza la barra de alertas de stock critico.
     * La oculta si no hay alertas, la muestra con los insumos afectados si hay.
     */
    private void actualizarPanelAlertas() {
        List<Insumo> alertas = controller.getInsumosBajoStock();
        panelAlertas.removeAll();

        if (!alertas.isEmpty()) {
            StringBuilder sb = new StringBuilder("\u26A0 Stock bajo: ");
            int limite = Math.min(alertas.size(), 3);
            for (int i = 0; i < limite; i++) {
                Insumo ins = alertas.get(i);
                sb.append(ins.getNombre())
                  .append(" (").append(ins.getStock()).append(" ").append(ins.getUnidad()).append(")");
                if (i < limite - 1) sb.append(" — ");
            }
            JLabel lblAlerta = new JLabel(sb.toString());
            lblAlerta.setForeground(COLOR_ALERTA_TEXT);
            lblAlerta.setFont(new Font("Arial", Font.PLAIN, 12));
            panelAlertas.add(lblAlerta);
            panelAlertas.setVisible(true);
        } else {
            panelAlertas.setVisible(false);
        }
    }

    // ── Acciones ──────────────────────────────────────────────────────────────

    /**
     * Cierra la sesion activa y vuelve al formulario de login.
     */
    private void cerrarSesion() {
        controller.cerrarSesion();
        new LoginForm().setVisible(true);
        dispose();
    }

    // ── Recursos ──────────────────────────────────────────────────────────────

    /**
     * Carga el logo de la empresa en tamano reducido para el sidebar.
     *
     * @return ImageIcon del logo escalado, o icono vacio si no se encuentra
     */
    private ImageIcon cargarLogoSmall() {
        java.net.URL url = getClass().getClassLoader().getResource("img/logo_yogurin.jpg");
        if (url != null) {
            ImageIcon icon = new ImageIcon(url);
            Image img = icon.getImage().getScaledInstance(100, 80, Image.SCALE_SMOOTH);
            return new ImageIcon(img);
        }
        return new ImageIcon();
    }

    // ── Configuracion de ventana ──────────────────────────────────────────────

    private void configurarVentana() {
        setTitle("Panel Principal — Sistema de Gestion Yogurin Bustamante");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(960, 620);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(860, 540));
    }
}
