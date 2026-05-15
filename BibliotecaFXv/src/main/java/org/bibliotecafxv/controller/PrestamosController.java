package org.bibliotecafxv.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.bibliotecafxv.dao.LibroDAO;
import org.bibliotecafxv.dao.PrestamoDAO;
import org.bibliotecafxv.dao.UsuarioDAO;
import org.bibliotecafxv.model.Libro;
import org.bibliotecafxv.model.Prestamo;
import org.bibliotecafxv.model.Usuario;

import java.sql.Date;
import java.time.LocalDate;

public class PrestamosController {

    @FXML private ComboBox<Usuario> cmbUsuario;
    @FXML private ComboBox<Libro> cmbLibro;
    @FXML private DatePicker dpFechaDevolucion;

    @FXML private TableView<Prestamo> tablaPrestamos;
    @FXML private TableColumn<Prestamo, Integer> colId;
    @FXML private TableColumn<Prestamo, String> colUsuario;
    @FXML private TableColumn<Prestamo, String> colLibro;
    @FXML private TableColumn<Prestamo, Date> colFechaPrestamo;
    @FXML private TableColumn<Prestamo, Date> colFechaDevolucion;
    @FXML private TableColumn<Prestamo, String> colEstado;

    private PrestamoDAO prestamoDAO;
    private UsuarioDAO usuarioDAO;
    private LibroDAO libroDAO;

    @FXML
    public void initialize() {
        prestamoDAO = new PrestamoDAO();
        usuarioDAO = new UsuarioDAO();
        libroDAO = new LibroDAO();

        configurarTabla();
        cargarCombos();
        cargarDatosTabla();
    }

    private void configurarTabla() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colUsuario.setCellValueFactory(new PropertyValueFactory<>("nombreUsuario"));
        colLibro.setCellValueFactory(new PropertyValueFactory<>("tituloLibro"));
        colFechaPrestamo.setCellValueFactory(new PropertyValueFactory<>("fechaPrestamo"));
        colFechaDevolucion.setCellValueFactory(new PropertyValueFactory<>("fechaDevolucion"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
    }

    private void cargarCombos() {
        // Cargar usuarios
        cmbUsuario.setItems(FXCollections.observableArrayList(usuarioDAO.listar()));

        // Cargar solo libros que estén disponibles
        ObservableList<Libro> librosDisponibles = FXCollections.observableArrayList();
        for (Libro l : libroDAO.listar()) {
            if (l.isDisponible()) {
                librosDisponibles.add(l);
            }
        }
        cmbLibro.setItems(librosDisponibles);
    }

    private void cargarDatosTabla() {
        tablaPrestamos.setItems(FXCollections.observableArrayList(prestamoDAO.listar()));
    }

    @FXML
    private void registrarPrestamo() {
        Usuario user = cmbUsuario.getValue();
        Libro libro = cmbLibro.getValue();
        LocalDate fechaDev = dpFechaDevolucion.getValue();

        if (user == null || libro == null || fechaDev == null) {
            mostrarAlerta("Error", "Todos los campos son obligatorios.", Alert.AlertType.ERROR);
            return;
        }

        Prestamo p = new Prestamo();
        p.setIdUsuario(user.getId());
        p.setIdLibro(libro.getId());
        p.setFechaPrestamo(Date.valueOf(LocalDate.now()));
        p.setFechaDevolucion(Date.valueOf(fechaDev));
        p.setEstado("ACTIVO");

        if (prestamoDAO.guardar(p)) {
            libroDAO.actualizarEstadoDisponible(libro.getId(), false);

            mostrarAlerta("Éxito", "Préstamo registrado correctamente.", Alert.AlertType.INFORMATION);
            limpiarFormulario();
            cargarCombos();
            cargarDatosTabla();
        }
    }

    @FXML
    private void finalizarPrestamo() {
        Prestamo seleccionado = tablaPrestamos.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            mostrarAlerta("Atención", "Selecciona un préstamo de la tabla.", Alert.AlertType.WARNING);
            return;
        }

        if (seleccionado.getEstado().equals("DEVUELTO")) {
            mostrarAlerta("Información", "Este préstamo ya fue devuelto.", Alert.AlertType.INFORMATION);
            return;
        }

        if (prestamoDAO.marcarComoDevuelto(seleccionado.getId())) {
            // IMPORTANTE: Volver a poner el libro como disponible
            libroDAO.actualizarEstadoDisponible(seleccionado.getIdLibro(), true);

            mostrarAlerta("Éxito", "Libro devuelto correctamente.", Alert.AlertType.INFORMATION);
            cargarCombos();
            cargarDatosTabla();
        }
    }

    @FXML
    private void limpiarFormulario() {
        cmbUsuario.getSelectionModel().clearSelection();
        cmbLibro.getSelectionModel().clearSelection();
        dpFechaDevolucion.setValue(null);
    }

    private void mostrarAlerta(String titulo, String contenido, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(contenido);
        alert.showAndWait();
    }
}