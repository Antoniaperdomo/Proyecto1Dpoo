package Usuario;

import java.util.Date;

public class Entrenador extends Empleado implements Participante {

	public Entrenador(int id, String nombre, String login, String password, Date fechaNacimiento,
			Date fechaContratacion) {
		super(id, nombre, login, password, fechaNacimiento, fechaContratacion);
	}

}
