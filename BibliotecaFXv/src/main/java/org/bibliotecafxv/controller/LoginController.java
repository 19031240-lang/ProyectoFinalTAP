package org.bibliotecafxv.controller;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.*;
import org.bibliotecafxv.dao.UsuarioDAO;
import org.bibliotecafxv.model.Usuario;
import org.bibliotecafxv.util.HashUtil;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;



public class LoginController {
    @FXML private TextField txtCorreo;
    @FXML private PasswordField txtPassword;
    @FXML private Label lblMensaje;

    @FXML
    public void login() {
        try {
            UsuarioDAO dao = new UsuarioDAO();
            Usuario usuario = dao.buscarPorCorreo(txtCorreo.getText());

            if (usuario == null) {
                lblMensaje.setText("Usuario no existe");
                return;
            }

            String hash = HashUtil.sha1(txtPassword.getText());

            if (hash.equals(usuario.getPassword())) {

                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/org/bibliotecafxv/view/dashboard.fxml")
                );

                Parent root = loader.load();

                Stage stage = (Stage) txtCorreo.getScene().getWindow();
                stage.getScene().setRoot(root);

            } else {
                lblMensaje.setText("Contraseña incorrecta ");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void irRegistro() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/org/bibliotecafxv/view/registro.fxml")
            );

            Stage stage = (Stage) txtCorreo.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}