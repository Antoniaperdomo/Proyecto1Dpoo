package Torneos;

import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import Usuario.Administrador;

// HU17 Administrador y HU22 Socio: consultar torneos y su historial. 
public class Historia17ConsultarTorneosTest {
	private Administrador admin;
	private ArrayList<Torneo> torneos;
	private Torneo abierto;
	private Torneo enCurso;

	@BeforeEach
	public void setUp() {
		admin = Fixture.admin();
		torneos = new ArrayList<Torneo>();
		abierto = admin.crearTorneoRecreativo(torneos, "Copa Abierta", new Deportes.Futbol(),
				new Deportes.Categoria(Deportes.Categoria.MAYORES), null, null, 8, Fixture.INICIO, Fixture.LIMITE, 0,
				10);
		enCurso = admin.crearTorneoCompetitivo(torneos, "Liga Activa", new Deportes.Futbol(),
				new Deportes.Categoria(Deportes.Categoria.MAYORES), null, null, 8, Fixture.INICIO, Fixture.LIMITE,
				20000);
		Deportes.Equipo a = Fixture.equipoFutbol("A", 7, 100);
		Deportes.Equipo b = Fixture.equipoFutbol("B", 7, 200);
		enCurso.inscribirEquipo(a, Fixture.integrante(a, 0), new ArrayList<Deportes.Equipo>(),
				new ArrayList<Deportes.Partido>(), Fixture.ANTES_DEL_LIMITE);
		enCurso.inscribirEquipo(b, Fixture.integrante(b, 0), new ArrayList<Deportes.Equipo>(),
				new ArrayList<Deportes.Partido>(), Fixture.ANTES_DEL_LIMITE);
		enCurso.iniciar();
	}

	@Test
	public void listadoMuestraNombreTipoDeporteCategoriaCuposYEstado() {
		String texto = admin.listarTorneos(torneos, null);
		assertTrue(texto.contains("Copa Abierta"));
		assertTrue(texto.contains("RECREATIVO"));
		assertTrue(texto.contains("COMPETITIVO"));
		assertTrue(texto.contains("FUTBOL"));
		assertTrue(texto.contains("MAYORES"));
		assertTrue(texto.contains("ABIERTO"));
		assertTrue(texto.contains("EN_CURSO"));
	}

	@Test
	public void filtroPorEstadoMuestraSoloEseEstado() {
		String texto = admin.listarTorneos(torneos, Torneo.EN_CURSO);
		assertTrue(texto.contains("Liga Activa"));
		assertFalse(texto.contains("Copa Abierta"));
	}

	@Test
	public void filtroPorTipoOPorDeporteDelSocio() {
		assertEquals(1, CatalogoTorneos.filtrar(torneos, Torneo.ABIERTO, "RECREATIVO", null).size());
		assertEquals(0, CatalogoTorneos.filtrar(torneos, Torneo.ABIERTO, "COMPETITIVO", null).size());
		assertEquals(1, CatalogoTorneos.filtrar(torneos, Torneo.ABIERTO, null, "futbol").size());
		assertEquals(0, CatalogoTorneos.filtrar(torneos, Torneo.ABIERTO, null, "TENIS").size());
	}

	@Test
	public void sinTorneosAbiertosInformaQueNoHay() {
		assertEquals("No hay torneos disponibles.", CatalogoTorneos.listar(CatalogoTorneos.filtrar(torneos,
				Torneo.FINALIZADO, null, null)));
	}

	@Test
	public void historialMuestraEncuentrosParticipantesYPosiciones() {
		String historial = admin.historialTorneo(torneos, enCurso.getId());
		assertTrue(historial.contains("A vs B"));
		assertTrue(historial.contains("Participantes (2/8)"));
		assertTrue(historial.contains("Posiciones"));
	}

	@Test
	public void historialDeUnTorneoAbiertoTambienSePuedeConsultar() {
		assertTrue(admin.historialTorneo(torneos, abierto.getId()).contains("[ABIERTO]"));
	}

	@Test
	public void torneoInexistenteSeRechaza() {
		assertThrows(IllegalArgumentException.class, () -> admin.historialTorneo(torneos, 99));
	}
}
