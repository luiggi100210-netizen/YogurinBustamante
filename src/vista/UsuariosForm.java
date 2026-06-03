package vista;

import dao.UsuarioDAO;
import modelo.Usuario;
import util.Encriptador;
import util.Validador;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

/**
 * Formulario de gestion de usuarios.
 * Solo accesible para usuarios con rol ADMIN.
 * Permite registrar, actualizar y desactivar cuentas de usuario.
 *
 * @author Luiggi
 */
public class UsuariosForm extends JFrame {

    private JTable tablaUsuarios;
    private DefaultTableModel modeloTabla;
    private JTextField txtNombre, txtUsuario;
    private JPasswordField txtClave;
    private JComboBox<String> cmbRol;
    private JButton btnGuardar, btnEditar, btnDesactivar, btnLimpiar;

    /** ID del usuario seleccionado para edicion; 0 si es nuevo */
    private int idEditando = 0;

    private final UsuarioDAO usuarioDAO;

    public UsuariosForm() {
        this.usuarioDAO = new UsuarioDAO();
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
        panelForm.setBorder(BorderFactory.createTitledBorder("Datos del Usuario"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 8, 5, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtNombre  = agregarCampo(panelForm, gbc, "Nombre completo:", 0);
        txtUsuario = agregarCampo(panelForm, gbc, "Usuario (login):", 1);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 1;
        panelForm.add(new JLabel("Contrasena:"), gbc);
        txtClave = new JPasswordField(20);
        gbc.gridx = 1;
        panelForm.add(txtClave, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        panelForm.add(new JLabel("Rol:"), gbc);
        cmbRol = new JComboBox<>(new String[]{"VENDEDOR", "ADMIN"});
        gbc.gridx = 1;
        panelForm.add(cmbRol, gbc);

        // Botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        btnGuardar    = new JButton("Guardar");
        btnEditar     = new JButton("Actualizar");
        btnDesactivar = new JButton("Desactivar");
        btnLimpiar    = new JButton("Limpiar");

        btnEditar.setEnabled(false);
        btnDesactivar.setEnabled(false);

        btnGuardar.addActionListener(e -> guardar());
        btnEditar.addActionListener(e -> actualizar());
        btnDesactivar.addActionListener(e -> desactivar());
        btnLimpiar.addActionListener(e -> limpiarFormulario());

        panelBotones.add(btnGuardar);
        panelBotones.add(btnEditar);
        panelBotones.add(btnDesactivar);
        panelBotones.add(btnLimpiar);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        panelForm.add(panelBotones, gbc);

        // Tabla
        String[] columnas = {"ID", "Nombre", "Usuario", "Rol"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tablaUsuarios = new JTable(modeloTabla);
        tablaUsuarios.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaUsuarios.getSelectionModel().addListSelectionListener(e -> cargarSeleccion());

        JScrollPane scroll = new JScrollPane(tablaUsuarios);

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
            // Si no ingreso nueva clave, se debe mantener la anterior.
            // Por seguridad, se obliga a ingresar la clave al actualizar.
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
            "¿Desea desactivar este usuario?", "Confirmar", JOptionPane.YES_NO_OPTION);

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
    }

    private void configurarVentana() {
        setTitle("Gestion de Usuarios (ADMIN)");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(650, 500);
        setLocationRelativeTo(null);
    }
}
