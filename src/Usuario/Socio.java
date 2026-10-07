package Usuario;
import java.util.Date;

public class Socio extends Usuario {
	private int puntosFidelidad;

	public Socio(int id, String nombre, String login, String password, Date fechaNacimiento, int puntosFidelidad) {
		super(id, nombre, login, password, fechaNacimiento);
		this.puntosFidelidad = puntosFidelidad;
	}

	public int getPuntosFidelidad() {
		return puntosFidelidad;
	}

	public void setPuntosFidelidad(int puntosFidelidad) {
		this.puntosFidelidad = puntosFidelidad;
	}

}
