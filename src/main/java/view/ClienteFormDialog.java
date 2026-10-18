package view;

import dao.ClienteDAO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class ClienteFormDialog extends JDialog {
    private JTextField txtNombre, txtNumId, txtProvincia, txtCanton, txtDistrito, txtBarrio, txtProfesion, txtActividad, txtCorreo, txtTelefono;
    private JComboBox<String> cmbTipoId;
    private JButton btnGuardar;
    private String usuario, password;
    private ClienteDAO clienteDAO = new ClienteDAO();
    private boolean guardadoExitoso = false;

    // --- PALETA DE COLORES (Igual al Dashboard) ---
    private final Color COLOR_FONDO = new Color(30, 30, 40);
    private final Color COLOR_PANEL = new Color(40, 40, 55);
    private final Color COLOR_TEXTO = new Color(230, 230, 240);
    private final Color COLOR_ACENTO = new Color(0, 200, 150);
    private final Color COLOR_BOTON_HOVER = new Color(0, 230, 180);

    public ClienteFormDialog(JFrame parent, String usuario, String password) {
        super(parent, "Registrar Nuevo Cliente", true);
        this.usuario = usuario;
        this.password = password;

        setSize(450, 650);
        setLocationRelativeTo(parent);
        getContentPane().setBackground(COLOR_FONDO);
        setLayout(new BorderLayout());

        // Panel contenedor con scroll (por si la pantalla es pequeña)
        JPanel panelFormulario = new JPanel();
        panelFormulario.setLayout(new BoxLayout(panelFormulario, BoxLayout.Y_AXIS));
        panelFormulario.setBackground(COLOR_FONDO);
        panelFormulario.setBorder(new EmptyBorder(15, 20, 15, 20));

        // Título
        JLabel lblTitulo = new JLabel("Datos del Cliente");
        lblTitulo.setForeground(COLOR_ACENTO);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelFormulario.add(lblTitulo);
        panelFormulario.add(Box.createVerticalStrut(15));

        // --- CAMPOS DEL FORMULARIO ---
        txtNombre = crearCampo(panelFormulario, "Nombre Completo (Obligatorio):");

        panelFormulario.add(crearLabel("Tipo Identificación:"));
        cmbTipoId = new JComboBox<>(new String[]{"1 - Cédula física", "2 - Cédula jurídica", "3 - DIMEX", "4 - NITE", "5 - Pasaporte"});
        estilizarComboBox(cmbTipoId);
        panelFormulario.add(cmbTipoId);
        panelFormulario.add(Box.createVerticalStrut(10));

        txtNumId = crearCampo(panelFormulario, "Número Identificación (Obligatorio):");
        txtProvincia = crearCampo(panelFormulario, "Provincia:");
        txtCanton = crearCampo(panelFormulario, "Cantón:");
        txtDistrito = crearCampo(panelFormulario, "Distrito:");
        txtBarrio = crearCampo(panelFormulario, "Barrio:");
        txtProfesion = crearCampo(panelFormulario, "Profesión:");
        txtActividad = crearCampo(panelFormulario, "Actividad Económica:");
        txtCorreo = crearCampo(panelFormulario, "Correo Electrónico:");
        txtTelefono = crearCampo(panelFormulario, "Teléfono:");

        // Botón Guardar
        btnGuardar = new JButton("Guardar Cliente");
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
        btnGuardar.addActionListener(e -> guardarCliente());

        panelFormulario.add(Box.createVerticalStrut(20));
        panelFormulario.add(btnGuardar);

        // Agregar un JScrollPane por si el formulario es muy largo
        JScrollPane scrollPane = new JScrollPane(panelFormulario);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(COLOR_FONDO);
        add(scrollPane, BorderLayout.CENTER);
    }

    // --- MÉTODOS AUXILIARES PARA CREAR LOS CAMPOS RÁPIDAMENTE ---
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
    private void guardarCliente() {
        if (txtNombre.getText().trim().isEmpty() || txtNumId.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nombre e Identificación son obligatorios.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int tipoIdSeleccionado = cmbTipoId.getSelectedIndex() + 1;

        // Ejecutamos la inserción
        boolean exito = clienteDAO.insertar(
                txtNombre.getText().trim(),
                tipoIdSeleccionado,
                txtNumId.getText().trim(),
                txtProvincia.getText().trim(),
                txtCanton.getText().trim(),
                txtDistrito.getText().trim(),
                txtBarrio.getText().trim(),
                txtProfesion.getText().trim(),
                txtActividad.getText().trim(),
                txtCorreo.getText().trim(),
                txtTelefono.getText().trim(),
                usuario,
                password
        );

        if (exito) {
            JOptionPane.showMessageDialog(this, "¡Cliente registrado con éxito!", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            guardadoExitoso = true;
            dispose(); // Cierra la ventana
        } else {
            JOptionPane.showMessageDialog(this, "Error al registrar el cliente en la base de datos.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isGuardadoExitoso() {
        return guardadoExitoso;
    }
}