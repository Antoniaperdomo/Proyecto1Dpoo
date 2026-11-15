package Administradores;

import Usuario.Entrenador;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;

public class SolicitudCambioTurnoTest {
	private Entrenador empleado;
	private Turno actual;
	private Turno propuesto;
	private SolicitudCambioTurno solicitud;

	@BeforeEach
	public void setUp() {
		empleado = new Entrenador(1, "Coach", "coach", "1234", new Date(0), new Date(0));
		actual = new Turno(empleado, "LUNES", 8, 12);
		propuesto = new Turno(empleado, "MARTES", 8, 12);
		solicitud = new SolicitudCambioTurno(empleado, actual, propuesto);
	}

	@Test
	public void nuevaSolicitudEstaPendienteYGuardaSusDatos() {
		assertEquals(SolicitudCambioTurno.PENDIENTE, solicitud.getEstado());
		assertSame(empleado, solicitud.getEmpleado());
		assertSame(actual, solicitud.getTurnoActual());
		assertSame(propuesto, solicitud.getTurnoPropuesto());
	}

	@Test
	public void sePuedeAprobar() {
		solicitud.setEstado(SolicitudCambioTurno.APROBADA);
		assertEquals(SolicitudCambioTurno.APROBADA, solicitud.getEstado());
	}

	@Test
	public void sePuedeRechazar() {
		solicitud.setEstado(SolicitudCambioTurno.RECHAZADA);
		assertEquals(SolicitudCambioTurno.RECHAZADA, solicitud.getEstado());
	}

	@Test
	public void estadoInvalidoLanzaExcepcionYNoCambiaElEstado() {
		assertThrows(IllegalArgumentException.class, () -> solicitud.setEstado("cancelada"));
		assertEquals(SolicitudCambioTurno.PENDIENTE, solicitud.getEstado());
	}
}
