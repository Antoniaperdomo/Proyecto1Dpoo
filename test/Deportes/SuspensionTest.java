package Deportes;

import static org.junit.jupiter.api.Assertions.*;
import java.util.Date;
import org.junit.jupiter.api.Test;
import Usuario.Socio;

public class SuspensionTest {
	private Socio socio() {
		return new Socio(1, "Prueba", "prueba", "1234", new Date(0), 0, true);
	}

	@Test
	public void ambitoInvalidoExcepcion() {
		assertThrows(IllegalArgumentException.class, () -> new Suspension(socio(), "Tarjeta roja", "OTRO"));
	}

	@Test
	public void nuevaSuspensionNoCumplida() {
		Suspension s = new Suspension(socio(), "Tarjeta roja", Suspension.INTERNO);
		assertFalse(s.isCumplida());
	}

	@Test
	public void cumplirMarcaComoCumplida() {
		Suspension s = new Suspension(socio(), "Tarjeta roja", Suspension.COMPETENCIA);
		s.cumplir();
		assertTrue(s.isCumplida());
	}

}
