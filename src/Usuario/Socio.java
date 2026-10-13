package Usuario;

import java.util.Date;

public class Socio extends Usuario implements Comprador, Participante {
	private int puntosFidelidad;
	private boolean activo;

	public Socio(int id, String nombre, String login, String password, Date fechaNacimiento, int puntosFidelidad,
			boolean activo) {
		super(id, nombre, login, password, fechaNacimiento);
		this.puntosFidelidad = puntosFidelidad;
		this.activo = activo;
	}

	public int getPuntosFidelidad() {
		return puntosFidelidad;
	}

	public void setPuntosFidelidad(int puntosFidelidad) {
		this.puntosFidelidad = puntosFidelidad;
	}

	public boolean isActivo() {
		return activo;
	}

	public void setActivo(boolean activo) {
		this.activo = activo;
	}

	public double getDescuentoPropio() {
		return 0;
	}

}
