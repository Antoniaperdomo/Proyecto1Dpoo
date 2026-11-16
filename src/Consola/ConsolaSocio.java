package Consola;

import java.util.ArrayList;
import Torneos.Torneo;   
import java.util.Date;

import Deportes.Equipo;
import Deportes.EstadisticaPractica;
import Deportes.InscripcionIndividual;
import Deportes.Instalacion;
import Deportes.PartidoPractica;
import Deportes.ReservaInstalacion;
import Torneos.BonoDescuento;
import Torneos.CatalogoTorneos;
import Torneos.InscripcionTorneo;
import Torneos.Torneo;
import Usuario.Socio;
import Usuario.Usuario;

public class ConsolaSocio extends ConsolaBase {

	public static void main(String[] args) {
		new ConsolaSocio().correr();
	}

	private static final String[] OPCIONES = { "Ver mi perfil y puntos de fidelidad", "Equipos: unirme a un equipo",
			"Deportes individuales: inscribirme / ver mi ranking", "Reservar una instalación",
			"Programar partido de práctica", "Torneos: consultar torneos", "Torneos: inscribirme / salirme",
			"Mi bono de descuento", "Comprar en tienda o cafetería" };

	@Override
	protected String titulo() {
		// TODO Auto-generated method stub
		return "Menú del Socio";
	}

	@Override
	protected boolean usuarioPermitido(Usuario u) {
		// TODO Auto-generated method stub
		return u instanceof Socio;
	}

	@Override
	protected String[] opciones() {
		// TODO Auto-generated method stub
		return OPCIONES;
	}

	private Socio socio() {
		return (Socio) usuarioActual;
	}

	private void verPerfil() {
		Socio s = socio();
		info("Nombre: " + s.getNombre());
		info("Edad: " + s.getEdad());
		info("Puntos de fidelidad: " + s.getPuntosFidelidad());
		info("Equipos: " + s.getEquipos().size());
		info("Disciplinas individuales: " + s.getInscripciones().size());
		info("Reservas: " + s.getReservas().size());
		info("Partidos de práctica: " + s.getPartidosPractica().size());
	}

	private void unirseEquipo() {
		ArrayList<Equipo> activos = new ArrayList<Equipo>();
		for (Equipo e : datos.getEquipos()) {
			if (e.isActivo()) {
				activos.add(e);
			}
		}
		Equipo equipo = elegirEquipo("Equipos disponibles", activos);
		if (equipo == null) {
			return;
		}
		String posicion = entrada.leerTexto("Posición");
		int camiseta = entrada.leerEntero("Número de camiseta", 1, 99);
		socio().unirseEquipo(equipo, posicion, camiseta);
		info("Ahora haces parte del equipo " + equipo.getNombre());
	}

	private void deportesIndividuales() {
		String[] ops = { "Inscribirme a una disciplina", "Ver mis inscripciones y ranking" };
		int op = entrada.elegirOpccion("Deportes individuales", ops);
		if (op == 1) {
			String disciplina = elegirTexto("Disciplina", DISCIPLINAS);
			if (disciplina == null) {
				return;
			}
			socio().inscribirse(disciplina);
			info("Inscripcion exitosa en " + disciplina);
		} else if (op == 2) {
			verInscripciones();
		}

	}

	private void verInscripciones() {
		if (socio().getInscripciones().isEmpty()) {
			info("No estás inscrito en ninguna disciplina.");
			return;
		}
		for (InscripcionIndividual i : socio().getInscripciones()) {
			info(i.getDeporte().getNombre() + " | nivel " + i.getDeporte().getNivel() + " | ELO " + i.getRankingELO()
					+ " | historial " + i.getHistorialElo());
		}
	}

	private void reservar() {
		String disciplina = elegirTexto("Disciplina a reservar", DISCIPLINAS);
		if (disciplina == null) {
			return;
		}
		Date fecha = entrada.leerFcha("Fecha de la Reserva");
		int hora = entrada.leerEntero("Hora de inicio del bloque", 6, 21);
		String[] modalidades = { "INDIVIDUAL", "DOBLES" };
		String modalidad = elegirTexto("Modalidad", modalidades);
		if (modalidad == null) {
			return;
		}
		int jugadores = entrada.leerEntero("Numero de jugadores", 1, 10);
		ReservaInstalacion r = socio().reservar(datos.getInstalaciones(), disciplina, fecha, bloqueHorario(fecha, hora),
				modalidad, jugadores);
		info("Reserva confirmada en " + r.getInstalacion().getNombre() + " a las " + hora + ":00");

	}

