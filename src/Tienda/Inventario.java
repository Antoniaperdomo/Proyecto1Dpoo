package Tienda;

import java.util.ArrayList;

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
			if (e.getArticulo().getNombre().equals(articulo.getNombre())
					&& e.getUbicacion().getNombre().equals(ubicacion.getNombre())) {
				return e;
			}
		}
		return null;
	}

	public int consultarDisponible(Articulo articulo, Ubicacion ubicacion) {
		ExistenciaArticulo e = buscar(articulo, ubicacion);
		return (e == null) ? 0 : e.getCantidadDisponible();
	}

}