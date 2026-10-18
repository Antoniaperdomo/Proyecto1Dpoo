package Tienda;

public class CodigoDescuento {
	public static final int PORCENTAJE_EMPLEADO = 15;
	public static final int PORCENTAJE_COMPARTIDO = 8;
	public static int calcularPorcentaje(boolean compradorEsEmpleado, boolean descuentoCompartidoPorEmpleado) {
		if (compradorEsEmpleado) {
			return PORCENTAJE_EMPLEADO;
		}
		if (descuentoCompartidoPorEmpleado) {
			return PORCENTAJE_COMPARTIDO;
		}
		return 0;
	}

}
