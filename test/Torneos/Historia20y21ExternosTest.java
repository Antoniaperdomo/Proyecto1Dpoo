package Torneos;

import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import Deportes.DeporteIndividual;
import Usuario.Administrador;
import Usuario.Externo;
import Usuario.Socio;
import Usuario.Usuario;

// HU20 y HU21 Administrador: registrar participantes externos e inscribirlos.
public class Historia20y21ExternosTest {
	private Administrador admin;
	private ArrayList<Usuario> usuarios;
	private ArrayList<Socio> socios;

	@BeforeEach
	public void setUp() {
		admin = Fixture.admin();
		usuarios = new ArrayList<Usuario>();
		socios = new ArrayList<Socio>();
	}

	private Externo registrar(int id) {
		return admin.registrarExterno(usuarios, id, "Externo" + id, "externo" + id, "1234",
				Fixture.fecha("1995-05-05"), id % 2 == 0);
	}

	// HU20

	@Test
	public void datosValidosCreanElExternoConSusCredenciales() {
		Externo e = registrar(10);
		assertEquals("externo10", e.getLogin());
		assertTrue(usuarios.contains(e));
		assertTrue(registrar(12).isEstudianteUniversidad());
	}

	@Test
	public void loginOIdentificadorRepetidoSeRechazaIndicandoCual() {
		registrar(10);
		IllegalArgumentException porId = assertThrows(IllegalArgumentException.class,
				() -> admin.registrarExterno(usuarios, 10, "Otro", "otro", "1234", Fixture.fecha("1995-05-05"), false));
		assertTrue(porId.getMessage().contains("identificador"));
		IllegalArgumentException porLogin = assertThrows(IllegalArgumentException.class, () -> admin
				.registrarExterno(usuarios, 11, "Otro", "externo10", "1234", Fixture.fecha("1995-05-05"), false));
		assertTrue(porLogin.getMessage().contains("login"));
		assertEquals(1, usuarios.size());
	}

	@Test
	public void datoObligatorioFaltanteOFechaFuturaSeRechaza() {
		assertThrows(IllegalArgumentException.class,
				() -> admin.registrarExterno(usuarios, 1, "", "x", "1234", Fixture.fecha("1995-05-05"), false));
		assertThrows(IllegalArgumentException.class,
				() -> admin.registrarExterno(usuarios, 1, "X", "x", "", Fixture.fecha("1995-05-05"), false));
		assertThrows(IllegalArgumentException.class,
				() -> admin.registrarExterno(usuarios, 1, "X", "x", "1234", null, false));
		assertThrows(IllegalArgumentException.class,
				() -> admin.registrarExterno(usuarios, 1, "X", "x", "1234", Fixture.fecha("2999-01-01"), false));
		assertTrue(usuarios.isEmpty());
	}

	// HU21

	@Test
	public void externoConNivelIgualOcupaCupoRegular() {
		Torneo t = Fixture.recreativoTenis(8, Torneo.SENCILLOS);
		Externo e = registrar(10);
		InscripcionTorneo i = admin.inscribirExterno(t, e, DeporteIndividual.INTERMEDIO, null, usuarios, socios,
				Fixture.ANTES_DEL_LIMITE);
		assertEquals(InscripcionTorneo.REGULAR, i.getTipoCupo());
		assertEquals(1, t.getInscripciones().size());
	}

	@Test
	public void dobleRequiereCompaneroYInscribeLaParejaEnUnCupo() {
		Torneo t = Fixture.recreativoTenis(8, Torneo.DOBLES);
		Externo a = registrar(10);
		Externo b = registrar(11);
		assertThrows(IllegalArgumentException.class, () -> admin.inscribirExterno(t, a, DeporteIndividual.INTERMEDIO,
				null, usuarios, socios, Fixture.ANTES_DEL_LIMITE));
		InscripcionTorneo i = admin.inscribirExterno(t, a, DeporteIndividual.INTERMEDIO, b, usuarios, socios,
				Fixture.ANTES_DEL_LIMITE);
		assertEquals(1, t.getInscripciones().size());
		assertEquals(2, i.getIntegrantes().size());
	}

