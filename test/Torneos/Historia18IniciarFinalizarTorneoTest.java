package Torneos;

import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;
import Administradores.Turno;
import Deportes.Equipo;
import Deportes.Partido;
import Usuario.Administrador;
import Usuario.Entrenador;
import Usuario.Socio;

// HU18 Administrador: iniciar y finalizar un torneo y asignar los premios. 
public class Historia18IniciarFinalizarTorneoTest {
	private Administrador admin = Fixture.admin();

	private void inscribirEquipos(Torneo t, Equipo... equipos) {
		for (Equipo e : equipos) {
			t.inscribirEquipo(e, Fixture.integrante(e, 0), new ArrayList<Equipo>(), new ArrayList<Partido>(),
					Fixture.ANTES_DEL_LIMITE);
		}
	}

	private ArrayList<Socio> inscribirSocios(Torneo t, int... ids) {
		ArrayList<Socio> socios = new ArrayList<Socio>();
		for (int id : ids) {
			socios.add(Fixture.socioEnDisciplina(id, "TENIS", 1200));
		}
		for (Socio s : socios) {
			Fixture.inscribir(t, s, socios, Fixture.ANTES_DEL_LIMITE);
		}
		return socios;
	}

	@Test
	public void iniciarConConjuntoGeneraTodosContraTodos() {
		Torneo t = Fixture.recreativoFutbol(8);
		inscribirEquipos(t, Fixture.equipoFutbol("A", 7, 100), Fixture.equipoFutbol("B", 7, 200),
				Fixture.equipoFutbol("C", 7, 300));
		admin.iniciarTorneo(t);
		assertEquals(Torneo.EN_CURSO, t.getEstado());
		assertEquals(3, t.getEncuentros().size());
	}

	@Test
	public void iniciarIndividualGeneraLaPrimeraRondaDeEliminacion() {
		Torneo t = Fixture.recreativoTenis(8, Torneo.SENCILLOS);
		inscribirSocios(t, 1, 2, 3, 4);
		t.iniciar();
		assertEquals(2, t.getEncuentros().size());
		assertEquals(3, t.encuentrosFaltantes());
	}

	@Test
	public void iniciarConMenosDeDosOSinEstarAbiertoSeRechaza() {
		Torneo t = Fixture.recreativoFutbol(8);
		inscribirEquipos(t, Fixture.equipoFutbol("A", 7, 100));
		assertThrows(IllegalStateException.class, () -> admin.iniciarTorneo(t));
		inscribirEquipos(t, Fixture.equipoFutbol("B", 7, 200));
		admin.iniciarTorneo(t);
		assertThrows(IllegalStateException.class, () -> admin.iniciarTorneo(t));
	}

	@Test
	public void iniciadoElTorneoSeCierranLasInscripciones() {
		Torneo t = Fixture.recreativoFutbol(8);
		inscribirEquipos(t, Fixture.equipoFutbol("A", 7, 100), Fixture.equipoFutbol("B", 7, 200));
		t.iniciar();
		Equipo c = Fixture.equipoFutbol("C", 7, 300);
		assertThrows(IllegalStateException.class, () -> inscribirEquipos(t, c));
	}

	@Test
	public void finalizarConTodosLosEncuentrosJugadosDefineGanador() {
		Torneo t = Fixture.recreativoFutbol(8);
		Equipo a = Fixture.equipoFutbol("A", 7, 100);
		Equipo b = Fixture.equipoFutbol("B", 7, 200);
		inscribirEquipos(t, a, b);
		t.iniciar();
		admin.registrarResultadoEncuentro(t, t.getEncuentros().get(0), 2, 0);
		admin.finalizarTorneo(t);
		assertEquals(Torneo.FINALIZADO, t.getEstado());
		assertEquals("A", t.getGanador().getNombre());
	}

	@Test
	public void finalizarConEncuentrosPendientesSeRechazaIndicandoCuantos() {
		Torneo t = Fixture.recreativoFutbol(8);
		inscribirEquipos(t, Fixture.equipoFutbol("A", 7, 100), Fixture.equipoFutbol("B", 7, 200),
				Fixture.equipoFutbol("C", 7, 300));
		t.iniciar();
		t.registrarResultado(t.getEncuentros().get(0), 1, 0);
		IllegalStateException e = assertThrows(IllegalStateException.class, () -> admin.finalizarTorneo(t));
		assertTrue(e.getMessage().contains("Faltan 2"));
		assertEquals(Torneo.EN_CURSO, t.getEstado());
	}

