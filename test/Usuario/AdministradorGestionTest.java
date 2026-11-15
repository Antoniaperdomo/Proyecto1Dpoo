package Usuario;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import Administradores.SolicitudCambioTurno;
import Administradores.SugerenciaProducto;
import Administradores.Turno;
import Deportes.Baloncesto;
import Deportes.Categoria;
import Deportes.Equipo;
import Deportes.Futbol;
import Tienda.Articulo;
import Tienda.ExistenciaArticulo;
import Tienda.Ubicacion;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Date;

public class AdministradorGestionTest {
	private Administrador admin;
	private Entrenador ent1;
	private Entrenador ent2;
	private Entrenador ent3;
	private Fisioterapeuta fisio;
	private ArrayList<Turno> turnos;
	private ArrayList<Usuario> usuarios;
	private ArrayList<Equipo> equipos;
	private ArrayList<ExistenciaArticulo> inventario;
	private Futbol futbol;

	@BeforeEach
	public void setUp() {
		admin = new Administrador(100, "Admin", "admin", "1234", new Date(0));
		ent1 = new Entrenador(1, "Ent1", "ent1", "1234", new Date(0), new Date(0));
		ent2 = new Entrenador(2, "Ent2", "ent2", "1234", new Date(0), new Date(0));
		ent3 = new Entrenador(3, "Ent3", "ent3", "1234", new Date(0), new Date(0));
		fisio = new Fisioterapeuta(4, "Fisio", "fisio", "1234", new Date(0), new Date(0));
		turnos = new ArrayList<Turno>();
		usuarios = new ArrayList<Usuario>();
		equipos = new ArrayList<Equipo>();
		inventario = new ArrayList<ExistenciaArticulo>();
		futbol = new Futbol();
	}

	// Turnos (RF 37)

	@Test
	public void asignarTurnoLoAgregaALaLista() {
		Turno t = admin.asignarTurno(ent1, "LUNES", 8, 12, turnos);
		assertEquals(1, turnos.size());
		assertSame(t, turnos.get(0));
		assertSame(ent1, t.getEmpleado());
	}

	@Test
	public void asignarTurnoQueSeCruzaConOtroDelMismoEmpleadoLanzaExcepcion() {
		admin.asignarTurno(ent1, "LUNES", 8, 12, turnos);
		assertThrows(IllegalArgumentException.class, () -> admin.asignarTurno(ent1, "LUNES", 10, 14, turnos));
		assertEquals(1, turnos.size());
	}

	@Test
	public void empleadosDistintosPuedenTenerElMismoHorario() {
		admin.asignarTurno(ent1, "LUNES", 8, 12, turnos);
		admin.asignarTurno(ent2, "LUNES", 8, 12, turnos);
		assertEquals(2, turnos.size());
	}

	@Test
	public void elMismoEmpleadoPuedeTenerTurnosEnDiasDistintos() {
		admin.asignarTurno(ent1, "LUNES", 8, 12, turnos);
		admin.asignarTurno(ent1, "MARTES", 8, 12, turnos);
		assertEquals(2, turnos.size());
	}

	@Test
	public void asignarTurnoConHorasInvalidasLanzaExcepcion() {
		assertThrows(IllegalArgumentException.class, () -> admin.asignarTurno(ent1, "LUNES", 12, 8, turnos));
		assertEquals(0, turnos.size());
	}

	private Turno cubrirLunes(Empleado e) {
		return admin.asignarTurno(e, "LUNES", 8, 12, turnos);
	}

	@Test
	public void minimoDePersonalSeCumpleConDosEntrenadoresYUnFisioterapeutaAdicionales() {
		Turno actual = cubrirLunes(ent1);
		cubrirLunes(ent2);
		cubrirLunes(ent3);
		cubrirLunes(fisio);
		SolicitudCambioTurno s = new SolicitudCambioTurno(ent1, actual, new Turno(ent1, "MARTES", 8, 12));
		assertTrue(admin.cumpleMinimoPersonal(s, turnos));
	}

