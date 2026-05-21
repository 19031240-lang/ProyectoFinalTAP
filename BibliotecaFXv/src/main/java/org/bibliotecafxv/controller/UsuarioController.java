package org.bibliotecafxv.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.bibliotecafxv.dao.UsuarioDAO;
import org.bibliotecafxv.factory.UsuarioFactory;
import org.bibliotecafxv.model.Usuario;
import org.bibliotecafxv.util.HashUtil;

/**
 * Controlador para la administración de cuentas y accesos al sistema.
 * Gestiona el alta, baja y modificación de usuarios administradores y clientes regulares,
 * integrando medidas de seguridad criptográfica (SHA-1) para el tratamiento de las contraseñas.
 */
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

    /**
     * Refleja los datos de la cuenta seleccionada en el formulario para edición.
     * Deja el campo de contraseña en blanco por seguridad para forzar un reingreso manual.
     */
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
        if (usuarioSeleccionado != null) {
            mostrarAlerta("Atención", "Hay un usuario seleccionado. Usa 'Actualizar'...", Alert.AlertType.WARNING);
            return;
        }

        if (txtNombre.getText().isEmpty() || txtCorreo.getText().isEmpty() || txtPassword.getText().isEmpty()) {
            mostrarAlerta("Error", "Todos los campos son obligatorios.", Alert.AlertType.ERROR);
            return;
        }

        String passwordCifrada = HashUtil.sha1(txtPassword.getText());

        Usuario nuevoUsuario = UsuarioFactory.crearUsuario(
                cmbRol.getValue(),
                txtNombre.getText(),
                txtCorreo.getText(),
                passwordCifrada
        );
        if (usuarioDAO.guardar(nuevoUsuario)) {
            mostrarAlerta("Éxito", "Usuario registrado correctamente.", Alert.AlertType.INFORMATION);
            limpiarFormulario();
            cargarDatos();
        } else {
            mostrarAlerta("Error", "No se pudo guardar el usuario.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void actualizarUsuario() {
        if (usuarioSeleccionado == null) {
            mostrarAlerta("Atención", "Selecciona un usuario de la tabla para actualizarlo.", Alert.AlertType.WARNING);
            return;
        }

        if (txtNombre.getText().isEmpty() || txtCorreo.getText().isEmpty()) {
            mostrarAlerta("Error", "El nombre y el correo son obligatorios.", Alert.AlertType.ERROR);
            return;
        }

        usuarioSeleccionado.setNombre(txtNombre.getText());
        usuarioSeleccionado.setCorreo(txtCorreo.getText());
        usuarioSeleccionado.setRol(cmbRol.getValue());

        // Solo se cifra y actualiza la contraseña si el campo no se dejó vacío
        if (!txtPassword.getText().isEmpty()) {
            usuarioSeleccionado.setPassword(HashUtil.sha1(txtPassword.getText()));
        } else {
            usuarioSeleccionado.setPassword(null);
        }

        if (usuarioDAO.actualizar(usuarioSeleccionado)) {
            mostrarAlerta("Éxito", "Usuario actualizado correctamente.", Alert.AlertType.INFORMATION);
            cargarDatos();
            limpiarFormulario();
        } else {
            mostrarAlerta("Error", "No se pudo actualizar el usuario.", Alert.AlertType.ERROR);
        }
    }

    /**
     * Elimina el acceso del usuario, validando como medida de seguridad que
     * el "Super Administrador" principal (ID = 1) no pueda ser borrado accidentalmente.
     */
    @FXML
    private void eliminarUsuario() {
        Usuario seleccionado = tablaUsuarios.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            mostrarAlerta("Atención", "Selecciona un usuario de la tabla.", Alert.AlertType.WARNING);
            return;
        }

        if (seleccionado.getId() == 1) {
            mostrarAlerta("Error", "No se puede eliminar al admin principal.", Alert.AlertType.ERROR);
            return;
        }

        if (usuarioDAO.eliminar(seleccionado.getId())) {
            mostrarAlerta("Éxito", "Usuario eliminado.", Alert.AlertType.INFORMATION);
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

    private void mostrarAlerta(String titulo, String contenido, Alert.AlertType tipo) {
        Alert a = new Alert(tipo);
        a.setTitle(titulo);
        a.setHeaderText(null);
        a.setContentText(contenido);
        a.showAndWait();
    }
}