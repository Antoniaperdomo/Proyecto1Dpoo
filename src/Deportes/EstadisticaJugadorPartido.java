package Deportes;

import Usuario.Participante;

public abstract class EstadisticaJugadorPartido {
	private Participante jugador;

	public EstadisticaJugadorPartido(Participante jugador) {
		super();
		this.jugador = jugador;
	}

	public Participante getJugador() {
		return jugador;
	}

	// RF-13
	public abstract int getPuntajeDestacado();

	public abstract String resumen();

	public void procesarTarjetas(Equipo equipo, String ambito) {
	}

}
