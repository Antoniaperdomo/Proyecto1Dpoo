package Administradores;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import Usuario.Entrenador;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;

public class TurnoTest {
	private Entrenador empleado;

	@BeforeEach
	public void setUp() {
		empleado = new Entrenador(1, "Coach", "coach", "1234", new Date(0), new Date(0));
	}

	@Test
	public void turnoValidoGuardaSusDatos() {
		Turno t = new Turno(empleado, "LUNES", 8, 12);
		assertSame(empleado, t.getEmpleado());
		assertEquals("LUNES", t.getDiaSemana());
		assertEquals(8, t.getHoraInicio());
		assertEquals(12, t.getHoraFin());
	}

	@Test
	public void horaInicioIgualALaFinLanzaExcepcion() {
		assertThrows(IllegalArgumentException.class, () -> new Turno(empleado, "LUNES", 8, 8));
	}

	@Test
	public void horaInicioMayorALaFinLanzaExcepcion() {
		assertThrows(IllegalArgumentException.class, () -> new Turno(empleado, "LUNES", 12, 8));
	}

	@Test
	public void turnosQueSeTraslapanSeCruzan() {
		Turno a = new Turno(empleado, "LUNES", 8, 12);
		Turno b = new Turno(empleado, "LUNES", 10, 14);
		assertTrue(a.seCruzaCon(b));
		assertTrue(b.seCruzaCon(a));
	}

	@Test
	public void turnoContenidoEnOtroSeCruza() {
		Turno grande = new Turno(empleado, "LUNES", 8, 18);
		Turno pequeno = new Turno(empleado, "LUNES", 10, 12);
		assertTrue(grande.seCruzaCon(pequeno));
		assertTrue(pequeno.seCruzaCon(grande));
	}

	@Test
	public void turnosContiguosNoSeCruzan() {
		Turno a = new Turno(empleado, "LUNES", 8, 12);
		Turno b = new Turno(empleado, "LUNES", 12, 16);
		assertFalse(a.seCruzaCon(b));
		assertFalse(b.seCruzaCon(a));
	}

	@Test
	public void turnosDeDiasDistintosNoSeCruzan() {
		Turno a = new Turno(empleado, "LUNES", 8, 12);
		Turno b = new Turno(empleado, "MARTES", 8, 12);
		assertFalse(a.seCruzaCon(b));
	}

	@Test
	public void turnosSeparadosEnElMismoDiaNoSeCruzan() {
		Turno a = new Turno(empleado, "LUNES", 6, 8);
		Turno b = new Turno(empleado, "LUNES", 14, 18);
		assertFalse(a.seCruzaCon(b));
	}

	@Test
	public void unTurnoSeCruzaConsigoMismo() {
		Turno a = new Turno(empleado, "LUNES", 8, 12);
		assertTrue(a.seCruzaCon(a));
	}

}
