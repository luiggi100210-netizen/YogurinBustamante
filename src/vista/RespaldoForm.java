package vista;

import controlador.RespaldoController;
import util.Tema;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;

/**
 * Formulario del modulo de respaldo de datos.
 * Permite al administrador generar una copia de seguridad
 * de la base de datos MySQL en una carpeta elegida.
 */
public class RespaldoForm extends JFrame {

    private JTextField txtCarpeta;
    private JTextArea areaLog;
    private JButton btnSeleccionar;
    private JButton btnRespaldar;

    private final RespaldoController controller;

    public RespaldoForm() {
        this.controller = new RespaldoController();
        inicializarComponentes();
        configurarVentana();
    }

    private void inicializarComponentes() {
        setLayout(new BorderLayout());
        getContentPane().setBackground(Tema.FONDO);

        add(Tema.header("Respaldo de Base de Datos"), BorderLayout.NORTH);

        JPanel panelPrincipal = new JPanel(new BorderLayout(10, 12));
        panelPrincipal.setBackground(Tema.FONDO);
        panelPrincipal.setBorder(new EmptyBorder(14, 16, 14, 16));

        panelPrincipal.add(crearPanelSuperior(), BorderLayout.NORTH);
        panelPrincipal.add(crearPanelLog(),      BorderLayout.CENTER);

        add(panelPrincipal, BorderLayout.CENTER);
    }

    private JPanel crearPanelSuperior() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(Tema.FONDO);

        // Info
        JPanel panelInfo = new JPanel(new BorderLayout());
        panelInfo.setBackground(Tema.BLANCO);
        panelInfo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Tema.BORDE, 1),
            new EmptyBorder(10, 14, 10, 14)
        ));
        JLabel lblInfo = new JLabel(
            "<html><b>Genera un archivo .sql</b> con toda la informacion del sistema.<br>"
            + "Selecciona la carpeta de destino y presiona <i>Generar Respaldo</i>.</html>"
        );
        lblInfo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblInfo.setForeground(Tema.NEUTRO);
        panelInfo.add(lblInfo);

        // Carpeta
        JPanel panelCarpeta = new JPanel(new BorderLayout(8, 0));
        panelCarpeta.setBackground(Tema.BLANCO);
        panelCarpeta.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Tema.BORDE, 1),
            new EmptyBorder(8, 12, 8, 12)
        ));

        JLabel lblCarpeta = new JLabel("Carpeta de destino:");
        lblCarpeta.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblCarpeta.setForeground(Tema.NEUTRO);

        txtCarpeta = new JTextField(System.getProperty("user.home") + File.separator + "Desktop");
        txtCarpeta.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        btnSeleccionar = Tema.botonNeutro("Examinar...");
        btnSeleccionar.setPreferredSize(new Dimension(110, 32));
        btnSeleccionar.addActionListener(e -> seleccionarCarpeta());

        JPanel panelCarpetaInner = new JPanel(new BorderLayout(0, 4));
        panelCarpetaInner.setBackground(Tema.BLANCO);
        panelCarpetaInner.add(lblCarpeta, BorderLayout.NORTH);

        JPanel panelRuta = new JPanel(new BorderLayout(6, 0));
        panelRuta.setBackground(Tema.BLANCO);
        panelRuta.add(txtCarpeta, BorderLayout.CENTER);
        panelRuta.add(btnSeleccionar, BorderLayout.EAST);
        panelCarpetaInner.add(panelRuta, BorderLayout.CENTER);
        panelCarpeta.add(panelCarpetaInner);

        // Boton principal
        JPanel panelBoton = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        panelBoton.setBackground(Tema.FONDO);
        btnRespaldar = Tema.botonExito("Generar Respaldo Ahora");
        btnRespaldar.setPreferredSize(new Dimension(200, 40));
        btnRespaldar.addActionListener(e -> ejecutarRespaldo());
        panelBoton.add(btnRespaldar);

        panel.add(panelInfo,   BorderLayout.NORTH);
        panel.add(panelCarpeta, BorderLayout.CENTER);
        panel.add(panelBoton,  BorderLayout.SOUTH);

        return panel;
    }

    private JPanel crearPanelLog() {
        areaLog = new JTextArea();
        areaLog.setEditable(false);
        areaLog.setFont(new Font("Monospaced", Font.PLAIN, 11));
        areaLog.setBackground(new Color(0xF8F8F8));
        areaLog.setText("Listo para generar respaldo.\n");
        areaLog.setBorder(new EmptyBorder(8, 10, 8, 10));

        JScrollPane scroll = new JScrollPane(areaLog);
        scroll.setBorder(BorderFactory.createLineBorder(Tema.BORDE, 1));

        JLabel lblSub = new JLabel("Registro de actividad");
        lblSub.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblSub.setForeground(Tema.SIDEBAR);
        lblSub.setBorder(new EmptyBorder(6, 0, 4, 0));

        JPanel panel = new JPanel(new BorderLayout(0, 4));
        panel.setBackground(Tema.FONDO);
        panel.add(lblSub, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private void seleccionarCarpeta() {
        JFileChooser chooser = new JFileChooser(txtCarpeta.getText());
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setDialogTitle("Seleccionar carpeta de destino");

        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            txtCarpeta.setText(chooser.getSelectedFile().getAbsolutePath());
        }
    }

    private void ejecutarRespaldo() {
        String carpeta = txtCarpeta.getText().trim();

        if (carpeta.isEmpty()) {
            log("ERROR: Seleccione una carpeta de destino.");
            return;
        }

        File dir = new File(carpeta);
        if (!dir.exists() || !dir.isDirectory()) {
            log("ERROR: La carpeta seleccionada no existe.");
            return;
        }

        if (!controller.mysqldumpDisponible()) {
            log("ERROR: mysqldump no encontrado. Verifique que MySQL este en el PATH del sistema.");
            return;
        }

        btnRespaldar.setEnabled(false);
        log("Iniciando respaldo...");

        new Thread(() -> {
            String rutaArchivo = controller.ejecutarRespaldo(carpeta);
            SwingUtilities.invokeLater(() -> {
                if (rutaArchivo != null) {
                    log("Respaldo generado correctamente:");
                    log("  " + rutaArchivo);
                    JOptionPane.showMessageDialog(this,
                        "Respaldo guardado en:\n" + rutaArchivo,
                        "Exito", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    log("ERROR: No se pudo generar el respaldo.");
                    log("Verifique que MySQL este corriendo y que mysqldump este en el PATH.");
                }
                btnRespaldar.setEnabled(true);
            });
        }).start();
    }

    private void log(String mensaje) {
        areaLog.append(mensaje + "\n");
        areaLog.setCaretPosition(areaLog.getDocument().getLength());
    }

    private void configurarVentana() {
        setTitle("Respaldo de Datos");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(580, 440);
        setMinimumSize(new Dimension(520, 380));
        setLocationRelativeTo(null);
    }
}
