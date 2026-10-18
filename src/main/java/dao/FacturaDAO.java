package dao;

import model.DetalleFactura;
import model.Factura;
import org.example.ConexionBD;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FacturaDAO {

    // 1. Guardar/Emitir Factura con sus detalles (Transacción Atómica)
    public boolean emitirFactura(Factura factura, String userConexion, String passConexion) {
        // Ajustado a los nombres reales de tu tabla Facturas
        String sqlFactura = "INSERT INTO dbo.Facturas (cod_cliente, fec_factura, ind_condicion_venta, cod_medio_pago, cod_moneda, mon_subtotal, mon_total_impuestos, mon_total) VALUES (?, ?, 'C', 1, 'CRC', ?, ?, ?)";

        // Ajustado a los nombres reales de tu tabla Facturas_Detalle
        String sqlDetalle = "INSERT INTO dbo.Facturas_Detalle (num_factura, num_linea, cod_cabys, can_cantidad, mon_linea) VALUES (?, ?, ?, ?, ?)";

        Connection conn = null;
        try {
            conn = ConexionBD.conectar(userConexion, passConexion);
            conn.setAutoCommit(false);

            int numFacturaGenerado = 0;

            // Insertar Cabecera
            try (PreparedStatement pstmtFactura = conn.prepareStatement(sqlFactura, Statement.RETURN_GENERATED_KEYS)) {
                pstmtFactura.setInt(1, factura.getIdCliente());
                pstmtFactura.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
                pstmtFactura.setBigDecimal(3, factura.getSubtotal());
                pstmtFactura.setBigDecimal(4, factura.getImpuesto());
                pstmtFactura.setBigDecimal(5, factura.getTotal());

                int affectedRows = pstmtFactura.executeUpdate();
                if (affectedRows == 0) {
                    conn.rollback();
                    return false;
                }

                try (ResultSet generatedKeys = pstmtFactura.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        numFacturaGenerado = generatedKeys.getInt(1);
                        factura.setIdFactura(numFacturaGenerado);
                    } else {
                        conn.rollback();
                        return false;
                    }
                }
            }

            // Insertar Detalles
            try (PreparedStatement pstmtDetalle = conn.prepareStatement(sqlDetalle)) {
                int numLinea = 1;
                for (DetalleFactura detalle : factura.getDetalles()) {
                    pstmtDetalle.setInt(1, numFacturaGenerado);
                    pstmtDetalle.setInt(2, numLinea++); // num_linea es parte de la llave primaria
                    pstmtDetalle.setString(3, detalle.getCodCabys()); // Usamos el String CABYS
                    pstmtDetalle.setInt(4, detalle.getCantidad());
                    pstmtDetalle.setBigDecimal(5, detalle.getSubtotalLinea());
                    pstmtDetalle.addBatch();
                }
                pstmtDetalle.executeBatch();
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
    }

    // 2. Consultar/Listar Facturas
    public List<Factura> listarFacturas(String userConexion, String passConexion) {
        List<Factura> lista = new ArrayList<>();
        String sql = "SELECT id_factura, id_cliente, fecha_emision, subtotal, impuesto, total, estado FROM Factura";

        try (Connection conn = ConexionBD.conectar(userConexion, passConexion);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Factura f = new Factura();
                f.setIdFactura(rs.getInt("id_factura"));
                f.setIdCliente(rs.getInt("id_cliente"));
                f.setFechaEmision(rs.getTimestamp("fecha_emision"));
                f.setSubtotal(rs.getBigDecimal("subtotal"));
                f.setImpuesto(rs.getBigDecimal("impuesto"));
                f.setTotal(rs.getBigDecimal("total"));
                f.setEstado(rs.getString("estado"));
                lista.add(f);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    // 3. Anular Factura
    public boolean anularFactura(int idFactura, String userConexion, String passConexion) {
        String sql = "UPDATE Factura SET estado = 'Anulada' WHERE id_factura = ?";

        try (Connection conn = ConexionBD.conectar(userConexion, passConexion);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idFactura);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
