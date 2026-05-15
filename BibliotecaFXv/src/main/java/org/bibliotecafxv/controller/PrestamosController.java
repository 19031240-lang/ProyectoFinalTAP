package org.bibliotecafxv.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.bibliotecafxv.dao.*;
import org.bibliotecafxv.model.*;
import org.bibliotecafxv.strategy.*;
import java.sql.Date;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class PrestamosController {

    @FXML private ComboBox<Usuario> cmbUsuario;
    @FXML private ComboBox<Libro> cmbLibro;
    @FXML private DatePicker dpFechaDevolucion;
    @FXML private TextField txtBuscar;

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
    private ObservableList<Prestamo> masterData = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        prestamoDAO = new PrestamoDAO();
        usuarioDAO = new UsuarioDAO();
        libroDAO = new LibroDAO();

        configurarTabla();
        cargarCombos();
        cargarDatosTabla();

        txtBuscar.textProperty().addListener((obs, oldVal, newVal) -> filtrar(newVal));

        tablaPrestamos.getSelectionModel().selectedItemProperty().addListener((obs, oldS, newS) -> {
            if (newS != null) cargarPrestamoEnFormulario(newS);
        });
    }

    private void configurarTabla() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colUsuario.setCellValueFactory(new PropertyValueFactory<>("nombreUsuario"));
        colLibro.setCellValueFactory(new PropertyValueFactory<>("tituloLibro"));
        colFechaPrestamo.setCellValueFactory(new PropertyValueFactory<>("fechaPrestamo"));
        colFechaDevolucion.setCellValueFactory(new PropertyValueFactory<>("fechaDevolucion"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
    }

    private void cargarDatosTabla() {
        masterData.clear();
        masterData.addAll(prestamoDAO.listar());
        tablaPrestamos.setItems(masterData);
    }

    private void filtrar(String valor) {
        if (valor == null || valor.isEmpty()) {
            tablaPrestamos.setItems(masterData);
        } else {
            String f = valor.toLowerCase();
            tablaPrestamos.setItems(masterData.filtered(p ->
                    p.getNombreUsuario().toLowerCase().contains(f) ||
                            p.getTituloLibro().toLowerCase().contains(f)
            ));
        }
    }

    private void cargarPrestamoEnFormulario(Prestamo p) {
        cmbUsuario.getItems().stream()
                .filter(u -> u.getId() == p.getIdUsuario())
                .findFirst().ifPresent(u -> cmbUsuario.setValue(u));

        cmbLibro.getItems().stream()
                .filter(l -> l.getId() == p.getIdLibro())
                .findFirst().ifPresent(l -> cmbLibro.setValue(l));

        dpFechaDevolucion.setValue(p.getFechaDevolucion().toLocalDate());
    }

    @FXML
    private void registrarPrestamo() {
        Usuario u = cmbUsuario.getValue();
        Libro l = cmbLibro.getValue();
        if (u == null || l == null || dpFechaDevolucion.getValue() == null) {
            mostrarAlerta("Error", "Completa todos los campos", Alert.AlertType.ERROR);
            return;
        }

        Prestamo p = new Prestamo();
        p.setIdUsuario(u.getId());
        p.setIdLibro(l.getId());
        p.setFechaPrestamo(Date.valueOf(LocalDate.now()));
        p.setFechaDevolucion(Date.valueOf(dpFechaDevolucion.getValue()));
        p.setEstado("ACTIVO");

        if (prestamoDAO.guardar(p)) {
            libroDAO.actualizarEstadoDisponible(l.getId(), false);
            mostrarAlerta("Éxito", "Prestamo guardado", Alert.AlertType.INFORMATION);
            limpiarFormulario();
            cargarDatosTabla();
            cargarCombos();
        }
    }

    @FXML
    private void actualizarPrestamo() {
        Prestamo sel = tablaPrestamos.getSelectionModel().getSelectedItem();
        if (sel == null) {
            mostrarAlerta("Atención", "Selecciona un préstamo", Alert.AlertType.WARNING);
            return;
        }

        sel.setIdUsuario(cmbUsuario.getValue().getId());
        sel.setIdLibro(cmbLibro.getValue().getId());
        sel.setFechaDevolucion(Date.valueOf(dpFechaDevolucion.getValue()));

        if (prestamoDAO.actualizar(sel)) {
            mostrarAlerta("Éxito", "Actualizado correctamente", Alert.AlertType.INFORMATION);
            cargarDatosTabla();
            limpiarFormulario();
        }
    }

    @FXML
    private void finalizarPrestamo() {
        Prestamo sel = tablaPrestamos.getSelectionModel().getSelectedItem();
        if (sel == null || sel.getEstado().equals("DEVUELTO")) return;

        // LÓGICA DE MULTAS (STRATEGY)
        LocalDate hoy = LocalDate.now();
        LocalDate limite = sel.getFechaDevolucion().toLocalDate();
        if (hoy.isAfter(limite)) {
            long dias = ChronoUnit.DAYS.between(limite, hoy);
            MultaStrategy strategy = new MultaBasicaStrategy();
            double monto = strategy.calcularMulta((int) dias);
            mostrarAlerta("MULTA", "Retraso: " + dias + " días. Pago: $" + monto, Alert.AlertType.WARNING);
        }

        if (prestamoDAO.marcarComoDevuelto(sel.getId())) {
            libroDAO.actualizarEstadoDisponible(sel.getIdLibro(), true);
            mostrarAlerta("Éxito", "Libro devuelto", Alert.AlertType.INFORMATION);
            cargarDatosTabla();
            cargarCombos();
            limpiarFormulario();
        }
    }

    @FXML
    private void eliminarPrestamo() {
        Prestamo sel = tablaPrestamos.getSelectionModel().getSelectedItem();
        if (sel == null) return;

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "¿Eliminar este registro de préstamo?");
        if (confirm.showAndWait().get() == ButtonType.OK) {
            if (prestamoDAO.eliminar(sel.getId())) {
                cargarDatosTabla();
                limpiarFormulario();
            }
        }
    }

    @FXML
    private void limpiarFormulario() {
        cmbUsuario.getSelectionModel().clearSelection();
        cmbLibro.getSelectionModel().clearSelection();
        dpFechaDevolucion.setValue(null);
        tablaPrestamos.getSelectionModel().clearSelection();
    }

    private void cargarCombos() {
        cmbUsuario.setItems(FXCollections.observableArrayList(usuarioDAO.listar()));
        ObservableList<Libro> disp = FXCollections.observableArrayList();
        for (Libro l : libroDAO.listar()) if (l.isDisponible()) disp.add(l);
        cmbLibro.setItems(disp);
    }

    private void mostrarAlerta(String t, String c, Alert.AlertType ti) {
        Alert a = new Alert(ti); a.setTitle(t); a.setHeaderText(null); a.setContentText(c); a.showAndWait();
    }
}