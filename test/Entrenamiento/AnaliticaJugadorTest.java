package Entrenamiento;

import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;
import java.util.Date;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import Deportes.CanchaTenis;
import Deportes.Categoria;
import Deportes.Equipo;
import Deportes.EstadisticaFutbol;
import Deportes.EstadisticasTenisYPadel;
import Deportes.Futbol;
import Deportes.JugadorEquipo;
import Deportes.Partido;
import Deportes.PartidoInterno;
import Deportes.PartidoPractica;
import Deportes.Suspension;
import Usuario.Entrenador;
import Usuario.Socio;

public class AnaliticaJugadorTest {
	private static final long DIA = 86400000L;

	private Socio socio;
	private Socio rival;
	private Entrenador entrenador;
	private Date desde;
	private Date hasta;
	private Futbol futbol;

	private Date dia(int n) {
		return new Date(n * DIA);
	}

	private SesionEntrenamiento sesion(int dia) {
		return new SesionEntrenamiento(dia(dia), 8, 1, entrenador, "Cancha 1");
	}

	private PartidoInterno partidoFutbol(int dia, Socio jugador, int goles, boolean jugado) {
		Equipo e1 = new Equipo("A", futbol, 10, new Categoria(Categoria.MAYORES), new ArrayList<JugadorEquipo>(),
				new ArrayList<Suspension>(), true);
		Equipo e2 = new Equipo("B", futbol, 10, new Categoria(Categoria.MAYORES), new ArrayList<JugadorEquipo>(),
				new ArrayList<Suspension>(), true);
		PartidoInterno partido = new PartidoInterno(dia(dia), futbol, e1, e2);
		EstadisticaFutbol est = new EstadisticaFutbol(jugador);
		est.setGoles(goles);
		partido.getEstadisticas().add(est);
		partido.setJugado(jugado);
		return partido;
	}

	private PartidoPractica partidoTenis(int dia, int setsSocio, boolean finalizado) {
		ArrayList<Socio> jugadores = new ArrayList<Socio>();
		jugadores.add(socio);
		jugadores.add(rival);
		PartidoPractica partido = new PartidoPractica(dia(dia), "TENIS", new CanchaTenis("Tenis 1"), jugadores, false);
		EstadisticasTenisYPadel est = new EstadisticasTenisYPadel();
		est.setSetsGanados(setsSocio);
		partido.registrarEstadisticas(socio, est);
		partido.setFinalizado(finalizado);
		return partido;
	}

	@BeforeEach
	public void setUp() {
		socio = new Socio(1, "Prueba", "prueba", "1234", new Date(0), 0, true);
		rival = new Socio(2, "Rival", "rival", "1234", new Date(0), 0, true);
		socio.inscribirse("TENIS");
		rival.inscribirse("TENIS");
		entrenador = new Entrenador(10, "Coach", "coach", "1234", new Date(0), new Date(0));
		futbol = new Futbol();
		desde = dia(10);
		hasta = dia(20);
	}


	@Test
	public void socioNuloExcepcion() {
		assertThrows(IllegalArgumentException.class, () -> new AnaliticaJugador(null, desde, hasta));
	}

	@Test
	public void fechasNulasExcepcion() {
		assertThrows(IllegalArgumentException.class, () -> new AnaliticaJugador(socio, null, hasta));
		assertThrows(IllegalArgumentException.class, () -> new AnaliticaJugador(socio, desde, null));
	}

	@Test
	public void rangoInvertidoExcepcion() {
		assertThrows(IllegalArgumentException.class, () -> new AnaliticaJugador(socio, hasta, desde));
	}

	@Test
	public void rangoDeUnSoloDiaEsValido() {
		AnaliticaJugador an = new AnaliticaJugador(socio, desde, desde);
		assertSame(socio, an.getSocio());
		assertSame(desde, an.getDesde());
		assertSame(desde, an.getHasta());
	}