	private void programarPartido() {
		String disciplina = elegirTexto("Disciplina", DISCIPLINAS);
		if (disciplina == null) {
			return;
		}
		Instalacion instalacion = elegirInstalacion("Instalacion", disciplina);
		if (instalacion == null) {
			return;
		}
		Date fecha = entrada.leerFechaHora("Fecha y hora del partido");
		ArrayList<Socio> jugadores = elegirJugadores(disciplina, socio());
		PartidoPractica partido = socio().programarPartido(fecha, disciplina, instalacion, jugadores);
		partido.validarNiveles();
		partido.registrarEnJugadores();
		datos.getPartidosPractica().add(partido);
		info("Partido programado con " + jugadores.size() + " jugadores en " + instalacion.getNombre());
	}
	
	private void registrarResultado() {
		ArrayList<PartidoPractica> pendientes = new ArrayList<PartidoPractica>();
		ArrayList<String> descripciones = new ArrayList<String>();
		for (PartidoPractica p : socio().getPartidosPractica()) {
			if (!p.isFinalizado() && p.getJugadoresParticipantes().size() == 2) {
				pendientes.add(p);
				descripciones.add(p.getDisciplina() + " | " + p.getFecha() + " | "
						+ p.getJugadoresParticipantes().get(0).getNombre() + " vs "
						+ p.getJugadoresParticipantes().get(1).getNombre());
			}
		}
		int pos = entrada.elegirIndice("Partidos sin resultado (de a 2 jugadores)", descripciones);
		if (pos < 0) {
			return;
		}
		PartidoPractica partido = pendientes.get(pos);
		for (Socio jugador : partido.getJugadoresParticipantes()) {
			int sets = entrada.leerEntero("Sets ganados por " + jugador.getNombre(), 0, 5);
			EstadisticaPractica est = jugador.buscarInscripcion(partido.getDisciplina()).getDeporte()
					.crearEstadistica();
			est.setSetsGanados(sets);
			partido.registrarEstadisticas(jugador, est);
		}
		info("Resultado registrado. Cambio de ranking:");
		info(partido.finalizar());
	}
	
	private void partidosPractica() {
		String[] ops = { "Programar un partido", "Registrar resultado de un partido" };
		int op = entrada.elegirOpccion("Partidos de práctica", ops);
		if (op == 1) {
			programarPartido();
		} else if (op == 2) {
			registrarResultado();
		}
	}
	
	// Torneos (HU22 a HU28)

		private static final String[] TIPOS_TORNEO = { "RECREATIVO", "COMPETITIVO" };
		private static final String[] DEPORTES_TORNEO = { "FUTBOL", "BALONCESTO", "VOLEIBOL", "TENIS", "PADEL",
				"TENISDEMESA" };

		private Torneo elegirTorneo(String titulo, ArrayList<Torneo> candidatos) {
			ArrayList<String> descripciones = new ArrayList<String>();
			for (Torneo t : candidatos) {
				descripciones.add(CatalogoTorneos.resumen(t));
			}
			int pos = entrada.elegirIndice(titulo, descripciones);
			if (pos < 0) {
				return null;
			}
			return candidatos.get(pos);
		}

		/** Torneos abiertos de conjunto (individual = false) o individuales (individual = true). */
		private ArrayList<Torneo> torneosAbiertos(boolean individual) {
			ArrayList<Torneo> lista = new ArrayList<Torneo>();
			for (Torneo t : CatalogoTorneos.filtrar(datos.getTorneos(), Torneo.ABIERTO, null, null)) {
				if (t.esIndividual() == individual) {
					lista.add(t);
				}
			}
			return lista;
		}

		// Opcion 6 (HU22): consultar torneos abiertos, con filtros, e historial
		private void consultarTorneos() {
			String[] ops = { "Ver los torneos abiertos", "Ver los torneos abiertos de un tipo",
					"Ver los torneos abiertos de un deporte", "Ver el historial de un torneo" };
			int op = entrada.elegirOpccion("Consultar torneos", ops);
			ArrayList<Torneo> todos = datos.getTorneos();
			if (op == 1) {
				info(CatalogoTorneos.listar(CatalogoTorneos.filtrar(todos, Torneo.ABIERTO, null, null)));
			} else if (op == 2) {
				String tipo = elegirTexto("Tipo de torneo", TIPOS_TORNEO);
				if (tipo != null) {
					info(CatalogoTorneos.listar(CatalogoTorneos.filtrar(todos, Torneo.ABIERTO, tipo, null)));
				}
			} else if (op == 3) {
				String deporte = elegirTexto("Deporte", DEPORTES_TORNEO);
				if (deporte != null) {
					info(CatalogoTorneos.listar(CatalogoTorneos.filtrar(todos, Torneo.ABIERTO, null, deporte)));
				}
			} else if (op == 4) {
				Torneo t = elegirTorneo("Torneos del club", todos);
				if (t != null) {
					info(t.getHistorial());
				}
			}
		}

