package Instituto2;

import java.sql.*;

public class Instituto {
    private static final String URL = "jdbc:mysql://localhost:3306/instituto?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "root";

    public static void main(String[] args) {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("DROP TABLE IF EXISTS notasfinales");
            stmt.executeUpdate("CREATE TABLE notasfinales AS SELECT Mat, Cod, (Nota1 + Nota2) / 2.0 AS NotaMedia FROM notas");
            System.out.println("Tabla notasfinales creada y poblada correctamente");
            String query = "SELECT a.APEL_NOM AS NombreAlumno, a.POBLACION AS Localidad, asig.NOMBRE AS NombreAsignatura, n.NOTA1 AS Nota1, n.NOTA2 AS Nota2, nf.NotaMedia " +
                    "FROM ALUMNOS a " +
                    "JOIN NOTAS n ON a.MAT = n.Mat " +
                    "JOIN ASIGNATURAS asig ON n.COD = asig.COD " +
                    "JOIN notasfinales nf ON n.Mat = nf.Mat AND n.COD = nf.COD";
            try (ResultSet rs = stmt.executeQuery(query)) {
                System.out.printf("%-20s | %-15s | %-20s | %-8s | %-8s | %-10s%n", "NombreAlumno", "Localidad", "Asignatura", "Nota 1", "Nota 2", "NotaMedia");
                System.out.println("------------------------------------------------------------------------------------------------------------------");
                while (rs.next()) {
                    System.out.printf("%-20s | %-15s | %-20s | %8.2f | %8.2f | %10.2f%n",
                            rs.getString("NombreAlumno"),
                            rs.getString("Localidad"),
                            rs.getString("NombreAsignatura"),
                            rs.getDouble("Nota1"),
                            rs.getDouble("Nota2"),
                            rs.getDouble("NotaMedia"));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error de base de datos: " + e.getMessage());
        }
    }
}