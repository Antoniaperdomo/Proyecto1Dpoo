package Administradores;

import java.util.Date;

import Usuario.Fisioterapeuta;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SugerenciaProductoTest {
	private Fisioterapeuta empleado;
	private SugerenciaProducto sugerencia;

	@BeforeEach
	public void setUp() {
		empleado = new Fisioterapeuta(2, "Fisio", "fisio", "1234", new Date(0), new Date(0));
		sugerencia = new SugerenciaProducto("Raqueta pro", "Raquetas", empleado);
	}

	@Test
	public void nuevaSugerenciaEstaPendienteYGuardaSusDatos() {
		assertEquals(SugerenciaProducto.PENDIENTE, sugerencia.getEstado());
		assertEquals("Raqueta pro", sugerencia.getNombre());
		assertEquals("Raquetas", sugerencia.getCategoria());
		assertSame(empleado, sugerencia.getEmpleado());
	}

	@Test
	public void sePuedeAprobar() {
		sugerencia.setEstado(SugerenciaProducto.APROBADA);
		assertEquals(SugerenciaProducto.APROBADA, sugerencia.getEstado());
	}

	@Test
	public void sePuedeRechazar() {
		sugerencia.setEstado(SugerenciaProducto.RECHAZADA);
		assertEquals(SugerenciaProducto.RECHAZADA, sugerencia.getEstado());
	}

	@Test
	public void estadoInvalidoLanzaExcepcionYNoCambiaElEstado() {
		assertThrows(IllegalArgumentException.class, () -> sugerencia.setEstado("quizas"));
		assertEquals(SugerenciaProducto.PENDIENTE, sugerencia.getEstado());
	}

}
