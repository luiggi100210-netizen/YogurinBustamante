package vista;

import controlador.ClientesController;
import modelo.Cliente;
import modelo.Venta;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Formulario de gestion de clientes.
 * Permite registrar, buscar por DNI, editar y listar clientes.
 *
 * @author Luiggi
 */
public class ClientesForm extends JFrame {

    private JTable tablaClientes;
    private DefaultTableModel modeloTabla;
    private JTextField txtNombre, txtDni, txtTelefono, txtBuscarDni;
    private JButton btnGuardar, btnEditar, btnLimpiar;

    /** ID del cliente seleccionado para edicion; 0 si es nuevo */
    private int idEditando = 0;

    private final ClientesController controller;

    public ClientesForm() {
        this.controller = new ClientesController();
        inicializarComponentes();
        cargarTabla();
        configurarVentana();
    }

    private void inicializarComponentes() {
        setLayout(new BorderLayout(10, 10));
        JPanel panelPrincipal = new JPanel(new BorderLayout(10, 10));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Panel busqueda
        JPanel panelBusqueda = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panelBusqueda.setBorder(BorderFactory.createTitledBorder("Buscar por DNI"));
        txtBuscarDni = new JTextField(12);
        JButton btnBuscar = new JButton("Buscar");
        btnBuscar.addActionListener(e -> buscarPorDni());
        panelBusqueda.add(new JLabel("DNI:"));
        panelBusqueda.add(txtBuscarDni);
        panelBusqueda.add(btnBuscar);

        // Formulario
        JPanel panelForm = new JPanel(new GridBagLayout());
        panelForm.setBorder(BorderFactory.createTitledBorder("Datos del Cliente"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 8, 5, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtNombre   = agregarCampo(panelForm, gbc, "Nombre:",   0);
        txtDni      = agregarCampo(panelForm, gbc, "DNI:",       1);
        txtTelefono = agregarCampo(panelForm, gbc, "Telefono:", 2);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        btnGuardar = new JButton("Guardar");
        btnEditar  = new JButton("Actualizar");
        btnLimpiar = new JButton("Limpiar");

        btnEditar.setEnabled(false);
        btnGuardar.addActionListener(e -> guardar());
        btnEditar.addActionListener(e -> actualizar());
        btnLimpiar.addActionListener(e -> limpiarFormulario());

        JButton btnHistorial = new JButton("Ver Historial");
        btnHistorial.addActionListener(e -> verHistorial());

        panelBotones.add(btnGuardar);
        panelBotones.add(btnEditar);
        panelBotones.add(btnHistorial);
        panelBotones.add(btnLimpiar);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        panelForm.add(panelBotones, gbc);

        JPanel panelSuperior = new JPanel(new GridLayout(2, 1));
        panelSuperior.add(panelBusqueda);
        panelSuperior.add(panelForm);

        // Tabla
        String[] columnas = {"ID", "Nombre", "DNI", "Telefono"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tablaClientes = new JTable(modeloTabla);
        tablaClientes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaClientes.getSelectionModel().addListSelectionListener(e -> cargarSeleccion());

        JScrollPane scroll = new JScrollPane(tablaClientes);

        panelPrincipal.add(panelSuperior, BorderLayout.NORTH);
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
        List<Cliente> clientes = controller.listarClientes();
        for (Cliente c : clientes) {
            modeloTabla.addRow(new Object[]{c.getId(), c.getNombre(), c.getDni(), c.getTelefono()});
        }
    }

    private void buscarPorDni() {
        Cliente encontrado = controller.buscarPorDni(txtBuscarDni.getText().trim());
        if (encontrado != null) {
            txtNombre.setText(encontrado.getNombre());
            txtDni.setText(encontrado.getDni());
            txtTelefono.setText(encontrado.getTelefono());
            idEditando = encontrado.getId();
            btnGuardar.setEnabled(false);
            btnEditar.setEnabled(true);
        } else {
            JOptionPane.showMessageDialog(this, "Cliente no encontrado.", "Info",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void cargarSeleccion() {
        int fila = tablaClientes.getSelectedRow();
        if (fila < 0) return;

        idEditando = (int) modeloTabla.getValueAt(fila, 0);
        txtNombre.setText(modeloTabla.getValueAt(fila, 1).toString());
        txtDni.setText(modeloTabla.getValueAt(fila, 2).toString());
        txtTelefono.setText(modeloTabla.getValueAt(fila, 3).toString());
        btnGuardar.setEnabled(false);
        btnEditar.setEnabled(true);
    }

    private void guardar() {
        String error = controller.guardarCliente(
            txtNombre.getText(), txtDni.getText(), txtTelefono.getText());
        mostrarResultado(error, "Cliente registrado correctamente.");
    }

    private void actualizar() {
        String error = controller.actualizarCliente(
            idEditando, txtNombre.getText(), txtDni.getText(), txtTelefono.getText());
        mostrarResultado(error, "Cliente actualizado correctamente.");
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

    /**
     * Muestra el historial de compras del cliente seleccionado en un dialogo.
     */
    private void verHistorial() {
        if (idEditando == 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un cliente primero.",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        List<Venta> historial = controller.getHistorialCompras(idEditando);

        String[] columnas = {"Venta #", "Fecha", "Total (S/)"};
        javax.swing.table.DefaultTableModel modelo =
            new javax.swing.table.DefaultTableModel(columnas, 0) {
                @Override public boolean isCellEditable(int r, int c) { return false; }
            };

        for (Venta v : historial) {
            modelo.addRow(new Object[]{
                v.getId(),
                v.getFecha(),
                String.format("%.2f", v.getTotal())
            });
        }

        JTable tabla = new JTable(modelo);
        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setPreferredSize(new Dimension(420, 250));

        String titulo = "Historial de compras: " + txtNombre.getText();
        JOptionPane.showMessageDialog(this, scroll, titulo, JOptionPane.PLAIN_MESSAGE);
    }

    private void limpiarFormulario() {
        idEditando = 0;
        txtNombre.setText("");
        txtDni.setText("");
        txtTelefono.setText("");
        txtBuscarDni.setText("");
        tablaClientes.clearSelection();
        btnGuardar.setEnabled(true);
        btnEditar.setEnabled(false);
    }

    private void configurarVentana() {
        setTitle("Gestion de Clientes");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(650, 500);
        setLocationRelativeTo(null);
    }
}
