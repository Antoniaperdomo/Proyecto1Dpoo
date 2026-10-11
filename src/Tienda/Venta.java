package Tienda;

import java.util.ArrayList;
import java.util.Date;

import Usuario.Usuario;

public abstract class Venta {

	public static final double PORCENTAJE_PUNTOS = 0.02;

	private Date fecha;
	private Usuario comprador;
	private ArrayList<ItemVenta> items;

	private int porcentajeDescuento;

	public Venta(Date fecha, Usuario comprador, ArrayList<ItemVenta> items, int porcentajeDescuento) {
		this.fecha = fecha;
		this.comprador = comprador;
		this.items = items;
		this.porcentajeDescuento = porcentajeDescuento;
	}

	public int getPorcentajeDescuento() {
		return porcentajeDescuento;
	}

	public void setPorcentajeDescuento(int porcentajeDescuento) {
		this.porcentajeDescuento = porcentajeDescuento;
	}

	public Date getFecha() {
		return fecha;
	}

	public Usuario getComprador() {
		return comprador;
	}

	public ArrayList<ItemVenta> getItems() {
		return items;
	}

	public void agregarItem(ItemVenta item) {
		items.add(item);
	}

	public double calcularSubtotal() {
		double total = 0;
		for (ItemVenta item : items) {
			total += item.getSubtotal();
		}
		return total;
	}

	public double calcularDescuento() {
		return calcularSubtotal() * (porcentajeDescuento / 100);
	}

	protected double calcularBase() {
		return calcularSubtotal() - calcularDescuento();
	}

	public abstract double calcularTotal();

	public int calcularPuntos() {
		return (int) (calcularTotal() * PORCENTAJE_PUNTOS);
	}

}
