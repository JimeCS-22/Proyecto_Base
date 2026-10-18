package view;

import org.example.ConexionBD;
import view.DashboardView;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetAddress;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;

public class LoginView extends JFrame {
    private JTextField txtUsuario;
    private JPasswordField txtPassword;
    private JButton btnIngresar;

    // --- PALETA DE COLORES ---
    private final Color COLOR_FONDO = new Color(30, 30, 40);
    private final Color COLOR_PANEL = new Color(40, 40, 55);
    private final Color COLOR_TEXTO = new Color(230, 230, 240);
    private final Color COLOR_ACENTO = new Color(0, 200, 150);

    public LoginView() {
        setTitle("Propi - Iniciar Sesión");
        setSize(400, 350);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridBagLayout()); // Centrado automático
        getContentPane().setBackground(COLOR_FONDO);

        // Panel contenedor central
        JPanel panelCentral = new JPanel();
        panelCentral.setLayout(new BoxLayout(panelCentral, BoxLayout.Y_AXIS));
        panelCentral.setBackground(COLOR_PANEL);
        panelCentral.setBorder(new EmptyBorder(30, 40, 30, 40));

        // Título
        JLabel lblTitulo = new JLabel("PROPI");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitulo.setForeground(COLOR_ACENTO);
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelCentral.add(lblTitulo);
        panelCentral.add(Box.createVerticalStrut(20));

        // Campo Usuario
        JLabel lblUsuario = new JLabel("Usuario:");
        lblUsuario.setForeground(COLOR_TEXTO);
        lblUsuario.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelCentral.add(lblUsuario);
        panelCentral.add(Box.createVerticalStrut(5));

        txtUsuario = new JTextField(15);
        estilizarCampoTexto(txtUsuario);
        panelCentral.add(txtUsuario);
        panelCentral.add(Box.createVerticalStrut(15));

        // Campo Contraseña
        JLabel lblPassword = new JLabel("Contraseña:");
        lblPassword.setForeground(COLOR_TEXTO);
        lblPassword.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelCentral.add(lblPassword);
        panelCentral.add(Box.createVerticalStrut(5));

        txtPassword = new JPasswordField(15);
        estilizarCampoTexto(txtPassword);
        panelCentral.add(txtPassword);
        panelCentral.add(Box.createVerticalStrut(25));

        // Botón
        btnIngresar = new JButton("INGRESAR");
        btnIngresar.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnIngresar.setBackground(COLOR_ACENTO);
        btnIngresar.setForeground(Color.BLACK);
        btnIngresar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnIngresar.setFocusPainted(false);
        btnIngresar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnIngresar.setMaximumSize(new Dimension(200, 40));
        btnIngresar.setPreferredSize(new Dimension(200, 40));
        panelCentral.add(btnIngresar);

        add(panelCentral);

        // Evento del botón
        btnIngresar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                autenticarUsuario();
            }
        });
    }

    private void estilizarCampoTexto(JTextField campo) {
        campo.setBackground(COLOR_FONDO);
        campo.setForeground(COLOR_TEXTO);
        campo.setCaretColor(COLOR_TEXTO);
        campo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_ACENTO),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        campo.setMaximumSize(new Dimension(300, 30));
    }

    private void autenticarUsuario() {
        String usuario = txtUsuario.getText().trim();
        String password = new String(txtPassword.getPassword());

        System.out.println("=== INTENTO DE LOGIN ===");
        System.out.println("Usuario ingresado: [" + usuario + "]");
        System.out.println("Largo de la contraseña: " + password.length());

        if (usuario.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, complete todos los campos.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        btnIngresar.setEnabled(false);
        btnIngresar.setText("CONECTANDO...");

        new SwingWorker<Void, Void>() {
            boolean exito = false;
            String mensajeError = "";
            int codAcceso = 0;

            @Override
            protected Void doInBackground() throws Exception {
                try {
                    Connection conexion = ConexionBD.conectar(usuario, password);
                    String nombreTerminal = InetAddress.getLocalHost().getHostName();
                    String direccionIp = InetAddress.getLocalHost().getHostAddress();

                    String sql = "{CALL dbo.sp_registrar_ingreso(?, ?, ?)}";
                    try (CallableStatement stmt = conexion.prepareCall(sql)) {
                        stmt.setString(1, nombreTerminal);
                        stmt.setString(2, direccionIp);
                        stmt.registerOutParameter(3, java.sql.Types.INTEGER);

                        stmt.execute();
                        codAcceso = stmt.getInt(3);
                        exito = true;
                    }
                } catch (SQLException ex) {
                    mensajeError = "Error de autenticación o conexión:\n" + ex.getMessage();
                } catch (Exception ex) {
                    mensajeError = "Ocurrió un error inesperado: " + ex.getMessage();
                }
                return null;
            }

            @Override
            protected void done() {
                btnIngresar.setEnabled(true);
                btnIngresar.setText("INGRESAR");

                if (exito) {
                    JOptionPane.showMessageDialog(LoginView.this, "¡Bienvenido al sistema Propi!\nAcceso registrado con ID: " + codAcceso, "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    new DashboardView(usuario, password).setVisible(true);
                    LoginView.this.dispose();
                } else {
                    JOptionPane.showMessageDialog(LoginView.this, mensajeError, "Acceso Denegado", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    public static void main(String[] args) {
        // Asegurar que se use el Look and Feel del sistema para que los componentes se vean bien
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            new LoginView().setVisible(true);
        });
    }
}