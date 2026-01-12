# 📚 LiterAlura – Catálogo de Libros

Proyecto desarrollado como parte del desafío de programación de Alura/Oracle Next Education. Se trata de una aplicación de consola que gestiona un catálogo de libros, consume una API externa, manipula datos JSON y persiste la información en una base de datos relacional.

El objetivo principal es consolidar conocimientos en Java, Spring Framework y bases de datos, permitiendo buscar libros, autores y generar estadísticas de lectura.

---

## 🚀 Características principales

- Consumo de la API **Gutendex** para buscar libros por título.
- Persistencia de datos en **PostgreSQL**.
- Validación de duplicados (verifica si el autor o libro ya existe).
- Búsqueda avanzada y filtrado:
  - Buscar libros por título.
  - Listar todos los libros registrados.
  - Listar autores registrados.
  - **Filtrar autores vivos** en un año específico.
  - **Filtrar libros por idioma** (ES, EN, FR, PT).
- Estadísticas y Extras:
  - 🏆 Top 10 libros más descargados.
  - 📊 Estadísticas generales de la base de datos.
  - 🔍 Búsqueda precisa de autores por nombre.

---

## 🛠️ Tecnologías utilizadas

- **Java 17**
- **Spring Boot 3.2.3**
- **Spring Data JPA** (Hibernate)
- **PostgreSQL 16**
- **Jackson** (Mapeo JSON)
- **Maven**
- **IntelliJ IDEA**

---

## 📂 Estructura del proyecto

```text
src/main/java/com/aluracursos/literalura
│
├── LiteraluraApplication.java  // Clase principal (Menú y ejecución)
│
├── model
│   ├── Autor.java              // Entidad JPA (Tabla 'autores')
│   ├── Libro.java              // Entidad JPA (Tabla 'libros')
│   ├── DatosAutor.java         // Record (DTO) para mapeo JSON
│   ├── DatosLibro.java         // Record (DTO) para mapeo JSON
│   └── DatosResultados.java    // Record contenedor de la respuesta API
│
├── repository
│   ├── AutorRepository.java    // Consultas a BD (Derived Queries)
│   └── LibroRepository.java    // Consultas a BD (JPA y JPQL)
│
└── service
    ├── ConsumoAPI.java         // Cliente HTTP para la API
    └── ConvierteDatos.java     // Deserialización con Jackson
🔑 Configuración inicial
Base de Datos:

Crear una base de datos en PostgreSQL llamada literalura.

Configurar las credenciales en src/main/resources/application.properties:

Properties

spring.datasource.url=jdbc:postgresql://localhost:5432/literalura
spring.datasource.username=postgres
spring.datasource.password=1234
spring.jpa.hibernate.ddl-auto=update
Ejecución:

Clonar el repositorio.

Abrir el proyecto en IntelliJ IDEA.

Esperar a que Maven descargue las dependencias.

Ejecutar la clase LiteraluraApplication.java.

🌐 API Utilizada
El proyecto consume la API pública de Gutendex (Project Gutenberg):

Endpoint: https://gutendex.com/books/

Documentación: Gutendex API

No requiere API Key. La respuesta es procesada para extraer título, autores, idiomas y número de descargas.

🖥️ Ejemplo de uso (Menú)
Plaintext

--- LITERALURA ---
1 - Buscar libro por título (y guardar en BD)
2 - Listar libros registrados
3 - Listar autores registrados
4 - Listar autores vivos en un determinado año
5 - Listar libros por idioma

--- ESTADÍSTICAS Y EXTRAS ---
6 - Top 10 libros más descargados
7 - Buscar autor por nombre
8 - Generar estadísticas generales de la BD
              
0 - Salir
Elija una opción: 
Ejemplo de Resultado (Opción 1):

Plaintext

Ingrese el título del libro: Quijote
Libro encontrado: Don Quijote
Libro guardado exitosamente:
----- LIBRO -----
Titulo: Don Quijote
Autor: Cervantes Saavedra, Miguel de
Idioma: es
Descargas: 9818.0
-----------------
Ejemplo de Resultado (Opción 4 - Autores vivos en 1600):

Plaintext

Autor: Cervantes Saavedra, Miguel de (1547-1616)
Autor: Shakespeare, William (1564-1616)
👤 Autor
Proyecto realizado por Juan Castro como parte de la formación Java Back-End de Alura Latam.
