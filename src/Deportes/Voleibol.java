package Deportes;

import Usuario.Socio;

public class Voleibol extends DeporteConjunto {

	public Voleibol() {
		super("VOLEIBOL", 6);
	}

	@Override
	public EstadisticaJugadorPartido crearEstadistica(Socio jugador) {
		return new EstadisticaVoleibol(jugador);
	}

	@Override
	public int calcularPuntos(int marcadorPropio, int marcadorRival) {
		if (marcadorPropio > marcadorRival) {
			if (marcadorRival <= 1) {
				return 3;
			}
			return 2;
		}
		if (marcadorPropio == 2) {
			return 1;
		}
		return 0;
	}

}
