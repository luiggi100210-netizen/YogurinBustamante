package vista;

import controlador.RespaldoController;

import javax.swing.*;
import java.awt.*;
import java.io.File;

/**
 * Formulario del modulo de respaldo de datos.
 * Permite al administrador generar una copia de seguridad
 * de la base de datos MySQL en una carpeta elegida.
 *
 * @author Luiggi
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
        setLayout(new BorderLayout(10, 10));
        JPanel panelPrincipal = new JPanel(new BorderLayout(10, 10));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Titulo informativo
        JLabel lblInfo = new JLabel(
            "<html><b>Respaldo de Base de Datos</b><br>"
            + "Genera un archivo .sql con toda la informacion del sistema.</html>",
            SwingConstants.CENTER
        );
        lblInfo.setFont(new Font("Arial", Font.PLAIN, 13));
        lblInfo.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        // Seleccion de carpeta destino
        JPanel panelCarpeta = new JPanel(new BorderLayout(8, 0));
        panelCarpeta.setBorder(BorderFactory.createTitledBorder("Carpeta de destino"));
        txtCarpeta = new JTextField(System.getProperty("user.home") + File.separator + "Desktop");
        btnSeleccionar = new JButton("Examinar...");
        btnSeleccionar.addActionListener(e -> seleccionarCarpeta());
        panelCarpeta.add(txtCarpeta, BorderLayout.CENTER);
        panelCarpeta.add(btnSeleccionar, BorderLayout.EAST);

        // Boton principal
        btnRespaldar = new JButton("Generar Respaldo Ahora");
        btnRespaldar.setFont(new Font("Arial", Font.BOLD, 14));
        btnRespaldar.setBackground(new Color(0x1565C0));
        btnRespaldar.setForeground(Color.WHITE);
        btnRespaldar.setPreferredSize(new Dimension(0, 45));
        btnRespaldar.addActionListener(e -> ejecutarRespaldo());

        // Area de log
        areaLog = new JTextArea(8, 40);
        areaLog.setEditable(false);
        areaLog.setFont(new Font("Monospaced", Font.PLAIN, 11));
        areaLog.setText("Listo para generar respaldo.\n");
        JScrollPane scroll = new JScrollPane(areaLog);
        scroll.setBorder(BorderFactory.createTitledBorder("Registro de actividad"));

        JPanel panelSuperior = new JPanel(new BorderLayout(5, 10));
        panelSuperior.add(lblInfo, BorderLayout.NORTH);
        panelSuperior.add(panelCarpeta, BorderLayout.CENTER);
        panelSuperior.add(btnRespaldar, BorderLayout.SOUTH);

        panelPrincipal.add(panelSuperior, BorderLayout.NORTH);
        panelPrincipal.add(scroll, BorderLayout.CENTER);
        add(panelPrincipal);
    }

    /** Abre un selector de carpeta para elegir el destino del respaldo. */
    private void seleccionarCarpeta() {
        JFileChooser chooser = new JFileChooser(txtCarpeta.getText());
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setDialogTitle("Seleccionar carpeta de destino");

        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            txtCarpeta.setText(chooser.getSelectedFile().getAbsolutePath());
        }
    }

    /** Ejecuta el respaldo y muestra el resultado en el area de log. */
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

        // Ejecutar en hilo separado para no bloquear la UI
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

    /** Agrega una linea al area de log con timestamp. */
    private void log(String mensaje) {
        areaLog.append(mensaje + "\n");
        areaLog.setCaretPosition(areaLog.getDocument().getLength());
    }

    private void configurarVentana() {
        setTitle("Respaldo de Datos");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(540, 400);
        setLocationRelativeTo(null);
        setResizable(false);
    }
}
