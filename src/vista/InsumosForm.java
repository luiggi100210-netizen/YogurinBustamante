package vista;

import controlador.InsumosController;
import modelo.Insumo;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Formulario de gestion de insumos.
 * Permite listar, agregar, editar y eliminar insumos de almacen.
 * Resalta visualmente los insumos con stock critico.
 *
 * @author Luiggi
 */
public class InsumosForm extends JFrame {

    private JTable tablaInsumos;
    private DefaultTableModel modeloTabla;
    private JTextField txtNombre, txtUnidad, txtStock, txtStockMinimo;
    private JButton btnGuardar, btnEditar, btnEliminar, btnLimpiar;

    /** ID del insumo seleccionado para edicion; 0 si es nuevo */
    private int idEditando = 0;

    private final InsumosController controller;

    public InsumosForm() {
        this.controller = new InsumosController();
        inicializarComponentes();
        cargarTabla();
        configurarVentana();
    }

    private void inicializarComponentes() {
        setLayout(new BorderLayout(10, 10));
        JPanel panelPrincipal = new JPanel(new BorderLayout(10, 10));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Formulario
        JPanel panelForm = new JPanel(new GridBagLayout());
        panelForm.setBorder(BorderFactory.createTitledBorder("Datos del Insumo"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 8, 5, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtNombre      = agregarCampo(panelForm, gbc, "Nombre:",        0);
        txtUnidad      = agregarCampo(panelForm, gbc, "Unidad:",         1);
        txtStock       = agregarCampo(panelForm, gbc, "Stock actual:",   2);
        txtStockMinimo = agregarCampo(panelForm, gbc, "Stock minimo:",   3);

        // Botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        btnGuardar  = new JButton("Guardar");
        btnEditar   = new JButton("Actualizar");
        btnEliminar = new JButton("Eliminar");
        btnLimpiar  = new JButton("Limpiar");

        btnEditar.setEnabled(false);
        btnEliminar.setEnabled(false);

        btnGuardar.addActionListener(e -> guardar());
        btnEditar.addActionListener(e -> actualizar());
        btnEliminar.addActionListener(e -> eliminar());
        btnLimpiar.addActionListener(e -> limpiarFormulario());

        panelBotones.add(btnGuardar);
        panelBotones.add(btnEditar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnLimpiar);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        panelForm.add(panelBotones, gbc);

        // Tabla
        String[] columnas = {"ID", "Nombre", "Unidad", "Stock", "Stock Min.", "Estado"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tablaInsumos = new JTable(modeloTabla);
        tablaInsumos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaInsumos.getSelectionModel().addListSelectionListener(e -> cargarSeleccion());

        JScrollPane scroll = new JScrollPane(tablaInsumos);

        panelPrincipal.add(panelForm, BorderLayout.NORTH);
        panelPrincipal.add(scroll, BorderLayout.CENTER);
        add(panelPrincipal);
    }

    private JTextField agregarCampo(JPanel panel, GridBagConstraints gbc,
                                    String etiqueta, int fila) {
        gbc.gridx = 0; gbc.gridy = fila; gbc.gridwidth = 1;
        panel.add(new JLabel(etiqueta), gbc);
        JTextField campo = new JTextField(20);
        gbc.gridx = 1;
        panel.add(campo, gbc);
        return campo;
    }

    private void cargarTabla() {
        modeloTabla.setRowCount(0);
        List<Insumo> insumos = controller.listarInsumos();
        for (Insumo i : insumos) {
            String estado = i.necesitaReabastecimiento() ? "CRITICO" : "OK";
            modeloTabla.addRow(new Object[]{
                i.getId(), i.getNombre(), i.getUnidad(),
                i.getStock(), i.getStockMinimo(), estado
            });
        }
    }

    private void cargarSeleccion() {
        int fila = tablaInsumos.getSelectedRow();
        if (fila < 0) return;

        idEditando = (int) modeloTabla.getValueAt(fila, 0);
        txtNombre.setText(modeloTabla.getValueAt(fila, 1).toString());
        txtUnidad.setText(modeloTabla.getValueAt(fila, 2).toString());
        txtStock.setText(modeloTabla.getValueAt(fila, 3).toString());
        txtStockMinimo.setText(modeloTabla.getValueAt(fila, 4).toString());

        btnGuardar.setEnabled(false);
        btnEditar.setEnabled(true);
        btnEliminar.setEnabled(true);
    }

    private void guardar() {
        String error = controller.guardarInsumo(
            txtNombre.getText(), txtUnidad.getText(),
            txtStock.getText(), txtStockMinimo.getText());
        mostrarResultado(error, "Insumo guardado correctamente.");
    }

    private void actualizar() {
        String error = controller.actualizarInsumo(
            idEditando, txtNombre.getText(), txtUnidad.getText(),
            txtStock.getText(), txtStockMinimo.getText());
        mostrarResultado(error, "Insumo actualizado correctamente.");
    }

    private void eliminar() {
        int confirmar = JOptionPane.showConfirmDialog(this,
            "¿Desea eliminar este insumo?", "Confirmar", JOptionPane.YES_NO_OPTION);

        if (confirmar == JOptionPane.YES_OPTION) {
            String error = controller.eliminarInsumo(idEditando);
            mostrarResultado(error, "Insumo eliminado correctamente.");
        }
    }

    private void mostrarResultado(String error, String mensajeExito) {
        if (error != null) {
            JOptionPane.showMessageDialog(this, error, "Error", JOptionPane.ERROR_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, mensajeExito, "Exito",
                    JOptionPane.INFORMATION_MESSAGE);
            limpiarFormulario();
            cargarTabla();
        }
    }

    private void limpiarFormulario() {
        idEditando = 0;
        txtNombre.setText("");
        txtUnidad.setText("");
        txtStock.setText("");
        txtStockMinimo.setText("");
        tablaInsumos.clearSelection();
        btnGuardar.setEnabled(true);
        btnEditar.setEnabled(false);
        btnEliminar.setEnabled(false);
    }

    private void configurarVentana() {
        setTitle("Gestion de Insumos");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(700, 520);
        setLocationRelativeTo(null);
    }
}
