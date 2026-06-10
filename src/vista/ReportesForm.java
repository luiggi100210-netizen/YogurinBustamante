package vista;

import controlador.ReportesController;
import util.Tema;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.Map;

/**
 * Formulario de reportes y estadisticas.
 * Muestra ventas por dia, productos mas vendidos y rendimiento por vendedor.
 */
public class ReportesForm extends JFrame {

    private JTextField txtDesde, txtHasta;
    private JTable tablaReporte;
    private DefaultTableModel modeloTabla;
    private JComboBox<String> cmbTipoReporte;

    private final ReportesController controller;

    public ReportesForm() {
        this.controller = new ReportesController();
        inicializarComponentes();
        configurarVentana();
    }

    private void inicializarComponentes() {
        setLayout(new BorderLayout());
        getContentPane().setBackground(Tema.FONDO);

        add(Tema.header("Reportes y Estadisticas"), BorderLayout.NORTH);

        JPanel panelPrincipal = new JPanel(new BorderLayout(10, 10));
        panelPrincipal.setBackground(Tema.FONDO);
        panelPrincipal.setBorder(new EmptyBorder(12, 14, 12, 14));

        panelPrincipal.add(crearPanelFiltros(), BorderLayout.NORTH);
        panelPrincipal.add(crearPanelResultados(), BorderLayout.CENTER);

        add(panelPrincipal, BorderLayout.CENTER);
    }

    private JPanel crearPanelFiltros() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 10));
        panel.setBackground(Tema.BLANCO);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Tema.BORDE, 1),
            new EmptyBorder(4, 8, 4, 8)
        ));

        JLabel lbl = new JLabel("Filtros:");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(Tema.SIDEBAR);
        panel.add(lbl);

        String hoy = LocalDate.now().toString();
        txtDesde = new JTextField(hoy, 12);
        txtHasta = new JTextField(hoy, 12);
        txtDesde.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtHasta.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        cmbTipoReporte = new JComboBox<>(new String[]{
            "Ventas por dia", "Productos mas vendidos", "Ventas por vendedor"
        });
        cmbTipoReporte.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cmbTipoReporte.setPreferredSize(new Dimension(210, 30));

        JButton btnGenerar = Tema.botonPrimario("Generar Reporte");
        btnGenerar.setPreferredSize(new Dimension(150, 34));
        btnGenerar.addActionListener(e -> generarReporte());

        panel.add(new JLabel("Desde:"));
        panel.add(txtDesde);
        panel.add(new JLabel("Hasta:"));
        panel.add(txtHasta);
        panel.add(new JLabel("Tipo:"));
        panel.add(cmbTipoReporte);
        panel.add(btnGenerar);

        return panel;
    }

    private JPanel crearPanelResultados() {
        modeloTabla = new DefaultTableModel() {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tablaReporte = new JTable(modeloTabla);
        Tema.estilizarTabla(tablaReporte);

        JScrollPane scroll = new JScrollPane(tablaReporte);
        scroll.setBorder(BorderFactory.createLineBorder(Tema.BORDE, 1));
        scroll.getViewport().setBackground(Tema.BLANCO);

        JLabel lblSub = new JLabel("Resultados del reporte");
        lblSub.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblSub.setForeground(Tema.SIDEBAR);
        lblSub.setBorder(new EmptyBorder(8, 0, 4, 0));

        JPanel panel = new JPanel(new BorderLayout(0, 4));
        panel.setBackground(Tema.FONDO);
        panel.add(lblSub, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private void generarReporte() {
        String desde = txtDesde.getText().trim();
        String hasta = txtHasta.getText().trim();

        String errorRango = controller.validarRango(desde, hasta);
        if (errorRango != null) {
            JOptionPane.showMessageDialog(this, errorRango, "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int tipo = cmbTipoReporte.getSelectedIndex();
        modeloTabla.setRowCount(0);
        modeloTabla.setColumnCount(0);

        switch (tipo) {
            case 0 -> mostrarVentasPorDia(desde, hasta);
            case 1 -> mostrarProductosMasVendidos(desde, hasta);
            case 2 -> mostrarVentasPorVendedor(desde, hasta);
        }
    }

    private void mostrarVentasPorDia(String desde, String hasta) {
        modeloTabla.addColumn("Fecha");
        modeloTabla.addColumn("Total (S/)");
        Map<String, Double> datos = controller.getVentasPorDia(desde, hasta);
        datos.forEach((fecha, total) ->
            modeloTabla.addRow(new Object[]{fecha, String.format("%.2f", total)}));
    }

    private void mostrarProductosMasVendidos(String desde, String hasta) {
        modeloTabla.addColumn("Producto");
        modeloTabla.addColumn("Unidades Vendidas");
        Map<String, Integer> datos = controller.getProductosMasVendidos(desde, hasta);
        datos.forEach((nombre, cantidad) ->
            modeloTabla.addRow(new Object[]{nombre, cantidad}));
    }

    private void mostrarVentasPorVendedor(String desde, String hasta) {
        modeloTabla.addColumn("Vendedor");
        modeloTabla.addColumn("Total Vendido (S/)");
        Map<String, Double> datos = controller.getVentasPorVendedor(desde, hasta);
        datos.forEach((vendedor, total) ->
            modeloTabla.addRow(new Object[]{vendedor, String.format("%.2f", total)}));
    }

    private void configurarVentana() {
        setTitle("Reportes y Estadisticas");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(780, 520);
        setMinimumSize(new Dimension(680, 440));
        setLocationRelativeTo(null);
    }
}
