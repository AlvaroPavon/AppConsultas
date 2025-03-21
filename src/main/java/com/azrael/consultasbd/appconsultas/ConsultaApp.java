package com.azrael.consultasbd.appconsultas;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ConsultaApp extends Application {

    private Configuracion configuracion;

    @Override
    public void start(Stage primaryStage) throws Exception {
        configuracion = new Configuracion();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("view.fxml"));
        Parent root = loader.load();

        ConsultaController controller = loader.getController();
        controller.setConfiguracion(configuracion);

        primaryStage.setTitle("Consulta Factura");
        primaryStage.setScene(new Scene(root, 400, 500)); // Ajustar tamaño de la ventana
        primaryStage.show();
    }

    public void mostrarVistaConsulta() throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("consulta_view.fxml"));
        Parent root = loader.load();

        Stage stage = new Stage();
        stage.setTitle("Realizar Consulta");
        stage.setScene(new Scene(root, 400, 300)); // Ajustar tamaño de la ventana
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}