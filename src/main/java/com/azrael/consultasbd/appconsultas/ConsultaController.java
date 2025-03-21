package com.azrael.consultasbd.appconsultas;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.util.Optional;

public class ConsultaController {

    @FXML
    private TextField hostInput;
    @FXML
    private TextField portInput;
    @FXML
    private TextField databaseInput;
    @FXML
    private TextField usuarioInput;
    @FXML
    private PasswordField passwordInput;
    @FXML
    private TextArea consultaInput;
    @FXML
    private TextField archivoInput;
    @FXML
    private Button ejecutarButton;
    @FXML
    private Button guardarButton;
    @FXML
    private Button seleccionarButton;
    @FXML
    private ComboBox<String> configuracionComboBox;
    @FXML
    private Button verModificarButton;
    @FXML
    private Button realizarConsultaButton; // Nuevo botón para abrir la vista de consulta

    private Configuracion configuracion;

    public void setConfiguracion(Configuracion configuracion) {
        this.configuracion = configuracion;
        cargarConfiguracion();
    }

    @FXML
    private void initialize() {
        configuracion = new Configuracion(); // Inicializar configuracion aquí
        cargarConfiguracion();
        ejecutarButton.setOnAction(e -> ejecutarConsulta());
        guardarButton.setOnAction(e -> guardarConfiguracion());
        seleccionarButton.setOnAction(e -> cargarConfiguracionSeleccionada());
        verModificarButton.setOnAction(e -> abrirVentanaModificarConsulta());
    }

    private void cargarConfiguracion() {
        configuracionComboBox.getItems().clear();
        configuracionComboBox.getItems().addAll(configuracion.getConfiguraciones());
    }

    private void cargarConfiguracionSeleccionada() {
        String nombreConfiguracion = configuracionComboBox.getValue();
        if (nombreConfiguracion != null) {
            hostInput.setText(configuracion.getConfiguracion(nombreConfiguracion, "host"));
            portInput.setText(configuracion.getConfiguracion(nombreConfiguracion, "port"));
            databaseInput.setText(configuracion.getConfiguracion(nombreConfiguracion, "database"));
            usuarioInput.setText(configuracion.getConfiguracion(nombreConfiguracion, "usuario"));
            passwordInput.setText(configuracion.getConfiguracion(nombreConfiguracion, "password"));
        }
    }

    private void ejecutarConsulta() {
        String host = hostInput.getText();
        String port = portInput.getText();
        String database = databaseInput.getText();
        String usuario = usuarioInput.getText();
        String password = passwordInput.getText();
        String consulta = consultaInput.getText();
        String nombreArchivo = archivoInput.getText();

        File archivo = new File(nombreArchivo);
        if (archivo.exists()) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Archivo existente");
            alert.setHeaderText("El archivo ya existe");
            alert.setContentText("¿Desea reemplazar el archivo existente?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                // El usuario desea reemplazar el archivo
                Consulta consultaFactura = new Consulta();
                Connection conn = consultaFactura.obtenerConexion(host, port, database, usuario, password);
                if (conn != null) {
                    consultaFactura.realizaConsulta(conn, consulta, nombreArchivo);
                } else {
                    System.out.println("No se pudo conectar a la base de datos.");
                }
            } else {
                // El usuario no desea reemplazar el archivo
                System.out.println("Operación cancelada por el usuario.");
            }
        } else {
            // El archivo no existe, proceder con la consulta
            Consulta consultaFactura = new Consulta();
            Connection conn = consultaFactura.obtenerConexion(host, port, database, usuario, password);
            if (conn != null) {
                consultaFactura.realizaConsulta(conn, consulta, nombreArchivo);
            } else {
                System.out.println("No se pudo conectar a la base de datos.");
            }
        }
    }

    private void guardarConfiguracion() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Guardar Configuración");
        dialog.setHeaderText("Guardar Configuración");
        dialog.setContentText("Por favor, introduce el nombre de la configuración:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(nombreConfiguracion -> {
            String host = hostInput.getText();
            String port = portInput.getText();
            String database = databaseInput.getText();
            String usuario = usuarioInput.getText();
            String password = passwordInput.getText();

            configuracion.guardarConfiguracion(nombreConfiguracion, host, port, database, usuario, password);
            configuracionComboBox.getItems().add(nombreConfiguracion);
        });
    }

    private void abrirVentanaModificarConsulta() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/demo/consulta.fxml"));
            Parent root = loader.load();

            ModificarConsultaController modificarConsultaController = loader.getController();
            modificarConsultaController.setConsultaController(this);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Modificar Consulta SQL");
            stage.setScene(new Scene(root, 600, 400));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public TextField getHostInput() {
        return hostInput;
    }

    public TextField getPortInput() {
        return portInput;
    }

    public TextField getDatabaseInput() {
        return databaseInput;
    }

    public TextField getUsuarioInput() {
        return usuarioInput;
    }

    public PasswordField getPasswordInput() {
        return passwordInput;
    }


    public TextField getArchivoInput() {
        return archivoInput;
    }

    public TextArea getConsultaInput() {
        return consultaInput;
    }
}