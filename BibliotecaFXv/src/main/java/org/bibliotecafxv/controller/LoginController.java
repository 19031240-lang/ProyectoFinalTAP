package org.bibliotecafxv.controller;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.*;
import org.bibliotecafxv.dao.UsuarioDAO;
import org.bibliotecafxv.model.Usuario;
import org.bibliotecafxv.util.HashUtil;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Controlador de la interfaz gráfica para la pantalla de Inicio de Sesión (Login).
 * Se encarga de capturar las credenciales del usuario, validar su autenticidad
 * mediante la capa de datos y redirigir al usuario a la vista correspondiente según su rol.
 */
public class LoginController {

    @FXML private TextField txtCorreo;
    @FXML private PasswordField txtPassword;
    @FXML private Label lblMensaje;

    /**
     * Gestiona el proceso de autenticación cuando el usuario presiona el botón de ingresar.
     * Recupera el usuario por correo, encripta la contraseña ingresada en pantalla usando SHA-1
     * y comprueba si coincide con el registro de la base de datos.
     * Redirecciona a la vista de Administrador (Dashboard) o de Usuario (Catálogo) correspondientemente.
     */
    @FXML
    public void login() {
        try {
            UsuarioDAO dao = new UsuarioDAO();
            Usuario usuario = dao.buscarPorCorreo(txtCorreo.getText());

            if (usuario == null) {
                lblMensaje.setText("Usuario no existe");
                return;
            }

            // Encriptamos la contraseña de la pantalla para compararla con la de la BD
            String hash = HashUtil.sha1(txtPassword.getText());

            if (hash.equals(usuario.getPassword())) {

                String vistaDestino;
                // Enrutamiento dinámico basado en Roles de Acceso
                if ("ADMIN".equals(usuario.getRol())) {
                    vistaDestino = "/org/bibliotecafxv/view/dashboard.fxml";
                } else {
                    vistaDestino = "/org/bibliotecafxv/view/catalogo_usuario.fxml";
                }

                FXMLLoader loader = new FXMLLoader(getClass().getResource(vistaDestino));
                Parent root = loader.load();

                // Inyección de dependencias de sesión: Pasamos el usuario logueado a su catálogo
                if ("USER".equals(usuario.getRol())) {
                    org.bibliotecafxv.controller.CatalogoUsuarioController controller = loader.getController();
                    controller.setUsuarioLogueado(usuario);
                }

                // Transición física de la ventana de JavaFX
                Stage stage = (Stage) txtCorreo.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setMaximized(true);
                stage.show();

            } else {
                lblMensaje.setText("Contraseña incorrecta ");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Cambia el escenario actual cargando la vista de registro de nuevos usuarios.
     * Se dispara al hacer clic en el enlace o botón de registrarse.
     */
    @FXML
    public void irRegistro() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/org/bibliotecafxv/view/registro.fxml")
            );

            Stage stage = (Stage) txtCorreo.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
            stage.setMaximized(true);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}