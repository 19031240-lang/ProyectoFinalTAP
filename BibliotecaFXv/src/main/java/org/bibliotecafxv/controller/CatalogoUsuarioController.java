package org.bibliotecafxv.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import org.bibliotecafxv.dao.LibroDAO;
import org.bibliotecafxv.dao.PrestamoDAO;
import org.bibliotecafxv.model.Libro;
import org.bibliotecafxv.model.Prestamo;
import org.bibliotecafxv.model.Usuario;

import java.time.LocalDate;
import java.util.List;

/**
 * Controlador de la interfaz gráfica para el Catálogo Público que visualizan los usuarios finales.
 * Construye de forma dinámica tarjetas visuales para los libros, despliega paneles informativos detallados
 * y procesa las solicitudes de préstamos.
 * (Nota: Las notificaciones del patrón Observer se delegan automáticamente a la capa DAO).
 */
public class CatalogoUsuarioController {

    @FXML private FlowPane contenedorLibros;
    @FXML private VBox panelDetalles;
    @FXML private ImageView imgPortadaDetalle;
    @FXML private Label lblTituloDetalle;
    @FXML private Label lblAutorDetalle;
    @FXML private Label lblCategoriaDetalle;
    @FXML private Label lblDescripcionDetalle;
    @FXML private Label lblEstadoSms;
    @FXML private Button btnSolicitar;

    private LibroDAO libroDAO;
    private PrestamoDAO prestamoDAO;
    private Libro libroSeleccionado;
    private Usuario usuarioLogueado;

    /**
     * Inyecta externamente la información del usuario que ha iniciado sesión en el sistema.
     * @param usuario Instancia del usuario autenticado.
     */
    public void setUsuarioLogueado(Usuario usuario) {
        this.usuarioLogueado = usuario;
    }

    /**
     * Inicializa los componentes y oculta el panel lateral de detalles antes de renderizar los libros.
     */
    @FXML
    public void initialize() {
        libroDAO = new LibroDAO();
        prestamoDAO = new PrestamoDAO();

        panelDetalles.setVisible(false);
        cargarCatalogo();
    }

    /**
     * Consulta la colección total de libros y genera de forma iterativa las tarjetas visuales dentro del FlowPane.
     */
    private void cargarCatalogo() {
        contenedorLibros.getChildren().clear();
        List<Libro> libros = libroDAO.listar();

        for (Libro libro : libros) {
            VBox card = crearCardLibro(libro);
            contenedorLibros.getChildren().add(card);
        }
    }

    /**
     * Construye de manera dinámica el componente visual (Card) para representar a un libro.
     * Agrega estilos CSS personalizados, formatea la imagen con bordes redondeados y asigna los eventos del ratón.
     * @param libro Datos del libro a maquetar.
     * @return Contenedor VBox estructurado con el diseño de la tarjeta.
     */
    private VBox crearCardLibro(Libro libro) {
        VBox card = new VBox(10);
        card.getStyleClass().add("book-card");
        card.setPrefWidth(160);

        // Evento que desencadena la visualización detallada lateral al hacer clic
        card.setOnMouseClicked(event -> mostrarDetalles(libro));

        ImageView imageView = new ImageView();
        try {
            if (libro.getPortada() != null && !libro.getPortada().isEmpty()) {
                Image image = new Image(libro.getPortada(), true);
                imageView.setImage(image);
            }
        } catch (Exception e) {
            System.out.println("Sin imagen: " + libro.getTitulo());
        }

        imageView.setFitWidth(130);
        imageView.setFitHeight(180);
        imageView.setPreserveRatio(false);
        Rectangle clip = new Rectangle(130, 180);
        clip.setArcWidth(15);
        clip.setArcHeight(15);
        imageView.setClip(clip);

        Label lblTitulo = new Label(libro.getTitulo());
        lblTitulo.getStyleClass().add("book-title");
        lblTitulo.setWrapText(true);

        Label lblAutor = new Label(libro.getAutor());
        lblAutor.getStyleClass().add("book-author");

        Label lblEstado = new Label(libro.isDisponible() ? "Disponible" : "Agotado");
        lblEstado.getStyleClass().add(libro.isDisponible() ? "badge-disponible" : "badge-prestado");

        card.getChildren().addAll(imageView, lblTitulo, lblAutor, lblEstado);
        return card;
    }

    /**
     * Hace visible el panel lateral y vuelca toda la información descriptiva y portadas del libro seleccionado.
     * Habilita o deshabilita los controles de adquisición según la disponibilidad física del ejemplar.
     * @param libro Objeto libro seleccionado de la cuadrícula.
     */
    private void mostrarDetalles(Libro libro) {
        this.libroSeleccionado = libro;
        panelDetalles.setVisible(true);

        lblTituloDetalle.setText(libro.getTitulo());
        lblAutorDetalle.setText("Autor: " + libro.getAutor());
        lblCategoriaDetalle.setText("Categoría: " + libro.getCategoria());

        if (libro.getDescripcion() != null && !libro.getDescripcion().trim().isEmpty()) {
            lblDescripcionDetalle.setText(libro.getDescripcion());
        } else {
            lblDescripcionDetalle.setText("No hay una sinopsis disponible para este libro.");
        }

        try {
            if (libro.getPortada() != null && !libro.getPortada().isEmpty()) {
                imgPortadaDetalle.setImage(new Image(libro.getPortada(), true));
            } else {
                imgPortadaDetalle.setImage(null);
            }
        } catch (Exception e) {}

        if (libro.isDisponible()) {
            lblEstadoSms.setText("¡Este libro está disponible!");
            lblEstadoSms.setStyle("-fx-text-fill: #27ae60;");
            btnSolicitar.setDisable(false);
        } else {
            lblEstadoSms.setText("Actualmente prestado");
            lblEstadoSms.setStyle("-fx-text-fill: #e74c3c;");
            btnSolicitar.setDisable(true);
        }
    }

    /**
     * Genera la transacción de un nuevo préstamo con una vigencia estándar de 7 días naturales.
     * Modifica el estado de disponibilidad del ejemplar en la BD y delega la ejecución del
     * Patrón Observer internamente a la clase PrestamoDAO.
     */
    @FXML
    private void solicitarPrestamo() {
        if (libroSeleccionado == null || usuarioLogueado == null) return;

        try {
            LocalDate hoy = LocalDate.now();
            LocalDate limite = hoy.plusDays(7);

            Prestamo nuevoPrestamo = new Prestamo(
                    0,
                    usuarioLogueado.getId(),
                    libroSeleccionado.getId(),
                    java.sql.Date.valueOf(hoy),
                    java.sql.Date.valueOf(limite),
                    "ACTIVO"
            );

            prestamoDAO.guardar(nuevoPrestamo);

            // Sincronización del stock y estado del libro
            libroSeleccionado.setDisponible(false);
            libroDAO.actualizar(libroSeleccionado);

            panelDetalles.setVisible(false);
            cargarCatalogo();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Préstamo Exitoso");
            alert.setHeaderText("¡Disfruta tu lectura!");
            alert.setContentText("Tienes hasta el " + limite.toString() + " para devolver '" + libroSeleccionado.getTitulo() + "'.");
            alert.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("No se pudo procesar el préstamo.");
            alert.showAndWait();
        }
    }

    /**
     * Cierra el espacio de trabajo del catálogo y regresa la aplicación a la pantalla de Login.
     * @param event Acción disparada por el botón correspondiente.
     */
    @FXML
    private void cerrarSesion(javafx.event.ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/bibliotecafxv/view/login.fxml"));
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
            stage.setMaximized(true);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}