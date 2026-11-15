package Deportes;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class DerporteIndividualTest {

	@Test
	public void esIndividual() {
		assertTrue(new Tenis(DeporteIndividual.INTERMEDIO).esIndividual());
	}

	@Test
	public void setNivelValidoCambiaElNivel() {
		Tenis tenis = new Tenis(DeporteIndividual.PRINCIPIANTE);
		tenis.setNivel(DeporteIndividual.AVANZADO);
		assertEquals(DeporteIndividual.AVANZADO, tenis.getNivel());
	}

	@Test
	public void setNivelInvalidoLanzaExcepcion() {
		Tenis tenis = new Tenis(DeporteIndividual.PRINCIPIANTE);
		assertThrows(IllegalArgumentException.class, () -> tenis.setNivel("EXPERTO"));
	}

	@Test
	public void constructorConNivelInvalido() {
		assertThrows(IllegalArgumentException.class, () -> new Tenis("XYZ"));
	}

	@Test
	public void principianteNojuegaConAvanzado() {
		Tenis a = new Tenis(DeporteIndividual.PRINCIPIANTE);
		Tenis b = new Tenis(DeporteIndividual.AVANZADO);
		assertFalse(a.puedeJugarCon(b));
		assertFalse(b.puedeJugarCon(b));
	}

	@Test
	public void principianteSijuegaConIntermedio() {
		Tenis a = new Tenis(DeporteIndividual.PRINCIPIANTE);
		Tenis b = new Tenis(DeporteIndividual.INTERMEDIO);
		assertTrue(a.puedeJugarCon(b));
	}

	@Test
	public void avanzadoSijuegaConIntermedio() {
		Tenis a = new Tenis(DeporteIndividual.AVANZADO);
		Tenis b = new Tenis(DeporteIndividual.INTERMEDIO);
		assertTrue(a.puedeJugarCon(b));
	}

	@Test
	public void mismoNivelSiJuegan() {
		Tenis a = new Tenis(DeporteIndividual.AVANZADO);
		Tenis b = new Tenis(DeporteIndividual.AVANZADO);
		assertTrue(a.puedeJugarCon(b));
	}

}
