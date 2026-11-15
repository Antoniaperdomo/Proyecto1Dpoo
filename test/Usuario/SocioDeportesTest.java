package Usuario;


import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;
import java.util.Date;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import Deportes.Baloncesto;
import Deportes.CanchaTenis;
import Deportes.Categoria;
import Deportes.DeporteIndividual;
import Deportes.Equipo;
import Deportes.Futbol;
import Deportes.InscripcionIndividual;
import Deportes.Instalacion;
import Deportes.JugadorEquipo;
import Deportes.ReservaInstalacion;
import Deportes.Suspension;

public class SocioDeportesTest {
	private Socio socio;

	private Socio nuevoSocio(int id) {
		return new Socio(id, "Socio" + id, "s" + id, "1234", new Date(0), 0, true);
	}

	private Equipo nuevoEquipo(String nombre, Futbol deporte) {
		return new Equipo(nombre, deporte, 10, new Categoria(Categoria.MAYORES), new ArrayList<JugadorEquipo>(),
				new ArrayList<Suspension>(), true);
	}
	
	@BeforeEach
	public void setUp() {
		socio = nuevoSocio(1);
	}
	
	@Test
	public void inscribirseCreaInscripcionPrincipianteConElo100() {
		InscripcionIndividual insc = socio.inscribirse("TENIS");
		assertEquals(1000, insc.getRankingELO());
		assertEquals(DeporteIndividual.PRINCIPIANTE, insc.getDeporte().getNivel());
		assertSame(insc, socio.buscarInscripcion("TENIS"));
	}
	@Test
	public void inscibirseDosVecesEnLaMismaExcepcion() {
		socio.inscribirse("PADEL");
		assertThrows(IllegalStateException.class, () -> socio.inscribirse("PADEL"));
	}
	@Test
	public void inscibirseEnDIsciplinaQueNoExisteExcepcion() {
		assertThrows(IllegalStateException.class, () -> socio.inscribirse("GOLF"));
	}
	@Test
	public void socioDadoDeBajaNoPuedeInscribirse() {
		socio.setActivo(false);
		assertThrows(IllegalStateException.class, () -> socio.inscribirse("TENIS"));
	}
	@Test
	public void buscarInscripcionSinInscribirseNull() {
		assertNull(socio.buscarInscripcion("TENIS"));
	}
	@Test
	public void unirseAEquipoLoRegistraEnAmbosLados() {
		Equipo equipo = nuevoEquipo("A", new Futbol());
		socio.unirseEquipo(equipo, "Portero", 1);
		assertTrue(equipo.tieneJugador(socio));
		assertEquals(1, socio.getEquipos().size());
	}
	@Test
	public void noPuedeUnirseADosEquiposDelMismoDeporte() {
		socio.unirseEquipo(nuevoEquipo("A", new Futbol()), "Portero", 1);
		assertThrows(IllegalStateException.class, () -> socio.unirseEquipo(nuevoEquipo("B", new Futbol()), "Defensa", 2));
		assertEquals(1, socio.getEquipos().size());
	}
	@Test
	public void puedeUnirseAEquiposDeDeportesDistintos() {
		socio.unirseEquipo(nuevoEquipo("A", new Futbol()), "Portero", 1);
		Equipo baloncesto = new Equipo("B", new Baloncesto(), 10, new Categoria(Categoria.MAYORES),
				new ArrayList<JugadorEquipo>(), new ArrayList<Suspension>(), true);
		socio.unirseEquipo(baloncesto, "Base", 5);
		assertEquals(2, socio.getEquipos().size());
	}
	@Test
	public void socioDadoDeBajaNoPuedeUnirse() {
		socio.setActivo(false);
		assertThrows(IllegalStateException.class, () -> socio.unirseEquipo(nuevoEquipo("A", new Futbol()), "Portero", 1));
	}
	@Test
	public void reservarSinInscripcionLanzaExcepcion() {
		ArrayList<Instalacion> canchas = new ArrayList<Instalacion>();
		canchas.add(new CanchaTenis("T1"));
		assertThrows(IllegalStateException.class,
				() -> socio.reservar(canchas, "TENIS", new Date(), new Date(), "SENCILLOS", 2));
	}
	@Test
	public void reservarCreaLaReservaEnElSocioYEnLaInstalacion() {
		socio.inscribirse("TENIS");
		CanchaTenis cancha = new CanchaTenis("T1");
		ArrayList<Instalacion> canchas = new ArrayList<Instalacion>();
		canchas.add(cancha);
		Date fecha = new Date();
		Date bloque = new Date();

		ReservaInstalacion reserva = socio.reservar(canchas, "TENIS", fecha, bloque, "SENCILLOS", 2);

		assertSame(cancha, reserva.getInstalacion());
		assertEquals(1, socio.getReservas().size());
		assertTrue(cancha.estaOcupada(fecha, bloque));
	}
	@Test
	public void reservarConDemasiadosJugadoresLanzaExcepcion() {
		socio.inscribirse("TENIS");
		ArrayList<Instalacion> canchas = new ArrayList<Instalacion>();
		canchas.add(new CanchaTenis("T1"));
		assertThrows(IllegalArgumentException.class,
				() -> socio.reservar(canchas, "TENIS", new Date(), new Date(), "DOBLES", 5));
		assertEquals(0, socio.getReservas().size());
	}
	@Test
	public void reservarConTodasLasInstalacionesOcupadasLanzaExcepcion() {
		Socio otro = nuevoSocio(2);
		socio.inscribirse("TENIS");
		otro.inscribirse("TENIS");
		ArrayList<Instalacion> canchas = new ArrayList<Instalacion>();
		canchas.add(new CanchaTenis("T1"));
		Date fecha = new Date();
		Date bloque = new Date();
		socio.reservar(canchas, "TENIS", fecha, bloque, "SENCILLOS", 2);

		assertThrows(IllegalStateException.class, () -> otro.reservar(canchas, "TENIS", fecha, bloque, "SENCILLOS", 2));
	}
	@Test
	public void reservarUsaOtraCanchaSiLaPrimeraEstaOcupada() {
		Socio otro = nuevoSocio(2);
		socio.inscribirse("TENIS");
		otro.inscribirse("TENIS");
		ArrayList<Instalacion> canchas = new ArrayList<Instalacion>();
		CanchaTenis t1 = new CanchaTenis("T1");
		CanchaTenis t2 = new CanchaTenis("T2");
		canchas.add(t1);
		canchas.add(t2);
		Date fecha = new Date();
		Date bloque = new Date();
		socio.reservar(canchas, "TENIS", fecha, bloque, "SENCILLOS", 2);

		ReservaInstalacion segunda = otro.reservar(canchas, "TENIS", fecha, bloque, "SENCILLOS", 2);

		assertSame(t2, segunda.getInstalacion());
	}
	
}
