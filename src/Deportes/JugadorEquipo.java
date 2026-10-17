package Deportes;

import Usuario.Socio;

public class JugadorEquipo {
	private Socio socio;
	private String posiccion;
	private int numeroCamiseta;
	private int amarillasInternas;
	private int amarillasCompetencias;

	public JugadorEquipo(Socio socio, String posiccion, int numeroCamiseta) {
		super();
		this.socio = socio;
		this.posiccion = posiccion;
		this.numeroCamiseta = numeroCamiseta;
		this.amarillasInternas = 0;
		this.amarillasCompetencias = 0;
	}

	public Socio getSocio() {
		return socio;
	}

	public String getPosiccion() {
		return posiccion;
	}

	public int getNumeroCamiseta() {
		return numeroCamiseta;
	}

	public int getAmarillas(String Ambito) {
		if (Suspension.INTERNO.equals(Ambito)) {
			return amarillasInternas;
		}
		return amarillasCompetencias;
	}

	public void setAmarillas(String Ambito, int cantidad) {
		if (Suspension.INTERNO.equals(Ambito)) {
			amarillasInternas = cantidad;
		} else {
			amarillasCompetencias = cantidad;
		}
	}

}
