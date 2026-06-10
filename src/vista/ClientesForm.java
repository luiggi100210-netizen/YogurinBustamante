package vista;

import controlador.ClientesController;
import modelo.Cliente;
import modelo.Venta;
import util.Tema;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Formulario de gestion de clientes.
 * Permite registrar, buscar por DNI, editar y listar clientes.
 */
public class ClientesForm extends JFrame {

    private JTable tablaClientes;
    private DefaultTableModel modeloTabla;
    private JTextField txtNombre, txtDni, txtTelefono, txtBuscarDni;
    private JButton btnGuardar, btnEditar, btnLimpiar;
    private JLabel lblHint;

    private int idEditando = 0;

    private final ClientesController controller;

    public ClientesForm() {
        this.controller = new ClientesController();
        inicializarComponentes();
        cargarTabla();
        configurarVentana();
    }

    private void inicializarComponentes() {
        setLayout(new BorderLayout());
        getContentPane().setBackground(Tema.FONDO);

        add(Tema.header("Gestion de Clientes"), BorderLayout.NORTH);

        JPanel panelPrincipal = new JPanel(new BorderLayout(10, 10));
        panelPrincipal.setBackground(Tema.FONDO);
        panelPrincipal.setBorder(new EmptyBorder(12, 14, 12, 14));

        JPanel panelSuperior = new JPanel(new BorderLayout(0, 8));
        panelSuperior.setBackground(Tema.FONDO);
        panelSuperior.add(crearPanelBusqueda(),   BorderLayout.NORTH);
        panelSuperior.add(crearPanelFormulario(), BorderLayout.CENTER);

        panelPrincipal.add(panelSuperior,   BorderLayout.NORTH);
        panelPrincipal.add(crearPanelTabla(), BorderLayout.CENTER);

        add(panelPrincipal, BorderLayout.CENTER);
    }

    private JPanel crearPanelBusqueda() {
        JPanel panelBusqueda = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        panelBusqueda.setBackground(Tema.BLANCO);
        panelBusqueda.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Tema.BORDE, 1),
            new EmptyBorder(2, 4, 2, 4)
        ));

        JLabel lbl = new JLabel("Buscar por DNI:");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(Tema.SIDEBAR);
        panelBusqueda.add(lbl);

        txtBuscarDni = new JTextField(14);
        txtBuscarDni.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        panelBusqueda.add(txtBuscarDni);

        JButton btnBuscar = Tema.botonPrimario("Buscar");
        btnBuscar.setPreferredSize(new Dimension(90, 30));
        btnBuscar.addActionListener(e -> buscarPorDni());
        panelBusqueda.add(btnBuscar);

        return panelBusqueda;
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

        txtNombre   = agregarCampo(panelForm, gbc, "Nombre:",   0);
        txtDni      = agregarCampo(panelForm, gbc, "DNI:",       1);
        txtTelefono = agregarCampo(panelForm, gbc, "Telefono:", 2);

        btnGuardar = Tema.botonExito("Guardar");
        btnEditar  = Tema.botonAdvertencia("Actualizar");
        btnLimpiar = Tema.botonNeutro("Limpiar");
        JButton btnHistorial = Tema.botonPrimario("Ver Historial");

        btnEditar.setEnabled(false);

        btnGuardar.addActionListener(e   -> guardar());
        btnEditar.addActionListener(e    -> actualizar());
        btnLimpiar.addActionListener(e   -> limpiarFormulario());
        btnHistorial.addActionListener(e -> verHistorial());

        lblHint = Tema.hint("Haga clic en una fila para editar");

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 4));
        panelBotones.setBackground(Tema.BLANCO);
        panelBotones.add(btnGuardar);
        panelBotones.add(btnEditar);
        panelBotones.add(btnHistorial);
        panelBotones.add(btnLimpiar);

        JPanel panelAcciones = new JPanel(new BorderLayout());
        panelAcciones.setBackground(Tema.BLANCO);
        lblHint.setHorizontalAlignment(SwingConstants.CENTER);
        panelAcciones.add(panelBotones, BorderLayout.CENTER);
        panelAcciones.add(lblHint, BorderLayout.SOUTH);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        panelForm.add(panelAcciones, gbc);

        return panelForm;
    }

    private JPanel crearPanelTabla() {
        String[] columnas = {"ID", "Nombre", "DNI", "Telefono"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tablaClientes = new JTable(modeloTabla);
        tablaClientes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaClientes.getSelectionModel().addListSelectionListener(e -> cargarSeleccion());
        Tema.estilizarTabla(tablaClientes);

        JScrollPane scroll = new JScrollPane(tablaClientes);
        scroll.setBorder(BorderFactory.createLineBorder(Tema.BORDE, 1));
        scroll.getViewport().setBackground(Tema.BLANCO);

        JLabel lblSub = new JLabel("Listado de clientes registrados");
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
            lblHint.setText("Cliente encontrado — puede actualizar");
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
        lblHint.setText("Cliente seleccionado — puede actualizar");
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

    private void verHistorial() {
        if (idEditando == 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un cliente primero.",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        List<Venta> historial = controller.getHistorialCompras(idEditando);

        String[] columnas = {"Venta #", "Fecha", "Total (S/)"};
        DefaultTableModel modelo = new DefaultTableModel(columnas, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        for (Venta v : historial) {
            modelo.addRow(new Object[]{
                v.getId(), v.getFecha(), String.format("%.2f", v.getTotal())
            });
        }

        JTable tabla = new JTable(modelo);
        Tema.estilizarTabla(tabla);
        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setPreferredSize(new Dimension(440, 260));

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
        lblHint.setText("Haga clic en una fila para editar");
    }

    private void configurarVentana() {
        setTitle("Gestion de Clientes");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(760, 580);
        setMinimumSize(new Dimension(660, 520));
        setLocationRelativeTo(null);
    }
}