	@Test
	public void minimoDePersonalNoSeCumpleConUnSoloEntrenadorAdicional() {
		Turno actual = cubrirLunes(ent1);
		cubrirLunes(ent2);
		cubrirLunes(fisio);
		SolicitudCambioTurno s = new SolicitudCambioTurno(ent1, actual, new Turno(ent1, "MARTES", 8, 12));
		assertFalse(admin.cumpleMinimoPersonal(s, turnos));
	}

	@Test
	public void minimoDePersonalNoSeCumpleSinFisioterapeuta() {
		Turno actual = cubrirLunes(ent1);
		cubrirLunes(ent2);
		cubrirLunes(ent3);
		SolicitudCambioTurno s = new SolicitudCambioTurno(ent1, actual, new Turno(ent1, "MARTES", 8, 12));
		assertFalse(admin.cumpleMinimoPersonal(s, turnos));
	}

	@Test
	public void turnosDeOtroDiaNoCuentanParaElMinimo() {
		Turno actual = cubrirLunes(ent1);
		cubrirLunes(ent2);
		admin.asignarTurno(ent3, "MARTES", 8, 12, turnos);
		cubrirLunes(fisio);
		SolicitudCambioTurno s = new SolicitudCambioTurno(ent1, actual, new Turno(ent1, "MIERCOLES", 8, 12));
		assertFalse(admin.cumpleMinimoPersonal(s, turnos));
	}

	@Test
	public void evaluarSolicitudQueNoViolaElMinimoLaApruebaYReemplazaElTurno() {
		Turno actual = cubrirLunes(ent1);
		cubrirLunes(ent2);
		cubrirLunes(ent3);
		cubrirLunes(fisio);
		Turno propuesto = new Turno(ent1, "MARTES", 8, 12);
		SolicitudCambioTurno s = new SolicitudCambioTurno(ent1, actual, propuesto);
		admin.evaluarSolicitud(s, turnos);
		assertEquals(SolicitudCambioTurno.APROBADA, s.getEstado());
		assertFalse(turnos.contains(actual));
		assertTrue(turnos.contains(propuesto));
	}

	@Test
	public void evaluarSolicitudQueDejaElTurnoDesprotegidoLaRechazaYNoTocaLosTurnos() {
		Turno actual = cubrirLunes(ent1);
		cubrirLunes(ent2);
		cubrirLunes(fisio);
		Turno propuesto = new Turno(ent1, "MARTES", 8, 12);
		SolicitudCambioTurno s = new SolicitudCambioTurno(ent1, actual, propuesto);
		admin.evaluarSolicitud(s, turnos);
		assertEquals(SolicitudCambioTurno.RECHAZADA, s.getEstado());
		assertTrue(turnos.contains(actual));
		assertFalse(turnos.contains(propuesto));
	}

	@Test
	public void evaluarSolicitudCuyoTurnoPropuestoChocaConOtroDelEmpleadoLaRechaza() {
		Turno actual = cubrirLunes(ent1);
		cubrirLunes(ent2);
		cubrirLunes(ent3);
		cubrirLunes(fisio);
		admin.asignarTurno(ent1, "MARTES", 8, 12, turnos);
		SolicitudCambioTurno s = new SolicitudCambioTurno(ent1, actual, new Turno(ent1, "MARTES", 10, 14));
		admin.evaluarSolicitud(s, turnos);
		assertEquals(SolicitudCambioTurno.RECHAZADA, s.getEstado());
		assertTrue(turnos.contains(actual));
	}

