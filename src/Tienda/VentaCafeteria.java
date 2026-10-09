package Tienda;

import java.util.ArrayList;
import java.util.Date;

import Usuario.Usuario;

public class VentaCafeteria extends Venta{
	public static final int IMPUESTO_CONSUMO = 8;
	
	private double porcentajePropina;

	public VentaCafeteria(Date fecha, Usuario comprador, ArrayList<ItemVenta> items, int porcentajeDescuento,
			double porcentajePropina) {
		super(fecha, comprador, items, porcentajeDescuento);
		this.porcentajePropina = porcentajePropina;
	}

	public double getPorcentajePropina() {
		return porcentajePropina;
	}

	public void setPorcentajePropina(double porcentajePropina) {
		this.porcentajePropina = porcentajePropina;
	}
	
	public double calcularImpuestoConsumo() {
		return calcularBase() * IMPUESTO_CONSUMO / 100.0;
	}
	
	public double calcularPropina() {
		return calcularBase() * porcentajePropina / 100.0;
	}

	@Override
	public double calcularTotal() {
		return calcularBase() + calcularImpuestoConsumo() + calcularPropina();
	}
	

}
