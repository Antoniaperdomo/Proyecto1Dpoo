package Entrenamiento;

import static org.junit.jupiter.api.Assertions.*;
import java.util.Date;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import Usuario.Entrenador;
import Usuario.Socio;

public class SesionEntrenamientoTest {
	private SesionEntrenamiento sesion;
	private Entrenador entrenador;
	private Socio a;
	private Socio b;

	private Socio socio(int id, boolean activo) {
		return new Socio(id, "Jugador" + id, "j" + id, "1234", new Date(0), 0, activo);
	}

	@BeforeEach
	public void setUp() {
		entrenador = new Entrenador(10, "Coach", "coach", "1234", new Date(0), new Date(0));
		sesion = new SesionEntrenamiento(new Date(0), 8, 1.5, entrenador, "Cancha 1");
		a = socio(1, true);
		b = socio(2, true);
	}

	@Test
	public void sesionNuevaEstaVaciaYNoFinalizada() {
		assertFalse(sesion.isFinalizada());
		assertTrue(sesion.getListaDeJugadoresConvocados().isEmpty());
		assertTrue(sesion.getRegistrosAsistencia().isEmpty());
		assertSame(entrenador, sesion.getEntrenadorResponsable());
		assertEquals(8, sesion.getHora());
		assertEquals(1.5, sesion.getDuracion(), 0.0001);
		assertEquals("Cancha 1", sesion.getInstalacionUtilizada());
	}

	@Test
	public void convocarJugadorActivo() {
		assertTrue(sesion.convocarJugador(a));
		assertTrue(sesion.estaConvocado(a));
		assertFalse(sesion.estaConvocado(b));
	}

	@Test
	public void convocarRepetidoNullOInactivoEsRechazado() {
		assertTrue(sesion.convocarJugador(a));
		assertFalse(sesion.convocarJugador(a));
		assertFalse(sesion.convocarJugador(null));
		assertFalse(sesion.convocarJugador(socio(3, false)));
		assertEquals(1, sesion.getListaDeJugadoresConvocados().size());
	}

	@Test
	public void quitarJugador() {
		sesion.convocarJugador(a);
		assertTrue(sesion.quitarJugador(a));
		assertFalse(sesion.estaConvocado(a));
		assertFalse(sesion.quitarJugador(a));
	}

	@Test
	public void sesionFinalizadaNoPermiteCambiarConvocados() {
		sesion.convocarJugador(a);
		sesion.finalizarSesion();
		assertTrue(sesion.isFinalizada());
		assertFalse(sesion.convocarJugador(b));
		assertFalse(sesion.quitarJugador(a));
		assertTrue(sesion.estaConvocado(a));
	}

	@Test
	public void individualOGrupalSegunConvocados() {
		assertFalse(sesion.esIndividual());
		assertFalse(sesion.esGrupal());
		sesion.convocarJugador(a);
		assertTrue(sesion.esIndividual());
		assertFalse(sesion.esGrupal());
		sesion.convocarJugador(b);
		assertFalse(sesion.esIndividual());
		assertTrue(sesion.esGrupal());
	}

	@Test
	public void registrarAsistenciaAntesDeFinalizarExcepcion() {
		sesion.convocarJugador(a);
		assertThrows(IllegalStateException.class,
				() -> sesion.registrarAsistencia(a, RegistroAsistencia.ASISTIO, 4.0, null));
	}

	@Test
	public void registrarAsistenciaDeNoConvocadoExcepcion() {
		sesion.finalizarSesion();
		assertThrows(IllegalArgumentException.class,
				() -> sesion.registrarAsistencia(a, RegistroAsistencia.ASISTIO, 4.0, null));
	}

	@Test
	public void registrarAsistenciaDosVecesExcepcion() {
		sesion.convocarJugador(a);
		sesion.finalizarSesion();
		sesion.registrarAsistencia(a, RegistroAsistencia.ASISTIO, 4.0, null);
		assertThrows(IllegalArgumentException.class,
				() -> sesion.registrarAsistencia(a, RegistroAsistencia.FALTO, null, null));
		assertEquals(1, sesion.getRegistrosAsistencia().size());
	}

