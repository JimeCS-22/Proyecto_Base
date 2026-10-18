package dao;

import org.example.ConexionBD;
import javax.swing.table.DefaultTableModel;
import java.sql.*;

public class ReporteDAO {

    // Método genérico para llenar un DefaultTableModel con los resultados de un SP
    public void cargarClientes(DefaultTableModel modelo, String desde, String hasta, String estado, String user, String pass) {
        modelo.setRowCount(0); // Limpiar tabla
        String sql = "{CALL dbo.sp_reporte_clientes_resumido(?, ?, ?)}";

        try (Connection conn = ConexionBD.conectar(user, pass);
             CallableStatement stmt = conn.prepareCall(sql)) {

            stmt.setString(1, desde); // Formato esperado: "YYYY-MM-DD HH:MM:SS"
            stmt.setString(2, hasta);
            stmt.setString(3, estado); // 'A' Activo, 'I' Inactivo, 'T' Todos

            try (ResultSet rs = stmt.executeQuery()) {
                ResultSetMetaData metaData = rs.getMetaData();
                int columnCount = metaData.getColumnCount();

                // Agregar las columnas del SP al modelo (si no están)
                if (modelo.getColumnCount() == 0) {
                    for (int i = 1; i <= columnCount; i++) {
                        modelo.addColumn(metaData.getColumnLabel(i));
                    }
                }

                // Agregar las filas
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

    public void cargarFacturas(DefaultTableModel modelo, String desde, String hasta, String user, String pass) {
        modelo.setRowCount(0);
        String sql = "{CALL dbo.sp_reporte_facturas(?, ?)}";

        try (Connection conn = ConexionBD.conectar(user, pass);
             CallableStatement stmt = conn.prepareCall(sql)) {

            stmt.setString(1, desde);
            stmt.setString(2, hasta);

            try (ResultSet rs = stmt.executeQuery()) {
                ResultSetMetaData metaData = rs.getMetaData();
                int columnCount = metaData.getColumnCount();

                if (modelo.getColumnCount() == 0) {
                    for (int i = 1; i <= columnCount; i++) {
                        modelo.addColumn(metaData.getColumnLabel(i));
                    }
                }

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

    public void cargarPistasAuditoria(DefaultTableModel modelo, String desde, String hasta, String usuarioFiltro, String user, String pass) {
        modelo.setRowCount(0);
        String sql = "{CALL dbo.sp_reporte_pistas_auditoria(?, ?, ?)}";

        try (Connection conn = ConexionBD.conectar(user, pass);
             CallableStatement stmt = conn.prepareCall(sql)) {

            stmt.setString(1, desde);
            stmt.setString(2, hasta);
            stmt.setString(3, usuarioFiltro); // Usuario específico o 'TODOS'

            try (ResultSet rs = stmt.executeQuery()) {
                ResultSetMetaData metaData = rs.getMetaData();
                int columnCount = metaData.getColumnCount();

                if (modelo.getColumnCount() == 0) {
                    for (int i = 1; i <= columnCount; i++) {
                        modelo.addColumn(metaData.getColumnLabel(i));
                    }
                }

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
}