package Persistencia;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


 //Punto de entrada de la persistencia. Toda la información del club vive en
 // una carpeta de archivos de texto (UTF-8), un archivo por tipo de entidad o
 // relación. La carpeta NO puede ser la del código fuente. 
 //Uso: 
 // DatosClub datos = PersistenciaClub.cargar("datos");
 // ... se usa la aplicación ...
 // PersistenciaClub.guardar(datos, "datos");

public class PersistenciaClub {

	public static final String USUARIOS = "usuarios.txt";
	public static final String UBICACIONES = "ubicaciones.txt";
	public static final String PRODUCTOS = "productos.txt";
	public static final String EXISTENCIAS = "existencias.txt";
	public static final String EQUIPOS = "equipos.txt";
	public static final String JUGADORES_EQUIPO = "jugadores_equipo.txt";
	public static final String SUSPENSIONES = "suspensiones.txt";
	public static final String INSCRIPCIONES = "inscripciones.txt";
	public static final String INSTALACIONES = "instalaciones.txt";
	public static final String RESERVAS = "reservas.txt";
	public static final String PARTIDOS_PRACTICA = "partidos_practica.txt";
	public static final String ESTADISTICAS_PRACTICA = "estadisticas_practica.txt";
	public static final String COMPETENCIAS = "competencias.txt";
	public static final String NOMINAS = "nominas.txt";
	public static final String TABLA_POSICIONES = "tabla_posiciones.txt";
	public static final String PARTIDOS = "partidos.txt";
	public static final String ESTADISTICAS_PARTIDO = "estadisticas_partido.txt";
	public static final String SESIONES = "sesiones.txt";
	public static final String ASISTENCIAS = "asistencias.txt";
	public static final String TURNOS = "turnos.txt";
	public static final String SOLICITUDES_TURNO = "solicitudes_turno.txt";
	public static final String SUGERENCIAS = "sugerencias.txt";
	public static final String VENTAS = "ventas.txt";
	public static final String VENTAS_ITEMS = "ventas_items.txt";

	// Carga el estado del club. Si la carpeta no existe, devuelve un club vacío. */
	public static DatosClub cargar(String carpeta) throws IOException {
		Path ruta = Paths.get(carpeta);
		if (!Files.isDirectory(ruta)) {
			return new DatosClub();
		}
		return CargadorClub.cargar(ruta);
	}

	// Guarda el estado del club, creando la carpeta si hace falta. */
	public static void guardar(DatosClub datos, String carpeta) throws IOException {
		Path ruta = Paths.get(carpeta);
		verificarQueNoEsCodigoFuente(ruta);
		GuardadorClub.guardar(datos, ruta);
	}

	private static void verificarQueNoEsCodigoFuente(Path ruta) throws IOException {
		if (!Files.isDirectory(ruta)) {
			return;
		}
		try (DirectoryStream<Path> contenido = Files.newDirectoryStream(ruta, "*.java")) {
			if (contenido.iterator().hasNext()) {
				throw new IllegalArgumentException(
						"La carpeta de datos no puede ser la del código fuente: " + ruta.toAbsolutePath());
			}
		}
	}
}