	@Test
	public void getSolicitudesPendientesDevuelveSoloLasPendientes() {
		Turno a = new Turno(ent1, "LUNES", 8, 12);
		Turno b = new Turno(ent1, "MARTES", 8, 12);
		SolicitudCambioTurno pendiente = new SolicitudCambioTurno(ent1, a, b);
		SolicitudCambioTurno aprobada = new SolicitudCambioTurno(ent1, a, b);
		SolicitudCambioTurno rechazada = new SolicitudCambioTurno(ent1, a, b);
		aprobada.setEstado(SolicitudCambioTurno.APROBADA);
		rechazada.setEstado(SolicitudCambioTurno.RECHAZADA);
		ArrayList<SolicitudCambioTurno> todas = new ArrayList<SolicitudCambioTurno>();
		todas.add(aprobada);
		todas.add(pendiente);
		todas.add(rechazada);
		ArrayList<SolicitudCambioTurno> resultado = admin.getSolicitudesPendientes(todas);
		assertEquals(1, resultado.size());
		assertSame(pendiente, resultado.get(0));
	}

	//Sugerencias (RF 36)

	@Test
	public void evaluarSugerenciaAprobadaLaMarcaComoAprobada() {
		SugerenciaProducto s = ent1.sugerirProducto("Balon", "Futbol", new ArrayList<SugerenciaProducto>());
		admin.evaluarSugerencia(s, true);
		assertEquals(SugerenciaProducto.APROBADA, s.getEstado());
	}

	@Test
	public void evaluarSugerenciaNoAprobadaLaMarcaComoRechazada() {
		SugerenciaProducto s = ent1.sugerirProducto("Balon", "Futbol", new ArrayList<SugerenciaProducto>());
		admin.evaluarSugerencia(s, false);
		assertEquals(SugerenciaProducto.RECHAZADA, s.getEstado());
	}

	@Test
	public void getSugerenciasPendientesExcluyeLasYaEvaluadas() {
		ArrayList<SugerenciaProducto> todas = new ArrayList<SugerenciaProducto>();
		SugerenciaProducto a = ent1.sugerirProducto("A", "X", todas);
		SugerenciaProducto b = ent1.sugerirProducto("B", "X", todas);
		SugerenciaProducto c = ent1.sugerirProducto("C", "X", todas);
		admin.evaluarSugerencia(a, true);
		admin.evaluarSugerencia(c, false);
		ArrayList<SugerenciaProducto> pendientes = admin.getSugerenciasPendientes(todas);
		assertEquals(1, pendientes.size());
		assertSame(b, pendientes.get(0));
	}

	//Socios (RF 36)

	@Test
	public void crearSocioLoAgregaActivoYSinPuntos() {
		Socio s = admin.crearSocio(10, "Ana", "ana", "1234", new Date(0), usuarios);
		assertEquals(1, usuarios.size());
		assertSame(s, usuarios.get(0));
		assertTrue(s.isActivo());
		assertEquals(0, s.getPuntosFidelidad());
		assertEquals("Ana", s.getNombre());
	}

	@Test
	public void crearSocioConLoginRepetidoLanzaExcepcion() {
		admin.crearSocio(10, "Ana", "ana", "1234", new Date(0), usuarios);
		assertThrows(IllegalArgumentException.class,
				() -> admin.crearSocio(11, "Otra", "ana", "1234", new Date(0), usuarios));
		assertEquals(1, usuarios.size());
	}

	@Test
	public void crearSocioConIdRepetidoLanzaExcepcion() {
		admin.crearSocio(10, "Ana", "ana", "1234", new Date(0), usuarios);
		assertThrows(IllegalArgumentException.class,
				() -> admin.crearSocio(10, "Otra", "otra", "1234", new Date(0), usuarios));
		assertEquals(1, usuarios.size());
	}

	@Test
	public void crearSocioValidaContraTodosLosTiposDeUsuario() {
		usuarios.add(ent1);
		assertThrows(IllegalArgumentException.class,
				() -> admin.crearSocio(50, "Otro", "ent1", "1234", new Date(0), usuarios));
		assertThrows(IllegalArgumentException.class,
				() -> admin.crearSocio(1, "Otro", "nuevo", "1234", new Date(0), usuarios));
	}

