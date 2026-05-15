package org.bibliotecafxv.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.bibliotecafxv.dao.UsuarioDAO;
import org.bibliotecafxv.model.Usuario;
import org.bibliotecafxv.util.HashUtil;

public class UsuarioController {

    @FXML private TextField txtNombre;
    @FXML private TextField txtCorreo;
    @FXML private PasswordField txtPassword;
    @FXML private ComboBox<String> cmbRol;

    @FXML private TableView<Usuario> tablaUsuarios;
    @FXML private TableColumn<Usuario, Integer> colId;
    @FXML private TableColumn<Usuario, String> colNombre;
    @FXML private TableColumn<Usuario, String> colCorreo;
    @FXML private TableColumn<Usuario, String> colRol;

    private UsuarioDAO usuarioDAO;
    private ObservableList<Usuario> listaUsuarios;
    private Usuario usuarioSeleccionado;

    @FXML
    public void initialize() {

        usuarioDAO = new UsuarioDAO();

        cmbRol.getSelectionModel().select("USER");

        configurarTabla();
        cargarDatos();

        tablaUsuarios.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> seleccionarUsuario(newValue)
        );
    }

    private void configurarTabla() {

        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colCorreo.setCellValueFactory(new PropertyValueFactory<>("correo"));
        colRol.setCellValueFactory(new PropertyValueFactory<>("rol"));
    }

    private void cargarDatos() {

        listaUsuarios = FXCollections.observableArrayList(usuarioDAO.listar());
        tablaUsuarios.setItems(listaUsuarios);
    }

    private void seleccionarUsuario(Usuario usuario) {

        if (usuario != null) {

            usuarioSeleccionado = usuario;

            txtNombre.setText(usuario.getNombre());
            txtCorreo.setText(usuario.getCorreo());
            txtPassword.clear();
            txtPassword.setPromptText("Dejar vacío para no cambiar");

            cmbRol.setValue(usuario.getRol());
        }
    }

    @FXML
    private void guardarUsuario() {

        // Si hay un usuario seleccionado, avisa que use Actualizar
        if (usuarioSeleccionado != null) {
            mostrarAlerta(
                    "Atención",
                    "Hay un usuario seleccionado. Usa 'Actualizar' para modificarlo o 'Limpiar' para crear uno nuevo.",
                    Alert.AlertType.WARNING
            );
            return;
        }

        if (txtNombre.getText().isEmpty()
                || txtCorreo.getText().isEmpty()
                || txtPassword.getText().isEmpty()) {

            mostrarAlerta(
                    "Error",
                    "Todos los campos son obligatorios para registrar un usuario.",
                    Alert.AlertType.ERROR
            );
            return;
        }

        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setNombre(txtNombre.getText());
        nuevoUsuario.setCorreo(txtCorreo.getText());
        nuevoUsuario.setPassword(HashUtil.sha1(txtPassword.getText()));
        nuevoUsuario.setRol(cmbRol.getValue());

        if (usuarioDAO.guardar(nuevoUsuario)) {

            mostrarAlerta(
                    "Éxito",
                    "Usuario registrado correctamente.",
                    Alert.AlertType.INFORMATION
            );

            limpiarFormulario();
            cargarDatos();

        } else {

            mostrarAlerta(
                    "Error",
                    "No se pudo guardar el usuario.",
                    Alert.AlertType.ERROR
            );
        }
    }

    @FXML
    private void actualizarUsuario() {

        if (usuarioSeleccionado == null) {

            mostrarAlerta(
                    "Atención",
                    "Selecciona un usuario de la tabla para actualizarlo.",
                    Alert.AlertType.WARNING
            );
            return;
        }

        if (txtNombre.getText().isEmpty() || txtCorreo.getText().isEmpty()) {

            mostrarAlerta(
                    "Error",
                    "El nombre y el correo son obligatorios.",
                    Alert.AlertType.ERROR
            );
            return;
        }

        usuarioSeleccionado.setNombre(txtNombre.getText());
        usuarioSeleccionado.setCorreo(txtCorreo.getText());
        usuarioSeleccionado.setRol(cmbRol.getValue());

        if (!txtPassword.getText().isEmpty()) {
            usuarioSeleccionado.setPassword(HashUtil.sha1(txtPassword.getText()));
        } else {
            usuarioSeleccionado.setPassword(null);
        }

        if (usuarioDAO.actualizar(usuarioSeleccionado)) {

            mostrarAlerta(
                    "Éxito",
                    "Usuario actualizado correctamente.",
                    Alert.AlertType.INFORMATION
            );

            cargarDatos();
            limpiarFormulario();

        } else {

            mostrarAlerta(
                    "Error",
                    "No se pudo actualizar el usuario.",
                    Alert.AlertType.ERROR
            );
        }
    }

    @FXML
    private void eliminarUsuario() {

        Usuario seleccionado = tablaUsuarios.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {

            mostrarAlerta(
                    "Atención",
                    "Selecciona un usuario de la tabla.",
                    Alert.AlertType.WARNING
            );
            return;
        }

        if (seleccionado.getId() == 1) {

            mostrarAlerta(
                    "Error",
                    "No se puede eliminar al admin principal.",
                    Alert.AlertType.ERROR
            );
            return;
        }

        if (usuarioDAO.eliminar(seleccionado.getId())) {

            mostrarAlerta(
                    "Éxito",
                    "Usuario eliminado.",
                    Alert.AlertType.INFORMATION
            );

            cargarDatos();
            limpiarFormulario();
        }
    }

    @FXML
    private void limpiarFormulario() {

        txtNombre.clear();
        txtCorreo.clear();
        txtPassword.clear();
        txtPassword.setPromptText("Nueva contraseña");

        cmbRol.getSelectionModel().select("USER");

        usuarioSeleccionado = null;

        tablaUsuarios.getSelectionModel().clearSelection();
    }

    private void mostrarAlerta(String titulo,
                               String contenido,
                               Alert.AlertType tipo) {

        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(contenido);
        alert.showAndWait();
    }
}