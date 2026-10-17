package Deportes;

import Usuario.Socio;

public abstract class EstadisticaJugadorPartido {
	private Socio jugador;

	public EstadisticaJugadorPartido(Socio jugador) {
		super();
		this.jugador = jugador;
	}

	public Socio getJugador() {
		return jugador;
	}

	// RF-13
	public abstract int getPuntajeDestacado();

	public abstract String resumen();

	public void procesarTarjetas(Equipo equipo, String ambito) {
	}

}