	@Test
	public void analiticaNuevaTodoEnCero() {
		AnaliticaJugador an = new AnaliticaJugador(socio, desde, hasta);
		assertAll(() -> assertEquals(0, an.getSesionesConvocado()), () -> assertEquals(0, an.getAsistencias()),
				() -> assertEquals(0, an.getFaltas()), () -> assertEquals(0, an.getPartidosConjunto()),
				() -> assertEquals(0, an.getPartidosPractica()),
				() -> assertEquals(0, an.calcularPorcentajeAsistencia(), 0.0001),
				() -> assertEquals(0, an.calcularPromedioEvaluaciones(), 0.0001),
				() -> assertEquals(0, an.calcularPuntajePromedioConjunto(), 0.0001));
	}


	@Test
	public void sesionesCuentanAsistenciasFaltasYTardes() {
		SesionEntrenamiento s1 = sesion(11);
		SesionEntrenamiento s2 = sesion(12);
		SesionEntrenamiento s3 = sesion(13);
		for (SesionEntrenamiento s : new SesionEntrenamiento[] { s1, s2, s3 }) {
			s.convocarJugador(socio);
			s.finalizarSesion();
		}
		s1.registrarAsistencia(socio, RegistroAsistencia.ASISTIO, 4.0, null);
		s2.registrarAsistencia(socio, RegistroAsistencia.LLEGO_TARDE, 3.0, null);
		s3.registrarAsistencia(socio, RegistroAsistencia.FALTO, null, null);
		ArrayList<SesionEntrenamiento> sesiones = new ArrayList<SesionEntrenamiento>();
		sesiones.add(s1);
		sesiones.add(s2);
		sesiones.add(s3);

		AnaliticaJugador an = new AnaliticaJugador(socio, desde, hasta);
		an.procesarSesiones(sesiones);

		assertEquals(3, an.getSesionesConvocado());
		assertEquals(2, an.getAsistencias());
		assertEquals(1, an.getLlegadasTarde());
		assertEquals(1, an.getFaltas());
		assertEquals(0, an.getSesionesSinRegistro());
		assertEquals(2, an.getCantidadEvaluaciones());
	}

	@Test
	public void sesionSinRegistroSeCuentaAparte() {
		SesionEntrenamiento s = sesion(11);
		s.convocarJugador(socio);
		ArrayList<SesionEntrenamiento> sesiones = new ArrayList<SesionEntrenamiento>();
		sesiones.add(s);

		AnaliticaJugador an = new AnaliticaJugador(socio, desde, hasta);
		an.procesarSesiones(sesiones);

		assertEquals(1, an.getSesionesConvocado());
		assertEquals(1, an.getSesionesSinRegistro());
		assertEquals(0, an.getAsistencias());
		assertEquals(0, an.getFaltas());
	}

	@Test
	public void sesionesFueraDeRangoOSinConvocarseSeIgnoran() {
		SesionEntrenamiento antes = sesion(9);
		SesionEntrenamiento despues = sesion(21);
		SesionEntrenamiento noConvocado = sesion(15);
		antes.convocarJugador(socio);
		despues.convocarJugador(socio);
		noConvocado.convocarJugador(rival);
		ArrayList<SesionEntrenamiento> sesiones = new ArrayList<SesionEntrenamiento>();
		sesiones.add(antes);
		sesiones.add(despues);
		sesiones.add(noConvocado);

		AnaliticaJugador an = new AnaliticaJugador(socio, desde, hasta);
		an.procesarSesiones(sesiones);

		assertEquals(0, an.getSesionesConvocado());
	}

	@Test
	public void sesionesEnLosExtremosDelRangoSeIncluyen() {
		SesionEntrenamiento enDesde = sesion(10);
		SesionEntrenamiento enHasta = sesion(20);
		enDesde.convocarJugador(socio);
		enHasta.convocarJugador(socio);
		ArrayList<SesionEntrenamiento> sesiones = new ArrayList<SesionEntrenamiento>();
		sesiones.add(enDesde);
		sesiones.add(enHasta);

		AnaliticaJugador an = new AnaliticaJugador(socio, desde, hasta);
		an.procesarSesiones(sesiones);

		assertEquals(2, an.getSesionesConvocado());
	}

