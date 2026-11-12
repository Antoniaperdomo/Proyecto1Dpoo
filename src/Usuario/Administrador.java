package Usuario;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import Administradores.SolicitudCambioTurno;
import Administradores.SugerenciaProducto;
import Administradores.Turno;
import Deportes.Categoria;
import Deportes.DeporteConjunto;
import Deportes.DeporteIndividual;
import Deportes.Equipo;
import Deportes.InscripcionIndividual;
import Deportes.Instalacion;
import Deportes.JugadorEquipo;
import Deportes.Partido;
import Deportes.PartidoPractica;
import Deportes.ReservaInstalacion;
import Deportes.Suspension;
import Entrenamiento.RegistroAsistencia;
import Entrenamiento.SesionEntrenamiento;
import Tienda.Articulo;
import Tienda.ExistenciaArticulo;
import Tienda.Inventario;
import Tienda.Ubicacion;
import Tienda.Venta;
import Tienda.VentaCafeteria;
import Tienda.VentaTienda;

import Entrenamiento.AnaliticaJugador;

public class Administrador extends Usuario {

	public static final String DIARIO = "diario";
	public static final String SEMANAL = "semanal";
	public static final String MENSUAL = "mensual";
	private static final long MILISEGUNDOS_POR_DIA = 24L * 60 * 60 * 1000;

	public Administrador(int id, String nombre, String login, String password, Date fechaNacimiento) {
		super(id, nombre, login, password, fechaNacimiento);
	}

	public void evaluarSolicitud(SolicitudCambioTurno solicitud, ArrayList<Turno> todosLosTurnos) {
		if (!chocaConOtrosTurnos(solicitud, todosLosTurnos) && cumpleMinimoPersonal(solicitud, todosLosTurnos)) {
			solicitud.setEstado(SolicitudCambioTurno.APROBADA);
			todosLosTurnos.remove(solicitud.getTurnoActual());
			todosLosTurnos.add(solicitud.getTurnoPropuesto());
		} else {
			solicitud.setEstado(SolicitudCambioTurno.RECHAZADA);
		}
	}

	public boolean cumpleMinimoPersonal(SolicitudCambioTurno solicitud, ArrayList<Turno> todosLosTurnos) {
		Turno turnoActual = solicitud.getTurnoActual();
		int entrenadoresDisponibles = 0;
		int fisioterapeutasDisponibles = 0;

		for (Turno t : todosLosTurnos) {
			if (t != turnoActual && t.seCruzaCon(turnoActual)) {
				if (t.getEmpleado() instanceof Entrenador) {
					entrenadoresDisponibles++;
				} else if (t.getEmpleado() instanceof Fisioterapeuta) {
					fisioterapeutasDisponibles++;
				}
			}

		}

		if (entrenadoresDisponibles >= 2 && fisioterapeutasDisponibles >= 1) {
			return true;
		} else {
			return false;
		}

	}

	private boolean chocaConOtrosTurnos(SolicitudCambioTurno solicitud, ArrayList<Turno> todosLosTurnos) {
		for (Turno t : todosLosTurnos) {
			if (t != solicitud.getTurnoActual() && t.getEmpleado().getId() == solicitud.getEmpleado().getId()
					&& t.seCruzaCon(solicitud.getTurnoPropuesto())) {
				return true;
			}
		}
		return false;
	}

	public ArrayList<SolicitudCambioTurno> getSolicitudesPendientes(
			ArrayList<SolicitudCambioTurno> todasLasSolicitudes) {
		ArrayList<SolicitudCambioTurno> solicitudesPendientes = new ArrayList<SolicitudCambioTurno>();

		for (SolicitudCambioTurno s : todasLasSolicitudes) {
			if (s.getEstado().equals(SolicitudCambioTurno.PENDIENTE)) {
				solicitudesPendientes.add(s);
			}
		}
		return solicitudesPendientes;
	}

	public ArrayList<SugerenciaProducto> getSugerenciasPendientes(ArrayList<SugerenciaProducto> todas) {
		ArrayList<SugerenciaProducto> pendientes = new ArrayList<>();
		for (SugerenciaProducto s : todas) {
			if (s.getEstado().equals(SugerenciaProducto.PENDIENTE)) {
				pendientes.add(s);
			}
		}
		return pendientes;
	}

