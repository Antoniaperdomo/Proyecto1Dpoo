package Deportes;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import Usuario.Socio;

public class PartidoPractica {
	private Date fecha;
	private String disciplina;
	private Instalacion instalacion;
	private ArrayList<Socio> jugadoresParticipantes;
	private HashMap<Socio, EstadisticaPractica> estadisticasPorJugador;
	private boolean programadoPorEntrenador;
	private boolean finalizado;

	public PartidoPractica(Date fecha, String disciplina, Instalacion instalacion,
			ArrayList<Socio> jugadoresParticipantes, boolean programadoPorEntrenador) {
		if (!instalacion.getDisciplina().equals(disciplina)) {
			throw new IllegalArgumentException("La instalacion no sirve para " + disciplina);
		}
		if (!instalacion.aceptaJugadores(jugadoresParticipantes.size())) {
			throw new IllegalArgumentException("El numero de jugadores no se permite en la instalacion");
		}
		for (Socio socio : jugadoresParticipantes) {
			if (socio.buscarInscripcion(disciplina) == null) {
				throw new IllegalStateException(socio.getNombre() + " no esta inscrito en " + disciplina);
			}
		}
		this.fecha = fecha;
		this.disciplina = disciplina;
		this.instalacion = instalacion;
		this.jugadoresParticipantes = jugadoresParticipantes;
		this.estadisticasPorJugador = new HashMap<Socio, EstadisticaPractica>();
		this.programadoPorEntrenador = programadoPorEntrenador;
		this.finalizado = false;
	}

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	public String getDisciplina() {
		return disciplina;
	}

	public void setDisciplina(String disciplina) {
		this.disciplina = disciplina;
	}

	public Instalacion getInstalacion() {
		return instalacion;
	}

	public void setInstalacion(Instalacion instalacion) {
		this.instalacion = instalacion;
	}

	public ArrayList<Socio> getJugadoresParticipantes() {
		return jugadoresParticipantes;
	}

	public void setJugadoresParticipantes(ArrayList<Socio> jugadoresParticipantes) {
		this.jugadoresParticipantes = jugadoresParticipantes;
	}

	public HashMap<Socio, EstadisticaPractica> getEstadisticasPorJugador() {
		return estadisticasPorJugador;
	}

	public void setEstadisticasPorJugador(HashMap<Socio, EstadisticaPractica> estadisticasPorJugador) {
		this.estadisticasPorJugador = estadisticasPorJugador;
	}

	public boolean isProgramadoPorEntrenador() {
		return programadoPorEntrenador;
	}

	public void setProgramadoPorEntrenador(boolean programadoPorEntrenador) {
		this.programadoPorEntrenador = programadoPorEntrenador;
	}

	public boolean isFinalizado() {
		return finalizado;
	}

	public void setFinalizado(boolean finalizado) {
		this.finalizado = finalizado;
	}

	// RF 19

	public void validarNiveles() {
		if (programadoPorEntrenador) {
			return;
		}
		for (int i = 0; i < jugadoresParticipantes.size(); i++) {
			for (int j = i + 1; j < jugadoresParticipantes.size(); j++) {
				Socio a = jugadoresParticipantes.get(i);
				Socio b = jugadoresParticipantes.get(j);
				DeporteIndividual nivelA = a.buscarInscripcion(disciplina).getDeporte();
				DeporteIndividual nivelB = b.buscarInscripcion(disciplina).getDeporte();
				if (!nivelA.puedeJugarCon(nivelB)) {
					throw new IllegalStateException(
							"Los niveles de " + a.getNombre() + " y " + b.getNombre() + " no permiten jugar juntos");
				}
			}
		}
	}

	public void registrarEnJugadores() {
		for (Socio socio : jugadoresParticipantes) {
			socio.agregarPartidoPractica(this);
		}

	}
	//Rf 20
	public void registrarEstadisticas(Socio socio, EstadisticaPractica est) {
		if(!jugadoresParticipantes.contains(socio)) {
			throw new IllegalArgumentException ("El socio no participo en este partido");
		}
		DeporteIndividual deporte = socio.buscarInscripcion(disciplina).getDeporte();
		if(est.getClass() != deporte.crearEstadistica().getClass()) {
			throw new IllegalArgumentException("La estadistica no corresponde a " + disciplina);
		}
		estadisticasPorJugador.put(socio, est);
	}
	//rf 21
	public String finalizar() {
		if (finalizado) {
			throw new IllegalStateException("El partido ya fue finalizado");
		}
		if (jugadoresParticipantes.size() != 2) {
			throw new IllegalStateException("El ELO se calcula en partidos de 2");
		}
		Socio a = jugadoresParticipantes.get(0);
		Socio b = jugadoresParticipantes.get(1);
		EstadisticaPractica estA = estadisticasPorJugador.get(a);
		EstadisticaPractica estB = estadisticasPorJugador.get(b);
		if(estA == null || estB == null) {
			throw new IllegalStateException("Faltan las estadisticas de alguno de los jugadores");
		}
		
		double resultadoA = 0.5;
		if (estA.getSetsGanados() > estB.getSetsGanados()) {
			resultadoA = 1;
		}
		if (estA.getSetsGanados() < estB.getSetsGanados()) {
			resultadoA = 0;
		}
		
		InscripcionIndividual inscA = a.buscarInscripcion(disciplina);
		InscripcionIndividual inscB = b.buscarInscripcion(disciplina);
		int eloAntesA = inscA.getRankingELO();
		int eloAntesB = inscB.getRankingELO();
		
		inscA.actualizarRanking(resultadoA, eloAntesB);
		inscB.actualizarRanking(1-resultadoA, eloAntesA);
		finalizado = true;
		
		return a.getNombre() + "(" + disciplina + "): ELO " + eloAntesA + " -> " + inscA.getRankingELO() + "\n"
				+ b.getNombre() + "(" + disciplina + "): ELO " + eloAntesB + " -> " + inscB.getRankingELO();
				
	}

}
