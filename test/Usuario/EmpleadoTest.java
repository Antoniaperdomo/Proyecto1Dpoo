package Usuario;

import Administradores.SolicitudCambioTurno;
import Administradores.SugerenciaProducto;
import Administradores.Turno;
import static org.junit.jupiter.api.Assertions.*;

import java.sql.Date;
import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class EmpleadoTest {
	private Entrenador entrenador;
	private Fisioterapeuta fisio;
	private Turno turnoLunes;
	private Turno turnoMartes;

	@BeforeEach
	public void setUp() {
		entrenador = new Entrenador(1, "Coach", "coach", "1234", new Date(0), new Date(1000));
		fisio = new Fisioterapeuta(2, "Fisio", "fisio", "1234", new Date(0), new Date(2000));
		turnoLunes = new Turno(entrenador, "LUNES", 8, 12);
		turnoMartes = new Turno(entrenador, "MARTES", 8, 12);
	}

	@Test
	public void descuentoPropioEsDel15PorCiento() {
		assertEquals(0.15, entrenador.getDescuentoPropio(), 0.0001);
		assertEquals(0.15, fisio.getDescuentoPropio(), 0.0001);
	}

	@Test
	public void empiezaSinPuntosYSePuedenActualizar() {
		assertEquals(0, entrenador.getPuntosFidelidad());
		entrenador.setPuntosFidelidad(250);
		assertEquals(250, entrenador.getPuntosFidelidad());
	}

	@Test
	public void guardaLaFechaDeContratacion() {
		assertEquals(new Date(1000), entrenador.getFechaContratacion());
		assertEquals(new Date(2000), fisio.getFechaContratacion());
	}

	@Test
	public void empleadosSonCompradoresYSoloElEntrenadorEsParticipante() {
		Object e = entrenador;
		Object f = fisio;
		assertTrue(e instanceof Comprador);
		assertTrue(f instanceof Comprador);
		assertTrue(e instanceof Participante);
	}

	@Test
	public void solicitarCambioDeTurnoCreaSolicitudPendienteYLaRegistra() {
		ArrayList<SolicitudCambioTurno> solicitudes = new ArrayList<SolicitudCambioTurno>();
		SolicitudCambioTurno s = entrenador.solicitarCambioTurno(turnoLunes, turnoMartes, solicitudes);
		assertEquals(1, solicitudes.size());
		assertSame(s, solicitudes.get(0));
		assertEquals(SolicitudCambioTurno.PENDIENTE, s.getEstado());
		assertSame(entrenador, s.getEmpleado());
		assertSame(turnoLunes, s.getTurnoActual());
		assertSame(turnoMartes, s.getTurnoPropuesto());
	}

	@Test
	public void unEmpleadoPuedeHacerVariasSolicitudes() {
		ArrayList<SolicitudCambioTurno> solicitudes = new ArrayList<SolicitudCambioTurno>();
		entrenador.solicitarCambioTurno(turnoLunes, turnoMartes, solicitudes);
		entrenador.solicitarCambioTurno(turnoMartes, turnoLunes, solicitudes);
		assertEquals(2, solicitudes.size());
	}

	@Test
	public void noPuedeSolicitarCambioSobreElTurnoActualDeOtroEmpleado() {
		ArrayList<SolicitudCambioTurno> solicitudes = new ArrayList<SolicitudCambioTurno>();
		Turno ajeno = new Turno(fisio, "LUNES", 8, 12);
		assertThrows(IllegalArgumentException.class,
				() -> entrenador.solicitarCambioTurno(ajeno, turnoMartes, solicitudes));
		assertEquals(0, solicitudes.size());
	}

	@Test
	public void noPuedeProponerUnTurnoDeOtroEmpleado() {
		ArrayList<SolicitudCambioTurno> solicitudes = new ArrayList<SolicitudCambioTurno>();
		Turno ajeno = new Turno(fisio, "MARTES", 8, 12);
		assertThrows(IllegalArgumentException.class,
				() -> entrenador.solicitarCambioTurno(turnoLunes, ajeno, solicitudes));
		assertEquals(0, solicitudes.size());
	}

	@Test
	public void sugerirProductoCreaSugerenciaPendienteYLaRegistra() {
		ArrayList<SugerenciaProducto> sugerencias = new ArrayList<SugerenciaProducto>();
		SugerenciaProducto s = fisio.sugerirProducto("Cinta kinesiologica", "Salud", sugerencias);
		assertEquals(1, sugerencias.size());
		assertSame(s, sugerencias.get(0));
		assertEquals(SugerenciaProducto.PENDIENTE, s.getEstado());
		assertEquals("Cinta kinesiologica", s.getNombre());
		assertEquals("Salud", s.getCategoria());
		assertSame(fisio, s.getEmpleado());
	}

}