	public void evaluarSugerencia(SugerenciaProducto sugerencia, boolean aprobar) {
		if (aprobar) {
			sugerencia.setEstado(SugerenciaProducto.APROBADA);
		} else {
			sugerencia.setEstado(SugerenciaProducto.RECHAZADA);
		}
	}

	public Turno asignarTurno(Empleado empleado, String diaSemana, int horaInicio, int horaFin,
			ArrayList<Turno> todosLosTurnos) throws IllegalArgumentException {
		Turno nuevo = new Turno(empleado, diaSemana, horaInicio, horaFin);
		for (Turno f : todosLosTurnos) {
			if (f.getEmpleado().getId() == empleado.getId() && f.seCruzaCon(nuevo)) {
				throw new IllegalArgumentException("El empleado ya tiene un turno que se cruza con este");
			}
		}
		todosLosTurnos.add(nuevo);
		return nuevo;
	}

	public Socio crearSocio(int id, String nombre, String login, String password, Date fechaNacimiento,
			ArrayList<Usuario> todosLosUsuarios) throws IllegalArgumentException {
		for (Usuario u : todosLosUsuarios) {
			if (u.getLogin().equals(login)) {
				throw new IllegalArgumentException("El login ya existe");
			}
			if (u.getId() == id) {
				throw new IllegalArgumentException("El id ya existe");
			}
		}
		Socio socio = new Socio(id, nombre, login, password, fechaNacimiento, 0, true);
		todosLosUsuarios.add(socio);
		return socio;
	}

	public void darDeBajaSocio(Socio socio, ArrayList<Socio> todosLosSocios) {
		socio.setActivo(false);
	}

	public Equipo crearEquipo(String nombre, DeporteConjunto deporte, int cupoMax, Categoria categoria,
			ArrayList<Equipo> todosLosEquipos) throws IllegalArgumentException {
		if (cupoMax <= 0) {
			throw new IllegalArgumentException("El cupo máximo debe ser mayor a cero");
		}

		for (Equipo e : todosLosEquipos) {
			if (e.getNombre().equals(nombre)) {
				throw new IllegalArgumentException("Ya existe un equipo con este nombre");
			}
		}
		Equipo equipo = new Equipo(nombre, deporte, cupoMax, categoria, new ArrayList<JugadorEquipo>(),
				new ArrayList<Suspension>(), true);
		todosLosEquipos.add(equipo);
		return equipo;
	}

	public void inscribirEnEquipo(Socio socio, Equipo equipo, String posicion, int numCamiseta,
			ArrayList<Equipo> todosLosEquipos) {
		for (Equipo e : todosLosEquipos) {
			if (e != equipo && e.isActivo() && e.getDeporte().getNombre().equals(equipo.getDeporte().getNombre())
					&& e.tieneJugador(socio)) {
				throw new IllegalStateException("El socio ya está en otro equipo de este deporte");
			}
		}
		equipo.agregarJugador(socio, posicion, numCamiseta);
	}

	public void darDeBajaEquipo(Equipo equipo) {
		equipo.setActivo(false);
	}

	private ExistenciaArticulo buscarExistencia(Articulo articulo, Ubicacion ubicacion,
			ArrayList<ExistenciaArticulo> inventario) {
		for (ExistenciaArticulo e : inventario) {
			if (e.getArticulo().getNombre().equals(articulo.getNombre())
					&& e.getUbicacion().getNombre().equals(ubicacion.getNombre())) {
				return e;
			}
		}
		return null;
	}

	public void reabastecer(Articulo articulo, Ubicacion ubicacion, int cantidad,
			ArrayList<ExistenciaArticulo> inventario) {
		ExistenciaArticulo existencia = buscarExistencia(articulo, ubicacion, inventario);
		if (existencia == null) {
			if (cantidad <= 0) {
				throw new IllegalArgumentException("La cantidad debe ser mayor a cero");
			}
			inventario.add(new ExistenciaArticulo(articulo, ubicacion, cantidad));
		} else {
			existencia.aumentar(cantidad);
		}
	}

