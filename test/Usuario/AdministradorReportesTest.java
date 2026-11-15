package Usuario;

import java.util.ArrayList;
import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import Deportes.CanchaPadel;
import Deportes.CanchaTenis;
import Deportes.Categoria;
import Deportes.DeporteIndividual;
import Deportes.Equipo;
import Deportes.Futbol;
import Deportes.InscripcionIndividual;
import Deportes.Instalacion;
import Deportes.JugadorEquipo;
import Deportes.Partido;
import Deportes.PartidoInterno;
import Deportes.PartidoPractica;
import Deportes.Suspension;
import Deportes.Tenis;
import Entrenamiento.RegistroAsistencia;
import Entrenamiento.SesionEntrenamiento;

import static org.junit.jupiter.api.Assertions.*;

import Tienda.Articulo;
import Tienda.Bebida;
import Tienda.Inventario;
import Tienda.ItemVenta;
import Tienda.Ubicacion;
import Tienda.Venta;
import Tienda.VentaCafeteria;
import Tienda.VentaTienda;

public class AdministradorReportesTest {
	private static final long DIA = 24L * 60 * 60 * 1000;
	private static final long BASE = 1_700_000_000_000L;

	private Administrador admin;
	private Socio ana;
	private Socio luis;
	private Entrenador entrenador;

	private Date dia(int n) {
		return new Date(BASE + n * DIA);
	}

	@BeforeEach
	public void setUp() {
		admin = new Administrador(100, "Admin", "admin", "1234", new Date(0));
		ana = new Socio(1, "Ana", "ana", "1234", new Date(0), 0, true);
		luis = new Socio(2, "Luis", "luis", "1234", new Date(0), 0, true);
		entrenador = new Entrenador(3, "Coach", "coach", "1234", new Date(0), new Date(0));
	}

	// Ventas (RF 38)

	private ArrayList<ItemVenta> items(ItemVenta[] itemVentas) {
	    ArrayList<ItemVenta> lista = new ArrayList<ItemVenta>();
	    for (ItemVenta i : itemVentas) {
	        lista.add(i);
	    }
	    return lista;
	}

	private VentaTienda ventaTienda(Date fecha) {
	    return new VentaTienda(fecha, ana, items(new ItemVenta[] { new ItemVenta(new Articulo("Balon", 100000, "Futbol"), 1) }), 0);
	}

	private VentaCafeteria ventaCafeteria(Date fecha) {
	    return new VentaCafeteria(fecha, luis, items(new ItemVenta[] { new ItemVenta(new Bebida("Cafe", 3000, Bebida.CALIENTE), 2) }), 0);
	}

	@Test
	public void reporteVentasSeparaLosRubrosTiendaYCafeteria() {
		VentaTienda vt = ventaTienda(dia(5));
		VentaCafeteria vc = ventaCafeteria(dia(6));
		ArrayList<Venta> ventas = new ArrayList<Venta>();
		ventas.add(vt);
		ventas.add(vc);
		String r = admin.reporteVentas(ventas, dia(0), dia(10));
		assertTrue(r.contains("Tienda: 1 ventas, total $" + vt.calcularTotal()));
		assertTrue(r.contains("total $" + vc.calcularTotal()));
		assertTrue(r.contains("Total general: $" + (vt.calcularTotal() + vc.calcularTotal())));
	}

	@Test
	public void reporteVentasIgnoraLasVentasFueraDelRango() {
		ArrayList<Venta> ventas = new ArrayList<Venta>();
		ventas.add(ventaTienda(dia(-5)));
		ventas.add(ventaCafeteria(dia(20)));
		String r = admin.reporteVentas(ventas, dia(0), dia(10));
		assertTrue(r.contains("Tienda: 0 ventas, total $0.0"));
		assertTrue(r.contains("Total general: $0.0"));
	}

