package Deportes;

import static org.junit.jupiter.api.Assertions.*;
import java.util.Date;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import Usuario.Socio;

public class InstalacionTest {
	private CanchaTenis tenis;
	private CanchaPadel padel;
	private MesaTenisDeMesa mesa;

	@BeforeEach
	public void setUp() {
		tenis = new CanchaTenis("Tenis 1");
		padel = new CanchaPadel("Padel 1");
		mesa = new MesaTenisDeMesa("Mesa 1", true);
	}

	@Test
	public void tenisAceptaDe2a4Jugadores() {
		assertAll("Diferentes # de jugadores", () -> assertFalse(tenis.aceptaJugadores(1)),
				() -> assertTrue(tenis.aceptaJugadores(2)), () -> assertTrue(tenis.aceptaJugadores(4)),
				() -> assertFalse(tenis.aceptaJugadores(5))

		);
	}

	@Test
	public void padelAceptaSolo4() {
		assertFalse(padel.aceptaJugadores(3));
		assertTrue(padel.aceptaJugadores(4));
		assertFalse(padel.aceptaJugadores(5));
	}

	@Test
	public void mesaAceptaDe2a4Jugadores() {
		assertAll("Diferentes # de jugadores", () -> assertFalse(mesa.aceptaJugadores(1)),
				() -> assertTrue(mesa.aceptaJugadores(2)), () -> assertTrue(mesa.aceptaJugadores(4)),
				() -> assertFalse(mesa.aceptaJugadores(5))

		);
	}

	@Test
	public void disciplinaCorrecta() {
		assertEquals("TENIS", tenis.getDisciplina());
		assertEquals("PADEL", padel.getDisciplina());
		assertEquals("TENISDEMESA", mesa.getDisciplina());
	}

	@Test
	public void mesaTechada() {
		assertTrue(mesa.isTechada());
		assertFalse(new MesaTenisDeMesa("Mesa 2", false).isTechada());
	}

	@Test
	public void nombreNuloExcepcion() {
		assertThrows(IllegalArgumentException.class, () -> new CanchaTenis(null));
	}

	@Test
	public void instalacionSinReservaNoEstaOcupada() {
		assertFalse(tenis.estaOcupada(new Date(), new Date()));
	}

	@Test
	public void reservaMarcaComoOcupada() {
		Date fecha = new Date();
		Date bloque = new Date();
		Socio socio = new Socio(1, "Prueba", "prueba", "1234", new Date(0), 0, true);
		tenis.agregarReserva(new ReservaInstalacion(socio, tenis, fecha, bloque, "SENCILLOS", 2));
		assertTrue(tenis.estaOcupada(fecha, bloque));
	}

	@Test
	public void reservaNoOcupaOtroBloque() {
		Date fecha = new Date();
		Date bloque = new Date(1000);
		Date otroBloque = new Date(2000);
		Socio socio = new Socio(1, "Prueba", "prueba", "1234", new Date(0), 0, true);
		tenis.agregarReserva(new ReservaInstalacion(socio, tenis, fecha, bloque, "SENCILLOS", 2));
		assertFalse(tenis.estaOcupada(fecha, otroBloque));

	}

}
