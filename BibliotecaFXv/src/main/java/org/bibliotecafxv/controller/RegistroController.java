package org.bibliotecafxv.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.bibliotecafxv.dao.UsuarioDAO;
import org.bibliotecafxv.model.Usuario;
import org.bibliotecafxv.util.HashUtil;
import org.bibliotecafxv.factory.UsuarioFactory;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Controlador de la interfaz gráfica para la pantalla de Registro de Usuarios.
 * Coordina la captura de datos de nuevos clientes, asegurando que no existan correos
 * duplicados y utilizando la fábrica de objetos para estandarizar la creación de cuentas.
 */
public class RegistroController {

    @FXML private TextField txtNombre;
    @FXML private TextField txtCorreo;
    @FXML private PasswordField txtPassword;
    @FXML private Label lblMensaje;

    /**
     * Procesa el formulario de inscripción de un nuevo usuario en la plataforma.
     * Valida que el correo electrónico ingresado no se encuentre registrado previamente.
     * Genera el hash SHA-1 de la contraseña y delega la instanciación al patrón **UsuarioFactory** * asignando de forma automática el rol "USER".
     */
    @FXML
    public void registrar() {

        UsuarioDAO dao = new UsuarioDAO();

        // Validación preventiva de unicidad de cuentas
        if (dao.buscarPorCorreo(txtCorreo.getText()) != null) {
            lblMensaje.setText("Correo ya existe");
            return;
        }

        // Criptografía de la contraseña del nuevo usuario antes del guardado definitivo
        String hash = HashUtil.sha1(txtPassword.getText());

        // --- Aplicación del Patrón de Diseño Factory ---
        Usuario u = UsuarioFactory.crearUsuario(
                "USER",
                txtNombre.getText(),
                txtCorreo.getText(),
                hash
        );

        // Envío del objeto construido por la fábrica hacia la persistencia en base de datos
        dao.guardar(u);

        lblMensaje.setText("Usuario registrado");
    }

    /**
     * Cierra el flujo de registro y regresa al usuario a la pantalla de Login tradicional.
     */
    @FXML
    public void volverLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/org/bibliotecafxv/view/login.fxml")
            );

            Stage stage = (Stage)  txtCorreo.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
            stage.setMaximized(true);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}