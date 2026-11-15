package Deportes;

import static org.junit.jupiter.api.Assertions.*;
import java.util.Date;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import Usuario.Socio;

public class EstadisticaTest {
	private Socio socio;

	@BeforeEach
	public void setUp() {
		socio = new Socio(1, "Prueba", "prueba", "1234", new Date(0), 0, true);
	}

	@Test
	public void puntajeDestacadoFutbol() {
		EstadisticaFutbol est = new EstadisticaFutbol(socio);
		est.setGoles(2);
		est.setAsistencias(5);
		assertEquals(2, est.getPuntajeDestacado());
	}

	@Test
	public void puntajeDestacadoBalocesto() {
		EstadisticaBaloncesto est = new EstadisticaBaloncesto(socio);
		est.setPuntos(18);
		est.setRebotes(7);
		assertEquals(18, est.getPuntajeDestacado());
	}

	@Test
	public void puntajeDestacadoVoleibol() {
		EstadisticaVoleibol est = new EstadisticaVoleibol(socio);
		est.setAces(3);
		est.setBloqueos(2);
		est.setSets(3);
		assertEquals(5, est.getPuntajeDestacado());
	}

	@Test
	public void cadaDeporteCreaSuEstadistica() {
		assertEquals(EstadisticaFutbol.class, new Futbol().crearEstadistica(socio).getClass());
		assertEquals(EstadisticaBaloncesto.class, new Baloncesto().crearEstadistica(socio).getClass());
		assertEquals(EstadisticaVoleibol.class, new Voleibol().crearEstadistica(socio).getClass());
		assertSame(socio, new Futbol().crearEstadistica(socio).getJugador());
	}

	@Test
	public void cadaIndividualCreaSuEstadistica() {
		assertEquals(EstadisticasTenisYPadel.class,
				new Tenis(DeporteIndividual.PRINCIPIANTE).crearEstadistica().getClass());
		assertEquals(EstadisticasTenisYPadel.class,
				new Padel(DeporteIndividual.PRINCIPIANTE).crearEstadistica().getClass());
		assertEquals(EstadisticaTenisDeMesa.class,
				new TenisMesa(DeporteIndividual.PRINCIPIANTE).crearEstadistica().getClass());
	}

	@Test
	public void resumenFutbol() {
		EstadisticaFutbol est = new EstadisticaFutbol(socio);
		est.setGoles(2);
		est.setAsistencias(5);
		assertEquals("2 goles, 1 asistencias, 0 tarjetas amarillas, 0 tarjetas rojas.", est.resumen());
	}

	@Test
	public void resumenTenisDeMesa() {
		EstadisticaTenisDeMesa est = new EstadisticaTenisDeMesa();
		est.setSetsGanados(3);
		est.setPuntosTotales(41);
		assertEquals("3 sets, 41 puntos", est.resumen());
	}

}
