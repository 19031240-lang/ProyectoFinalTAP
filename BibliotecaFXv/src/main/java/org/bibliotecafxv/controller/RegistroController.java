package org.bibliotecafxv.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.bibliotecafxv.dao.UsuarioDAO;
import org.bibliotecafxv.model.Usuario;
import org.bibliotecafxv.util.HashUtil;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

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
        dao.guardar(u);

        lblMensaje.setText("Usuario registrado");
    }

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