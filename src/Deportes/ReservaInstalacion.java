package Deportes;

import java.util.Date;

import Usuario.Socio;

public class ReservaInstalacion {
	private Socio socio;
	private Instalacion instalacion;
	private Date fecha;
	private Date bloqueHorario;
	private String modalidad;
	private int numJugadores;

	public ReservaInstalacion(Socio socio, Instalacion instalacion, Date fecha, Date bloqueHorario, String modalidad,
			int numJugadores) {
		this.socio = socio;
		this.instalacion = instalacion;
		this.fecha = fecha;
		this.bloqueHorario = bloqueHorario;
		this.modalidad = modalidad;
		this.numJugadores = numJugadores;
	}

	public Instalacion getInstalacion() {
		return instalacion;
	}

	public Date getFecha() {
		return fecha;
	}

	public Date getBloqueHorario() {
		return bloqueHorario;
	}

	public String getModalidad() {
		return modalidad;
	}

	public int getNumJugadores() {
		return numJugadores;
	}

	public Socio getSocio() {
		return socio;
	}

	public void setSocio(Socio socio) {
		this.socio = socio;
	}

}
