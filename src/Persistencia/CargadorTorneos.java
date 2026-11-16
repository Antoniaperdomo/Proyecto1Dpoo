package Persistencia;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import Deportes.Baloncesto;
import Deportes.Categoria;
import Deportes.Deporte;
import Deportes.Equipo;
import Deportes.Futbol;
import Deportes.Pareja;
import Deportes.Padel;
import Deportes.Tenis;
import Deportes.TenisMesa;
import Deportes.Voleibol;
import Deportes.DeporteIndividual;
import Torneos.BonoDescuento;
import Torneos.EncuentroTorneo;
import Torneos.InscripcionTorneo;
import Torneos.Torneo;
import Torneos.TorneoCompetitivo;
import Torneos.TorneoRecreativo;
import Usuario.Participante;
import Usuario.Socio;
import Usuario.Usuario;

public class CargadorTorneos {
	
	private interface ProcesadorFila {
		void procesar(Fila f) throws IOException;
	}

	public static void cargar(final DatosClub d, Path carpeta) throws IOException {
		final HashMap<Integer, Torneo> porId = new HashMap<Integer, Torneo>();
		final ArrayList<Fila> filasTorneos = new ArrayList<Fila>();

		recorrer(carpeta, GuardadorTorneos.TORNEOS, new ProcesadorFila() {
			public void procesar(Fila f) throws IOException {
				int id = f.entero(0);
				if (porId.containsKey(id)) {
					throw f.error("el torneo " + id + " esta repetido");
				}
				String tipo = f.texto(1).trim().toUpperCase();
				String nombre = f.texto(2);
				Deporte deporte = deporte(f, f.texto(3).trim().toUpperCase());
				String nombreCategoria = f.textoOpcional(4);
				Categoria categoria = nombreCategoria == null ? null
						: new Categoria(nombreCategoria.trim().toUpperCase());
				String nivel = f.textoOpcional(5);
				String modalidad = f.textoOpcional(6);
				int cupos = f.entero(7);
				Date inicio = f.fecha(8);
				Date limite = f.fecha(9);
				double tarifa = f.decimal(10);
				Torneo t;
				if (tipo.equals("RECREATIVO")) {
					t = new TorneoRecreativo(id, nombre, deporte, categoria, nivel, modalidad, cupos, inicio, limite,
							tarifa, f.entero(11));
				} else if (tipo.equals("COMPETITIVO")) {
					t = new TorneoCompetitivo(id, nombre, deporte, categoria, nivel, modalidad, cupos, inicio, limite,
							tarifa);
				} else {
					throw f.error("tipo de torneo desconocido: " + tipo);
				}
				porId.put(id, t);
				filasTorneos.add(f);
				d.getTorneos().add(t);
			}
		});

		recorrer(carpeta, GuardadorTorneos.TORNEOS_INSCRIPCIONES, new ProcesadorFila() {
			public void procesar(Fila f) throws IOException {
				Torneo t = torneo(porId, f, f.entero(0));
				String tipoCupo = f.texto(1).trim().toUpperCase();
				double tarifa = f.decimal(2);
				String nombreEquipo = f.textoOpcional(3);
				InscripcionTorneo inscripcion;
				if (nombreEquipo != null) {
					Equipo e = d.buscarEquipo(nombreEquipo);
					if (e == null) {
						throw f.error("el equipo '" + nombreEquipo + "' no existe");
					}
					inscripcion = new InscripcionTorneo(e, tipoCupo, tarifa);
				} else {
					Participante j1 = participante(d, f, f.entero(4));
					if (f.enteroOpcional(5, -1) < 0) {
						inscripcion = new InscripcionTorneo(j1, tipoCupo, tarifa);
					} else {
						inscripcion = new InscripcionTorneo(new Pareja(j1, participante(d, f, f.entero(5))), tipoCupo,
								tarifa);
					}
				}
				t.getInscripciones().add(inscripcion);
			}
		});

		recorrer(carpeta, GuardadorTorneos.TORNEOS_ENCUENTROS, new ProcesadorFila() {
			public void procesar(Fila f) throws IOException {
				Torneo t = torneo(porId, f, f.entero(0));
				EncuentroTorneo e = new EncuentroTorneo(f.entero(1), inscripcion(t, f, f.entero(2)),
						inscripcion(t, f, f.entero(3)), f.fecha(4));
				if (f.booleano(7)) {
					e.registrar(f.entero(5), f.entero(6));
				}
				t.getEncuentros().add(e);
			}
		});

		// Estado interno del torneo: ahora que ya existen sus inscripciones
		for (Fila f : filasTorneos) {
			Torneo t = porId.get(f.entero(0));
			ArrayList<InscripcionTorneo> avanzan = new ArrayList<InscripcionTorneo>();
			for (int posicion : f.listaEnteros(14)) {
				avanzan.add(inscripcion(t, f, posicion));
			}
			t.restaurar(f.texto(12).trim().toUpperCase(), f.entero(13), avanzan, f.lista(15));
		}

		recorrer(carpeta, GuardadorTorneos.BONOS, new ProcesadorFila() {
			public void procesar(Fila f) throws IOException {
				Socio s = d.buscarSocio(f.entero(0));
				if (s == null) {
					throw f.error("el socio " + f.entero(0) + " no existe");
				}
				BonoDescuento b = new BonoDescuento(f.texto(1), f.entero(2));
				b.restaurar(f.booleano(3));
				s.agregarBono(b);
			}
		});
	}

