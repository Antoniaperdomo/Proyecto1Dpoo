package Deportes;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import Usuario.Socio;

public class PartidoPractica {
	private Date fecha;
	private String Disciplina;
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
			if (socio.b) {
			}
		}
		this.fecha = fecha;
		this.Disciplina = disciplina;
		this.instalacion = instalacion;
		this.jugadoresParticipantes = jugadoresParticipantes;
		this.estadisticasPorJugador = new HashMap<Socio, EstadisticaPractica>();
		this.programadoPorEntrenador = programadoPorEntrenador;
		this.finalizado = false;
	}

}
