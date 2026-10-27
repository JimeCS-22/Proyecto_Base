package view;

import dao.ClienteDAO;
import dao.ProductoDAO;
import dao.FacturaDAO;
import dao.ReporteDAO;
import model.Cliente;
import model.Producto;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;

public class DashboardView extends JFrame {

    private String usuarioSesion;
    private String passwordSesion;

    private JTabbedPane tabbedPane;

    // DAOs
    private ClienteDAO clienteDAO = new ClienteDAO();
    private ProductoDAO productoDAO = new ProductoDAO();
    private FacturaDAO facturaDAO = new FacturaDAO();
    private ReporteDAO reporteDAO = new ReporteDAO();

    // --- PALETA DE COLORES (Dark Mode elegante) ---
    private final Color COLOR_FONDO = new Color(30, 30, 40);          // Fondo general oscuro
    private final Color COLOR_PANEL = new Color(40, 40, 55);          // Fondo de paneles y tablas
    private final Color COLOR_TEXTO = new Color(230, 230, 240);       // Texto claro
    private final Color COLOR_ACENTO = new Color(0, 200, 150);        // Verde/Teal para botones
    private final Color COLOR_BOTON_HOVER = new Color(0, 230, 180);   // Verde más brillante
    private final Color COLOR_TABLA_HEADER = new Color(50, 50, 70);   // Cabecera de tabla

    public DashboardView(String usuario, String password) {
        this.usuarioSesion = usuario;
        this.passwordSesion = password;

        setTitle("Propi - Sistema de Facturación [Usuario: " + usuarioSesion + "]");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(COLOR_FONDO);

        initComponents();
    }

