package Deportes;

import Usuario.Socio;

public abstract class EstadisticasJugadorPartido {
	private Socio jugador;

	public EstadisticasJugadorPartido(Socio jugador) {
		super();
		this.jugador = jugador;
	}

	// RF 13
	public abstract int getPuntajeDestacado();

	public abstract String resumen();

}
