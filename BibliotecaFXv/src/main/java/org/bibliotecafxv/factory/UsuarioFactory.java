package org.bibliotecafxv.factory;

import org.bibliotecafxv.model.Usuario;

public class UsuarioFactory {

    public static Usuario crearUsuario(String rol) {

        Usuario usuario = new Usuario();

        usuario.setRol(rol);

        return usuario;
    }
}