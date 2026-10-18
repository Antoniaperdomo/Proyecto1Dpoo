package Tienda;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import Usuario.Usuario;

public class VentaTienda extends Venta {
	public static final int IVA = 19;

	public VentaTienda(Date fecha, Usuario comprador, ArrayList<ItemVenta> items, int porcentajeDescuento) {
		super(fecha, comprador, items, porcentajeDescuento);
	}
	@Override
	protected boolean esItemValido(Producto producto) {
		return producto instanceof Articulo;
	}

	public double calcularIVA() {
		return calcularBase() * IVA / 100.0;
	}

	@Override
	public double calcularTotal() {
		return calcularBase() + calcularIVA();
	}
	@Override
	protected void agregarRubrosAdicionales(Map<String, Double> rubros) {
		rubros.put("IVA (" + IVA + "%)", calcularIVA());
	}

}
