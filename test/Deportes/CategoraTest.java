package Deportes;

import static org.junit.jupiter.api.Assertions.*;
import java.util.Date;
import org.junit.jupiter.api.Test;
import Usuario.Socio;

public class CategoraTest {
	private Socio socioConEdad(int edad) {
		long msPorAnio = 31557600000L;
		Date nacimiento = new Date(new Date().getTime() - edad * msPorAnio);
		return new Socio(1, "Prueba", "prueba", "1234", nacimiento, 0, true);
	}

	@Test
	public void categoriasValidasSeCran() {
		assertEquals("SUB13", new Categoria(Categoria.SUB13).getNombre());
		assertEquals("SUB15", new Categoria(Categoria.SUB15).getNombre());
		assertEquals("SUB17", new Categoria(Categoria.SUB17).getNombre());
		assertEquals("MAYORES", new Categoria(Categoria.MAYORES).getNombre());
	}

	@Test
	public void categoriaInvalidaLansaExcepcion() {
		assertThrows(IllegalArgumentException.class, () -> new Categoria("SUB20"));
	}

	@Test
	public void sun13AceptaHasta12() {
		Categoria c = new Categoria(Categoria.SUB13);
		assertTrue(c.aceptaEdad(socioConEdad(12)));
		assertFalse(c.aceptaEdad(socioConEdad(13)));
	}

	@Test
	public void sub15Acepta13y14() {
		Categoria c = new Categoria(Categoria.SUB15);
		assertFalse(c.aceptaEdad(socioConEdad(12)));
		assertTrue(c.aceptaEdad(socioConEdad(13)));
		assertTrue(c.aceptaEdad(socioConEdad(14)));
		assertFalse(c.aceptaEdad(socioConEdad(15)));

	}

	@Test
	public void sub15Acepta15y16() {
		Categoria c = new Categoria(Categoria.SUB17);
		assertFalse(c.aceptaEdad(socioConEdad(14)));
		assertTrue(c.aceptaEdad(socioConEdad(15)));
		assertTrue(c.aceptaEdad(socioConEdad(16)));
		assertFalse(c.aceptaEdad(socioConEdad(17)));

	}

	@Test
	public void mayoresAcepta17omas() {
		Categoria c = new Categoria(Categoria.MAYORES);
		assertFalse(c.aceptaEdad(socioConEdad(16)));
		assertTrue(c.aceptaEdad(socioConEdad(17)));
		assertTrue(c.aceptaEdad(socioConEdad(18)));
		assertTrue(c.aceptaEdad(socioConEdad(40)));

	}
}
