package org.bibliotecafxv.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import org.bibliotecafxv.dao.LibroDAO;
import org.bibliotecafxv.dao.PrestamoDAO;
import org.bibliotecafxv.dao.UsuarioDAO;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;

public class DashboardController {

    // Contenedores principales
    @FXML private BorderPane rootPane;
    @FXML private VBox contentPane;
    @FXML private VBox panelDerecho; // <-- NUEVA VARIABLE

    // Tabla
    @FXML private TableView<org.bibliotecafxv.model.Prestamo> tablaPrestamos;
    @FXML private TableColumn<org.bibliotecafxv.model.Prestamo, String> colUsuario;
    @FXML private TableColumn<org.bibliotecafxv.model.Prestamo, String> colLibro;
    @FXML private TableColumn<org.bibliotecafxv.model.Prestamo, String> colFecha;

    // Gráfica y otros
    @FXML private BarChart<String, Number> graficaPrestamos;
    @FXML private Label lblTotalLibros;
    @FXML private Label lblTotalUsuarios;
    @FXML private Label lblTotalPrestamos;
    @FXML private javafx.scene.layout.HBox contenedorLibros;
    @FXML private javafx.scene.layout.VBox contenedorCalendario;

    private LibroDAO libroDAO;
    private UsuarioDAO usuarioDAO;
    private PrestamoDAO prestamoDAO;

    @FXML
    public void initialize() {
        libroDAO = new LibroDAO();
        usuarioDAO = new UsuarioDAO();
        prestamoDAO = new PrestamoDAO();

        cargarEstadisticas();
        cargarLibrosDestacados();
        crearCalendario();
        configurarTabla();
        cargarDatosTabla();
        configurarGrafica();
    }

    private void configurarTabla() {
        colUsuario.setCellValueFactory(new PropertyValueFactory<>("nombreUsuario"));
        colLibro.setCellValueFactory(new PropertyValueFactory<>("tituloLibro"));
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fechaDevolucion"));
    }

    private void cargarDatosTabla() {
        java.util.List<org.bibliotecafxv.model.Prestamo> lista = prestamoDAO.listarRecientes();
        tablaPrestamos.setItems(FXCollections.observableArrayList(lista));
    }

    private void configurarGrafica() {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.getData().add(new XYChart.Data<>("Lun", 5));
        series.getData().add(new XYChart.Data<>("Mar", 8));
        series.getData().add(new XYChart.Data<>("Mie", 15));
        series.getData().add(new XYChart.Data<>("Jue", 12));
        series.getData().add(new XYChart.Data<>("Vie", 20));

        graficaPrestamos.getData().add(series);
    }

    private void cargarEstadisticas() {
        int libros = libroDAO.contarTotalLibros();
        int usuarios = usuarioDAO.contarTotalUsuarios();
        int prestamos = prestamoDAO.contarPrestamosActivos();
        if (lblTotalLibros != null) lblTotalLibros.setText(String.valueOf(libros));
        if (lblTotalUsuarios != null) lblTotalUsuarios.setText(String.valueOf(usuarios));
        if (lblTotalPrestamos != null) lblTotalPrestamos.setText(String.valueOf(prestamos));
    }

    // --- MÉTODOS DE NAVEGACIÓN ---

    @FXML
    private void abrirDashboard() {
        rootPane.setCenter(contentPane);
        panelDerecho.setVisible(true);
        panelDerecho.setManaged(true);
        cargarEstadisticas();
        cargarDatosTabla();
        cargarLibrosDestacados();
        crearCalendario();
    }

    @FXML
    private void abrirLibros() {
        cargarVista("/org/bibliotecafxv/view/libros.fxml");
    }

    @FXML
    private void abrirUsuarios() {
        cargarVista("/org/bibliotecafxv/view/usuarios.fxml");
    }

    @FXML
    private void abrirPrestamos() {
        cargarVista("/org/bibliotecafxv/view/prestamos.fxml");
    }

    @FXML
    private void abrirReportes() {
        cargarVista("/org/bibliotecafxv/view/reportes.fxml");
    }

    @FXML
    private void abrirReservas() {
        cargarVista("/org/bibliotecafxv/view/reservas.fxml");
    }

