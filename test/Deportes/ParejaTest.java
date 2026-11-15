package Deportes;

import static org.junit.jupiter.api.Assertions.*;
import java.util.Date;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import Usuario.Entrenador;
import Usuario.Fisioterapeuta;
import Usuario.Socio;

public class ParejaTest {
	private Socio ana;
	private Socio luis;
	private Entrenador carlos;
	private Fisioterapeuta maria;

	@BeforeEach
	public void setUp() {
		ana = new Socio(1, "Ana", "ana", "1234", new Date(0), 0, true);
		luis = new Socio(2, "Luis", "luis", "1234", new Date(0), 0, true);
		carlos = new Entrenador(3, "Carlos", "carlos", "1234", new Date(0), new Date(0));
		maria = new Fisioterapeuta(4, "Maria", "maria", "1234", new Date(0), new Date(0));
	}

	@Test
	public void parejaDeDosSocios() {
		Pareja p = new Pareja(ana, luis);
		assertSame(ana, p.getJugador1());
		assertSame(luis, p.getJugador2());
	}

	@Test
	public void parejaPuedeMezclarSocioYEmpleados() {
		Pareja p = new Pareja(ana, carlos);
		assertTrue(p.incluye(ana));
		assertTrue(p.incluye(carlos));
		Pareja empleados = new Pareja(carlos, maria);
		assertTrue(empleados.incluye(maria));
	}

	@Test
	public void unJugadorNoPuedeFormarParejaConSigoMismo() {
		assertThrows(IllegalArgumentException.class, () -> new Pareja(ana, ana));
	}

	@Test
	public void parejaConJugadorNuloEsRechazada() {
		assertThrows(IllegalArgumentException.class, () -> new Pareja(null, ana));
		assertThrows(IllegalArgumentException.class, () -> new Pareja(ana, null));
	}

	@Test
	public void incluyeSoloALosDosJugadores() {
		Pareja p = new Pareja(ana, luis);
		assertTrue(p.incluye(ana));
		assertTrue(p.incluye(luis));
		assertFalse(p.incluye(carlos));
	}

	@Test
	public void nombreDePArejaJuntaNombres() {
		assertEquals("Ana / Luis", new Pareja(ana, luis).getNombre());
	}
}
