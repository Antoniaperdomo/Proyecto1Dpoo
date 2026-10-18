package Tienda;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
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


	public void confirmar(ArrayList<ExistenciaArticulo> inventario, Ubicacion ubicacion) {
		descontarInventario(inventario, ubicacion);
		confirmarVenta();
	}

	private void descontarInventario(ArrayList<ExistenciaArticulo> inventario, Ubicacion ubicacion) {
		Map<String, Integer> requerido = new LinkedHashMap<String, Integer>();
		for (ItemVenta item : getItems()) {
			requerido.merge(item.getProducto().getNombre(), item.getCantidad(), Integer::sum);
		}

		for (Map.Entry<String, Integer> entrada : requerido.entrySet()) {
			ExistenciaArticulo e = buscarExistencia(entrada.getKey(), ubicacion, inventario);
			if (e == null || e.getCantidadDisponible() < entrada.getValue()) {
				throw new IllegalStateException(
						"No hay suficientes unidades en el inventario de " + entrada.getKey());
			}
		}

		for (Map.Entry<String, Integer> entrada : requerido.entrySet()) {
			buscarExistencia(entrada.getKey(), ubicacion, inventario).reducir(entrada.getValue());
		}
	}


	private ExistenciaArticulo buscarExistencia(String nombreArticulo, Ubicacion ubicacion,
			ArrayList<ExistenciaArticulo> inventario) {
		for (ExistenciaArticulo e : inventario) {
			if (e.getArticulo().getNombre().equals(nombreArticulo)
					&& e.getUbicacion().getNombre().equals(ubicacion.getNombre())) {
				return e;
			}
		}
		return null;
	}

}