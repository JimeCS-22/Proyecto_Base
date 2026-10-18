package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionBD {

    public static Connection conectar(String usuario, String password) throws SQLException {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("No se encontró el Driver JDBC de SQL Server: " + e.getMessage());
        }

        // URL corregida que fuerza el uso de la base de datos Proyecto desde el inicio
        String url = "jdbc:sqlserver://localhost:1433;databaseName=Proyecto;initialCatalog=Proyecto;encrypt=true;trustServerCertificate=true;";

        System.out.println("Intentando conectar a JDBC con usuario: " + usuario);

        return DriverManager.getConnection(url, usuario, password);
    }
}