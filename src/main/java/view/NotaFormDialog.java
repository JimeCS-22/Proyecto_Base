package view;

import dao.NotaDAO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class NotaFormDialog extends JDialog {
    private JTextField txtNumFactura, txtMotivo;
    private JComboBox<String> cmbTipoNota;
    private JButton btnGuardar;
    private String usuario, password;
    private NotaDAO notaDAO = new NotaDAO();
    private boolean guardadoExitoso = false;

    // --- PALETA DE COLORES (Igual al Dashboard) ---
    private final Color COLOR_FONDO = new Color(30, 30, 40);
    private final Color COLOR_PANEL = new Color(40, 40, 55);
    private final Color COLOR_TEXTO = new Color(230, 230, 240);
    private final Color COLOR_ACENTO = new Color(0, 200, 150);
    private final Color COLOR_BOTON_HOVER = new Color(0, 230, 180);

    public NotaFormDialog(JFrame parent, String usuario, String password) {
        super(parent, "Emitir Nota de Crédito / Débito", true);
        this.usuario = usuario;
        this.password = password;

        setSize(400, 350);
        setLocationRelativeTo(parent);
        getContentPane().setBackground(COLOR_FONDO);
        setLayout(new BorderLayout());

        // Panel contenedor
        JPanel panelFormulario = new JPanel();
        panelFormulario.setLayout(new BoxLayout(panelFormulario, BoxLayout.Y_AXIS));
        panelFormulario.setBackground(COLOR_FONDO);
        panelFormulario.setBorder(new EmptyBorder(15, 20, 15, 20));

        // Título
        JLabel lblTitulo = new JLabel("Datos de la Nota");
        lblTitulo.setForeground(COLOR_ACENTO);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelFormulario.add(lblTitulo);
        panelFormulario.add(Box.createVerticalStrut(15));

        // --- CAMPOS ---
        txtNumFactura = crearCampo(panelFormulario, "Número de Factura (Obligatorio):");

        panelFormulario.add(crearLabel("Tipo de Nota:"));
        cmbTipoNota = new JComboBox<>(new String[]{"C - Nota de Crédito", "D - Nota de Débito"});
        estilizarComboBox(cmbTipoNota);
        panelFormulario.add(cmbTipoNota);
        panelFormulario.add(Box.createVerticalStrut(10));

        txtMotivo = crearCampo(panelFormulario, "Motivo de la Nota:");

        // Botón Guardar
        btnGuardar = new JButton("Guardar Nota");
        btnGuardar.setBackground(COLOR_ACENTO);
        btnGuardar.setForeground(Color.BLACK);
        btnGuardar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnGuardar.setFocusPainted(false);
        btnGuardar.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnGuardar.setMaximumSize(new Dimension(200, 40));
        btnGuardar.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Efecto Hover
        btnGuardar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) { btnGuardar.setBackground(COLOR_BOTON_HOVER); }
            public void mouseExited(java.awt.event.MouseEvent evt) { btnGuardar.setBackground(COLOR_ACENTO); }
        });
        btnGuardar.addActionListener(e -> guardarNota());

        panelFormulario.add(Box.createVerticalStrut(20));
        panelFormulario.add(btnGuardar);

        // Agregar al JFrame
        JScrollPane scrollPane = new JScrollPane(panelFormulario);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(COLOR_FONDO);
        add(scrollPane, BorderLayout.CENTER);
    }

    // --- MÉTODOS DE ESTILO ---
    private JLabel crearLabel(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setForeground(COLOR_TEXTO);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    private JTextField crearCampo(JPanel panel, String labelText) {
        panel.add(crearLabel(labelText));
        JTextField txt = new JTextField();
        txt.setBackground(COLOR_PANEL);
        txt.setForeground(COLOR_TEXTO);
        txt.setCaretColor(COLOR_TEXTO);
        txt.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txt.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        txt.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_ACENTO),
                BorderFactory.createEmptyBorder(2, 5, 2, 5)
        ));
        panel.add(txt);
        panel.add(Box.createVerticalStrut(10));
        return txt;
    }

    private void estilizarComboBox(JComboBox<?> combo) {
        combo.setBackground(COLOR_PANEL);
        combo.setForeground(COLOR_TEXTO);
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        combo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        combo.setAlignmentX(Component.LEFT_ALIGNMENT);

        combo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                label.setOpaque(true);
                if (index == -1) {
                    label.setBackground(COLOR_PANEL);
                    label.setForeground(COLOR_TEXTO);
                } else {
                    label.setBackground(isSelected ? COLOR_ACENTO : COLOR_PANEL);
                    label.setForeground(isSelected ? Color.BLACK : COLOR_TEXTO);
                }
                return label;
            }
        });
    }

    // --- LÓGICA DE GUARDADO ---
    private void guardarNota() {
        try {
            if (txtNumFactura.getText().trim().isEmpty() || txtMotivo.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios.", "Advertencia", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int numFactura = Integer.parseInt(txtNumFactura.getText().trim());
            String tipoSeleccionado = cmbTipoNota.getSelectedIndex() == 0 ? "C" : "D";
            String motivo = txtMotivo.getText().trim();

            boolean exito = notaDAO.insertarNota(numFactura, tipoSeleccionado, motivo, usuario, password);

            if (exito) {
                JOptionPane.showMessageDialog(this, "¡Nota registrada con éxito!", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                guardadoExitoso = true;
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Error al registrar la nota en la base de datos. Verifique que el número de factura exista.", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "El número de factura debe ser un valor numérico.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isGuardadoExitoso() {
        return guardadoExitoso;
    }
}
