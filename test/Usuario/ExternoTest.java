package Usuario;

import static org.junit.jupiter.api.Assertions.*;
import java.util.Date;
import org.junit.jupiter.api.Test;

public class ExternoTest {

	@Test
	public void externosGuardaDatosYSiEsEstudiante() {
		Externo e = new Externo(7, "Pedro", "pedro", "1234", new Date(0), true);
		assertEquals(7, e.getId());
		assertEquals("Pedro", e.getNombre());
		assertTrue(e.isEstudianteUniversidad());
		assertFalse(new Externo(8, "Ana", "ana", "1234", new Date(0), false).isEstudianteUniversidad());
	}
	@Test
	public void externoEsParticipanteYCalculaSuEdad() {
		Participante p = new Externo(7, "Pedro", "pedro", "1234", new Date(0), false);
		assertTrue(p.getEdad() >= 55);
	}
	@Test
	public void laEdadSeCalculaIgualParaTodosLosUsuarios() {
		Date nacimiento = new Date(0);
		Socio socio = new Socio(1, "S", "s", "1234", nacimiento, 0, true);
		Externo externo = new Externo(2, "E", "e", "1234", nacimiento, false);
		assertEquals(socio.getEdad(), externo.getEdad());
	}

}
