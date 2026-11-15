package Consola;

import java.util.ArrayList;
import java.util.Date;

import Deportes.Equipo;
import Deportes.EstadisticaPractica;
import Deportes.InscripcionIndividual;
import Deportes.Instalacion;
import Deportes.PartidoPractica;
import Deportes.ReservaInstalacion;
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
		case 9:
			new MenuCompras(entrada, datos, socio()).comprar();
			break;
		default:
			info("Opción pendiente de conectar con los torneos: " + OPCIONES[opcion - 1]);

		}

	}

}
