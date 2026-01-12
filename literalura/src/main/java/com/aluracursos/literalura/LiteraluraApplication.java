package com.aluracursos.literalura;

import com.aluracursos.literalura.model.Autor;
import com.aluracursos.literalura.model.DatosLibro;
import com.aluracursos.literalura.model.DatosResultados;
import com.aluracursos.literalura.model.Libro;
import com.aluracursos.literalura.repository.AutorRepository;
import com.aluracursos.literalura.repository.LibroRepository;
import com.aluracursos.literalura.service.ConsumoAPI;
import com.aluracursos.literalura.service.ConvierteDatos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

@SpringBootApplication
public class LiteraluraApplication implements CommandLineRunner {

	@Autowired
	private LibroRepository libroRepository;
	@Autowired
	private AutorRepository autorRepository;

	private final ConsumoAPI consumoAPI = new ConsumoAPI();
	private final ConvierteDatos conversor = new ConvierteDatos();
	private final Scanner teclado = new Scanner(System.in);
	private static final String URL_BASE = "https://gutendex.com/books/";

	public static void main(String[] args) {
		SpringApplication.run(LiteraluraApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		var opcion = -1;
		while (opcion != 0) {
			var menu = """
                    \n--- LITERALURA ---
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
                    """;
			System.out.println(menu);
			if(teclado.hasNextInt()){
				opcion = teclado.nextInt();
				teclado.nextLine();
			} else {
				System.out.println("Entrada inválida.");
				teclado.nextLine();
				continue;
			}

			switch (opcion) {
				case 1 -> buscarLibroWeb();
				case 2 -> listarLibrosRegistrados();
				case 3 -> listarAutoresRegistrados();
				case 4 -> listarAutoresVivos();
				case 5 -> listarLibrosPorIdioma();
				case 6 -> top10Libros();
				case 7 -> buscarAutorPorNombre();
				case 8 -> generarEstadisticas();
				case 0 -> System.out.println("Cerrando la aplicación...");
				default -> System.out.println("Opción inválida");
			}
		}
	}

	private void buscarLibroWeb() {
		System.out.println("Ingrese el título del libro:");
		var tituloLibro = teclado.nextLine();
		var json = consumoAPI.obtenerDatos(URL_BASE + "?search=" + tituloLibro.replace(" ", "+"));
		var datosBusqueda = conversor.obtenerDatos(json, DatosResultados.class);

		Optional<DatosLibro> libroBuscado = datosBusqueda.libros().stream()
				.findFirst();

		if (libroBuscado.isPresent()) {
			DatosLibro datos = libroBuscado.get();
			System.out.println("Libro encontrado: " + datos.titulo());

			Autor autor = new Autor(datos.autores().get(0));
			Optional<Autor> autorExistente = autorRepository.findByNombreContainsIgnoreCase(autor.getNombre());

			if (autorExistente.isPresent()) {
				autor = autorExistente.get();
			} else {
				autor = autorRepository.save(autor);
			}

			try {
				Libro libro = new Libro(datos);
				libro.setAutor(autor);
				libroRepository.save(libro);
				System.out.println("Libro guardado exitosamente: " + libro);
			} catch (Exception e) {
				System.out.println("Error: El libro ya existe en la base de datos.");
			}

		} else {
			System.out.println("Libro no encontrado");
		}
	}

	private void listarLibrosRegistrados() {
		List<Libro> libros = libroRepository.findAll();
		libros.forEach(System.out::println);
	}

	private void listarAutoresRegistrados() {
		List<Autor> autores = autorRepository.findAll();
		autores.forEach(System.out::println);
	}

	private void listarAutoresVivos() {
		System.out.println("Ingrese el año vivo de autor(es) que desea buscar:");
		if(teclado.hasNextInt()){
			int anio = teclado.nextInt();
			teclado.nextLine();

			List<Autor> autores = autorRepository.findAll();
			autores.stream()
					.filter(a -> a.getFechaDeNacimiento() != null && a.getFechaDeNacimiento() <= anio)
					.filter(a -> a.getFechaDeFallecimiento() == null || a.getFechaDeFallecimiento() >= anio)
					.forEach(System.out::println);
		}
	}

	private void listarLibrosPorIdioma() {
		System.out.println("""
                Ingrese el idioma para buscar los libros:
                es - español
                en - inglés
                fr - francés
                pt - portugués
                """);
		var idioma = teclado.nextLine();
		List<Libro> libros = libroRepository.findByIdioma(idioma);
		if (libros.isEmpty()) {
			System.out.println("No hay libros registrados en ese idioma.");
		} else {
			libros.forEach(System.out::println);
		}
	}

	private void top10Libros() {
		System.out.println("--- TOP 10 LIBROS MÁS DESCARGADOS ---");
		List<Libro> libros = libroRepository.findTop10ByOrderByNumeroDeDescargasDesc();
		libros.forEach(l -> System.out.println("Libro: " + l.getTitulo() + " | Descargas: " + l.getNumeroDeDescargas()));
	}

	private void buscarAutorPorNombre() {
		System.out.println("Ingrese el nombre del autor que desea buscar en la BD:");
		var nombre = teclado.nextLine();
		Optional<Autor> autor = autorRepository.findByNombreContainsIgnoreCase(nombre);
		if (autor.isPresent()) {
			System.out.println("El autor existe en la base de datos: " + autor.get());
		} else {
			System.out.println("El autor no está registrado en la base de datos.");
		}
	}

	private void generarEstadisticas() {
		System.out.println("--- ESTADÍSTICAS DE DESCARGAS ---");
		var libros = libroRepository.findAll();
		DoubleSummaryStatistics est = libros.stream()
				.filter(l -> l.getNumeroDeDescargas() > 0)
				.mapToDouble(Libro::getNumeroDeDescargas)
				.summaryStatistics();

		System.out.println("Cantidad media de descargas: " + est.getAverage());
		System.out.println("Cantidad máxima de descargas: " + est.getMax());
		System.out.println("Cantidad mínima de descargas: " + est.getMin());
		System.out.println("Cantidad de registros evaluados: " + est.getCount());
	}
}