package dao;

import model.Producto;
import org.example.ConexionBD;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductoDAO {

    // 1. Insertar Producto
    // 1. Insertar Producto (Adaptado a los parámetros del formulario)
    public boolean insertar(String codCabys, String desProducto, String descripcion,
                            BigDecimal precio, int codUnidadMedida, int codTipoImpuesto,
                            String userConexion, String passConexion) {

        // Nota: Tu tabla no tiene columna 'descripcion'. Si quieres guardarla, tendrías que agregarla en SQL.
        String sql = "INSERT INTO dbo.Productos_Servicios (cod_cabys, des_producto, mon_precio, cod_unidad_medida, cod_tipo_impuesto, ind_estado) VALUES (?, ?, ?, ?, ?, 'A')";

        try (Connection conn = ConexionBD.conectar(userConexion, passConexion);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, codCabys);
            pstmt.setString(2, desProducto);
            pstmt.setBigDecimal(3, precio);
            pstmt.setInt(4, codUnidadMedida);
            pstmt.setInt(5, codTipoImpuesto);

            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 2. Obtener todos los Productos
    public List<Producto> listar(String userConexion, String passConexion) {
        List<Producto> lista = new ArrayList<>();
        String sql = "SELECT cod_cabys, des_producto, mon_precio, ind_estado FROM dbo.Productos_Servicios";

        try (Connection conn = ConexionBD.conectar(userConexion, passConexion);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Producto p = new Producto(
                        rs.getString("cod_cabys"),
                        rs.getString("des_producto"),
                        "Sin descripción",                  // No existe en la BD
                        rs.getBigDecimal("mon_precio"),
                        0,                                  // No existe stock en la BD
                        "A".equals(rs.getString("ind_estado"))
                );
                lista.add(p);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    // 3. Actualizar Producto
    public boolean actualizar(Producto producto, String userConexion, String passConexion) {
        // Se actualiza usando el cod_cabys (que es el código) en el WHERE
        String sql = "UPDATE dbo.Productos_Servicios SET des_producto = ?, mon_precio = ?, ind_estado = ? WHERE cod_cabys = ?";

        try (Connection conn = ConexionBD.conectar(userConexion, passConexion);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, producto.getNombre());
            pstmt.setBigDecimal(2, producto.getPrecioUnitario());
            pstmt.setString(3, producto.isActivo() ? "A" : "I");
            pstmt.setString(4, producto.getCodigo()); // El WHERE usa el código CABYS

            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 4. Desactivar / Eliminar Lógico de Producto
    public boolean eliminar(String codigoProducto, String userConexion, String passConexion) {
        // Recibe un String en vez de int, porque el ID es el cod_cabys
        String sql = "UPDATE dbo.Productos_Servicios SET ind_estado = 'I' WHERE cod_cabys = ?";

        try (Connection conn = ConexionBD.conectar(userConexion, passConexion);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, codigoProducto);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}