    private void cargarVista(String ruta) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(ruta));
            Parent vista = loader.load();
            rootPane.setCenter(vista); // Cambia solo el centro

            // Ocultamos el panel derecho para que la nueva vista ocupe todo el espacio
            panelDerecho.setVisible(false);
            panelDerecho.setManaged(false);

        } catch (Exception e) {
            System.out.println("Error cargando vista: " + ruta);
            e.printStackTrace();
        }
    }

    private void cargarLibrosDestacados() {
        if (contenedorLibros != null) contenedorLibros.getChildren().clear();

        java.util.List<org.bibliotecafxv.model.Libro> libros = libroDAO.listar();

        int limite = Math.min(libros.size(), 4);
        for (int i = 0; i < limite; i++) {
            org.bibliotecafxv.model.Libro libro = libros.get(i);
            javafx.scene.layout.VBox cardLibro = crearCardLibro(libro);
            contenedorLibros.getChildren().add(cardLibro);
        }
    }

    private javafx.scene.layout.VBox crearCardLibro(org.bibliotecafxv.model.Libro libro) {
        javafx.scene.layout.VBox card = new javafx.scene.layout.VBox(10);
        card.getStyleClass().add("book-card");
        card.setPrefWidth(160);

        ImageView imageView = new ImageView();
        try {
            if (libro.getPortada() != null && !libro.getPortada().isEmpty()) {
                Image image = new Image(libro.getPortada(), true);
                imageView.setImage(image);
            }
        } catch (Exception e) {
            System.out.println("No se pudo cargar la imagen: " + libro.getPortada());
        }

        imageView.setFitWidth(130);
        imageView.setFitHeight(180);
        imageView.setPreserveRatio(false);
        Rectangle clip = new Rectangle(130, 180);
        clip.setArcWidth(15);
        clip.setArcHeight(15);
        imageView.setClip(clip);

        javafx.scene.control.Label lblTitulo = new javafx.scene.control.Label(libro.getTitulo());
        lblTitulo.getStyleClass().add("book-title");
        lblTitulo.setWrapText(true);

        javafx.scene.control.Label lblAutor = new javafx.scene.control.Label(libro.getAutor());
        lblAutor.getStyleClass().add("book-author");

        javafx.scene.control.Label lblEstado = new javafx.scene.control.Label(
                libro.isDisponible() ? "Disponible" : "Prestado"
        );
        lblEstado.getStyleClass().add(libro.isDisponible() ? "badge-disponible" : "badge-prestado");

        card.getChildren().addAll(imageView, lblTitulo, lblAutor, lblEstado);
        return card;
    }

    private void crearCalendario() {
        if (contenedorCalendario == null) return;
        contenedorCalendario.getChildren().clear();

        java.time.YearMonth anioMes = java.time.YearMonth.now();
        int diasEnMes = anioMes.lengthOfMonth();
        int diaSemanaInicio = anioMes.atDay(1).getDayOfWeek().getValue();

        String[] meses = {"Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"};
        String mesActual = meses[anioMes.getMonthValue() - 1] + " " + anioMes.getYear();
        javafx.scene.control.Label lblMes = new javafx.scene.control.Label(mesActual);
        lblMes.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #2c3e50;");

        javafx.scene.layout.GridPane grid = new javafx.scene.layout.GridPane();
        grid.setHgap(8);
        grid.setVgap(8);
        grid.setAlignment(javafx.geometry.Pos.CENTER);

        String[] diasSemana = {"Lu", "Ma", "Mi", "Ju", "Vi", "Sa", "Do"};
        for (int i = 0; i < 7; i++) {
            javafx.scene.control.Label lblDia = new javafx.scene.control.Label(diasSemana[i]);
            lblDia.setStyle("-fx-text-fill: #888888; -fx-font-size: 12px; -fx-font-weight: bold;");
            grid.add(lblDia, i, 0);
        }

        int mesNum = anioMes.getMonthValue();
        int anioNum = anioMes.getYear();
        java.util.List<Integer> diasConDevolucion = prestamoDAO.obtenerDiasConDevolucion(mesNum, anioNum);
        int fila = 1;
        int col = diaSemanaInicio - 1;

        for (int dia = 1; dia <= diasEnMes; dia++) {
            javafx.scene.control.Label lblNum = new javafx.scene.control.Label(String.valueOf(dia));
            lblNum.getStyleClass().add("calendar-day");

            if (diasConDevolucion.contains(dia)) {
                lblNum.getStyleClass().add("calendar-day-due");
            }

            grid.add(lblNum, col, fila);

            col++;
            if (col > 6) {
                col = 0;
                fila++;
            }
        }

        contenedorCalendario.getChildren().addAll(lblMes, grid);
    }
}