package dao;

import model.Producto;
import org.example.ConexionBD;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductoDAO {

    // 1. Insertar Producto
    public boolean insertar(Producto producto, String userConexion, String passConexion) {
        // Nota: Tu tabla real requiere cod_unidad_medida y cod_tipo_impuesto (que son llaves foráneas).
        // Aquí uso el 1 por defecto (Unidad / IVA general) para que no falle la inserción.
        String sql = "INSERT INTO dbo.Productos_Servicios (cod_cabys, des_producto, cod_unidad_medida, mon_precio, cod_tipo_impuesto, ind_estado) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConexionBD.conectar(userConexion, passConexion);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, producto.getCodigo());       // cod_cabys
            pstmt.setString(2, producto.getNombre());       // des_producto
            pstmt.setInt(3, 1);                             // cod_unidad_medida = 1 (Unidad) por defecto
            pstmt.setBigDecimal(4, producto.getPrecioUnitario()); // mon_precio
            pstmt.setInt(5, 1);                             // cod_tipo_impuesto = 1 (IVA General) por defecto
            pstmt.setString(6, producto.isActivo() ? "A" : "I");  // ind_estado

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