package Deportes;

import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;
import java.util.Date;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import Usuario.Socio;

public class PartidoInternoTest {
	private Futbol futbol;
	private Equipo e1;
	private Equipo e2;
	private PartidoInterno partido;

	private Equipo crearEquipo(String nombre, DeporteConjunto deporte, int numJugadores, int idInicial) {
		Equipo equipo = new Equipo(nombre, deporte, 20, new Categoria(Categoria.MAYORES),
				new ArrayList<JugadorEquipo>(), new ArrayList<Suspension>(), true);
		for (int i = 0; i < numJugadores; i++) {
			int id = idInicial + i;
			Socio s = new Socio(id, "Jugador" + id, "j" + id, "1234", new Date(0), 0, true);
			equipo.agregarJugador(s, "Jugador", i + 1);
		}
		return equipo;
	}

	private Socio jugador(Equipo equipo, int indice) {
		return equipo.getJugadores().get(indice).getSocio();
	}

	@BeforeEach
	public void setUp() {
		futbol = new Futbol();
		e1 = crearEquipo("A", futbol, 8, 100);
		e2 = crearEquipo("B", futbol, 8, 200);
		partido = new PartidoInterno(new Date(), futbol, e1, e2);
	}

	@Test
	public void ambitoEsInternoConDosEquipos() {
		assertEquals(Suspension.INTERNO, partido.getAmbito());
		assertEquals(2, partido.getEquipos().size());
		assertSame(e1, partido.getEquipos().get(0));
		assertSame(e2, partido.getEquipos().get(1));
	}

	@Test
	public void registrarMarcador() {
		partido.registrarMarcador(2, 1);
		assertEquals(2, partido.getMarcadorEquipo1());
		assertEquals(1, partido.getMarcadorEquipo2());
	}

	@Test
	public void marcadorNegativoExcepcion() {
		assertThrows(IllegalArgumentException.class, () -> partido.registrarMarcador(-1, 0));
		assertThrows(IllegalArgumentException.class, () -> partido.registrarMarcador(0, -3));
	}

	@Test
	public void partidoNuevoNoEstaValidado() {
		assertFalse(partido.isJugado());
	}

	@Test
	public void validarConEquiposcompletos() {
		partido.validar();
	}

	@Test
	public void validarSinEquiposCompletos() {
		Equipo peque = crearEquipo("C", futbol, 6, 300);
		PartidoInterno p = new PartidoInterno(new Date(), futbol, e1, peque);
		assertThrows(IllegalStateException.class, () -> p.validar());
	}

	@Test
	public void validarConEquipoDeOtroDeporte() {
		Equipo baloncesto = crearEquipo("Bal", new Baloncesto(), 8, 300);
		PartidoInterno p = new PartidoInterno(new Date(), futbol, e1, baloncesto);
		assertThrows(IllegalStateException.class, () -> p.validar());
	}

	@Test
	public void rechazarEstadisticaDeotroDeporte() {
		EstadisticaBaloncesto est = new EstadisticaBaloncesto(jugador(e1, 0));
		assertThrows(IllegalArgumentException.class, () -> partido.agregarEstadisistica(est));
	}

	@Test
	public void estadisticaRepetidoDeJugadorRechaza() {
		partido.agregarEstadisistica(new EstadisticaFutbol(jugador(e1, 0)));
		assertThrows(IllegalStateException.class,
				() -> partido.agregarEstadisistica(new EstadisticaFutbol(jugador(e1, 0))));
		assertEquals(1, partido.getEstadisticas().size());
	}

	@Test
	public void validarFallaConSUspension() {
		Socio suspendido = jugador(e1, 0);
		e1.getSuspensiones().add(new Suspension(suspendido, "Tarjeta roja", Suspension.INTERNO));
		partido.agregarEstadisistica(new EstadisticaFutbol(suspendido));
		assertThrows(IllegalStateException.class, () -> partido.validar());
	}

