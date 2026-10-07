package Usuario;
import java.util.Date;

public abstract class Empleado extends Usuario {
	
	private Date fechaContratacion;

	public Empleado(int id, String nombre, String login, String password, Date fechaNacimiento,
			Date fechaContratacion) {
		super(id, nombre, login, password, fechaNacimiento);
		this.fechaContratacion = fechaContratacion;
	}

	public Date getFechaContratacion() {
		return fechaContratacion;
	}

	public void setFechaContratacion(Date fechaContratacion) {
		this.fechaContratacion = fechaContratacion;
	}
	
	

}
