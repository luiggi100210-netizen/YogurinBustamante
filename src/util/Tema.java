package util;

import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Tema visual unico del sistema.
 * Centraliza la paleta de colores, la tipografia y la construccion de
 * componentes (botones, campos, tablas, headers) para que todos los
 * formularios compartan exactamente el mismo estilo.
 *
 * @author Luiggi
 */
public final class Tema {

    // ── Paleta de colores ────────────────────────────────────────────────────

    public static final Color FONDO       = new Color(0xF0F4F8);
    public static final Color SIDEBAR     = new Color(0x1A2744);
    public static final Color ACTIVO      = new Color(0x2E4A8A);
    public static final Color HOVER_MENU  = new Color(0x243560);
    public static final Color BLANCO      = Color.WHITE;
    public static final Color PRIMARIO    = new Color(0x1A6FBA);
    public static final Color EXITO       = new Color(0x2E7D32);
    public static final Color ADVERTENCIA = new Color(0xE07B00);
    public static final Color PELIGRO     = new Color(0xC0392B);
    public static final Color NEUTRO      = new Color(0x546E7A);
    public static final Color BORDE       = new Color(0xDDE3ED);
    public static final Color TEXTO       = new Color(0x1A2744);
    public static final Color TEXTO_GRIS  = new Color(0x888888);
    public static final Color TEXTO_SUAVE = new Color(0x555555);

    // ── Tipografia ───────────────────────────────────────────────────────────

    /** Familia tipografica unica de la aplicacion (nativa de Windows 10+) */
    public static final String FUENTE = "Segoe UI";

    private Tema() {}

    /**
     * Crea una fuente de la familia del tema.
     *
     * @param estilo  Font.PLAIN, Font.BOLD o Font.ITALIC
     * @param tamanio Tamanio en puntos
     * @return Fuente configurada
     */
    public static Font fuente(int estilo, int tamanio) {
        return new Font(FUENTE, estilo, tamanio);
    }

    // ── Look and Feel ────────────────────────────────────────────────────────

    /**
     * Aplica el Look and Feel moderno (FlatLaf) a toda la aplicacion.
     * Debe llamarse una sola vez, antes de crear cualquier ventana.
     * Si FlatLaf no esta disponible se mantiene el L&F por defecto.
     */
    public static void aplicarLookAndFeel() {
        FlatLightLaf.setup();
        UIManager.put("defaultFont", fuente(Font.PLAIN, 13));
        UIManager.put("Button.arc", 10);
        UIManager.put("Component.arc", 10);
        UIManager.put("TextComponent.arc", 10);
    }

    // ── Fabrica de botones ───────────────────────────────────────────────────

    /** Boton azul — accion principal (buscar, generar, etc.) */
    public static JButton botonPrimario(String texto)    { return boton(texto, PRIMARIO); }

    /** Boton verde — guardar / registrar */
    public static JButton botonExito(String texto)       { return boton(texto, EXITO); }

    /** Boton rojo — eliminar / desactivar */
    public static JButton botonPeligro(String texto)     { return boton(texto, PELIGRO); }

    /** Boton gris azulado — limpiar / cancelar */
    public static JButton botonNeutro(String texto)      { return boton(texto, NEUTRO); }

    /** Boton naranja — actualizar / editar */
    public static JButton botonAdvertencia(String texto) { return boton(texto, ADVERTENCIA); }

    private static JButton boton(String texto, Color color) {
        JButton btn = new JButton(texto);
        btn.setBackground(color);
        btn.setForeground(BLANCO);
        btn.setFont(fuente(Font.BOLD, 12));
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(120, 34));
        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                if (btn.isEnabled()) btn.setBackground(color.darker());
            }
            @Override public void mouseExited(MouseEvent e) {
                btn.setBackground(color);
            }
        });
        return btn;
    }

    // ── Campos de formulario ─────────────────────────────────────────────────

    /**
     * Aplica el estilo claro comun a los campos de texto del sistema.
     *
     * @param campo Campo de texto a estilizar
     */
    public static void estilizarCampo(JTextField campo) {
        campo.setBackground(BLANCO);
        campo.setForeground(TEXTO);
        campo.setFont(fuente(Font.PLAIN, 13));
        campo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDE, 1),
            new EmptyBorder(8, 10, 8, 10)
        ));
        campo.setPreferredSize(new Dimension(0, 38));
    }

    /**
     * Crea la etiqueta pequenia en mayusculas que acompania a un campo.
     *
     * @param texto Texto de la etiqueta
     * @return Etiqueta estilizada
     */
    public static JLabel etiquetaCampo(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(fuente(Font.BOLD, 11));
        lbl.setForeground(TEXTO_SUAVE);
        return lbl;
    }

    // ── Header de modulo ─────────────────────────────────────────────────────

    /**
     * Barra de titulo azul oscuro para la parte superior de cada formulario.
     *
     * @param titulo Texto a mostrar en la barra
     * @return Panel cabecera configurado
     */
    public static JPanel header(String titulo) {
        JPanel h = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 10));
        h.setBackground(SIDEBAR);
        JLabel lbl = new JLabel(titulo);
        lbl.setForeground(BLANCO);
        lbl.setFont(fuente(Font.BOLD, 15));
        h.add(lbl);
        return h;
    }

    // ── Tablas ───────────────────────────────────────────────────────────────

    /**
     * Aplica el estilo del tema a una JTable: encabezado oscuro, filas altas,
     * seleccion azul claro y cuadricula suave.
     *
     * @param tabla Tabla a estilizar
     */
    public static void estilizarTabla(JTable tabla) {
        tabla.setRowHeight(28);
        tabla.setFont(fuente(Font.PLAIN, 12));
        tabla.setGridColor(BORDE);
        tabla.setShowGrid(true);
        tabla.setSelectionBackground(new Color(0xBBD3F0));
        tabla.setSelectionForeground(SIDEBAR);
        tabla.setBackground(BLANCO);
        tabla.setIntercellSpacing(new Dimension(1, 1));

        JTableHeader header = tabla.getTableHeader();
        header.setBackground(SIDEBAR);
        header.setForeground(BLANCO);
        header.setFont(fuente(Font.BOLD, 12));
        header.setReorderingAllowed(false);
        header.setPreferredSize(new Dimension(header.getWidth(), 32));
    }

    // ── Hint para acciones contextuales ──────────────────────────────────────

    /**
     * Etiqueta de ayuda en cursiva gris para indicar acciones contextuales.
     *
     * @param texto Mensaje de ayuda
     * @return Etiqueta estilizada
     */
    public static JLabel hint(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(fuente(Font.ITALIC, 11));
        lbl.setForeground(TEXTO_GRIS);
        return lbl;
    }
}