	@Test
	public void registrarAsistenciaGuardaTodosLosDatos() {
		sesion.convocarJugador(a);
		sesion.finalizarSesion();
		RegistroAsistencia r = sesion.registrarAsistencia(a, RegistroAsistencia.LLEGO_TARDE, 3.5, "Llego 10 min tarde");
		assertSame(r, sesion.obtenerRegistro(a));
		assertSame(sesion, r.getSesion());
		assertEquals(RegistroAsistencia.LLEGO_TARDE, r.getEstado());
		assertEquals(3.5, r.getEvaluacion(), 0.0001);
		assertEquals("Llego 10 min tarde", r.getComentario());
	}

	@Test
	public void estadoOEvaluacionInvalidosNoDejanRegistro() {
		sesion.convocarJugador(a);
		sesion.finalizarSesion();
		assertThrows(IllegalArgumentException.class, () -> sesion.registrarAsistencia(a, "OTRO", 3.0, null));
		assertThrows(IllegalArgumentException.class,
				() -> sesion.registrarAsistencia(a, RegistroAsistencia.ASISTIO, 9.0, null));
		assertNull(sesion.obtenerRegistro(a));
		assertTrue(sesion.getRegistrosAsistencia().isEmpty());
	}

	@Test
	public void obtenerRegistroDeQuienNoTieneNull() {
		assertNull(sesion.obtenerRegistro(a));
	}

	@Test
	public void asistenciaCompletaSoloCuandoTodosTienenRegistro() {
		sesion.convocarJugador(a);
		sesion.convocarJugador(b);
		sesion.finalizarSesion();
		assertFalse(sesion.asistenciaCompleta());
		sesion.registrarAsistencia(a, RegistroAsistencia.ASISTIO, null, null);
		assertFalse(sesion.asistenciaCompleta());
		sesion.registrarAsistencia(b, RegistroAsistencia.FALTO, null, null);
		assertTrue(sesion.asistenciaCompleta());
	}

	@Test
	public void contarAsistentesIncluyeLlegadasTarde() {
		Socio c = socio(3, true);
		sesion.convocarJugador(a);
		sesion.convocarJugador(b);
		sesion.convocarJugador(c);
		sesion.finalizarSesion();
		sesion.registrarAsistencia(a, RegistroAsistencia.ASISTIO, null, null);
		sesion.registrarAsistencia(b, RegistroAsistencia.LLEGO_TARDE, null, null);
		sesion.registrarAsistencia(c, RegistroAsistencia.FALTO, null, null);
		assertEquals(2, sesion.contarAsistentes());
	}

	@Test
	public void promedioSoloUsaLosRegistrosConEvaluacion() {
		Socio c = socio(3, true);
		sesion.convocarJugador(a);
		sesion.convocarJugador(b);
		sesion.convocarJugador(c);
		sesion.finalizarSesion();
		sesion.registrarAsistencia(a, RegistroAsistencia.ASISTIO, 4.0, null);
		sesion.registrarAsistencia(b, RegistroAsistencia.ASISTIO, 5.0, null);
		sesion.registrarAsistencia(c, RegistroAsistencia.ASISTIO, null, null);
		assertEquals(4.5, sesion.calcularPromedioEvaluaciones(), 0.0001);
	}

	@Test
	public void promedioSinEvaluacionesEsCero() {
		assertEquals(0, sesion.calcularPromedioEvaluaciones(), 0.0001);
	}

	@Test
	public void settersActualizanLosDatos() {
		Date nueva = new Date(5000);
		sesion.setFecha(nueva);
		sesion.setHora(15);
		sesion.setDuracion(2.0);
		sesion.setInstalacionUtilizada("Cancha 2");
		assertSame(nueva, sesion.getFecha());
		assertEquals(15, sesion.getHora());
		assertEquals(2.0, sesion.getDuracion(), 0.0001);
		assertEquals("Cancha 2", sesion.getInstalacionUtilizada());
	}

}
