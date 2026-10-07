package Usuario;
import java.util.Date;

public class Fisioterapeuta extends Empleado {

	public Fisioterapeuta(int id, String nombre, String login, String password, Date fechaNacimiento,
			Date fechaContratacion) {
		super(id, nombre, login, password, fechaNacimiento, fechaContratacion);
	}
	
}
