package Deportes;

import Usuario.Socio;

public class Baloncesto extends DeporteConjunto {

	public Baloncesto() {
		super("BALOMCESTO", 5);
	}

	@Override
	public EstadisticaJugadorPartido crearEstadistica(Socio jugador) {
		return new EstadisticaBaloncesto;
	}

	@Override
	public int calcularPuntos(int marcadorPropio, int marcadorRival) {

		if (marcadorPropio == marcadorRival) {
			throw new IllegalArgumentException("En baloncesto no hay empates.");
		} else if (marcadorPropio > marcadorRival) {
			return 2;
		}
		return 1;
	}

}
