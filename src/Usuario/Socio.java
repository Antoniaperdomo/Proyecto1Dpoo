package Usuario;

import java.util.ArrayList;
import java.util.Date;

import Deportes.Equipo;
import Deportes.InscripcionIndividual;
import Deportes.PartidoPractica;
import Deportes.ReservaInstalacion;

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
	
	//RF 02
	public void unirseEquipo(Equipo equipo, String posicion, int numeroCamiseta)

	public InscripcionIndividual buscarInscripcion(String disciplina) {
		for (InscripcionIndividual insc : inscripciones) {

		}
	}

}
