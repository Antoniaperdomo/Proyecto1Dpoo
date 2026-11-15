package Tienda;

import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;
import java.util.Date;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import Usuario.Socio;

public class VentaTiendaTest {
	private Socio socio;
	private Articulo camiseta;
	private Articulo balon;
	private Ubicacion bodega;
	private ArrayList<ExistenciaArticulo> inventario;

	private VentaTienda venta(ItemVenta... lista) {
		ArrayList<ItemVenta> items = new ArrayList<ItemVenta>();
		for (ItemVenta i : lista) {
			items.add(i);
		}
		return new VentaTienda(new Date(1000), socio, items, 0);
	}

	private int disponible(Articulo a) {
		for (ExistenciaArticulo e : inventario) {
			if (e.getArticulo() == a) {
				return e.getCantidadDisponible();
			}
		}
		return -1;
	}

	@BeforeEach
	public void setUp() {
		socio = new Socio(1, "Socio", "socio", "1234", new Date(0), 0, true);
		camiseta = new Articulo("Camiseta", 50000, "Ropa");
		balon = new Articulo("Balon", 80000, "Equipo");
		bodega = new Ubicacion("Bodega");
		inventario = new ArrayList<ExistenciaArticulo>();
		inventario.add(new ExistenciaArticulo(camiseta, bodega, 5));
		inventario.add(new ExistenciaArticulo(balon, bodega, 2));
	}

	@Test
	public void soloAceptaArticulos() {
		VentaTienda v = venta();
		v.agregarItem(new ItemVenta(camiseta, 1));
		assertThrows(IllegalArgumentException.class,
				() -> v.agregarItem(new ItemVenta(new Snack("Galleta", 2500, null), 1)));
		assertEquals(1, v.getItems().size());
	}

	@Test
	public void ivaEsElDiecinuevePorCientoDeLaBase() {
		VentaTienda v = venta(new ItemVenta(camiseta, 2));
		assertEquals(19, VentaTienda.IVA);
		assertEquals(19000, v.calcularIVA(), 0.0001);
		assertEquals(119000, v.calcularTotal(), 0.0001);
	}

	@Test
	public void ivaSeCalculaDespuesDeDescuentos() {
		VentaTienda v = venta(new ItemVenta(camiseta, 2));
		v.setPorcentajeDescuento(20);
		// base = 100000 - 20000 = 80000 ; IVA = 15200
		assertEquals(15200, v.calcularIVA(), 0.0001);
		assertEquals(95200, v.calcularTotal(), 0.0001);
	}

	@Test
	public void confirmarDescuentaDelInventario() {
		VentaTienda v = venta(new ItemVenta(camiseta, 2), new ItemVenta(balon, 1));
		v.confirmar(inventario, bodega);
		assertTrue(v.isConfirmada());
		assertEquals(3, disponible(camiseta));
		assertEquals(1, disponible(balon));
	}

	@Test
	public void confirmarConExactamenteLoDisponible() {
		VentaTienda v = venta(new ItemVenta(balon, 2));
		v.confirmar(inventario, bodega);
		assertEquals(0, disponible(balon));
	}

	@Test
	public void inventarioInsuficienteExcepcionYNoConfirma() {
		VentaTienda v = venta(new ItemVenta(balon, 3));
		assertThrows(IllegalStateException.class, () -> v.confirmar(inventario, bodega));
		assertFalse(v.isConfirmada());
		assertEquals(2, disponible(balon));
	}

	@Test
	public void sinStockEnUnArticuloNoSeDescuentaNingunoDeLaVenta() {
		VentaTienda v = venta(new ItemVenta(camiseta, 1), new ItemVenta(balon, 3));
		assertThrows(IllegalStateException.class, () -> v.confirmar(inventario, bodega));
		assertEquals(5, disponible(camiseta));
		assertEquals(2, disponible(balon));
	}

	@Test
	public void mismoArticuloEnVariosItemsSeSumaParaValidarStock() {
		VentaTienda v = venta(new ItemVenta(camiseta, 3), new ItemVenta(camiseta, 3));
		assertThrows(IllegalStateException.class, () -> v.confirmar(inventario, bodega));
		assertEquals(5, disponible(camiseta));
	}

	@Test
	public void mismoArticuloEnVariosItemsSeDescuentaJunto() {
		VentaTienda v = venta(new ItemVenta(camiseta, 2), new ItemVenta(camiseta, 2));
		v.confirmar(inventario, bodega);
		assertEquals(1, disponible(camiseta));
	}

	@Test
	public void articuloSinExistenciaEnLaUbicacionExcepcion() {
		VentaTienda v = venta(new ItemVenta(camiseta, 1));
		assertThrows(IllegalStateException.class, () -> v.confirmar(inventario, new Ubicacion("Sucursal")));
		assertEquals(5, disponible(camiseta));
	}

	@Test
	public void ubicacionSeIdentificaPorNombre() {
		VentaTienda v = venta(new ItemVenta(camiseta, 1));
		v.confirmar(inventario, new Ubicacion("Bodega"));
		assertEquals(4, disponible(camiseta));
	}

	@Test
	public void socioInactivoNoDescuentaInventario() {
		socio.setActivo(false);
		VentaTienda v = venta(new ItemVenta(camiseta, 2));
		assertThrows(IllegalStateException.class, () -> v.confirmar(inventario, bodega));
		assertFalse(v.isConfirmada());
		assertEquals(5, disponible(camiseta));
	}

	@Test
	public void ventaSinItemsNoTocaInventario() {
		VentaTienda v = venta();
		assertThrows(IllegalStateException.class, () -> v.confirmar(inventario, bodega));
		assertEquals(5, disponible(camiseta));
		assertEquals(2, disponible(balon));
	}

	@Test
	public void confirmarDosVecesNoDescuentaDeNuevo() {
		VentaTienda v = venta(new ItemVenta(camiseta, 2));
		v.confirmar(inventario, bodega);
		assertThrows(IllegalStateException.class, () -> v.confirmar(inventario, bodega));
		assertEquals(3, disponible(camiseta));
	}

}
