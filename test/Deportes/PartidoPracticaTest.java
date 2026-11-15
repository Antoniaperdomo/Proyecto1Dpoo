package Deportes;

import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;
import java.util.Date;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import Usuario.Socio;

public class PartidoPracticaTest {
	private Socio a;
	private Socio b;
	private PartidoPractica partido;

	private Socio socioInscrito(int id, String disciplina) {
		Socio s = new Socio(id, "Jugador" + id, "j" + id, "1234", new Date(0), 0, true);
		s.inscribirse(disciplina);
		return s;
	}

	private ArrayList<Socio> lista(Socio... socios) {
		ArrayList<Socio> l = new ArrayList<Socio>();
		for (Socio s : socios) {
			l.add(s);
		}
		return l;
	}

	private EstadisticasTenisYPadel sets(int ganados) {
		EstadisticasTenisYPadel est = new EstadisticasTenisYPadel();
		est.setSetsGanados(ganados);
		return est;
	}

	@BeforeEach
	public void preparar() {
		a = socioInscrito(1, "TENIS");
		b = socioInscrito(2, "TENIS");
		partido = new PartidoPractica(new Date(), "TENIS", new CanchaTenis("T1"), lista(a, b), false);
	}

	@Test
	public void instalacionDeOtraDiscplina() {
		assertThrows(IllegalArgumentException.class,
				() -> new PartidoPractica(new Date(), "TENIS", new CanchaPadel("P1"), lista(a, b), false));
	}

	@Test
	public void numeroDeJugadoresFueraDeCapacidad() {
		assertThrows(IllegalArgumentException.class,
				() -> new PartidoPractica(new Date(), "TENIS", new CanchaTenis("T1"), lista(a), false));
	}

	@Test
	public void jugadorNoInscritoExcepction() {
		Socio sinInscripcion = new Socio(3, "Nuevo", "nuevo", "1234", new Date(0), 0, true);
		assertThrows(IllegalStateException.class,
				() -> new PartidoPractica(new Date(), "TENIS", new CanchaTenis("T1"), lista(a, sinInscripcion), false));
	}

	@Test
	public void partidoNuevoNoEstaFInalizado() {
		assertFalse(partido.isFinalizado());
	}

	@Test
	public void validarNivelesCompatible() {
		partido.validarNiveles();
	}

	@Test
	public void validarNuvelesFallaConPrincipianteYavanzado() {
		b.buscarInscripcion("TENIS").restaurar(1400, new ArrayList<Integer>());
		assertThrows(IllegalStateException.class, () -> partido.validarNiveles());
	}

	@Test
	public void unEntrenadorPuedeProgramarPartidosConNivelesDistintos() {
		b.buscarInscripcion("TENIS").restaurar(1400, new ArrayList<Integer>());
		PartidoPractica deEntrenador = new PartidoPractica(new Date(), "TENIS", new CanchaTenis("T1"), lista(a, b),
				true);
		deEntrenador.validarNiveles();
	}

	@Test
	public void socioProgramaPartidoSinEntrenador() {
		PartidoPractica p = a.programarPartido(new Date(), "TENIS", new CanchaTenis("T2"), lista(a, b));
		assertFalse(p.isProgramadoPorEntrenador());
	}

	@Test
	public void registrarEnJugadoresAgregaACadaSocio() {
		partido.registrarEnJugadores();
		assertEquals(1, a.getPartidosPractica().size());
		assertEquals(1, b.getPartidosPractica().size());
	}

	@Test
	public void registrarEstadisticasDeUnSocioQueNoJugoEsRechazado() {
		Socio otro = socioInscrito(9, "TENIS");
		assertThrows(IllegalArgumentException.class, () -> partido.registrarEstadisticas(otro, sets(1)));
	}

	@Test
	public void registrarEstadisticasDeOtraDisciplinaEsRechazado() {
		assertThrows(IllegalArgumentException.class,
				() -> partido.registrarEstadisticas(a, new EstadisticaTenisDeMesa()));
	}

	@Test
	public void registrarEstadisticasValidasLasGuarda() {
		EstadisticasTenisYPadel est = sets(2);
		partido.registrarEstadisticas(a, est);
		assertSame(est, partido.getEstadisticasPorJugador().get(a));
	}

	@Test
	public void finalizarSinEstadisticasLanzaExcepcion() {
		assertThrows(IllegalStateException.class, () -> partido.finalizar());
		assertFalse(partido.isFinalizado());
	}

	@Test
	public void inalizarConFaltaDeEstadisticasDeUnJugadorExcepcion() {
		partido.registrarEstadisticas(a, sets(2));
		assertThrows(IllegalStateException.class, () -> partido.finalizar());
	}

	@Test
	public void finalizarConMasDeDosJugadoresLanzaExcepcion() {
		Socio p1 = socioInscrito(11, "PADEL");
		Socio p2 = socioInscrito(12, "PADEL");
		Socio p3 = socioInscrito(13, "PADEL");
		Socio p4 = socioInscrito(14, "PADEL");
		PartidoPractica dobles = new PartidoPractica(new Date(), "PADEL", new CanchaPadel("P1"), lista(p1, p2, p3, p4),
				false);
		assertThrows(IllegalStateException.class, () -> dobles.finalizar());
	}

	@Test
	public void ganadorSubeElEloYPerdedorBaja() {
		partido.registrarEstadisticas(a, sets(2));
		partido.registrarEstadisticas(b, sets(0));

		String resumen = partido.finalizar();

		assertTrue(partido.isFinalizado());
		assertEquals(1016, a.buscarInscripcion("TENIS").getRankingELO());
		assertEquals(984, b.buscarInscripcion("TENIS").getRankingELO());
		assertTrue(resumen.contains("1000 -> 1016"));
		assertTrue(resumen.contains("1000 -> 984"));
	}

	@Test
	public void empateNoCambiaElElo() {
		partido.registrarEstadisticas(a, sets(1));
		partido.registrarEstadisticas(b, sets(1));

		partido.finalizar();

		assertEquals(1000, a.buscarInscripcion("TENIS").getRankingELO());
		assertEquals(1000, b.buscarInscripcion("TENIS").getRankingELO());
	}

	@Test
	public void finalizarDosVecesLanzaExcepcion() {
		partido.registrarEstadisticas(a, sets(2));
		partido.registrarEstadisticas(b, sets(0));
		partido.finalizar();
		assertThrows(IllegalStateException.class, () -> partido.finalizar());
		assertEquals(1016, a.buscarInscripcion("TENIS").getRankingELO());
	}

}
