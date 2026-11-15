package Competencias;

import java.util.ArrayList;

import Deportes.Equipo;
import Usuario.Participante;
import Deportes.Suspension;

public class NominaCompetencia {

	private Equipo equipo;
	private Competencia competencia;
	private ArrayList<Participante> jugadoresHabilitados;

//RF 09
	public NominaCompetencia(Equipo equipo, Competencia competencia, ArrayList<Participante> jugadoresHabilitados) {
		super();
		if (equipo == null || competencia == null || jugadoresHabilitados == null) {
			throw new IllegalArgumentException("La nomina necesita equipo, competencia y jugadores");
		}
		if (!equipo.isActivo()) {
			throw new IllegalArgumentException(
					"El equipo " + equipo.getNombre() + " no es de " + competencia.getDeporte().getNombre());
		}
		if (!equipo.getDeporte().getNombre().equals(competencia.getDeporte().getNombre())) {
			throw new IllegalArgumentException(
					"El equipo " + equipo.getNombre() + " no es de " + competencia.getDeporte().getNombre());
		}
		if (!equipo.getCategoria().esIgual(competencia.getCategoria())) {
			throw new IllegalArgumentException("El equipo " + equipo.getNombre() + " no es de la categoria "
					+ competencia.getCategoria().getNombre());
		}
		for (int i = 0; i < jugadoresHabilitados.size(); i++) {
			Participante s = jugadoresHabilitados.get(i);
			if (!equipo.tieneJugador(s)) {
				throw new IllegalArgumentException(s.getNombre() + " no pertenece al equipo " + equipo.getNombre());
			}
			for (int j = i + 1; j < jugadoresHabilitados.size(); j++) {
				if (jugadoresHabilitados.get(j) == s) {
					throw new IllegalArgumentException(s.getNombre() + " esta repetido en la nomina");
				}
			}
		}
		if (jugadoresHabilitados.size() < equipo.getDeporte().getMinJugadores()) {
			throw new IllegalArgumentException(
					"La nomina necesita minimo " + equipo.getDeporte().getMinJugadores() + " jugadores");
		}

		this.equipo = equipo;
		this.competencia = competencia;
		this.jugadoresHabilitados = jugadoresHabilitados;
	}

	public Equipo getEquipo() {
		return equipo;
	}

	public void setEquipo(Equipo equipo) {
		this.equipo = equipo;
	}

	public Competencia getCompetencia() {
		return competencia;
	}

	public void setCompetencia(Competencia competencia) {
		this.competencia = competencia;
	}

	public ArrayList<Participante> getJugadoresHabilitados() {
		return jugadoresHabilitados;
	}

	public void setJugadoresHabilitados(ArrayList<Participante> jugadoresHabilitados) {
		this.jugadoresHabilitados = jugadoresHabilitados;
	}

	// RF 10
	public boolean incluye(Participante Participante) {
		for (Participante s : jugadoresHabilitados) {
			if (s == Participante) {
				return true;
			}
		}
		return false;
	}

	public int contarDisponibles() {
		int total = 0;
		for (Participante s : jugadoresHabilitados) {
			if (!equipo.estaSuspendido(s, Suspension.COMPETENCIA)) {
				total++;
			}
		}
		return total;
	}

}