	@Test
	public void darDeBajaSocioLoDesactivaYNoPuedeIniciarSesion() {
		Socio s = admin.crearSocio(10, "Ana", "ana", "1234", new Date(0), usuarios);
		ArrayList<Socio> socios = new ArrayList<Socio>();
		socios.add(s);
		admin.darDeBajaSocio(s, socios);
		assertFalse(s.isActivo());
		assertThrows(IllegalStateException.class, () -> new Autentication().iniciarSesion("ana", "1234", usuarios));
	}

	@Test
	public void darDeBajaSocioConservaSusDatos() {
		Socio s = admin.crearSocio(10, "Ana", "ana", "1234", new Date(0), usuarios);
		s.inscribirse("TENIS");
		admin.darDeBajaSocio(s, new ArrayList<Socio>());
		assertEquals(1, s.getInscripciones().size());
		assertEquals(1, usuarios.size());
	}

	//Equipos (RF 01-03, 36)

	private Equipo crearEquipoMayores(String nombre) {
		return admin.crearEquipo(nombre, futbol, 11, new Categoria(Categoria.MAYORES), equipos);
	}

	private Socio nuevoSocioAdulto(int id) {
		return new Socio(id, "Socio" + id, "s" + id, "1234", new Date(0), 0, true);
	}

	@Test
	public void crearEquipoLoAgregaActivoYVacio() {
		Equipo e = crearEquipoMayores("Leones");
		assertEquals(1, equipos.size());
		assertSame(e, equipos.get(0));
		assertTrue(e.isActivo());
		assertEquals(11, e.getCupoMax());
		assertEquals(Categoria.MAYORES, e.getCategoria().getNombre());
		assertEquals(0, e.getJugadores().size());
	}

	@Test
	public void crearEquipoConCupoCeroONegativoLanzaExcepcion() {
		assertThrows(IllegalArgumentException.class,
				() -> admin.crearEquipo("A", futbol, 0, new Categoria(Categoria.MAYORES), equipos));
		assertThrows(IllegalArgumentException.class,
				() -> admin.crearEquipo("B", futbol, -3, new Categoria(Categoria.MAYORES), equipos));
		assertEquals(0, equipos.size());
	}

	@Test
	public void crearEquipoConNombreRepetidoLanzaExcepcion() {
		crearEquipoMayores("Leones");
		assertThrows(IllegalArgumentException.class, () -> crearEquipoMayores("Leones"));
		assertEquals(1, equipos.size());
	}

	@Test
	public void inscribirEnEquipoAgregaAlJugadorConSuPosicionYCamiseta() {
		Equipo e = crearEquipoMayores("Leones");
		Socio s = nuevoSocioAdulto(1);
		admin.inscribirEnEquipo(s, e, "DELANTERO", 9, equipos);
		assertTrue(e.tieneJugador(s));
		assertEquals(9, e.buscarJugador(s).getNumeroCamiseta());
		assertEquals("DELANTERO", e.buscarJugador(s).getPosiccion());
	}

	@Test
	public void inscribirEnSegundoEquipoDelMismoDeporteLanzaExcepcion() {
		Equipo a = crearEquipoMayores("Leones");
		Equipo b = crearEquipoMayores("Tigres");
		Socio s = nuevoSocioAdulto(1);
		admin.inscribirEnEquipo(s, a, "DELANTERO", 9, equipos);
		assertThrows(IllegalStateException.class, () -> admin.inscribirEnEquipo(s, b, "PORTERO", 1, equipos));
		assertFalse(b.tieneJugador(s));
	}

	@Test
	public void inscribirEnEquipoDeOtroDeporteEsPosible() {
		Equipo futbolEq = crearEquipoMayores("Leones");
		Equipo basket = admin.crearEquipo("Aros", new Baloncesto(), 10, new Categoria(Categoria.MAYORES), equipos);
		Socio s = nuevoSocioAdulto(1);
		admin.inscribirEnEquipo(s, futbolEq, "DELANTERO", 9, equipos);
		admin.inscribirEnEquipo(s, basket, "ALERO", 7, equipos);
		assertTrue(basket.tieneJugador(s));
	}

