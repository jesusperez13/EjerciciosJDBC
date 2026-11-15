package Tienda1;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CreacionTabla {
    private static final String URL = "jdbc:mysql://localhost:3306/tienda1?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "root";

    public static void main(String[] args) {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("DROP TABLE IF EXISTS ArtFab");
            String sqlCreacion = "CREATE TABLE ArtFab AS " +
                    "SELECT art.Nombre AS nombre_articulo, " +
                    "       fab.Nombre AS nombre_fabricante, " +
                    "       art.Precio, " +
                    "       CASE " +
                    "           WHEN art.Precio < 200 THEN art.Precio * 0.10 " +
                    "           WHEN art.Precio < 500 THEN art.Precio * 0.08 " +
                    "           WHEN art.Precio < 700 THEN art.Precio * 0.06 " +
                    "           ELSE 0 " +
                    "       END AS iva " +
                    "FROM Articulos art " +
                    "JOIN Fabricantes fab ON art.CLFAB = fab.CLFAB";
            stmt.executeUpdate(sqlCreacion);
            System.out.println("Tabla ArtFab creada con éxito en la base de datos tienda1");
            ResultSet rs = stmt.executeQuery("SELECT * FROM ArtFab");
            System.out.println("Listado de la nueva tabla ArtFab");
            System.out.printf("%-35s | %-20s | %-10s | %-10s\n", "Artículo", "Fabricante", "Precio (€)", "IVA (€)");
            System.out.println("------------------------------------------------------------------------------------------");
            while (rs.next()) {
                String art = rs.getString("nombre_articulo");
                String fab = rs.getString("nombre_fabricante");
                int precio = rs.getInt("Precio");
                double iva = rs.getDouble("iva");
                System.out.printf("%-35s | %-26s | %-10d | %-10.2f\n", art, fab, precio, iva);
            }
        } catch (SQLException e) {
            System.out.println("Error de base de datos: " + e.getMessage());
        }
    }
}