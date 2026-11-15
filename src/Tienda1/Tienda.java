package Tienda1;

import java.sql.*;
import java.util.Scanner;

public class Tienda {
    private static final String URL = "jdbc:mysql://localhost:3306/tienda1?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "root";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int opcion = 0;
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            do {
                System.out.println("Menú tienda");
                System.out.println("1.Listar artículos con precio menor al introducido");
                System.out.println("2.Agregar un fabricante");
                System.out.println("3.Modificar precio de Disco Duro 300G");
                System.out.println("4.Eliminar un artículo por nombre");
                System.out.println("5.Salir");
                System.out.print("Elige una opción: ");
                try {
                    opcion = Integer.parseInt(scanner.nextLine());
                } catch (NumberFormatException e) {
                    System.out.println("Introduce un número válido");
                    continue;
                }
                switch (opcion) {
                    case 1:
                        listarArticulosMenoresQue(conn, scanner);
                        break;
                    case 2:
                        agregarFabricante(conn, scanner);
                        break;
                    case 3:
                        modificarPrecioDiscoDuro(conn, scanner);
                        break;
                    case 4:
                        eliminarArticulo(conn, scanner);
                        break;
                    case 5:
                        System.out.println("Saliendo del programa");
                        break;
                    default:
                        System.out.println("Opción no válida. Inténtalo de nuevo");
                }
            } while (opcion != 5);
        } catch (SQLException e) {
            System.out.println("Error de conexión a la base de datos: " + e.getMessage());
        } finally {
            scanner.close();
        }
    }
    private static void listarArticulosMenoresQue(Connection conn, Scanner scanner) {
        System.out.print("Introduce el precio límite: ");
        double limite = Double.parseDouble(scanner.nextLine());
        String sql = "SELECT Nombre, " +
                "Precio FROM Articulos " +
                "WHERE Precio < ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, limite);
            ResultSet rs = pstmt.executeQuery();
            System.out.println("Artículos con precio menor a " + limite + " €");
            boolean encontrados = false;
            while (rs.next()) {
                encontrados = true;
                System.out.println("- " + rs.getString("Nombre") + " | Precio: " + rs.getInt("Precio") + " €");
            }
            if (!encontrados) {
                System.out.println("No se encontraron artículos por debajo de ese precio");
            }
        } catch (SQLException e) {
            System.out.println("Error al listar artículos: " + e.getMessage());
        }
    }
    private static void agregarFabricante(Connection conn, Scanner scanner) {
        System.out.print("Introduce el nombre del nuevo fabricante: ");
        String nombreFabricante = scanner.nextLine();
        String sqlMax = "SELECT MAX(CLFAB) FROM Fabricantes";
        String sqlInsert = "INSERT INTO Fabricantes (CLFAB, Nombre) VALUES (?, ?)";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sqlMax)) {
            int siguienteCodigo = 1;
            if (rs.next()) {
                siguienteCodigo = rs.getInt(1) + 1;
            }
            try (PreparedStatement pstmt = conn.prepareStatement(sqlInsert)) {
                pstmt.setInt(1, siguienteCodigo);
                pstmt.setString(2, nombreFabricante);
                int filas = pstmt.executeUpdate();
                if (filas > 0) {
                    System.out.println("Fabricante '" + nombreFabricante + "' añadido correctamente con el código " + siguienteCodigo);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al agregar el fabricante: " + e.getMessage());
        }
    }
    private static void modificarPrecioDiscoDuro(Connection conn, Scanner scanner) {
        System.out.print("Introduce el porcentaje de reducción: ");
        double porcentaje = Double.parseDouble(scanner.nextLine());
        String sql = "UPDATE Articulos " +
                "SET Precio = Precio - (Precio * ? / 100) " +
                "WHERE Nombre = 'Disco Duro 300Gb'";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, porcentaje);
            int filas = pstmt.executeUpdate();
            if (filas > 0) {
                System.out.println("Precio del Disco Duro 300Gb reducido exitosamente en un " + porcentaje + "%");
            } else {
                System.out.println("No se encontró ningún artículo denominado Disco Duro 300Gb");
            }
        } catch (SQLException e) {
            System.out.println("Error al modificar el precio: " + e.getMessage());
        }
    }
    private static void eliminarArticulo(Connection conn, Scanner scanner) {
        System.out.print("Introduce el nombre exacto del artículo a eliminar: ");
        String nombreArticulo = scanner.nextLine();
        String sql = "DELETE FROM Articulos " +
                "WHERE Nombre = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nombreArticulo);
            int filas = pstmt.executeUpdate();
            if (filas > 0) {
                System.out.println("Artículo " + nombreArticulo + " eliminado correctamente");
            } else {
                System.out.println("No se encontró ningún artículo con ese nombre");
            }
        } catch (SQLException e) {
            System.out.println("Error al eliminar el artículo: " + e.getMessage());
        }
    }
}