package Tienda;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import Usuario.Usuario;

public abstract class Venta {

	public static final double PORCENTAJE_PUNTOS = 0.02;

	private Date fecha;
	private Usuario comprador;
	private ArrayList<ItemVenta> items;

	private int porcentajeDescuento;
	private int puntosRedimidos;

	public Venta(Date fecha, Usuario comprador, ArrayList<ItemVenta> items, int porcentajeDescuento) {
		if (fecha == null || comprador == null) {
			throw new IllegalArgumentException("La fecha y el comprador son obligatorios");
		}
		this.fecha = fecha;
		this.comprador = comprador;
		this.items = new ArrayList<ItemVenta>();
		setPorcentajeDescuento(porcentajeDescuento);
		if (items != null) {
			for (ItemVenta item : items) {
				agregarItem(item);
			}
		}
	}

	public int getPorcentajeDescuento() {
		return porcentajeDescuento;
	}

	public void setPorcentajeDescuento(int porcentajeDescuento) {
		if (porcentajeDescuento < 0 || porcentajeDescuento > 100) {
			throw new IllegalArgumentException("El descuento debe estar entre 0 y 100");
		}
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
		if (item == null) {
			throw new IllegalArgumentException("El item es obligatorio");
		}
		if (!esItemValido(item.getProducto())) {
			throw new IllegalArgumentException(
					"El producto " + item.getProducto().getNombre() + " no se puede vender en este tipo de venta");
		}
		items.add(item);

	}
	protected abstract boolean esItemValido(Producto producto);
	
	public void redimirPuntos(int puntos) {
		if (puntos < 0) {
			throw new IllegalArgumentException("Los puntos no pueden ser negativos");
		}
		this.puntosRedimidos = puntos;
	}

	public double calcularSubtotal() {
		double total = 0;
		for (ItemVenta item : items) {
			total += item.getSubtotal();
		}
		return total;
	}

	public double calcularDescuento() {
		return calcularSubtotal() * (porcentajeDescuento / 100.0);
	}
	public double calcularDescuentoPuntos() {
		double restante = calcularSubtotal() - calcularDescuento();
		return Math.min(puntosRedimidos, Math.floor(restante));
	}
	public int getPuntosAplicados() {
		return (int) calcularDescuentoPuntos();
	}

	protected double calcularBase() {
		return calcularSubtotal() - calcularDescuento() - calcularDescuentoPuntos();
	}

	public abstract double calcularTotal();

	public int calcularPuntos() {
		return (int) (calcularTotal() * PORCENTAJE_PUNTOS);
	}
	protected abstract void agregarRubrosAdicionales(Map<String, Double> rubros);
	public Map<String, Double> getRubros() {
		Map<String, Double> rubros = new LinkedHashMap<String, Double>();
		rubros.put("Subtotal", calcularSubtotal());
		rubros.put("Descuento", calcularDescuento());
		rubros.put("Descuento por puntos", calcularDescuentoPuntos());
		rubros.put("Base gravable", calcularBase());
		agregarRubrosAdicionales(rubros);
		rubros.put("Total", calcularTotal());
		return rubros;
	}

}
