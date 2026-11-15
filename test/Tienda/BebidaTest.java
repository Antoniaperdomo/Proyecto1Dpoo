package Tienda;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class BebidaTest {
	@Test
	public void bebidaFriaNoEsCaliente() {
		Bebida b = new Bebida("Jugo", 6000, Bebida.FRIA);
		assertEquals(Bebida.FRIA, b.getTipo());
		assertFalse(b.esCaliente());
	}

	@Test
	public void bebidaCalienteEsCaliente() {
		Bebida b = new Bebida("Cafe", 4000, Bebida.CALIENTE);
		assertTrue(b.esCaliente());
	}

	@Test
	public void tipoInvalidoExcepcion() {
		assertThrows(IllegalArgumentException.class, () -> new Bebida("Jugo", 6000, "TIBIA"));
		assertThrows(IllegalArgumentException.class, () -> new Bebida("Jugo", 6000, null));
	}

	@Test
	public void esUnProductoDeCafeteria() {
		ProductoCafeteria p = new Bebida("Cafe", 4000, Bebida.CALIENTE);
		assertEquals("Cafe", p.getNombre());
		assertEquals(4000, p.getPrecio(), 0.0001);
	}

	@Test
	public void setTipoCambiaElTipo() {
		Bebida b = new Bebida("Chocolate", 5000, Bebida.FRIA);
		b.setTipo(Bebida.CALIENTE);
		assertTrue(b.esCaliente());
	}

}
