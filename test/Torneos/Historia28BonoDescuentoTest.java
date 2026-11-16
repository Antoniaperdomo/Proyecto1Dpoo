package Torneos;

import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;
import java.util.Date;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import Deportes.Equipo;
import Deportes.Partido;
import Tienda.Articulo;
import Tienda.ExistenciaArticulo;
import Tienda.ItemVenta;
import Tienda.Ubicacion;
import Tienda.VentaTienda;
import Usuario.Socio;

// HU28 Socio: guardar y usar el bono de descuento de un torneo recreativo. 
public class Historia28BonoDescuentoTest {
	private Socio socio;
	private Articulo camiseta;
	private ArrayList<ExistenciaArticulo> inventario;
	private Ubicacion bodega;

	@BeforeEach
	public void setUp() {
		socio = new Socio(1, "Ganador", "ganador", "1234", new Date(0), 1000, true);
		camiseta = new Articulo("Camiseta", 50000, "Ropa");
		bodega = new Ubicacion("Bodega");
		inventario = new ArrayList<ExistenciaArticulo>();
		inventario.add(new ExistenciaArticulo(camiseta, bodega, 10));
	}

	private VentaTienda compra(int unidades) {
		ArrayList<ItemVenta> items = new ArrayList<ItemVenta>();
		items.add(new ItemVenta(camiseta, unidades));
		return new VentaTienda(new Date(1000), socio, items, 0);
	}

	@Test
	public void finalizarRecreativoGuardaBonoDisponibleEnElSocio() {
		Torneo t = Fixture.recreativoFutbol(8);
		Equipo a = Fixture.equipoFutbol("A", 7, 100);
		Equipo b = Fixture.equipoFutbol("B", 7, 200);
		for (Equipo e : new Equipo[] { a, b }) {
			t.inscribirEquipo(e, Fixture.integrante(e, 0), new ArrayList<Equipo>(), new ArrayList<Partido>(),
					Fixture.ANTES_DEL_LIMITE);
		}
		t.iniciar();
		t.registrarResultado(t.getEncuentros().get(0), 1, 0);
		t.finalizar();
		BonoDescuento bono = Fixture.integrante(a, 0).getBonosDisponibles().get(0);
		assertEquals("Copa", bono.getTorneoOrigen());
		assertEquals(20, bono.getPorcentaje());
		assertFalse(bono.isUsado());
	}

	@Test
	public void usarBonoAplicaPorcentajeSobreElSubtotalYLoMarcaUsadoAlConfirmar() {
		BonoDescuento bono = new BonoDescuento("Copa", 20);
		socio.agregarBono(bono);
		VentaTienda v = compra(2); // subtotal 100000
		v.aplicarBono(bono);
		assertEquals(20000, v.calcularDescuento(), 0.0001);
		assertFalse(bono.isUsado()); // todavia no se confirma
		// base 80000 + IVA 19% = 95200
		assertEquals(95200, v.calcularTotal(), 0.0001);
		v.confirmar(inventario, bodega);
		assertTrue(bono.isUsado());
		assertEquals(0, socio.getBonosDisponibles().size());
	}

	@Test
	public void bonoNoSeAcumulaConCodigoDeEmpleadoNiPuntosEnNingunOrden() {
		BonoDescuento bono = new BonoDescuento("Copa", 20);
		socio.agregarBono(bono);

		VentaTienda conCodigo = compra(1);
		conCodigo.setPorcentajeDescuento(8);
		assertThrows(IllegalStateException.class, () -> conCodigo.aplicarBono(bono));

		VentaTienda conPuntos = compra(1);
		conPuntos.redimirPuntos(500);
		assertThrows(IllegalStateException.class, () -> conPuntos.aplicarBono(bono));

		VentaTienda conBono = compra(1);
		conBono.aplicarBono(bono);
		assertThrows(IllegalStateException.class, () -> conBono.setPorcentajeDescuento(8));
		assertThrows(IllegalStateException.class, () -> conBono.redimirPuntos(500));
		conBono.redimirPuntos(0); // pedir cero puntos no es otro descuento
		assertFalse(bono.isUsado());
	}

	@Test
	public void bonoYaUsadoSeRechaza() {
		BonoDescuento bono = new BonoDescuento("Copa", 20);
		socio.agregarBono(bono);
		VentaTienda primera = compra(1);
		primera.aplicarBono(bono);
		primera.confirmar(inventario, bodega);
		assertThrows(IllegalStateException.class, () -> compra(1).aplicarBono(bono));
		assertThrows(IllegalStateException.class, () -> bono.usar());
	}

	@Test
	public void dosVentasConElMismoBonoSoloPermitenConfirmarLaPrimera() {
		BonoDescuento bono = new BonoDescuento("Copa", 20);
		socio.agregarBono(bono);
		VentaTienda a = compra(1);
		VentaTienda b = compra(1);
		a.aplicarBono(bono);
		b.aplicarBono(bono);
		a.confirmar(inventario, bodega);
		assertThrows(IllegalStateException.class, () -> b.confirmar(inventario, bodega));
		assertEquals(9, inventario.get(0).getCantidadDisponible()); // la segunda no desconto inventario
	}

	@Test
	public void bonoDeOtroSocioSeRechaza() {
		Socio otro = new Socio(2, "Otro", "otro", "1234", new Date(0), 0, true);
		BonoDescuento ajeno = new BonoDescuento("Copa", 20);
		otro.agregarBono(ajeno);
		assertThrows(IllegalArgumentException.class, () -> compra(1).aplicarBono(ajeno));
	}

	@Test
	public void consultaMuestraTorneoOrigenPorcentajeYEstado() {
		BonoDescuento usado = new BonoDescuento("Copa Vieja", 10);
		usado.usar();
		socio.agregarBono(usado);
		socio.agregarBono(new BonoDescuento("Copa", 20));
		assertEquals(2, socio.getBonos().size());
		assertEquals(1, socio.getBonosDisponibles().size());
		assertTrue(socio.getBonos().get(0).toString().contains("Copa Vieja - 10% (usado)"));
		assertTrue(socio.getBonos().get(1).toString().contains("Copa - 20% (disponible)"));
	}

	@Test
	public void elBonoDebeTenerPorcentajeValido() {
		assertThrows(IllegalArgumentException.class, () -> new BonoDescuento("Copa", 0));
		assertThrows(IllegalArgumentException.class, () -> new BonoDescuento("Copa", 101));
		assertThrows(IllegalArgumentException.class, () -> new BonoDescuento("", 10));
	}

	@Test
	public void ganadorExternoNoRecibeBono() {
		Torneo t = Fixture.recreativoTenis(2, Torneo.SENCILLOS);
		Usuario.Externo externo = Fixture.externo(10);
		Socio rival = Fixture.socioEnDisciplina(1, "TENIS", 1200);
		t.inscribirParticipante(externo, null, "INTERMEDIO", Fixture.lista(rival),
				new ArrayList<Administradores.Turno>(), Fixture.ANTES_DEL_LIMITE);
		Fixture.inscribir(t, rival, Fixture.lista(rival), Fixture.ANTES_DEL_LIMITE);
		t.iniciar();
		t.registrarResultado(t.getEncuentros().get(0), 6, 1); // gana el externo
		t.finalizar();
		assertEquals(0, rival.getBonos().size());
		assertTrue(t.getPremiosEntregados().isEmpty());
	}
}
