package Competencias;
import java.util.ArrayList;

import Usuario.Socio;

public class NominaCompetencia {
	
	private Equipo equipo;
	private Competencia competencia;
	private ArrayList<Socio> jugadoresHabilitados;
	public NominaCompetencia(Equipo equipo, Competencia competencia, ArrayList<Socio> jugadoresHabilitados) {
		super();
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
	public ArrayList<Socio> getJugadoresHabilitados() {
		return jugadoresHabilitados;
	}
	public void setJugadoresHabilitados(ArrayList<Socio> jugadoresHabilitados) {
		this.jugadoresHabilitados = jugadoresHabilitados;
	}
	
	

}
