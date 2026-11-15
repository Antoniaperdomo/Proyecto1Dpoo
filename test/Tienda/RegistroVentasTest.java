package Tienda;

import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import Usuario.Socio;

public class RegistroVentasTest {
	
	private RegistroVentas registro;
	private Socio socio;
	private Articulo camiseta;
	private Bebida cafe;

	private VentaTienda ventaTienda(long fecha, int cantidad) {
		ArrayList<ItemVenta> items = new ArrayList<ItemVenta>();
		items.add(new ItemVenta(camiseta, cantidad));
		return new VentaTienda(new Date(fecha), socio, items, 0);
	}

	private VentaCafeteria ventaCafeteria(long fecha, int cantidad) {
		ArrayList<ItemVenta> items = new ArrayList<ItemVenta>();
		items.add(new ItemVenta(cafe, cantidad));
		return new VentaCafeteria(new Date(fecha), socio, items, 0, 0);
	}

	@BeforeEach
	public void setUp() {
		registro = new RegistroVentas();
		socio = new Socio(1, "Socio", "socio", "1234", new Date(0), 0, true);
		camiseta = new Articulo("Camiseta", 1000, "Ropa");
		cafe = new Bebida("Cafe", 2000, Bebida.CALIENTE);
	}

	@Test
	public void registroNuevoEstaVacio() {
		assertTrue(registro.getVentas().isEmpty());
	}

	@Test
	public void registrarAgregaLaVenta() {
		Venta v = ventaTienda(100, 1);
		registro.registrar(v);
		assertEquals(1, registro.getVentas().size());
		assertSame(v, registro.getVentas().get(0));
	}

	@Test
	public void registrarNullExcepcion() {
		assertThrows(IllegalArgumentException.class, () -> registro.registrar(null));
	}

	@Test
	public void getVentasDevuelveUnaCopia() {
		registro.registrar(ventaTienda(100, 1));
		registro.getVentas().clear();
		assertEquals(1, registro.getVentas().size());
	}

	@Test
	public void consultarPorFechaIncluyeLosExtremos() {
		Venta antes = ventaTienda(99, 1);
		Venta enDesde = ventaTienda(100, 1);
		Venta enMedio = ventaTienda(150, 1);
		Venta enHasta = ventaTienda(200, 1);
		Venta despues = ventaTienda(201, 1);
		registro.registrar(antes);
		registro.registrar(enDesde);
		registro.registrar(enMedio);
		registro.registrar(enHasta);
		registro.registrar(despues);

		ArrayList<Venta> r = registro.consultarPorFecha(new Date(100), new Date(200));

		assertEquals(3, r.size());
		assertTrue(r.contains(enDesde));
		assertTrue(r.contains(enMedio));
		assertTrue(r.contains(enHasta));
		assertFalse(r.contains(antes));
		assertFalse(r.contains(despues));
	}

	@Test
	public void consultarPorFechaSinResultados() {
		registro.registrar(ventaTienda(100, 1));
		assertTrue(registro.consultarPorFecha(new Date(500), new Date(600)).isEmpty());
	}

	@Test
	public void totalesPorRubroSumaLasVentasDelRango() {
		registro.registrar(ventaTienda(100, 1)); // subtotal 1000
		registro.registrar(ventaTienda(150, 2)); // subtotal 2000
		registro.registrar(ventaTienda(900, 5)); // fuera del rango

		Map<String, Double> totales = registro.totalesPorRubro(new Date(100), new Date(200));

		assertEquals(3000, totales.get("Subtotal"), 0.0001);
		assertEquals(0, totales.get("Descuento"), 0.0001);
		assertEquals(3000, totales.get("Base gravable"), 0.0001);
		assertEquals(570, totales.get("IVA (19%)"), 0.0001);
		assertEquals(3570, totales.get("Total"), 0.0001);
	}

	@Test
	public void totalesPorRubroSeparaRubrosDeTiendaYCafeteria() {
		registro.registrar(ventaTienda(100, 1)); // IVA 190
		registro.registrar(ventaCafeteria(110, 1)); // consumo 160, sin propina

		Map<String, Double> totales = registro.totalesPorRubro(new Date(0), new Date(1000));

		assertEquals(190, totales.get("IVA (19%)"), 0.0001);
		assertEquals(160, totales.get("Impuesto al consumo (8%)"), 0.0001);
		assertEquals(3000, totales.get("Subtotal"), 0.0001);
	}

	@Test
	public void totalesPorRubroSinVentasEsVacio() {
		assertTrue(registro.totalesPorRubro(new Date(0), new Date(1000)).isEmpty());
	}

}
