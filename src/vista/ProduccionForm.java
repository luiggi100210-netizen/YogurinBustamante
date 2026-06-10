package vista;

import controlador.ProduccionController;
import modelo.Insumo;
import modelo.LoteProduccion;
import modelo.Producto;
import util.Tema;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Formulario de registro de lotes de produccion.
 * Permite seleccionar un producto, ingresar unidades producidas
 * y registrar los insumos utilizados con sus cantidades.
 */
public class ProduccionForm extends JFrame {

    private JComboBox<Producto> cmbProducto;
    private JTextField txtUnidades;

    private JComboBox<Insumo> cmbInsumo;
    private JTextField txtCantidadInsumo;
    private JTable tablaInsumos;
    private DefaultTableModel modeloInsumos;

    private JTable tablaHistorial;
    private DefaultTableModel modeloHistorial;

    private final ProduccionController controller;
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
        setLayout(new BorderLayout());
        getContentPane().setBackground(Tema.FONDO);

        add(Tema.header("Registro de Produccion"), BorderLayout.NORTH);

        JPanel panelPrincipal = new JPanel(new BorderLayout(10, 10));
        panelPrincipal.setBackground(Tema.FONDO);
        panelPrincipal.setBorder(new EmptyBorder(12, 14, 12, 14));

        // Panel superior: lote + insumos + boton registrar
        JPanel panelSuperior = new JPanel(new BorderLayout(0, 8));
        panelSuperior.setBackground(Tema.FONDO);
        panelSuperior.add(crearPanelDatosLote(), BorderLayout.NORTH);
        panelSuperior.add(crearPanelInsumos(),   BorderLayout.CENTER);
        panelSuperior.add(crearPanelBotonRegistrar(), BorderLayout.SOUTH);

        panelPrincipal.add(panelSuperior,        BorderLayout.NORTH);
        panelPrincipal.add(crearPanelHistorial(), BorderLayout.CENTER);

        add(panelPrincipal, BorderLayout.CENTER);
    }

    private JPanel crearPanelDatosLote() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        panel.setBackground(Tema.BLANCO);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Tema.BORDE, 1),
            new EmptyBorder(2, 4, 2, 4)
        ));

        JLabel lbl = new JLabel("Datos del Lote:");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(Tema.SIDEBAR);
        panel.add(lbl);

        panel.add(new JLabel("Producto:"));
        cmbProducto = new JComboBox<>();
        cmbProducto.setPreferredSize(new Dimension(220, 30));
        cmbProducto.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        panel.add(cmbProducto);

        panel.add(new JLabel("Unidades producidas:"));
        txtUnidades = new JTextField(8);
        txtUnidades.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        panel.add(txtUnidades);

        return panel;
    }

    private JPanel crearPanelInsumos() {
        JPanel panel = new JPanel(new BorderLayout(0, 6));
        panel.setBackground(Tema.BLANCO);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Tema.BORDE, 1),
            new EmptyBorder(8, 12, 8, 12)
        ));

        JLabel lblTitulo = new JLabel("Insumos Utilizados");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTitulo.setForeground(Tema.SIDEBAR);

        // Fila de agregar insumo
        JPanel panelAgregar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 4));
        panelAgregar.setBackground(Tema.BLANCO);

        cmbInsumo = new JComboBox<>();
        cmbInsumo.setPreferredSize(new Dimension(200, 30));
        cmbInsumo.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        txtCantidadInsumo = new JTextField(8);
        txtCantidadInsumo.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        JButton btnAgregarInsumo = Tema.botonPrimario("Agregar");
        btnAgregarInsumo.setPreferredSize(new Dimension(90, 30));
        btnAgregarInsumo.addActionListener(e -> agregarInsumo());

        panelAgregar.add(new JLabel("Insumo:"));
        panelAgregar.add(cmbInsumo);
        panelAgregar.add(new JLabel("Cantidad:"));
        panelAgregar.add(txtCantidadInsumo);
        panelAgregar.add(btnAgregarInsumo);

        String[] colInsumos = {"Insumo", "Cantidad"};
        modeloInsumos = new DefaultTableModel(colInsumos, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tablaInsumos = new JTable(modeloInsumos);
        Tema.estilizarTabla(tablaInsumos);
        tablaInsumos.setPreferredScrollableViewportSize(new Dimension(0, 90));

        JScrollPane scroll = new JScrollPane(tablaInsumos);
        scroll.setBorder(BorderFactory.createLineBorder(Tema.BORDE, 1));

        JPanel panelNorte = new JPanel(new BorderLayout());
        panelNorte.setBackground(Tema.BLANCO);
        panelNorte.add(lblTitulo, BorderLayout.NORTH);
        panelNorte.add(panelAgregar, BorderLayout.CENTER);

        panel.add(panelNorte, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearPanelBotonRegistrar() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        panel.setBackground(Tema.FONDO);

        JButton btnRegistrar = Tema.botonExito("Registrar Lote de Produccion");
        btnRegistrar.setPreferredSize(new Dimension(220, 38));
        btnRegistrar.addActionListener(e -> registrarLote());
        panel.add(btnRegistrar);

        return panel;
    }

    private JPanel crearPanelHistorial() {
        String[] colHistorial = {"Lote #", "Fecha", "Producto", "Unidades"};
        modeloHistorial = new DefaultTableModel(colHistorial, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tablaHistorial = new JTable(modeloHistorial);
        Tema.estilizarTabla(tablaHistorial);

        JScrollPane scrollHistorial = new JScrollPane(tablaHistorial);
        scrollHistorial.setBorder(BorderFactory.createLineBorder(Tema.BORDE, 1));
        scrollHistorial.getViewport().setBackground(Tema.BLANCO);

        JLabel lblSub = new JLabel("Historial de lotes registrados");
        lblSub.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblSub.setForeground(Tema.SIDEBAR);
        lblSub.setBorder(new EmptyBorder(8, 0, 4, 0));

        JPanel panel = new JPanel(new BorderLayout(0, 4));
        panel.setBackground(Tema.FONDO);
        panel.add(lblSub, BorderLayout.NORTH);
        panel.add(scrollHistorial, BorderLayout.CENTER);
        return panel;
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
        setSize(800, 620);
        setMinimumSize(new Dimension(720, 560));
        setLocationRelativeTo(null);
    }
}
