package Deportes;

import java.util.ArrayList;
import java.util.Date;

import Competencias.Competencia;

public class PartidoCompetencia extends Partido {
	private Institucion institucion;
	private String escenarioJugador;
	private Competencia competencia;

	public PartidoCompetencia(Date fecha, DeporteConjunto deeporte, Equipo equipo1, Institucion institucion,
			String escenarioJugador) {
		super(fecha, deeporte, equipo1);
		this.institucion = institucion;
		this.escenarioJugador = escenarioJugador;
	}
	
	public Competencia getCompetencia() {
		return competencia;
	}
	
	public void setCompetencia(Competencia competencia) {
		this.competencia = competencia;
	}

	public Institucion getInstitucion() {
		return institucion;
	}

	public void setInstitucion(Institucion institucion) {
		this.institucion = institucion;
	}

	public String getEscenarioJugador() {
		return escenarioJugador;
	}

	public void setEscenarioJugador(String escenarioJugador) {
		this.escenarioJugador = escenarioJugador;
	}

	@Override
	public ArrayList<Equipo> getEquipos() {
		ArrayList<Equipo> lista = new ArrayList<Equipo>();
		lista.add(getEquipo1());
		return lista;
	}

	@Override
	public String getAmbito() {
		return Suspension.COMPETENCIA;
	}

}
