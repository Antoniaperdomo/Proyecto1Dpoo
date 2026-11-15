package Tienda;

import static org.junit.jupiter.api.Assertions.*;
import java.util.Date;
import org.junit.jupiter.api.Test;
import Usuario.Entrenador;
import Usuario.Socio;

public class CodigoDescuentoTest {
	private Socio socio = new Socio(1, "Socio", "socio", "1234", new Date(0), 0, true);
	private Entrenador empleado = new Entrenador(2, "Coach", "coach", "1234", new Date(0), new Date(0));

	@Test
	public void constantesDelDescuento() {
		assertEquals(15, CodigoDescuento.PORCENTAJE_EMPLEADO);
		assertEquals(8, CodigoDescuento.PORCENTAJE_COMPARTIDO);
	}

	@Test
	public void empleadoRecibeSuDescuentoPropio() {
		assertEquals(15, CodigoDescuento.calcularPorcentaje(empleado, false));
	}

	@Test
	public void empleadoNoAcumulaConElDescuentoCompartido() {
		assertEquals(15, CodigoDescuento.calcularPorcentaje(empleado, true));
	}

	@Test
	public void socioConCodigoCompartidoRecibe8() {
		assertEquals(8, CodigoDescuento.calcularPorcentaje(socio, true));
	}

	@Test
	public void socioSinCodigoNoTieneDescuento() {
		assertEquals(0, CodigoDescuento.calcularPorcentaje(socio, false));
	}

}
