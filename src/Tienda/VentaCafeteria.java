package Tienda;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import Usuario.Usuario;

public class VentaCafeteria extends Venta {
	public static final int IMPUESTO_CONSUMO = 8;
	public static final double PROPINA_SUGERIDA = 10;

	private double porcentajePropina;

	public VentaCafeteria(Date fecha, Usuario comprador, ArrayList<ItemVenta> items, int porcentajeDescuento) {
		this(fecha, comprador, items, porcentajeDescuento, PROPINA_SUGERIDA);
	}
 
	public VentaCafeteria(Date fecha, Usuario comprador, ArrayList<ItemVenta> items, int porcentajeDescuento,
			double porcentajePropina) {
		super(fecha, comprador, items, porcentajeDescuento);
		setPorcentajePropina(porcentajePropina);
	}
	@Override
	protected boolean esItemValido(Producto producto) {
		return producto instanceof ProductoCafeteria;
	}
	public double getPorcentajePropina() {
		return porcentajePropina;
	}

	public void setPorcentajePropina(double porcentajePropina) {
		if (porcentajePropina < 0) {
			throw new IllegalArgumentException("La propina no puede ser negativa");
		}
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
	public boolean contieneBebidaCaliente() {
		for (ItemVenta item : getItems()) {
			if (item.getProducto() instanceof Bebida && ((Bebida) item.getProducto()).esCaliente()) {
				return true;
			}
		}
		return false;
	}
	public String getAdvertenciaAlergenos() {
		StringBuilder sb = new StringBuilder();
		for (ItemVenta item : getItems()) {
			if (item.getProducto() instanceof Snack) {
				Snack snack = (Snack) item.getProducto();
				if (snack.tieneAlergenos()) {
					sb.append(snack.getAdvertenciaAlergenos()).append("\n");
				}
			}
		}
		return sb.toString().trim();
	}
	@Override
	protected void agregarRubrosAdicionales(Map<String, Double> rubros) {
		rubros.put("Impuesto al consumo (" + IMPUESTO_CONSUMO + "%)", calcularImpuestoConsumo());
		rubros.put("Propina (" + porcentajePropina + "%)", calcularPropina());
	}

}
