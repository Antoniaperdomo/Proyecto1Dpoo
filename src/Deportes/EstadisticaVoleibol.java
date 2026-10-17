package Deportes;

import Usuario.Socio;

public class EstadisticaVoleibol extends EstadisticaJugadorPartido {
	private int aces;
	private int bloqueos;
	private int sets;

	public int getAces() {
		return aces;
	}

	public void setAces(int aces) {
		this.aces = aces;
	}

	public int getBloqueos() {
		return bloqueos;
	}

	public void setBloqueos(int bloqueos) {
		this.bloqueos = bloqueos;
	}

	public int getSets() {
		return sets;
	}

	public void setSets(int sets) {
		this.sets = sets;
	}

	public EstadisticaVoleibol(Socio jugador) {
		super(jugador);
		// TODO Auto-generated constructor stub
	}

	@Override
	public int getPuntajeDestacado() {
		// TODO Auto-generated method stub
		return aces + bloqueos;
	}

	@Override
	public String resumen() {
		// TODO Auto-generated method stub
		return aces + " aces, " + bloqueos + " bloqueos, " + sets + " sets, ";
	}

}
