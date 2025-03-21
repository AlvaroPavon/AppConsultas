package com.azrael.consultasbd.appconsultas;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class ModificarConsultaController {

    @FXML
    private TextArea consultaInputGrande;
    @FXML
    private Button guardarCambiosButton;

    private ConsultaController consultaController;

    public void setConsultaController(ConsultaController consultaController) {
        this.consultaController = consultaController;
        consultaInputGrande.setText(consultaController.getConsultaInput().getText());
    }

    @FXML
    private void initialize() {
        guardarCambiosButton.setOnAction(e -> guardarCambios());
    }

    private void guardarCambios() {
        consultaController.getConsultaInput().setText(consultaInputGrande.getText());
        Stage stage = (Stage) guardarCambiosButton.getScene().getWindow();
        stage.close();
    }
}