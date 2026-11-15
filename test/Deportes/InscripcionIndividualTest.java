package Deportes;

import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;
import java.util.Date;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import Usuario.Socio;

public class InscripcionIndividualTest {
	private Socio socio;
	private Tenis tenis;
	private InscripcionIndividual insc;

	@BeforeEach
	public void setUp() {
		socio = new Socio(1, "Prueba", "prueba", "1234", new Date(0), 0, true);
		tenis = new Tenis(DeporteIndividual.AVANZADO);
		insc = new InscripcionIndividual(socio, tenis);
	}

	@Test
	public void iniciaConElo100YNivelPrincipiante() {
		assertEquals(1000, insc.getRankingELO());
		assertEquals(DeporteIndividual.PRINCIPIANTE, tenis.getNivel());
		assertEquals(1, insc.getHistorialElo().size());
		assertEquals(1000, insc.getHistorialElo().get(0).intValue());
	}

	@Test
	public void ganarConIgualSuma16() {
		insc.actualizarRanking(1, 1000);
		assertEquals(1016, insc.getRankingELO());
	}

	@Test
	public void perderConIgualResta16() {
		insc.actualizarRanking(0, 1000);
		assertEquals(984, insc.getRankingELO());
	}

	@Test
	public void empateNoCambbiaElo() {
		insc.actualizarRanking(0, 1000);
		assertEquals(984, insc.getRankingELO());
	}

	@Test
	public void ganarContraSuperiorSumaMasPuntos() {
		insc.actualizarRanking(1, 1400);
		assertEquals(1029, insc.getRankingELO());
	}

	@Test
	public void ganarContraDebilSumaMenos() {
		insc.restaurar(1100, new ArrayList<Integer>());
		insc.actualizarRanking(1, 900);
		assertEquals(1108, insc.getRankingELO());
	}

	@Test
	public void nivelSigueAlElo() {
		insc.restaurar(1199, new ArrayList<Integer>());
		assertEquals(DeporteIndividual.PRINCIPIANTE, tenis.getNivel());
		insc.restaurar(1200, new ArrayList<Integer>());
		assertEquals(DeporteIndividual.INTERMEDIO, tenis.getNivel());
		insc.restaurar(1399, new ArrayList<Integer>());
		assertEquals(DeporteIndividual.INTERMEDIO, tenis.getNivel());
		insc.restaurar(1400, new ArrayList<Integer>());
		assertEquals(DeporteIndividual.AVANZADO, tenis.getNivel());
	}

	@Test
	public void subirEloCambiaNivel() {
		insc.restaurar(1199, new ArrayList<Integer>());
		insc.actualizarRanking(1, 1199);
		assertEquals(1215, insc.getRankingELO());
		assertEquals(DeporteIndividual.INTERMEDIO, tenis.getNivel());
	}

	@Test
	public void copiaHistorial() {
		ArrayList<Integer> historial = new ArrayList<Integer>();
		historial.add(1000);
		historial.add(1016);
		insc.restaurar(1016, historial);

		assertEquals(1016, insc.getRankingELO());
		assertEquals(2, insc.getHistorialElo().size());
		historial.add(1050);
		assertEquals(2, insc.getHistorialElo().size());
	}

	@Test
	public void historialVacio() {
		insc.restaurar(1150, new ArrayList<Integer>());
		assertEquals(1, insc.getHistorialElo().size());
		assertEquals(1150, insc.getHistorialElo().get(0).intValue());
	}

	@Test
	public void actualiazrRankingAgregaAlHistorial() {
		insc.actualizarRanking(1, 1000);
		assertEquals(2, insc.getHistorialElo().size());
		assertEquals(1016, insc.getHistorialElo().get(1).intValue());

	}

}