	public void moverArticulo(Articulo articulo, Ubicacion origen, Ubicacion destino, int cantidad,
			ArrayList<ExistenciaArticulo> inventario) {
		if (origen.getNombre().equals(destino.getNombre())) {
			throw new IllegalArgumentException("El origen y el destino son la misma ubicación");
		}
		ExistenciaArticulo enOrigen = buscarExistencia(articulo, origen, inventario);
		if (enOrigen == null) {
			throw new IllegalArgumentException("El artículo no existe en la ubicación de origen");
		}
		enOrigen.reducir(cantidad);
		reabastecer(articulo, destino, cantidad, inventario);
	}

	public String reporteResultadosPartidos(ArrayList<Partido> partidos) {
		String reporte = "RESULTADOS DE PARTIDOS\n";
		for (Partido p : partidos) {
			if (p.isJugado()) {
				String rival = "rival externo";
				for (Equipo e : p.getEquipos()) {
					if (e != p.getEquipo1()) {
						rival = e.getNombre();
					}
				}
				reporte += p.getFecha() + " | " + p.getDeeporte().getNombre() + " | " + p.getAmbito() + " | "
						+ p.getEquipo1().getNombre() + " " + p.getMarcadorEquipo1() + " - " + p.getMarcadorEquipo2()
						+ " " + rival + "\n";
			}
		}
		return reporte;
	}

	public String reporteEvolucionRanking(ArrayList<InscripcionIndividual> inscripciones, DeporteIndividual deporte) {
		String reporte = "EVOLUCIÓN DE RANKING - " + deporte.getNombre() + "\n";
		for (InscripcionIndividual i : inscripciones) {
			if (i.getDeporte().getNombre().equals(deporte.getNombre())) {
				reporte += i.getSocio().getNombre() + " | actual: " + i.getRankingELO() + " ("
						+ i.getDeporte().getNivel() + ") | historial: ";
				for (int k = 0; k < i.getHistorialElo().size(); k++) {
					reporte += i.getHistorialElo().get(k);
					if (k < i.getHistorialElo().size() - 1) {
						reporte += " -> ";
					}
				}
				reporte += "\n";
			}
		}
		return reporte;
	}

	public String reporteVentas(ArrayList<Venta> ventas, Date desde, Date hasta) {
		double totalTienda = 0;
		double totalCafeteria = 0;
		int cantidadTienda = 0;
		int cantidadCafeteria = 0;

		for (Venta v : ventas) {
			long momento = v.getFecha().getTime();
			if (momento >= desde.getTime() && momento <= hasta.getTime()) {
				if (v instanceof VentaTienda) {
					totalTienda += v.calcularTotal();
					cantidadTienda++;
				} else if (v instanceof VentaCafeteria) {
					totalCafeteria += v.calcularTotal();
					cantidadCafeteria++;
				}
			}
		}

		return "REPORTE DE VENTAS\n" + "Desde: " + desde + "\nHasta: " + hasta + "\n" + "Tienda: " + cantidadTienda
				+ " ventas, total $" + totalTienda + "\n" + "Cafetería: " + cantidadCafeteria + " ventas, total $"
				+ totalCafeteria + "\n" + "Total general: $" + (totalTienda + totalCafeteria) + "\n";
	}

	public String reporteVentasPorPeriodo(ArrayList<Venta> ventas, Date fechaReferencia, String periodo) {
		int dias;
		if (periodo.equals(DIARIO)) {
			dias = 1;
		} else if (periodo.equals(SEMANAL)) {
			dias = 7;
		} else if (periodo.equals(MENSUAL)) {
			dias = 30;
		} else {
			throw new IllegalArgumentException("Periodo inválido: " + periodo);
		}
		Date desde = new Date(fechaReferencia.getTime() - dias * MILISEGUNDOS_POR_DIA);
		return reporteVentas(ventas, desde, fechaReferencia);
	}
	
	public String reporteDesempenoJugador(Socio socio, Date desde, Date hasta, ArrayList<SesionEntrenamiento> sesiones,
			ArrayList<Partido> partidos, ArrayList<PartidoPractica> partidosPractica) {
		AnaliticaJugador analitica = AnaliticaJugador.calcular(socio, desde, hasta, sesiones, partidos,
				partidosPractica);
		return "DESEMPEÑO DE JUGADOR\n" + analitica.resumen() + "\n";
	}

