package Tienda;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class UbicacionTest {
	@Test
	public void guardaElNombre() {
		assertEquals("Bodega", new Ubicacion("Bodega").getNombre());
	}

}
