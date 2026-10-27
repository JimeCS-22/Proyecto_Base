package dao;

import model.Cliente;
import org.example.ConexionBD;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAO {

    // 1. Insertar Cliente (Adaptado a los 13 parámetros del formulario y al cifrado de BD)
    public boolean insertar(String nomCliente, int codTipoIdentificacion, String numIdentificacion,
                            String desProvincia, String desCanton, String desDistrito, String desBarrio,
                            String desProfesion, String desActividadEconomica, String desCorreo,
                            String numTelefono, String userConexion, String passConexion) {

        // Usamos CONVERT para que el dato binario sea compatible con los reportes
        String sql = "INSERT INTO dbo.Clientes (" +
                "nom_cliente, cod_tipo_identificacion, num_identificacion, " +
                "des_provincia, des_canton, des_distrito, des_barrio, " +
                "des_profesion, des_actividad_economica, des_correo, num_telefono, ind_estado" +
                ") VALUES (?, ?, CONVERT(VARBINARY(256), ?), ?, ?, ?, ?, ?, ?, CONVERT(VARBINARY(256), ?), CONVERT(VARBINARY(256), ?), 'A')";

        try (Connection conn = ConexionBD.conectar(userConexion, passConexion);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, nomCliente);
            pstmt.setInt(2, codTipoIdentificacion);
            pstmt.setString(3, numIdentificacion);
            pstmt.setString(4, desProvincia);
            pstmt.setString(5, desCanton);
            pstmt.setString(6, desDistrito);
            pstmt.setString(7, desBarrio);
            pstmt.setString(8, desProfesion);
            pstmt.setString(9, desActividadEconomica);
            pstmt.setString(10, desCorreo);
            pstmt.setString(11, numTelefono);

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
                // (Para leer los datos cifrados se necesitaría un SP o desencriptar con una llave)
                c.setCedula("Cifrado");
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
        String sql = "UPDATE dbo.Clientes SET nom_cliente = ?, ind_estado = ? WHERE cod_cliente = ?";

        try (Connection conn = ConexionBD.conectar(userConexion, passConexion);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, cliente.getNombre());
            pstmt.setString(2, cliente.isActivo() ? "A" : "I");
            pstmt.setInt(3, cliente.getIdCliente());

            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 4. Desactivar / Eliminar Lógico de Cliente
    public boolean eliminar(int idCliente, String userConexion, String passConexion) {
        String sql = "UPDATE dbo.Clientes SET ind_estado = 'I' WHERE cod_cliente = ?";

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