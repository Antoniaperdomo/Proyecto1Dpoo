package Deportes;

import Usuario.Socio;

public class Suspension {
	public static final String INTERNO = "INTERNO";
	public static final String COMPETENCIA = "COMPETENCIA";

	private Socio jugador;
	private String motivoSuspension;
	private String ambito;
	private boolean cumplida;

	public Suspension(Socio jugador, String motivoSuspension, String ambito, boolean cumplida) {
		if (!INTERNO.equals(ambito) && !COMPETENCIA.equals(ambito)) {
			throw new IllegalArgumentException("Ambito Invalido: " + ambito);
		}
		this.jugador = jugador;
		this.motivoSuspension = motivoSuspension;
		this.ambito = ambito;
		this.cumplida = cumplida;
	}

	public Socio getJugador() {
		return jugador;
	}

	public void setJugador(Socio jugador) {
		this.jugador = jugador;
	}

	public String getMotivoSuspension() {
		return motivoSuspension;
	}

	public void setMotivoSuspension(String motivoSuspension) {
		this.motivoSuspension = motivoSuspension;
	}

	public String getAmbito() {
		return ambito;
	}

	public void setAmbito(String ambito) {
		this.ambito = ambito;
	}

	public boolean isCumplida() {
		return cumplida;
	}

	public void cumplir() {
		this.cumplida = true;
	}

}