	@Test
	public void inscribirEnEquipoSePermiteSiElEquipoAnteriorFueDadoDeBaja() {
		Equipo a = crearEquipoMayores("Leones");
		Equipo b = crearEquipoMayores("Tigres");
		Socio s = nuevoSocioAdulto(1);
		admin.inscribirEnEquipo(s, a, "DELANTERO", 9, equipos);
		admin.darDeBajaEquipo(a);
		admin.inscribirEnEquipo(s, b, "PORTERO", 1, equipos);
		assertTrue(b.tieneJugador(s));
	}

	@Test
	public void inscribirSocioDeEdadIncorrectaParaLaCategoriaLanzaExcepcion() {
		Equipo e = crearEquipoMayores("Leones");
		Socio nino = new Socio(2, "Nino", "nino", "1234", new Date(), 0, true);
		assertThrows(IllegalArgumentException.class, () -> admin.inscribirEnEquipo(nino, e, "DELANTERO", 9, equipos));
	}

	@Test
	public void inscribirEnEquipoLlenoLanzaExcepcion() {
		Equipo e = admin.crearEquipo("Chico", futbol, 1, new Categoria(Categoria.MAYORES), equipos);
		admin.inscribirEnEquipo(nuevoSocioAdulto(1), e, "DELANTERO", 9, equipos);
		assertThrows(IllegalStateException.class,
				() -> admin.inscribirEnEquipo(nuevoSocioAdulto(2), e, "PORTERO", 1, equipos));
	}

	@Test
	public void darDeBajaEquipoLoDesactivaPeroConservaSuPlantilla() {
		Equipo e = crearEquipoMayores("Leones");
		Socio s = nuevoSocioAdulto(1);
		admin.inscribirEnEquipo(s, e, "DELANTERO", 9, equipos);
		admin.darDeBajaEquipo(e);
		assertFalse(e.isActivo());
		assertTrue(e.tieneJugador(s));
		assertEquals(1, equipos.size());
	}

	//Inventario (RF 36)

	private int cantidad(Articulo a, Ubicacion u) {
		for (ExistenciaArticulo e : inventario) {
			if (e.getArticulo().getNombre().equals(a.getNombre()) && e.getUbicacion().getNombre().equals(u.getNombre())) {
				return e.getCantidadDisponible();
			}
		}
		return -1;
	}

	@Test
	public void reabastecerUnArticuloNuevoCreaLaExistencia() {
		Articulo balon = new Articulo("Balon", 50000, "Futbol");
		Ubicacion bodega = new Ubicacion("Bodega");
		admin.reabastecer(balon, bodega, 10, inventario);
		assertEquals(1, inventario.size());
		assertEquals(10, cantidad(balon, bodega));
	}

	@Test
	public void reabastecerUnArticuloExistenteSumaALaCantidad() {
		Articulo balon = new Articulo("Balon", 50000, "Futbol");
		Ubicacion bodega = new Ubicacion("Bodega");
		admin.reabastecer(balon, bodega, 10, inventario);
		admin.reabastecer(new Articulo("Balon", 50000, "Futbol"), new Ubicacion("Bodega"), 5, inventario);
		assertEquals(1, inventario.size());
		assertEquals(15, cantidad(balon, bodega));
	}

	@Test
	public void elMismoArticuloEnOtraUbicacionEsUnaExistenciaDistinta() {
		Articulo balon = new Articulo("Balon", 50000, "Futbol");
		Ubicacion bodega = new Ubicacion("Bodega");
		Ubicacion tienda = new Ubicacion("Tienda");
		admin.reabastecer(balon, bodega, 10, inventario);
		admin.reabastecer(balon, tienda, 3, inventario);
		assertEquals(2, inventario.size());
		assertEquals(10, cantidad(balon, bodega));
		assertEquals(3, cantidad(balon, tienda));
	}

