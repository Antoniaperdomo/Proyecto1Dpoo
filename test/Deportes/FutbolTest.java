package Deportes;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class FutbolTest {
	private Futbol futbol;

	@BeforeEach
	public void setUp() {
		futbol = new Futbol();
	}

	@Test
	public void victoriaDaTresPuntos() {
		assertEquals(3, futbol.calcularPuntos(2, 1));
	}

	@Test
	public void empateDaUnPunto() {
		assertEquals(1, futbol.calcularPuntos(1, 1));
	}

	@Test
	public void derrotaDaCeroPuntos() {
		assertEquals(0, futbol.calcularPuntos(0, 2));
	}

	@Test
	public void reglasDelDeporte() {
		assertEquals(7, futbol.getMinJugadores());
		assertFalse(futbol.esIndividual());

	}

}
