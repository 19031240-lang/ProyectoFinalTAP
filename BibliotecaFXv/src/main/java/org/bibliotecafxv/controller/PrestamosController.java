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

/**
 * Controlador que orquesta la lógica de negocio de los Préstamos y Devoluciones.
 * Gestiona la sincronización del inventario de libros (disponibilidad), el histórico de
 * transacciones y aplica penalizaciones económicas utilizando el **Patrón de Diseño Strategy**.
 */
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

        // Filtro de búsqueda reactivo acoplado a la propiedad de texto
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

    /**
     * Registra un nuevo contrato de préstamo en la base de datos.
     * Automáticamente localiza el libro prestado y actualiza su estado a "No Disponible".
     */
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
            l.setDisponible(false);
            libroDAO.actualizar(l);

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

    /**
     * Finaliza el ciclo de vida de un préstamo devolviendo el libro al inventario activo.
     * Evalúa si existe un incumplimiento de fechas y aplica el **Patrón Strategy** para calcular
     * el monto económico de la multa correspondiente antes de cerrar la transacción.
     */
    @FXML
    private void finalizarPrestamo() {
        Prestamo sel = tablaPrestamos.getSelectionModel().getSelectedItem();
        if (sel == null || sel.getEstado().equals("DEVUELTO")) return;

        LocalDate hoy = LocalDate.now();
        LocalDate limite = sel.getFechaDevolucion().toLocalDate();

        // --- APLICACIÓN PATRÓN STRATEGY ---
        if (hoy.isAfter(limite)) {
            long dias = ChronoUnit.DAYS.between(limite, hoy);
            MultaStrategy strategy = new MultaBasicaStrategy();
            double monto = strategy.calcularMulta((int) dias);
            mostrarAlerta("MULTA", "Retraso: " + dias + " días. Pago: $" + monto, Alert.AlertType.WARNING);
        }

        sel.setEstado("DEVUELTO");
        if (prestamoDAO.actualizar(sel)) {
            for (Libro lib : libroDAO.listar()) {
                if (lib.getId() == sel.getIdLibro()) {
                    lib.setDisponible(true);
                    libroDAO.actualizar(lib);
                    break;
                }
            }

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

    /**
     * Rellena las listas de selección, filtrando la lista de libros para mostrar
     * únicamente aquellos que cuentan con stock físico disponible.
     */
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