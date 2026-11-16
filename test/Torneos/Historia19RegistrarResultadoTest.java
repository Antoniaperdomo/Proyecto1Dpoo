package Torneos;

import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;
import Deportes.Baloncesto;
import Deportes.Categoria;
import Deportes.Equipo;
import Deportes.Partido;
import Usuario.Administrador;
import Usuario.Socio;

// HU19 Administrador: registrar el resultado de un encuentro. 
public class Historia19RegistrarResultadoTest {
	private Administrador admin = Fixture.admin();

	private Torneo futbolEnCurso(Equipo a, Equipo b) {
		Torneo t = Fixture.recreativoFutbol(8);
		for (Equipo e : new Equipo[] { a, b }) {
			t.inscribirEquipo(e, Fixture.integrante(e, 0), new ArrayList<Equipo>(), new ArrayList<Partido>(),
					Fixture.ANTES_DEL_LIMITE);
		}
		t.iniciar();
		return t;
	}

	@Test
	public void victoriaEnFutbolDaTresPuntosYDerrotaCero() {
		Torneo t = futbolEnCurso(Fixture.equipoFutbol("A", 7, 100), Fixture.equipoFutbol("B", 7, 200));
		admin.registrarResultadoEncuentro(t, t.getEncuentros().get(0), 3, 1);
		assertEquals(3, t.puntos(t.getInscripciones().get(0)));
		assertEquals(0, t.puntos(t.getInscripciones().get(1)));
		assertEquals("A", t.clasificacion().get(0).getNombre());
	}

	@Test
	public void empateEnFutbolDaUnPuntoACadaUno() {
		Torneo t = futbolEnCurso(Fixture.equipoFutbol("A", 7, 100), Fixture.equipoFutbol("B", 7, 200));
		t.registrarResultado(t.getEncuentros().get(0), 2, 2);
		assertEquals(1, t.puntos(t.getInscripciones().get(0)));
		assertEquals(1, t.puntos(t.getInscripciones().get(1)));
	}

	@Test
	public void empateEnBaloncestoSeRechaza() {
		Torneo t = new TorneoRecreativo(1, "Basket", new Baloncesto(), new Categoria(Categoria.MAYORES), null, null, 8,
				Fixture.INICIO, Fixture.LIMITE, 0, 10);
		Equipo a = Fixture.equipoBaloncesto("A", 5, 100);
		Equipo b = Fixture.equipoBaloncesto("B", 5, 200);
		for (Equipo e : new Equipo[] { a, b }) {
			t.inscribirEquipo(e, Fixture.integrante(e, 0), new ArrayList<Equipo>(), new ArrayList<Partido>(),
					Fixture.ANTES_DEL_LIMITE);
		}
		t.iniciar();
		assertThrows(IllegalArgumentException.class, () -> t.registrarResultado(t.getEncuentros().get(0), 80, 80));
		assertFalse(t.getEncuentros().get(0).isJugado());
		t.registrarResultado(t.getEncuentros().get(0), 81, 80);
		assertEquals(2, t.puntos(t.getInscripciones().get(0))); // baloncesto: victoria 2
	}

	@Test
	public void empateEnDisciplinaIndividualSeRechaza() {
		Torneo t = Fixture.recreativoTenis(4, Torneo.SENCILLOS);
		ArrayList<Socio> socios = Fixture.lista(Fixture.socioEnDisciplina(1, "TENIS", 1200),
				Fixture.socioEnDisciplina(2, "TENIS", 1200));
		for (Socio s : socios) {
			Fixture.inscribir(t, s, socios, Fixture.ANTES_DEL_LIMITE);
		}
		t.iniciar();
		assertThrows(IllegalArgumentException.class, () -> t.registrarResultado(t.getEncuentros().get(0), 6, 6));
	}

	@Test
	public void marcadorNegativoSeRechaza() {
		Torneo t = futbolEnCurso(Fixture.equipoFutbol("A", 7, 100), Fixture.equipoFutbol("B", 7, 200));
		assertThrows(IllegalArgumentException.class, () -> t.registrarResultado(t.getEncuentros().get(0), -1, 2));
		assertFalse(t.getEncuentros().get(0).isJugado());
	}

	@Test
	public void resultadoYaRegistradoSeRechazaYConservaElOriginal() {
		Torneo t = futbolEnCurso(Fixture.equipoFutbol("A", 7, 100), Fixture.equipoFutbol("B", 7, 200));
		EncuentroTorneo e = t.getEncuentros().get(0);
		t.registrarResultado(e, 2, 1);
		assertThrows(IllegalStateException.class, () -> t.registrarResultado(e, 0, 5));
		assertEquals(2, e.getMarcadorLocal());
		assertEquals(1, e.getMarcadorVisitante());
	}

	@Test
	public void torneoNoEnCursoSeRechaza() {
		Torneo abierto = Fixture.recreativoFutbol(8);
		EncuentroTorneo suelto = new EncuentroTorneo(1, new InscripcionTorneo(Fixture.equipoFutbol("A", 7, 100),
				InscripcionTorneo.REGULAR, 0), new InscripcionTorneo(Fixture.equipoFutbol("B", 7, 200),
						InscripcionTorneo.REGULAR, 0), Fixture.INICIO);
		assertThrows(IllegalStateException.class, () -> abierto.registrarResultado(suelto, 1, 0));
	}

	@Test
	public void elGanadorDeUnEncuentroIndividualAvanzaEnElCuadro() {
		Torneo t = Fixture.recreativoTenis(4, Torneo.SENCILLOS);
		ArrayList<Socio> socios = new ArrayList<Socio>();
		for (int id = 1; id <= 3; id++) {
			socios.add(Fixture.socioEnDisciplina(id, "TENIS", 1200));
		}
		for (Socio s : socios) {
			Fixture.inscribir(t, s, socios, Fixture.ANTES_DEL_LIMITE);
		}
		t.iniciar(); // 3 jugadores: el primero pasa libre y 2 vs 3
		assertEquals(1, t.getEncuentros().size());
		t.registrarResultado(t.getEncuentros().get(0), 6, 3); // gana Socio2
		assertEquals(2, t.getEncuentros().size());
		assertEquals("Socio1", t.getEncuentros().get(1).getLocal().getNombre());
		assertEquals("Socio2", t.getEncuentros().get(1).getVisitante().getNombre());
	}
}