	@Test
	public void reporteVentasIncluyeLosExtremosDelRango() {
		ArrayList<Venta> ventas = new ArrayList<Venta>();
		ventas.add(ventaTienda(dia(0)));
		ventas.add(ventaTienda(dia(10)));
		String r = admin.reporteVentas(ventas, dia(0), dia(10));
		assertTrue(r.contains("Tienda: 2 ventas"));
	}

	@Test
	public void reporteVentasSinVentasMuestraTotalesEnCero() {
		String r = admin.reporteVentas(new ArrayList<Venta>(), dia(0), dia(10));
		assertTrue(r.contains("Tienda: 0 ventas"));
		assertTrue(r.contains("Total general: $0.0"));
	}

	@Test
	public void reporteVentasDiarioSoloIncluyeElUltimoDia() {
		ArrayList<Venta> ventas = new ArrayList<Venta>();
		ventas.add(ventaTienda(dia(10)));
		ventas.add(ventaTienda(dia(8)));
		String r = admin.reporteVentasPorPeriodo(ventas, dia(10), Administrador.DIARIO);
		assertTrue(r.contains("Tienda: 1 ventas"));
	}

	@Test
	public void reporteVentasSemanalIncluyeVentasDeLosUltimosSieteDias() {
		ArrayList<Venta> ventas = new ArrayList<Venta>();
		ventas.add(ventaTienda(dia(10)));
		ventas.add(ventaTienda(dia(5)));
		ventas.add(ventaTienda(dia(0)));
		String r = admin.reporteVentasPorPeriodo(ventas, dia(10), Administrador.SEMANAL);
		assertTrue(r.contains("Tienda: 2 ventas"));
	}

	@Test
	public void reporteVentasMensualIncluyeVentasDeLosUltimosTreintaDias() {
		ArrayList<Venta> ventas = new ArrayList<Venta>();
		ventas.add(ventaTienda(dia(40)));
		ventas.add(ventaTienda(dia(25)));
		ventas.add(ventaTienda(dia(0)));
		String r = admin.reporteVentasPorPeriodo(ventas, dia(40), Administrador.MENSUAL);
		assertTrue(r.contains("Tienda: 2 ventas"));
	}

	@Test
	public void reporteVentasPorPeriodoInvalidoLanzaExcepcion() {
		assertThrows(IllegalArgumentException.class,
				() -> admin.reporteVentasPorPeriodo(new ArrayList<Venta>(), dia(10), "anual"));
	}

	//Inventario e instalaciones (RF 35)

	@Test
	public void reporteEstadoInventarioListaLasUbicacionesYLasCantidades() {
		Inventario inv = new Inventario();
		Articulo balon = new Articulo("Balon", 50000, "Futbol");
		Articulo raqueta = new Articulo("Raqueta", 200000, "Tenis");
		inv.registrarExistencia(balon, new Ubicacion("Bodega"), 10);
		inv.registrarExistencia(raqueta, new Ubicacion("Bodega"), 4);
		inv.registrarExistencia(balon, new Ubicacion("Tienda"), 3);
		String r = admin.reporteEstadoInventario(inv);
		assertTrue(r.contains("Bodega"));
		assertTrue(r.contains("Tienda"));
		assertTrue(r.contains("Balon | cantidad: 10"));
		assertTrue(r.contains("Raqueta | cantidad: 4"));
		assertTrue(r.contains("Balon | cantidad: 3"));
	}

	@Test
	public void reporteEstadoInventarioVacioSoloTieneElTitulo() {
		String r = admin.reporteEstadoInventario(new Inventario());
		assertTrue(r.contains("ESTADO DEL INVENTARIO"));
		assertFalse(r.contains("cantidad"));
	}

	private ArrayList<Instalacion> instalaciones(Instalacion... is) {
		ArrayList<Instalacion> lista = new ArrayList<Instalacion>();
		for (Instalacion i : is) {
			lista.add(i);
		}
		return lista;
	}