		// Opcion 7 (HU23 a HU26): inscribir a mi equipo, inscribirme o salirme
		private void inscripcionTorneos() {
			String[] ops = { "Inscribir a mi equipo (deportes de conjunto)", "Inscribirme en un torneo individual",
					"Salirme de un torneo" };
			int op = entrada.elegirOpccion("Torneos: inscripción", ops);
			if (op == 1) {
				inscribirEquipoEnTorneo();
			} else if (op == 2) {
				inscribirmeEnTorneo();
			} else if (op == 3) {
				salirmeDeTorneo();
			}
		}

		private boolean confirmarTarifa(Torneo t) {
			if (t.getTarifa() > 0) {
				return entrada.leerSiNo("Este torneo cobra una tarifa de $" + t.getTarifa() + ". ¿Confirma la inscripción?");
			}
			return true;
		}

		private void informarInscripcion(Torneo t, InscripcionTorneo i) {
			info("Inscripción exitosa en " + t.getNombre() + " con cupo " + i.getTipoCupo().toLowerCase() + ".");
			if (i.getTarifaPagada() > 0) {
				info("Tarifa registrada como pagada: $" + i.getTarifaPagada());
			}
		}

		private void inscribirEquipoEnTorneo() {
			Torneo t = elegirTorneo("Torneos de conjunto abiertos", torneosAbiertos(false));
			if (t == null) {
				return;
			}
			Equipo equipo = elegirEquipo("¿Qué equipo inscribe?", socio().getEquipos());
			if (equipo == null || !confirmarTarifa(t)) {
				return;
			}
			informarInscripcion(t, t.inscribirEquipo(equipo, socio(), datos.getEquipos(), datos.getPartidos(), new Date()));
		}

		private void inscribirmeEnTorneo() {
			Torneo t = elegirTorneo("Torneos individuales abiertos", torneosAbiertos(true));
			if (t == null) {
				return;
			}
			Socio companero = null;
			if (Torneo.DOBLES.equals(t.getModalidad())) {
				ArrayList<Socio> candidatos = new ArrayList<Socio>();
				for (Socio s : sociosActivos()) {
					if (s != socio() && s.buscarInscripcion(t.getDeporte().getNombre()) != null) {
						candidatos.add(s);
					}
				}
				companero = elegirSocio("Elija a su compañero de dobles", candidatos);
				if (companero == null) {
					return;
				}
			}
			if (!confirmarTarifa(t)) {
				return;
			}
			informarInscripcion(t,
					t.inscribirParticipante(socio(), companero, null, todosLosSocios(), datos.getTurnos(), new Date()));
		}

		/** Mi inscripcion en el torneo: la individual o la de alguno de mis equipos. */
		private InscripcionTorneo inscripcionPropia(Torneo t) {
			InscripcionTorneo i = t.buscarInscripcion(socio());
			if (i != null) {
				return i;
			}
			for (Equipo e : socio().getEquipos()) {
				i = t.buscarInscripcion(e);
				if (i != null) {
					return i;
				}
			}
			return null;
		}

		private void salirmeDeTorneo() {
			ArrayList<Torneo> mios = new ArrayList<Torneo>();
			for (Torneo t : datos.getTorneos()) {
				if (inscripcionPropia(t) != null) {
					mios.add(t);
				}
			}
			Torneo t = elegirTorneo("Torneos en los que estoy inscrito", mios);
			if (t == null || !entrada.leerSiNo("¿Seguro que desea salirse de " + t.getNombre() + "?")) {
				return;
			}
			InscripcionTorneo propia = inscripcionPropia(t);
			InscripcionTorneo retirada;
			if (propia.getEquipo() != null) {
				retirada = t.retirarEquipo(propia.getEquipo(), socio());
			} else {
				retirada = t.retirarParticipante(socio());
			}
			info(t.mensajeRetiro(retirada));
		}

		// Opcion 8 (HU28): ver mis bonos de descuento
		private void verBonos() {
			if (socio().getBonos().isEmpty()) {
				info("No tienes bonos de descuento. Los ganas siendo campeón de un torneo recreativo.");
				return;
			}
			for (BonoDescuento b : socio().getBonos()) {
				info(b.toString());
			}
			info("Bonos disponibles: " + socio().getBonosDisponibles().size()
					+ ". Puedes usar uno al comprar (opción 9).");
		}

		@Override
		protected void ejecutar(int opcion) {
			// TODO Auto-generated method stub
			switch (opcion) {
			case 1:
				verPerfil();
				break;
			case 2:
				unirseEquipo();
				break;
			case 3:
				deportesIndividuales();
				break;
			case 4:
				reservar();
				break;
			case 5:
				partidosPractica();
				break;
			case 6:
				consultarTorneos();
				break;
			case 7:
				inscripcionTorneos();
				break;
			case 8:
				verBonos();
				break;
			case 9:
				new MenuCompras(entrada, datos, socio()).comprar();
				break;
			default:
				info("Opción no válida");

			}

		}

}
