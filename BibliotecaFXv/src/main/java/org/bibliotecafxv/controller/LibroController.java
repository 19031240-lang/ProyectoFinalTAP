package org.bibliotecafxv.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.bibliotecafxv.dao.LibroDAO;
import org.bibliotecafxv.model.Libro;

public class LibroController {

    @FXML private TableView<Libro> tblLibros;

    @FXML private TableColumn<Libro, Integer> colId;
    @FXML private TableColumn<Libro, String> colTitulo;
    @FXML private TableColumn<Libro, String> colAutor;
    @FXML private TableColumn<Libro, String> colCategoria;
    @FXML private TableColumn<Libro, String> colDescripcion;

    @FXML private TextField txtTitulo;
    @FXML private TextField txtAutor;
    @FXML private TextField txtCategoria;
    @FXML private CheckBox chkDisponible;

    @FXML private TextArea txtDescripcion;
    @FXML private TextField txtPortada;
    @FXML private TextField txtBuscar;

    private final LibroDAO dao = new LibroDAO();

    @FXML
    public void initialize() {

        colId.setCellValueFactory(
                new PropertyValueFactory<>("id"));

        colTitulo.setCellValueFactory(
                new PropertyValueFactory<>("titulo"));

        colAutor.setCellValueFactory(
                new PropertyValueFactory<>("autor"));

        colCategoria.setCellValueFactory(
                new PropertyValueFactory<>("categoria"));

        colDescripcion.setCellValueFactory(
                new PropertyValueFactory<>("descripcion"));

        cargarLibros();

        tblLibros.getSelectionModel().selectedItemProperty()
                .addListener((obs, oldValue, libro) -> {

                    if (libro != null) {

                        txtTitulo.setText(libro.getTitulo());
                        txtAutor.setText(libro.getAutor());
                        txtCategoria.setText(libro.getCategoria());

                        chkDisponible.setSelected(
                                libro.isDisponible()
                        );

                        txtDescripcion.setText(
                                libro.getDescripcion()
                        );

                        txtPortada.setText(
                                libro.getPortada()
                        );
                    }
                });
    }

    private void cargarLibros() {
        tblLibros.getItems().setAll(dao.listar());
    }

    @FXML
    private void guardarLibro() {

        Libro libro = new Libro(
                0,
                txtTitulo.getText(),
                txtAutor.getText(),
                txtCategoria.getText(),
                chkDisponible.isSelected(),
                txtDescripcion.getText(),
                txtPortada.getText()
        );

        dao.guardar(libro);

        cargarLibros();
        limpiarCampos();
    }

    @FXML
    private void actualizarLibro() {

        Libro libro = tblLibros.getSelectionModel().getSelectedItem();

        if (libro == null) return;

        libro.setTitulo(txtTitulo.getText());
        libro.setAutor(txtAutor.getText());
        libro.setCategoria(txtCategoria.getText());

        libro.setDisponible(
                chkDisponible.isSelected()
        );

        libro.setDescripcion(txtDescripcion.getText());
        libro.setPortada(txtPortada.getText());

        dao.actualizar(libro);

        cargarLibros();
        limpiarCampos();
    }

    @FXML
    private void eliminarLibro() {

        Libro libro = tblLibros.getSelectionModel().getSelectedItem();

        if (libro == null) return;

        dao.eliminar(libro.getId());

        cargarLibros();
        limpiarCampos();
    }

    @FXML
    private void buscarLibro() {

        tblLibros.getItems().setAll(
                dao.buscarPorTitulo(txtBuscar.getText())
        );
    }

    private void limpiarCampos() {

        txtTitulo.clear();
        txtAutor.clear();
        txtCategoria.clear();

        chkDisponible.setSelected(false);

        txtDescripcion.clear();
        txtPortada.clear();
    }
}