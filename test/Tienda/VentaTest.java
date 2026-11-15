package Tienda;

import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import Usuario.Entrenador;
import Usuario.Socio;
import Usuario.Usuario;


public class VentaTest {
	
	private Socio socio;
	private Articulo camiseta;
	private Articulo balon;
	private Date fecha;

	private ArrayList<ItemVenta> items(ItemVenta... lista) {
		ArrayList<ItemVenta> items = new ArrayList<ItemVenta>();
		for (ItemVenta i : lista) {
			items.add(i);
		}
		return items;
	}

	private ArrayList<ExistenciaArticulo> inventarioConTodo(Ubicacion u) {
		ArrayList<ExistenciaArticulo> inv = new ArrayList<ExistenciaArticulo>();
		inv.add(new ExistenciaArticulo(camiseta, u, 100));
		inv.add(new ExistenciaArticulo(balon, u, 100));
		return inv;
	}

	@BeforeEach
	public void setUp() {
		socio = new Socio(1, "Socio", "socio", "1234", new Date(0), 1000, true);
		camiseta = new Articulo("Camiseta", 50000, "Ropa");
		balon = new Articulo("Balon", 80000, "Equipo");
		fecha = new Date(1000);
	}


	@Test
	public void ventaNuevaNoEstaConfirmada() {
		VentaTienda v = new VentaTienda(fecha, socio, null, 0);
		assertFalse(v.isConfirmada());
		assertSame(socio, v.getComprador());
		assertSame(fecha, v.getFecha());
		assertTrue(v.getItems().isEmpty());
	}

	@Test
	public void fechaOCompradorNulosExcepcion() {
		assertThrows(IllegalArgumentException.class, () -> new VentaTienda(null, socio, null, 0));
		assertThrows(IllegalArgumentException.class, () -> new VentaTienda(fecha, null, null, 0));
	}

	@Test
	public void compradorQueNoEsSocioNiEmpleadoExcepcion() {
		Usuario otro = new Usuario(9, "Otro", "otro", "1234", new Date(0)) {
		};
		assertThrows(IllegalArgumentException.class, () -> new VentaTienda(fecha, otro, null, 0));
	}

	@Test
	public void empleadoPuedeComprar() {
		Entrenador e = new Entrenador(2, "Coach", "coach", "1234", new Date(0), new Date(0));
		VentaTienda v = new VentaTienda(fecha, e, null, 15);
		assertSame(e, v.getComprador());
	}

	@Test
	public void constructorAgregaLosItemsIniciales() {
		VentaTienda v = new VentaTienda(fecha, socio, items(new ItemVenta(camiseta, 1), new ItemVenta(balon, 2)), 0);
		assertEquals(2, v.getItems().size());
	}


	@Test
	public void agregarItemNuloExcepcion() {
		VentaTienda v = new VentaTienda(fecha, socio, null, 0);
		assertThrows(IllegalArgumentException.class, () -> v.agregarItem(null));
	}

	@Test
	public void agregarItemDeOtroTipoDeVentaExcepcion() {
		VentaTienda v = new VentaTienda(fecha, socio, null, 0);
		assertThrows(IllegalArgumentException.class,
				() -> v.agregarItem(new ItemVenta(new Bebida("Cafe", 4000, Bebida.CALIENTE), 1)));
	}


	@Test
	public void descuentoFueraDeRangoExcepcion() {
		assertThrows(IllegalArgumentException.class, () -> new VentaTienda(fecha, socio, null, -1));
		assertThrows(IllegalArgumentException.class, () -> new VentaTienda(fecha, socio, null, 101));
	}

	@Test
	public void descuentoEnLosLimitesEsValido() {
		assertEquals(0, new VentaTienda(fecha, socio, null, 0).getPorcentajeDescuento());
		assertEquals(100, new VentaTienda(fecha, socio, null, 100).getPorcentajeDescuento());
	}

	@Test
	public void subtotalYDescuento() {
		VentaTienda v = new VentaTienda(fecha, socio, items(new ItemVenta(camiseta, 2), new ItemVenta(balon, 1)), 10);
		assertEquals(180000, v.calcularSubtotal(), 0.0001);
		assertEquals(18000, v.calcularDescuento(), 0.0001);
	}

	@Test
	public void descuentoDel100PorCientoDejaBaseEnCero() {
		VentaTienda v = new VentaTienda(fecha, socio, items(new ItemVenta(camiseta, 1)), 100);
		assertEquals(0, v.calcularTotal(), 0.0001);
	}


	@Test
	public void redimirPuntosNegativosExcepcion() {
		VentaTienda v = new VentaTienda(fecha, socio, items(new ItemVenta(camiseta, 1)), 0);
		assertThrows(IllegalArgumentException.class, () -> v.redimirPuntos(-1));
	}

