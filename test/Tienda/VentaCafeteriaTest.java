package Tienda;

import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import Usuario.Socio;


public class VentaCafeteriaTest {
	private Socio socio;
	private Bebida cafe;
	private Snack galleta;
	private Snack manzana;

	private ArrayList<ItemVenta> items(ItemVenta... lista) {
		ArrayList<ItemVenta> items = new ArrayList<ItemVenta>();
		for (ItemVenta i : lista) {
			items.add(i);
		}
		return items;
	}

	@BeforeEach
	public void setUp() {
		socio = new Socio(1, "Socio", "socio", "1234", new Date(0), 0, true);
		cafe = new Bebida("Cafe", 4000, Bebida.CALIENTE);
		ArrayList<String> alergenos = new ArrayList<String>();
		alergenos.add("gluten");
		alergenos.add("leche");
		galleta = new Snack("Galleta", 2500, alergenos);
		manzana = new Snack("Manzana", 2000, null);
	}

	@Test
	public void soloAceptaProductosDeCafeteria() {
		VentaCafeteria v = new VentaCafeteria(new Date(), socio, null, 0);
		v.agregarItem(new ItemVenta(cafe, 1));
		assertThrows(IllegalArgumentException.class,
				() -> v.agregarItem(new ItemVenta(new Articulo("Camiseta", 50000, "Ropa"), 1)));
		assertEquals(1, v.getItems().size());
	}

	@Test
	public void propinaPorDefectoEsLaSugerida() {
		VentaCafeteria v = new VentaCafeteria(new Date(), socio, null, 0);
		assertEquals(VentaCafeteria.PROPINA_SUGERIDA, v.getPorcentajePropina(), 0.0001);
		assertEquals(10, v.getPorcentajePropina(), 0.0001);
	}

	@Test
	public void propinaNegativaExcepcion() {
		assertThrows(IllegalArgumentException.class, () -> new VentaCafeteria(new Date(), socio, null, 0, -1));
		VentaCafeteria v = new VentaCafeteria(new Date(), socio, null, 0);
		assertThrows(IllegalArgumentException.class, () -> v.setPorcentajePropina(-0.5));
		assertEquals(10, v.getPorcentajePropina(), 0.0001);
	}

	@Test
	public void impuestoPropinaYTotal() {
		VentaCafeteria v = new VentaCafeteria(new Date(), socio,
				items(new ItemVenta(cafe, 2), new ItemVenta(galleta, 2)), 0);
		assertEquals(13000, v.calcularSubtotal(), 0.0001);
		assertEquals(1040, v.calcularImpuestoConsumo(), 0.0001);
		assertEquals(1300, v.calcularPropina(), 0.0001);
		assertEquals(15340, v.calcularTotal(), 0.0001);
	}

	@Test
	public void propinaEnCeroNoSumaNada() {
		VentaCafeteria v = new VentaCafeteria(new Date(), socio, items(new ItemVenta(cafe, 2)), 0, 0);
		assertEquals(0, v.calcularPropina(), 0.0001);
		assertEquals(8640, v.calcularTotal(), 0.0001);
	}

	@Test
	public void impuestoYPropinaSeCalculanSobreLaBaseConDescuento() {
		VentaCafeteria v = new VentaCafeteria(new Date(), socio, items(new ItemVenta(cafe, 2)), 50);
		assertEquals(320, v.calcularImpuestoConsumo(), 0.0001);
		assertEquals(400, v.calcularPropina(), 0.0001);
		assertEquals(4720, v.calcularTotal(), 0.0001);
	}

	@Test
	public void advertenciaListaSoloSnacksConAlergenos() {
		VentaCafeteria v = new VentaCafeteria(new Date(), socio,
				items(new ItemVenta(cafe, 1), new ItemVenta(galleta, 1), new ItemVenta(manzana, 1)), 0);
		assertEquals("Atencion: Galleta contiene: gluten, leche", v.getAdvertenciaAlergenos());
	}

	@Test
	public void advertenciaConVariosSnacksUsaUnaLineaPorCada() {
		ArrayList<String> mani = new ArrayList<String>();
		mani.add("mani");
		Snack barra = new Snack("Barra", 3000, mani);
		VentaCafeteria v = new VentaCafeteria(new Date(), socio,
				items(new ItemVenta(galleta, 1), new ItemVenta(barra, 1)), 0);
		assertEquals("Atencion: Galleta contiene: gluten, leche\nAtencion: Barra contiene: mani",
				v.getAdvertenciaAlergenos());
	}

	@Test
	public void advertenciaVaciaSiNadaTieneAlergenos() {
		VentaCafeteria v = new VentaCafeteria(new Date(), socio,
				items(new ItemVenta(cafe, 1), new ItemVenta(manzana, 1)), 0);
		assertEquals("", v.getAdvertenciaAlergenos());
	}

	@Test
	public void confirmarActualizaPuntosYBloqueaLaVenta() {
		VentaCafeteria v = new VentaCafeteria(new Date(), socio,
				items(new ItemVenta(cafe, 2), new ItemVenta(galleta, 2)), 0);
		v.confirmar();
		assertTrue(v.isConfirmada());
		assertEquals(306, socio.getPuntosFidelidad());
		assertThrows(IllegalStateException.class, () -> v.confirmar());
		assertEquals(306, socio.getPuntosFidelidad());
	}

	@Test
	public void confirmarSinItemsExcepcion() {
		VentaCafeteria v = new VentaCafeteria(new Date(), socio, null, 0);
		assertThrows(IllegalStateException.class, () -> v.confirmar());
	}

	@Test
	public void rubrosIncluyenImpuestoYPropina() {
		VentaCafeteria v = new VentaCafeteria(new Date(), socio, items(new ItemVenta(cafe, 2)), 0);
		Map<String, Double> rubros = v.getRubros();
		assertEquals("[Subtotal, Descuento, Descuento por puntos, Base gravable, "
				+ "Impuesto al consumo (8%), Propina (10.0%), Total]", rubros.keySet().toString());
		assertEquals(640, rubros.get("Impuesto al consumo (8%)"), 0.0001);
		assertEquals(800, rubros.get("Propina (10.0%)"), 0.0001);
		assertEquals(9440, rubros.get("Total"), 0.0001);
	}

}
