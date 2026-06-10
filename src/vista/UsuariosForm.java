package vista;

import dao.UsuarioDAO;
import modelo.Usuario;
import util.Encriptador;
import util.Tema;
import util.Validador;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

/**
 * Formulario de gestion de usuarios.
 * Solo accesible para usuarios con rol ADMIN.
 * Permite registrar, actualizar y desactivar cuentas de usuario.
 */
public class UsuariosForm extends JFrame {

    private JTable tablaUsuarios;
    private DefaultTableModel modeloTabla;
    private JTextField txtNombre, txtUsuario;
    private JPasswordField txtClave;
    private JComboBox<String> cmbRol;
    private JButton btnGuardar, btnEditar, btnDesactivar, btnLimpiar;
    private JLabel lblHint;

    private int idEditando = 0;

    private final UsuarioDAO usuarioDAO;

    public UsuariosForm() {
        this.usuarioDAO = new UsuarioDAO();
        inicializarComponentes();
        cargarTabla();
        configurarVentana();
    }

    private void inicializarComponentes() {
        setLayout(new BorderLayout());
        getContentPane().setBackground(Tema.FONDO);

        add(Tema.header("Gestion de Usuarios  —  Solo Administrador"), BorderLayout.NORTH);

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

        txtNombre  = agregarCampo(panelForm, gbc, "Nombre completo:", 0);
        txtUsuario = agregarCampo(panelForm, gbc, "Usuario (login):", 1);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 1;
        JLabel lblClave = new JLabel("Contrasena:");
        lblClave.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblClave.setForeground(Tema.NEUTRO);
        panelForm.add(lblClave, gbc);
        txtClave = new JPasswordField(20);
        txtClave.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        gbc.gridx = 1;
        panelForm.add(txtClave, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        JLabel lblRol = new JLabel("Rol:");
        lblRol.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblRol.setForeground(Tema.NEUTRO);
        panelForm.add(lblRol, gbc);
        cmbRol = new JComboBox<>(new String[]{"VENDEDOR", "ADMIN"});
        cmbRol.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        gbc.gridx = 1;
        panelForm.add(cmbRol, gbc);

        btnGuardar    = Tema.botonExito("Guardar");
        btnEditar     = Tema.botonAdvertencia("Actualizar");
        btnDesactivar = Tema.botonPeligro("Desactivar");
        btnLimpiar    = Tema.botonNeutro("Limpiar");

        btnEditar.setEnabled(false);
        btnDesactivar.setEnabled(false);

        btnGuardar.addActionListener(e    -> guardar());
        btnEditar.addActionListener(e     -> actualizar());
        btnDesactivar.addActionListener(e -> desactivar());
        btnLimpiar.addActionListener(e    -> limpiarFormulario());

        lblHint = Tema.hint("Haga clic en una fila para editar o desactivar un usuario");

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 4));
        panelBotones.setBackground(Tema.BLANCO);
        panelBotones.add(btnGuardar);
        panelBotones.add(btnEditar);
        panelBotones.add(btnDesactivar);
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
        String[] columnas = {"ID", "Nombre", "Usuario", "Rol"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tablaUsuarios = new JTable(modeloTabla);
        tablaUsuarios.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaUsuarios.getSelectionModel().addListSelectionListener(e -> cargarSeleccion());
        Tema.estilizarTabla(tablaUsuarios);

        JScrollPane scroll = new JScrollPane(tablaUsuarios);
        scroll.setBorder(BorderFactory.createLineBorder(Tema.BORDE, 1));
        scroll.getViewport().setBackground(Tema.BLANCO);

        JLabel lblSub = new JLabel("Usuarios activos del sistema");
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
        try {
            List<Usuario> usuarios = usuarioDAO.listarTodos();
            for (Usuario u : usuarios) {
                modeloTabla.addRow(new Object[]{
                    u.getId(), u.getNombre(), u.getUsuario(), u.getRol()
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar usuarios: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cargarSeleccion() {
        int fila = tablaUsuarios.getSelectedRow();
        if (fila < 0) return;

        idEditando = (int) modeloTabla.getValueAt(fila, 0);
        txtNombre.setText(modeloTabla.getValueAt(fila, 1).toString());
        txtUsuario.setText(modeloTabla.getValueAt(fila, 2).toString());
        txtClave.setText("");
        cmbRol.setSelectedItem(modeloTabla.getValueAt(fila, 3).toString());

        btnGuardar.setEnabled(false);
        btnEditar.setEnabled(true);
        btnDesactivar.setEnabled(true);
        lblHint.setText("Usuario seleccionado — puede actualizar o desactivar");
    }

    private void guardar() {
        if (!Validador.noEstaVacio(txtNombre.getText()) ||
            !Validador.noEstaVacio(txtUsuario.getText())) {
            JOptionPane.showMessageDialog(this, "Nombre y usuario son obligatorios.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String clavePlana = new String(txtClave.getPassword());
        if (!Validador.esClaveValida(clavePlana)) {
            JOptionPane.showMessageDialog(this, "La contrasena debe tener al menos 6 caracteres.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Usuario u = new Usuario(0,
                txtNombre.getText().trim(),
                txtUsuario.getText().trim(),
                Encriptador.encriptarMD5(clavePlana),
                cmbRol.getSelectedItem().toString());

            usuarioDAO.insertar(u);
            JOptionPane.showMessageDialog(this, "Usuario registrado correctamente.",
                    "Exito", JOptionPane.INFORMATION_MESSAGE);
            limpiarFormulario();
            cargarTabla();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void actualizar() {
        try {
            String clavePlana = new String(txtClave.getPassword());
            if (!Validador.esClaveValida(clavePlana)) {
                JOptionPane.showMessageDialog(this,
                    "Ingrese la contrasena (minimo 6 caracteres) para actualizar.",
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Usuario u = new Usuario(idEditando,
                txtNombre.getText().trim(),
                txtUsuario.getText().trim(),
                Encriptador.encriptarMD5(clavePlana),
                cmbRol.getSelectedItem().toString());

            usuarioDAO.actualizar(u);
            JOptionPane.showMessageDialog(this, "Usuario actualizado correctamente.",
                    "Exito", JOptionPane.INFORMATION_MESSAGE);
            limpiarFormulario();
            cargarTabla();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void desactivar() {
        int confirmar = JOptionPane.showConfirmDialog(this,
            "Desea desactivar este usuario?", "Confirmar", JOptionPane.YES_NO_OPTION);

        if (confirmar == JOptionPane.YES_OPTION) {
            try {
                usuarioDAO.desactivar(idEditando);
                JOptionPane.showMessageDialog(this, "Usuario desactivado.",
                        "Exito", JOptionPane.INFORMATION_MESSAGE);
                limpiarFormulario();
                cargarTabla();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void limpiarFormulario() {
        idEditando = 0;
        txtNombre.setText("");
        txtUsuario.setText("");
        txtClave.setText("");
        cmbRol.setSelectedIndex(0);
        tablaUsuarios.clearSelection();
        btnGuardar.setEnabled(true);
        btnEditar.setEnabled(false);
        btnDesactivar.setEnabled(false);
        lblHint.setText("Haga clic en una fila para editar o desactivar un usuario");
    }

    private void configurarVentana() {
        setTitle("Gestion de Usuarios (ADMIN)");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(760, 560);
        setMinimumSize(new Dimension(660, 500));
        setLocationRelativeTo(null);
    }
}
