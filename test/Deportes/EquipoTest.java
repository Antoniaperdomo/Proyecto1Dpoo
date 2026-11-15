package Deportes;

import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;
import java.util.Date;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import Usuario.Socio;

public class EquipoTest {
	private Equipo equipo;
	private Futbol futbol;

	private Socio socioAdulto(int id) {
		return new Socio(id, "Jugador" + id, "j" + id, "1234", new Date(0), 0, true);
	}
	
	private Equipo equipoConJugadores(int cuantos) {
		Equipo grande = new Equipo("Grande", futbol, 20, new Categoria(Categoria.MAYORES),
				new ArrayList<JugadorEquipo>(), new ArrayList<Suspension>(), true);
		for (int i = 1; i <= cuantos; i++) {
			grande.agregarJugador(socioAdulto(i), "Jugador", i);
		}
		return grande;
	}

	@BeforeEach
	public void setUp() {
		futbol = new Futbol();
		equipo = new Equipo("Pruebas", futbol, 3, new Categoria(Categoria.MAYORES), new ArrayList<JugadorEquipo>(),
				new ArrayList<Suspension>(), true);
	}

	@Test
	public void cupoCeroExcepcion() {
		assertThrows(IllegalArgumentException.class, () -> new Equipo("X", futbol, 0, new Categoria(Categoria.MAYORES),
				new ArrayList<JugadorEquipo>(), new ArrayList<Suspension>(), true));
	}

	@Test
	public void cupoNegativoException() {
		assertThrows(IllegalArgumentException.class, () -> new Equipo("X", futbol, -5, new Categoria(Categoria.MAYORES),
				new ArrayList<JugadorEquipo>(), new ArrayList<Suspension>(), true));
	}

	@Test
	public void agregarJugador() {
		Socio s = socioAdulto(1);
		JugadorEquipo j = equipo.agregarJugador(s, "Portero", 1);
		assertTrue(equipo.tieneJugador(s));
		assertSame(j, equipo.buscarJugador(s));
		assertEquals(1, equipo.getJugadores().size());
		assertEquals("Portero", j.getPosiccion());
		assertEquals(1, j.getNumeroCamiseta());
	}

	@Test
	public void buscarJugadorQueNoEsta() {
		assertNull(equipo.buscarJugador(socioAdulto(1)));
		assertFalse(equipo.tieneJugador(socioAdulto(1)));
	}

	@Test
	public void jugadorRepetido() {
		equipo.agregarJugador(socioAdulto(1), "Portero", 1);
		equipo.agregarJugador(socioAdulto(2), "Defensa", 2);
		equipo.agregarJugador(socioAdulto(3), "Medio", 2);
		assertThrows(IllegalStateException.class, () -> equipo.agregarJugador(socioAdulto(4), "Delantero", 4));
		assertEquals(3, equipo.getJugadores().size());
	}

	@Test
	public void poscicionNullOVacio() {
		assertThrows(IllegalArgumentException.class, () -> equipo.agregarJugador(socioAdulto(1), null, 1));
		assertThrows(IllegalArgumentException.class, () -> equipo.agregarJugador(socioAdulto(2), "", 2));
	}

	@Test
	public void camisetaRepetidaEsRechazada() {
		equipo.agregarJugador(socioAdulto(1), "Portero", 10);
		assertThrows(IllegalStateException.class, () -> equipo.agregarJugador(socioAdulto(2), "Defensa", 10));
	}

	@Test
	public void suspensionSoloAplicaCOnJugadorYAmbitoCorrectos() {
		Socio s = socioAdulto(1);
		Socio otro = socioAdulto(2);
		equipo.getSuspensiones().add(new Suspension(s, "Tarjeta roja", Suspension.INTERNO));
		assertTrue(equipo.estaSuspendido(s, Suspension.INTERNO));
		assertFalse(equipo.estaSuspendido(s, Suspension.COMPETENCIA));
		assertFalse(equipo.estaSuspendido(otro, Suspension.INTERNO));
	}

	@Test
	public void contarHabilitados() {
		Socio a = socioAdulto(1);
		Socio b = socioAdulto(2);
		equipo.agregarJugador(a, "Portero", 1);
		equipo.agregarJugador(b, "Defensa", 2);
		assertEquals(2, equipo.contarHabilitados(Suspension.INTERNO));
		equipo.getSuspensiones().add(new Suspension(a, "Tarjeta roja", Suspension.INTERNO));
		assertEquals(1, equipo.contarHabilitados(Suspension.INTERNO));
		assertEquals(2, equipo.contarHabilitados(Suspension.COMPETENCIA));
	}

