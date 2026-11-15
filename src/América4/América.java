package América4;

import java.sql.*;
import java.util.Scanner;

public class América {
    private static final String URL = "jdbc:mysql://localhost:3306/America?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "root";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int opcion = 0;
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement stmt = conn.createStatement()) {
            try {
                stmt.executeUpdate("DROP TABLE IF EXISTS PersonasPaises");
                stmt.executeUpdate("CREATE TABLE PersonasPaises (" +
                        "Id INT AUTO_INCREMENT PRIMARY KEY, " +
                        "Nombre VARCHAR(15), " +
                        "Apellido VARCHAR(15), " +
                        "Edad INT, " +
                        "NombrePais VARCHAR(15), " +
                        "Tamano VARCHAR(15))");
                stmt.executeUpdate("INSERT INTO PersonasPaises (Nombre, Apellido, Edad, NombrePais, Tamano) " +
                        "SELECT p.Nombre, p.Apellido, p.Edad, pa.Nombre, pa.Tamanio " +
                        "FROM Persona p JOIN Pais pa ON p.Pais = pa.Id");
            } catch (SQLException e) {
                System.out.println("Aviso al preparar la tabla: " + e.getMessage());
            }
            try {
                stmt.executeUpdate("UPDATE PersonasPaises SET Edad = Edad + 1 " +
                        "WHERE NombrePais = 'Costa Rica'");
            } catch (SQLException ignored) {}
            do {
                System.out.println("Menú América");
                System.out.println("1.Consultar edad por nombre");
                System.out.println("2.Listar personas por edad");
                System.out.println("3.Listar personas por orden alfabético");
                System.out.println("4.Insertar persona");
                System.out.println("5.Salir");
                System.out.print("Elige una opción: ");
                String input = scanner.nextLine().trim();
                if (input.equals("5")) {
                    opcion = 5;
                    continue;
                }
                switch (input) {
                    case "1":
                        System.out.print("Introduce el nombre: ");
                        String nombreBusq = scanner.nextLine();
                        try (PreparedStatement pstmt = conn.prepareStatement("SELECT Edad " +
                                "FROM PersonasPaises " +
                                "WHERE Nombre = ?")) {
                            pstmt.setString(1, nombreBusq);
                            try (ResultSet rs = pstmt.executeQuery()) {
                                while (rs.next()) System.out.println("Edad: " + rs.getInt("Edad"));
                            }
                        }
                        break;
                    case "2":
                        System.out.print("Introduce la edad: ");
                        int edadBusq = Integer.parseInt(scanner.nextLine());
                        try (PreparedStatement pstmt = conn.prepareStatement("SELECT * " +
                                "FROM PersonasPaises " +
                                "WHERE Edad = ?")) {
                            pstmt.setInt(1, edadBusq);
                            try (ResultSet rs = pstmt.executeQuery()) {
                                while (rs.next()) System.out.println("- " + rs.getString("Nombre") + " " + rs.getString("Apellido"));
                            }
                        }
                        break;
                    case "3":
                        try (ResultSet rs = stmt.executeQuery("SELECT * " +
                                "FROM PersonasPaises " +
                                "ORDER BY Nombre ASC")) {
                            while (rs.next()) {
                                System.out.println(rs.getString("Nombre") + " " + rs.getString("Apellido") + " (" + rs.getString("NombrePais") + ")");
                            }
                        }
                        break;
                    case "4":
                        System.out.print("Nombre: ");
                        String nom = scanner.nextLine();
                        System.out.print("Apellido: ");
                        String ap = scanner.nextLine();
                        System.out.print("Edad: ");
                        int ed = Integer.parseInt(scanner.nextLine());
                        System.out.print("Nombre país: ");
                        String pais = scanner.nextLine();
                        try (PreparedStatement pstmt = conn.prepareStatement("INSERT INTO PersonasPaises (Nombre, Apellido, Edad, NombrePais) VALUES (?, ?, ?, ?)")) {
                            pstmt.setString(1, nom);
                            pstmt.setString(2, ap);
                            pstmt.setInt(3, ed);
                            pstmt.setString(4, pais);
                            pstmt.executeUpdate();
                            System.out.println("Persona insertada correctamente");
                        }
                        break;
                    default:
                        System.out.println("Opción no válida");
                }
            } while (opcion != 5);
        } catch (SQLException e) {
            System.out.println("Error de base de datos: " + e.getMessage());
        } finally {
            scanner.close();
        }
    }
}