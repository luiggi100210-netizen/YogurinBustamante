package vista;

import controlador.ClientesController;
import controlador.VentasController;
import modelo.Cliente;
import modelo.DetalleVenta;
import modelo.Producto;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Formulario de registro de ventas.
 * Permite buscar clientes, seleccionar productos y registrar la venta.
 *
 * @author Luiggi
 */
public class VentasForm extends JFrame {

    // Busqueda de cliente
    private JTextField txtDni;
    private JLabel lblNombreCliente;
    private Cliente clienteSeleccionado;

    // Seleccion de producto
    private JComboBox<Producto> cmbProductos;
    private JTextField txtCantidad;
    private JButton btnAgregar;

    // Tabla del carrito
    private JTable tablaCarrito;
    private DefaultTableModel modeloTabla;

    // Total y acciones
    private JLabel lblTotal;
    private JButton btnRegistrar;
    private JButton btnLimpiar;

    private final VentasController controller;
    private final ClientesController clientesController;

    public VentasForm() {
        this.controller = new VentasController();
        this.clientesController = new ClientesController();
        inicializarComponentes();
        cargarProductos();
        configurarVentana();
    }

    private void inicializarComponentes() {
        setLayout(new BorderLayout(10, 10));
        JPanel panelPrincipal = new JPanel(new BorderLayout(10, 10));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Panel cliente
        JPanel panelCliente = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panelCliente.setBorder(BorderFactory.createTitledBorder("Cliente"));
        panelCliente.add(new JLabel("DNI:"));
        txtDni = new JTextField(10);
        panelCliente.add(txtDni);
        JButton btnBuscar = new JButton("Buscar");
        btnBuscar.addActionListener(e -> buscarCliente());
        panelCliente.add(btnBuscar);
        lblNombreCliente = new JLabel("Sin seleccionar");
        lblNombreCliente.setFont(new Font("Arial", Font.ITALIC, 12));
        panelCliente.add(lblNombreCliente);

        // Panel producto
        JPanel panelProducto = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panelProducto.setBorder(BorderFactory.createTitledBorder("Agregar Producto"));
        cmbProductos = new JComboBox<>();
        cmbProductos.setPreferredSize(new Dimension(200, 25));
        panelProducto.add(cmbProductos);
        panelProducto.add(new JLabel("Cantidad:"));
        txtCantidad = new JTextField(5);
        panelProducto.add(txtCantidad);
        btnAgregar = new JButton("Agregar");
        btnAgregar.addActionListener(e -> agregarProducto());
        panelProducto.add(btnAgregar);

        JPanel panelSuperior = new JPanel(new GridLayout(2, 1));
        panelSuperior.add(panelCliente);
        panelSuperior.add(panelProducto);

        // Tabla carrito
        String[] columnas = {"Producto", "Precio Unit.", "Cantidad", "Subtotal"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tablaCarrito = new JTable(modeloTabla);
        JScrollPane scroll = new JScrollPane(tablaCarrito);
        scroll.setBorder(BorderFactory.createTitledBorder("Carrito de Venta"));

        // Panel inferior — total y botones
        JPanel panelInferior = new JPanel(new BorderLayout());
        lblTotal = new JLabel("Total: S/ 0.00", SwingConstants.RIGHT);
        lblTotal.setFont(new Font("Arial", Font.BOLD, 16));
        lblTotal.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 15));
        panelInferior.add(lblTotal, BorderLayout.NORTH);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        JButton btnEliminar = new JButton("Eliminar Fila");
        btnEliminar.addActionListener(e -> eliminarFilaSeleccionada());
        btnLimpiar = new JButton("Limpiar");
        btnLimpiar.addActionListener(e -> limpiarCarrito());
        btnRegistrar = new JButton("Registrar Venta");
        btnRegistrar.setBackground(new Color(0x2E7D32));
        btnRegistrar.setForeground(Color.WHITE);
        btnRegistrar.setFont(new Font("Arial", Font.BOLD, 13));
        btnRegistrar.addActionListener(e -> registrarVenta());
        panelBotones.add(btnEliminar);
        panelBotones.add(btnLimpiar);
        panelBotones.add(btnRegistrar);
        panelInferior.add(panelBotones, BorderLayout.SOUTH);

        panelPrincipal.add(panelSuperior, BorderLayout.NORTH);
        panelPrincipal.add(scroll, BorderLayout.CENTER);
        panelPrincipal.add(panelInferior, BorderLayout.SOUTH);
        add(panelPrincipal);
    }

    private void cargarProductos() {
        List<Producto> productos = controller.getProductosDisponibles();
        cmbProductos.removeAllItems();
        for (Producto p : productos) {
            cmbProductos.addItem(p);
        }
    }

    private void buscarCliente() {
        clienteSeleccionado = clientesController.buscarPorDni(txtDni.getText().trim());
        if (clienteSeleccionado != null) {
            lblNombreCliente.setText(clienteSeleccionado.getNombre());
        } else {
            lblNombreCliente.setText("Cliente no encontrado.");
        }
    }

    private void agregarProducto() {
        Producto seleccionado = (Producto) cmbProductos.getSelectedItem();
        if (seleccionado == null) return;

        String error = controller.agregarAlCarrito(seleccionado, parseCantidad());
        if (error != null) {
            JOptionPane.showMessageDialog(this, error, "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        refrescarTabla();
    }

    private void eliminarFilaSeleccionada() {
        int fila = tablaCarrito.getSelectedRow();
        if (fila >= 0) {
            controller.eliminarDelCarrito(fila);
            refrescarTabla();
        }
    }

    private void limpiarCarrito() {
        controller.limpiarCarrito();
        clienteSeleccionado = null;
        txtDni.setText("");
        lblNombreCliente.setText("Sin seleccionar");
        refrescarTabla();
    }

    private void registrarVenta() {
        String error = controller.registrarVenta(clienteSeleccionado);
        if (error != null) {
            JOptionPane.showMessageDialog(this, error, "Error", JOptionPane.ERROR_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Venta registrada correctamente.",
                    "Exito", JOptionPane.INFORMATION_MESSAGE);
            limpiarCarrito();
        }
    }

    /** Recarga la tabla con el contenido actual del carrito. */
    private void refrescarTabla() {
        modeloTabla.setRowCount(0);
        for (DetalleVenta d : controller.getCarrito()) {
            modeloTabla.addRow(new Object[]{
                d.getProducto().getNombre(),
                String.format("S/ %.2f", d.getProducto().getPrecio()),
                d.getCantidad(),
                String.format("S/ %.2f", d.getSubtotal())
            });
        }
        lblTotal.setText("Total: S/ " + String.format("%.2f", controller.calcularTotal()));
    }

    /** Parsea la cantidad del campo de texto. Retorna 0 si no es valida. */
    private int parseCantidad() {
        try {
            return Integer.parseInt(txtCantidad.getText().trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private void configurarVentana() {
        setTitle("Ventas");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(720, 520);
        setLocationRelativeTo(null);
    }
}
