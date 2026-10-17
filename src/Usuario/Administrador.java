package Usuario;

import java.util.ArrayList;
import java.util.Date;

import Administradores.SolicitudCambioTurno;
import Administradores.SugerenciaProducto;
import Administradores.Turno;
import Deportes.Categoria;
import Deportes.DeporteConjunto;
import Deportes.Equipo;
import Deportes.JugadorEquipo;
import Deportes.Suspension;

public class Administrador extends Usuario {

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
		for (Turno t:todosLosTurnos) {
			if(t != solicitud.getTurnoActual() && t.getEmpleado().getId() == solicitud.getEmpleado().getId() && t.seCruzaCon(solicitud.getTurnoPropuesto())) {
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
			ArrayList<Turno> todosLosTurnos) throws IllegalArgumentException{
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
			ArrayList<Usuario> todosLosUsuarios) throws IllegalArgumentException{
		for (Usuario u : todosLosUsuarios) {
			if (u.getLogin().equals(login)) {
				throw new IllegalArgumentException("El login ya existe");
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
			ArrayList<JugadorEquipo> jugadores, ArrayList<Suspension> suspensiones, ArrayList<Equipo> todosLosEquipos) throws IllegalArgumentException {
		if(cupoMax <=0) {
			throw new IllegalArgumentException("El cupo máximo debe ser mayor a cero");
		}
		
		for (Equipo e:todosLosEquipos) {
			if(e.getNombre().equals(nombre)) {
				throw new IllegalArgumentException("Ya existe un equipo con este nombre");
			}
		}
		Equipo equipo = new Equipo(nombre, deporte, cupoMax, categoria, new ArrayList<JugadorEquipo>(), new ArrayList<Suspension>(), true);
		todosLosEquipos.add(equipo);
		return equipo;
	}
	
	public void inscribirEnEquipo(Socio socio, Equipo equipo, String posicion, int numCamiseta, ArrayList<Equipo> todosLosEquipos) {
		for(Equipo e: todosLosEquipos) {
			if(e != equipo && e.isActivo()
					&& e.getDeporte().getNombre().equals(equipo.getDeporte().getNombre())
					&& e.tieneJugador(socio)) {
				throw new IllegalStateException("El socio ya está en otro equipo de este deporte");
			}
		}
		equipo.agregarJugador(socio, posicion, numCamiseta);
	}
	
	public void darDeBajaEquipo(Equipo equipo) {
		equipo.setActivo(false);
	}
	
	public void reabastecer
}