	public String reporteDesempenoSocios(ArrayList<Socio> socios, Date desde, Date hasta,
			ArrayList<SesionEntrenamiento> sesiones, ArrayList<Partido> partidos,
			ArrayList<PartidoPractica> partidosPractica) {
		String reporte = "DESEMPEÑO DE JUGADORES (" + desde + " a " + hasta + ")\n";
		for (Socio socio : socios) {
			if (socio.isActivo()) {
				reporte += AnaliticaJugador.calcular(socio, desde, hasta, sesiones, partidos, partidosPractica)
						.resumen() + "\n";
			}
		}
		return reporte;
	}
	
	
	//rf 38
	//asistencia a entrenamientos entre dos fechas: detalle por sesión finalizada
	//y resumen por socio(asistió,llegó tarde, faltó y porcentje de asistencia sobre asistecias registradas)
	
	public String reporteAsistenciaEntrenamientos(ArrayList<SesionEntrenamiento> sesiones, Date desde, Date hasta) {
		String reporte = "ASISTENCIA A ENTRENAMIENTOS\nDesde: " + desde + "\nHasta: " + hasta + "\n";
		// por socio: {asistio, llego tarde, falto, sin registrar}
		Map<Socio, int[]> resumen = new LinkedHashMap<Socio, int[]>();
		int sesionesEnRango = 0;

		for (SesionEntrenamiento s : sesiones) {
			long momento = s.getFecha().getTime();
			if (!s.isFinalizada() || momento < desde.getTime() || momento > hasta.getTime()) {
				continue;
			}
			sesionesEnRango++;
			int faltaron = 0;
			int tarde = 0;
			for (Socio convocado : s.getListaDeJugadoresConvocados()) {
				int[] cuenta = resumen.get(convocado);
				if (cuenta == null) {
					cuenta = new int[4];
					resumen.put(convocado, cuenta);
				}
				RegistroAsistencia r = s.obtenerRegistro(convocado);
				if (r == null) {
					cuenta[3]++;
				} else if (RegistroAsistencia.ASISTIO.equals(r.getEstado())) {
					cuenta[0]++;
				} else if (RegistroAsistencia.LLEGO_TARDE.equals(r.getEstado())) {
					cuenta[1]++;
					tarde++;
				} else {
					cuenta[2]++;
					faltaron++;
				}
			}
			reporte += s.getFecha() + " | " + s.getEntrenadorResponsable().getNombre() + " | "
					+ s.getInstalacionUtilizada() + " | convocados: " + s.getListaDeJugadoresConvocados().size()
					+ " | asistieron: " + s.contarAsistentes() + " (tarde: " + tarde + ") | faltaron: " + faltaron
					+ " | promedio evaluación: " + s.calcularPromedioEvaluaciones() + "\n";
		}
		reporte += "Sesiones finalizadas en el periodo: " + sesionesEnRango + "\n";

		reporte += "RESUMEN POR SOCIO\n";
		for (Map.Entry<Socio, int[]> e : resumen.entrySet()) {
			int[] c = e.getValue();
			int registradas = c[0] + c[1] + c[2];
			double porcentaje = (registradas == 0) ? 0 : (c[0] + c[1]) * 100.0 / registradas;
			reporte += e.getKey().getNombre() + " | asistió: " + c[0] + " | llegó tarde: " + c[1] + " | faltó: "
					+ c[2] + " | sin registrar: " + c[3] + " | asistencia: " + String.format("%.1f", porcentaje)
					+ "%\n";
		}
		return reporte;
	}
	
	//rf 35 estado del inventario/instalaciones e historiales
	