	@Test
	public void sesionSinFechaSeIgnora() {
		SesionEntrenamiento s = new SesionEntrenamiento(null, 8, 1, entrenador, "Cancha 1");
		s.convocarJugador(socio);
		ArrayList<SesionEntrenamiento> sesiones = new ArrayList<SesionEntrenamiento>();
		sesiones.add(s);

		AnaliticaJugador an = new AnaliticaJugador(socio, desde, hasta);
		an.procesarSesiones(sesiones);

		assertEquals(0, an.getSesionesConvocado());
	}

	@Test
	public void porcentajeDeAsistenciaNoCuentaSesionesSinRegistro() {
		SesionEntrenamiento s1 = sesion(11);
		SesionEntrenamiento s2 = sesion(12);
		SesionEntrenamiento s3 = sesion(13);
		s1.convocarJugador(socio);
		s2.convocarJugador(socio);
		s3.convocarJugador(socio);
		s1.finalizarSesion();
		s2.finalizarSesion();
		s1.registrarAsistencia(socio, RegistroAsistencia.ASISTIO, null, null);
		s2.registrarAsistencia(socio, RegistroAsistencia.FALTO, null, null);
		ArrayList<SesionEntrenamiento> sesiones = new ArrayList<SesionEntrenamiento>();
		sesiones.add(s1);
		sesiones.add(s2);
		sesiones.add(s3);

		AnaliticaJugador an = new AnaliticaJugador(socio, desde, hasta);
		an.procesarSesiones(sesiones);

		assertEquals(50.0, an.calcularPorcentajeAsistencia(), 0.0001);
	}

	@Test
	public void promedioDeEvaluaciones() {
		SesionEntrenamiento s1 = sesion(11);
		SesionEntrenamiento s2 = sesion(12);
		s1.convocarJugador(socio);
		s2.convocarJugador(socio);
		s1.finalizarSesion();
		s2.finalizarSesion();
		s1.registrarAsistencia(socio, RegistroAsistencia.ASISTIO, 4.0, null);
		s2.registrarAsistencia(socio, RegistroAsistencia.ASISTIO, 5.0, null);
		ArrayList<SesionEntrenamiento> sesiones = new ArrayList<SesionEntrenamiento>();
		sesiones.add(s1);
		sesiones.add(s2);

		AnaliticaJugador an = new AnaliticaJugador(socio, desde, hasta);
		an.procesarSesiones(sesiones);

		assertEquals(4.5, an.calcularPromedioEvaluaciones(), 0.0001);
	}


	@Test
	public void partidosJugadosSumanPartidosYPuntaje() {
		ArrayList<Partido> partidos = new ArrayList<Partido>();
		partidos.add(partidoFutbol(11, socio, 2, true));
		partidos.add(partidoFutbol(12, socio, 1, true));

		AnaliticaJugador an = new AnaliticaJugador(socio, desde, hasta);
		an.procesarPartidos(partidos);

		assertEquals(2, an.getPartidosConjunto());
		assertEquals(3, an.getPuntajeConjunto());
		assertEquals(1.5, an.calcularPuntajePromedioConjunto(), 0.0001);
	}

	@Test
	public void partidoNoJugadoOFueraDeRangoSeIgnora() {
		ArrayList<Partido> partidos = new ArrayList<Partido>();
		partidos.add(partidoFutbol(11, socio, 2, false));
		partidos.add(partidoFutbol(30, socio, 2, true));

		AnaliticaJugador an = new AnaliticaJugador(socio, desde, hasta);
		an.procesarPartidos(partidos);

		assertEquals(0, an.getPartidosConjunto());
		assertEquals(0, an.getPuntajeConjunto());
	}

