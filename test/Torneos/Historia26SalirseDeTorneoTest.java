package Torneos;

import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;
import Deportes.Equipo;
import Deportes.Partido;
import Usuario.Socio;

// HU26 Socio: salirse de un torneo antes de que inicie.
public class Historia26SalirseDeTorneoTest {

	@Test
	public void salidaLiberaElCupoRegular() {
		Torneo t = Fixture.recreativoTenis(8, Torneo.SENCILLOS);
		Socio s = Fixture.socioEnDisciplina(1, "TENIS", 1200);
		Fixture.inscribir(t, s, Fixture.lista(s), Fixture.ANTES_DEL_LIMITE);
		t.retirarParticipante(s);
		assertEquals(0, t.contarOcupados(InscripcionTorneo.REGULAR));
		assertEquals(0, t.getInscripciones().size());
	}

	@Test
	public void salidaLiberaElCupoPreferencial() {
		Torneo t = Fixture.recreativoTenis(5, Torneo.SENCILLOS);
		ArrayList<Socio> socios = new ArrayList<Socio>();
		for (int id = 1; id <= 4; id++) {
			socios.add(Fixture.socioEnDisciplina(id, "TENIS", 1199 + id));
		}
		for (Socio s : socios) {
			Fixture.inscribir(t, s, socios, Fixture.ANTES_DEL_LIMITE);
		}
		Socio estrella = Fixture.socioEnDisciplina(10, "TENIS", 1380);
		socios.add(estrella);
		Fixture.inscribir(t, estrella, socios, Fixture.ANTES_DEL_LIMITE);
		assertEquals(1, t.contarOcupados(InscripcionTorneo.PREFERENCIAL));
		t.retirarParticipante(estrella);
		assertEquals(0, t.contarOcupados(InscripcionTorneo.PREFERENCIAL));
	}

	@Test
	public void cupoLiberadoPuedeSerOcupadoPorOtro() {
		Torneo t = Fixture.recreativoTenis(2, Torneo.SENCILLOS);
		Socio a = Fixture.socioEnDisciplina(1, "TENIS", 1200);
		Socio b = Fixture.socioEnDisciplina(2, "TENIS", 1200);
		Socio c = Fixture.socioEnDisciplina(3, "TENIS", 1200);
		ArrayList<Socio> todos = Fixture.lista(a, b, c);
		Fixture.inscribir(t, a, todos, Fixture.DESPUES_DEL_LIMITE);
		Fixture.inscribir(t, b, todos, Fixture.DESPUES_DEL_LIMITE);
		assertThrows(IllegalStateException.class, () -> Fixture.inscribir(t, c, todos, Fixture.DESPUES_DEL_LIMITE));
		t.retirarParticipante(a);
		Fixture.inscribir(t, c, todos, Fixture.DESPUES_DEL_LIMITE);
		assertEquals(2, t.getInscripciones().size());
	}

	@Test
	public void unEquipoSeRetiraYLiberaSuCupo() {
		Torneo t = Fixture.recreativoFutbol(8);
		Equipo e = Fixture.equipoFutbol("A", 7, 100);
		t.inscribirEquipo(e, Fixture.integrante(e, 0), new ArrayList<Equipo>(), new ArrayList<Partido>(),
				Fixture.ANTES_DEL_LIMITE);
		assertThrows(IllegalStateException.class, () -> t.retirarEquipo(e, Fixture.socio(999)));
		t.retirarEquipo(e, Fixture.integrante(e, 3));
		assertTrue(t.getInscripciones().isEmpty());
	}

	@Test
	public void cualquierIntegranteDeLaParejaPuedeSalirseYLiberaElCupoDeLosDos() {
		Torneo t = Fixture.recreativoTenis(8, Torneo.DOBLES);
		Socio a = Fixture.socioEnDisciplina(1, "TENIS", 1200);
		Socio b = Fixture.socioEnDisciplina(2, "TENIS", 1200);
		t.inscribirParticipante(a, b, null, Fixture.lista(a, b), new ArrayList<Administradores.Turno>(),
				Fixture.ANTES_DEL_LIMITE);
		t.retirarParticipante(b);
		assertTrue(t.getInscripciones().isEmpty());
	}

	@Test
	public void torneoEnCursoOFinalizadoSeRechazaIndicandoQueYaInicio() {
		Torneo t = Fixture.recreativoTenis(8, Torneo.SENCILLOS);
		Socio a = Fixture.socioEnDisciplina(1, "TENIS", 1200);
		Socio b = Fixture.socioEnDisciplina(2, "TENIS", 1200);
		ArrayList<Socio> todos = Fixture.lista(a, b);
		Fixture.inscribir(t, a, todos, Fixture.ANTES_DEL_LIMITE);
		Fixture.inscribir(t, b, todos, Fixture.ANTES_DEL_LIMITE);
		t.iniciar();
		IllegalStateException e = assertThrows(IllegalStateException.class, () -> t.retirarParticipante(a));
		assertTrue(e.getMessage().contains("ya inicio"));
		t.registrarResultado(t.getEncuentros().get(0), 6, 0);
		t.finalizar();
		assertThrows(IllegalStateException.class, () -> t.retirarParticipante(a));
	}

	@Test
	public void socioNoInscritoSeRechaza() {
		Torneo t = Fixture.recreativoTenis(8, Torneo.SENCILLOS);
		assertThrows(IllegalStateException.class, () -> t.retirarParticipante(Fixture.socio(1)));
	}

	@Test
	public void competitivoInformaDevolucionExternaYAjustaElRecaudado() {
		Torneo t = Fixture.competitivoTenis(8, Torneo.SENCILLOS, 30000);
		Socio s = Fixture.socioEnDisciplina(1, "TENIS", 1200);
		Fixture.inscribir(t, s, Fixture.lista(s), Fixture.ANTES_DEL_LIMITE);
		assertEquals(30000, t.getTotalRecaudado(), 0.0001);
		InscripcionTorneo retirada = t.retirarParticipante(s);
		assertTrue(t.mensajeRetiro(retirada).contains("fuera del sistema"));
		assertEquals(0, t.getTotalRecaudado(), 0.0001);
	}
}
