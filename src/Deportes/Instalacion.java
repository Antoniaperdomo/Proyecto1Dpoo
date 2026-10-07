package Deportes;

public abstract class Instalacion {
	private final int capacidadMin;
	private final int capacidadMax;

	public Instalacion(int capacidadMin, int capacidadMax) {
		this.capacidadMin = capacidadMin;
		this.capacidadMax = capacidadMax;
	}

	public int getCapacidadMin() {
		return capacidadMin;
	}

	public int getCapacidadMax() {
		return capacidadMax;
	}

	// RF 18//

	public boolean aceptaJugadores(int numJugadores) {
		return numJugadores >= capacidadMin && numJugadores <= capacidadMax;
	}

}
