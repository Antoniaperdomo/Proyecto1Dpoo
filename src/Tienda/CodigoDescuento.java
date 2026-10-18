package Tienda;

import Usuario.Comprador;
import Usuario.Empleado;

public class CodigoDescuento {
	public static final int PORCENTAJE_EMPLEADO = 15;
	public static final int PORCENTAJE_COMPARTIDO = 8;

	public static int calcularPorcentaje(Comprador comprador, boolean descuentoCompartidoPorEmpleado) {
		if (comprador instanceof Empleado) {
			return (int) Math.round(comprador.getDescuentoPropio() * 100);
		}
		if (descuentoCompartidoPorEmpleado) {
			return PORCENTAJE_COMPARTIDO;
		}
		return 0;
	}

}