	@Test
	public void torneoDeConjuntoRechazaAlExterno() {
		Torneo t = Fixture.recreativoFutbol(8);
		Externo e = registrar(10);
		assertThrows(IllegalArgumentException.class, () -> admin.inscribirExterno(t, e, DeporteIndividual.INTERMEDIO,
				null, usuarios, socios, Fixture.ANTES_DEL_LIMITE));
	}

	@Test
	public void soloQuedanPreferencialesAntesDeLaFechaLimiteSeRechaza() {
		Torneo t = Fixture.recreativoTenis(5, Torneo.SENCILLOS); // 4 regulares + 1 preferencial
		for (int id = 10; id < 14; id++) {
			admin.inscribirExterno(t, registrar(id), DeporteIndividual.INTERMEDIO, null, usuarios, socios,
					Fixture.ANTES_DEL_LIMITE);
		}
		Externo quinto = registrar(20);
		IllegalStateException e = assertThrows(IllegalStateException.class, () -> admin.inscribirExterno(t, quinto,
				DeporteIndividual.INTERMEDIO, null, usuarios, socios, Fixture.ANTES_DEL_LIMITE));
		assertTrue(e.getMessage().contains("reservados"));
		// pasada la fecha limite el cupo preferencial libre se vuelve regular
		InscripcionTorneo i = admin.inscribirExterno(t, quinto, DeporteIndividual.INTERMEDIO, null, usuarios, socios,
				Fixture.DESPUES_DEL_LIMITE);
		assertEquals(InscripcionTorneo.REGULAR, i.getTipoCupo());
	}

	@Test
	public void competitivoRegistraLaTarifaComoPagadaYSumaAlRecaudado() {
		Torneo t = Fixture.competitivoTenis(8, Torneo.SENCILLOS, 25000);
		InscripcionTorneo i = admin.inscribirExterno(t, registrar(10), DeporteIndividual.INTERMEDIO, null, usuarios,
				socios, Fixture.ANTES_DEL_LIMITE);
		assertEquals(25000, i.getTarifaPagada(), 0.0001);
		assertEquals(25000, t.getTotalRecaudado(), 0.0001);
	}

	@Test
	public void yaInscritoTorneoNoAbiertoONivelDistintoSeRechaza() {
		Torneo t = Fixture.recreativoTenis(8, Torneo.SENCILLOS);
		Externo e = registrar(10);
		admin.inscribirExterno(t, e, DeporteIndividual.INTERMEDIO, null, usuarios, socios, Fixture.ANTES_DEL_LIMITE);
		assertThrows(IllegalStateException.class, () -> admin.inscribirExterno(t, e, DeporteIndividual.INTERMEDIO, null,
				usuarios, socios, Fixture.ANTES_DEL_LIMITE));
		Externo otro = registrar(11);
		assertThrows(IllegalStateException.class, () -> admin.inscribirExterno(t, otro, DeporteIndividual.AVANZADO,
				null, usuarios, socios, Fixture.ANTES_DEL_LIMITE));
		admin.inscribirExterno(t, otro, DeporteIndividual.INTERMEDIO, null, usuarios, socios,
				Fixture.ANTES_DEL_LIMITE);
		t.iniciar();
		Externo tarde = registrar(12);
		assertThrows(IllegalStateException.class, () -> admin.inscribirExterno(t, tarde,
				DeporteIndividual.INTERMEDIO, null, usuarios, socios, Fixture.ANTES_DEL_LIMITE));
	}

	@Test
	public void externoNoRegistradoSeRechaza() {
		Torneo t = Fixture.recreativoTenis(8, Torneo.SENCILLOS);
		Externo noRegistrado = Fixture.externo(77);
		assertThrows(IllegalArgumentException.class, () -> admin.inscribirExterno(t, noRegistrado,
				DeporteIndividual.INTERMEDIO, null, usuarios, socios, Fixture.ANTES_DEL_LIMITE));
	}

	@Test
	public void retirarAntesDeIniciarLiberaElCupo() {
		Torneo t = Fixture.recreativoTenis(8, Torneo.SENCILLOS);
		Externo e = registrar(10);
		admin.inscribirExterno(t, e, DeporteIndividual.INTERMEDIO, null, usuarios, socios, Fixture.ANTES_DEL_LIMITE);
		admin.retirarExterno(t, e);
		assertEquals(0, t.getInscripciones().size());
		assertEquals(0, t.contarOcupados(InscripcionTorneo.REGULAR));
	}
}
