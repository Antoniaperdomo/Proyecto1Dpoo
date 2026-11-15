package Usuario;

import Deportes.CanchaPadel;
import Deportes.CanchaTenis;
import Deportes.PartidoPractica;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class EntrenadorTest {
	private Entrenador entrenador;
	private CanchaTenis cancha;
	private Socio principiante;
	private Socio avanzado;
	
	@BeforeEach
	public void setUp() {
		entrenador = new Entrenador(1, "Coach", "coach", "1234", new Date(0), new Date(0));
		cancha = new CanchaTenis("Tenis 1");
		principiante = new Socio(2, "Pri", "pri", "1234", new Date(0), 0, true);
		avanzado = new Socio(3, "Ava", "ava", "1234", new Date(0), 0, true);
		principiante.inscribirse("TENIS");
		avanzado.inscribirse("TENIS").restaurar(1500, new ArrayList<Integer>());
	}

	private ArrayList<Socio> jugadores(Socio... socios) {
		ArrayList<Socio> lista = new ArrayList<Socio>();
		for (Socio s : socios) {
			lista.add(s);
		}
		return lista;
	}

	@Test
	public void programarPartidoCreaUnPartidoMarcadoComoDeEntrenador() {
		Date fecha = new Date(5000);
		PartidoPractica p = entrenador.programarPartido(fecha, "TENIS", cancha, jugadores(principiante, avanzado));
		assertTrue(p.isProgramadoPorEntrenador());
		assertFalse(p.isFinalizado());
		assertEquals(fecha, p.getFecha());
		assertEquals("TENIS", p.getDisciplina());
		assertSame(cancha, p.getInstalacion());
		assertEquals(2, p.getJugadoresParticipantes().size());
	}

	@Test
	public void elEntrenadorPuedeEmparejarNivelesNoAdyacentes() {
		PartidoPractica p = entrenador.programarPartido(new Date(), "TENIS", cancha, jugadores(principiante, avanzado));
		p.validarNiveles();
	}

	@Test
	public void elMismoEmparejamientoHechoPorUnSocioSeRechaza() {
		PartidoPractica p = principiante.programarPartido(new Date(), "TENIS", cancha,
				jugadores(principiante, avanzado));
		assertFalse(p.isProgramadoPorEntrenador());
		assertThrows(IllegalStateException.class, () -> p.validarNiveles());
	}

	@Test
	public void instalacionDeOtraDisciplinaLanzaExcepcion() {
		assertThrows(IllegalArgumentException.class, () -> entrenador.programarPartido(new Date(), "TENIS",
				new CanchaPadel("Padel 1"), jugadores(principiante, avanzado)));
	}

	@Test
	public void numeroDeJugadoresFueraDelRangoDeLaInstalacionLanzaExcepcion() {
		assertThrows(IllegalArgumentException.class,
				() -> entrenador.programarPartido(new Date(), "TENIS", cancha, jugadores(principiante)));
	}

	@Test
	public void jugadorNoInscritoEnLaDisciplinaLanzaExcepcion() {
		Socio sinInscripcion = new Socio(5, "Sin", "sin", "1234", new Date(0), 0, true);
		assertThrows(IllegalStateException.class, () -> entrenador.programarPartido(new Date(), "TENIS", cancha,
				jugadores(principiante, sinInscripcion)));
	}

	@Test
	public void elEntrenadorEsParticipanteConIdYNombre() {
		Participante p = entrenador;
		assertEquals(1, p.getId());
		assertEquals("Coach", p.getNombre());
	}
	

}
