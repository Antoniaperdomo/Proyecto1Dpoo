package Torneos;

import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;
import Deportes.Equipo;
import Deportes.Partido;
import Deportes.PartidoInterno;
import Usuario.Socio;

// HU25 Socio: cupo preferencial por ranking (20% de los cupos, redondeado hacia arriba).
public class Historia25CupoPreferencialTest {

	/** 5 cupos = 4 regulares + 1 preferencial; se llenan los regulares con ELO 1200..1203. */
	private ArrayList<Socio> llenarRegulares(Torneo t) {
		ArrayList<Socio> socios = new ArrayList<Socio>();
		for (int id = 1; id <= 4; id++) {
			socios.add(Fixture.socioEnDisciplina(id, "TENIS", 1199 + id));
		}
		for (Socio s : socios) {
			Fixture.inscribir(t, s, socios, Fixture.ANTES_DEL_LIMITE);
		}
		return socios;
	}

	@Test
	public void individualMejorELOOcupaPreferencialConRegularesAgotados() {
		Torneo t = Fixture.recreativoTenis(5, Torneo.SENCILLOS);
		ArrayList<Socio> socios = llenarRegulares(t);
		Socio estrella = Fixture.socioEnDisciplina(10, "TENIS", 1350);
		socios.add(estrella);
		InscripcionTorneo i = Fixture.inscribir(t, estrella, socios, Fixture.ANTES_DEL_LIMITE);
		assertEquals(InscripcionTorneo.PREFERENCIAL, i.getTipoCupo());
		assertEquals(1, t.contarOcupados(InscripcionTorneo.PREFERENCIAL));
	}

	@Test
	public void individualFueraDeLosMejoresSeRechazaInformandoQueSoloQuedanReservados() {
		Torneo t = Fixture.recreativoTenis(5, Torneo.SENCILLOS);
		ArrayList<Socio> socios = llenarRegulares(t);
		Socio comun = Fixture.socioEnDisciplina(10, "TENIS", 1200);
		socios.add(comun);
		IllegalStateException e = assertThrows(IllegalStateException.class,
				() -> Fixture.inscribir(t, comun, socios, Fixture.ANTES_DEL_LIMITE));
		assertTrue(e.getMessage().contains("reservados"));
	}

	@Test
	public void hayTantosPreferencialesComoCuposReservados() {
		Torneo t = Fixture.recreativoTenis(5, Torneo.SENCILLOS);
		ArrayList<Socio> socios = llenarRegulares(t);
		Socio primero = Fixture.socioEnDisciplina(10, "TENIS", 1390);
		Socio segundo = Fixture.socioEnDisciplina(11, "TENIS", 1380);
		socios.add(primero);
		socios.add(segundo);
		assertEquals(InscripcionTorneo.PREFERENCIAL,
				Fixture.inscribir(t, primero, socios, Fixture.ANTES_DEL_LIMITE).getTipoCupo());
		assertThrows(IllegalStateException.class,
				() -> Fixture.inscribir(t, segundo, socios, Fixture.ANTES_DEL_LIMITE));
	}

	@Test
	public void conjuntoEquipoDeMayorPuntajeOcupaPreferencial() {
		Torneo t = Fixture.recreativoFutbol(2); // 1 regular + 1 preferencial
		Equipo a = Fixture.equipoFutbol("A", 7, 100);
		Equipo b = Fixture.equipoFutbol("B", 7, 200);
		Equipo c = Fixture.equipoFutbol("C", 7, 300);
		ArrayList<Equipo> club = new ArrayList<Equipo>();
		club.add(a);
		club.add(b);
		club.add(c);
		ArrayList<Partido> partidos = new ArrayList<Partido>();
		PartidoInterno ganoB = new PartidoInterno(Fixture.fecha("2027-01-10"), b.getDeporte(), b, c);
		ganoB.registrarMarcador(2, 0);
		ganoB.setJugado(true);
		partidos.add(ganoB);
		assertEquals(3, RankingTorneo.puntajeAcumulado(b, partidos));
		assertEquals(0, RankingTorneo.puntajeAcumulado(c, partidos));

		t.inscribirEquipo(a, Fixture.integrante(a, 0), club, partidos, Fixture.ANTES_DEL_LIMITE);
		assertEquals(InscripcionTorneo.REGULAR, t.getInscripciones().get(0).getTipoCupo());
		InscripcionTorneo del = t.inscribirEquipo(b, Fixture.integrante(b, 0), club, partidos,
				Fixture.ANTES_DEL_LIMITE);
		assertEquals(InscripcionTorneo.PREFERENCIAL, del.getTipoCupo());
	}

	@Test
	public void conjuntoEquipoFueraDeLosMejoresSeRechaza() {
		Torneo t = Fixture.recreativoFutbol(2);
		Equipo a = Fixture.equipoFutbol("A", 7, 100);
		Equipo b = Fixture.equipoFutbol("B", 7, 200);
		Equipo c = Fixture.equipoFutbol("C", 7, 300);
		ArrayList<Equipo> club = new ArrayList<Equipo>();
		club.add(a);
		club.add(b);
		club.add(c);
		ArrayList<Partido> partidos = new ArrayList<Partido>();
		PartidoInterno ganoB = new PartidoInterno(Fixture.fecha("2027-01-10"), b.getDeporte(), b, c);
		ganoB.registrarMarcador(2, 0);
		ganoB.setJugado(true);
		partidos.add(ganoB);
		t.inscribirEquipo(a, Fixture.integrante(a, 0), club, partidos, Fixture.ANTES_DEL_LIMITE);
		IllegalStateException e = assertThrows(IllegalStateException.class, () -> t.inscribirEquipo(c,
				Fixture.integrante(c, 0), club, partidos, Fixture.ANTES_DEL_LIMITE));
		assertTrue(e.getMessage().contains("reservados"));
	}

	@Test
	public void pasadaLaFechaLimiteLosPreferencialesSeVuelvenRegulares() {
		Torneo t = Fixture.recreativoTenis(5, Torneo.SENCILLOS);
		ArrayList<Socio> socios = llenarRegulares(t);
		Socio comun = Fixture.socioEnDisciplina(10, "TENIS", 1200);
		socios.add(comun);
		InscripcionTorneo i = Fixture.inscribir(t, comun, socios, Fixture.DESPUES_DEL_LIMITE);
		assertEquals(InscripcionTorneo.REGULAR, i.getTipoCupo());
		Socio otro = Fixture.socioEnDisciplina(11, "TENIS", 1200);
		socios.add(otro);
		assertThrows(IllegalStateException.class,
				() -> Fixture.inscribir(t, otro, socios, Fixture.DESPUES_DEL_LIMITE)); // ya esta lleno
	}

	@Test
	public void elRankingSoloCompiteEntreSociosDelMismoNivel() {
		ArrayList<Socio> socios = new ArrayList<Socio>();
		Socio avanzado = Fixture.socioEnDisciplina(1, "TENIS", 1500);
		Socio intermedio = Fixture.socioEnDisciplina(2, "TENIS", 1210);
		socios.add(avanzado);
		socios.add(intermedio);
		assertTrue(RankingTorneo.estaEntreLosMejores(intermedio, "TENIS", "INTERMEDIO", 1, socios));
		assertFalse(RankingTorneo.estaEntreLosMejores(avanzado, "TENIS", "INTERMEDIO", 1, socios));
		assertFalse(RankingTorneo.estaEntreLosMejores(Fixture.externo(9), "TENIS", "INTERMEDIO", 5, socios));
	}
}
