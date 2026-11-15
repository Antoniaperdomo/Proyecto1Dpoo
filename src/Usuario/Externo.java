package Usuario;

import java.util.Date;

public class Externo extends Usuario implements Participante {

	private boolean estudianteUniversidad;

	public Externo(int id, String nombre, String login, String password, Date fechaNacimiento,
			boolean estudianteUniversidad) {
		super(id, nombre, login, password, fechaNacimiento);
		// TODO Auto-generated constructor stub
		this.estudianteUniversidad = estudianteUniversidad;
	}

	public boolean isEstudianteUniversidad() {
		return estudianteUniversidad;
	}

	public void setEstudianteUniversidad(boolean estudianteUniversidad) {
		this.estudianteUniversidad = estudianteUniversidad;
	}
	

}
