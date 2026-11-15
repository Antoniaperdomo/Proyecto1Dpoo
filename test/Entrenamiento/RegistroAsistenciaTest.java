package Entrenamiento;

import static org.junit.jupiter.api.Assertions.*;
import java.util.Date;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import Usuario.Socio;

public class RegistroAsistenciaTest {
	private Socio socio;
	private RegistroAsistencia registro;

	@BeforeEach
	public void setUp() {
		socio = new Socio(1, "Prueba", "prueba", "1234", new Date(0), 0, true);
		registro = new RegistroAsistencia(null, socio, RegistroAsistencia.ASISTIO);
	}

	@Test
	public void estadoInvalidoExcepcion() {
		assertThrows(IllegalArgumentException.class, () -> new RegistroAsistencia(null, socio, "OTRO"));
		assertThrows(IllegalArgumentException.class, () -> new RegistroAsistencia(null, socio, null));
	}

	@Test
	public void asistioYLlegoTardeCuentanComoAsistencia() {
		assertTrue(registro.asistio());
		registro.setEstado(RegistroAsistencia.LLEGO_TARDE);
		assertTrue(registro.asistio());
	}

	@Test
	public void faltoNoCuentaComoAsistencia() {
		registro.setEstado(RegistroAsistencia.FALTO);
		assertFalse(registro.asistio());
	}

	@Test
	public void setEstadoInvalidoNoCambiaElEstado() {
		assertThrows(IllegalArgumentException.class, () -> registro.setEstado("tal vez"));
		assertEquals(RegistroAsistencia.ASISTIO, registro.getEstado());
	}

	@Test
	public void nuevoRegistroNoTieneEvaluacionNiComentario() {
		assertFalse(registro.tieneEvaluacion());
		assertNull(registro.getEvaluacion());
		assertNull(registro.getComentario());
	}

	@Test
	public void evaluacionEnLosLimitesEsValida() {
		registro.setEvaluacion(1.0);
		assertEquals(1.0, registro.getEvaluacion(), 0.0001);
		registro.setEvaluacion(5.0);
		assertEquals(5.0, registro.getEvaluacion(), 0.0001);
		assertTrue(registro.tieneEvaluacion());
	}

	@Test
	public void evaluacionFueraDeRangoExcepcion() {
		assertThrows(IllegalArgumentException.class, () -> registro.setEvaluacion(0.9));
		assertThrows(IllegalArgumentException.class, () -> registro.setEvaluacion(5.1));
		assertFalse(registro.tieneEvaluacion());
	}

	@Test
	public void evaluacionNullQuitaLaEvaluacion() {
		registro.setEvaluacion(4.0);
		registro.setEvaluacion(null);
		assertFalse(registro.tieneEvaluacion());
	}

	@Test
	public void gettersDevuelvenLoGuardado() {
		SesionEntrenamiento sesion = new SesionEntrenamiento(new Date(), 8, 1.5, null, "Cancha 1");
		RegistroAsistencia r = new RegistroAsistencia(sesion, socio, RegistroAsistencia.FALTO);
		assertSame(sesion, r.getSesion());
		assertSame(socio, r.getSocio());
		assertEquals(RegistroAsistencia.FALTO, r.getEstado());
	}

	@Test
	public void toStringSoloConEstado() {
		assertEquals("Prueba - asistio", registro.toString());
	}

	@Test
	public void toStringConEvaluacionYComentario() {
		registro.setEvaluacion(4.5);
		registro.setComentario("Muy bien");
		assertEquals("Prueba - asistio - nota: 4.5 - Muy bien", registro.toString());
	}

}
