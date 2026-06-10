package vista;

import controlador.InsumosController;
import modelo.Insumo;
import util.Tema;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.List;

/**
 * Formulario de gestion de insumos.
 * Permite listar, agregar, editar y eliminar insumos de almacen.
 * Resalta visualmente los insumos con stock critico.
 */
public class InsumosForm extends JFrame {

    private JTable tablaInsumos;
    private DefaultTableModel modeloTabla;
    private JTextField txtNombre, txtUnidad, txtStock, txtStockMinimo;
    private JButton btnGuardar, btnEditar, btnEliminar, btnLimpiar;
    private JLabel lblHint;

    private int idEditando = 0;

    private final InsumosController controller;

    public InsumosForm() {
        this.controller = new InsumosController();
        inicializarComponentes();
        cargarTabla();
        configurarVentana();
    }

    private void inicializarComponentes() {
        setLayout(new BorderLayout());
        getContentPane().setBackground(Tema.FONDO);

        add(Tema.header("Gestion de Insumos / Inventario"), BorderLayout.NORTH);

        JPanel panelPrincipal = new JPanel(new BorderLayout(10, 10));
        panelPrincipal.setBackground(Tema.FONDO);
        panelPrincipal.setBorder(new EmptyBorder(12, 14, 12, 14));

        panelPrincipal.add(crearPanelFormulario(), BorderLayout.NORTH);
        panelPrincipal.add(crearPanelTabla(),      BorderLayout.CENTER);

        add(panelPrincipal, BorderLayout.CENTER);
    }

    private JPanel crearPanelFormulario() {
        JPanel panelForm = new JPanel(new GridBagLayout());
        panelForm.setBackground(Tema.BLANCO);
        panelForm.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Tema.BORDE, 1),
            new EmptyBorder(12, 14, 8, 14)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 8, 5, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtNombre      = agregarCampo(panelForm, gbc, "Nombre:",       0);
        txtUnidad      = agregarCampo(panelForm, gbc, "Unidad:",        1);
        txtStock       = agregarCampo(panelForm, gbc, "Stock actual:",  2);
        txtStockMinimo = agregarCampo(panelForm, gbc, "Stock minimo:",  3);

        btnGuardar  = Tema.botonExito("Guardar");
        btnEditar   = Tema.botonAdvertencia("Actualizar");
        btnEliminar = Tema.botonPeligro("Eliminar");
        btnLimpiar  = Tema.botonNeutro("Limpiar");

        btnEditar.setEnabled(false);
        btnEliminar.setEnabled(false);

        btnGuardar.addActionListener(e  -> guardar());
        btnEditar.addActionListener(e   -> actualizar());
        btnEliminar.addActionListener(e -> eliminar());
        btnLimpiar.addActionListener(e  -> limpiarFormulario());

        lblHint = Tema.hint("Haga clic en una fila para editar o eliminar");

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 4));
        panelBotones.setBackground(Tema.BLANCO);
        panelBotones.add(btnGuardar);
        panelBotones.add(btnEditar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnLimpiar);

        JPanel panelAcciones = new JPanel(new BorderLayout());
        panelAcciones.setBackground(Tema.BLANCO);
        lblHint.setHorizontalAlignment(SwingConstants.CENTER);
        panelAcciones.add(panelBotones, BorderLayout.CENTER);
        panelAcciones.add(lblHint, BorderLayout.SOUTH);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        panelForm.add(panelAcciones, gbc);

        return panelForm;
    }

    private JPanel crearPanelTabla() {
        String[] columnas = {"ID", "Nombre", "Unidad", "Stock", "Stock Min.", "Estado"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tablaInsumos = new JTable(modeloTabla);
        tablaInsumos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaInsumos.getSelectionModel().addListSelectionListener(e -> cargarSeleccion());
        Tema.estilizarTabla(tablaInsumos);

        // Renderer para colorear filas criticas
        tablaInsumos.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v,
                    boolean sel, boolean focus, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, v, sel, focus, row, col);
                String estado = modeloTabla.getValueAt(row, 5) != null
                    ? modeloTabla.getValueAt(row, 5).toString() : "";
                if (sel) {
                    c.setBackground(new Color(0xBBD3F0));
                } else if ("CRITICO".equals(estado)) {
                    c.setBackground(new Color(0xFFF3CD));
                } else {
                    c.setBackground(Tema.BLANCO);
                }
                return c;
            }
        });

        JScrollPane scroll = new JScrollPane(tablaInsumos);
        scroll.setBorder(BorderFactory.createLineBorder(Tema.BORDE, 1));
        scroll.getViewport().setBackground(Tema.BLANCO);

        JLabel lblSub = new JLabel("Listado de insumos  —  filas en amarillo indican stock critico");
        lblSub.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblSub.setForeground(Tema.SIDEBAR);
        lblSub.setBorder(new EmptyBorder(8, 0, 4, 0));

        JPanel panel = new JPanel(new BorderLayout(0, 4));
        panel.setBackground(Tema.FONDO);
        panel.add(lblSub, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private JTextField agregarCampo(JPanel panel, GridBagConstraints gbc,
                                    String etiqueta, int fila) {
        gbc.gridx = 0; gbc.gridy = fila; gbc.gridwidth = 1;
        JLabel lbl = new JLabel(etiqueta);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lbl.setForeground(Tema.NEUTRO);
        panel.add(lbl, gbc);
        JTextField campo = new JTextField(20);
        campo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
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
        lblHint.setText("Insumo seleccionado — puede actualizar o eliminar");
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
            "Desea eliminar este insumo?", "Confirmar", JOptionPane.YES_NO_OPTION);
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
        lblHint.setText("Haga clic en una fila para editar o eliminar");
    }

    private void configurarVentana() {
        setTitle("Gestion de Insumos");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(780, 580);
        setMinimumSize(new Dimension(680, 500));
        setLocationRelativeTo(null);
    }
}
