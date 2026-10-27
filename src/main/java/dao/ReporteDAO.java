package dao;

import org.example.ConexionBD;
import javax.swing.table.DefaultTableModel;
import java.sql.*;

public class ReporteDAO {

    private void ejecutarReporte(DefaultTableModel modelo, String sql, Object[] parametros, String user, String pass) {
        modelo.setRowCount(0); // Limpiar filas
        modelo.setColumnCount(0); // Limpiar columnas anteriores

        try (Connection conn = ConexionBD.conectar(user, pass);
             CallableStatement stmt = conn.prepareCall(sql)) {

            // Asignar parámetros dinámicamente
            for (int i = 0; i < parametros.length; i++) {
                stmt.setObject(i + 1, parametros[i]);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                ResultSetMetaData metaData = rs.getMetaData();
                int columnCount = metaData.getColumnCount();

                // Agregar las nuevas columnas
                for (int i = 1; i <= columnCount; i++) {
                    modelo.addColumn(metaData.getColumnLabel(i));
                }

                // Agregar filas
                while (rs.next()) {
                    Object[] row = new Object[columnCount];
                    for (int i = 1; i <= columnCount; i++) {
                        row[i - 1] = rs.getObject(i);
                    }
                    modelo.addRow(row);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void cargarClientes(DefaultTableModel modelo, String desde, String hasta, String estado, String user, String pass) {
        String sql = "{CALL dbo.sp_reporte_clientes_resumido(?, ?, ?)}";
        Object[] params = { desde, hasta, estado };
        ejecutarReporte(modelo, sql, params, user, pass);
    }

    public void cargarFacturas(DefaultTableModel modelo, String desde, String hasta, String user, String pass) {
        String sql = "{CALL dbo.sp_reporte_facturas(?, ?)}";
        Object[] params = { desde, hasta };
        ejecutarReporte(modelo, sql, params, user, pass);
    }

    public void cargarPistasAuditoria(DefaultTableModel modelo, String desde, String hasta, String usuarioFiltro, String user, String pass) {
        String sql = "{CALL dbo.sp_reporte_pistas_auditoria(?, ?, ?)}";
        Object[] params = { desde, hasta, usuarioFiltro };
        ejecutarReporte(modelo, sql, params, user, pass);
    }
}