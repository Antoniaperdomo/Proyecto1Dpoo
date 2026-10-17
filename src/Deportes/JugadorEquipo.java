package Deportes;

import Usuario.Socio;

public class JugadorEquipo {
	private Socio socio;
	private Equipo equipo;
	private String posiccion;
	private int numeroCamiseta;
	private int amarillasInternas;
	private int amarillasCompetencias;

	public JugadorEquipo(Socio socio, String posiccion, Equipo equipo, int numeroCamiseta) {
		super();
		this.socio = socio;
		this.equipo = equipo;
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

	public Equipo getEquipo() {
		return equipo;
	}

	public void setEquipo(Equipo equipo) {
		this.equipo = equipo;
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
