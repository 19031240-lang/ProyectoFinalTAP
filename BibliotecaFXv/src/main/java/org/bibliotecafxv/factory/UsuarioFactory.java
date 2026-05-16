package org.bibliotecafxv.factory;

import org.bibliotecafxv.model.Usuario;

public class UsuarioFactory {

    public static Usuario crearUsuario(String rol, String nombre, String correo, String passwordHash) {
        if ("ADMIN".equalsIgnoreCase(rol)) {
            return new Usuario(0, nombre, correo, passwordHash, "ADMIN");
        } else {
            return new Usuario(0, nombre, correo, passwordHash, "USER");
        }
    }
}