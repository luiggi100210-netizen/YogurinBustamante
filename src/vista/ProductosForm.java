package vista;

import controlador.ProductosController;
import modelo.Producto;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Formulario de gestion de productos.
 * Permite listar, agregar, editar y eliminar productos.
 *
 * @author Luiggi
 */
public class ProductosForm extends JFrame {

    private JTable tablaProductos;
    private DefaultTableModel modeloTabla;
    private JTextField txtNombre, txtPrecio, txtStock, txtDescripcion;
    private JButton btnGuardar, btnEditar, btnEliminar, btnLimpiar;

    /** ID del producto seleccionado para edicion; 0 si es nuevo */
    private int idEditando = 0;

    private final ProductosController controller;

    public ProductosForm() {
        this.controller = new ProductosController();
        inicializarComponentes();
        cargarTabla();
        configurarVentana();
    }

    private void inicializarComponentes() {
        setLayout(new BorderLayout(10, 10));
        JPanel panelPrincipal = new JPanel(new BorderLayout(10, 10));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Formulario de campos
        JPanel panelForm = new JPanel(new GridBagLayout());
        panelForm.setBorder(BorderFactory.createTitledBorder("Datos del Producto"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 8, 5, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtNombre      = agregarCampo(panelForm, gbc, "Nombre:",      0);
        txtPrecio      = agregarCampo(panelForm, gbc, "Precio (S/):", 1);
        txtStock       = agregarCampo(panelForm, gbc, "Stock:",        2);
        txtDescripcion = agregarCampo(panelForm, gbc, "Descripcion:",  3);

        // Botones del formulario
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

        // Tabla de productos
        String[] columnas = {"ID", "Nombre", "Precio", "Stock", "Descripcion"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tablaProductos = new JTable(modeloTabla);
        tablaProductos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaProductos.getSelectionModel().addListSelectionListener(e -> cargarSeleccion());

        JScrollPane scroll = new JScrollPane(tablaProductos);

        panelPrincipal.add(panelForm, BorderLayout.NORTH);
        panelPrincipal.add(scroll, BorderLayout.CENTER);
        add(panelPrincipal);
    }

    /** Crea un par etiqueta-campo y lo agrega al panel del formulario. */
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
        List<Producto> productos = controller.listarProductos();
        for (Producto p : productos) {
            modeloTabla.addRow(new Object[]{
                p.getId(), p.getNombre(),
                String.format("S/ %.2f", p.getPrecio()),
                p.getStock(), p.getDescripcion()
            });
        }
    }

    /** Rellena el formulario con el producto seleccionado en la tabla. */
    private void cargarSeleccion() {
        int fila = tablaProductos.getSelectedRow();
        if (fila < 0) return;

        idEditando = (int) modeloTabla.getValueAt(fila, 0);
        txtNombre.setText(modeloTabla.getValueAt(fila, 1).toString());
        txtPrecio.setText(modeloTabla.getValueAt(fila, 2).toString().replace("S/ ", ""));
        txtStock.setText(modeloTabla.getValueAt(fila, 3).toString());
        txtDescripcion.setText(modeloTabla.getValueAt(fila, 4).toString());

        btnGuardar.setEnabled(false);
        btnEditar.setEnabled(true);
        btnEliminar.setEnabled(true);
    }

    private void guardar() {
        String error = controller.guardarProducto(
            txtNombre.getText(), txtPrecio.getText(),
            txtStock.getText(), txtDescripcion.getText());

        mostrarResultado(error, "Producto guardado correctamente.");
    }

    private void actualizar() {
        String error = controller.actualizarProducto(
            idEditando, txtNombre.getText(), txtPrecio.getText(),
            txtStock.getText(), txtDescripcion.getText());

        mostrarResultado(error, "Producto actualizado correctamente.");
    }

    private void eliminar() {
        int confirmar = JOptionPane.showConfirmDialog(this,
            "¿Desea eliminar este producto?", "Confirmar", JOptionPane.YES_NO_OPTION);

        if (confirmar == JOptionPane.YES_OPTION) {
            String error = controller.eliminarProducto(idEditando);
            mostrarResultado(error, "Producto eliminado correctamente.");
        }
    }

    /** Muestra un mensaje y recarga la tabla si la operacion fue exitosa. */
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
        txtPrecio.setText("");
        txtStock.setText("");
        txtDescripcion.setText("");
        tablaProductos.clearSelection();
        btnGuardar.setEnabled(true);
        btnEditar.setEnabled(false);
        btnEliminar.setEnabled(false);
    }

    private void configurarVentana() {
        setTitle("Gestion de Productos");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(700, 520);
        setLocationRelativeTo(null);
    }
}