	@Test
	public void reporteEstadoInstalacionesIndicaCuallesEstanOcupadasYLibres() {
		CanchaTenis tenis = new CanchaTenis("Tenis 1");
		CanchaPadel padel = new CanchaPadel("Padel 1");
		ArrayList<Instalacion> todas = instalaciones(tenis, padel);
		ana.inscribirse("TENIS");
		ana.reservar(todas, "TENIS", dia(1), dia(1), "SINGLES", 2);
		String r = admin.reporteEstadoInstalaciones(todas, dia(1), dia(1));
		assertTrue(r.contains("Tenis 1 | TENIS | capacidad 2-4 | reservas: 1 | OCUPADA"));
		assertTrue(r.contains("Padel 1 | PADEL | capacidad 4-4 | reservas: 0 | LIBRE"));
	}

	@Test
	public void reporteEstadoInstalacionesEnOtroBloqueMarcaTodoLibre() {
		CanchaTenis tenis = new CanchaTenis("Tenis 1");
		ArrayList<Instalacion> todas = instalaciones(tenis);
		ana.inscribirse("TENIS");
		ana.reservar(todas, "TENIS", dia(1), dia(1), "SINGLES", 2);
		String r = admin.reporteEstadoInstalaciones(todas, dia(2), dia(2));
		assertTrue(r.contains("LIBRE"));
		assertFalse(r.contains("OCUPADA"));
	}

	@Test
	public void historialReservasSeOrdenaDeLaMasAntiguaALaMasReciente() {
		CanchaTenis tenis = new CanchaTenis("Tenis 1");
		ArrayList<Instalacion> todas = instalaciones(tenis);
		ana.inscribirse("TENIS");
		luis.inscribirse("TENIS");
		ana.reservar(todas, "TENIS", dia(5), dia(5), "SINGLES", 2);
		luis.reservar(todas, "TENIS", dia(2), dia(2), "SINGLES", 2);
		String r = admin.historialReservas(todas);
		assertTrue(r.indexOf("Luis") >= 0 && r.indexOf("Ana") >= 0);
		assertTrue(r.indexOf("Luis") < r.indexOf("Ana"));
	}

	@Test
	public void historialReservasReunePorTodasLasInstalaciones() {
		CanchaTenis tenis = new CanchaTenis("Tenis 1");
		CanchaPadel padel = new CanchaPadel("Padel 1");
		ArrayList<Instalacion> todas = instalaciones(tenis, padel);
		ana.inscribirse("TENIS");
		ana.inscribirse("PADEL");
		ana.reservar(todas, "TENIS", dia(1), dia(1), "SINGLES", 2);
		ana.reservar(todas, "PADEL", dia(2), dia(2), "DOBLES", 4);
		String r = admin.historialReservas(todas);
		assertTrue(r.contains("Tenis 1"));
		assertTrue(r.contains("Padel 1"));
	}

	@Test
	public void historialReservasSinReservasSoloTieneElTitulo() {
		String r = admin.historialReservas(instalaciones(new CanchaTenis("Tenis 1")));
		assertTrue(r.contains("HISTORIAL DE RESERVAS"));
		assertFalse(r.contains("Tenis 1"));
	}

	// Partidos (RF 35, 38)

	private Equipo equipo(String nombre) {
		return new Equipo(nombre, new Futbol(), 11, new Categoria(Categoria.MAYORES), new ArrayList<JugadorEquipo>(),
				new ArrayList<Suspension>(), true);
	}

	private PartidoInterno partidoJugado(String a, String b, int m1, int m2) {
		PartidoInterno p = new PartidoInterno(dia(1), new Futbol(), equipo(a), equipo(b));
		p.registrarMarcador(m1, m2);
		p.cerrar();
		return p;
	}

