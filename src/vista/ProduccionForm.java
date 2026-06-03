package vista;

import controlador.ProduccionController;
import modelo.Insumo;
import modelo.LoteProduccion;
import modelo.Producto;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Formulario de registro de lotes de produccion.
 * Permite seleccionar un producto, ingresar las unidades producidas
 * y registrar los insumos utilizados con sus cantidades.
 *
 * @author Luiggi
 */
public class ProduccionForm extends JFrame {

    // Datos del lote
    private JComboBox<Producto> cmbProducto;
    private JTextField txtUnidades;

    // Insumos
    private JComboBox<Insumo> cmbInsumo;
    private JTextField txtCantidadInsumo;
    private JTable tablaInsumos;
    private DefaultTableModel modeloInsumos;

    // Historial
    private JTable tablaHistorial;
    private DefaultTableModel modeloHistorial;

    private final ProduccionController controller;

    /** Mapa temporal de insumos seleccionados para el lote actual */
    private final Map<Insumo, Double> insumosSeleccionados;

    public ProduccionForm() {
        this.controller = new ProduccionController();
        this.insumosSeleccionados = new HashMap<>();
        inicializarComponentes();
        cargarCombos();
        cargarHistorial();
        configurarVentana();
    }

    private void inicializarComponentes() {
        setLayout(new BorderLayout(10, 10));
        JPanel panelPrincipal = new JPanel(new BorderLayout(10, 10));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Panel superior — datos del lote
        JPanel panelLote = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panelLote.setBorder(BorderFactory.createTitledBorder("Datos del Lote"));
        cmbProducto = new JComboBox<>();
        cmbProducto.setPreferredSize(new Dimension(200, 25));
        txtUnidades = new JTextField(6);
        panelLote.add(new JLabel("Producto:"));
        panelLote.add(cmbProducto);
        panelLote.add(new JLabel("Unidades producidas:"));
        panelLote.add(txtUnidades);

        // Panel insumos
        JPanel panelInsumos = new JPanel(new BorderLayout(5, 5));
        panelInsumos.setBorder(BorderFactory.createTitledBorder("Insumos Utilizados"));

        JPanel panelAgregarInsumo = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        cmbInsumo = new JComboBox<>();
        cmbInsumo.setPreferredSize(new Dimension(180, 25));
        txtCantidadInsumo = new JTextField(6);
        JButton btnAgregarInsumo = new JButton("Agregar Insumo");
        btnAgregarInsumo.addActionListener(e -> agregarInsumo());
        panelAgregarInsumo.add(new JLabel("Insumo:"));
        panelAgregarInsumo.add(cmbInsumo);
        panelAgregarInsumo.add(new JLabel("Cantidad:"));
        panelAgregarInsumo.add(txtCantidadInsumo);
        panelAgregarInsumo.add(btnAgregarInsumo);

        String[] colInsumos = {"Insumo", "Cantidad"};
        modeloInsumos = new DefaultTableModel(colInsumos, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tablaInsumos = new JTable(modeloInsumos);

        panelInsumos.add(panelAgregarInsumo, BorderLayout.NORTH);
        panelInsumos.add(new JScrollPane(tablaInsumos), BorderLayout.CENTER);

        // Boton registrar lote
        JButton btnRegistrar = new JButton("Registrar Lote");
        btnRegistrar.setBackground(new Color(0x2E7D32));
        btnRegistrar.setForeground(Color.WHITE);
        btnRegistrar.setFont(new Font("Arial", Font.BOLD, 13));
        btnRegistrar.addActionListener(e -> registrarLote());

        JPanel panelSuperior = new JPanel(new BorderLayout(5, 5));
        panelSuperior.add(panelLote, BorderLayout.NORTH);
        panelSuperior.add(panelInsumos, BorderLayout.CENTER);
        panelSuperior.add(btnRegistrar, BorderLayout.SOUTH);

        // Historial
        String[] colHistorial = {"Lote #", "Fecha", "Producto", "Unidades"};
        modeloHistorial = new DefaultTableModel(colHistorial, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tablaHistorial = new JTable(modeloHistorial);
        JScrollPane scrollHistorial = new JScrollPane(tablaHistorial);
        scrollHistorial.setBorder(BorderFactory.createTitledBorder("Historial de Lotes"));

        panelPrincipal.add(panelSuperior, BorderLayout.NORTH);
        panelPrincipal.add(scrollHistorial, BorderLayout.CENTER);
        add(panelPrincipal);
    }

    private void cargarCombos() {
        controller.getProductos().forEach(cmbProducto::addItem);
        controller.getInsumos().forEach(cmbInsumo::addItem);
    }

    private void cargarHistorial() {
        modeloHistorial.setRowCount(0);
        List<LoteProduccion> lotes = controller.listarLotes();
        for (LoteProduccion l : lotes) {
            modeloHistorial.addRow(new Object[]{
                l.getId(), l.getFecha(),
                l.getProducto().getNombre(), l.getUnidades()
            });
        }
    }

    private void agregarInsumo() {
        Insumo insumo = (Insumo) cmbInsumo.getSelectedItem();
        if (insumo == null) return;

        try {
            double cantidad = Double.parseDouble(txtCantidadInsumo.getText().trim());
            if (cantidad <= 0) throw new NumberFormatException();

            insumosSeleccionados.put(insumo, cantidad);
            modeloInsumos.addRow(new Object[]{insumo.getNombre(), cantidad});
            txtCantidadInsumo.setText("");

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Ingrese una cantidad valida.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void registrarLote() {
        Producto producto = (Producto) cmbProducto.getSelectedItem();
        String error = controller.registrarLote(producto, txtUnidades.getText(),
                insumosSeleccionados);

        if (error != null) {
            JOptionPane.showMessageDialog(this, error, "Error", JOptionPane.ERROR_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Lote registrado correctamente.",
                    "Exito", JOptionPane.INFORMATION_MESSAGE);
            insumosSeleccionados.clear();
            modeloInsumos.setRowCount(0);
            txtUnidades.setText("");
            cargarHistorial();
        }
    }

    private void configurarVentana() {
        setTitle("Registro de Produccion");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(700, 560);
        setLocationRelativeTo(null);
    }
}