	//Estado del inventario de artículos, agrupado por ubicación.
	public String reporteEstadoInventario(Inventario inventario) {
		Map<String, ArrayList<ExistenciaArticulo>> porUbicacion = new LinkedHashMap<String, ArrayList<ExistenciaArticulo>>();
		for (ExistenciaArticulo e : inventario.getExistencias()) {
			String ubicacion = e.getUbicacion().getNombre();
			if (!porUbicacion.containsKey(ubicacion)) {
				porUbicacion.put(ubicacion, new ArrayList<ExistenciaArticulo>());
			}
			porUbicacion.get(ubicacion).add(e);
		}
		String reporte = "ESTADO DEL INVENTARIO\n";
		for (Map.Entry<String, ArrayList<ExistenciaArticulo>> u : porUbicacion.entrySet()) {
			reporte += "Ubicación: " + u.getKey() + "\n";
			for (ExistenciaArticulo e : u.getValue()) {
				reporte += "  " + e.getArticulo().getNombre() + " | cantidad: " + e.getCantidadDisponible() + "\n";
			}
		}
		return reporte;
	}
	
	
	//Estado de las instalaciones: disciplina, capacidad, total de reservas y si
	// están ocupadas en la fecha y bloque indicados.
	public String reporteEstadoInstalaciones(ArrayList<Instalacion> instalaciones, Date fecha, Date bloqueHorario) {
		String reporte = "ESTADO DE INSTALACIONES (" + fecha + " / " + bloqueHorario + ")\n";
		for (Instalacion i : instalaciones) {
			reporte += i.getNombre() + " | " + i.getDisciplina() + " | capacidad " + i.getCapacidadMin() + "-"
					+ i.getCapacidadMax() + " | reservas: " + i.getReservas().size() + " | "
					+ (i.estaOcupada(fecha, bloqueHorario) ? "OCUPADA" : "LIBRE") + "\n";
		}
		return reporte;
	}
	
	//Historial completo de reservas de todas las instalaciones, de la más antigua a la más reciente.
	public String historialReservas(ArrayList<Instalacion> instalaciones) {
		ArrayList<ReservaInstalacion> todas = new ArrayList<ReservaInstalacion>();
		for (Instalacion i : instalaciones) {
			todas.addAll(i.getReservas());
		}
		Collections.sort(todas, new Comparator<ReservaInstalacion>() {
			public int compare(ReservaInstalacion a, ReservaInstalacion b) {
				int c = a.getFecha().compareTo(b.getFecha());
				return (c != 0) ? c : a.getBloqueHorario().compareTo(b.getBloqueHorario());
			}
		});
		String reporte = "HISTORIAL DE RESERVAS\n";
		for (ReservaInstalacion r : todas) {
			reporte += r.getFecha() + " | " + r.getBloqueHorario() + " | " + r.getInstalacion().getNombre() + " | "
					+ r.getSocio().getNombre() + " | " + r.getModalidad() + " | jugadores: " + r.getNumJugadores()
					+ "\n";
		}
		return reporte;
	}
	
	//Historial completo de partidos de conjunto (jugados o programados) y de práctica.
	public String historialPartidos(ArrayList<Partido> partidos, ArrayList<PartidoPractica> partidosPractica) {
		String reporte = "HISTORIAL DE PARTIDOS\n";
		for (Partido p : partidos) {
			String rival = "rival externo";
			for (Equipo e : p.getEquipos()) {
				if (e != p.getEquipo1()) {
					rival = e.getNombre();
				}
			}
			String resultado = p.isJugado() ? (p.getMarcadorEquipo1() + " - " + p.getMarcadorEquipo2())
					: "programado";
			reporte += p.getFecha() + " | " + p.getDeeporte().getNombre() + " | " + p.getAmbito() + " | "
					+ p.getEquipo1().getNombre() + " vs " + rival + " | " + resultado + "\n";
		}
		for (PartidoPractica pp : partidosPractica) {
			String jugadores = "";
			for (Socio s : pp.getJugadoresParticipantes()) {
				jugadores += (jugadores.isEmpty() ? "" : ", ") + s.getNombre();
			}
			reporte += pp.getFecha() + " | " + pp.getDisciplina() + " | PRACTICA | " + pp.getInstalacion().getNombre()
					+ " | " + jugadores + " | " + (pp.isFinalizado() ? "finalizado" : "programado") + "\n";
		}
		return reporte;
	}
	
	//Historial completo de sesiones de entrenamiento.
	public String historialSesiones(ArrayList<SesionEntrenamiento> sesiones) {
		String reporte = "HISTORIAL DE SESIONES DE ENTRENAMIENTO\n";
		for (SesionEntrenamiento s : sesiones) {
			reporte += s.getFecha() + " " + s.getHora() + "h | " + s.getDuracion() + "h | "
					+ s.getEntrenadorResponsable().getNombre() + " | " + s.getInstalacionUtilizada() + " | "
					+ (s.esIndividual() ? "individual" : "grupal") + " | convocados: "
					+ s.getListaDeJugadoresConvocados().size() + " | "
					+ (s.isFinalizada() ? "finalizada, asistentes: " + s.contarAsistentes() : "abierta") + "\n";
		}
		return reporte;
	}
	
	
	
	

}