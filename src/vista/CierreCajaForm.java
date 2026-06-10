package vista;

import controlador.ReportesController;
import util.Tema;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.util.Map;

/**
 * Formulario de cierre de caja diario.
 * Muestra el resumen de ventas del dia: numero de transacciones y monto total.
 */
public class CierreCajaForm extends JFrame {

    private JLabel lblFecha;
    private JLabel lblNumVentas;
    private JLabel lblTotalDia;
    private JTextArea areaDetalle;

    private final ReportesController controller;

    public CierreCajaForm() {
        this.controller = new ReportesController();
        inicializarComponentes();
        cargarResumen();
        configurarVentana();
    }

    private void inicializarComponentes() {
        setLayout(new BorderLayout());
        getContentPane().setBackground(Tema.FONDO);

        add(Tema.header("Cierre de Caja  —  " + LocalDate.now()), BorderLayout.NORTH);

        JPanel panelPrincipal = new JPanel(new BorderLayout(10, 14));
        panelPrincipal.setBackground(Tema.FONDO);
        panelPrincipal.setBorder(new EmptyBorder(14, 16, 14, 16));

        panelPrincipal.add(crearPanelResumen(), BorderLayout.NORTH);
        panelPrincipal.add(crearPanelDetalle(), BorderLayout.CENTER);
        panelPrincipal.add(crearBotonImprimir(), BorderLayout.SOUTH);

        add(panelPrincipal, BorderLayout.CENTER);
    }

    private JPanel crearPanelResumen() {
        JPanel panel = new JPanel(new GridLayout(1, 3, 12, 0));
        panel.setBackground(Tema.FONDO);
        panel.setPreferredSize(new Dimension(0, 100));

        lblFecha     = new JLabel("—", SwingConstants.CENTER);
        lblNumVentas = new JLabel("—", SwingConstants.CENTER);
        lblTotalDia  = new JLabel("S/ 0.00", SwingConstants.CENTER);

        panel.add(crearKPI(lblFecha,     "Fecha",            new Color(0x1A6FBA)));
        panel.add(crearKPI(lblNumVentas, "Numero de Ventas", new Color(0x1A8A4A)));
        panel.add(crearKPI(lblTotalDia,  "Total del Dia",    new Color(0xE07B00)));

        return panel;
    }

    private JPanel crearKPI(JLabel lblValor, String titulo, Color color) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Tema.BLANCO);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 4, 0, 0, color),
            new EmptyBorder(12, 14, 12, 14)
        ));

        lblValor.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblValor.setForeground(color);
        lblValor.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblTitulo.setForeground(Tema.TEXTO_GRIS);
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(Box.createVerticalGlue());
        card.add(lblValor);
        card.add(Box.createVerticalStrut(4));
        card.add(lblTitulo);
        card.add(Box.createVerticalGlue());
        return card;
    }

    private JPanel crearPanelDetalle() {
        areaDetalle = new JTextArea();
        areaDetalle.setEditable(false);
        areaDetalle.setFont(new Font("Monospaced", Font.PLAIN, 12));
        areaDetalle.setBackground(Tema.BLANCO);
        areaDetalle.setBorder(new EmptyBorder(8, 10, 8, 10));

        JScrollPane scroll = new JScrollPane(areaDetalle);
        scroll.setBorder(BorderFactory.createLineBorder(Tema.BORDE, 1));

        JLabel lblSub = new JLabel("Detalle por vendedor");
        lblSub.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblSub.setForeground(Tema.SIDEBAR);
        lblSub.setBorder(new EmptyBorder(8, 0, 4, 0));

        JPanel panel = new JPanel(new BorderLayout(0, 4));
        panel.setBackground(Tema.FONDO);
        panel.add(lblSub, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearBotonImprimir() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 6));
        panel.setBackground(Tema.FONDO);
        JButton btnImprimir = Tema.botonPrimario("Imprimir Cierre");
        btnImprimir.addActionListener(e ->
            JOptionPane.showMessageDialog(this,
                "Funcion de impresion disponible en version futura.",
                "Info", JOptionPane.INFORMATION_MESSAGE));
        panel.add(btnImprimir);
        return panel;
    }

    private void cargarResumen() {
        String hoy = LocalDate.now().toString();
        lblFecha.setText(hoy);

        double[] resumen = new double[]{0, 0};
        try {
            resumen = obtenerResumen();
        } catch (Exception e) {
            System.err.println("Error al cargar cierre de caja: " + e.getMessage());
        }

        lblNumVentas.setText(String.valueOf((int) resumen[0]));
        lblTotalDia.setText("S/ " + String.format("%.2f", resumen[1]));

        Map<String, Double> porVendedor = controller.getVentasPorVendedor(hoy, hoy);
        StringBuilder sb = new StringBuilder();
        porVendedor.forEach((vendedor, total) ->
            sb.append(String.format("%-25s S/ %.2f%n", vendedor, total)));
        areaDetalle.setText(sb.length() > 0 ? sb.toString() : "Sin ventas registradas hoy.");
    }

    private double[] obtenerResumen() {
        try {
            dao.ReporteDAO dao = new dao.ReporteDAO();
            return dao.resumenCierreCaja();
        } catch (java.sql.SQLException e) {
            System.err.println("Error al obtener resumen: " + e.getMessage());
            return new double[]{0, 0};
        }
    }

    private void configurarVentana() {
        setTitle("Cierre de Caja — " + LocalDate.now());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(580, 480);
        setMinimumSize(new Dimension(520, 420));
        setLocationRelativeTo(null);
    }
}
