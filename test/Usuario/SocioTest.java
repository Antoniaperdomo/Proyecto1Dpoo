package Usuario;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import Deportes.CanchaPadel;
import Deportes.CanchaTenis;
import Deportes.PartidoPractica;


public class SocioTest {
	private static final long MILISEGUNDOS_POR_ANIO = 31557600000L;
	private Socio socio;
	
	@BeforeEach
	public void setUp() {
		socio = new Socio(1, "Antonia", "antonia", "1234", new Date(0), 0, true);
	}
	
	private ArrayList<Socio> jugadores(Socio... socios) {
		ArrayList<Socio> lista = new ArrayList<Socio>();
		for (Socio s : socios) {
			lista.add(s);
		}
		return lista;
	}
	
	@Test
	public void socioNuevoNoTieneEquiposInscripcionesReservasNiPartidos() {
		assertTrue(socio.isActivo());
		assertEquals(0, socio.getEquipos().size());
		assertEquals(0, socio.getInscripciones().size());
		assertEquals(0, socio.getReservas().size());
		assertEquals(0, socio.getPartidosPractica().size());
	}

	@Test
	public void conservaLosPuntosDeFidelidadConLosQueSeCrea() {
		Socio conPuntos = new Socio(2, "Luis", "luis", "1234", new Date(0), 500, true);
		assertEquals(500, conPuntos.getPuntosFidelidad());
		conPuntos.setPuntosFidelidad(120);
		assertEquals(120, conPuntos.getPuntosFidelidad());
	}

	@Test
	public void elSocioNoTieneDescuentoPropio() {
		assertEquals(0, socio.getDescuentoPropio(), 0.0001);
	}

	@Test
	public void darDeBajaYReactivarCambiaElEstadoActivo() {
		socio.setActivo(false);
		assertFalse(socio.isActivo());
		socio.setActivo(true);
		assertTrue(socio.isActivo());
	}

	@Test
	public void edadSeCalculaDesdeLaFechaDeNacimiento() {
		long ahora = System.currentTimeMillis();
		Socio veinte = new Socio(3, "Veinte", "v20", "1234", new Date(ahora - (long) (20.5 * MILISEGUNDOS_POR_ANIO)),
				0, true);
		assertEquals(20, veinte.getEdad());
	}

	@Test
	public void edadDeUnRecienNacidoEsCero() {
		Socio bebe = new Socio(4, "Bebe", "bebe", "1234", new Date(), 0, true);
		assertEquals(0, bebe.getEdad());
	}

	@Test
	public void programarPartidoDeSocioNoEsDeEntrenador() {
		Socio rival = new Socio(5, "Rival", "rival", "1234", new Date(0), 0, true);
		socio.inscribirse("TENIS");
		rival.inscribirse("TENIS");
		PartidoPractica p = socio.programarPartido(new Date(), "TENIS", new CanchaTenis("Tenis 1"),
				jugadores(socio, rival));
		assertFalse(p.isProgramadoPorEntrenador());
		p.validarNiveles();
	}

	@Test
	public void programarPartidoConNivelesNoAdyacentesFallaAlValidar() {
		Socio experto = new Socio(6, "Experto", "exp", "1234", new Date(0), 0, true);
		socio.inscribirse("TENIS");
		experto.inscribirse("TENIS").restaurar(1600, new ArrayList<Integer>());
		PartidoPractica p = socio.programarPartido(new Date(), "TENIS", new CanchaTenis("Tenis 1"),
				jugadores(socio, experto));
		assertThrows(IllegalStateException.class, () -> p.validarNiveles());
	}

	@Test
	public void programarPartidoEnInstalacionDeOtraDisciplinaLanzaExcepcion() {
		Socio rival = new Socio(7, "Rival", "rival7", "1234", new Date(0), 0, true);
		socio.inscribirse("TENIS");
		rival.inscribirse("TENIS");
		assertThrows(IllegalArgumentException.class, () -> socio.programarPartido(new Date(), "TENIS",
				new CanchaPadel("Padel 1"), jugadores(socio, rival)));
	}

	@Test
	public void registrarEnJugadoresAgregaElPartidoACadaSocio() {
		Socio rival = new Socio(8, "Rival", "rival8", "1234", new Date(0), 0, true);
		socio.inscribirse("TENIS");
		rival.inscribirse("TENIS");
		PartidoPractica p = socio.programarPartido(new Date(), "TENIS", new CanchaTenis("Tenis 1"),
				jugadores(socio, rival));
		p.registrarEnJugadores();
		assertEquals(1, socio.getPartidosPractica().size());
		assertSame(p, rival.getPartidosPractica().get(0));
	}

	@Test
	public void agregarPartidoPracticaLoGuardaEnElSocio() {
		Socio rival = new Socio(9, "Rival", "rival9", "1234", new Date(0), 0, true);
		socio.inscribirse("PADEL");
		rival.inscribirse("PADEL");
		Socio c = new Socio(10, "C", "c10", "1234", new Date(0), 0, true);
		Socio d = new Socio(11, "D", "d11", "1234", new Date(0), 0, true);
		c.inscribirse("PADEL");
		d.inscribirse("PADEL");
		PartidoPractica p = socio.programarPartido(new Date(), "PADEL", new CanchaPadel("Padel 1"),
				jugadores(socio, rival, c, d));
		socio.agregarPartidoPractica(p);
		assertEquals(1, socio.getPartidosPractica().size());
	}
}
