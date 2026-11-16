package Persistencia;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import Deportes.Equipo;
import Deportes.Partido;
import Torneos.BonoDescuento;
import Torneos.CatalogoTorneos;
import Torneos.EncuentroTorneo;
import Torneos.Fixture;
import Torneos.InscripcionTorneo;
import Torneos.Torneo;
import Usuario.Externo;
import Usuario.Socio;

public class PersistenciaTorneosTest {
	
	private Path carpeta;
	private DatosClub datos;

	@BeforeEach
	public void preparar() throws IOException {
		carpeta = Files.createTempDirectory("club_torneos");
		datos = new DatosClub();
	}

	private DatosClub recargar() throws IOException {
		PersistenciaClub.guardar(datos, carpeta.toString());
		return PersistenciaClub.cargar(carpeta.toString());
	}

	private void registrarEquipo(Equipo e) {
		datos.getEquipos().add(e);
		for (int i = 0; i < e.getJugadores().size(); i++) {
			datos.getUsuarios().add(Fixture.integrante(e, i));
		}
	}

	private Torneo inscribirEquipos(Torneo t, Equipo a, Equipo b) {
		for (Equipo e : new Equipo[] { a, b }) {
			t.inscribirEquipo(e, Fixture.integrante(e, 0), new ArrayList<Equipo>(), new ArrayList<Partido>(),
					Fixture.ANTES_DEL_LIMITE);
		}
		return t;
	}

	private Socio tenista(int id) {
		Socio s = Fixture.socioEnDisciplina(id, "TENIS", 1200);
		datos.getUsuarios().add(s);
		return s;
	}

	@Test
	public void unExternoSeGuardaYSeCargaConSusDatos() throws IOException {
		Externo ext = Fixture.externo(500);
		ext.setEstudianteUniversidad(true);
		datos.getUsuarios().add(ext);

		DatosClub cargado = recargar();

		Externo leido = (Externo) cargado.buscarUsuario(500);
		assertEquals("Externo500", leido.getNombre());
		assertTrue(leido.isEstudianteUniversidad());
	}

	@Test
	public void sinArchivosDeTorneosElClubNoTieneTorneos() throws IOException {
		DatosClub cargado = PersistenciaClub.cargar(carpeta.toString());
		assertTrue(cargado.getTorneos().isEmpty());
	}

	@Test
	public void torneoAbiertoDeConjuntoConservaSusInscripciones() throws IOException {
		Equipo a = Fixture.equipoFutbol("Alfa", 7, 100);
		Equipo b = Fixture.equipoFutbol("Beta", 7, 200);
		registrarEquipo(a);
		registrarEquipo(b);
		Torneo t = inscribirEquipos(Fixture.competitivoFutbol(8, 70000), a, b);
		datos.getTorneos().add(t);

		DatosClub cargado = recargar();

		Torneo leido = cargado.getTorneos().get(0);
		assertEquals(CatalogoTorneos.resumen(t), CatalogoTorneos.resumen(leido));
		assertEquals(t.getHistorial(), leido.getHistorial());
		assertEquals("COMPETITIVO", leido.getTipo());
		assertEquals(70000.0, leido.getTarifa());
		assertEquals(2, leido.getInscripciones().size());
		assertSame(cargado.buscarEquipo("Alfa"), leido.getInscripciones().get(0).getEquipo());
	}

	@Test
	public void torneoDeDoblesConservaLaParejaConUnExterno() throws IOException {
		Socio socio = tenista(10);
		Externo ext = Fixture.externo(500);
		datos.getUsuarios().add(ext);
		Torneo t = Fixture.recreativoTenis(4, Torneo.DOBLES);
		t.inscribirParticipante(socio, ext, "INTERMEDIO", Fixture.lista(socio), new ArrayList<Administradores.Turno>(),
				Fixture.ANTES_DEL_LIMITE);
		datos.getTorneos().add(t);

		DatosClub cargado = recargar();

		Torneo leido = cargado.getTorneos().get(0);
		assertEquals(t.getHistorial(), leido.getHistorial());
		InscripcionTorneo insc = leido.getInscripciones().get(0);
		assertEquals(2, insc.getIntegrantes().size());
		assertTrue(insc.incluye((Externo) cargado.buscarUsuario(500)));
	}

