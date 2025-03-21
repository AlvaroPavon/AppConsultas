package com.azrael.consultasbd.appconsultas;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.Optional;
import java.util.Scanner;

public class Consulta {

    private static final Logger logger = LogManager.getLogger(Consulta.class);

    public Connection obtenerConexion(String host, String port, String database, String usuario, String password) {
        Connection conn = null;
        String urlDatabase = "jdbc:postgresql://" + host + ":" + port + "/" + database;
        try {
            // Cargar explícitamente el driver de PostgreSQL
            Class.forName("org.postgresql.Driver");
            conn = DriverManager.getConnection(urlDatabase, usuario, password);
            logger.info("La conexión se realizó sin problemas! =)");
        } catch (SQLException e) {
            logger.error("Error de conexión: " + e.getMessage());
            System.out.println("Error de conexión: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            logger.error("Driver de PostgreSQL no encontrado: " + e.getMessage());
            System.out.println("Driver de PostgreSQL no encontrado: " + e.getMessage());
        }
        return conn;
    }

    public void realizaConsulta(Connection conn, String consulta, String nombreArchivo) {
        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery(consulta);

            // Crear el archivo Excel
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Resultados");

            // Crear la fila de encabezados
            Row headerRow = sheet.createRow(0);
            int columnCount = rs.getMetaData().getColumnCount();
            for (int i = 1; i <= columnCount; i++) {
                Cell cell = headerRow.createCell(i - 1);
                cell.setCellValue(rs.getMetaData().getColumnName(i));
                cell.setCellStyle(createHeaderCellStyle(workbook));
            }

            // Llenar el archivo Excel con los datos de la consulta
            int rowNum = 1;
            while (rs.next()) {
                Row row = sheet.createRow(rowNum++);
                for (int i = 1; i <= columnCount; i++) {
                    Cell cell = row.createCell(i - 1);
                    switch (rs.getMetaData().getColumnType(i)) {
                        case java.sql.Types.INTEGER:
                            cell.setCellValue(rs.getInt(i));
                            break;
                        case java.sql.Types.FLOAT:
                        case java.sql.Types.DOUBLE:
                        case java.sql.Types.REAL:
                            cell.setCellValue(rs.getDouble(i));
                            break;
                        case java.sql.Types.DATE:
                            cell.setCellValue(rs.getDate(i));
                            break;
                        case java.sql.Types.TIMESTAMP:
                            cell.setCellValue(rs.getTimestamp(i).toString());
                            break;
                        default:
                            cell.setCellValue(rs.getString(i));
                            break;
                    }
                }
            }

            // Ajustar el tamaño de las columnas
            for (int i = 0; i < columnCount; i++) {
                sheet.autoSizeColumn(i);
            }

            // Crear la carpeta y guardar el archivo Excel
            Path path = Paths.get("resultados");
            Files.createDirectories(path);
            File file = new File("resultados/" + nombreArchivo + ".xlsx");
            if (file.exists()) {
                Alert alert = new Alert(AlertType.CONFIRMATION);
                alert.setTitle("Archivo existente");
                alert.setHeaderText("El archivo ya existe");
                alert.setContentText("¿Desea reemplazar el archivo existente?");

                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    if (file.delete()) {
                        logger.info("Archivo eliminado: " + file.getPath());
                    } else {
                        logger.error("No se pudo eliminar el archivo: " + file.getPath());
                        System.out.println("No se pudo eliminar el archivo. Operación cancelada.");
                        return;
                    }
                } else {
                    logger.info("Operación cancelada por el usuario.");
                    System.out.println("Operación cancelada por el usuario.");
                    return;
                }
            }
            try (FileOutputStream fileOut = new FileOutputStream(file)) {
                workbook.write(fileOut);
            }
            workbook.close();

            logger.info("Datos guardados en " + file.getPath());

        } catch (SQLException | IOException e) {
            logger.error("Error: No se pudo ejecutar la consulta o guardar los datos.");
            logger.error("Detalles: " + e.getMessage());
        } finally {
            // Cerrar los recursos
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                logger.error("Error al cerrar los recursos: " + e.getMessage());
            }
        }
    }

    private CellStyle createHeaderCellStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        return style;
    }
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        String host;
        while (true) {
            System.out.println("Introduce la puerta de enlace (host):");
            host = scanner.nextLine();
            if (!host.isEmpty()) break;
            System.out.println("El host no puede estar vacío.");
        }

        String port;
        while (true) {
            System.out.println("Introduce el puerto:");
            port = scanner.nextLine();
            if (!port.isEmpty()) break;
            System.out.println("El puerto no puede estar vacío.");
        }

        String database;
        String usuario;
        String password;
        Connection conn = null;
        while (conn == null) {
            System.out.println("Introduce el nombre de la base de datos:");
            database = scanner.nextLine();
            if (database.isEmpty()) {
                System.out.println("El nombre de la base de datos no puede estar vacío.");
                continue;
            }

            System.out.println("Introduce el nombre de usuario:");
            usuario = scanner.nextLine();
            if (usuario.isEmpty()) {
                System.out.println("El nombre de usuario no puede estar vacío.");
                continue;
            }

            System.out.println("Introduce la contraseña:");
            password = scanner.nextLine();
            if (password.isEmpty()) {
                System.out.println("La contraseña no puede estar vacía.");
                continue;
            }

            Consulta consultaFactura = new Consulta();
            conn = consultaFactura.obtenerConexion(host, port, database, usuario, password);
            if (conn == null) {
                System.out.println("No se pudo conectar a la base de datos. Por favor, inténtelo de nuevo.");
            }
        }

        System.out.println("Introduce la fecha de inicio (YYYY-MM-DD):");
        String startDate = scanner.nextLine();

        System.out.println("Introduce la fecha de fin (YYYY-MM-DD):");
        String endDate = scanner.nextLine();

        String consultaSQL = "SELECT " +
                "ai.id AS \"ID FACTURA\", " +
                "ai.date_invoice AS \"FECHA FACTURA\", " +
                "ai.internal_number AS \"CODIGO FACTURA\", " +
                "ai.name AS \"DESCRIPCION\", " +
                "rc.name AS \"COMPAÑÍA\", " +
                "ssp.name AS \"SEDE\", " +
                "rp.nombre_comercial AS \"CLIENTE\", " +
                "rpa.city AS \"CIUDAD\", " +
                "(CASE WHEN rpa.prov_id IS NOT NULL THEN (SELECT UPPER(name) FROM res_country_provincia WHERE id = rpa.prov_id) " +
                "ELSE UPPER(rpa.state_id_2) END) AS \"PROVINCIA\", " +
                "(CASE WHEN rpa.cautonoma_id IS NOT NULL THEN (SELECT UPPER(name) FROM res_country_ca WHERE id = rpa.cautonoma_id) " +
                "ELSE '' END) AS \"COMUNIDAD\", " +
                "c.name AS \"PAÍS\", " +
                "TO_CHAR(ai.date_invoice, 'MM') AS \"MES\", " +
                "TO_CHAR(ai.date_invoice, 'DD') AS \"DÍA\", " +
                "(CASE WHEN ai.type = 'out_invoice' THEN COALESCE(ai.portes,0) + COALESCE(ai.portes_cubiertos,0) " +
                "ELSE -(COALESCE(ai.portes,0) + COALESCE(ai.portes_cubiertos,0)) END) AS \"PORTES CARGADOS POR EL TRANSPORTISTA\", " +
                "(CASE WHEN ai.type = 'out_invoice' THEN COALESCE(ai.portes_cubiertos,0) " +
                "ELSE -(COALESCE(ai.portes_cubiertos,0)) END) AS \"PORTES CUBIERTOS\", " +
                "(CASE WHEN ai.type = 'out_invoice' THEN COALESCE(ai.portes,0) " +
                "ELSE -(COALESCE(ai.portes,0)) END) AS \"PORTES COBRADOS A CLIENTE\" " +
                "FROM account_invoice ai " +
                "INNER JOIN res_partner_address rpa ON rpa.id = ai.address_shipping_id " +
                "INNER JOIN res_country c ON c.id = rpa.pais_id " +
                "LEFT JOIN stock_sede_ps ssp ON ssp.id = ai.sede_id " +
                "LEFT JOIN res_company rc ON rc.id = ai.company_id " +
                "LEFT JOIN res_partner rp ON rp.id = ai.partner_id " +
                "WHERE ai.state NOT IN ('draft','cancel') " +
                "AND ai.type IN ('out_invoice','out_refund') " +
                "AND ai.carrier_id IS NOT NULL " +
                "AND ai.date_invoice >= '" + startDate + "' " +
                "AND ai.date_invoice <= '" + endDate + "' " +
                "GROUP BY ai.id, ai.company_id, ai.date_invoice, TO_CHAR(ai.date_invoice, 'YYYY'), " +
                "TO_CHAR(ai.date_invoice, 'MM'), TO_CHAR(ai.date_invoice, 'YYYY-MM-DD'), ai.carrier_id, ai.partner_id, " +
                "ai.name, ai.obsolescencia, ai.type, c.name, rpa.state_id_2, " +
                "COALESCE(ai.portes,0) + COALESCE(ai.portes_cubiertos,0), COALESCE(ai.portes_cubiertos,0), " +
                "COALESCE(ai.portes,0), rc.name, ssp.name, rpa.prov_id, rpa.cautonoma_id, rp.nombre_comercial, rpa.city";

        String nombreArchivo;
        while (true) {
            System.out.println("Introduce el nombre del archivo (sin extensión):");
            nombreArchivo = scanner.nextLine();
            if (!nombreArchivo.isEmpty()) break;
            System.out.println("El nombre del archivo no puede estar vacío.");
        }

        Consulta consultaFactura = new Consulta();
        consultaFactura.realizaConsulta(conn, consultaSQL, nombreArchivo);
    }
}