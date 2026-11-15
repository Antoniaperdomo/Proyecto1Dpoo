package Tienda;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class ArticuloTest {
	@Test
	public void guardaNombrePrecioYCategoria() {
		Articulo a = new Articulo("Camiseta", 50000, "Ropa");
		assertEquals("Camiseta", a.getNombre());
		assertEquals(50000, a.getPrecio(), 0.0001);
		assertEquals("Ropa", a.getCategoria());
	}

	@Test
	public void esUnProductoDeTienda() {
		Producto p = new Articulo("Camiseta", 50000, "Ropa");
		assertFalse(p instanceof ProductoCafeteria);
	}

	@Test
	public void setPrecioYNombreActualizan() {
		Articulo a = new Articulo("Camiseta", 50000, "Ropa");
		a.setPrecio(45000);
		a.setNombre("Camiseta oficial");
		assertEquals(45000, a.getPrecio(), 0.0001);
		assertEquals("Camiseta oficial", a.getNombre());
	}

}