	@Test
	public void torneoEnCursoSigueDondeQuedoDespuesDeCargar() throws IOException {
		Socio s1 = tenista(10);
		Socio s2 = tenista(11);
		Socio s3 = tenista(12);
		Torneo t = Fixture.recreativoTenis(8, Torneo.SENCILLOS);
		ArrayList<Socio> socios = Fixture.lista(s1, s2, s3);
		Fixture.inscribir(t, s1, socios, Fixture.ANTES_DEL_LIMITE);
		Fixture.inscribir(t, s2, socios, Fixture.ANTES_DEL_LIMITE);
		Fixture.inscribir(t, s3, socios, Fixture.ANTES_DEL_LIMITE);
		t.iniciar(); // 3 inscritos: el primero pasa libre y se juega 2 vs 3
		t.registrarResultado(t.getEncuentros().get(0), 6, 3);
		datos.getTorneos().add(t);

		DatosClub cargado = recargar();

		Torneo leido = cargado.getTorneos().get(0);
		assertEquals(Torneo.EN_CURSO, leido.getEstado());
		assertEquals(t.getHistorial(), leido.getHistorial());
		assertEquals(t.encuentrosFaltantes(), leido.encuentrosFaltantes());
		// la final se genero al terminar la primera ronda y se puede jugar
		EncuentroTorneo pendiente = null;
		for (EncuentroTorneo e : leido.getEncuentros()) {
			if (!e.isJugado()) {
				pendiente = e;
			}
		}
		assertNotNull(pendiente);
		leido.registrarResultado(pendiente, 6, 4);
		leido.finalizar();
		assertEquals(Torneo.FINALIZADO, leido.getEstado());
		assertNotNull(leido.getGanador());
	}

	@Test
	public void torneoRecreativoFinalizadoConservaPremiosYBonos() throws IOException {
		Equipo a = Fixture.equipoFutbol("Alfa", 7, 100);
		Equipo b = Fixture.equipoFutbol("Beta", 7, 200);
		registrarEquipo(a);
		registrarEquipo(b);
		Torneo t = inscribirEquipos(Fixture.recreativoFutbol(8), a, b);
		t.iniciar();
		t.registrarResultado(t.getEncuentros().get(0), 3, 0);
		t.finalizar();
		datos.getTorneos().add(t);
		Socio ganador = Fixture.integrante(a, 0);
		assertEquals(1, ganador.getBonos().size());
		ganador.getBonos().get(0).usar();

		DatosClub cargado = recargar();

		Torneo leido = cargado.getTorneos().get(0);
		assertEquals(Torneo.FINALIZADO, leido.getEstado());
		assertEquals(t.getHistorial(), leido.getHistorial());
		assertEquals(7, leido.getPremiosEntregados().size());
		Socio ganadorCargado = cargado.buscarSocio(ganador.getId());
		assertEquals(1, ganadorCargado.getBonos().size());
		BonoDescuento bono = ganadorCargado.getBonos().get(0);
		assertEquals(20, bono.getPorcentaje());
		assertTrue(bono.isUsado());
		Socio otro = cargado.buscarSocio(Fixture.integrante(a, 1).getId());
		assertFalse(otro.getBonos().get(0).isUsado());
		assertTrue(cargado.buscarSocio(Fixture.integrante(b, 0).getId()).getBonos().isEmpty());
	}

	@Test
	public void torneoCompetitivoFinalizadoConservaElPremioEnEfectivo() throws IOException {
		Equipo a = Fixture.equipoFutbol("Alfa", 7, 100);
		Equipo b = Fixture.equipoFutbol("Beta", 7, 200);
		registrarEquipo(a);
		registrarEquipo(b);
		Torneo t = inscribirEquipos(Fixture.competitivoFutbol(8, 70000), a, b);
		t.iniciar();
		t.registrarResultado(t.getEncuentros().get(0), 1, 2);
		t.finalizar();
		datos.getTorneos().add(t);

		DatosClub cargado = recargar();

		Torneo leido = cargado.getTorneos().get(0);
		assertEquals(t.getPremiosEntregados(), leido.getPremiosEntregados());
		assertFalse(leido.getPremiosEntregados().isEmpty());
		assertEquals(t.getGanador().getNombre(), leido.getGanador().getNombre());
		assertEquals(t.getTotalRecaudado(), leido.getTotalRecaudado());
	}

	@Test
	public void guardarDosVecesNoDuplicaLosDatos() throws IOException {
		Equipo a = Fixture.equipoFutbol("Alfa", 7, 100);
		Equipo b = Fixture.equipoFutbol("Beta", 7, 200);
		registrarEquipo(a);
		registrarEquipo(b);
		datos.getTorneos().add(inscribirEquipos(Fixture.recreativoFutbol(8), a, b));

		DatosClub primera = recargar();
		PersistenciaClub.guardar(primera, carpeta.toString());
		DatosClub segunda = PersistenciaClub.cargar(carpeta.toString());

		assertEquals(1, segunda.getTorneos().size());
		assertEquals(2, segunda.getTorneos().get(0).getInscripciones().size());
	}

}