	@Test
	public void unaSuspensionNoImpideJugarUnPartidoInterno() {
		Socio s = jugador(e1, 0);
		e1.getSuspensiones().add(new Suspension(s, "Tarjeta roja", Suspension.COMPETENCIA));
		partido.agregarEstadisistica(new EstadisticaFutbol(s));
		partido.validar();

	}

	@Test
	public void cerrarMarcaElPartidoComoJUgado() {
		partido.cerrar();
		assertTrue(partido.isJugado());
	}

	@Test
	public void cerrarDosVecesExcepcion() {
		partido.cerrar();
		assertThrows(IllegalStateException.class, () -> partido.cerrar());
	}

	@Test
	public void cerrarCumpleExcepciones() {
		Socio s = jugador(e1, 0);
		e1.getSuspensiones().add(new Suspension(s, "Tarjeta roja", Suspension.INTERNO));

		partido.cerrar();

		assertFalse(e1.estaSuspendido(s, Suspension.INTERNO));
	}

	@Test
	public void tarjetaRojaAlCerrarSuspendeElAmbito() {
		Socio s = jugador(e1, 0);
		EstadisticaFutbol est = new EstadisticaFutbol(s);
		est.setTarjetasRojas(1);
		partido.agregarEstadisistica(est);

		partido.cerrar();

		assertTrue(e1.estaSuspendido(s, Suspension.INTERNO));
		assertFalse(e1.estaSuspendido(s, Suspension.COMPETENCIA));
	}

	@Test
	public void dosAmarillasAlCerrarSuspenden() {
		Socio s = jugador(e2, 3);
		EstadisticaFutbol est = new EstadisticaFutbol(s);
		est.setTarjetasAmarillas(2);
		partido.agregarEstadisistica(est);

		partido.cerrar();

		assertTrue(e2.estaSuspendido(s, Suspension.INTERNO));
	}

	@Test
	public void unaAmarillaNoSuspende() {
		Socio s = jugador(e2, 3);
		EstadisticaFutbol est = new EstadisticaFutbol(s);
		est.setTarjetasAmarillas(1);
		partido.agregarEstadisistica(est);

		partido.cerrar();

		assertFalse(e2.estaSuspendido(s, Suspension.INTERNO));
		assertEquals(1, e2.buscarJugador(s).getAmarillas(Suspension.INTERNO));
	}

	@Test
	public void registrarResultadi() {
		EstadisticaFutbol est = new EstadisticaFutbol(jugador(e1, 0));
		est.setGoles(3);
		ArrayList<EstadisticaJugadorPartido> estadisticas = new ArrayList<EstadisticaJugadorPartido>();
		estadisticas.add(est);

		partido.registrarResultadi(3, 1, estadisticas);

		assertEquals(3, partido.getMarcadorEquipo1());
		assertEquals(1, partido.getMarcadorEquipo2());
		assertEquals(1, partido.getEstadisticas().size());
		assertTrue(partido.isJugado());
	}

	@Test
	public void registrarNegativoNoCierra() {
		Socio s = jugador(e1, 0);
		e1.getSuspensiones().add(new Suspension(s, "Tarjeta roja", Suspension.INTERNO));
		ArrayList<EstadisticaJugadorPartido> estadisticas = new ArrayList<EstadisticaJugadorPartido>();
		estadisticas.add(new EstadisticaFutbol(s));

		assertThrows(IllegalStateException.class, () -> partido.registrarResultadi(1, 0, estadisticas));
		assertFalse(partido.isJugado());
	}

	@Test
	public void puntajeDestacadoDevuelveLosGolesDelJugadorCero() {
		Socio goleador = jugador(e1, 0);
		EstadisticaFutbol est = new EstadisticaFutbol(goleador);
		est.setGoles(2);
		partido.agregarEstadisistica(est);

		assertEquals(2, partido.getPuntajeDestacado(goleador));
		assertEquals(0, partido.getPuntajeDestacado(jugador(e2, 0)));
	}
}
