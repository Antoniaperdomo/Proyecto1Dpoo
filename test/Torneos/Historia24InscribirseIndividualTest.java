package Torneos;

import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import Administradores.Turno;
import Usuario.Socio;

// HU24 Socio: inscribirse en un torneo individual, solo o en pareja. 
public class Historia24InscribirseIndividualTest {
	private ArrayList<Turno> sinTurnos;

	@BeforeEach
	public void setUp() {
		sinTurnos = new ArrayList<Turno>();
	}

	private Socio intermedio(int id) {
		return Fixture.socioEnDisciplina(id, "TENIS", 1200);
	}

	@Test
	public void sencillosConNivelIgualOcupaCupoRegular() {
		Torneo t = Fixture.recreativoTenis(8, Torneo.SENCILLOS);
		Socio s = intermedio(1);
		InscripcionTorneo i = Fixture.inscribir(t, s, Fixture.lista(s), Fixture.ANTES_DEL_LIMITE);
		assertEquals(InscripcionTorneo.REGULAR, i.getTipoCupo());
		assertTrue(i.incluye(s));
	}

	@Test
	public void doblesRequierePareja() {
		Torneo t = Fixture.recreativoTenis(8, Torneo.DOBLES);
		Socio a = intermedio(1);
		Socio b = intermedio(2);
		ArrayList<Socio> todos = Fixture.lista(a, b);
		assertThrows(IllegalArgumentException.class,
				() -> t.inscribirParticipante(a, null, null, todos, sinTurnos, Fixture.ANTES_DEL_LIMITE));
		InscripcionTorneo i = t.inscribirParticipante(a, b, null, todos, sinTurnos, Fixture.ANTES_DEL_LIMITE);
		assertEquals(1, t.getInscripciones().size()); // un solo cupo
		assertTrue(i.incluye(a) && i.incluye(b));
	}

	@Test
	public void laParejaDebeCumplirTambienLosRequisitos() {
		Torneo t = Fixture.recreativoTenis(8, Torneo.DOBLES);
		Socio a = intermedio(1);
		Socio principiante = Fixture.socioEnDisciplina(2, "TENIS", 1000);
		assertThrows(IllegalStateException.class, () -> t.inscribirParticipante(a, principiante, null,
				Fixture.lista(a, principiante), sinTurnos, Fixture.ANTES_DEL_LIMITE));
		assertThrows(IllegalArgumentException.class, () -> t.inscribirParticipante(a, a, null, Fixture.lista(a),
				sinTurnos, Fixture.ANTES_DEL_LIMITE));
	}

	@Test
	public void sencillosNoAdmiteCompanero() {
		Torneo t = Fixture.recreativoTenis(8, Torneo.SENCILLOS);
		Socio a = intermedio(1);
		Socio b = intermedio(2);
		assertThrows(IllegalArgumentException.class, () -> t.inscribirParticipante(a, b, null, Fixture.lista(a, b),
				sinTurnos, Fixture.ANTES_DEL_LIMITE));
	}

	@Test
	public void nivelDistintoSeRechazaIndicandoElNivelRequerido() {
		Torneo t = Fixture.recreativoTenis(8, Torneo.SENCILLOS);
		Socio principiante = Fixture.socioEnDisciplina(1, "TENIS", 1000);
		IllegalStateException e = assertThrows(IllegalStateException.class,
				() -> Fixture.inscribir(t, principiante, Fixture.lista(principiante), Fixture.ANTES_DEL_LIMITE));
		assertTrue(e.getMessage().contains("INTERMEDIO"));
	}

	@Test
	public void subirDeEloCambiaElNivelConElQueSePuedeInscribir() {
		Torneo t = Fixture.recreativoTenis(8, Torneo.SENCILLOS);
		Socio s = Fixture.socioEnDisciplina(1, "TENIS", 1000);
		s.buscarInscripcion("TENIS").restaurar(1250, new ArrayList<Integer>(java.util.Arrays.asList(1250)));
		assertEquals(InscripcionTorneo.REGULAR,
				Fixture.inscribir(t, s, Fixture.lista(s), Fixture.ANTES_DEL_LIMITE).getTipoCupo());
	}

	@Test
	public void sinInscripcionEnLaDisciplinaSeRechazaIndicandoQueDebeInscribirseEnLaAcademia() {
		Torneo t = Fixture.recreativoTenis(8, Torneo.SENCILLOS);
		Socio s = Fixture.socio(1);
		IllegalStateException e = assertThrows(IllegalStateException.class,
				() -> Fixture.inscribir(t, s, Fixture.lista(s), Fixture.ANTES_DEL_LIMITE));
		assertTrue(e.getMessage().contains("academia"));
	}

	@Test
	public void yaInscritoDadoDeBajaOTorneoNoAbiertoSeRechaza() {
		Torneo t = Fixture.recreativoTenis(8, Torneo.SENCILLOS);
		Socio s = intermedio(1);
		Socio baja = intermedio(2);
		baja.setActivo(false);
		Fixture.inscribir(t, s, Fixture.lista(s, baja), Fixture.ANTES_DEL_LIMITE);
		assertThrows(IllegalStateException.class,
				() -> Fixture.inscribir(t, s, Fixture.lista(s, baja), Fixture.ANTES_DEL_LIMITE));
		assertThrows(IllegalStateException.class,
				() -> Fixture.inscribir(t, baja, Fixture.lista(s, baja), Fixture.ANTES_DEL_LIMITE));
		Socio otro = intermedio(3);
		Fixture.inscribir(t, otro, Fixture.lista(s, otro), Fixture.ANTES_DEL_LIMITE);
		t.iniciar();
		Socio tarde = intermedio(4);
		assertThrows(IllegalStateException.class,
				() -> Fixture.inscribir(t, tarde, Fixture.lista(s, tarde), Fixture.ANTES_DEL_LIMITE));
	}

	@Test
	public void competitivoRegistraLaTarifaComoPagada() {
		Torneo t = Fixture.competitivoTenis(8, Torneo.SENCILLOS, 30000);
		Socio s = intermedio(1);
		InscripcionTorneo i = Fixture.inscribir(t, s, Fixture.lista(s), Fixture.ANTES_DEL_LIMITE);
		assertEquals(30000, i.getTarifaPagada(), 0.0001);
		assertEquals(30000, t.getTotalRecaudado(), 0.0001);
	}

	@Test
	public void torneoDeConjuntoNoAceptaInscripcionIndividual() {
		Torneo t = Fixture.recreativoFutbol(8);
		Socio s = intermedio(1);
		assertThrows(IllegalArgumentException.class,
				() -> Fixture.inscribir(t, s, Fixture.lista(s), Fixture.ANTES_DEL_LIMITE));
	}
}
