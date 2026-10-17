package Deportes;

import java.util.ArrayList;
import java.util.Date;

public abstract class Instalacion {
	private final int capacidadMin;
	private final int capacidadMax;
	private ArrayList<ReservaInstalacion> reservas;

	public Instalacion(int capacidadMin, int capacidadMax) {
		this.capacidadMin = capacidadMin;
		this.capacidadMax = capacidadMax;
		this.reservas = new ArrayList<ReservaInstalacion>();
	}

	public ArrayList<ReservaInstalacion> getReservas() {
		return reservas;
	}

	public void setReservas(ArrayList<ReservaInstalacion> reservas) {
		this.reservas = reservas;
	}

	public int getCapacidadMin() {
		return capacidadMin;
	}

	public int getCapacidadMax() {
		return capacidadMax;
	}

	public abstract String getDisciplina();

	// RF 18

	public boolean aceptaJugadores(int numJugadores) {
		return numJugadores >= capacidadMin && numJugadores <= capacidadMax;
	}

	public boolean estaOcupada(Date fecha, Date bloqueHorario) {
		for (ReservaInstalacion ri : reservas) {
			if (ri.getFecha().equals(fecha) && ri.getBloqueHorario().equals(bloqueHorario)) {
				return true;
			}
		}
		return false;
	}

	public void agregarReserva(ReservaInstalacion reserva) {
		reservas.add(reserva);
	}

}
