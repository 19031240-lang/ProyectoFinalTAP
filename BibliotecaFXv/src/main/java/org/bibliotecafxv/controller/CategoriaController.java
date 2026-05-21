package org.bibliotecafxv.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.bibliotecafxv.dao.CategoriaDAO;
import org.bibliotecafxv.model.Categoria;

/**
 * Controlador de la interfaz gráfica encargado de clasificar la literatura de la biblioteca.
 * Proporciona el mantenimiento integral del listado de categorías, asegurando la consistencia
 * de los nombres en la base de datos y administrando los eventos de selección de la interfaz.
 */
public class CategoriaController {

    @FXML private TextField txtNombre;
    @FXML private TableView<Categoria> tblCategorias;
    @FXML private TableColumn<Categoria, Integer> colId;
    @FXML private TableColumn<Categoria, String> colNombre;

    private CategoriaDAO categoriaDAO;
    private ObservableList<Categoria> listaCategorias;
    private Categoria categoriaSeleccionada;

    /**
     * Vincula las columnas con el modelo Categoria, descarga los registros persistidos
     * y añade el listener de escucha en el renglón activo de la tabla.
     */
    @FXML
    public void initialize() {
        categoriaDAO = new CategoriaDAO();
        configurarTabla();
        cargarDatos();

        tblCategorias.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> seleccionarCategoria(newSelection)
        );
    }

    private void configurarTabla() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
    }

    private void cargarDatos() {
        listaCategorias = FXCollections.observableArrayList(categoriaDAO.listar());
        tblCategorias.setItems(listaCategorias);
    }

    /**
     * Copia los datos del objeto Categoria clicado hacia los campos modificables de la vista.
     * @param categoria Fila activa seleccionada en el TableView.
     */
    private void seleccionarCategoria(Categoria categoria) {
        if (categoria != null) {
            categoriaSeleccionada = categoria;
            txtNombre.setText(categoria.getNombre());
        }
    }

    @FXML
    private void guardarCategoria() {
        if (txtNombre.getText().trim().isEmpty()) {
            mostrarAlerta("Error", "El nombre de la categoría es obligatorio.", Alert.AlertType.ERROR);
            return;
        }
        if (categoriaSeleccionada != null) {
            mostrarAlerta("Atención", "Hay una categoría seleccionada. Usa 'Actualizar' o 'Limpiar'.", Alert.AlertType.WARNING);
            return;
        }

        Categoria nueva = new Categoria();
        nueva.setNombre(txtNombre.getText().trim());

        if (categoriaDAO.guardar(nueva)) {
            mostrarAlerta("Éxito", "Categoría guardada.", Alert.AlertType.INFORMATION);
            limpiarFormulario();
            cargarDatos();
        }
    }

    @FXML
    private void actualizarCategoria() {
        if (categoriaSeleccionada == null) {
            mostrarAlerta("Atención", "Selecciona una categoría de la tabla.", Alert.AlertType.WARNING);
            return;
        }
        if (txtNombre.getText().trim().isEmpty()) {
            mostrarAlerta("Error", "El nombre de la categoría es obligatorio.", Alert.AlertType.ERROR);
            return;
        }

        categoriaSeleccionada.setNombre(txtNombre.getText().trim());

        if (categoriaDAO.actualizar(categoriaSeleccionada)) {
            mostrarAlerta("Éxito", "Categoría actualizada.", Alert.AlertType.INFORMATION);
            limpiarFormulario();
            cargarDatos();
        }
    }

    @FXML
    private void eliminarCategoria() {
        if (categoriaSeleccionada == null) {
            mostrarAlerta("Atención", "Selecciona una categoría de la tabla.", Alert.AlertType.WARNING);
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "¿Seguro que deseas eliminar esta categoría?");
        if (confirm.showAndWait().get() == ButtonType.OK) {
            if (categoriaDAO.eliminar(categoriaSeleccionada.getId())) {
                mostrarAlerta("Éxito", "Categoría eliminada.", Alert.AlertType.INFORMATION);
                limpiarFormulario();
                cargarDatos();
            } else {
                mostrarAlerta("Error", "No se puede eliminar. Es probable que haya libros registrados en esta categoría.", Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
    private void limpiarFormulario() {
        txtNombre.clear();
        categoriaSeleccionada = null;
        tblCategorias.getSelectionModel().clearSelection();
    }

    private void mostrarAlerta(String titulo, String contenido, Alert.AlertType tipo) {
        Alert a = new Alert(tipo);
        a.setTitle(titulo);
        a.setHeaderText(null);
        a.setContentText(contenido);
        a.showAndWait();
    }
}