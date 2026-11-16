package Torneos;

import java.util.ArrayList;

import Deportes.Equipo;
import Deportes.JugadorEquipo;
import Deportes.Pareja;
import Usuario.Empleado;
import Usuario.Participante;

public class InscripcionTorneo {
	public static final String REGULAR = "REGULAR";
	public static final String PREFERENCIAL = "PREFERENCIAL";

	private Equipo equipo;
	private Participante jugador;
	private Pareja pareja;
	private String tipoCupo;
	private double tarifaPagada;

	/** Inscripcion de un equipo en un torneo de conjunto. */
	public InscripcionTorneo(Equipo equipo, String tipoCupo, double tarifaPagada) {
		if (equipo == null) {
			throw new IllegalArgumentException("El equipo es obligatorio");
		}
		this.equipo = equipo;
		asignar(tipoCupo, tarifaPagada);
	}

	/** Inscripcion de un jugador solo (sencillos). */
	public InscripcionTorneo(Participante jugador, String tipoCupo, double tarifaPagada) {
		if (jugador == null) {
			throw new IllegalArgumentException("El jugador es obligatorio");
		}
		this.jugador = jugador;
		asignar(tipoCupo, tarifaPagada);
	}

	/** Inscripcion de una pareja (dobles): ocupa un solo cupo. */
	public InscripcionTorneo(Pareja pareja, String tipoCupo, double tarifaPagada) {
		if (pareja == null) {
			throw new IllegalArgumentException("La pareja es obligatoria");
		}
		this.pareja = pareja;
		asignar(tipoCupo, tarifaPagada);
	}

	private void asignar(String tipoCupo, double tarifaPagada) {
		if (!REGULAR.equals(tipoCupo) && !PREFERENCIAL.equals(tipoCupo)) {
			throw new IllegalArgumentException("Tipo de cupo invalido: " + tipoCupo);
		}
		if (tarifaPagada < 0) {
			throw new IllegalArgumentException("La tarifa no puede ser negativa");
		}
		this.tipoCupo = tipoCupo;
		this.tarifaPagada = tarifaPagada;
	}

	public Equipo getEquipo() {
		return equipo;
	}

	public String getTipoCupo() {
		return tipoCupo;
	}

	public double getTarifaPagada() {
		return tarifaPagada;
	}

	/** Las personas que recibirian un premio si esta inscripcion gana. */
	public ArrayList<Participante> getIntegrantes() {
		ArrayList<Participante> lista = new ArrayList<Participante>();
		if (equipo != null) {
			for (JugadorEquipo j : equipo.getJugadores()) {
				lista.add(j.getParticipante());
			}
		} else if (pareja != null) {
			lista.add(pareja.getJugador1());
			lista.add(pareja.getJugador2());
		} else {
			lista.add(jugador);
		}
		return lista;
	}

	public boolean incluye(Participante p) {
		for (Participante i : getIntegrantes()) {
			if (i.getId() == p.getId()) {
				return true;
			}
		}
		return false;
	}

	public boolean esDeEquipo(Equipo otro) {
		return equipo != null && equipo == otro;
	}

	/** Si algun integrante es empleado, no puede recibir premio en efectivo. */
	public boolean tieneEmpleado() {
		for (Participante p : getIntegrantes()) {
			if (p instanceof Empleado) {
				return true;
			}
		}
		return false;
	}

	public String getNombre() {
		if (equipo != null) {
			return equipo.getNombre();
		}
		if (pareja != null) {
			return pareja.getNombre();
		}
		return jugador.getNombre();
	}

}
