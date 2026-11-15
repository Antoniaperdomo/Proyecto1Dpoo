package Tienda;

import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;

public class SnackTest {
	@Test
	public void snackSinAlergenos() {
		Snack s = new Snack("Manzana", 2000, new ArrayList<String>());
		assertFalse(s.tieneAlergenos());
		assertEquals("Manzana: no registra alergenos.", s.getAdvertenciaAlergenos());
	}

	@Test
	public void alergenosNullSeTrataComoListaVacia() {
		Snack s = new Snack("Manzana", 2000, null);
		assertNotNull(s.getAlergenos());
		assertTrue(s.getAlergenos().isEmpty());
		assertFalse(s.tieneAlergenos());
	}

	@Test
	public void snackConUnAlergeno() {
		ArrayList<String> alergenos = new ArrayList<String>();
		alergenos.add("mani");
		Snack s = new Snack("Barra", 3000, alergenos);
		assertTrue(s.tieneAlergenos());
		assertEquals("Atencion: Barra contiene: mani", s.getAdvertenciaAlergenos());
	}

	@Test
	public void snackConVariosAlergenosLosSeparaConComas() {
		ArrayList<String> alergenos = new ArrayList<String>();
		alergenos.add("mani");
		alergenos.add("leche");
		alergenos.add("gluten");
		Snack s = new Snack("Galleta", 2500, alergenos);
		assertEquals("Atencion: Galleta contiene: mani, leche, gluten", s.getAdvertenciaAlergenos());
	}

	@Test
	public void esUnProductoDeCafeteria() {
		Producto p = new Snack("Galleta", 2500, null);
		assertTrue(p instanceof ProductoCafeteria);
		assertEquals(2500, p.getPrecio(), 0.0001);
	}

}