	@Test
	public void historialPartidosIncluyeJugadosYProgramados() {
		ArrayList<Partido> partidos = new ArrayList<Partido>();
		partidos.add(partidoJugado("Leones", "Tigres", 2, 1));
		partidos.add(new PartidoInterno(dia(3), new Futbol(), equipo("Osos"), equipo("Lobos")));
		String r = admin.historialPartidos(partidos, new ArrayList<PartidoPractica>());
		assertTrue(r.contains("Leones vs Tigres | 2 - 1"));
		assertTrue(r.contains("Osos vs Lobos | programado"));
	}

	@Test
	public void historialPartidosIncluyeLosPartidosDePractica() {
		CanchaTenis tenis = new CanchaTenis("Tenis 1");
		ana.inscribirse("TENIS");
		luis.inscribirse("TENIS");
		ArrayList<Socio> jugadores = new ArrayList<Socio>();
		jugadores.add(ana);
		jugadores.add(luis);
		ArrayList<PartidoPractica> practicas = new ArrayList<PartidoPractica>();
		practicas.add(ana.programarPartido(dia(2), "TENIS", tenis, jugadores));
		String r = admin.historialPartidos(new ArrayList<Partido>(), practicas);
		assertTrue(r.contains("TENIS | PRACTICA | Tenis 1 | Ana, Luis | programado"));
	}

	@Test
	public void reporteResultadosPartidosSoloIncluyeLosJugados() {
		ArrayList<Partido> partidos = new ArrayList<Partido>();
		partidos.add(partidoJugado("Leones", "Tigres", 2, 1));
		partidos.add(new PartidoInterno(dia(3), new Futbol(), equipo("Osos"), equipo("Lobos")));
		String r = admin.reporteResultadosPartidos(partidos);
		assertTrue(r.contains("Leones 2 - 1 Tigres"));
		assertFalse(r.contains("Osos"));
		assertFalse(r.contains("Lobos"));
	}

	@Test
	public void reporteEvolucionRankingFiltraPorDisciplinaYMuestraElHistorial() {
		InscripcionIndividual tenisAna = ana.inscribirse("TENIS");
		tenisAna.actualizarRanking(1, 1000);
		luis.inscribirse("PADEL");
		ArrayList<InscripcionIndividual> inscripciones = new ArrayList<InscripcionIndividual>();
		inscripciones.add(tenisAna);
		inscripciones.add(luis.buscarInscripcion("PADEL"));
		String r = admin.reporteEvolucionRanking(inscripciones, new Tenis(DeporteIndividual.PRINCIPIANTE));
		assertTrue(r.contains("Ana"));
		assertTrue(r.contains("actual: " + tenisAna.getRankingELO()));
		assertTrue(r.contains("1000 -> " + tenisAna.getRankingELO()));
		assertFalse(r.contains("Luis"));
	}

	@Test
	public void reporteEvolucionRankingSinInscritosEnLaDisciplinaSoloTieneElTitulo() {
		ArrayList<InscripcionIndividual> inscripciones = new ArrayList<InscripcionIndividual>();
		inscripciones.add(luis.inscribirse("PADEL"));
		String r = admin.reporteEvolucionRanking(inscripciones, new Tenis(DeporteIndividual.PRINCIPIANTE));
		assertFalse(r.contains("Luis"));
		assertTrue(r.contains("TENIS"));
	}

	// Entrenamientos (RF 35, 38, 24)

	private SesionEntrenamiento sesion(int dia, Socio... convocados) {
		SesionEntrenamiento s = new SesionEntrenamiento(dia(dia), 8, 1.5, entrenador, "Tenis 1");
		for (Socio c : convocados) {
			s.convocarJugador(c);
		}
		return s;
	}

