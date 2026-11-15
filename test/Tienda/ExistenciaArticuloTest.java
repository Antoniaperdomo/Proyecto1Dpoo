package Tienda;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ExistenciaArticuloTest {
	private Articulo camiseta;
	private Ubicacion bodega;
	private ExistenciaArticulo existencia;

	@BeforeEach
	public void setUp() {
		camiseta = new Articulo("Camiseta", 50000, "Ropa");
		bodega = new Ubicacion("Bodega");
		existencia = new ExistenciaArticulo(camiseta, bodega, 10);
	}

	@Test
	public void guardaArticuloUbicacionYCantidad() {
		assertSame(camiseta, existencia.getArticulo());
		assertSame(bodega, existencia.getUbicacion());
		assertEquals(10, existencia.getCantidadDisponible());
	}

	@Test
	public void reducirDescuentaUnidades() {
		existencia.reducir(4);
		assertEquals(6, existencia.getCantidadDisponible());
	}

	@Test
	public void reducirTodoDejaEnCero() {
		existencia.reducir(10);
		assertEquals(0, existencia.getCantidadDisponible());
	}

	@Test
	public void reducirMasDeLoDisponibleExcepcion() {
		assertThrows(IllegalStateException.class, () -> existencia.reducir(11));
		assertEquals(10, existencia.getCantidadDisponible());
	}

	@Test
	public void reducirCantidadInvalidaExcepcion() {
		assertThrows(IllegalArgumentException.class, () -> existencia.reducir(0));
		assertThrows(IllegalArgumentException.class, () -> existencia.reducir(-1));
	}

	@Test
	public void aumentarSumaUnidades() {
		existencia.aumentar(5);
		assertEquals(15, existencia.getCantidadDisponible());
	}

	@Test
	public void aumentarCantidadInvalidaExcepcion() {
		assertThrows(IllegalArgumentException.class, () -> existencia.aumentar(0));
		assertThrows(IllegalArgumentException.class, () -> existencia.aumentar(-3));
	}

}
