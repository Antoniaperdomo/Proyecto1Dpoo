package Deportes;

import Usuario.Participante;

public class Futbol extends DeporteConjunto {

	public Futbol() {
		super("FUTBOL", 7);
	}

	@Override
	public EstadisticaJugadorPartido crearEstadistica(Participante jugador) {
		return new EstadisticaFutbol(jugador);
	}

	@Override
	public int calcularPuntos(int marcadorPropio, int marcadorRival) {
		if (marcadorPropio > marcadorRival)
			return 3;
		if (marcadorPropio == marcadorRival)
			return 1;
		return 0;

	}

}
