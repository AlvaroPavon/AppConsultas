package com.azrael.consultasbd.appconsultas;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;

import java.io.*;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;

public class Configuracion {

    private static final String CONFIG_FILE = "config.properties";
    private Properties properties;

    public Configuracion() {
        properties = new Properties();
        cargarConfiguracion();
    }

    public void cargarConfiguracion() {
        File configFile = new File(CONFIG_FILE);
        if (configFile.exists()) {
            try (InputStream input = new FileInputStream(configFile)) {
                properties.load(input);
            } catch (IOException ex) {
                System.out.println("No se pudo cargar la configuración: " + ex.getMessage());
            }
        }
    }

    public void guardarConfiguracion() {
        try (OutputStream output = new FileOutputStream(CONFIG_FILE)) {
            properties.store(output, null);
        } catch (IOException ex) {
            System.out.println("No se pudo guardar la configuración: " + ex.getMessage());
        }
    }

    public String get(String key) {
        return properties.getProperty(key);
    }

    public void set(String key, String value) {
        properties.setProperty(key, value);
    }

    public void guardarConfiguracion(String nombreConfiguracion, String host, String port, String database, String usuario, String password) {
        String configKey = nombreConfiguracion + ".host";
        if (properties.containsKey(configKey)) {
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Configuración existente");
            alert.setHeaderText("La configuración ya existe");
            alert.setContentText("¿Desea reemplazar la configuración existente?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                // El usuario desea reemplazar la configuración
                properties.setProperty(nombreConfiguracion + ".host", host);
                properties.setProperty(nombreConfiguracion + ".port", port);
                properties.setProperty(nombreConfiguracion + ".database", database);
                properties.setProperty(nombreConfiguracion + ".usuario", usuario);
                properties.setProperty(nombreConfiguracion + ".password", password);
                guardarConfiguracion();
            } else {
                // El usuario no desea reemplazar la configuración
                System.out.println("Operación cancelada por el usuario.");
            }
        } else {
            // La configuración no existe, proceder a guardarla
            properties.setProperty(nombreConfiguracion + ".host", host);
            properties.setProperty(nombreConfiguracion + ".port", port);
            properties.setProperty(nombreConfiguracion + ".database", database);
            properties.setProperty(nombreConfiguracion + ".usuario", usuario);
            properties.setProperty(nombreConfiguracion + ".password", password);
            guardarConfiguracion();
        }
    }

    public String[] getConfiguraciones() {
        Set<String> keys = properties.stringPropertyNames();
        return keys.stream()
                .map(key -> key.split("\\.")[0])
                .distinct()
                .toArray(String[]::new);
    }

    public String getConfiguracion(String nombreConfiguracion, String key) {
        return properties.getProperty(nombreConfiguracion + "." + key);
    }
}