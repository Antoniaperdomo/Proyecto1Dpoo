package Deportes;

import Usuario.Socio;

public abstract class EstadisticasJugadorPartido {
	private Socio jugador;

	public EstadisticasJugadorPartido(Socio jugador) {
		super();
		this.setJugador(jugador);
	}

	// RF 13
	public abstract int getPuntajeDestacado();

	public abstract String resumen();

	public Socio getJugador() {
		return jugador;
	}

	public void setJugador(Socio jugador) {
		this.jugador = jugador;
	}

}
