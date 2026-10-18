package view;

import dao.ProductoDAO;
import model.Producto;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.math.BigDecimal;

public class ProductoFormDialog extends JDialog {
    private JTextField txtCodCabys, txtNombre, txtDescripcion, txtPrecio;
    private JComboBox<String> cmbUnidadMedida, cmbImpuesto;
    private JButton btnGuardar;
    private String usuario, password;
    private ProductoDAO productoDAO = new ProductoDAO();
    private boolean guardadoExitoso = false;

    // --- PALETA DE COLORES ---
    private final Color COLOR_FONDO = new Color(30, 30, 40);
    private final Color COLOR_PANEL = new Color(40, 40, 55);
    private final Color COLOR_TEXTO = new Color(230, 230, 240);
    private final Color COLOR_ACENTO = new Color(0, 200, 150);
    private final Color COLOR_BOTON_HOVER = new Color(0, 230, 180);

    public ProductoFormDialog(JFrame parent, String usuario, String password) {
        super(parent, "Registrar Nuevo Producto", true);
        this.usuario = usuario;
        this.password = password;

        setSize(450, 500);
        setLocationRelativeTo(parent);
        getContentPane().setBackground(COLOR_FONDO);
        setLayout(new BorderLayout());

        JPanel panelFormulario = new JPanel();
        panelFormulario.setLayout(new BoxLayout(panelFormulario, BoxLayout.Y_AXIS));
        panelFormulario.setBackground(COLOR_FONDO);
        panelFormulario.setBorder(new EmptyBorder(15, 20, 15, 20));

        // Título
        JLabel lblTitulo = new JLabel("Datos del Producto");
        lblTitulo.setForeground(COLOR_ACENTO);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelFormulario.add(lblTitulo);
        panelFormulario.add(Box.createVerticalStrut(15));

        // --- CAMPOS ---
        txtCodCabys = crearCampo(panelFormulario, "Código CABYS (13 dígitos):");
        txtNombre = crearCampo(panelFormulario, "Nombre del Producto:");
        txtDescripcion = crearCampo(panelFormulario, "Descripción:");
        txtPrecio = crearCampo(panelFormulario, "Precio Unitario (₡):");

        panelFormulario.add(crearLabel("Unidad de Medida:"));
        cmbUnidadMedida = new JComboBox<>(new String[]{"1 - Unidad", "2 - Servicios profesionales", "3 - Metro cuadrado", "4 - Otros servicios"});
        estilizarComboBox(cmbUnidadMedida);
        panelFormulario.add(cmbUnidadMedida);
        panelFormulario.add(Box.createVerticalStrut(10));

        panelFormulario.add(crearLabel("Tipo de Impuesto:"));
        cmbImpuesto = new JComboBox<>(new String[]{"1 - IVA General (13%)", "2 - IVA Reducido (4%)", "3 - IVA Reducido (2%)", "4 - IVA Reducido (1%)", "5 - Exento"});
        estilizarComboBox(cmbImpuesto);
        panelFormulario.add(cmbImpuesto);

        // Botón Guardar
        btnGuardar = new JButton("Guardar Producto");
        btnGuardar.setBackground(COLOR_ACENTO);
        btnGuardar.setForeground(Color.BLACK);
        btnGuardar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnGuardar.setFocusPainted(false);
        btnGuardar.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnGuardar.setMaximumSize(new Dimension(200, 40));
        btnGuardar.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnGuardar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) { btnGuardar.setBackground(COLOR_BOTON_HOVER); }
            public void mouseExited(java.awt.event.MouseEvent evt) { btnGuardar.setBackground(COLOR_ACENTO); }
        });
        btnGuardar.addActionListener(e -> guardarProducto());

        panelFormulario.add(Box.createVerticalStrut(20));
        panelFormulario.add(btnGuardar);

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
    private void guardarProducto() {
        if (txtCodCabys.getText().trim().isEmpty() || txtNombre.getText().trim().isEmpty() || txtPrecio.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Código, Nombre y Precio son obligatorios.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            BigDecimal precio = new BigDecimal(txtPrecio.getText().trim());
            int codUnidad = cmbUnidadMedida.getSelectedIndex() + 1;
            int codImpuesto = cmbImpuesto.getSelectedIndex() + 1;

            boolean exito = productoDAO.insertar(
                    txtCodCabys.getText().trim(),
                    txtNombre.getText().trim(),
                    txtDescripcion.getText().trim(),
                    precio,
                    codUnidad,
                    codImpuesto,
                    usuario,
                    password
            );

            if (exito) {
                JOptionPane.showMessageDialog(this, "¡Producto registrado con éxito!", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                guardadoExitoso = true;
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Error al registrar el producto en la base de datos.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "El precio debe ser un número válido.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isGuardadoExitoso() {
        return guardadoExitoso;
    }
}