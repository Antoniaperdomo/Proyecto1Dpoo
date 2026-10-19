package Tienda;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import Usuario.Comprador;
import Usuario.Socio;
import Usuario.Usuario;

public abstract class Venta {

	public static final double PORCENTAJE_PUNTOS = 0.02; // RF-34

	private Date fecha;
	private Usuario comprador;
	private ArrayList<ItemVenta> items;

	private int porcentajeDescuento;
	private int puntosRedimidos;
	private boolean confirmada;

	public Venta(Date fecha, Usuario comprador, ArrayList<ItemVenta> items, int porcentajeDescuento) {
		if (fecha == null || comprador == null) {
			throw new IllegalArgumentException("La fecha y el comprador son obligatorios");
		}
		if (!(comprador instanceof Comprador)) {
			throw new IllegalArgumentException("El comprador debe ser un socio o un empleado");
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
		verificarNoConfirmada();
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

	public boolean isConfirmada() {
		return confirmada;
	}

	public void agregarItem(ItemVenta item) {
		verificarNoConfirmada();
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
		verificarNoConfirmada();
		if (puntos < 0) {
			throw new IllegalArgumentException("Los puntos no pueden ser negativos");
		}
		if (puntos > ((Comprador) comprador).getPuntosFidelidad()) {
			throw new IllegalArgumentException("El comprador no tiene suficientes puntos");
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
		return calcularSubtotal() * porcentajeDescuento / 100.0;
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

	protected void confirmarVenta() {
		verificarNoConfirmada();
		if (items.isEmpty()) {
			throw new IllegalStateException("La venta no tiene items");
		}
		if (comprador instanceof Socio && !((Socio) comprador).isActivo()) {
			throw new IllegalStateException("El socio esta dado de baja");
		}
		Comprador c = (Comprador) comprador;
		int ganados = calcularPuntos();
		c.setPuntosFidelidad(c.getPuntosFidelidad() - getPuntosAplicados() + ganados);
		confirmada = true;
	}

	public void restaurarConfirmada(int puntosRedimidos) {
		if (puntosRedimidos < 0) {
			throw new IllegalArgumentException("Los puntos no pueden ser negativos");
		}
		this.puntosRedimidos = puntosRedimidos;
		this.confirmada = true;
	}

	private void verificarNoConfirmada() {
		if (confirmada) {
			throw new IllegalStateException("La venta ya fue confirmada");
		}
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