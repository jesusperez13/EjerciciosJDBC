package Deportes3;

import java.sql.*;

public class Deportes {
    private static final String URL = "jdbc:mysql://localhost:3306/deportes?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "root";

    public static void main(String[] args) {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS clasificacion (" +
                    "id_clasifiacion INT AUTO_INCREMENT PRIMARY KEY, " +
                    "id_equipo INT, " +
                    "posicion INT, " +
                    "puntos INT, " +
                    "FOREIGN KEY (id_equipo) REFERENCES equipos(id_equipo))");
            try {
                stmt.executeUpdate("INSERT INTO clasificacion (id_clasifiacion, id_equipo, posicion, puntos) VALUES " +
                        "(1, 1, 1, 20), " +
                        "(2, 2, 2, 15), " +
                        "(3, 3, 3, 12), " +
                        "(4, 4, 4, 10), " +
                        "(5, 5, 5, 8)");
            } catch (SQLException ignored) {}
            System.out.println("Lista de jugadores del Equipo C");
            try (ResultSet rs1 = stmt.executeQuery("SELECT j.* " +
                    "FROM jugadores j " +
                    "JOIN equipos e ON j.id_equipo = e.id_equipo " +
                    "WHERE e.nombre_equipo = 'Equipo C'")) {
                while (rs1.next()) {
                    System.out.println("Jugador: " + rs1.getString("nombre_jugador"));                }
            }
            System.out.println("Resultados de los partidos jugados");
            try (ResultSet rs2 = stmt.executeQuery("SELECT * FROM partidos")) {
                while (rs2.next()) {
                    System.out.println("Partido ID: " + rs2.getInt(1));
                }
            }
            System.out.println("Clasificación actual de los equipos");
            try (ResultSet rs3 = stmt.executeQuery("SELECT * FROM clasificacion")) {
                while (rs3.next()) {
                    System.out.println("Equipo ID: " + rs3.getInt("id_equipo") + " | Posición: " + rs3.getInt("posicion") + " | Puntos: " + rs3.getInt("puntos"));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error de base de datos: " + e.getMessage());
        }
    }
}