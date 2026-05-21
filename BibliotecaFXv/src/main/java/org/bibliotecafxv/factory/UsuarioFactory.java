package org.bibliotecafxv.factory;

import org.bibliotecafxv.model.Usuario;

/**
 * Fábrica encargada de la creación y parametrización de objetos Usuario.
 * Implementa el **Patrón de Diseño Factory** para encapsular y centralizar la lógica
 * de asignación de roles de acceso (ADMIN o USER) al momento de registrar usuarios.
 */
public class UsuarioFactory {

    /**
     * Crea e instancia un nuevo objeto Usuario con el rol adecuado procesado dinámicamente.
     * * @param rol El rol solicitado en texto (ej. "admin", "user").
     * @param nombre El nombre completo del usuario.
     * @param correo Dirección de correo electrónico única.
     * @param passwordHash Contraseña del usuario previamente encriptada.
     * @return Un objeto Usuario configurado listo para ser procesado por el DAO.
     */
    public static Usuario crearUsuario(String rol, String nombre, String correo, String passwordHash) {
        if ("ADMIN".equalsIgnoreCase(rol)) {
            return new Usuario(0, nombre, correo, passwordHash, "ADMIN");
        } else {
            return new Usuario(0, nombre, correo, passwordHash, "USER");
        }
    }
}