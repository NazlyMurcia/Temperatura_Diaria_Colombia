import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import java.nio.charset.StandardCharsets;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import java.util.*;
import java.util.stream.Collectors;


    public class AnalizadorTemperatura {
    
        // Constante para el formato de fecha utilizado en el archivo CSV
        private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    
        public static List<RegistroTemperatura> leerDatosDesdeCSV(String rutaArchivo) throws IOException {
        List<RegistroTemperatura> registros = new ArrayList<>();
        //try-with-resources para asegurar que el BufferedReader se cierre automáticamente
        try (BufferedReader br = new BufferedReader(new FileReader("C:\\Users\\Antivirus\\Desktop\\EJERCICIOS LABORATORIO\\PARCIAL 2\\Temperaturas.csv", StandardCharsets.UTF_8))) {
            String linea;
            br.readLine(); // Leer y descartar la primera línea de encabezado
            while ((linea = br.readLine()) != null) {
                String[] partes = linea.split(",");
                if (partes.length == 3) {
                    try {
                        // Convertir las partes de la línea a los tipos de datos correctos
                        LocalDate fecha = LocalDate.parse(partes[1].trim(), DATE_FORMATTER);
                        String ciudad = partes[0].trim();
                        double temperatura = Double.parseDouble(partes[2].trim());
                        registros.add(new RegistroTemperatura(ciudad, fecha, temperatura));
                    } catch (DateTimeParseException | NumberFormatException e) {
                        // excepciones
                        System.err.println("Error al procesar la línea: " + linea + " - " + e.getMessage());
                    }
                } else {
                    System.err.println("Línea inválida: " + linea);
                }
            }
        }
        return registros;
    }

    public static List<RegistroTemperatura> filtrarRegistros(List<RegistroTemperatura> registros, LocalDate desde, LocalDate hasta) {
        return registros.stream()
                .filter(registro -> !registro.getFecha().isBefore(desde) && !registro.getFecha().isAfter(hasta))
                .collect(Collectors.toList());
    }

    public static List<RegistroTemperatura> filtrarRegistrosPorFecha(List<RegistroTemperatura> registros, LocalDate fecha) {
        return registros.stream()
                .filter(registro -> registro.getFecha().equals(fecha))
                .collect(Collectors.toList());
    }

    public static Map<String, Double> calcularPromedioPorCiudad(List<RegistroTemperatura> registros) {
        return registros.stream()
                .collect(Collectors.groupingBy(RegistroTemperatura::getCiudad, Collectors.averagingDouble(RegistroTemperatura::getTemperatura)));
    }

    public static RegistroTemperatura obtenerRegistroMasCaluroso(List<RegistroTemperatura> registros) {
        if (registros == null || registros.isEmpty()) {
            return null;
        }
        return registros.stream()
                .max(Comparator.comparingDouble(RegistroTemperatura::getTemperatura))
                .orElse(null);
    }

    public static RegistroTemperatura obtenerRegistroMasFrio(List<RegistroTemperatura> registros) {
        if (registros == null || registros.isEmpty()) {
            return null;
        }
        return registros.stream()
                .min(Comparator.comparingDouble(RegistroTemperatura::getTemperatura))
                .orElse(null);
    }

    public static String obtenerCiudadMasCalurosa(List<RegistroTemperatura> registros) {
        return registros.stream()
                .max(Comparator.comparingDouble(RegistroTemperatura::getTemperatura))
                .map(RegistroTemperatura::getCiudad)
                .orElse("No hay datos disponibles");
    }

    public static String obtenerCiudadMasFria(List<RegistroTemperatura> registros) {
        return registros.stream()
                .min(Comparator.comparingDouble(RegistroTemperatura::getTemperatura))
                .map(RegistroTemperatura::getCiudad)
                .orElse("No hay datos disponibles");
    }
}