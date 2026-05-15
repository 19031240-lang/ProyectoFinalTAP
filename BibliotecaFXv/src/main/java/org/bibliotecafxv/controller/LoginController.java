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

                String vistaDestino;
                if ("ADMIN".equals(usuario.getRol())) {
                    vistaDestino = "/org/bibliotecafxv/view/dashboard.fxml";
                } else {
                    vistaDestino = "/org/bibliotecafxv/view/catalogo_usuario.fxml";
                }

                FXMLLoader loader = new FXMLLoader(getClass().getResource(vistaDestino));
                Parent root = loader.load();

                // --- AQUÍ PASAMOS EL USUARIO AL CATÁLOGO ---
                if ("USER".equals(usuario.getRol())) {
                    org.bibliotecafxv.controller.CatalogoUsuarioController controller = loader.getController();
                    controller.setUsuarioLogueado(usuario);
                }

                Stage stage = (Stage) txtCorreo.getScene().getWindow();
                stage.getScene().setRoot(root);
                stage.setMaximized(true);

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
            stage.setMaximized(true);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}