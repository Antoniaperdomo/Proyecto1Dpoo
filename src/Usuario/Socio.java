package Usuario;

import java.util.ArrayList;
import java.util.Date;

import Deportes.DeporteIndividual;
import Deportes.Equipo;
import Deportes.InscripcionIndividual;
import Deportes.Instalacion;
import Deportes.Padel;
import Deportes.PartidoPractica;
import Deportes.ReservaInstalacion;
import Deportes.Tenis;
import Deportes.TenisMesa;

public class Socio extends Usuario implements Comprador, Participante {
	private int puntosFidelidad;
	private boolean activo;
	private ArrayList<Equipo> equipos;
	private ArrayList<InscripcionIndividual> inscripciones;
	private ArrayList<ReservaInstalacion> reservas;
	private ArrayList<PartidoPractica> partidosPractica;

	public Socio(int id, String nombre, String login, String password, Date fechaNacimiento, int puntosFidelidad,
			boolean activo, ArrayList<Equipo> equipos, ArrayList<InscripcionIndividual> inscripciones,
			ArrayList<ReservaInstalacion> reservas, ArrayList<PartidoPractica> partidosPractica) {
		super(id, nombre, login, password, fechaNacimiento);
		this.puntosFidelidad = puntosFidelidad;
		this.activo = activo;
		this.equipos = equipos;
		this.inscripciones = inscripciones;
		this.reservas = reservas;
		this.partidosPractica = partidosPractica;
	}

	@Override
	public double getDescuentoPropio() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getPuntosFidelidad() {
		return puntosFidelidad;
	}

	public void setPuntosFidelidad(int puntosFidelidad) {
		this.puntosFidelidad = puntosFidelidad;
	}

	public boolean isActivo() {
		return activo;
	}

	public void setActivo(boolean activo) {
		this.activo = activo;
	}

	public int getEdad() {
		long milisegundosPorAnio = 31557600000L;
		long diferencia = new Date().getTime() - fechaNacimiento.getTime();
		return (int) (diferencia / milisegundosPorAnio);

	}

	public ArrayList<Equipo> getEquipos() {
		return equipos;
	}

	public ArrayList<InscripcionIndividual> getInscripciones() {
		return inscripciones;
	}

	public void setInscripciones(ArrayList<InscripcionIndividual> inscripciones) {
		this.inscripciones = inscripciones;
	}

	public ArrayList<ReservaInstalacion> getReservas() {
		return reservas;
	}

	public void setReservas(ArrayList<ReservaInstalacion> reservas) {
		this.reservas = reservas;
	}

	public ArrayList<PartidoPractica> getPartidosPractica() {
		return partidosPractica;
	}

	public void setPartidosPractica(ArrayList<PartidoPractica> partidosPractica) {
		this.partidosPractica = partidosPractica;
	}

	// RF 02
	public void unirseEquipo(Equipo equipo, String posicion, int numeroCamiseta) {
		for (Equipo n : equipos) {
			if (n.getDeporte().getNombre().equals(equipo.getDeporte().getNombre())) {
				throw new IllegalStateException("Ya pertenece a un equipo de " + equipo.getDeporte().getNombre());
			}
		}
		equipo.agregarJugador(this, posicion, numeroCamiseta);
		equipos.add(equipo);
	}

	// RF 15, 16

	public InscripcionIndividual buscarInscripcion(String disciplina) {
		for (InscripcionIndividual insc : inscripciones) {
			if (insc.getDeporte().getNombre().equals(disciplina)) {
				return insc;
			}

		}
		return null;
	}

	public InscripcionIndividual inscribirse(String disciplina) {
		if (buscarInscripcion(disciplina) != null) {
			throw new IllegalStateException("Ya esta inscrito en " + disciplina);
		}
		DeporteIndividual deporte;
		if ("TENIS".equals(disciplina)) {
			deporte = new Tenis(DeporteIndividual.PRINCIPIANTE);
		} else if ("PADEL".equals(disciplina)) {
			deporte = new Padel(DeporteIndividual.PRINCIPIANTE);
		} else if ("TENISDEMESA".equals(disciplina)) {
			deporte = new TenisMesa(DeporteIndividual.PRINCIPIANTE);
		} else {
			throw new IllegalArgumentException("Disciplina no disponiblae: " + disciplina);
		}
		InscripcionIndividual inscripcion = new InscripcionIndividual(this, deporte);
		inscripciones.add(inscripcion);
		return inscripcion;

	}

	// RF 18
	public ReservaInstalacion reservar(ArrayList<Instalacion> instalaciones, String disciplina, Date fecha,
			Date bloqueHorario, String modalidad, int numJugadores) {
		if (buscarInscripcion(disciplina) == null) {
			throw new IllegalStateException("No esta inscrito en " + disciplina);
		}
		boolean capacidad = false;
		for (Instalacion i : instalaciones) {
			if (i.getDisciplina().equals(disciplina) && i.aceptaJugadores(numJugadores)) {
				capacidad = true;
				if (!i.estaOcupada(fecha, bloqueHorario)) {
					ReservaInstalacion reserva = new ReservaInstalacion(this, i, fecha, bloqueHorario, modalidad,
							numJugadores);
					i.agregarReserva(reserva);
					reservas.add(reserva);
					return reserva;
				}
			}
		}
		if (!capacidad) {
			throw new IllegalArgumentException("Numero de jugadores no es permitido para la instalacion");
		}
		throw new IllegalStateException("Todas las instalaciones estan ocupadas");

	}

	// RF 19
	public PartidoPractica progamarPartido(Date fecha, String disciplina, Instalacion instalacion,
			ArrayList<Socio> jugadores) {
		return new PartidoPractica(fecha, disciplina, instalacion, jugadores, false);
	}
	
	public void agregarPartidoPractica(PartidoPractica partido) {
		partidosPractica.add(partido);
	}

}
