package Deportes;

import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;
import java.util.Date;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import Competencias.NominaCompetencia;
import Competencias.TablaPosiciones;
import Competencias.TorneoCerros;
import Usuario.Participante;
import Usuario.Socio;

public class PartidoCompetenciaTest {
	private Futbol futbol;
	private Equipo equipo;
	private Institucion rival;
	private PartidoCompetencia partido;

	private Equipo crearEquipo(String nombre, DeporteConjunto deporte, String categoria, int numJugadores) {
		Equipo e = new Equipo(nombre, deporte, 20, new Categoria(categoria), new ArrayList<JugadorEquipo>(),
				new ArrayList<Suspension>(), true);
		for (int i = 0; i < numJugadores; i++) {
			Participante s = new Socio(i, "Jugador" + i, "j" + i, "1234", new Date(0), 0, true);
			e.agregarJugador(s, "Jugador", i + 1);
		}
		return e;
	}

	private TorneoCerros crearTorneo(DeporteConjunto deporte, String categoria) {
		return new TorneoCerros("Cerros 2026", "Liga", "2026", deporte, new Categoria(categoria),
				new TablaPosiciones());
	}

	private ArrayList<Participante> primeros(Equipo e, int cuantos) {
		ArrayList<Participante> lista = new ArrayList<Participante>();
		for (int i = 0; i < cuantos; i++) {
			lista.add(e.getJugadores().get(i).getParticipante());
		}
		return lista;
	}

	@BeforeEach
	public void setUp() {
		futbol = new Futbol();
		equipo = crearEquipo("A", futbol, Categoria.MAYORES, 8);
		rival = new Institucion("Colegio Rival");
		partido = new PartidoCompetencia(new Date(), futbol, equipo, rival, "Visitante");
	}

	@Test
	public void ambitoEsCompetenciaYTieneUnSoloEquipo() {
		assertEquals(Suspension.COMPETENCIA, partido.getAmbito());
		assertEquals(1, partido.getEquipos().size());
		assertSame(equipo, partido.getEquipos().get(0));
		assertEquals("Colegio Rival", partido.getInstitucion().getNombre());
		assertEquals("Visitante", partido.getEscenarioJugador());
	}

	@Test
	public void sinCompetenciaSeValidaYCierra() {
		partido.validar();
		partido.cerrar();
		assertTrue(partido.isJugado());
	}

	@Test
	public void asignarCompetenciaRegistraElPartido() {
		TorneoCerros torneo = crearTorneo(futbol, Categoria.MAYORES);
		partido.setCompetencia(torneo);
		partido.setCompetencia(torneo);
		assertEquals(1, torneo.getPartidos().size());
	}

	@Test
	public void validarFallaSiELEquipoNoEstaInscrito() {
		partido.setCompetencia(crearTorneo(futbol, Categoria.MAYORES));
		assertThrows(IllegalStateException.class, () -> partido.validar());
	}

	@Test
	public void validarFallaParaOtroDeporte() {
		partido.setCompetencia(crearTorneo(new Baloncesto(), Categoria.MAYORES));
		assertThrows(IllegalStateException.class, () -> partido.validar());
	}

	@Test
	public void validarFallaSiNoLaCategoriaNoCoincide() {
		partido.setCompetencia(crearTorneo(futbol, Categoria.SUB17));
		assertThrows(IllegalStateException.class, () -> partido.validar());
	}

	@Test
	public void validarFallaSiUnJugadorNoEstaEnLaNomina() {
		TorneoCerros torneo = crearTorneo(futbol, Categoria.MAYORES);
		torneo.inscribir(new NominaCompetencia(equipo, torneo, primeros(equipo, 7)));
		partido.setCompetencia(torneo);
		Participante fueraDeNomina = equipo.getJugadores().get(7).getParticipante();
		partido.agregarEstadisistica(new EstadisticaFutbol(fueraDeNomina));

		assertThrows(IllegalStateException.class, () -> partido.validar());
	}
	
	@Test
	public void victoriaSuma3PuntosEnLaTabla() {
		TorneoCerros torneo = crearTorneo(futbol, Categoria.MAYORES);
		torneo.inscribir(new NominaCompetencia(equipo, torneo, primeros(equipo, 8)));
		partido.setCompetencia(torneo);

		partido.registrarResultadi(2, 1, new ArrayList<EstadisticaJugadorPartido>());

		assertTrue(partido.isJugado());
		assertEquals(3, torneo.getTabla().getPuntos(equipo));
	}

	@Test
	public void empateSma1PuntoYderroteNosuma() {
		TorneoCerros torneo = crearTorneo(futbol, Categoria.MAYORES);
		torneo.inscribir(new NominaCompetencia(equipo, torneo, primeros(equipo, 8)));
		PartidoCompetencia empate = new PartidoCompetencia(new Date(), futbol, equipo, rival, "Local");
		empate.setCompetencia(torneo);
		empate.registrarResultadi(1, 1, new ArrayList<EstadisticaJugadorPartido>());
		assertEquals(1, torneo.getTabla().getPuntos(equipo));

		PartidoCompetencia derrota = new PartidoCompetencia(new Date(), futbol, equipo, rival, "Local");
		derrota.setCompetencia(torneo);
		derrota.registrarResultadi(0, 2, new ArrayList<EstadisticaJugadorPartido>());
		assertEquals(1, torneo.getTabla().getPuntos(equipo));
	}

	@Test
	public void tarjetaRojaEnCompetenciaNoSuspenedeEnPartidosInternos() {
		Participante s = equipo.getJugadores().get(0).getParticipante();
		EstadisticaFutbol est = new EstadisticaFutbol(s);
		est.setTarjetasRojas(1);
		partido.agregarEstadisistica(est);

		partido.cerrar();

		assertTrue(equipo.estaSuspendido(s, Suspension.COMPETENCIA));
		assertFalse(equipo.estaSuspendido(s, Suspension.INTERNO));
	}

}
