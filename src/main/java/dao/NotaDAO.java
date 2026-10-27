package dao;

import org.example.ConexionBD;
import java.sql.*;
import javax.swing.table.DefaultTableModel;

public class NotaDAO {

    // 1. Registrar una Nota de Crédito o Débito
    public boolean insertarNota(int numFactura, String tipo, String motivo, String user, String pass) {
        String sql = "INSERT INTO Notas_Credito_Debito (num_factura, ind_tipo, des_motivo) VALUES (?, ?, ?)";

        try (Connection conn = ConexionBD.conectar(user, pass);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, numFactura);
            pstmt.setString(2, tipo); // 'C' para Crédito, 'D' para Débito
            pstmt.setString(3, motivo);

            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 2. Cargar notas en una JTable para visualización
    public void cargarTablaNotas(DefaultTableModel modeloTabla, String user, String pass) {
        modeloTabla.setRowCount(0);
        String sql = "SELECT cod_nota, fec_nota, num_factura, ind_tipo, des_motivo FROM Notas_Credito_Debito";

        try (Connection conn = ConexionBD.conectar(user, pass);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                modeloTabla.addRow(new Object[]{
                        rs.getInt("cod_nota"),
                        rs.getTimestamp("fec_nota"),
                        rs.getInt("num_factura"),
                        rs.getString("ind_tipo").equals("C") ? "Crédito" : "Débito",
                        rs.getString("des_motivo")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
