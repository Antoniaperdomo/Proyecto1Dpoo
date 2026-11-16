package Persistencia;

import static Persistencia.Formato.esc;
import static Persistencia.Formato.fecha;
import static Persistencia.Formato.linea;
import static Persistencia.Formato.lista;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import Torneos.BonoDescuento;
import Torneos.EncuentroTorneo;
import Torneos.InscripcionTorneo;
import Torneos.Torneo;
import Torneos.TorneoCompetitivo;
import Torneos.TorneoRecreativo;
import Usuario.Socio;
import Usuario.Usuario;

public class GuardadorTorneos {
	
	public static final String TORNEOS = "torneos.txt";
	public static final String TORNEOS_INSCRIPCIONES = "torneos_inscripciones.txt";
	public static final String TORNEOS_ENCUENTROS = "torneos_encuentros.txt";
	public static final String BONOS = "bonos.txt";

	public static void guardar(DatosClub d, Path carpeta) throws IOException {
		List<String> filasTorneos = new ArrayList<String>();
		List<String> filasInscripciones = new ArrayList<String>();
		List<String> filasEncuentros = new ArrayList<String>();

		for (Torneo t : d.getTorneos()) {
			String tarifa = String.valueOf(t.getTarifa());
			String bono = "";
			if (t instanceof TorneoRecreativo) {
				bono = String.valueOf(((TorneoRecreativo) t).getPorcentajeBono());
			} else if (!(t instanceof TorneoCompetitivo)) {
				throw new IOException("Tipo de torneo no soportado: " + t.getClass().getName());
			}
			ArrayList<Integer> avanzan = new ArrayList<Integer>();
			for (InscripcionTorneo i : t.getAvanzan()) {
				avanzan.add(t.getInscripciones().indexOf(i));
			}
			filasTorneos.add(linea(String.valueOf(t.getId()), t.getTipo(), esc(t.getNombre()),
					esc(t.getDeporte().getNombre()), t.getCategoria() == null ? "" : esc(t.getCategoria().getNombre()),
					esc(t.getNivel()), esc(t.getModalidad()), String.valueOf(t.getCupos()), fecha(t.getFechaInicio()),
					fecha(t.getFechaLimiteReserva()), tarifa, bono, t.getEstado(), String.valueOf(t.getRondaActual()),
					lista(avanzan), lista(t.getPremiosEntregados())));

			for (InscripcionTorneo i : t.getInscripciones()) {
				String equipo = "";
				String jugador1 = "";
				String jugador2 = "";
				if (i.getEquipo() != null) {
					equipo = esc(i.getEquipo().getNombre());
				} else {
					jugador1 = esc(i.getIntegrantes().get(0).getId());
					if (i.getIntegrantes().size() > 1) {
						jugador2 = esc(i.getIntegrantes().get(1).getId());
					}
				}
				filasInscripciones.add(linea(String.valueOf(t.getId()), esc(i.getTipoCupo()),
						String.valueOf(i.getTarifaPagada()), equipo, jugador1, jugador2));
			}

			for (EncuentroTorneo e : t.getEncuentros()) {
				filasEncuentros.add(linea(String.valueOf(t.getId()), String.valueOf(e.getRonda()),
						String.valueOf(t.getInscripciones().indexOf(e.getLocal())),
						String.valueOf(t.getInscripciones().indexOf(e.getVisitante())), fecha(e.getFecha()),
						String.valueOf(e.getMarcadorLocal()), String.valueOf(e.getMarcadorVisitante()),
						String.valueOf(e.isJugado())));
			}
		}

		List<String> filasBonos = new ArrayList<String>();
		for (Usuario u : d.getUsuarios()) {
			if (u instanceof Socio) {
				for (BonoDescuento b : ((Socio) u).getBonos()) {
					filasBonos.add(linea(esc(u.getId()), esc(b.getTorneoOrigen()), String.valueOf(b.getPorcentaje()),
							String.valueOf(b.isUsado())));
				}
			}
		}

		escribir(carpeta, TORNEOS,
				"id;tipo;nombre;deporte;categoria;nivel;modalidad;cupos;fechaInicio;fechaLimiteReserva;tarifa;"
						+ "porcentajeBono;estado;rondaActual;avanzan;premios",
				filasTorneos);
		escribir(carpeta, TORNEOS_INSCRIPCIONES, "torneoId;tipoCupo;tarifaPagada;equipo;jugador1Id;jugador2Id",
				filasInscripciones);
		escribir(carpeta, TORNEOS_ENCUENTROS,
				"torneoId;ronda;local;visitante;fecha;marcadorLocal;marcadorVisitante;jugado", filasEncuentros);
		escribir(carpeta, BONOS, "socioId;torneoOrigen;porcentaje;usado", filasBonos);
	}

	private static void escribir(Path carpeta, String nombre, String encabezado, List<String> filas)
			throws IOException {
		List<String> contenido = new ArrayList<String>();
		contenido.add("# " + encabezado);
		contenido.addAll(filas);
		Path destino = carpeta.resolve(nombre);
		Path temporal = carpeta.resolve(nombre + ".tmp");
		Files.write(temporal, contenido, StandardCharsets.UTF_8);
		Files.move(temporal, destino, StandardCopyOption.REPLACE_EXISTING);
	}

}
