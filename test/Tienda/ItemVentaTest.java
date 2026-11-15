package Tienda;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class ItemVentaTest {
	private Articulo camiseta = new Articulo("Camiseta", 50000, "Ropa");

	@Test
	public void guardaProductoYCantidad() {
		ItemVenta item = new ItemVenta(camiseta, 3);
		assertSame(camiseta, item.getProducto());
		assertEquals(3, item.getCantidad());
	}

	@Test
	public void subtotalEsPrecioPorCantidad() {
		assertEquals(150000, new ItemVenta(camiseta, 3).getSubtotal(), 0.0001);
	}

	@Test
	public void productoNuloExcepcion() {
		assertThrows(IllegalArgumentException.class, () -> new ItemVenta(null, 1));
	}

	@Test
	public void cantidadCeroONegativaExcepcion() {
		assertThrows(IllegalArgumentException.class, () -> new ItemVenta(camiseta, 0));
		assertThrows(IllegalArgumentException.class, () -> new ItemVenta(camiseta, -2));
	}

	@Test
	public void subtotalSigueElPrecioActualDelProducto() {
		ItemVenta item = new ItemVenta(camiseta, 2);
		camiseta.setPrecio(10000);
		assertEquals(20000, item.getSubtotal(), 0.0001);
	}

}
