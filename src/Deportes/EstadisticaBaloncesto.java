package Deportes;

import Usuario.Socio;

public class EstadisticaBaloncesto extends EstadisticaJugadorPartido {
	private int puntos;
	private int rebotes;

	public EstadisticaBaloncesto(Socio jugador) {
		super(jugador);
		// TODO Auto-generated constructor stub
	}

	@Override
	public int getPuntajeDestacado() {
		// TODO Auto-generated method stub
		return puntos;
	}

	@Override
	public String resumen() {
		// TODO Auto-generated method stub
		return puntos + " puntos," + rebotes + " rebotes";
	}

}
