package Deportes;

import Usuario.Socio;

public abstract class DeporteConjunto extends Deporte {
	private final int minJugadores;

	public DeporteConjunto(String nombre, int minJugadores) {
		super(nombre);
		this.minJugadores = minJugadores;
	}

	public int getMinJugadores() {
		return minJugadores;
	}

	@Override
	public boolean esIndividual() {
		return false;
	}

	// RF-05

	public abstract EstadisticaJugadorPartido crearEstadistica(Socio jugador);

	// RF-11
	public abstract int calcularPuntos(int marcadorPropio, int marcadorRival);

}