	@Test
	public void recreativoEntregaBonoACadaSocioDelEquipoGanador() {
		Torneo t = Fixture.recreativoFutbol(8);
		Equipo a = Fixture.equipoFutbol("A", 7, 100);
		Equipo b = Fixture.equipoFutbol("B", 7, 200);
		inscribirEquipos(t, a, b);
		t.iniciar();
		t.registrarResultado(t.getEncuentros().get(0), 3, 1);
		t.finalizar();
		for (int i = 0; i < 7; i++) {
			assertEquals(1, Fixture.integrante(a, i).getBonosDisponibles().size());
			assertEquals(20, Fixture.integrante(a, i).getBonos().get(0).getPorcentaje());
			assertEquals(0, Fixture.integrante(b, i).getBonos().size());
		}
	}

	@Test
	public void competitivoEntregaElTotalRecaudadoAlCampeonDelCuadro() {
		Torneo t = Fixture.competitivoTenis(5, Torneo.SENCILLOS, 50000); // 4 regulares + 1 preferencial
		ArrayList<Socio> socios = inscribirSocios(t, 1, 2, 3, 4);
		assertEquals(200000, t.getTotalRecaudado(), 0.0001);
		t.iniciar();
		t.registrarResultado(t.getEncuentros().get(0), 6, 4); // 1 gana a 2
		t.registrarResultado(t.getEncuentros().get(1), 6, 2); // 3 gana a 4
		assertEquals(3, t.getEncuentros().size()); // se genero la final
		assertEquals(1, t.encuentrosFaltantes());
		t.registrarResultado(t.getEncuentros().get(2), 6, 3); // 1 gana la final
		t.finalizar();
		assertEquals("Socio1", t.getGanador().getNombre());
		assertEquals("Socio3", t.clasificacion().get(1).getNombre());
		assertEquals(1, t.getPremiosEntregados().size());
		assertTrue(t.getPremiosEntregados().get(0).contains("200000.0"));
		assertTrue(t.getPremiosEntregados().get(0).contains("Socio1"));
		assertEquals(0, socios.get(0).getBonos().size()); // competitivo: sin bonos
	}

	@Test
	public void competitivoReparteElPremioEnPartesIgualesEntreLaPareja() {
		Torneo t = Fixture.competitivoTenis(4, Torneo.DOBLES, 40000);
		ArrayList<Socio> s = new ArrayList<Socio>();
		for (int id = 1; id <= 4; id++) {
			s.add(Fixture.socioEnDisciplina(id, "TENIS", 1200));
		}
		ArrayList<Turno> sinTurnos = new ArrayList<Turno>();
		t.inscribirParticipante(s.get(0), s.get(1), null, s, sinTurnos, Fixture.ANTES_DEL_LIMITE);
		t.inscribirParticipante(s.get(2), s.get(3), null, s, sinTurnos, Fixture.ANTES_DEL_LIMITE);
		t.iniciar();
		t.registrarResultado(t.getEncuentros().get(0), 6, 1);
		t.finalizar();
		assertEquals(2, t.getPremiosEntregados().size());
		assertTrue(t.getPremiosEntregados().get(0).contains("40000.0")); // 80000 / 2
	}

	@Test
	public void ganadorEmpleadoCedeElPremioEnEfectivoAlSiguienteClasificado() {
		Torneo t = Fixture.competitivoTenis(2, Torneo.SENCILLOS, 50000);
		Entrenador entrenador = Fixture.entrenador(50);
		Socio socio = Fixture.socioEnDisciplina(1, "TENIS", 1200);
		ArrayList<Socio> socios = Fixture.lista(socio);
		t.inscribirParticipante(entrenador, null, null, socios, new ArrayList<Turno>(), Fixture.ANTES_DEL_LIMITE);
		Fixture.inscribir(t, socio, socios, Fixture.ANTES_DEL_LIMITE);
		assertEquals(50000, t.getTotalRecaudado(), 0.0001); // el empleado no paga
		t.iniciar();
		t.registrarResultado(t.getEncuentros().get(0), 6, 0); // gana el entrenador
		t.finalizar();
		assertEquals("Entrenador50", t.getGanador().getNombre());
		assertEquals(1, t.getPremiosEntregados().size());
		assertTrue(t.getPremiosEntregados().get(0).contains("Socio1"));
		assertTrue(t.getPremiosEntregados().get(0).contains("50000.0"));
	}

	@Test
	public void ganadorEmpleadoEnRecreativoNoRecibeBono() {
		Torneo t = Fixture.recreativoTenis(2, Torneo.SENCILLOS);
		Entrenador entrenador = Fixture.entrenador(50);
		Socio socio = Fixture.socioEnDisciplina(1, "TENIS", 1200);
		t.inscribirParticipante(entrenador, null, null, Fixture.lista(socio), new ArrayList<Turno>(),
				Fixture.ANTES_DEL_LIMITE);
		Fixture.inscribir(t, socio, Fixture.lista(socio), Fixture.ANTES_DEL_LIMITE);
		t.iniciar();
		t.registrarResultado(t.getEncuentros().get(0), 6, 0);
		t.finalizar();
		assertTrue(t.getPremiosEntregados().isEmpty());
		assertEquals(0, socio.getBonos().size());
	}
}