	private static Torneo torneo(HashMap<Integer, Torneo> porId, Fila f, int id) throws IOException {
		Torneo t = porId.get(id);
		if (t == null) {
			throw f.error("el torneo " + id + " no existe");
		}
		return t;
	}

	private static InscripcionTorneo inscripcion(Torneo t, Fila f, int posicion) throws IOException {
		if (posicion < 0 || posicion >= t.getInscripciones().size()) {
			throw f.error("la inscripcion " + posicion + " no existe en el torneo " + t.getId());
		}
		return t.getInscripciones().get(posicion);
	}

	private static Participante participante(DatosClub d, Fila f, int id) throws IOException {
		Usuario u = d.buscarUsuario(id);
		if (!(u instanceof Participante)) {
			throw f.error("el participante " + id + " no existe");
		}
		return (Participante) u;
	}

	private static Deporte deporte(Fila f, String nombre) throws IOException {
		if (nombre.equals("FUTBOL")) {
			return new Futbol();
		}
		if (nombre.equals("BALONCESTO")) {
			return new Baloncesto();
		}
		if (nombre.equals("VOLEIBOL")) {
			return new Voleibol();
		}
		if (nombre.equals("TENIS")) {
			return new Tenis(DeporteIndividual.PRINCIPIANTE);
		}
		if (nombre.equals("PADEL")) {
			return new Padel(DeporteIndividual.PRINCIPIANTE);
		}
		if (nombre.equals("TENISDEMESA")) {
			return new TenisMesa(DeporteIndividual.PRINCIPIANTE);
		}
		throw f.error("deporte desconocido: " + nombre);
	}

	private static void recorrer(Path carpeta, String nombreArchivo, ProcesadorFila procesador) throws IOException {
		Path archivo = carpeta.resolve(nombreArchivo);
		if (!Files.exists(archivo)) {
			return; // archivo ausente = sin datos
		}
		List<String> lineas = Files.readAllLines(archivo, StandardCharsets.UTF_8);
		for (int i = 0; i < lineas.size(); i++) {
			String texto = lineas.get(i);
			if (texto.trim().isEmpty() || texto.startsWith("#")) {
				continue;
			}
			Fila fila = new Fila(nombreArchivo, i + 1, Formato.dividirCrudo(texto, Formato.SEPARADOR_COLUMNA));
			try {
				procesador.procesar(fila);
			} catch (IOException e) {
				throw e;
			} catch (RuntimeException e) {
				throw fila.error(e.getMessage());
			}
		}
	}

}
