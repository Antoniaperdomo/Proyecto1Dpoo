package Deportes;

import static org.junit.jupiter.api.Assertions.*;
import java.util.Date;
import org.junit.jupiter.api.Test;
import Usuario.Socio;

public class JugadorEquipoTest {
	@Test
	public void amarillasGuardadasPorAAmnito() {
		Socio s = new Socio(1, "Prueba", "prueba", "1234", new Date(0), 0, true);
		JugadorEquipo j = new JugadorEquipo(s, "Portero", null, 1);
		assertEquals(0, j.getAmarillas(Suspension.INTERNO));
		assertEquals(0, j.getAmarillas(Suspension.COMPETENCIA));
		j.setAmarillas(Suspension.INTERNO, 1);
		j.setAmarillas(Suspension.COMPETENCIA, 2);
		assertEquals(1, j.getAmarillas(Suspension.INTERNO));
		assertEquals(2, j.getAmarillas(Suspension.COMPETENCIA));
	}

}
