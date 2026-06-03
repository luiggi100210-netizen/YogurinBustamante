package vista;

import controlador.ReportesController;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.Map;

/**
 * Formulario de reportes y estadisticas.
 * Muestra ventas por dia, productos mas vendidos y rendimiento por vendedor.
 *
 * @author Luiggi
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
        setLayout(new BorderLayout(10, 10));
        JPanel panelPrincipal = new JPanel(new BorderLayout(10, 10));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Panel de filtros
        JPanel panelFiltros = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panelFiltros.setBorder(BorderFactory.createTitledBorder("Filtros"));

        String hoy = LocalDate.now().toString();
        txtDesde = new JTextField(hoy, 12);
        txtHasta = new JTextField(hoy, 12);
        cmbTipoReporte = new JComboBox<>(new String[]{
            "Ventas por dia", "Productos mas vendidos", "Ventas por vendedor"
        });

        JButton btnGenerar = new JButton("Generar Reporte");
        btnGenerar.setBackground(new Color(0x3B6FA0));
        btnGenerar.setForeground(Color.WHITE);
        btnGenerar.addActionListener(e -> generarReporte());

        panelFiltros.add(new JLabel("Desde:"));
        panelFiltros.add(txtDesde);
        panelFiltros.add(new JLabel("Hasta:"));
        panelFiltros.add(txtHasta);
        panelFiltros.add(new JLabel("Tipo:"));
        panelFiltros.add(cmbTipoReporte);
        panelFiltros.add(btnGenerar);

        // Tabla de resultados
        modeloTabla = new DefaultTableModel() {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tablaReporte = new JTable(modeloTabla);
        JScrollPane scroll = new JScrollPane(tablaReporte);
        scroll.setBorder(BorderFactory.createTitledBorder("Resultados"));

        panelPrincipal.add(panelFiltros, BorderLayout.NORTH);
        panelPrincipal.add(scroll, BorderLayout.CENTER);
        add(panelPrincipal);
    }

    private void generarReporte() {
        String desde = txtDesde.getText().trim();
        String hasta = txtHasta.getText().trim();

        String errorRango = controller.validarRango(desde, hasta);
        if (errorRango != null) {
            JOptionPane.showMessageDialog(this, errorRango, "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int tipoSeleccionado = cmbTipoReporte.getSelectedIndex();
        modeloTabla.setRowCount(0);
        modeloTabla.setColumnCount(0);

        switch (tipoSeleccionado) {
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
        setSize(700, 480);
        setLocationRelativeTo(null);
    }
}
