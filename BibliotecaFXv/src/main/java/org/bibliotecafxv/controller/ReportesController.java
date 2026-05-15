package org.bibliotecafxv.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.stage.FileChooser;
import org.bibliotecafxv.dao.LibroDAO;
import org.bibliotecafxv.dao.PrestamoDAO;
import org.bibliotecafxv.model.Libro;
import org.bibliotecafxv.model.Prestamo;
import org.bibliotecafxv.strategy.MultaBasicaStrategy;
import org.bibliotecafxv.strategy.MultaPremiumStrategy;
import org.bibliotecafxv.strategy.MultaStrategy;
import java.time.temporal.ChronoUnit;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.Writer;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class ReportesController {

    private LibroDAO libroDAO;
    private PrestamoDAO prestamoDAO;

    @FXML
    public void initialize() {
        libroDAO = new LibroDAO();
        prestamoDAO = new PrestamoDAO();
    }

    // --- SECCIÓN DE PDF ---
    @FXML
    private void generarPdfMorosos() {
        List<Prestamo> morosos = prestamoDAO.listar().stream()
                .filter(p -> p.getEstado().equals("ACTIVO") && p.getFechaDevolucion().toLocalDate().isBefore(LocalDate.now()))
                .collect(Collectors.toList());

        if (morosos.isEmpty()) {
            mostrarAlerta("Información", "¡Todo en orden! No hay usuarios morosos.", Alert.AlertType.INFORMATION);
            return;
        }

        File archivo = seleccionarDestino("Guardar PDF Morosos", "Morosos.pdf", "*.pdf");
        if (archivo != null) {
            crearDocumentoPdfMorosos(archivo, morosos);
        }
    }

    @FXML
    private void generarPdfPrestamos() {
        List<Prestamo> activos = prestamoDAO.listar().stream()
                .filter(p -> p.getEstado().equals("ACTIVO"))
                .collect(Collectors.toList());

        if (activos.isEmpty()) {
            mostrarAlerta("Información", "No hay libros prestados actualmente.", Alert.AlertType.INFORMATION);
            return;
        }

        File archivo = seleccionarDestino("Guardar PDF Préstamos", "Libros_Prestados.pdf", "*.pdf");
        if (archivo != null) {
            crearDocumentoPdfPrestamos(archivo, activos);
        }
    }
    // --- CREADOR DE PDF PARA MOROSOS (CON MULTAS Y DÍAS) ---
    private void crearDocumentoPdfMorosos(File archivo, List<Prestamo> lista) {
        Document documento = new Document();
        try {
            PdfWriter.getInstance(documento, new FileOutputStream(archivo));
            documento.open();

            Font fontTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.BLACK);
            Paragraph titulo = new Paragraph("Reporte de Usuarios Morosos", fontTitulo);
            titulo.setAlignment(Element.ALIGN_CENTER);
            titulo.setSpacingAfter(20);
            documento.add(titulo);

            PdfPTable tabla = new PdfPTable(7);
            tabla.setWidthPercentage(100);
            tabla.setWidths(new float[]{1f, 3f, 3f, 2f, 2f, 2f, 2f});

            tabla.addCell("ID");
            tabla.addCell("Usuario");
            tabla.addCell("Libro");
            tabla.addCell("F. Préstamo");
            tabla.addCell("F. Límite");
            tabla.addCell("Días Retraso");
            tabla.addCell("Deuda");

            LocalDate hoy = LocalDate.now();

            for (int i = 0; i < lista.size(); i++) {
                Prestamo p = lista.get(i);

                tabla.addCell(String.valueOf(p.getId()));
                tabla.addCell(p.getNombreUsuario());
                tabla.addCell(p.getTituloLibro());
                tabla.addCell(p.getFechaPrestamo().toString());
                tabla.addCell(p.getFechaDevolucion().toString());

                LocalDate fechaLimite = p.getFechaDevolucion().toLocalDate();
                long diasRetraso = ChronoUnit.DAYS.between(fechaLimite, hoy);

                MultaStrategy estrategia;
                if (diasRetraso <= 7) {
                    estrategia = new MultaBasicaStrategy(); // 1 a 7 días cobra básico
                } else {
                    estrategia = new MultaPremiumStrategy(); // Más de 7 días cobra premium
                }

                double multa = estrategia.calcularMulta((int) diasRetraso);

                tabla.addCell(diasRetraso + " días");
                tabla.addCell("$" + multa);
            }

            documento.add(tabla);
            documento.close();
            mostrarAlerta("Éxito", "Reporte de morosos generado con multas.", Alert.AlertType.INFORMATION);

        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudo generar el PDF:\n" + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    // --- CREADOR DE PDF PARA PRÉSTAMOS NORMALES (SIN MULTAS) ---
    private void crearDocumentoPdfPrestamos(File archivo, List<Prestamo> lista) {
        Document documento = new Document();
        try {
            PdfWriter.getInstance(documento, new FileOutputStream(archivo));
            documento.open();

            Font fontTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.BLACK);
            Paragraph titulo = new Paragraph("Reporte de Libros Prestados", fontTitulo);
            titulo.setAlignment(Element.ALIGN_CENTER);
            titulo.setSpacingAfter(20);
            documento.add(titulo);

            PdfPTable tabla = new PdfPTable(5);
            tabla.setWidthPercentage(100);
            tabla.setWidths(new float[]{1f, 3f, 3f, 2f, 2f});

            tabla.addCell("ID");
            tabla.addCell("Usuario");
            tabla.addCell("Libro");
            tabla.addCell("F. Préstamo");
            tabla.addCell("F. Límite");

            for (Prestamo p : lista) {
                tabla.addCell(String.valueOf(p.getId()));
                tabla.addCell(p.getNombreUsuario());
                tabla.addCell(p.getTituloLibro());
                tabla.addCell(p.getFechaPrestamo().toString());
                tabla.addCell(p.getFechaDevolucion().toString());
            }

            documento.add(tabla);
            documento.close();
            mostrarAlerta("Éxito", "Reporte de préstamos generado.", Alert.AlertType.INFORMATION);

        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudo generar el PDF:\n" + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    // --- SECCIÓN DE EXPORTACIÓN (JSON Y CSV) ---

    @FXML
    private void exportarCatalogoJson() {
        File archivo = seleccionarDestino("Exportar Catálogo JSON", "Catalogo.json", "*.json");
        if (archivo == null) return;

        List<Libro> libros = libroDAO.listar();
        // Usamos Gson para convertir la lista de libros a formato JSON bonito
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        try (Writer writer = new FileWriter(archivo)) {
            gson.toJson(libros, writer);
            mostrarAlerta("Éxito", "Catálogo exportado a JSON exitosamente.", Alert.AlertType.INFORMATION);
        } catch (Exception e) {
            mostrarAlerta("Error", "Falló la exportación:\n" + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void exportarCatalogoCsv() {
        File archivo = seleccionarDestino("Exportar Catálogo CSV", "Catalogo.csv", "*.csv");
        if (archivo == null) return;

        List<Libro> libros = libroDAO.listar();

        try (FileWriter writer = new FileWriter(archivo)) {
            // Escribimos la cabecera (nombres de columnas)
            writer.write("ID,Titulo,Autor,Categoria,Disponible\n");

            // Escribimos los datos línea por línea
            for (Libro l : libros) {
                writer.write(String.format("%d,\"%s\",\"%s\",\"%s\",%b\n",
                        l.getId(), l.getTitulo(), l.getAutor(), l.getCategoria(), l.isDisponible()));
            }
            mostrarAlerta("Éxito", "Catálogo exportado a Excel/CSV exitosamente.", Alert.AlertType.INFORMATION);
        } catch (Exception e) {
            mostrarAlerta("Error", "Falló la exportación:\n" + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    // --- HERRAMIENTAS UTILES ---

    private File seleccionarDestino(String titulo, String nombreDefault, String extension) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(titulo);
        fileChooser.setInitialFileName(nombreDefault);
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos " + extension, extension));
        return fileChooser.showSaveDialog(null);
    }

    private void mostrarAlerta(String titulo, String contenido, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(contenido);
        alert.showAndWait();
    }
}