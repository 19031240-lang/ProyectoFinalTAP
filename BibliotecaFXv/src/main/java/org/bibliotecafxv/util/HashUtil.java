package org.bibliotecafxv.util;
import java.security.MessageDigest;

/**
 * Componente de soporte encargado de la seguridad y criptografía del sistema.
 * Centraliza las funciones de hashing para garantizar que información crítica como las contraseñas
 * nunca se almacene ni procese en texto plano dentro de la base de datos.
 */
public class HashUtil {

    /**
     * Procesa una cadena de texto convencional y la transforma en una huella digital criptográfica (Hash)
     * utilizando el algoritmo robusto SHA-1.
     * @param input El texto plano original (por ejemplo, la contraseña escrita por el usuario).
     * @return Una representación de 40 caracteres en formato hexadecimal que identifica unívocamente al texto ingresado.
     * @throws RuntimeException Si el entorno de ejecución no soporta o no encuentra el algoritmo SHA-1.
     */
    public static String sha1(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] result = md.digest(input.getBytes());

            StringBuilder sb = new StringBuilder();
            for (byte b : result) {
                sb.append(String.format("%02x", b));
            }

            return sb.toString();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}