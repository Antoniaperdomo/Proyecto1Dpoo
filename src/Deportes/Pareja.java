package Deportes;

import Usuario.Participante;

public class Pareja {
	private Participante jugador1;
	private Participante jugador2;

	public Pareja(Participante jugador1, Participante jugador2) {
		if (jugador1 == null || jugador2 == null) {
			throw new IllegalArgumentException("Una pareja necesita dos jugadores");
		}
		if (jugador1.equals(jugador2)) {
			throw new IllegalArgumentException("Una pareja no tener al mismo jugador 2 veces");
		}
		this.jugador1 = jugador1;
		this.jugador2 = jugador2;
	}

	public Participante getJugador1() {
		return jugador1;
	}

	public Participante getJugador2() {
		return jugador2;
	}

	public boolean incluye(Participante participante) {
		return jugador1.equals(participante) || jugador2.equals(participante);
	}

	public String getNombre() {
		return jugador1.getNombre() + " / " + jugador2.getNombre();
	}

}
