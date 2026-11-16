package Torneos;

import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import Deportes.Categoria;
import Deportes.DeporteIndividual;
import Deportes.Futbol;
import Deportes.Tenis;
import Usuario.Administrador;

// HU16 Administrador: crear un torneo interno.
public class Historia16CrearTorneoTest {
	private Administrador admin;
	private ArrayList<Torneo> torneos;

	@BeforeEach
	public void setUp() {
		admin = Fixture.admin();
		torneos = new ArrayList<Torneo>();
	}

	private TorneoRecreativo recreativoFutbol(int cupos) {
		return admin.crearTorneoRecreativo(torneos, "Copa", new Futbol(), new Categoria(Categoria.MAYORES), null, null,
				cupos, Fixture.INICIO, Fixture.LIMITE, 0, 20);
	}

	@Test
	public void conjuntoValidoQuedaAbiertoYCalculaPreferenciales() {
		Torneo t = recreativoFutbol(10);
		assertEquals(Torneo.ABIERTO, t.getEstado());
		assertEquals(1, t.getId());
		assertEquals(2, t.getCuposPreferenciales());
		assertEquals(8, t.getCuposRegulares());
		assertEquals(1, torneos.size());
	}

	@Test
	public void individualValidoConNivelYModalidadQuedaAbierto() {
		Torneo t = admin.crearTorneoCompetitivo(torneos, "Abierto", new Tenis(DeporteIndividual.INTERMEDIO), null,
				DeporteIndividual.INTERMEDIO, Torneo.DOBLES, 8, Fixture.INICIO, Fixture.LIMITE, 30000);
		assertEquals(Torneo.ABIERTO, t.getEstado());
		assertEquals(Torneo.DOBLES, t.getModalidad());
		assertEquals("COMPETITIVO", t.getTipo());
	}

	@Test
	public void competitivoSinTarifaOTarifaNoPositivaSeRechaza() {
		assertThrows(IllegalArgumentException.class, () -> admin.crearTorneoCompetitivo(torneos, "Liga", new Futbol(),
				new Categoria(Categoria.MAYORES), null, null, 8, Fixture.INICIO, Fixture.LIMITE, 0));
		assertThrows(IllegalArgumentException.class, () -> admin.crearTorneoCompetitivo(torneos, "Liga", new Futbol(),
				new Categoria(Categoria.MAYORES), null, null, 8, Fixture.INICIO, Fixture.LIMITE, -5));
		assertTrue(torneos.isEmpty());
	}

	@Test
	public void recreativoConBonoValidoSinTarifaSeRegistra() {
		TorneoRecreativo t = recreativoFutbol(8);
		assertEquals(20, t.getPorcentajeBono());
		assertEquals(0, t.getTarifa(), 0.0001);
	}

	@Test
	public void recreativoConTarifaOSinBonoSeRechaza() {
		assertThrows(IllegalArgumentException.class, () -> admin.crearTorneoRecreativo(torneos, "Copa", new Futbol(),
				new Categoria(Categoria.MAYORES), null, null, 8, Fixture.INICIO, Fixture.LIMITE, 5000, 20));
		assertThrows(IllegalArgumentException.class, () -> admin.crearTorneoRecreativo(torneos, "Copa", new Futbol(),
				new Categoria(Categoria.MAYORES), null, null, 8, Fixture.INICIO, Fixture.LIMITE, 0, 0));
		assertThrows(IllegalArgumentException.class, () -> admin.crearTorneoRecreativo(torneos, "Copa", new Futbol(),
				new Categoria(Categoria.MAYORES), null, null, 8, Fixture.INICIO, Fixture.LIMITE, 0, 101));
	}

	@Test
	public void deporteCategoriaNivelInexistenteOCuposMenoresA2SeRechaza() {
		assertThrows(IllegalArgumentException.class, () -> admin.crearTorneoRecreativo(torneos, "Copa", null,
				new Categoria(Categoria.MAYORES), null, null, 8, Fixture.INICIO, Fixture.LIMITE, 0, 20));
		assertThrows(IllegalArgumentException.class, () -> admin.crearTorneoRecreativo(torneos, "Copa", new Futbol(),
				null, null, null, 8, Fixture.INICIO, Fixture.LIMITE, 0, 20));
		assertThrows(IllegalArgumentException.class,
				() -> admin.crearTorneoRecreativo(torneos, "Abierto", new Tenis(DeporteIndividual.INTERMEDIO), null,
						"EXPERTO", Torneo.SENCILLOS, 8, Fixture.INICIO, Fixture.LIMITE, 0, 20));
		assertThrows(IllegalArgumentException.class, () -> recreativoFutbol(1));
		assertTrue(torneos.isEmpty());
	}

	@Test
	public void variosTorneosAbiertosMismoDeporteYCategoriaSeAceptan() {
		Torneo a = recreativoFutbol(8);
		Torneo b = recreativoFutbol(8);
		assertEquals(2, torneos.size());
		assertEquals(1, a.getId());
		assertEquals(2, b.getId());
	}

	@Test
	public void cupos11ReservanTresPreferenciales() {
		assertEquals(3, recreativoFutbol(11).getCuposPreferenciales());
		assertEquals(1, recreativoFutbol(5).getCuposPreferenciales());
		assertEquals(1, recreativoFutbol(2).getCuposPreferenciales());
	}
}
