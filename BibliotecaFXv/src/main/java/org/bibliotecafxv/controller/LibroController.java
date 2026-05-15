package org.bibliotecafxv.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.bibliotecafxv.dao.LibroDAO;
import org.bibliotecafxv.model.Libro;

public class LibroController {

    @FXML private TextField txtTitulo;
    @FXML private TextField txtAutor;
    @FXML private TextField txtCategoria;
    @FXML private TextArea  txtDescripcion;
    @FXML private TextField txtPortada;
    @FXML private CheckBox  chkDisponible;
    @FXML private TextField txtBuscar;

    @FXML private TableView<Libro>              tblLibros;
    @FXML private TableColumn<Libro, Integer>   colId;
    @FXML private TableColumn<Libro, String>    colTitulo;
    @FXML private TableColumn<Libro, String>    colAutor;
    @FXML private TableColumn<Libro, String>    colCategoria;
    @FXML private TableColumn<Libro, Boolean>   colDisponible;
    @FXML private TableColumn<Libro, String>    colDescripcion;

    private LibroDAO libroDAO;
    private ObservableList<Libro> listaLibros;
    private Libro libroSeleccionado;

    @FXML
    public void initialize() {

        libroDAO = new LibroDAO();

        configurarTabla();
        cargarDatos();

        tblLibros.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> seleccionarLibro(newValue)
        );
    }

    private void configurarTabla() {

        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colTitulo.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        colAutor.setCellValueFactory(new PropertyValueFactory<>("autor"));
        colCategoria.setCellValueFactory(new PropertyValueFactory<>("categoria"));
        colDisponible.setCellValueFactory(new PropertyValueFactory<>("disponible"));
        colDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
    }

    private void cargarDatos() {

        listaLibros = FXCollections.observableArrayList(libroDAO.listar());
        tblLibros.setItems(listaLibros);
    }

    private void seleccionarLibro(Libro libro) {

        if (libro != null) {

            libroSeleccionado = libro;

            txtTitulo.setText(libro.getTitulo());
            txtAutor.setText(libro.getAutor());
            txtCategoria.setText(libro.getCategoria());
            txtDescripcion.setText(libro.getDescripcion());
            txtPortada.setText(libro.getPortada());
            chkDisponible.setSelected(libro.isDisponible());
        }
    }

    @FXML
    private void guardarLibro() {

        if (libroSeleccionado != null) {
            mostrarAlerta("Atención",
                    "Hay un libro seleccionado. Usa 'Actualizar' para modificarlo o 'Limpiar' para agregar uno nuevo.",
                    Alert.AlertType.WARNING);
            return;
        }

        if (txtTitulo.getText().isEmpty() || txtAutor.getText().isEmpty()) {
            mostrarAlerta("Error",
                    "El título y el autor son obligatorios.",
                    Alert.AlertType.ERROR);
            return;
        }

        Libro nuevo = new Libro();
        nuevo.setTitulo(txtTitulo.getText());
        nuevo.setAutor(txtAutor.getText());
        nuevo.setCategoria(txtCategoria.getText());
        nuevo.setDescripcion(txtDescripcion.getText());
        nuevo.setPortada(txtPortada.getText());
        nuevo.setDisponible(chkDisponible.isSelected());

        libroDAO.guardar(nuevo);

        mostrarAlerta("Éxito", "Libro guardado correctamente.", Alert.AlertType.INFORMATION);
        limpiarFormulario();
        cargarDatos();
    }

    @FXML
    private void actualizarLibro() {

        if (libroSeleccionado == null) {
            mostrarAlerta("Atención",
                    "Selecciona un libro de la tabla para actualizarlo.",
                    Alert.AlertType.WARNING);
            return;
        }

        if (txtTitulo.getText().isEmpty() || txtAutor.getText().isEmpty()) {
            mostrarAlerta("Error",
                    "El título y el autor son obligatorios.",
                    Alert.AlertType.ERROR);
            return;
        }

        libroSeleccionado.setTitulo(txtTitulo.getText());
        libroSeleccionado.setAutor(txtAutor.getText());
        libroSeleccionado.setCategoria(txtCategoria.getText());
        libroSeleccionado.setDescripcion(txtDescripcion.getText());
        libroSeleccionado.setPortada(txtPortada.getText());
        libroSeleccionado.setDisponible(chkDisponible.isSelected());

        libroDAO.actualizar(libroSeleccionado);

        mostrarAlerta("Éxito", "Libro actualizado correctamente.", Alert.AlertType.INFORMATION);
        cargarDatos();
        limpiarFormulario();
    }

    @FXML
    private void eliminarLibro() {

        Libro seleccionado = tblLibros.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            mostrarAlerta("Atención",
                    "Selecciona un libro de la tabla.",
                    Alert.AlertType.WARNING);
            return;
        }

        libroDAO.eliminar(seleccionado.getId());

        mostrarAlerta("Éxito", "Libro eliminado.", Alert.AlertType.INFORMATION);
        cargarDatos();
        limpiarFormulario();
    }

    @FXML
    private void buscarLibro() {

        String texto = txtBuscar.getText().trim();

        if (texto.isEmpty()) {
            cargarDatos();
            return;
        }

        listaLibros = FXCollections.observableArrayList(libroDAO.buscarPorTitulo(texto));
        tblLibros.setItems(listaLibros);
    }

    @FXML
    private void limpiarFormulario() {

        txtTitulo.clear();
        txtAutor.clear();
        txtCategoria.clear();
        txtDescripcion.clear();
        txtPortada.clear();
        txtBuscar.clear();
        chkDisponible.setSelected(true);

        libroSeleccionado = null;
        tblLibros.getSelectionModel().clearSelection();
    }

    private void mostrarAlerta(String titulo, String contenido, Alert.AlertType tipo) {

        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(contenido);
        alert.showAndWait();
    }
}