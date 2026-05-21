package org.bibliotecafxv.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.bibliotecafxv.dao.AutorDAO;
import org.bibliotecafxv.model.Autor;

/**
 * Controlador de la interfaz gráfica para la administración del catálogo de Autores.
 * Proporciona las operaciones CRUD básicas (Crear, Leer, Actualizar, Eliminar) interactuando
 * de manera directa entre los componentes de JavaFX (TableView, TextField) y la capa de datos (AutorDAO).
 */
public class AutorController {

    @FXML private TextField txtNombre;
    @FXML private TableView<Autor> tblAutores;
    @FXML private TableColumn<Autor, Integer> colId;
    @FXML private TableColumn<Autor, String> colNombre;

    private AutorDAO autorDAO;
    private ObservableList<Autor> listaAutores;
    private Autor autorSeleccionado;

    /**
     * Inicializa el controlador de forma automática tras cargar el archivo FXML.
     * Configura el mapeo de las columnas de la tabla, carga los registros desde la base de datos
     * y añade un escucha de selección para rellenar el formulario dinámicamente al hacer clic en una fila.
     */
    @FXML
    public void initialize() {
        autorDAO = new AutorDAO();
        configurarTabla();
        cargarDatos();

        // Escucha de selección en la tabla (Sincroniza vista-modelo)
        tblAutores.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> seleccionarAutor(newSelection)
        );
    }

    /**
     * Define la correspondencia de las columnas de la tabla con los atributos del modelo Autor.
     */
    private void configurarTabla() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
    }

    /**
     * Consulta los registros actualizados en la base de datos y refresca el contenido de la TableView.
     */
    private void cargarDatos() {
        listaAutores = FXCollections.observableArrayList(autorDAO.listar());
        tblAutores.setItems(listaAutores);
    }

    /**
     * Captura el autor seleccionado de la tabla y traslada sus atributos al campo de texto del formulario.
     * @param autor Objeto Autor seleccionado por el usuario, o null si se limpia la selección.
     */
    private void seleccionarAutor(Autor autor) {
        if (autor != null) {
            autorSeleccionado = autor;
            txtNombre.setText(autor.getNombre());
        }
    }

    /**
     * Procesa y valida el registro de un nuevo autor en el sistema.
     * Verifica que no existan campos vacíos ni selecciones activas para evitar sobreescrituras accidentales.
     */
    @FXML
    private void guardarAutor() {
        if (txtNombre.getText().trim().isEmpty()) {
            mostrarAlerta("Error", "El nombre es obligatorio.", Alert.AlertType.ERROR);
            return;
        }
        if (autorSeleccionado != null) {
            mostrarAlerta("Atención", "Hay un autor seleccionado. Usa 'Actualizar' o 'Limpiar'.", Alert.AlertType.WARNING);
            return;
        }

        Autor nuevo = new Autor();
        nuevo.setNombre(txtNombre.getText().trim());

        if (autorDAO.guardar(nuevo)) {
            mostrarAlerta("Éxito", "Autor guardado.", Alert.AlertType.INFORMATION);
            limpiarFormulario();
            cargarDatos();
        }
    }

    /**
     * Modifica los datos del autor que se encuentra seleccionado actualmente en la interfaz.
     */
    @FXML
    private void actualizarAutor() {
        if (autorSeleccionado == null) {
            mostrarAlerta("Atención", "Selecciona un autor de la tabla.", Alert.AlertType.WARNING);
            return;
        }
        if (txtNombre.getText().trim().isEmpty()) {
            mostrarAlerta("Error", "El nombre es obligatorio.", Alert.AlertType.ERROR);
            return;
        }

        autorSeleccionado.setNombre(txtNombre.getText().trim());

        if (autorDAO.actualizar(autorSeleccionado)) {
            mostrarAlerta("Éxito", "Autor actualizado.", Alert.AlertType.INFORMATION);
            limpiarFormulario();
            cargarDatos();
        }
    }

    /**
     * Elimina el registro del autor seleccionado previa confirmación explícita del usuario.
     * Controla las excepciones de integridad referencial si el autor posee libros vinculados en la BD.
     */
    @FXML
    private void eliminarAutor() {
        if (autorSeleccionado == null) {
            mostrarAlerta("Atención", "Selecciona un autor de la tabla.", Alert.AlertType.WARNING);
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "¿Seguro que deseas eliminar este autor?");
        if (confirm.showAndWait().get() == ButtonType.OK) {
            if (autorDAO.eliminar(autorSeleccionado.getId())) {
                mostrarAlerta("Éxito", "Autor eliminado.", Alert.AlertType.INFORMATION);
                limpiarFormulario();
                cargarDatos();
            } else {
                mostrarAlerta("Error", "No se puede eliminar. Es probable que tenga libros registrados.", Alert.AlertType.ERROR);
            }
        }
    }

    /**
     * Restablece el estado de los componentes visuales y limpia las variables de selección temporal.
     */
    @FXML
    private void limpiarFormulario() {
        txtNombre.clear();
        autorSeleccionado = null;
        tblAutores.getSelectionModel().clearSelection();
    }

    /**
     * Centraliza la creación y despliegue de ventanas emergentes (diálogos) informativas o de error.
     * @param titulo Encabezado de la ventana.
     * @param contenido Mensaje explicativo para el usuario.
     * @param tipo Nivel de gravedad del diálogo (ERROR, WARNING, INFORMATION, etc.)
     */
    private void mostrarAlerta(String titulo, String contenido, Alert.AlertType tipo) {
        Alert a = new Alert(tipo);
        a.setTitle(titulo);
        a.setHeaderText(null);
        a.setContentText(contenido);
        a.showAndWait();
    }
}