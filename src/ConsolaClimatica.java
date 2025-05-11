import javax.swing.*;
import java.awt.*;

import java.io.IOException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.ZoneId;

import java.util.*;
import java.util.List;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import datechooser.beans.DateChooserCombo; 

public class ConsolaClimatica extends JFrame {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private List<RegistroTemperatura> registros = new ArrayList<>();
    private JTextArea resultadoTextArea;
    private DateChooserCombo fechaConsultaChooser;
    private DateChooserCombo desdeChooser; 
    private DateChooserCombo hastaChooser;
    private JPanel graficoPanel;

    public ConsolaClimatica() {
        setTitle("Análisis de Temperaturas");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 700);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new GridBagLayout());
        controlPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1; // Para que los campos de texto se expandan


        JLabel tituloLabel = new JLabel("CONSOLA CLIMÁTICA", SwingConstants.CENTER);
        tituloLabel.setFont(new Font("Arial", Font.BOLD, 12)); // Opcional: darle un formato
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2; //columnas para centrar
        controlPanel.add(tituloLabel, gbc);

        JLabel descripcionLabel = new JLabel("Por favor, elija el rango de fechas para el que desea consultar, a continuación cargue los datos, luego elija la opción mostrar gráfica.", SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2; // Ocupa dos columnas para centrar
        controlPanel.add(descripcionLabel, gbc);

        gbc.gridwidth = 1;



        JLabel desdeLabel = new JLabel("Fecha Desde:");
        desdeChooser = new DateChooserCombo(); // Inicializamos DateChooserCombo para "Desde"
        desdeChooser.setPreferredSize(new Dimension(120, 25));

        JLabel hastaLabel = new JLabel("Fecha Hasta:");
        hastaChooser = new DateChooserCombo(); // Inicializamos DateChooserCombo para "Hasta"
        hastaChooser.setPreferredSize(new Dimension(120, 25));

        JLabel fechaConsultaLabel = new JLabel("Fecha Consulta:");
        fechaConsultaChooser = new DateChooserCombo(); // Inicializamos DateChooserCombo para "Consulta"
        fechaConsultaChooser.setPreferredSize(new Dimension(120, 25));

        JButton cargarButton = new JButton("Cargar Datos");
        JButton graficarButton = new JButton("Mostrar Gráfica");
        JButton consultarButton = new JButton("Consultar Ciudades");

        resultadoTextArea = new JTextArea(10, 40);
        resultadoTextArea.setEditable(false);
        JScrollPane resultadoScrollPane = new JScrollPane(resultadoTextArea);
        resultadoScrollPane.setBorder(BorderFactory.createTitledBorder("Resultados"));

        graficoPanel = new JPanel();
        graficoPanel.setLayout(new BorderLayout());
        graficoPanel.setBorder(BorderFactory.createTitledBorder("Gráfica de Temperaturas Promedio"));

        gbc.gridx = 0;
        gbc.gridy = 2;
        controlPanel.add(desdeLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        controlPanel.add(desdeChooser, gbc); 

        gbc.gridx = 0;
        gbc.gridy = 3;
        controlPanel.add(hastaLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        controlPanel.add(hastaChooser, gbc); 

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2; 
        controlPanel.add(cargarButton, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        controlPanel.add(graficarButton, gbc);

        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 6;
        controlPanel.add(fechaConsultaLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 6;
        controlPanel.add(fechaConsultaChooser, gbc); 

        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 2;
        controlPanel.add(consultarButton, gbc);

        panel.add(controlPanel, BorderLayout.NORTH);
        panel.add(resultadoScrollPane, BorderLayout.CENTER);
        panel.add(graficoPanel, BorderLayout.SOUTH);

        add(panel);
        //pack(); // Ajusta el tamaño de la ventana al contenido
        cargarButton.addActionListener(e -> cargarDatos());
        graficarButton.addActionListener(e -> {
            Date desdeDate = desdeChooser.getSelectedDate().getTime();
            Date hastaDate = hastaChooser.getSelectedDate().getTime();
            if (desdeDate != null && hastaDate != null) {
                LocalDate desdeLocalDate = desdeDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                LocalDate hastaLocalDate = hastaDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                mostrarGrafica(desdeLocalDate.format(DATE_FORMATTER), hastaLocalDate.format(DATE_FORMATTER));
            } else {
                resultadoTextArea.append("Por favor, seleccione las fechas de inicio y fin para la gráfica.\n");
            }
        });
        consultarButton.addActionListener(e -> {
            Date selectedDate = fechaConsultaChooser.getSelectedDate().getTime();
            if (selectedDate != null) {
                LocalDate localDate = selectedDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                consultarCiudades(localDate.format(DATE_FORMATTER));
            } else {
                resultadoTextArea.append("Por favor, seleccione una fecha para la consulta.\n");
            }
        });

        setVisible(true);
    }

    private void cargarDatos() {
        try {
            registros = AnalizadorTemperatura.leerDatosDesdeCSV("Temperaturas.csv");
            resultadoTextArea.append("Datos cargados exitosamente.\n");
        } catch (IOException e) {
            resultadoTextArea.append("Error al cargar los datos: " + e.getMessage() + "\n");
        }
    }

     private void mostrarGrafica(String desdeStr, String hastaStr) {
        resultadoTextArea.setText("");
        if (registros.isEmpty()) {
            resultadoTextArea.append("Primero debe cargar los datos.\n");
            return;
        }

        LocalDate desde = null;
        LocalDate hasta = null;
        try {
            desde = LocalDate.parse(desdeStr, DATE_FORMATTER);
            hasta = LocalDate.parse(hastaStr, DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            
        }

        if (desde.isAfter(hasta)) {
            resultadoTextArea.append("La fecha de inicio no puede ser posterior a la fecha de fin.\n");
            return;
        }

        List<RegistroTemperatura> registrosFiltrados = AnalizadorTemperatura.filtrarRegistros(registros, desde, hasta);

        Map<String, Double> promedioPorCiudad = AnalizadorTemperatura.calcularPromedioPorCiudad(registrosFiltrados);

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
            for (Map.Entry<String, Double> entry : promedioPorCiudad.entrySet()) {
            dataset.addValue(entry.getValue(), "Promedio", entry.getKey());

}



        JFreeChart grafico = ChartFactory.createBarChart(
                "Promedio de Temperatura por Ciudad",
                "Ciudad",
                "Temperatura Promedio (°C)",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        CategoryPlot plot = grafico.getCategoryPlot();
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setDrawBarOutline(false);
        plot.setBackgroundPaint(Color.WHITE);
        plot.setRangeGridlinePaint(Color.GRAY);

        ChartPanel chartPanel = new ChartPanel(grafico);
        chartPanel.setPreferredSize(new Dimension(400, 250));
        graficoPanel.removeAll();
        graficoPanel.add(chartPanel, BorderLayout.CENTER);
        graficoPanel.revalidate();
        graficoPanel.repaint();

        resultadoTextArea.append("Gráfica mostrada.\n");
    }

    private void consultarCiudades(String fechaStr) {
        if (registros.isEmpty()) {
            resultadoTextArea.append("Primero debe cargar los datos.\n");
            return;
        }

        LocalDate fechaConsulta = null;
        try {
            fechaConsulta = LocalDate.parse(fechaStr, DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            resultadoTextArea.append("Error en el formato de fecha. Use dd/MM/yyyy.\n");
            return;
        }

        List<RegistroTemperatura> registrosFiltrados = AnalizadorTemperatura.filtrarRegistrosPorFecha(registros, fechaConsulta);

        if (registrosFiltrados.isEmpty()) {
            resultadoTextArea.append("No hay datos de temperatura para la fecha: " + fechaStr + "\n");
            return;
        }

        RegistroTemperatura registroMasCaluroso = AnalizadorTemperatura.obtenerRegistroMasCaluroso(registrosFiltrados);
        RegistroTemperatura registroMasFrio = AnalizadorTemperatura.obtenerRegistroMasFrio(registrosFiltrados);

        resultadoTextArea.append("Fecha de consulta: " + fechaStr + "\n");

        if (registroMasCaluroso != null) {
            resultadoTextArea.append("Ciudad más calurosa: " + registroMasCaluroso.getCiudad() +
                    " con temperatura: " + String.format("%.2f", registroMasCaluroso.getTemperatura()) + " °C\n");
        } else {
            resultadoTextArea.append("No se encontraron registros para determinar la ciudad más calurosa.\n");
        }

        if (registroMasFrio != null) {
            resultadoTextArea.append("Ciudad más fría: " + registroMasFrio.getCiudad() +
                    " con temperatura: " + String.format("%.2f", registroMasFrio.getTemperatura()) + " °C\n");
        } else {
            resultadoTextArea.append("No se encontraron registros para determinar la ciudad más fría.\n");
        }
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ConsolaClimatica());
    }
}