	@Test
	public void reabastecerConCantidadNoPositivaLanzaExcepcion() {
		Articulo balon = new Articulo("Balon", 50000, "Futbol");
		Ubicacion bodega = new Ubicacion("Bodega");
		assertThrows(IllegalArgumentException.class, () -> admin.reabastecer(balon, bodega, 0, inventario));
		admin.reabastecer(balon, bodega, 10, inventario);
		assertThrows(IllegalArgumentException.class, () -> admin.reabastecer(balon, bodega, -2, inventario));
		assertEquals(10, cantidad(balon, bodega));
	}

	@Test
	public void moverArticuloDescuentaDelOrigenYSumaAlDestino() {
		Articulo balon = new Articulo("Balon", 50000, "Futbol");
		Ubicacion bodega = new Ubicacion("Bodega");
		Ubicacion tienda = new Ubicacion("Tienda");
		admin.reabastecer(balon, bodega, 10, inventario);
		admin.reabastecer(balon, tienda, 2, inventario);
		admin.moverArticulo(balon, bodega, tienda, 4, inventario);
		assertEquals(6, cantidad(balon, bodega));
		assertEquals(6, cantidad(balon, tienda));
	}

	@Test
	public void moverArticuloAUnDestinoSinExistenciaLaCrea() {
		Articulo balon = new Articulo("Balon", 50000, "Futbol");
		Ubicacion bodega = new Ubicacion("Bodega");
		Ubicacion tienda = new Ubicacion("Tienda");
		admin.reabastecer(balon, bodega, 10, inventario);
		admin.moverArticulo(balon, bodega, tienda, 4, inventario);
		assertEquals(6, cantidad(balon, bodega));
		assertEquals(4, cantidad(balon, tienda));
	}

	@Test
	public void moverMasDeLoQueHayLanzaExcepcionYNoCambiaElInventario() {
		Articulo balon = new Articulo("Balon", 50000, "Futbol");
		Ubicacion bodega = new Ubicacion("Bodega");
		Ubicacion tienda = new Ubicacion("Tienda");
		admin.reabastecer(balon, bodega, 10, inventario);
		assertThrows(IllegalStateException.class, () -> admin.moverArticulo(balon, bodega, tienda, 11, inventario));
		assertEquals(10, cantidad(balon, bodega));
		assertEquals(-1, cantidad(balon, tienda));
	}

	@Test
	public void moverALaMismaUbicacionLanzaExcepcion() {
		Articulo balon = new Articulo("Balon", 50000, "Futbol");
		Ubicacion bodega = new Ubicacion("Bodega");
		admin.reabastecer(balon, bodega, 10, inventario);
		assertThrows(IllegalArgumentException.class, () -> admin.moverArticulo(balon, bodega, bodega, 1, inventario));
	}

	@Test
	public void moverUnArticuloQueNoEstaEnElOrigenLanzaExcepcion() {
		Articulo balon = new Articulo("Balon", 50000, "Futbol");
		assertThrows(IllegalArgumentException.class, () -> admin.moverArticulo(balon, new Ubicacion("Bodega"),
				new Ubicacion("Tienda"), 1, inventario));
	}

	@Test
	public void moverCantidadCeroONegativaLanzaExcepcion() {
		Articulo balon = new Articulo("Balon", 50000, "Futbol");
		Ubicacion bodega = new Ubicacion("Bodega");
		Ubicacion tienda = new Ubicacion("Tienda");
		admin.reabastecer(balon, bodega, 10, inventario);
		assertThrows(IllegalArgumentException.class, () -> admin.moverArticulo(balon, bodega, tienda, 0, inventario));
		assertEquals(10, cantidad(balon, bodega));
	}

}