    private void initComponents() {
        tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(COLOR_FONDO);
        tabbedPane.setForeground(COLOR_TEXTO);
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 14));

        // Forzar a que las pestañas usen el fondo oscuro
        tabbedPane.setUI(new javax.swing.plaf.basic.BasicTabbedPaneUI() {
            @Override
            protected void paintTabBackground(Graphics g, int tabPlacement, int tabIndex, int x, int y, int w, int h, boolean isSelected) {
                g.setColor(isSelected ? COLOR_PANEL : COLOR_FONDO);
                g.fillRect(x, y, w, h);
            }
            @Override
            protected void paintText(Graphics g, int tabPlacement, Font font, FontMetrics metrics, int tabIndex, String title, Rectangle textRect, boolean isSelected) {
                g.setColor(isSelected ? COLOR_ACENTO : COLOR_TEXTO);
                super.paintText(g, tabPlacement, font, metrics, tabIndex, title, textRect, isSelected);
            }
        });

        // 1. Pestaña de Clientes
        JPanel panelClientes = crearPanelClientes();
        tabbedPane.addTab("  Clientes  ", panelClientes);

        // 2. Pestaña de Productos
        JPanel panelProductos = crearPanelProductos();
        tabbedPane.addTab("  Productos  ", panelProductos);

        // 3. Pestaña de Facturación
        JPanel panelFacturacion = crearPanelFacturacion();
        tabbedPane.addTab("  Emitir Factura  ", panelFacturacion);

        // 4. Pestaña de Reportes
        JPanel panelReportes = crearPanelReportes();
        tabbedPane.addTab("  Reportes  ", panelReportes);

        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel crearPanelClientes() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(COLOR_FONDO);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        String[] columnas = {"ID", "Cédula", "Nombre", "Apellidos", "Teléfono", "Correo", "Activo"};
        DefaultTableModel modeloTabla = new DefaultTableModel(columnas, 0);
        JTable tablaClientes = new JTable(modeloTabla);
        estilizarTabla(tablaClientes);

        List<Cliente> clientes = clienteDAO.listar(usuarioSesion, passwordSesion);
        for (Cliente c : clientes) {
            modeloTabla.addRow(new Object[]{
                    c.getIdCliente(),
                    c.getCedula(),
                    c.getNombre(),
                    c.getPrimerApellido() + " " + (c.getSegundoApellido() != null ? c.getSegundoApellido() : ""),
                    c.getTelefono(),
                    c.getCorreo(),
                    c.isActivo() ? "Sí" : "No"
            });
        }

        JScrollPane scroll = new JScrollPane(tablaClientes);
        scroll.getViewport().setBackground(COLOR_PANEL);
        panel.add(scroll, BorderLayout.CENTER);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelBotones.setBackground(COLOR_FONDO);

        // Botón Nuevo Cliente
        JButton btnNuevo = crearBoton("+ Nuevo Cliente");
        btnNuevo.addActionListener(e -> {
            ClienteFormDialog dialog = new ClienteFormDialog(this, usuarioSesion, passwordSesion);
            dialog.setVisible(true);

            if (dialog.isGuardadoExitoso()) {
                modeloTabla.setRowCount(0);
                List<Cliente> listaActualizada = clienteDAO.listar(usuarioSesion, passwordSesion);
                for (Cliente c : listaActualizada) {
                    modeloTabla.addRow(new Object[]{
                            c.getIdCliente(), c.getCedula(), c.getNombre(),
                            c.getPrimerApellido() + " " + (c.getSegundoApellido() != null ? c.getSegundoApellido() : ""),
                            c.getTelefono(), c.getCorreo(), c.isActivo() ? "Sí" : "No"
                    });
                }
            }
        });
        panelBotones.add(btnNuevo);

        // Botón Recargar
        JButton btnRecargar = crearBoton("Actualizar Lista");
        btnRecargar.addActionListener(e -> {
            modeloTabla.setRowCount(0);
            List<Cliente> listaActualizada = clienteDAO.listar(usuarioSesion, passwordSesion);
            for (Cliente c : listaActualizada) {
                modeloTabla.addRow(new Object[]{
                        c.getIdCliente(), c.getCedula(), c.getNombre(),
                        c.getPrimerApellido() + " " + (c.getSegundoApellido() != null ? c.getSegundoApellido() : ""),
                        c.getTelefono(), c.getCorreo(), c.isActivo() ? "Sí" : "No"
                });
            }
        });
        panelBotones.add(btnRecargar);
        panel.add(panelBotones, BorderLayout.NORTH);

        return panel;
    }

    private JPanel crearPanelProductos() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(COLOR_FONDO);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        String[] columnas = {"Código CABYS", "Nombre", "Descripción", "Precio Unitario", "Stock", "Activo"};
        DefaultTableModel modeloTabla = new DefaultTableModel(columnas, 0);
        JTable tablaProductos = new JTable(modeloTabla);
        estilizarTabla(tablaProductos);

        List<Producto> productos = productoDAO.listar(usuarioSesion, passwordSesion);
        for (Producto p : productos) {
            modeloTabla.addRow(new Object[]{
                    p.getCodigo(),
                    p.getNombre(),
                    p.getDescripcion(),
                    "₡" + p.getPrecioUnitario(),
                    p.getStock(),
                    p.isActivo() ? "Sí" : "No"
            });
        }

        JScrollPane scroll = new JScrollPane(tablaProductos);
        scroll.getViewport().setBackground(COLOR_PANEL);
        panel.add(scroll, BorderLayout.CENTER);

        // --- BOTONES PRODUCTOS ---
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelBotones.setBackground(COLOR_FONDO);

        JButton btnNuevo = crearBoton("+ Nuevo Producto");
        btnNuevo.addActionListener(e -> {
            ProductoFormDialog dialog = new ProductoFormDialog(this, usuarioSesion, passwordSesion);
            dialog.setVisible(true);

            if (dialog.isGuardadoExitoso()) {
                modeloTabla.setRowCount(0);
                List<Producto> listaActualizada = productoDAO.listar(usuarioSesion, passwordSesion);
                for (Producto p : listaActualizada) {
                    modeloTabla.addRow(new Object[]{
                            p.getCodigo(), p.getNombre(), p.getDescripcion(),
                            "₡" + p.getPrecioUnitario(), p.getStock(), p.isActivo() ? "Sí" : "No"
                    });
                }
            }
        });
        panelBotones.add(btnNuevo);

        JButton btnRecargar = crearBoton("Actualizar Lista");
        btnRecargar.addActionListener(e -> {
            modeloTabla.setRowCount(0);
            List<Producto> listaActualizada = productoDAO.listar(usuarioSesion, passwordSesion);
            for (Producto p : listaActualizada) {
                modeloTabla.addRow(new Object[]{
                        p.getCodigo(), p.getNombre(), p.getDescripcion(),
                        "₡" + p.getPrecioUnitario(), p.getStock(), p.isActivo() ? "Sí" : "No"
                });
            }
        });
        panelBotones.add(btnRecargar);

        panel.add(panelBotones, BorderLayout.NORTH);
        return panel;
    }

    private JPanel crearPanelFacturacion() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(COLOR_FONDO);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // --- 1. PANEL SUPERIOR: Selección de Cliente ---
        JPanel panelCliente = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        panelCliente.setBackground(COLOR_PANEL);
        panelCliente.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(COLOR_ACENTO), "Datos del Cliente", 0, 0, new Font("Segoe UI", Font.BOLD, 12), COLOR_ACENTO));

        JLabel lblCliente = new JLabel("Seleccionar Cliente:");
        lblCliente.setForeground(COLOR_TEXTO);
        panelCliente.add(lblCliente);

        JComboBox<Cliente> cmbClientes = new JComboBox<>();
        estilizarComboBox(cmbClientes);
        List<Cliente> listaClientes = clienteDAO.listar(usuarioSesion, passwordSesion);
        for (Cliente c : listaClientes) {
            cmbClientes.addItem(c);
        }

        cmbClientes.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                label.setOpaque(true);
                if (index == -1) {
                    label.setBackground(COLOR_FONDO);
                    label.setForeground(COLOR_TEXTO);
                } else {
                    label.setBackground(isSelected ? COLOR_ACENTO : COLOR_FONDO);
                    label.setForeground(isSelected ? Color.BLACK : COLOR_TEXTO);
                }
                if (value instanceof Cliente) {
                    Cliente c = (Cliente) value;
                    label.setText(c.getNombre() + " " + c.getPrimerApellido() + " (ID: " + c.getIdCliente() + ")");
                }
                return label;
            }
        });
        panelCliente.add(cmbClientes);
        panel.add(panelCliente, BorderLayout.NORTH);

        // --- 2. PANEL CENTRAL: Selección de Productos y Tabla de Detalles ---
        JPanel panelCentro = new JPanel(new BorderLayout(5, 10));
        panelCentro.setBackground(COLOR_FONDO);

        JPanel panelAgregarProd = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panelAgregarProd.setBackground(COLOR_PANEL);
        panelAgregarProd.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(COLOR_ACENTO), "Agregar Productos", 0, 0, new Font("Segoe UI", Font.BOLD, 12), COLOR_ACENTO));

        JLabel lblProd = new JLabel("Producto:");
        lblProd.setForeground(COLOR_TEXTO);
        panelAgregarProd.add(lblProd);

        JComboBox<Producto> cmbProductos = new JComboBox<>();
        estilizarComboBox(cmbProductos);
        List<Producto> listaProductos = productoDAO.listar(usuarioSesion, passwordSesion);
        for (Producto p : listaProductos) {
            cmbProductos.addItem(p);
        }

        cmbProductos.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                label.setOpaque(true);
                if (index == -1) {
                    label.setBackground(COLOR_FONDO);
                    label.setForeground(COLOR_TEXTO);
                } else {
                    label.setBackground(isSelected ? COLOR_ACENTO : COLOR_FONDO);
                    label.setForeground(isSelected ? Color.BLACK : COLOR_TEXTO);
                }
                if (value instanceof Producto) {
                    Producto p = (Producto) value;
                    label.setText(p.getNombre() + " - ₡" + p.getPrecioUnitario());
                }
                return label;
            }
        });
        panelAgregarProd.add(cmbProductos);

        JLabel lblCant = new JLabel("Cantidad:");
        lblCant.setForeground(COLOR_TEXTO);
        panelAgregarProd.add(lblCant);

        JTextField txtCantidad = new JTextField("1", 5);
        estilizarTextField(txtCantidad);
        panelAgregarProd.add(txtCantidad);

        JButton btnAgregarItem = crearBoton("+ Agregar a Factura");
        panelAgregarProd.add(btnAgregarItem);

        panelCentro.add(panelAgregarProd, BorderLayout.NORTH);

        // Tabla de ítems
        String[] columnasDetalle = {"Cód. CABYS", "Producto", "Precio Unit.", "Cantidad", "Subtotal"};
        DefaultTableModel modeloDetalle = new DefaultTableModel(columnasDetalle, 0);
        JTable tablaDetalle = new JTable(modeloDetalle);
        estilizarTabla(tablaDetalle);

        JScrollPane scrollDetalle = new JScrollPane(tablaDetalle);
        scrollDetalle.getViewport().setBackground(COLOR_PANEL);
        panelCentro.add(scrollDetalle, BorderLayout.CENTER);

        panel.add(panelCentro, BorderLayout.CENTER);

        // --- 3. PANEL INFERIOR: Totales y Botones de Acción ---
        JPanel panelSur = new JPanel(new BorderLayout());
        panelSur.setBackground(COLOR_PANEL);
        panelSur.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel panelTotales = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 0));
        panelTotales.setBackground(COLOR_PANEL);

        JLabel lblSubtotalVal = new JLabel("Subtotal: ₡0.00");
        lblSubtotalVal.setForeground(COLOR_TEXTO);
        JLabel lblImpuestoVal = new JLabel("IVA (13%): ₡0.00");
        lblImpuestoVal.setForeground(COLOR_TEXTO);
        JLabel lblTotalVal = new JLabel("TOTAL: ₡0.00");
        lblTotalVal.setForeground(COLOR_ACENTO);
        lblTotalVal.setFont(new Font("Segoe UI", Font.BOLD, 18));

        panelTotales.add(lblSubtotalVal);
        panelTotales.add(lblImpuestoVal);
        panelTotales.add(lblTotalVal);
        panelSur.add(panelTotales, BorderLayout.CENTER);

        JPanel panelAccion = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelAccion.setBackground(COLOR_PANEL);

        // --- BOTÓN PARA EMITIR NOTA CRÉDITO/DÉBITO ---
        JButton btnNota = crearBoton("Emitir Nota C/D");
        btnNota.setBackground(new Color(200, 150, 0)); // Color mostaza/naranja para diferenciarlo
        btnNota.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) { btnNota.setBackground(new Color(230, 180, 0)); }
            public void mouseExited(java.awt.event.MouseEvent evt) { btnNota.setBackground(new Color(200, 150, 0)); }
        });
        btnNota.addActionListener(e -> {
            // Abre el formulario de Notas
            // Asegúrate de que la clase NotaFormDialog exista en el paquete org.example.view
            view.NotaFormDialog dialog = new view.NotaFormDialog(this, usuarioSesion, passwordSesion);
            dialog.setVisible(true);

            if (dialog.isGuardadoExitoso()) {
                JOptionPane.showMessageDialog(this, "¡Nota registrada con éxito en el sistema!", "Información", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        panelAccion.add(btnNota);

        // Botón Emitir Factura
        JButton btnEmitirFactura = crearBoton("Emitir y Guardar Factura");
        btnEmitirFactura.setBackground(new Color(0, 150, 100));
        panelAccion.add(btnEmitirFactura);

        panelSur.add(panelAccion, BorderLayout.SOUTH);

        panel.add(panelSur, BorderLayout.SOUTH);

        // --- LÓGICA ---
        java.util.List<model.DetalleFactura> listaDetallesTemp = new java.util.ArrayList<>();

        btnAgregarItem.addActionListener(e -> {
            try {
                Producto prodSeleccionado = (Producto) cmbProductos.getSelectedItem();
                int cantidad = Integer.parseInt(txtCantidad.getText().trim());

                if (prodSeleccionado == null) return;
                if (cantidad <= 0) {
                    JOptionPane.showMessageDialog(this, "La cantidad debe ser mayor a cero.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                model.DetalleFactura detalle = new model.DetalleFactura(
                        prodSeleccionado.getCodigo(),
                        cantidad,
                        prodSeleccionado.getPrecioUnitario()
                );

                listaDetallesTemp.add(detalle);
                modeloDetalle.addRow(new Object[]{
                        prodSeleccionado.getCodigo(),
                        prodSeleccionado.getNombre(),
                        "₡" + prodSeleccionado.getPrecioUnitario(),
                        cantidad,
                        "₡" + detalle.getSubtotalLinea()
                });

                java.math.BigDecimal subtotal = java.math.BigDecimal.ZERO;
                for (model.DetalleFactura d : listaDetallesTemp) {
                    subtotal = subtotal.add(d.getSubtotalLinea());
                }
                java.math.BigDecimal impuesto = subtotal.multiply(new java.math.BigDecimal("0.13"));
                java.math.BigDecimal total = subtotal.add(impuesto);

                lblSubtotalVal.setText("Subtotal: ₡" + subtotal);
                lblImpuestoVal.setText("IVA (13%): ₡" + impuesto);
                lblTotalVal.setText("TOTAL: ₡" + total);

                txtCantidad.setText("1");

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Ingrese una cantidad numérica válida.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnEmitirFactura.addActionListener(e -> {
            Cliente clienteSel = (Cliente) cmbClientes.getSelectedItem();
            if (clienteSel == null || listaDetallesTemp.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Debe seleccionar un cliente y agregar al menos un producto.", "Advertencia", JOptionPane.WARNING_MESSAGE);
                return;
            }

            java.math.BigDecimal subtotal = java.math.BigDecimal.ZERO;
            for (model.DetalleFactura d : listaDetallesTemp) {
                subtotal = subtotal.add(d.getSubtotalLinea());
            }
            java.math.BigDecimal impuesto = subtotal.multiply(new java.math.BigDecimal("0.13"));
            java.math.BigDecimal total = subtotal.add(impuesto);

            model.Factura nuevaFactura = new model.Factura();
            nuevaFactura.setIdCliente(clienteSel.getIdCliente());
            nuevaFactura.setSubtotal(subtotal);
            nuevaFactura.setImpuesto(impuesto);
            nuevaFactura.setTotal(total);
            nuevaFactura.setEstado("Emitida");
            nuevaFactura.setDetalles(listaDetallesTemp);

            boolean registrado = facturaDAO.emitirFactura(nuevaFactura, usuarioSesion, passwordSesion);
            if (registrado) {
                JOptionPane.showMessageDialog(this, "¡Factura emitida con éxito! ID: " + nuevaFactura.getIdFactura(), "Éxito", JOptionPane.INFORMATION_MESSAGE);
                modeloDetalle.setRowCount(0);
                listaDetallesTemp.clear();
                lblSubtotalVal.setText("Subtotal: ₡0.00");
                lblImpuestoVal.setText("IVA (13%): ₡0.00");
                lblTotalVal.setText("TOTAL: ₡0.00");
            } else {
                JOptionPane.showMessageDialog(this, "Error al emitir la factura en la base de datos.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        return panel;
    }

    private JPanel crearPanelReportes() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(COLOR_FONDO);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Panel de filtros superior
        JPanel panelFiltros = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panelFiltros.setBackground(COLOR_PANEL);
        panelFiltros.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(COLOR_ACENTO), "Filtros de Búsqueda", 0, 0, new Font("Segoe UI", Font.BOLD, 12), COLOR_ACENTO));

        JLabel lblTipo = new JLabel("Reporte:");
        lblTipo.setForeground(COLOR_TEXTO);
        panelFiltros.add(lblTipo);

        JComboBox<String> cmbTipoReporte = new JComboBox<>(new String[]{"Clientes", "Facturas", "Auditoría"});
        estilizarComboBox(cmbTipoReporte);
        panelFiltros.add(cmbTipoReporte);

        JLabel lblDesde = new JLabel("Desde (YYYY-MM-DD):");
        lblDesde.setForeground(COLOR_TEXTO);
        panelFiltros.add(lblDesde);

        JTextField txtDesde = new JTextField("2020-01-01 00:00:00", 12);
        estilizarTextField(txtDesde);
        panelFiltros.add(txtDesde);

        JLabel lblHasta = new JLabel("Hasta:");
        lblHasta.setForeground(COLOR_TEXTO);
        panelFiltros.add(lblHasta);

        JTextField txtHasta = new JTextField("2030-12-31 23:59:59", 12);
        estilizarTextField(txtHasta);
        panelFiltros.add(txtHasta);

        JButton btnGenerar = crearBoton("Generar Reporte");
        panelFiltros.add(btnGenerar);

        panel.add(panelFiltros, BorderLayout.NORTH);

        // Tabla de resultados
        DefaultTableModel modeloReporte = new DefaultTableModel();
        JTable tablaReporte = new JTable(modeloReporte);
        estilizarTabla(tablaReporte);
        JScrollPane scroll = new JScrollPane(tablaReporte);
        scroll.getViewport().setBackground(COLOR_PANEL);
        panel.add(scroll, BorderLayout.CENTER);

        // Lógica del botón Generar
        btnGenerar.addActionListener(e -> {
            String tipo = (String) cmbTipoReporte.getSelectedItem();
            String desde = txtDesde.getText().trim();
            String hasta = txtHasta.getText().trim();

            try {
                if ("Clientes".equals(tipo)) {
                    reporteDAO.cargarClientes(modeloReporte, desde, hasta, "T", usuarioSesion, passwordSesion);
                } else if ("Facturas".equals(tipo)) {
                    reporteDAO.cargarFacturas(modeloReporte, desde, hasta, usuarioSesion, passwordSesion);
                } else if ("Auditoría".equals(tipo)) {
                    reporteDAO.cargarPistasAuditoria(modeloReporte, desde, hasta, "TODOS", usuarioSesion, passwordSesion);
                }

                // Si no se cargaron filas, mostramos el mensaje de "vacío"
                if (modeloReporte.getRowCount() == 0) {
                    if (modeloReporte.getColumnCount() == 0) {
                        modeloReporte.addColumn("Mensaje");
                    }
                    modeloReporte.addRow(new Object[]{"No hay registros para este reporte en las fechas seleccionadas."});
                }

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al generar el reporte:\n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        return panel;
    }

    // --- MÉTODOS DE ESTILO REUTILIZABLES ---

    private void estilizarTabla(JTable tabla) {
        tabla.setBackground(COLOR_PANEL);
        tabla.setForeground(COLOR_TEXTO);
        tabla.setGridColor(new Color(60, 60, 80));
        tabla.setRowHeight(30);
        tabla.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabla.setSelectionBackground(COLOR_ACENTO);
        tabla.setSelectionForeground(Color.BLACK);

        JTableHeader header = tabla.getTableHeader();
        header.setBackground(COLOR_TABLA_HEADER);
        header.setForeground(COLOR_ACENTO);
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        ((DefaultTableCellRenderer)header.getDefaultRenderer()).setHorizontalAlignment(JLabel.LEFT);
    }

    private JButton crearBoton(String texto) {
        JButton btn = new JButton(texto);
        btn.setBackground(COLOR_ACENTO);
        btn.setForeground(Color.BLACK);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(COLOR_BOTON_HOVER);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(COLOR_ACENTO);
            }
        });
        return btn;
    }

    private void estilizarComboBox(JComboBox<?> combo) {
        combo.setBackground(COLOR_FONDO);
        combo.setForeground(COLOR_TEXTO);
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        combo.setUI(new javax.swing.plaf.basic.BasicComboBoxUI() {
            @Override
            protected JButton createArrowButton() {
                JButton button = new JButton("▼");
                button.setBackground(COLOR_FONDO);
                button.setForeground(COLOR_ACENTO);
                button.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
                button.setFocusPainted(false);
                return button;
            }
        });
    }

    private void estilizarTextField(JTextField txt) {
        txt.setBackground(COLOR_FONDO);
        txt.setForeground(COLOR_TEXTO);
        txt.setCaretColor(COLOR_TEXTO);
        txt.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txt.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_ACENTO),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
    }
}