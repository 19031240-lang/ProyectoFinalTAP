package org.bibliotecafxv.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.bibliotecafxv.dao.LibroDAO;
import org.bibliotecafxv.dao.AutorDAO;
import org.bibliotecafxv.dao.CategoriaDAO;
import org.bibliotecafxv.model.Libro;
import org.bibliotecafxv.model.Autor;
import org.bibliotecafxv.model.Categoria;
import java.util.ArrayList;
import java.util.List;

/**
 * Controlador de la interfaz gráfica para la administración del inventario de Libros.
 * Centraliza las operaciones CRUD de los ejemplares e integra los catálogos vinculados
 * (Autores y Categorías) para mantener la integridad relacional de la base de datos.
 */
public class LibroController {

    @FXML private TextField txtTitulo;
    @FXML private ComboBox<Autor> cmbAutor;
    @FXML private ComboBox<Categoria> cmbCategoria;
    @FXML private TextArea txtDescripcion;
    @FXML private TextField txtPortada;
    @FXML private CheckBox chkDisponible;
    @FXML private TextField txtBuscar;

    @FXML private TableView<Libro> tblLibros;
    @FXML private TableColumn<Libro, Integer> colId;
    @FXML private TableColumn<Libro, String> colTitulo;
    @FXML private TableColumn<Libro, String> colAutor;
    @FXML private TableColumn<Libro, String> colCategoria;
    @FXML private TableColumn<Libro, Boolean> colDisponible;
    @FXML private TableColumn<Libro, String> colDescripcion;

    private LibroDAO libroDAO;
    private AutorDAO autorDAO;
    private CategoriaDAO categoriaDAO;
    private ObservableList<Libro> listaLibros;
    private Libro libroSeleccionado;

    /**
     * Inicializa los componentes visuales y las instancias DAO.
     * Carga de forma automática las listas desplegables (ComboBox) y el contenido de la tabla principal.
     */
    @FXML
    public void initialize() {
        libroDAO = new LibroDAO();
        autorDAO = new AutorDAO();
        categoriaDAO = new CategoriaDAO();

        configurarTabla();
        cargarDatos();
        cargarListasDesplegables();

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

    /**
     * Consulta la base de datos para poblar los ComboBox de selección con los objetos Autor y Categoria disponibles.
     */
    private void cargarListasDesplegables() {
        cmbAutor.setItems(FXCollections.observableArrayList(autorDAO.listar()));
        cmbCategoria.setItems(FXCollections.observableArrayList(categoriaDAO.listar()));
    }

    /**
     * Sincroniza el formulario con los datos de la fila seleccionada.
     * Itera sobre los elementos de los ComboBox para seleccionar visualmente el objeto que coincide con el texto en la tabla.
     * @param libro Objeto Libro extraído de la selección activa.
     */
    private void seleccionarLibro(Libro libro) {
        if (libro != null) {
            libroSeleccionado = libro;
            txtTitulo.setText(libro.getTitulo());
            txtDescripcion.setText(libro.getDescripcion());
            txtPortada.setText(libro.getPortada());
            chkDisponible.setSelected(libro.isDisponible());

            if (libro.getAutor() != null) {
                for (Autor a : cmbAutor.getItems()) {
                    if (a.getNombre().equals(libro.getAutor())) {
                        cmbAutor.setValue(a);
                        break;
                    }
                }
            }

            if (libro.getCategoria() != null) {
                for (Categoria c : cmbCategoria.getItems()) {
                    if (c.getNombre().equals(libro.getCategoria())) {
                        cmbCategoria.setValue(c);
                        break;
                    }
                }
            }
        }
    }

    @FXML
    private void guardarLibro() {
        if (libroSeleccionado != null) {
            mostrarAlerta("Atención", "Hay un libro seleccionado. Usa 'Actualizar' o 'Limpiar'.", Alert.AlertType.WARNING);
            return;
        }

        Autor autorSel = cmbAutor.getValue();
        Categoria catSel = cmbCategoria.getValue();

        if (txtTitulo.getText().isEmpty() || autorSel == null || catSel == null) {
            mostrarAlerta("Error", "El título, el autor y la categoría son obligatorios.", Alert.AlertType.ERROR);
            return;
        }

        Libro nuevo = new Libro();
        nuevo.setTitulo(txtTitulo.getText());
        nuevo.setDescripcion(txtDescripcion.getText());
        nuevo.setPortada(txtPortada.getText());
        nuevo.setDisponible(chkDisponible.isSelected());
        nuevo.setIdAutor(autorSel.getId());
        nuevo.setIdCategoria(catSel.getId());

        if (libroDAO.guardar(nuevo)) {
            mostrarAlerta("Éxito", "Libro guardado correctamente.", Alert.AlertType.INFORMATION);
            limpiarFormulario();
            cargarDatos();
        } else {
            mostrarAlerta("Error", "No se pudo guardar el libro.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void actualizarLibro() {
        if (libroSeleccionado == null) {
            mostrarAlerta("Atención", "Selecciona un libro de la tabla para actualizarlo.", Alert.AlertType.WARNING);
            return;
        }

        Autor autorSel = cmbAutor.getValue();
        Categoria catSel = cmbCategoria.getValue();

        if (txtTitulo.getText().isEmpty() || autorSel == null || catSel == null) {
            mostrarAlerta("Error", "El título, el autor y la categoría son obligatorios.", Alert.AlertType.ERROR);
            return;
        }

        libroSeleccionado.setTitulo(txtTitulo.getText());
        libroSeleccionado.setDescripcion(txtDescripcion.getText());
        libroSeleccionado.setPortada(txtPortada.getText());
        libroSeleccionado.setDisponible(chkDisponible.isSelected());
        libroSeleccionado.setIdAutor(autorSel.getId());
        libroSeleccionado.setIdCategoria(catSel.getId());

        if (libroDAO.actualizar(libroSeleccionado)) {
            mostrarAlerta("Éxito", "Libro actualizado correctamente.", Alert.AlertType.INFORMATION);
            cargarDatos();
            limpiarFormulario();
        } else {
            mostrarAlerta("Error", "No se pudo actualizar el libro.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void eliminarLibro() {
        Libro seleccionado = tblLibros.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            mostrarAlerta("Atención", "Selecciona un libro de la tabla.", Alert.AlertType.WARNING);
            return;
        }

        if (libroDAO.eliminar(seleccionado.getId())) {
            mostrarAlerta("Éxito", "Libro eliminado.", Alert.AlertType.INFORMATION);
            cargarDatos();
            limpiarFormulario();
        } else {
            mostrarAlerta("Error", "No se pudo eliminar el libro.", Alert.AlertType.ERROR);
        }
    }

    /**
     * Filtra los registros de la tabla en tiempo real basándose en coincidencias de texto
     * parciales dentro de los campos Título o Autor.
     */
    @FXML
    private void buscarLibro() {
        String texto = txtBuscar.getText().trim().toLowerCase();

        if (texto.isEmpty()) {
            cargarDatos();
            return;
        }

        List<Libro> todosLosLibros = libroDAO.listar();
        List<Libro> librosFiltrados = new ArrayList<>();

        for (Libro l : todosLosLibros) {
            if (l.getTitulo().toLowerCase().contains(texto) || l.getAutor().toLowerCase().contains(texto)) {
                librosFiltrados.add(l);
            }
        }

        listaLibros = FXCollections.observableArrayList(librosFiltrados);
        tblLibros.setItems(listaLibros);
    }

    @FXML
    private void limpiarFormulario() {
        txtTitulo.clear();
        cmbAutor.setValue(null);
        cmbCategoria.setValue(null);
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