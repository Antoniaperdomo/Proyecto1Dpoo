package Usuario;
import java.util.Date;

public abstract class Usuario {
	protected int id;
	protected String nombre;
	protected String login;
	protected String password;
	protected Date fechaNacimiento;
	public Usuario(int id, String nombre, String login, String password, Date fechaNacimiento) {
		this.id = id;
		this.nombre = nombre;
		this.login = login;
		this.password = password;
		this.fechaNacimiento = fechaNacimiento;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getLogin() {
		return login;
	}
	public void setLogin(String login) {
		this.login = login;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public Date getFechaNacimiento() {
		return fechaNacimiento;
	}
	public void setFechaNacimiento(Date fechaNacimiento) {
		this.fechaNacimiento = fechaNacimiento;
	}

}
