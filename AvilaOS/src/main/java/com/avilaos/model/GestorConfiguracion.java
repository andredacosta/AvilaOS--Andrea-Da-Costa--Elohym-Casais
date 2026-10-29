/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 *
 * @author Elohym
 */

public class GestorConfiguracion {
    private final Gson gson;

    public GestorConfiguracion() {
        this.gson = new GsonBuilder().setPrettyPrinting().create();
    }

    public void guardarConfiguracion(ConfiguracionSistema config, String rutaArchivo) throws IOException {
        try (FileWriter writer = new FileWriter(rutaArchivo)) {
            gson.toJson(config, writer);
        }
    }

    public ConfiguracionSistema cargarConfiguracion(String rutaArchivo) throws IOException {
        try (FileReader reader = new FileReader(rutaArchivo)) {
            return gson.fromJson(reader, ConfiguracionSistema.class);
        }
    }
}
