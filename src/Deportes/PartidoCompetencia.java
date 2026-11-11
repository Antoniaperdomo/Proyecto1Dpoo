package Deportes;

import java.util.ArrayList;
import java.util.Date;

import Competencias.Competencia;
import Competencias.NominaCompetencia;

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
		if (competencia != null) {
			competencia.agregarPartido(this);
		}
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

	@Override
	public void validar() {
		super.validar();
		if (competencia == null) {
			return;
		}
		if (!competencia.getDeporte().getNombre().equals(getDeeporte().getNombre())) {
			throw new IllegalStateException(
					"La competencia " + competencia.getNombre() + " no es de " + getDeeporte().getNombre());
		}
		if (!getEquipo1().getCategoria().esIgual(competencia.getCategoria())) {
			throw new IllegalStateException(
					"El equipo " + getEquipo1().getNombre() + " no es de la categoria de " + competencia.getNombre());
		}
		NominaCompetencia nomina = competencia.buscarNomina(getEquipo1());
		if (nomina == null) {
			throw new IllegalStateException(
					"El equipo " + getEquipo1().getNombre() + " no esta inscrito en " + competencia.getNombre());
		}
		if (!competencia.puedeJugar(getEquipo1())) {
			throw new IllegalStateException("El equipo " + getEquipo1().getNombre() + " no esta en la fase actual de "
					+ competencia.getNombre());
		}
		if (nomina.contarDisponibles() < getDeeporte().getMinJugadores()) {
			throw new IllegalStateException(
					"La nomina de " + getEquipo1().getNombre() + " no tiene el minimo de jugadores disponibles");
		}
		for (EstadisticaJugadorPartido est : getEstadisticas()) {
			if (!nomina.incluye(est.getJugador())) {
				throw new IllegalStateException(est.getJugador().getNombre() + " no esta en la nomina de "
						+ getEquipo1().getNombre() + " para " + competencia.getNombre());
			}
		}
	}

	@Override
	public void cerrar() {
		int puntos = 0;
		if (competencia != null) {
			puntos = getDeeporte().calcularPuntos(getMarcadorEquipo1(), getMarcadorEquipo2());
		}
		super.cerrar();
		if (competencia != null) {
			competencia.regustrarResultado(getEquipo1(), puntos);
		}
	}

}