	@Test
	public void cumplirSuspensionNoAfectaAmbito() {
		Socio a = socioAdulto(1);
		equipo.agregarJugador(a, "Portero", 1);
		equipo.getSuspensiones().add(new Suspension(a, "Tarjeta roja", Suspension.INTERNO));
		equipo.cumplirSuspension(Suspension.INTERNO);
		assertFalse(equipo.estaSuspendido(a, Suspension.INTERNO));

	}

	@Test
	public void tarjetaRojaSuspendeYReiniciaAmarillas() {
		Socio a = socioAdulto(1);
		JugadorEquipo j = equipo.agregarJugador(a, "Portero", 1);
		j.setAmarillas(Suspension.INTERNO, 0);
		EstadisticaFutbol est = new EstadisticaFutbol(a);
		est.setTarjetasRojas(1);
		assertTrue(equipo.estaSuspendido(a, Suspension.INTERNO));
		assertEquals(0, j.getAmarillas(Suspension.INTERNO));
	}

	@Test
	public void amarillaAcumulada() {
		Socio a = socioAdulto(1);
		JugadorEquipo j = equipo.agregarJugador(a, "Portero", 1);
		EstadisticaFutbol est = new EstadisticaFutbol(a);
		est.setTarjetasRojas(1);
		equipo.registrarTarjetas(est, Suspension.INTERNO);
		assertFalse(equipo.estaSuspendido(a, Suspension.INTERNO));
		assertEquals(1, j.getAmarillas(Suspension.INTERNO));
	}

	@Test
	public void dosAmariilasSuspenden() {
		Socio a = socioAdulto(1);
		JugadorEquipo j = equipo.agregarJugador(a, "Portero", 1);
		EstadisticaFutbol est = new EstadisticaFutbol(a);
		est.setTarjetasRojas(2);
		equipo.registrarTarjetas(est, Suspension.INTERNO);
		assertTrue(equipo.estaSuspendido(a, Suspension.INTERNO));
		assertEquals(0, j.getAmarillas(Suspension.INTERNO));
	}

	@Test
	public void amarillasDeDosPartidosSuspenden() {
		Socio a = socioAdulto(1);
		equipo.agregarJugador(a, "Portero", 1);
		EstadisticaFutbol partido1 = new EstadisticaFutbol(a);
		partido1.setTarjetasAmarillas(1);
		EstadisticaFutbol partido2 = new EstadisticaFutbol(a);
		partido2.setTarjetasAmarillas(1);
		equipo.registrarTarjetas(partido1, Suspension.INTERNO);
		assertFalse(equipo.estaSuspendido(a, Suspension.INTERNO));
		equipo.registrarTarjetas(partido2, Suspension.INTERNO);
		assertTrue(equipo.estaSuspendido(a, Suspension.INTERNO));

	}

	@Test
	public void amarillasInternasNoCuentanEnCompetencia() {
		Socio a = socioAdulto(1);
		JugadorEquipo j = equipo.agregarJugador(a, "Portero", 1);
		EstadisticaFutbol est = new EstadisticaFutbol(a);
		est.setTarjetasRojas(1);
		equipo.registrarTarjetas(est, Suspension.INTERNO);
		assertEquals(1, j.getAmarillas(Suspension.INTERNO));
		assertEquals(0, j.getAmarillas(Suspension.COMPETENCIA));
	}

	@Test
	public void tarjetasAjenas() {
		EstadisticaFutbol est = new EstadisticaFutbol(socioAdulto(99));
		est.setTarjetasRojas(1);
		equipo.registrarTarjetas(est, Suspension.INTERNO);
		assertEquals(0, equipo.getSuspensiones().size());
	}
	
	@Test
	public void equipoConElminimoDeJugadoresEstaCompleto() {
		assertTrue(equipoConJugadores(7).tieneMinimoDeJugadores(Suspension.INTERNO));
		assertTrue(equipoConJugadores(8).tieneMinimoDeJugadores(Suspension.INTERNO));
	}
	@Test
	public void equipoPorDebajoDelMinimoEstaIncompleto() {
		assertFalse(equipoConJugadores(6).tieneMinimoDeJugadores(Suspension.INTERNO));
		assertFalse(equipoConJugadores(0).tieneMinimoDeJugadores(Suspension.INTERNO));
	}
	@Test
	public void losSuspendidosNoCuentanParaElMinimoSoloEnSuAmbito() {
		Equipo e = equipoConJugadores(7);
		e.getSuspensiones().add(new Suspension(e.getJugadores().get(0).getParticipante(), "Tarjeta roja", Suspension.INTERNO));

		assertFalse(e.tieneMinimoDeJugadores(Suspension.INTERNO));
		assertTrue(e.tieneMinimoDeJugadores(Suspension.COMPETENCIA));
	}

}
