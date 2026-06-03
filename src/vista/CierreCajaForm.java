package vista;

import controlador.ReportesController;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.Map;

/**
 * Formulario de cierre de caja diario.
 * Muestra el resumen de ventas del dia: numero de transacciones y monto total.
 *
 * @author Luiggi
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
        setLayout(new BorderLayout(10, 10));
        JPanel panelPrincipal = new JPanel(new BorderLayout(10, 10));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Titulo
        JLabel lblTitulo = new JLabel("CIERRE DE CAJA", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 20));
        lblTitulo.setForeground(new Color(0x3B6FA0));

        // Indicadores
        JPanel panelIndicadores = new JPanel(new GridLayout(3, 2, 10, 10));
        panelIndicadores.setBorder(BorderFactory.createTitledBorder("Resumen del Dia"));

        lblFecha     = new JLabel();
        lblNumVentas = new JLabel();
        lblTotalDia  = new JLabel();

        lblFecha.setFont(new Font("Arial", Font.PLAIN, 14));
        lblNumVentas.setFont(new Font("Arial", Font.PLAIN, 14));
        lblTotalDia.setFont(new Font("Arial", Font.BOLD, 18));
        lblTotalDia.setForeground(new Color(0x2E7D32));

        panelIndicadores.add(new JLabel("Fecha:", SwingConstants.RIGHT));
        panelIndicadores.add(lblFecha);
        panelIndicadores.add(new JLabel("Numero de ventas:", SwingConstants.RIGHT));
        panelIndicadores.add(lblNumVentas);
        panelIndicadores.add(new JLabel("Total del dia:", SwingConstants.RIGHT));
        panelIndicadores.add(lblTotalDia);

        // Detalle por vendedor
        areaDetalle = new JTextArea(8, 40);
        areaDetalle.setEditable(false);
        areaDetalle.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scroll = new JScrollPane(areaDetalle);
        scroll.setBorder(BorderFactory.createTitledBorder("Ventas por Vendedor"));

        JButton btnImprimir = new JButton("Imprimir Cierre");
        btnImprimir.addActionListener(e ->
            JOptionPane.showMessageDialog(this,
                "Funcion de impresion disponible en version futura.",
                "Info", JOptionPane.INFORMATION_MESSAGE));

        panelPrincipal.add(lblTitulo, BorderLayout.NORTH);
        panelPrincipal.add(panelIndicadores, BorderLayout.CENTER);
        panelPrincipal.add(scroll, BorderLayout.SOUTH);
        add(panelPrincipal, BorderLayout.CENTER);
        add(btnImprimir, BorderLayout.SOUTH);
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

    /** Delega al controlador para obtener el resumen del dia. */
    private double[] obtenerResumen() {
        // Usa directamente el DAO a traves del controlador de reportes
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
        setSize(520, 480);
        setLocationRelativeTo(null);
    }
}
