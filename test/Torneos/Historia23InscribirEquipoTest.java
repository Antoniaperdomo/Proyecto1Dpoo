package Torneos;

import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import Deportes.Baloncesto;
import Deportes.Categoria;
import Deportes.Equipo;
import Deportes.Futbol;
import Deportes.Partido;
import Deportes.Suspension;
import Usuario.Socio;

// HU23 Socio: inscribir a su equipo en un torneo de deporte de conjunto. 
public class Historia23InscribirEquipoTest {
	private ArrayList<Equipo> club;
	private ArrayList<Partido> partidos;

	@BeforeEach
	public void setUp() {
		club = new ArrayList<Equipo>();
		partidos = new ArrayList<Partido>();
	}

	private InscripcionTorneo inscribir(Torneo t, Equipo e) {
		return t.inscribirEquipo(e, Fixture.integrante(e, 0), club, partidos, Fixture.ANTES_DEL_LIMITE);
	}

	@Test
	public void equipoActivoMismoDeporteCategoriaConMinimoOcupaCupoRegular() {
		Torneo t = Fixture.recreativoFutbol(8);
		Equipo e = Fixture.equipoFutbol("Alfa", 7, 100);
		club.add(e);
		InscripcionTorneo i = inscribir(t, e);
		assertEquals(InscripcionTorneo.REGULAR, i.getTipoCupo());
		assertEquals(1, t.getInscripciones().size());
	}

	@Test
	public void minimoDeJugadoresDependeDelDeporte() {
		Torneo t = new TorneoRecreativo(1, "Basket", new Baloncesto(), new Categoria(Categoria.MAYORES), null, null, 8,
				Fixture.INICIO, Fixture.LIMITE, 0, 10);
		inscribir(t, Fixture.equipoBaloncesto("Cinco", 5, 100)); // baloncesto exige 5
		assertThrows(IllegalStateException.class, () -> inscribir(t, Fixture.equipoBaloncesto("Cuatro", 4, 200)));
	}

	@Test
	public void equipoDeOtroDeporteOCategoriaSeRechazaIndicandoLoRequerido() {
		Torneo t = Fixture.recreativoFutbol(8);
		IllegalArgumentException otroDeporte = assertThrows(IllegalArgumentException.class,
				() -> inscribir(t, Fixture.equipoBaloncesto("Basket", 5, 100)));
		assertTrue(otroDeporte.getMessage().contains("FUTBOL"));
		assertTrue(otroDeporte.getMessage().contains("MAYORES"));
		Equipo sub15 = new Equipo("Peques", new Futbol(), 20, new Categoria(Categoria.SUB15),
				new ArrayList<Deportes.JugadorEquipo>(), new ArrayList<Suspension>(), true);
		Socio chico = new Socio(300, "Chico", "chico", "1234", Fixture.fecha("2013-01-01"), 0, true);
		chico.unirseEquipo(sub15, "DELANTERO", 9);
		assertThrows(IllegalArgumentException.class,
				() -> t.inscribirEquipo(sub15, chico, club, partidos, Fixture.ANTES_DEL_LIMITE));
	}

	@Test
	public void equipoSinMinimoDeHabilitadosSeRechazaIndicandoCuantosFaltan() {
		Torneo t = Fixture.recreativoFutbol(8);
		Equipo e = Fixture.equipoFutbol("Corto", 6, 100);
		IllegalStateException ex = assertThrows(IllegalStateException.class, () -> inscribir(t, e));
		assertTrue(ex.getMessage().contains("1 jugadores"));
	}

	@Test
	public void losSuspendidosNoCuentanParaElMinimo() {
		Torneo t = Fixture.recreativoFutbol(8);
		Equipo e = Fixture.equipoFutbol("Suspendido", 7, 100);
		e.getSuspensiones().add(new Suspension(e.getJugadores().get(1).getParticipante(), "roja", Suspension.INTERNO));
		assertThrows(IllegalStateException.class, () -> inscribir(t, e));
	}

	@Test
	public void socioNoIntegranteOEquipoYaInscritoSeRechaza() {
		Torneo t = Fixture.recreativoFutbol(8);
		Equipo e = Fixture.equipoFutbol("Alfa", 7, 100);
		Socio ajeno = Fixture.socio(999);
		assertThrows(IllegalStateException.class,
				() -> t.inscribirEquipo(e, ajeno, club, partidos, Fixture.ANTES_DEL_LIMITE));
		inscribir(t, e);
		assertThrows(IllegalStateException.class, () -> inscribir(t, e));
	}

	@Test
	public void equipoDadoDeBajaSeRechaza() {
		Torneo t = Fixture.recreativoFutbol(8);
		Equipo e = Fixture.equipoFutbol("Alfa", 7, 100);
		e.setActivo(false);
		assertThrows(IllegalStateException.class, () -> inscribir(t, e));
	}

	@Test
	public void cuposAgotadosSeRechazaIndicandoQueEstaLleno() {
		Torneo t = Fixture.recreativoFutbol(2);
		Equipo a = Fixture.equipoFutbol("A", 7, 100);
		t.inscribirEquipo(a, Fixture.integrante(a, 0), club, partidos, Fixture.DESPUES_DEL_LIMITE);
		Equipo b = Fixture.equipoFutbol("B", 7, 200);
		Equipo c = Fixture.equipoFutbol("C", 7, 300);
		t.inscribirEquipo(b, Fixture.integrante(b, 0), club, partidos, Fixture.DESPUES_DEL_LIMITE);
		IllegalStateException e = assertThrows(IllegalStateException.class,
				() -> t.inscribirEquipo(c, Fixture.integrante(c, 0), club, partidos, Fixture.DESPUES_DEL_LIMITE));
		assertTrue(e.getMessage().contains("lleno"));
	}

	@Test
	public void competitivoRegistraLaTarifaComoPagada() {
		Torneo t = Fixture.competitivoFutbol(8, 70000);
		InscripcionTorneo i = inscribir(t, Fixture.equipoFutbol("Alfa", 7, 100));
		assertEquals(70000, i.getTarifaPagada(), 0.0001);
		assertEquals(70000, t.getTotalRecaudado(), 0.0001);
	}

	@Test
	public void torneoNoAbiertoSeRechaza() {
		Torneo t = Fixture.recreativoFutbol(8);
		inscribir(t, Fixture.equipoFutbol("A", 7, 100));
		inscribir(t, Fixture.equipoFutbol("B", 7, 200));
		t.iniciar();
		assertThrows(IllegalStateException.class, () -> inscribir(t, Fixture.equipoFutbol("C", 7, 300)));
	}
}
