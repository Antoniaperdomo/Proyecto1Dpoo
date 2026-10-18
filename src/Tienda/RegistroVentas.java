package Tienda;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

public class RegistroVentas {
	private ArrayList<Venta> ventas = new ArrayList<Venta>();
	 
	public void registrar(Venta venta) {
		if (venta == null) {
			throw new IllegalArgumentException("La venta es obligatoria");
		}
		ventas.add(venta);
	}
 
	public ArrayList<Venta> getVentas() {
		return new ArrayList<Venta>(ventas);
	}
 
	public ArrayList<Venta> consultarPorFecha(Date desde, Date hasta) {
		ArrayList<Venta> resultado = new ArrayList<Venta>();
		for (Venta v : ventas) {
			if (!v.getFecha().before(desde) && !v.getFecha().after(hasta)) {
				resultado.add(v);
			}
		}
		return resultado;
	}
	public Map<String, Double> totalesPorRubro(Date desde, Date hasta) {
		Map<String, Double> totales = new LinkedHashMap<String, Double>();
		for (Venta v : consultarPorFecha(desde, hasta)) {
			for (Map.Entry<String, Double> rubro : v.getRubros().entrySet()) {
				totales.merge(rubro.getKey(), rubro.getValue(), Double::sum);
			}
		}
		return totales;
	}

}
