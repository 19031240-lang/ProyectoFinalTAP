package org.bibliotecafxv.controller;

import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.util.Duration;
import org.bibliotecafxv.dao.UsuarioDAO;
import org.bibliotecafxv.model.Usuario;
import org.bibliotecafxv.util.HashUtil;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.event.ActionEvent;


public class RegistroController {

    @FXML private TextField txtNombre;
    @FXML private TextField txtCorreo;
    @FXML private PasswordField txtPassword;
    @FXML private Label lblMensaje;

    @FXML
    public void registrar() {

        UsuarioDAO dao = new UsuarioDAO();

        if (dao.buscarPorCorreo(txtCorreo.getText()) != null) {
            lblMensaje.setText("Correo ya existe");
            return;
        }

        String hash = HashUtil.sha1(txtPassword.getText());

        Usuario u = new Usuario(0,
                txtNombre.getText(),
                txtCorreo.getText(),
                hash,
                "USER"
        );

        dao.insertar(u);

        lblMensaje.setText("Usuario registrado");
    }

    @FXML
    public void volverLogin(javafx.event.ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/org/bibliotecafxv/view/login.fxml")
                );
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Parent currentRoot = stage.getScene().getRoot();

            // Posicion inicial del nuevo root (fuera de la pantalla a la derecha)
            root.setTranslateX(stage.getWidth());

            // Animación del actual (se va a la izquierda)
            TranslateTransition slideOut = new TranslateTransition(Duration.millis(300), currentRoot);
            slideOut.setToX(-stage.getWidth());

            // Animación del nuevo (entra desde la derecha)
            TranslateTransition slideIn = new TranslateTransition(Duration.millis(300), root);
            slideIn.setToX(0);

            slideOut.setOnFinished(e -> {
                stage.getScene().setRoot(root);
                slideIn.play();
                });
            slideOut.play();

            } catch (Exception e) {
                e.printStackTrace();
            }
    }
}