	@Test
	public void redimirMasPuntosDeLosQueTieneExcepcion() {
		VentaTienda v = new VentaTienda(fecha, socio, items(new ItemVenta(camiseta, 1)), 0);
		assertThrows(IllegalArgumentException.class, () -> v.redimirPuntos(1001));
	}

	@Test
	public void redimirPuntosRestaDineroDeLaBase() {
		VentaTienda v = new VentaTienda(fecha, socio, items(new ItemVenta(camiseta, 1)), 0);
		v.redimirPuntos(1000);
		assertEquals(1000, v.calcularDescuentoPuntos(), 0.0001);
		assertEquals(1000, v.getPuntosAplicados());
		assertEquals(49000, v.calcularBase(), 0.0001);
	}

	@Test
	public void puntosAplicadosNoSuperanElValorDeLaCompra() {
		socio.setPuntosFidelidad(100000);
		VentaTienda v = new VentaTienda(fecha, socio, items(new ItemVenta(camiseta, 1)), 0);
		v.redimirPuntos(100000);
		assertEquals(50000, v.getPuntosAplicados());
	}

	@Test
	public void puntosGanadosSonElDosPorCientoDelTotal() {
		VentaTienda v = new VentaTienda(fecha, socio, items(new ItemVenta(camiseta, 1)), 0);
		assertEquals(59500, v.calcularTotal(), 0.0001);
		assertEquals(1190, v.calcularPuntos());
	}


	@Test
	public void confirmarActualizaLosPuntosDelComprador() {
		VentaTienda v = new VentaTienda(fecha, socio, items(new ItemVenta(camiseta, 1)), 0);
		v.redimirPuntos(100);
		v.confirmar(inventarioConTodo(new Ubicacion("Bodega")), new Ubicacion("Bodega"));
		assertTrue(v.isConfirmada());
		assertEquals(2087, socio.getPuntosFidelidad());
	}

	@Test
	public void ventaSinItemsNoSePuedeConfirmar() {
		VentaTienda v = new VentaTienda(fecha, socio, null, 0);
		assertThrows(IllegalStateException.class,
				() -> v.confirmar(inventarioConTodo(new Ubicacion("Bodega")), new Ubicacion("Bodega")));
		assertFalse(v.isConfirmada());
	}

	@Test
	public void socioDadoDeBajaNoPuedeConfirmar() {
		socio.setActivo(false);
		VentaTienda v = new VentaTienda(fecha, socio, items(new ItemVenta(camiseta, 1)), 0);
		assertThrows(IllegalStateException.class,
				() -> v.confirmar(inventarioConTodo(new Ubicacion("Bodega")), new Ubicacion("Bodega")));
		assertFalse(v.isConfirmada());
		assertEquals(1000, socio.getPuntosFidelidad());
	}

	@Test
	public void ventaConfirmadaYaNoSeModifica() {
		VentaTienda v = new VentaTienda(fecha, socio, items(new ItemVenta(camiseta, 1)), 0);
		v.confirmar(inventarioConTodo(new Ubicacion("Bodega")), new Ubicacion("Bodega"));
		assertThrows(IllegalStateException.class, () -> v.agregarItem(new ItemVenta(balon, 1)));
		assertThrows(IllegalStateException.class, () -> v.setPorcentajeDescuento(5));
		assertThrows(IllegalStateException.class, () -> v.redimirPuntos(10));
	}

	@Test
	public void restaurarConfirmadaMarcaLaVentaYGuardaLosPuntos() {
		VentaTienda v = new VentaTienda(fecha, socio, items(new ItemVenta(camiseta, 1)), 0);
		v.restaurarConfirmada(50);
		assertTrue(v.isConfirmada());
		assertEquals(50, v.getPuntosAplicados());
	}

	@Test
	public void restaurarConfirmadaConPuntosNegativosExcepcion() {
		VentaTienda v = new VentaTienda(fecha, socio, items(new ItemVenta(camiseta, 1)), 0);
		assertThrows(IllegalArgumentException.class, () -> v.restaurarConfirmada(-1));
		assertFalse(v.isConfirmada());
	}


	@Test
	public void rubrosEnOrdenYConSusValores() {
		VentaTienda v = new VentaTienda(fecha, socio, items(new ItemVenta(camiseta, 2)), 10);
		Map<String, Double> rubros = v.getRubros();
		assertEquals("[Subtotal, Descuento, Descuento por puntos, Base gravable, IVA (19%), Total]",
				rubros.keySet().toString());
		assertEquals(100000, rubros.get("Subtotal"), 0.0001);
		assertEquals(10000, rubros.get("Descuento"), 0.0001);
		assertEquals(0, rubros.get("Descuento por puntos"), 0.0001);
		assertEquals(90000, rubros.get("Base gravable"), 0.0001);
		assertEquals(17100, rubros.get("IVA (19%)"), 0.0001);
		assertEquals(107100, rubros.get("Total"), 0.0001);
	}

}
