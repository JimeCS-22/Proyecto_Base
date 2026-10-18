package dao;

import model.Cliente;
import org.example.ConexionBD;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAO {

    // 1. Insertar Cliente
    public boolean insertar(Cliente cliente, String userConexion, String passConexion) {
        String sql = "INSERT INTO Cliente (cedula, nombre, primer_apellido, segundo_apellido, telefono, correo, direccion, activo) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConexionBD.conectar(userConexion, passConexion);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, cliente.getCedula());
            pstmt.setString(2, cliente.getNombre());
            pstmt.setString(3, cliente.getPrimerApellido());
            pstmt.setString(4, cliente.getSegundoApellido());
            pstmt.setString(5, cliente.getTelefono());
            pstmt.setString(6, cliente.getCorreo());
            pstmt.setString(7, cliente.getDireccion());
            pstmt.setBoolean(8, cliente.isActivo());

            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 2. Obtener todos los Clientes
    public List<Cliente> listar(String userConexion, String passConexion) {
        List<Cliente> lista = new ArrayList<>();
        String sql = "SELECT cod_cliente, nom_cliente, des_provincia, des_canton, ind_estado FROM dbo.Clientes";

        try (Connection conn = ConexionBD.conectar(userConexion, passConexion);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Cliente c = new Cliente();
                c.setIdCliente(rs.getInt("cod_cliente"));
                c.setNombre(rs.getString("nom_cliente"));

                // Rellenamos con texto vacío o valores por defecto para que no se vea "null"
                c.setCedula("Ver en reporte");
                c.setPrimerApellido("");
                c.setSegundoApellido("");
                c.setTelefono("***");
                c.setCorreo("***");
                c.setDireccion(rs.getString("des_provincia") + ", " + rs.getString("des_canton"));

                c.setActivo("A".equals(rs.getString("ind_estado")));
                lista.add(c);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    // 3. Actualizar Cliente
    public boolean actualizar(Cliente cliente, String userConexion, String passConexion) {
        String sql = "UPDATE Cliente SET cedula = ?, nombre = ?, primer_apellido = ?, segundo_apellido = ?, telefono = ?, correo = ?, direccion = ?, activo = ? WHERE id_cliente = ?";

        try (Connection conn = ConexionBD.conectar(userConexion, passConexion);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, cliente.getCedula());
            pstmt.setString(2, cliente.getNombre());
            pstmt.setString(3, cliente.getPrimerApellido());
            pstmt.setString(4, cliente.getSegundoApellido());
            pstmt.setString(5, cliente.getTelefono());
            pstmt.setString(6, cliente.getCorreo());
            pstmt.setString(7, cliente.getDireccion());
            pstmt.setBoolean(8, cliente.isActivo());
            pstmt.setInt(9, cliente.getIdCliente());

            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 4. Desactivar / Eliminar Lógico de Cliente
    public boolean eliminar(int idCliente, String userConexion, String passConexion) {
        String sql = "UPDATE Cliente SET activo = 0 WHERE id_cliente = ?";

        try (Connection conn = ConexionBD.conectar(userConexion, passConexion);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idCliente);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}