	@Test
	public void partidoDondeNoJugoElSocioSeIgnora() {
		ArrayList<Partido> partidos = new ArrayList<Partido>();
		partidos.add(partidoFutbol(11, rival, 3, true));

		AnaliticaJugador an = new AnaliticaJugador(socio, desde, hasta);
		an.procesarPartidos(partidos);

		assertEquals(0, an.getPartidosConjunto());
	}


	@Test
	public void partidoPracticaFinalizadoSumaSets() {
		ArrayList<PartidoPractica> partidos = new ArrayList<PartidoPractica>();
		partidos.add(partidoTenis(11, 2, true));
		partidos.add(partidoTenis(12, 1, true));

		AnaliticaJugador an = new AnaliticaJugador(socio, desde, hasta);
		an.procesarPartidosPractica(partidos);

		assertEquals(2, an.getPartidosPractica());
		assertEquals(3, an.getSetsGanados());
	}

	@Test
	public void partidoPracticaNoFinalizadoOFueraDeRangoSeIgnora() {
		ArrayList<PartidoPractica> partidos = new ArrayList<PartidoPractica>();
		partidos.add(partidoTenis(11, 2, false));
		partidos.add(partidoTenis(25, 2, true));

		AnaliticaJugador an = new AnaliticaJugador(socio, desde, hasta);
		an.procesarPartidosPractica(partidos);

		assertEquals(0, an.getPartidosPractica());
		assertEquals(0, an.getSetsGanados());
	}

	@Test
	public void partidoPracticaSinEstadisticasCuentaPartidoPeroNoSets() {
		ArrayList<Socio> jugadores = new ArrayList<Socio>();
		jugadores.add(socio);
		jugadores.add(rival);
		PartidoPractica partido = new PartidoPractica(dia(11), "TENIS", new CanchaTenis("Tenis 1"), jugadores, false);
		partido.setFinalizado(true);
		ArrayList<PartidoPractica> partidos = new ArrayList<PartidoPractica>();
		partidos.add(partido);

		AnaliticaJugador an = new AnaliticaJugador(socio, desde, hasta);
		an.procesarPartidosPractica(partidos);

		assertEquals(1, an.getPartidosPractica());
		assertEquals(0, an.getSetsGanados());
	}


	@Test
	public void calcularProcesaLasTresFuentes() {
		SesionEntrenamiento s = sesion(11);
		s.convocarJugador(socio);
		s.finalizarSesion();
		s.registrarAsistencia(socio, RegistroAsistencia.ASISTIO, 4.0, null);
		ArrayList<SesionEntrenamiento> sesiones = new ArrayList<SesionEntrenamiento>();
		sesiones.add(s);
		ArrayList<Partido> partidos = new ArrayList<Partido>();
		partidos.add(partidoFutbol(12, socio, 2, true));
		ArrayList<PartidoPractica> practicas = new ArrayList<PartidoPractica>();
		practicas.add(partidoTenis(13, 2, true));

		AnaliticaJugador an = AnaliticaJugador.calcular(socio, desde, hasta, sesiones, partidos, practicas);

		assertEquals(1, an.getAsistencias());
		assertEquals(1, an.getPartidosConjunto());
		assertEquals(1, an.getPartidosPractica());
		assertEquals(2, an.getSetsGanados());
	}

	@Test
	public void calcularConRangoInvalidoExcepcion() {
		assertThrows(IllegalArgumentException.class, () -> AnaliticaJugador.calcular(socio, hasta, desde,
				new ArrayList<SesionEntrenamiento>(), new ArrayList<Partido>(), new ArrayList<PartidoPractica>()));
	}

	@Test
	public void resumenContieneNombreYDatosClave() {
		AnaliticaJugador an = new AnaliticaJugador(socio, desde, hasta);
		String r = an.resumen();
		assertTrue(r.startsWith("Prueba"));
		assertTrue(r.contains("0 convocado"));
		assertTrue(r.contains("0 evaluaciones"));
		assertTrue(r.contains("partidos de practica: 0"));
	}

}
