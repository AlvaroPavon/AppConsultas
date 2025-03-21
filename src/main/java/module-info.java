module com.azrael.consultasbd.appconsultas {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.apache.logging.log4j;
    requires java.sql;
    requires org.apache.poi.poi;
    requires org.apache.poi.ooxml;


    opens com.azrael.consultasbd.appconsultas to javafx.fxml;
    exports com.azrael.consultasbd.appconsultas;
}