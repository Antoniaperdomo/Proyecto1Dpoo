package Tienda;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class Inventario {
	private ArrayList<ExistenciaArticulo> existencias = new ArrayList<ExistenciaArticulo>();
	
	public ArrayList<ExistenciaArticulo> getExistencias() {
	    return existencias;
	}
	
	public void registrarExistencia(Articulo articulo, Ubicacion ubicacion, int cantidad) {
		if (cantidad < 0) {
			throw new IllegalArgumentException("La cantidad no puede ser negativa");
		}
		ExistenciaArticulo existencia = buscar(articulo, ubicacion);
		if (existencia == null) {
			existencias.add(new ExistenciaArticulo(articulo, ubicacion, cantidad));
		} else if (cantidad > 0) {
			existencia.aumentar(cantidad);
		}
	}
 
	public ExistenciaArticulo buscar(Articulo articulo, Ubicacion ubicacion) {
		for (ExistenciaArticulo e : existencias) {
			if (e.getArticulo() == articulo && e.getUbicacion() == ubicacion) {
				return e;
			}
		}
		return null;
	}
 
	public int consultarDisponible(Articulo articulo, Ubicacion ubicacion) {
		ExistenciaArticulo e = buscar(articulo, ubicacion);
		return (e == null) ? 0 : e.getCantidadDisponible();
	}
	public void descontarVenta(VentaTienda venta, Ubicacion ubicacion) {
		Map<Articulo, Integer> requerido = new LinkedHashMap<Articulo, Integer>();
		for (ItemVenta item : venta.getItems()) {
			Articulo articulo = (Articulo) item.getProducto();
			requerido.merge(articulo, item.getCantidad(), Integer::sum);
		}
 
		for (Map.Entry<Articulo, Integer> entrada : requerido.entrySet()) {
			if (consultarDisponible(entrada.getKey(), ubicacion) < entrada.getValue()) {
				throw new IllegalStateException(
						"No hay suficientes unidades en el inventario de " + entrada.getKey().getNombre());
			}
		}
 
		for (Map.Entry<Articulo, Integer> entrada : requerido.entrySet()) {
			buscar(entrada.getKey(), ubicacion).reducir(entrada.getValue());
		}
	}

}
