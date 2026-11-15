package Deportes;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class VoleibolTest {
	private Voleibol voleibol;

	@BeforeEach
	public void setUp() {
		voleibol = new Voleibol();
	}

	@Test
	public void resultadosXANDaNPuntos() {
		assertAll("Diferentes resultados", () -> assertEquals(3, voleibol.calcularPuntos(3, 0)),
				() -> assertEquals(3, voleibol.calcularPuntos(3, 1)),
				() -> assertEquals(2, voleibol.calcularPuntos(3, 2)),
				() -> assertEquals(1, voleibol.calcularPuntos(2, 3)),
				() -> assertEquals(0, voleibol.calcularPuntos(1, 3)),
				() -> assertEquals(0, voleibol.calcularPuntos(0, 3))

		);

	}

	@Test
	public void reglasDelDeporte() {
		assertEquals(6, voleibol.getMinJugadores());
		assertFalse(voleibol.esIndividual());
	}

}
