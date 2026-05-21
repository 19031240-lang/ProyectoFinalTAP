# Sistema de Gestión de Biblioteca (BibliotecaFX)

Un sistema de escritorio moderno e interactivo para la gestión integral de una biblioteca, desarrollado con **Java** y **JavaFX**. Este proyecto permite administrar un catálogo de libros, controlar préstamos y gestionar usuarios mediante un sistema de inicio de sesión con roles (Administrador y Cliente), destacando por su sólida arquitectura de software.

---

## Características Principales

### Módulo de Administrador (Dashboard)
* **Panel Estadístico:** Visualización de métricas en tiempo real.
* **Gestión de Inventario (CRUD):** Agregar, editar, eliminar y visualizar libros del catálogo.
* **Control de Usuarios:** Administración de cuentas y roles de usuarios registrados.
* **Monitoreo de Préstamos y Multas:** Historial de préstamos activos y cálculo automatizado de recargos por devoluciones tardías.

### Módulo de Usuario (Cliente)
* **Exploración Visual:** Catálogo interactivo de libros con diseño de tarjetas (portada, título, autor y estado).
* **Diseño Maestro-Detalle:** Panel interactivo para ver la información detallada del libro seleccionado.
* **Sistema de Solicitud:** Proceso automatizado de préstamos con cálculo inmediato de la fecha límite de devolución y actualización de disponibilidad en tiempo real.

---

## Arquitectura y Patrones de Diseño

Este proyecto fue estructurado siguiendo las mejores prácticas de la Ingeniería de Software, aplicando múltiples patrones de diseño para garantizar que el código sea escalable,  mantenible y limpio:

* **MVC (Model-View-Controller):** Separación estricta de la lógica de negocio, los modelos de datos y la interfaz gráfica (archivos FXML).
* **Singleton Pattern:** Aplicado en la clase `ConexionBD` mediante el método `getInstancia()`. Garantiza una única instancia de la conexión a la base de datos en toda la aplicación, optimizando el uso de recursos y memoria.
* **DAO con Tipos Genéricos (Data Access Object):** Implementación de una interfaz `GenericDAO<T>` para estandarizar las operaciones CRUD en la base de datos (utilizado en `UsuarioDAO`, `LibroDAO`, `PrestamoDAO` y `MultaDAO`).
* **Factory Pattern:** Creación centralizada y segura de instancias de usuarios mediante `UsuarioFactory`, facilitando la escalabilidad de nuevos roles.
* **Strategy Pattern:** Algoritmo dinámico e intercambiable para el cálculo automático de multas por morosidad.
* **Observer Pattern:** Sistema de notificaciones desacoplado (Interfaces `Subject` y `Observer`). Cuando un cliente solicita un libro, el `PrestamoNotifier` alerta al sistema (`AdminObserver`) en tiempo real sin bloquear la interfaz.

---

##  Tecnologías Utilizadas

* **Lenguaje Core:** Java
* **Interfaz Gráfica:** JavaFX (Controladores FXML y estilos CSS)
* **Gestor de Dependencias:** Maven
* **Base de Datos:** Relacional (SQL) conectada mediante JDBC
* **Seguridad:** Encriptación de contraseñas mediante función Hash (SHA-1)

---

## Instalación y Ejecución

1. **Clonar el repositorio:**
   ```bash
   git clone https://github.com/19031240-lang/ProyectoFinalTAP.git

2. **Configuración de la Base de Datos:**

* Crea una base de datos en tu gestor SQL (MySQL/MariaDB, etc.).
* Importa las tablas necesarias (usuarios, libros, prestamos, multas).
* Actualiza las credenciales de conexión en la clase ConexionBD dentro del paquete connection.
3. **Ejecución del Proyecto:**
* Abre el proyecto en tu IDE favorito (IntelliJ IDEA, Eclipse, NetBeans).
*Asegúrate de recargar el archivo pom.xml para descargar las dependencias de JavaFX.
*Ejecuta la clase principal Main.java.

---
## Conexión a la Base de Datos
* MySQL
* Tomar en consideración el cambio del puerto normalmente es 3306.
* Cambiar el nombre del usuario y la contraseña adeptandolo a lo propio.
<img width="856" height="2504" alt="libreria@localhost  2" src="https://github.com/user-attachments/assets/968ae3a6-701a-4f5b-bfae-ce574e85c23c" />


---

## Capturas de Pantalla

* **Menú Principal / Catálogo**
<img width="1912" height="1023" alt="Captura de pantalla 2026-05-15 182503" src="https://github.com/user-attachments/assets/1f30284b-c741-4f61-a1c0-bc5784a7aa4b" />

* **Dashboard Administrador**

<img width="1919" height="1019" alt="Captura de pantalla 2026-05-15 182358" src="https://github.com/user-attachments/assets/77a0295c-287d-4cef-91ca-c81a1993a696" />
