package Usuario;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Date;

public class AutenticationTest {
	private Autentication autenticacion;
	private ArrayList<Usuario> usuarios;
	private Socio ana;
	private Socio inactivo;
	private Administrador admin;
	private Entrenador entrenador;

	@BeforeEach
	public void setUp() {
		autenticacion = new Autentication();
		ana = new Socio(1, "Ana", "ana", "clave1", new Date(0), 0, true);
		inactivo = new Socio(2, "Baja", "baja", "clave2", new Date(0), 0, false);
		admin = new Administrador(3, "Admin", "admin", "clave3", new Date(0));
		entrenador = new Entrenador(4, "Coach", "coach", "clave4", new Date(0), new Date(0));
		usuarios = new ArrayList<Usuario>();
		usuarios.add(ana);
		usuarios.add(inactivo);
		usuarios.add(admin);
		usuarios.add(entrenador);
	}

	@Test
	public void credencialesCorrectasDevuelvenAlSocio() {
		assertSame(ana, autenticacion.iniciarSesion("ana", "clave1", usuarios));
	}

	@Test
	public void credencialesCorrectasDevuelvenAlAdministradorYAlEmpleado() {
		assertSame(admin, autenticacion.iniciarSesion("admin", "clave3", usuarios));
		assertSame(entrenador, autenticacion.iniciarSesion("coach", "clave4", usuarios));
	}

	@Test
	public void passwordIncorrectaLanzaExcepcion() {
		assertThrows(IllegalArgumentException.class, () -> autenticacion.iniciarSesion("ana", "otra", usuarios));
	}

	@Test
	public void loginInexistenteLanzaExcepcion() {
		assertThrows(IllegalArgumentException.class, () -> autenticacion.iniciarSesion("nadie", "clave1", usuarios));
	}

	@Test
	public void passwordDeOtroUsuarioNoSirve() {
		assertThrows(IllegalArgumentException.class, () -> autenticacion.iniciarSesion("ana", "clave3", usuarios));
	}

	@Test
	public void loginDistingueMayusculas() {
		assertThrows(IllegalArgumentException.class, () -> autenticacion.iniciarSesion("ANA", "clave1", usuarios));
	}

	@Test
	public void listaVaciaLanzaExcepcion() {
		assertThrows(IllegalArgumentException.class,
				() -> autenticacion.iniciarSesion("ana", "clave1", new ArrayList<Usuario>()));
	}

	@Test
	public void socioDadoDeBajaNoPuedeIniciarSesion() {
		assertThrows(IllegalStateException.class, () -> autenticacion.iniciarSesion("baja", "clave2", usuarios));
	}

	@Test
	public void socioReactivadoVuelveAPoderEntrar() {
		inactivo.setActivo(true);
		assertSame(inactivo, autenticacion.iniciarSesion("baja", "clave2", usuarios));
	}

	@Test
	public void cambiarLaPasswordInvalidaLaAnterior() {
		ana.setPassword("nueva");
		assertThrows(IllegalArgumentException.class, () -> autenticacion.iniciarSesion("ana", "clave1", usuarios));
		assertSame(ana, autenticacion.iniciarSesion("ana", "nueva", usuarios));
	}

}
