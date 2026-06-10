package vista;

import controlador.ClientesController;
import controlador.VentasController;
import modelo.Cliente;
import modelo.DetalleVenta;
import modelo.Producto;
import util.Tema;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Formulario de registro de ventas.
 * Permite buscar clientes, seleccionar productos y registrar la venta.
 */
public class VentasForm extends JFrame {

    private JTextField txtDni;
    private JLabel lblNombreCliente;
    private Cliente clienteSeleccionado;

    private JComboBox<Producto> cmbProductos;
    private JTextField txtCantidad;

    private JTable tablaCarrito;
    private DefaultTableModel modeloTabla;

    private JLabel lblTotal;

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
        setLayout(new BorderLayout());
        getContentPane().setBackground(Tema.FONDO);

        add(Tema.header("Registro de Ventas"), BorderLayout.NORTH);

        JPanel panelPrincipal = new JPanel(new BorderLayout(10, 10));
        panelPrincipal.setBackground(Tema.FONDO);
        panelPrincipal.setBorder(new EmptyBorder(12, 14, 12, 14));

        panelPrincipal.add(crearPanelSuperior(), BorderLayout.NORTH);
        panelPrincipal.add(crearPanelCarrito(),  BorderLayout.CENTER);
        panelPrincipal.add(crearPanelAcciones(), BorderLayout.SOUTH);

        add(panelPrincipal, BorderLayout.CENTER);
    }

    private JPanel crearPanelSuperior() {
        JPanel panelSuperior = new JPanel(new GridLayout(2, 1, 0, 8));
        panelSuperior.setBackground(Tema.FONDO);

        // Panel cliente
        JPanel panelCliente = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        panelCliente.setBackground(Tema.BLANCO);
        panelCliente.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Tema.BORDE, 1),
            new EmptyBorder(2, 4, 2, 4)
        ));

        JLabel lblClienteTitulo = new JLabel("Cliente:");
        lblClienteTitulo.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblClienteTitulo.setForeground(Tema.SIDEBAR);
        panelCliente.add(lblClienteTitulo);

        txtDni = new JTextField(12);
        txtDni.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtDni.setPreferredSize(new Dimension(120, 30));
        panelCliente.add(new JLabel("DNI:"));
        panelCliente.add(txtDni);

        JButton btnBuscar = Tema.botonPrimario("Buscar");
        btnBuscar.setPreferredSize(new Dimension(90, 30));
        btnBuscar.addActionListener(e -> buscarCliente());
        panelCliente.add(btnBuscar);

        lblNombreCliente = new JLabel("Sin seleccionar");
        lblNombreCliente.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblNombreCliente.setForeground(Tema.NEUTRO);
        panelCliente.add(lblNombreCliente);

        // Panel producto
        JPanel panelProducto = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        panelProducto.setBackground(Tema.BLANCO);
        panelProducto.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Tema.BORDE, 1),
            new EmptyBorder(2, 4, 2, 4)
        ));

        JLabel lblProdTitulo = new JLabel("Agregar Producto:");
        lblProdTitulo.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblProdTitulo.setForeground(Tema.SIDEBAR);
        panelProducto.add(lblProdTitulo);

        cmbProductos = new JComboBox<>();
        cmbProductos.setPreferredSize(new Dimension(220, 30));
        cmbProductos.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        panelProducto.add(cmbProductos);

        panelProducto.add(new JLabel("Cantidad:"));
        txtCantidad = new JTextField(6);
        txtCantidad.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        panelProducto.add(txtCantidad);

        JButton btnAgregar = Tema.botonExito("Agregar");
        btnAgregar.setPreferredSize(new Dimension(90, 30));
        btnAgregar.addActionListener(e -> agregarProducto());
        panelProducto.add(btnAgregar);

        panelSuperior.add(panelCliente);
        panelSuperior.add(panelProducto);
        return panelSuperior;
    }

    private JPanel crearPanelCarrito() {
        String[] columnas = {"Producto", "Precio Unit.", "Cantidad", "Subtotal"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tablaCarrito = new JTable(modeloTabla);
        Tema.estilizarTabla(tablaCarrito);

        JScrollPane scroll = new JScrollPane(tablaCarrito);
        scroll.setBorder(BorderFactory.createLineBorder(Tema.BORDE, 1));
        scroll.getViewport().setBackground(Tema.BLANCO);

        JLabel lblSub = new JLabel("Carrito de venta");
        lblSub.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblSub.setForeground(Tema.SIDEBAR);
        lblSub.setBorder(new EmptyBorder(8, 0, 4, 0));

        JPanel panel = new JPanel(new BorderLayout(0, 4));
        panel.setBackground(Tema.FONDO);
        panel.add(lblSub, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearPanelAcciones() {
        JPanel panelAcciones = new JPanel(new BorderLayout());
        panelAcciones.setBackground(Tema.BLANCO);
        panelAcciones.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Tema.BORDE, 1),
            new EmptyBorder(8, 12, 8, 12)
        ));

        lblTotal = new JLabel("Total: S/ 0.00", SwingConstants.RIGHT);
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTotal.setForeground(Tema.SIDEBAR);
        panelAcciones.add(lblTotal, BorderLayout.WEST);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panelBotones.setBackground(Tema.BLANCO);

        JButton btnEliminar = Tema.botonPeligro("Quitar fila");
        btnEliminar.addActionListener(e -> eliminarFilaSeleccionada());

        JButton btnLimpiar = Tema.botonNeutro("Limpiar");
        btnLimpiar.addActionListener(e -> limpiarCarrito());

        JButton btnRegistrar = Tema.botonExito("Registrar Venta");
        btnRegistrar.setPreferredSize(new Dimension(150, 34));
        btnRegistrar.addActionListener(e -> registrarVenta());

        panelBotones.add(btnEliminar);
        panelBotones.add(btnLimpiar);
        panelBotones.add(btnRegistrar);
        panelAcciones.add(panelBotones, BorderLayout.EAST);

        return panelAcciones;
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
            lblNombreCliente.setForeground(Tema.EXITO);
        } else {
            lblNombreCliente.setText("Cliente no encontrado.");
            lblNombreCliente.setForeground(Tema.PELIGRO);
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
        lblNombreCliente.setForeground(Tema.NEUTRO);
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

    private int parseCantidad() {
        try {
            return Integer.parseInt(txtCantidad.getText().trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private void configurarVentana() {
        setTitle("Registro de Ventas");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(780, 580);
        setMinimumSize(new Dimension(680, 520));
        setLocationRelativeTo(null);
    }
}