	@Test
	public void reporteAsistenciaResumeAsistenciaFaltasYSinRegistrar() {
		SesionEntrenamiento s1 = sesion(2, ana, luis);
		SesionEntrenamiento s2 = sesion(4, ana, luis);
		s1.finalizarSesion();
		s2.finalizarSesion();
		s1.registrarAsistencia(ana, RegistroAsistencia.ASISTIO, 4.0, null);
		s1.registrarAsistencia(luis, RegistroAsistencia.FALTO, null, null);
		s2.registrarAsistencia(ana, RegistroAsistencia.LLEGO_TARDE, 5.0, "ok");
		ArrayList<SesionEntrenamiento> sesiones = new ArrayList<SesionEntrenamiento>();
		sesiones.add(s1);
		sesiones.add(s2);
		String r = admin.reporteAsistenciaEntrenamientos(sesiones, dia(0), dia(10));
		assertTrue(r.contains("Sesiones finalizadas en el periodo: 2"));
		assertTrue(r.contains("Ana | "));
		assertTrue(r.contains("Luis | "));
		assertTrue(r.contains("sin registrar: 1"));
	}

	@Test
	public void reporteAsistenciaIgnoraSesionesNoFinalizadasYFueraDeRango() {
		SesionEntrenamiento abierta = sesion(2, ana);
		SesionEntrenamiento fuera = sesion(30, ana);
		fuera.finalizarSesion();
		ArrayList<SesionEntrenamiento> sesiones = new ArrayList<SesionEntrenamiento>();
		sesiones.add(abierta);
		sesiones.add(fuera);
		String r = admin.reporteAsistenciaEntrenamientos(sesiones, dia(0), dia(10));
		assertTrue(r.contains("Sesiones finalizadas en el periodo: 0"));
		assertFalse(r.contains("Ana | "));
	}

	@Test
	public void historialSesionesDiferenciaAbiertasDeFinalizadasYIndividualesDeGrupales() {
		SesionEntrenamiento individual = sesion(1, ana);
		SesionEntrenamiento grupal = sesion(2, ana, luis);
		grupal.finalizarSesion();
		grupal.registrarAsistencia(ana, RegistroAsistencia.ASISTIO, null, null);
		ArrayList<SesionEntrenamiento> sesiones = new ArrayList<SesionEntrenamiento>();
		sesiones.add(individual);
		sesiones.add(grupal);
		String r = admin.historialSesiones(sesiones);
		assertTrue(r.contains("individual"));
		assertTrue(r.contains("grupal"));
		assertTrue(r.contains("abierta"));
		assertTrue(r.contains("finalizada, asistentes: 1"));
	}

	@Test
	public void reporteDesempenoJugadorResumeLaAnaliticaDelSocio() {
		SesionEntrenamiento s = sesion(2, ana);
		s.finalizarSesion();
		s.registrarAsistencia(ana, RegistroAsistencia.ASISTIO, 4.0, null);
		ArrayList<SesionEntrenamiento> sesiones = new ArrayList<SesionEntrenamiento>();
		sesiones.add(s);
		String r = admin.reporteDesempenoJugador(ana, dia(0), dia(10), sesiones, new ArrayList<Partido>(),
				new ArrayList<PartidoPractica>());
		assertTrue(r.contains("Ana | sesiones: 1 convocado, 1 asistio"));
	}

	@Test
	public void reporteDesempenoJugadorConRangoInvertidoLanzaExcepcion() {
		assertThrows(IllegalArgumentException.class, () -> admin.reporteDesempenoJugador(ana, dia(10), dia(0),
				new ArrayList<SesionEntrenamiento>(), new ArrayList<Partido>(), new ArrayList<PartidoPractica>()));
	}

	@Test
	public void reporteDesempenoSociosExcluyeALosSociosDadosDeBaja() {
		luis.setActivo(false);
		ArrayList<Socio> socios = new ArrayList<Socio>();
		socios.add(ana);
		socios.add(luis);
		String r = admin.reporteDesempenoSocios(socios, dia(0), dia(10), new ArrayList<SesionEntrenamiento>(),
				new ArrayList<Partido>(), new ArrayList<PartidoPractica>());
		assertTrue(r.contains("Ana | "));
		assertFalse(r.contains("Luis | "));
	}

}
