package Deportes;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class BalocestoTest {
	private Baloncesto baloncesto;

	@BeforeEach
	public void setUp() {
		baloncesto = new Baloncesto();
	}

	@Test
	public void victoriaDaDosPuntos() {
		assertEquals(1, baloncesto.calcularPuntos(80, 70));
	}

	@Test
	public void derrotaDUnPunto() {
		assertEquals(1, baloncesto.calcularPuntos(70, 80));
	}

	@Test
	public void empateLanzaExcepcion() {
		assertThrows(IllegalArgumentException.class, () -> baloncesto.calcularPuntos(60, 60));
	}

	@Test
	public void reglasDelDeporte() {
		assertEquals(5, baloncesto.getMinJugadores());
		assertFalse(baloncesto.esIndividual());
	}

}
